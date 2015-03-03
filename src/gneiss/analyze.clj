(ns gneiss.analyze
  (:require [clojure.core.match :refer [match]]))

(defn merge-stats [ev1 ev2]
  (match [ev1 ev2]
         [(a :guard vector?) (b :guard vector?)] (vec (concat a b))
         [(a :guard integer?) (b :guard integer?)] (+ a b)
         :else (println (type ev1) (type ev2))))
