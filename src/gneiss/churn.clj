(ns gneiss.churn
  (:require [clojure.core.reducers :as r]
            [clojure.java.io :as io]
            [gneiss.formats.irssi :as irssi]
            [gneiss.regular :as regular]))

(defn make-matcher
  "Creates a matcher using the specific format."
  [format]
  (let [matcher (case format
                  :irssi irssi/matcher
                  irssi/matcher)
        {:keys [regular type]} matcher]
    {:regular (regular/make-regular regular)
     :type type}))

(defmulti update-results
  (fn [match stats-map] (:kind match)))

(defmethod update-results :regular
  [match stats-map]
  ((apply comp [(partial regular/update-words match)
                (partial regular/update-users match)]) stats-map))

(defn analyze-line
  "Given a set of matcher funcs with matching statistics keys, fetches
  a matcher func and tries to match it, and updates by calling the updater
  based on the type of the match."
  ([matcher-fns stats] {})
  ([matcher-fns stats stats-map line]
   (if-let [match
            (some identity
                  (map (fn [stat]
                         ((stat matcher-fns) line)) stats))]
     (update-results match stats-map)
     stats-map)))

(defn merge-line-results
  "Merges two items together."
  [ev1 ev2]
  (let [types [(type ev1) (type ev2)]]
        (condp = types
          [java.lang.Long java.lang.Long] (+ ev1 ev2)
          [clojure.lang.PersistentVector clojure.lang.PersistentVector] (into ev1 ev2)
          ;; ev1 wins if types aren't compatible
          ev1)))

(defn merge-stats
  "Merges two statistics dictionaries."
  ([] {})  
  ([& m]
   (if (every? map? m)
     (apply merge-with merge-stats m)
     (apply merge-line-results m))))

(defn analyze-lines
  "Analyze analyzes a file using a specific format."
  [format statistics lines]
  (r/fold merge-stats
          #(analyze-line
            (make-matcher format) statistics %1 %2)
          lines))

(defn neither-nick-nor-short
  "Returns true whether the word isn't a hollering, i.e., someone is
  trying to address someone like \"< derp> john: you're so wrong!\",
  and whether the word is longer or equal to five characters. FIXME:
  won't add swearwords, but basic common english words could be an
  option."
  [word]
  (and (>= (.length word) 5)
       (not (.endsWith word ":"))))

(defn log
  "Processes the log buffer in lines, throws an exception if it isn't found."
  [lines]
  (let [{words :words, users :users} (analyze-lines :irssi [:regular] lines)]
    {:words (->> words
                 (filter (comp neither-nick-nor-short first))
                 (sort-by val >)
                 (take 10))
     :users users}))
