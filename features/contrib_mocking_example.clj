(ns contrib-mocking-example
  (:refer-clojure :exclude (assert))
  (:use circumspec circumspec.contrib-mocking))

(testing "example test with failed mock expectation"
  (expect [assoc (times 1)]))