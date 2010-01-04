(ns error-example
  (:use circumspec))

(testing "example error test"
  (throw (RuntimeException. "boom")))