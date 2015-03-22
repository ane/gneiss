(ns gneiss.formats.irssi-test
  (:require [clojure.test :refer :all]
            [gneiss.formats.irssi :refer :all]
            [gneiss.formats.matcher :as m])
  (:import [gneiss.formats.irssi Irssi]))

(def irssi (Irssi.))

(def sample "13:37 <@ane> hello everybody I'm using irssi")

(def kick "13:37 -!- foo was kicked from #bar by Derp [lol]")

(def bad-sample "ding dong the witch is dead 23:23 < tallow> for albion!")

(deftest should-match
  (is (some? (m/regular irssi sample))))

(deftest should-not-match
  (is (nil? (m/regular irssi bad-sample))))

(deftest should-kick
  (is (= (m/kick irssi kick)
         {:kicker "Derp" :kicked "foo" :reason "lol" :kind :kick})))

(deftest satisfies-matcher
  (is (satisfies? m/Matcher (Irssi.))))


