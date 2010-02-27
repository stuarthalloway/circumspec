(ns circumspec.should-test
  (:use circumspec)
  (:require [circumspec.should :as s]))

(testing s/match-fn
  (for-these [fn type] (should (= fn (s/match-fn type)))
             re-find #"foo"
             = "foo"))

(testing "should throws"
  (let [f 0]
    (should (throws? ArithmeticException (/ 1 f)))
    (should (throws? Exception (/ 1 f)))
    (should (throws? ArithmeticException "Divide by zero" (/ 1 f)))
    (should (throws? ArithmeticException #"by" (/ 1 f)))))

(testing s/should-exception-matches
  (should (s/should-exception-matches nil "boom" (Throwable. "boom"))))





