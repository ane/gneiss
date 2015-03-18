(ns gneiss.core-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [gneiss.core :refer :all]))

(deftest process-cannot-find
  "Tests that process correctly throws an exception
   when it can't find a file."
  (is (thrown? java.io.FileNotFoundException
               (process "imaginary-foobar<\\ding/dong<flargh.log"))))

(deftest process-processes
  "Tests process can actually find things, and process them."
  (let [log (process "test/test.log")]
    (is (and (seq log)
             (seq (get-in log [:users "ane"]))))))
