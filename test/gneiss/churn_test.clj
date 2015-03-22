(ns gneiss.churn-test
  (:require [clojure.test :refer :all]
            [gneiss.churn :refer :all]
            [gneiss.formats.matcher :as m]
            [gneiss.regular :as user])
  (:import [gneiss.formats.irssi Irssi]))


(deftest default-matcher
  (is (instance? gneiss.formats.irssi.Irssi (make-matcher :foobarasdf))
      "The default matcher is :irssi."))

(deftest merging
  (let [m1 {:users {"ane" {:words 5 :kicks ["ding"]} "esko" {:kicks ["ane"]}}
            :words {"foo" 1 "bar" 2}}
        m2 {:users {"esko" {:words 3} "ane" {:words 3 :kicks ["dong"]}}
            :words {"foo" 1 "baz" 9}}
        result (merge-stats m1 m2)]
    (is (= result
           {:users
            {"ane" {:words 8 :kicks ["ding" "dong"]}
             "esko" {:words 3 :kicks ["ane"]}}
            :words {"foo" 2 "bar" 2 "baz" 9}})
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
      (when-let [stats (not-empty (analyze-line matcher `(m/regular) {} line))]
        ;; at least some lines ought to be produced, so
        ;; use any entries you want
        (is (some-keys? [:users :words] stats)
            "At least some stats must be produced when there is a match.")))))


(deftest analyzing
  (let [res (analyze-lines (Irssi.) `(m/regular) lines)]
    (is (= res {:users {"bip" {:words 17 :lines 3}, "ding" {:words 12 :lines 2}}
                ;; this was not fun to write
                :words {"hurr" 1, "durr" 1, "herp" 1, "derp" 1, "burp" 1, ";D" 1,
                        "this" 1, "is" 2, "six" 1, "word" 1, "sentence" 1,
                        "yeah" 1, "i" 1, "had" 1, "great" 1, "fun" 1, "trimming" 1,
                        "the" 2, "last" 1, "night" 1, ":)" 1, "what" 1,
                        "fuck" 1, "going" 1, "on" 1, "a" 1, "hedges" 1}}))))

(deftest regular-updating
  (let [match {:kind :regular :nick "ane" :words ["yes" "this" "dog" "how" "may" "i" "help" "dog"]}
        reg (update-results match {})]
    (is (= reg {:users {"ane" {:words 8 :lines 1}}
                :words {"yes" 1, "this" 1, "dog" 2, "how" 1, "may" 1, "i" 1, "help" 1}}))))
