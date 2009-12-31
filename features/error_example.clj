(ns error-example
  (:refer-clojure :exclude (assert))
  (:use circumspec))

(testing "example error test"
  (throw (RuntimeException. "boom")))