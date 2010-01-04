(ns story-example
  (:refer-clojure :exclude [assert when and])
  (:use circumspec circumspec.story))

(describe "a describe"
  (describe "with a nested describe"
    (describe "and even more nested"
      (testing "a test"
        (given "a given"
               (assert (= 1 2)))))))