(ns gneiss.analysis.churn
  (:require [clojure.core.match :refer [match]]
            [clojure.core.reducers :as r]
            [clojure.java.io :as io]
            [gneiss.analysis.user :as user]
            [gneiss.formats.irssi :as irssi]))

(defn make-matcher
  "Creates a matcher using the specific format."
  [format]
  (let [matcher (match [format]
                       [:irssi] irssi/matcher
                       :else irssi/matcher)]
    (let [{:keys [regular type]} matcher]
      {:regular (user/make-regular regular)
       :type type})))


(defn analyze-line
  "Matches a log line with the matchers, using the statistics given in stats."
  ([matcher stats] {})
  ([matcher stats stats-map line]
   (def select-values (comp vals select-keys))
   (let [matcher-funcs (select-values matcher stats)]
     (let [tester (apply some-fn matcher-funcs)]
       (match [(tester line)]
              [{:regular stat}] (update-in stats-map [:users]
                                           user/update-users
                                           stat)
              :else stats-map)))))

(defn merge-line-results [ev1 ev2]
  "Merges two items together."
  (match [ev1 ev2]
         [(a :guard vector?) (b :guard vector?)] (vec (concat a b))
         [(a :guard integer?) (b :guard integer?)] (+ a b)
         :else (println (type ev1) (type ev2))))

(defn merge-stats
  "Merges two statistics dictionaries."
  ([] {})  
  ([& m]
   (apply merge-with (partial merge-with (partial merge-with merge-line-results)) m)))

(defn analyze-lines
  "Analyze analyzes a file using a specific format."
  [format statistics lines]
  (def matcher (make-matcher format))
  (r/fold merge-stats (partial analyze-line matcher statistics) lines))


(defn log
  [f]
  (with-open [rdr (io/reader f)]
    (analyze-lines :irssi [:regular] (line-seq rdr))))
