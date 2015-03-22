(ns gneiss.formats.matcher)

(defprotocol Matcher
  (kick [this line])
  (regular [this line]))
