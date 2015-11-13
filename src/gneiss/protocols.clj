(ns gneiss.protocols)

(defprotocol Analyzer
  "An analyzer produces statistics from a set of lines."
  (merge-statistics [self s1 s2])
  (analyze-buffer [self lines]))

(defprotocol Storage
  "A storage keeps the statistical state in storage,
  you can retrieve state and also store a new one."
  (get-stats [self])
  (set-stats [self state]))
