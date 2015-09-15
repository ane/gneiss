(ns gneiss.core
  (:require [clojure.java.io :as io]
            [gneiss.analysis.churn :refer [->Churner]]
            [gneiss.formats.irssi :refer [->Irssi]]
            [gneiss.protocols :refer [analyze-buffer]]))

(defn process
  [analyzer file]
  (with-open [rdr (io/reader file)]
    (analyze-buffer analyzer (line-seq rdr))))

(defn -main
  "Runs Gneiss. It's gneiss."
  [& args]
  (println "Gneiss started!")
  (doseq [f args]
    (println (process (->Churner (->Irssi)) f))))


