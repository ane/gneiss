(ns gneiss.analysis.user
  (:require [clojure.core.match :refer [match]]
            [gneiss.formats.irssi :as irssi]))

(defn make-regular
  "Makes a regular message matcher using the given matcher func.
   f must be a function that given a log line gives at least three arguments:
   - the whole log line
   - the authoring nickname
   - the message itself following the nickname"
  [f]
  (fn [line]
    (if-let [[whole nick msg] (f line)]
      {:nick nick :words (clojure.string/split msg #"\s")}
      nil)))

(defn calculate-regular
  "Calculates the new statistics from the words array."
  [stats]
  {:words (count (:words stats))})

(defn update-user
  [user stats]
  {:lines (inc (or (:lines user) 1))
   :words (+ (:words stats) (get user :words 0))})

(defn update-users
  [statistic statsmap]
  (let [{usermap :users} statsmap
        {nick :nick} statistic
        change (calculate-regular statistic)]
    (assoc statsmap :users
           (if-let [user (get usermap nick)]
             (update-in usermap [nick] update-user change)
             (assoc usermap nick change)))))



