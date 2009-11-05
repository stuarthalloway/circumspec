(ns circumspec-test
  (:use circumspec))

(describe
 "good old fashioned fn"
 (it
  "can do basic things"
  (2 should = 2)
  (3 should not = 2)
  (2 should be = 2)
  (2 should be #(< 1 % 3))
  (2 should be integer)
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
 "failures"
 (it "should raise an exception"
     ((/ 3 0) should throw ArithmeticException)
     ((/ 3 0) should throw ArithmeticException "Boom")))

;; (comment
;;   "these will work"
;;   (describe
;;    "Circumspect"
;;    [:metadata]
;;    (it "is possible to do a basic assertion"
;;        (let [curvy-glyph? ...]
;;          (2 should have curvy-glyph))
;;        (2 should not = 3 ...)
;;        ([1 2] should contain 1)
;;        (2 should = 2)
;;        (2 should be #(< 1 % 3))
;;        (2 should be (fn [x] (< 1 x 3)))
;;        ((< 1 2 3) should be true)
;;        (2 should be numeric)
;;        ([1 2] should =set [2 1])
;;        (safe! should not fail)
;;        (burp! should fail)
;;        (burp! should fail-with ArithmeticException)
;;        (burp! should fail-with ArithmeticException "divide by zero"
;;             (burp! should fail-with "divide by zero"
;;                  (burp! should fail-with ArithmeticException #"zero"))          
;;             (burp! should fail-with #"zero")))
 
 
;;    (it "is possible to do a multiple assertion"
;;        [:pending]
;;        (for-all [x y] (* x y) should = (* y x)
;;                 10 15
;;                 1  1
;;                 0  20))))

;; (comment
;;   "Ancient history"
;;   ((+ 1 1) in some places of the world be > 2)
;;   ((+ 1 1) should as-big-as 2)
;;   ((+ 1 1) should 2) 
;;   (should (+ 1 1) 2)
;;   (2 should be (< 1 % 3))   ;;hmmm....
;;   (2 should be x (< 1 x 3)) ;;hmmm....)

