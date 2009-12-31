(ns failure-example
  (:refer-clojure :exclude (assert))
  (:use circumspec))

(describe "failures"
  (testing "throws? with no body should fail"
    (assert (throws? Exception)))

  (testing "not equal should fail"
    ((+ 1 41) should = 43))

  (testing "error not thrown should fail"
    ((+ 1 2) should throw ArithmeticException))

  (testing "wrong error message should fail"
    ((/ 3 0) should throw ArithmeticException "Boom"))

  (testing "incomplete string match should fail"
    ((/ 3 0) should throw ArithmeticException "zero"))

  (testing "failed predicate"
    (1 should be even)))