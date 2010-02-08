(ns greeter-test
  (:use circumspec greeter))

;; TODO: fix zero-arg support in =>
(describe "Circumspec Greeter"
  (it "should say hello"
    (should (= "Hello Circumspec!" (greet))))
  (testing-fn greet
    (=> "Hello Circumspec!")))