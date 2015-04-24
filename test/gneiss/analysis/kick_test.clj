(ns gneiss.analysis.kick-test
  (:require [clojure.test :refer :all]
            [gneiss.formats.irssi :as irssi]
            [gneiss.formats.matcher :as m]
            [gneiss.analysis.kick :refer :all])
  (:import [gneiss.formats.irssi Irssi]))

(def test-msg "13:37 -!- ane was kicked from #foo by derp [haha!]")

(deftest update-kicks-works
  (let [delta (m/kick (Irssi.) test-msg)
        changed (update-kicks delta {})]
    (is (seq (get-in changed [:kicks "derp"])))))
