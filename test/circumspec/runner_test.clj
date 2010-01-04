(ns circumspec.runner-test
  (:use circumspec)
  (:require [circumspec.runner :as r]))

(describe "with-timing"
  (it "adds nsec timings"
    (let [x (r/with-timing {:success 1})]
      (should (= 2 (count x)))
      (should (= 1 (:success x)))
      (should (instance? Long (:nsec x))))))