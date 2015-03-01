(ns gneiss.core
  (require [clojure.java.io :as io]
           [gneiss.chunks :as chunks]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println (str "I'm in " (System/getProperty "user.dir")))
  (println "Getting some chunks!")
  (map chunks/kakkapaska args))


