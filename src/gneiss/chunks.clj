(ns gneiss.chunks)

(defn size
  [file]
  (let [f (clojure.java.io/file file)]
    (if (and (.exists f) (.isFile f))
      (.getTotalSpace f)
      (nil))))

(defn chunks
  "Splits a buffer into n chunks."
  [file & [n]]
  (let [sz (size file)
        num (or n 4)]
    (if sz
      (let [base (/ sz num)]
        (reduce
         (fn [specs pos]
           (conj specs
                 (let [prevOffs (or (peek specs) 0)]
                   ;; TODO: check if the offs is in the middle of a line
                   (+ prevOffs base))))
         []
         (range num)))
      nil)))
