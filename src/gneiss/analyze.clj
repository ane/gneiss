(ns gneiss.analyze
  (:require [clojure.core.match :refer [match]]
            [clojure.core.reducers :as r]
            [clojure.java.io :as io]
            [gneiss.formats.irssi :as irssi]))

(defn make-regular
  "Makes a regular message matcher using the given matcher func."
  [reg]
  (fn [line]
    (if-let [[whole nick msg] (reg line)]
      (do
        {:nick nick :words (count (clojure.string/split msg #"\s"))})
      nil)))

(defn make-matcher
  "Creates a matcher using the specific format."
  [format]
  (let [matcher (match [format]
                       [:irssi] irssi/matcher
                       :else irssi/matcher)]
    (let [{:keys [regular type]} matcher]
      {:regular (make-regular regular)
       :type type})))

(defn merge-line-results [ev1 ev2]
  (match [ev1 ev2]
         [(a :guard vector?) (b :guard vector?)] (vec (concat a b))
         [(a :guard integer?) (b :guard integer?)] (+ a b)
         :else (println (type ev1) (type ev2))))

(defn merge-stats
  "Merges two statistics dictionaries."
  ([] {})  
  ([& m] (apply merge-with (partial merge-with merge-line-results) m)))

(defn update-stats
  [map nick stat f new-val]
  (if-let [user (get map nick)]
    (assoc map nick
           (update-in user [stat] f new-val))
    (assoc map nick {stat new-val})))

(defn analyze-line
  "Matches a log line with the matchers."
  ([matcher] {})
  ([matcher stats-map line]
   (def select-values (comp vals select-keys))
   (let [matcher-funcs (select-values matcher [:regular])]
     (match [(let [value (apply some-fn matcher-funcs)]
               (value line))]
            [{:nick n :words wc}] (update-stats stats-map n :words + wc)
            :else stats-map))))

(defn analyze-lines
  "Analyze analyzes a file using a specific format."
  [format lines]
  (def matcher (make-matcher format))
  (r/fold merge-stats (partial analyze-line matcher) lines))


(defn lazy-file-lines [file]
  (letfn [(helper [rdr]
                  (lazy-seq
                    (if-let [line (.readLine rdr)]
                      (cons line (helper rdr))
                      (do (.close rdr) nil))))]
    (helper (clojure.java.io/reader file))))

(defn churn-file
  [f]
  (with-open [rdr (io/reader f)]
    (analyze-lines :irssi (line-seq rdr))))
