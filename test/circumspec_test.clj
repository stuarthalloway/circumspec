(ns circumspec-test
  (:use circumspec))

(describe
 "test execution context"
 (it
  "run tests in the namespace where they are defined"
  (*ns* should = (find-ns 'circumspec-test))))

(describe
 "good old fashioned fn"
 (it
  "can do basic things"
  (2 should = 2)
  (3 should not = 2)
  (2 should be = 2)
  (2 should be #(< 1 % 3))
  (2 should be (fn [x] (< 1 x 3)))))

(describe
 "it looks better when described"
 (it
  "also works inside describe"
  (3 should = 3)
  ((+ 1 42) should = 43))
 (describe
  "inside of another describe"
  (it
   "should work"
   (true should be = true))))

(describe
 "predicates"

 (it "handles clojure predicates"
     (3 should be integer)
     (1 should be odd)))

(describe
 "failures"
 (it "should match exception type"
     ((/ 3 0) should throw ArithmeticException))
 (it "should match exception message string"
     ((/ 4 0) should throw ArithmeticException "Divide by zero"))
 (it "should match regular expression"
     ((/ 5 0) should throw ArithmeticException #"by")))

(describe
 "multiple assertions"
 (it
  "should be possible to do"
  (for-these [x y] (+ x 10) should = y
           5 15
           0 10
           3 13
           325342342 325342352))
 (it
  "should take three items"
  (for-these [x y z] (+ x y) should = z
           5 5 10
           0 0 0
           0 1 1
           -4 10 6)))

;; (comment
;;   "these will work"
;;   (describe
;;    "Circumspect"
;;    [:metadata]
;;    (it "is possible to do a basic assertion"
;;        (let [curvy-glyph? ...]
;;          (2 should have curvy-glyph))
;;        ([1 2] should contain 1)
;;        ([1 2] should =set [2 1])


;;   (it
;;     "should be possible to mock out a function, at least partially"
;;     (foo should be called with (42 555 blarg)))
 

