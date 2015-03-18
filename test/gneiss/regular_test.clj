(ns gneiss.regular-test
  (:require [gneiss.regular :refer :all]
            [clojure.test :refer :all]
            [gneiss.formats.irssi :as irssi]))

(def regular-fn (make-regular (:regular irssi/matcher)))

(def test-msg "13:37 < ane> hello i am a dog. you are not a dog, but a cat.")

(deftest regular-is-function
  (is (fn? regular-fn)
      "make-regular makes a function."))

(deftest regular-works-as-expected
  (is (=
       (regular-fn test-msg)
       {:kind :regular :nick "ane" :words ["hello" "i" "am" "a" "dog." "you" "are"
                                           "not" "a" "dog," "but" "a" "cat."]})))

(deftest user-update-works
  (let [stats-map {}
        statistic (regular-fn test-msg)
        changed-map (update-users statistic stats-map)]
    (is (= (get-in changed-map [:users "ane"])
           {:lines 1 :words 13}))))

(deftest word-update-works
  (let [stats-map {}
        statistic (regular-fn test-msg)
        changed-map (update-words statistic stats-map)]
    (is (= (get-in changed-map [:words "a"]) 3)))) 