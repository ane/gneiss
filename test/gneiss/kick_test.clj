(ns gneiss.kick-test
  (:require [clojure.test :refer :all]
            [gneiss.formats.irssi :as irssi]
            [gneiss.kick :refer :all]))

(def kicker-fn (make-kicker (:kick irssi/matcher)))

(def test-msg "13:37 -!- ane was kicked from #foo by derp [haha!]")

(deftest kicker-is-fn
  (is (fn? kicker-fn)))

(deftest update-kicks-works
  (let [delta (kicker-fn test-msg)
        changed (update-kicks delta {})]
    (is (seq (get-in changed [:kicks "derp"])))))
