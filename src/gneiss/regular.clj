(ns gneiss.regular
  (:require [clojure.core.reducers :as r]
            [gneiss.formats.irssi :as irssi]))


(defn calculate-regular
  "Calculates the new statistics from the words array."
  [stats]
  {:words (count (:words stats))})

(defn update-user
  [user stats]
  {:lines (inc (or (:lines user) 0))
   :words (+ (:words stats) (get user :words 0))})

(defn update-users
  [statistic statsmap]
  (let [{nick :nick} statistic
        change (calculate-regular statistic)]
    (update-in statsmap [:users nick] (fnil update-user {}) change)))

(defn update-words
  [statistic statsmap]
  (let [{words :words} statistic]
    (reduce
     (fn [m w]
       (update-in m [:words w] (fnil inc 0))) statsmap words)))
