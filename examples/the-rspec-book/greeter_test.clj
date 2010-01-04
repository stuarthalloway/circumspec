(ns greeter-test
  (:refer-clojure :exclude [assert])
  (:use circumspec greeter))

;; TODO: fix zero-arg support in =>
(describe "Circumspec Greeter"
  (it "should say hello (assert style)"
    (assert (= "Hello Circumspec!" (greet))))
  (it "should say hello (should style)"
    ((greet) should = "Hello Circumspec!"))
  (describe-function greet
    (=> "Hello Circumspec!")))