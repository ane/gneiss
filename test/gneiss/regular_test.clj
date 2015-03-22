(ns gneiss.regular-test
  (:require [clojure.test :refer :all]
            [gneiss.formats.irssi :as irssi]
            [gneiss.formats.matcher :as m]
            [gneiss.regular :refer :all])
  (:import [gneiss.formats.irssi Irssi]))

(def test-msg "13:37 < ane> hello i am a dog. you are not a dog, but a cat.")

(deftest regular-works-as-expected
  (is (=
       (m/regular (Irssi.) test-msg)
       {:kind :regular :nick "ane" :words ["hello" "i" "am" "a" "dog." "you" "are"
                                           "not" "a" "dog," "but" "a" "cat."]})))

(deftest user-update-works
  (let [stats-map {}
        statistic (m/regular (Irssi.) test-msg)
        changed-map (update-users statistic stats-map)]
    (is (= (get-in changed-map [:users "ane"])
           {:lines 1 :words 13}))))

(deftest word-update-works
  (let [stats-map {}
        statistic (m/regular (Irssi.) test-msg)
        changed-map (update-words statistic stats-map)]
    (is (= (get-in changed-map [:words "a"]) 3)))) 
