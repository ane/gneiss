(ns gneiss.core
  (:require [gneiss.churn :as churn]))


(defn -main
  "Runs Gneiss. It's gneiss."
  [& args]
  (map churn/log args))


