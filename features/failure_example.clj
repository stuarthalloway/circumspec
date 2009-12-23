(ns failure-example
  (:refer-clojure :exclude (assert))
  (:use circumspec))

(describe "example failure test"
  (assert (= 5 (+ 2 2))))