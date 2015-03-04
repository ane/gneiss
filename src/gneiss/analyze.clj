(ns gneiss.analyze
  (:require [gneiss.formats.irssi :as irssi]
            [clojure.core.match :refer [match]]))

(defn merge-stats [ev1 ev2]
  (match [ev1 ev2]
         [(a :guard vector?) (b :guard vector?)] (vec (concat a b))
         [(a :guard integer?) (b :guard integer?)] (+ a b)
         :else (println (type ev1) (type ev2))))

(defn make-regular
  "Makes a regular message matcher using the given matcher func."
  [reg]
  (fn [line]
    (if-let [[_ nick msg] (reg line)]
      {:nick nick :words (frequencies (clojure.string/split msg #"\s"))}
      :nothing)))

(defn make-matcher
  "Creates a matcher using the specific format."
  [format]
  (let [matcher (match [format]
                       [:irssi] irssi/matcher
                       :else irssi/matcher)]
    (let [{reg :regular} matcher]
      {:regular (make-regular reg)})))

(def select-values (comp vals select-keys))

(defn match-line
  "Matches a log line with the matchers."
  [line matcher]
  (let [matcher-funcs (select-values matcher [:regular])]
    (match [((apply some-fn matcher-funcs) line)]
           [{:nick n :words wc}] {n {:words wc}}
           :else {})))
