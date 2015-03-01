(ns gneiss.chunks
  (:require [clojure.java.io :as io]))

(defn handle
  [file]
  (let [f (io/file file)]
    (when (and (.exists f) (.isFile f))
      f)))

(defn chunks
  "Splits a buffer into n chunks."
  [h & [chks]]
  (let [num (or chks (.availableProcessors (Runtime/getRuntime)))]
    (with-open [rdr (clojure.java.io/reader h)]
      (let [lines (line-seq rdr)] 
        (partition-all (/ (count lines) num) lines)))))
