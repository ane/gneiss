(ns gneiss.churn
  (:require [clojure.core.reducers :as r]
            [clojure.java.io :as io]
            [gneiss.formats.matcher :as m]
            [gneiss.kick :as kick]
            [gneiss.regular :as regular])
  (:import [gneiss.formats.irssi Irssi]))

(defn make-matcher
  "Creates a matcher using the specific format."
  [format]
  (case format
    :irssi (Irssi.)
    (Irssi.)))

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
  ([fmt stats stats-map line]
   (if-let [match (some identity (map (fn [stat] ((resolve stat) fmt line)) stats))]
     (update-results match stats-map)
     stats-map)))

(defn same-type?
  [t & items]
  (every? #(identical? t (type %1)) items))

(defn merge-line-results
  "Merges two items together."
  [ev1 ev2]
  (let [t1 (type ev1)
        t2 (type ev2)]
    (cond
      (same-type? java.lang.Long ev1 ev2)
      (+ ev1 ev2)
      (same-type? clojure.lang.PersistentVector ev1 ev2)
      (into ev1 ev2))))

(defn merge-stats
  "Merges two statistics dictionaries."
  ([] {})  
  ([& m]
   (if (every? map? m)
     (apply merge-with merge-stats m)
     (apply merge-line-results m))))

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

(defn log
  "Processes the log buffer in lines, throws an exception if it isn't found."
  [lines]
  (let [result (analyze-lines (Irssi.) `(m/regular m/kick) lines)
        words (:words result)]
    (merge result {:words (->> words
                               (filter (comp neither-nick-nor-short first))
                               (sort-by val >)
                               (take 10))})))
