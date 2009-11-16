(ns circumspec-test
  (:use circumspec))

(def foo 27)

(describe
 "test execution context"
 (it
  "tests can access the namespace where they are defined"
  (foo should = 27))
 (let [three? #(= % 3)]
   (it "handles predicates let locally"
       (3 should be three))))

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
 "describe"
 (it
  "can have a nested it"
  (3 should = 3)
  ((+ 1 42) should = 43))
 (describe
  "can have a nested describe"
  (it
   "can have a subnested it"
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

(describe
 "it"
 (it
  "returns a map of information needed to run a test"
  ((macroexpand-1
    '(circumspec/it "works" (1 should = 1))) should =
    '(clojure.core/with-meta {:type :example
                              :description "works"
                              :forms-and-fns (circumspec/forms-and-fns ((1 should = 1)))}
       {})))
 
 (describe
  "rewrite-describe"
  (it
   "replaces describe with describe-inner if appearing at first of sequence"
   (for-these [input result] (rewrite-describe input) should = result
              '(describe foo) '(describe-inner foo)
              '(foo bar) '(foo bar)
              {:A :B} {:A :B})))
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
 

)