(ns gneiss.churn-test
  (:require [clojure.test :refer :all]
            [gneiss.churn :refer :all]
            [gneiss.regular :as user]))

(deftest matcher-creation
  (doseq [mtype [:irssi]]
    (let [m (make-matcher mtype)]
      (is (= (:type m) mtype)
          "Matcher type is correctly defined as the :type key."))))

(deftest default-matcher
  (is (= (:type (make-matcher :foobarasdf)) :irssi)
      "The default matcher is :irssi."))

(deftest merging
  (let [m1 {:users {"ane" {:words 5 :kicks ["ding"]} "esko" {:kicks ["ane"]}}}
        m2 {:users {"esko" {:words 3} "ane" {:words 3 :kicks ["dong"]}}}
        result (merge-stats m1 m2)]
    (is (= result
           {:users
            {"ane" {:words 8 :kicks ["ding" "dong"]}
             "esko" {:words 3 :kicks ["ane"]}}})
        "Merging should work as expected.")))

(def lines ["14:33 < bip> hurr durr herp derp burp ;D"
            "13:39 < ding> this is a six word sentence"
            "16:13 < bip> yeah"
            "-- this line is noise and should not match! --"
            "14:40 < bip> i had great fun trimming the hedges last night :)"
            "14:33 < ding> what the fuck is going on"])

(defn some-keys?
  "Checks if the map m contains any of the keys in keys."
  [keys m]
  (let [preds (map #(fn [map] (contains? map %)) keys)]
    ((apply some-fn preds) m)))

(deftest analyzing-line
  (let [matcher (make-matcher :irssi)]
    (doseq [line lines]
      (when-let [stats (not-empty (analyze-line matcher [:regular] {} line))]
        ;; at least some lines ought to be produced, so
        ;; use any entries you want
        (is (some-keys? [:users :words] stats)
            "At least some stats must be produced when there is a match.")))))

(deftest analyzing
  (let [res (analyze-lines :irssi [:regular] lines)]
    (is (= res {:users {"bip" {:words 17 :lines 3}, "ding" {:words 12 :lines 2}}}))))
