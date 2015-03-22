(ns gneiss.kick)

(defn update-kicks
  [statistic statsmap]
  (update-in statsmap [:kicks (:kicker statistic)]
             (fnil conj []) (:kicked statistic)))
