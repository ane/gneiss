(ns gneiss.core-test
  (:require [clojure.test :refer :all]
            [gneiss.analysis.churn :refer [->Churner]]
            [gneiss.core :refer [process]]
            [gneiss.formats.irssi :refer [->Irssi]]))

(defonce analyzer (->Churner (->Irssi)))

(deftest process-cannot-find
  (testing "process correctly throws an exception when it can't find a file."
    (is (thrown? java.io.FileNotFoundException
                 (process analyzer "imaginary-foobar<\\ding/dong<flargh.log")))))

(deftest process-actually-processes
  (testing "process can actually find things, and process them."
    (let [log (process analyzer "test/test.log")]
      (is (and (seq log)
               (seq (get-in log [:users "ane"])))))))
