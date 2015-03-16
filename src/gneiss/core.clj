(ns gneiss.core
  (:require [clojure.java.io :as io]
            [gneiss.churn :as churn])
  (:gen-class))

(defn process
  [file]
  (with-open [rdr (io/reader file)]
    (churn/log (line-seq rdr))))

(defn -main
  "Runs Gneiss. It's gneiss."
  [& args]
  (println "Gneiss started!")
  (doseq [f args]
    (println (process f))))


