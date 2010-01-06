(ns error-example
  (:use circumspec))

(describe "a single error"
  (testing "nested to demonstrate report indentation"
    (throw (RuntimeException. "boom"))))