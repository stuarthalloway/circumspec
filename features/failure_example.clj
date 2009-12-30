(ns failure-example
  (:refer-clojure :exclude (assert))
  (:use circumspec))

(describe "throws? with no body should fail"
  (assert (throws? Exception)))