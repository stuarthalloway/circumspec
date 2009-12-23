(ns error-example
  (:refer-clojure :exclude (assert))
  (:use circumspec))

(describe "example error test"
  (throw (RuntimeException. "boom")))