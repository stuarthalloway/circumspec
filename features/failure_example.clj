(ns failure-example
  (:use circumspec))

(describe "failures"
  (testing "throws? with no body should fail"
    (should (throws? Exception)))

  (testing "not equal should fail"
    (should (= (+ 1 41) 43)))

  (testing "error not thrown should fail"
    (should (throws? ArithmeticException (+ 1 2))))

  (testing "wrong error message should fail"
    (let [x 0]
      (should (throws? ArithmeticException "Boom" (/ 3 x)))))

  (testing "incomplete string match should fail"
    (let [x 0]
      (should (throws? ArithmeticException "zero" (/ 3 x))))))