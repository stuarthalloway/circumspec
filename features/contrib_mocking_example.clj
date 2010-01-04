(ns contrib-mocking-example
  (:use circumspec circumspec.contrib-mocking))

(testing "example test with failed mock expectation"
  (expect [assoc (times 1)]))