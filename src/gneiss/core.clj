(ns gneiss.core
  (:require [gneiss.analyze :as anal]
            [gneiss.io :as gio]))

(defn churn
  "Analyzes a file."
  [files]
  (map #(anal/analyze-lines :irssi %) (keep gio/load-log files)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println (str "I'm in " (System/getProperty "user.dir")))
  (println "Getting some chunks!")
  (map chunks/kakkapaska args))


