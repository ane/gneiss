(ns gneiss.analyze-test
  (:require [gneiss.analyze :refer :all]
            [clojure.test :refer :all]))

(deftest matcher-creation
  (doseq [mtype [:irssi]]
    (let [m (make-matcher mtype)]
      (is (= (:type m) mtype)
          "Matcher type is correctly defined as the :type key."))))

(deftest default-matcher
  (is (= (:type (make-matcher :foobarasdf)) :irssi)
      "The default matcher is :irssi."))

(deftest default-regular
  (let [m (make-matcher :irssi)]
    (let [reg (:regular m)]
      (is (= (reg "13:37 < ane> this is a six word message")
             {:nick "ane" :words 6})
          "The correct amount of words is parsed and the right nick as well."))))

(deftest merging
  (let [m1 {"ane" {:words 5 :kicks ["ding"]} "esko" {:kicks ["ane"]}}
        m2 {"esko" {:words 3} "ane" {:words 3 :kicks ["dong"]}}
        result (merge-stats m1 m2)]
    (is (= result
           {"ane" {:words 8 :kicks ["ding" "dong"]}
            "esko" {:words 3 :kicks ["ane"]}})
        "Merging should work as expected.")))

(def lines ["14:33 < bip> hurr durr herp derp burp ;D"
            "13:39 < ding> this is a six word sentence"
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
      (let [stats (first (vals (analyze-line matcher {} line)))]
        ;; at least some lines ought to be produced, so
        ;; use any entries you want
        (is (some-keys? [:kicks
                         :words] stats)
            "At least some stats must be produced.")))))

(deftest analyzing
  (let [res (analyze-lines :irssi lines)]
    (is (= res {"bip" {:words 16}, "ding" {:words 12}}))))
