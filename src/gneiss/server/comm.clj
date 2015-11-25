(ns gneiss.server.comm
  (:require [clojure.core.async :refer [<! go-loop]]
            [gneiss.protocols
             :refer
             [analyze-buffer Analyzer get-stats merge-statistics set-stats Storage]]))

(defn make-in-memory
  "Creates an in-memory storage that uses the churner and stores
   the data in just a simple ref in store."
  [churner store]
  (assert (satisfies? Analyzer churner) "Must be an Analyzer")
  (reify Storage
    (get-stats [self] (deref store))
    (set-stats [self state update-fn]
      (dosync
       (let [current-state @store]
         (println "merging" current-state "with" state)
         (ref-set store (update-fn current-state state)))))))

(defn listen-for-lines
  "Listens for incoming stats from c and pushes them into storage."
  [storage churner ch]
  (go-loop []
    (when-let [lines (<! ch)]
      (let [processed (analyze-buffer churner lines)]
         (set-stats storage processed
                    (fn [old new]
                      (merge-statistics churner old new)))))))
