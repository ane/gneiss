(ns gneiss.kick)

(defn make-kicker
  "Makes a kick matcher with the given f."
  [f]
  (when (fn? f)
    (fn
      [line]
      (when-let [[_ kicked kicker reason] (f line)]
        {:kind :kick :kicked kicked :kicker kicker :reason reason}))))


(defn update-kicks
  [statistic statsmap]
  (update-in statsmap [:kicks (:kicker statistic)]
             (fnil conj []) (:kicked statistic)))
