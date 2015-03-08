(ns gneiss.analysis.user-test
  (:require [gneiss.analysis.user :refer :all]
            [clojure.test :refer :all]
            [gneiss.formats.irssi :as irssi]))

(deftest making-regular
  (let [{regular-matcher :regular} irssi/matcher]
    (let [regularfn (make-regular regular-matcher)]
      (is (fn? regularfn)
          "make-regular returned a function.")
      (let [sampl (regularfn "13:37 < ane> hello i am a dog")]
        (is (= sampl {:regular {:nick "ane" :words ["hello" "i" "am" "a" "dog"]}})
            "the returned function works.")))))
