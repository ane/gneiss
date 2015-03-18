(ns gneiss.formats.irssi)

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

(def matcher {:type :irssi
              :regular (partial re-find regular?)
              :kick (partial re-find kick?)})
