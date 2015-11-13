(ns gneiss.server.comm
  (:require [clojure.core.async :refer [<! chan >!! go-loop]]
            [gneiss.protocols
             :refer
             [get-stats merge-statistics set-stats Storage]]))

(defn make-in-memory [churner store]
  (reify Storage
    (get-stats [self] (deref store))
    (set-stats [self state]
      (dosync
       (let [current-state (deref store)]
         (println "merging" current-state "with" state)
         (ref-set store
                  (merge-statistics churner current-state state)))))))

(defn listen-for-stats
  "Listens for incoming stats from c and pushes them into storage."
  [storage ch]
  (go-loop []
    (when-let [v (<! ch)]
      (when (set-stats storage v)
        (recur)))))
