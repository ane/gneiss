(ns gneiss.regular
  (:require [gneiss.formats.irssi :as irssi]))

(defn make-regular
  "Makes a regular message matcher using the given matcher func.
  f must be a function that given a log line gives at least three arguments:
  - the whole log line
  - the authoring nickname
  - the message itself following the nickname"
  [f]
  (fn [line]
    (when-let [[whole nick msg] (f line)]
      {:kind :regular :nick nick :words (clojure.string/split msg #"\s")})))

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
  (let [{usermap :users} statsmap
        {nick :nick} statistic
        change (calculate-regular statistic)]
    (assoc-in statsmap [:users nick]
              (update-user
               (get usermap nick {}) change))))

