(ns circumspec-test
  (:use circumspec))

;; TODO: use metadata to leave these out of the main build so the
;; self-tests don't fail.
(describe
 "failing"

 (it
  "should report failures correctly"
  (42 should = 43))

  (it
  "should report errors correctly"
  ((/ 3 0) should = 0)))
