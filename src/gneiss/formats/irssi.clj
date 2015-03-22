(ns gneiss.formats.irssi
  (:require [gneiss.formats.matcher :as matcher]))

(def nickname #"[A-Za-z\[\]\\`_\^\{\|\}]+")

(def modechars #"[@\s+%]")

(def time-stamp #"\d{2}:\d{2}")

(def event-prefix #"-!-")

(def channel #"[!&#\+]+[^\r\n\s,:]+")

(def regular? (re-pattern (str "^" time-stamp " <" modechars "(" nickname ")> (.*)$")))

(def kick? (re-pattern (str "^"
                            time-stamp " " event-prefix " "
                            "(" nickname ")" " was kicked from " channel " by "
                            "(" nickname ") " #"\[(.*)\]")))

(defrecord Irssi []
  matcher/Matcher
  (kick [x line] (when-let [[_ kicked kicker reason] (re-find kick? line)]
      {:kind :kick :kicked kicked :kicker kicker :reason reason}))
  (regular [x line] (when-let [[whole nick msg] (re-find regular? line)]
      {:kind :regular :nick nick :words (clojure.string/split msg #"\s")})))
