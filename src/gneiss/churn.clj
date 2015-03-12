(ns gneiss.churn
  (:require [clojure.core.match :refer [match]]
            [clojure.core.reducers :as r]
            [clojure.java.io :as io]
            [gneiss.regular :as regular]
            [gneiss.formats.irssi :as irssi]))

(defn make-matcher
  "Creates a matcher using the specific format."
  [format]
  (let [matcher (match [format]
                       [:irssi] irssi/matcher
                       :else irssi/matcher)]
    (let [{:keys [regular type]} matcher]
      {:regular (regular/make-regular regular)
       :type type})))

(defn compose-updaters
  "Takes functions that update a map, passes them stats as the last parameter,
  and returns a collection of curried fns you can fold over."
  [stats fs]
  (map #(partial % stats) fs))

(defn try-matcher
  "Tries the matcher func against line, and composes the updater
  funcs with the result."
  [line matcher-fn updater-fns]
  (when-let [result (matcher-fn line)]
    (compose-updaters result updater-fns)))

(def updaters
  {:regular [regular/update-users]})

(defn find-and-apply-matcher
  "With a log line and a statistic, gets the corresponding
  matcher and updater from the updater and matcher maps."
  [line stat matchers updaters]
  (let [matcher (stat matchers)
        updater (when matcher (stat updaters))]
    (when updater (try-matcher line matcher updater))))

(defn analyze-line
  "Given a set of matcher and updater hash-maps with keys present of that of
  stats, and given an existing stats-map and a log line, fetches the matcher and
  updater for that particular statistic.

  A matcher is a function that tells us whether this line satisfies some
  condition, for example, if it's a regular message, and the function returns
  information about it.

  An updater is a sequence of functions that determine what to do with
  matching information. Updaters are given the result of the matcher and an
  existing statistics map to modify, and they will do it sequentially."
  ([matcher-fns updater-fns stats] {})
  ([matcher-fns updater-fns stats stats-map line]
   (if-let
       [up-fns
        (some identity
              (map #(find-and-apply-matcher line %1 matcher-fns updater-fns)
                   stats))]
     ((apply comp up-fns) stats-map)
     stats-map)))

(defn merge-line-results
  "Merges two items together."
  [ev1 ev2]
  (match [ev1 ev2]
         [(a :guard vector?) (b :guard vector?)] (vec (concat a b))
         [(a :guard integer?) (b :guard integer?)] (+ a b)
         :else (println (type ev1) (type ev2))))

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
            (make-matcher format) updaters statistics %1 %2)
          lines))

(defn log
  [f]
  (with-open [rdr (io/reader f)]
    (analyze-lines :irssi [:regular] (line-seq rdr))))
