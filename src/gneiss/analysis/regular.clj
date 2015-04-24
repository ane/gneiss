(ns gneiss.analysis.regular
  (:require [clojure.core.reducers :as r]
            [gneiss.formats.irssi :as irssi]))

(defn update-user
  [{:keys [lines words] :or {lines 0 words 0}} stats]
  {:lines (inc lines) :words (+ words (:words stats))})

(defn update-users
  [{:keys [nick words]} statsmap]
  (update-in statsmap [:users nick] (fnil update-user {}) {:words (count words)}))

(defn update-social
  [statistic statsmap]
  (let [target (first (:words statistic))
        source (:nick statistic)]
    (if (and (or (.endsWith target ":") (.endsWith target ","))
             (> (.length target) 1))
      (let [suffixless (subs target 0 (- (.length target) 1))]
        (update-in statsmap [:graph source suffixless] (fnil inc 0)))
      statsmap)))

(defn update-words
  [statistic statsmap]
  (let [{words :words} statistic]
    (reduce
     (fn [m w]
       (update-in m [:words w] (fnil inc 0))) statsmap words)))
