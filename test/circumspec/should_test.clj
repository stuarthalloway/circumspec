(ns circumspec.should-test
  (:refer-clojure :exclude (assert))
  (:use circumspec circumspec.should))

(describe "should"
  (testing "equality"
    (2 should = 2)
    ((+ 1 42) should = 43))
  (testing "inequality"
    (3 should not = 2))
  (testing "predicates"
    (3 should be integer)
    (1 should be odd))
  (testing "accessing let values"
    (let [three? #(= % 3)]
      (3 should be three)))
  (testing "with anonymous functions"
    (2 should be #(< 1 % 3))
    (2 should be (fn [x] (< 1 x 3)))))

(testing "for-these test"
  (for-these [x y] ((+ x 10) should = y)
             5 15
             0 10
             3 13
             325342342 325342352)
  (for-these [x y z] ((+ x y) should = z)
             5 5 10
             0 0 0
             0 1 1
             -4 10 6))

(testing "exeptions matching exception type"
    ((/ 3 0) should throw ArithmeticException)
    ((/ 4 0) should throw ArithmeticException "Divide by zero")
    ((/ 5 0) should throw ArithmeticException #"by"))

(testing reorder
  (assert "no reordering necessary"
    (= '(assert (= 1 1))
       (reorder '(assert (= 1 1)))))
  (assert "positive assertion"
    (= '(circumspec/assert (= a b))
       (reorder '(a should = b)))))



