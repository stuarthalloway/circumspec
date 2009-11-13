(ns failing-test
  (:use circumspec))

;; TODO: use metadata to leave these out of the main build so the
;; self-tests don't fail.
(describe
 "failing"

 (it
  "should fail (not equal)"
  (42 should = 43))

  (it
   "should fail (throws an error)"
   ((/ 3 0) should = 0))

  (it
   "should fail (error not thrown)"
   ((+ 1 2) should throw ArithmeticException))

  (it
   "should fail (wrong error thrown)"
   ((/ 3 0) should throw NullPointerException))

  (it
   "should fail (wrong message)"
   ((/ 3 0) should throw ArithmeticException "Boom"))

  (it
   "should fail (partial string not good enough)"
   ((/ 3 0) should throw ArithmeticException "zero")))
