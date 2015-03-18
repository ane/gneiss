(ns gneiss.formats.irssi-test
  (:require [gneiss.formats.irssi :refer :all]
            [clojure.test :refer :all]))

(def sample "13:37 <@ane> hello everybody I'm using irssi")

(def kick "13:37 -!- foo was kicked from #bar by Derp [lol]")

(def bad-sample "ding dong the witch is dead 23:23 < tallow> for albion!")

(deftest should-match

  (is (some? ((:regular matcher) sample))))

(deftest should-not-match
  (is (nil? ((:regular matcher) bad-sample))))

(deftest should-kick
  (is (= ((:kick matcher) kick)
         [kick "foo" "Derp" "lol"])))

(testing "Irssi format:"
  (testing "The regexp should match a valid Irssi line."      
    (should-match))
  (testing "The regexp must match on full lines, no cruft at the start is allowed."
    (should-not-match)))


