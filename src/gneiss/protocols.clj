(ns gneiss.protocols)

(defprotocol Analyzer
  "An analyzer produces statistics from a set of lines."
  (analyze-buffer [self lines]))
