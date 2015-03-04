(ns gneiss.formats.irssi)

(def nickname #"([A-Za-z\[\]\\`_\^\{\|\}]+)")

(def modechars #"[@\s+%]")

(def time-stamp #"\d{2}:\d{2}")

(def regular? (re-pattern (str time-stamp " <" modechars nickname "> (.*)")))

(def matcher {:regular (partial re-find regular?)})
