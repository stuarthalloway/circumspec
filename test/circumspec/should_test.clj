(ns circumspec.should-test
  (:use circumspec)
  (:require [circumspec.should :as s]))

(testing "match-fn"
  (for-these [fn type] (should (= fn (s/match-fn type)))
             re-find #"foo"
             = "foo"))

(testing "should throws"
  (let [f 0]
    (should (throws? ArithmeticException (/ 1 f)))
    (should (throws? Exception (/ 1 f)))
    (should (throws? ArithmeticException "Divide by zero" (/ 1 f)))
    (should (throws? ArithmeticException #"by" (/ 1 f)))))

(testing "should-exception-matches"
  (should (s/should-exception-matches nil "boom" (Throwable. "boom"))))

(testing "for-these test"
  (for-these [x y] (should (= (+ x 10) y))
             5 15
             0 10
             3 13
             325342342 325342352)
  (for-these [x y z] (should (= (+ x y) z))
             5 5 10
             0 0 0
             0 1 1
             -4 10 6))




