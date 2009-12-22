(ns circumspec.assert-test
  (:refer-clojure :exclude [assert])
  (:use circumspec)
  (:require [circumspec.assert :as a]))

(describe a/match-fn
 (for-these [fn type] (assert (= fn (a/match-fn type)))
            re-find #"foo"
            = "foo"))

(describe "assert throws"
  (let [f 0]
    (assert (throws? ArithmeticException (/ 1 f)))
    (assert (throws? Exception (/ 1 f)))
    (assert (throws? ArithmeticException "Divide by zero" (/ 1 f)))
    (assert (throws? ArithmeticException #"by" (/ 1 f)))))

(describe a/assert-exception-matches
  (assert (a/assert-exception-matches nil "boom" (Throwable. "boom"))))

