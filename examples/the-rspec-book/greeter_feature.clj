(ns greeter-feature
  (:refer-clojure :exclude [assert when and])
  (:use circumspec circumspec.story greeter))

(testing "greeter says hello"
  (given "a greeter")
  (when "I send it the greet message"
    (let [result (greet)]
      (then "I should see 'Hello Circumspec!'"
            (result should = "Hello Circumspec!")))))
