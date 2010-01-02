(ns circumspec.runner-test
  (:refer-clojure :exclude [assert])
  (:use circumspec)
  (:require [circumspec.runner :as r]))

(describe "with-timing"
  (it "adds nsec timings"
    (let [x (r/with-timing {:success 1})]
      (assert (= 2 (count x)))
      (assert (= 1 (:success x)))
      (assert (instance? Long (:nsec x))))))