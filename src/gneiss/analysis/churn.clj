(ns gneiss.analysis.churn
  (:require [clojure.core.reducers :as r]
            [gneiss.analysis
             [kick :as kick]
             [regular :as regular]]
            [gneiss.formats.matcher :as m]
            [gneiss.protocols :as p]))

(defmulti update-results
  (fn [match stats-map] (:kind match)))

(defn curry-match
  [m & fns]
  (apply comp (map (fn [f] (partial f m)) fns)))

(defmethod update-results :regular
  [match stats-map]
  ((curry-match match
                #'regular/update-users
                #'regular/update-words
                #'regular/update-social) stats-map))

(defmethod update-results :kick
  [match stats-map]
  (kick/update-kicks match stats-map))

(defn analyze-line
  "Given a set of matcher funcs with matching statistics keys, fetches
  a matcher func and tries to match it, and updates by calling the updater
  based on the type of the match."
  ([fmt stats] {})
  ([fmt stats ^clojure.lang.IPersistentMap stats-map ^String line]
   (if-let [match (some identity (map (fn [stat] ((resolve stat) fmt line)) stats))]
     (update-results match stats-map)
     stats-map)))

(defmulti merge-line-results
  "Merges two items together."
  (fn [x y]
    [(type x) (type y)]))

(defmethod merge-line-results [Long Long]
  [x y]
  (+ x y))

(defmethod merge-line-results [clojure.lang.PersistentVector clojure.lang.PersistentVector]
  [v1 v2]
  (into v1 v2))

(defn merge-stats
  "Merges two statistics dictionaries."
  ([] {})  
  ([& m]
   (try
     (if (every? map? m)
      (apply merge-with merge-stats m)
      (apply merge-line-results m))
     (catch IllegalArgumentException e
       (first m)))))

(defn analyze-lines
  "Analyze analyzes a file using a specific format."
  [matcher statistics lines]
  (r/fold merge-stats #(analyze-line matcher statistics %1 %2) lines))

(defn neither-nick-nor-short
  "Returns true whether the word isn't a hollering, i.e., someone is
  trying to address someone like \"< derp> john: you're so wrong!\",
  and whether the word is longer or equal to five characters. FIXME:
  won't add swearwords, but basic common english words could be an
  option."
  [^String word]
  (and (>= (.length word) 5)
       (not (.endsWith word ":"))))

(defn process-buffer
  "Processes the log buffer in lines using the matcher matcher, throws an exception if it isn't found."
  [lines matcher]
  (let [result (analyze-lines matcher `(m/regular m/kick) lines)]
    (merge result {:words (->> (:words result)
                               (filter (comp neither-nick-nor-short first))
                               (sort-by val >)
                               (take 10))})))

(deftype Churner [matcher]
  p/Analyzer
  (merge-statistics [self s1 s2] (merge-stats s1 s2))
  (analyze-buffer [self lines] (process-buffer lines matcher)))
