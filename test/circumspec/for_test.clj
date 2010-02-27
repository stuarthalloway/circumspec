(ns circumspec.for-test
  (:use circumspec)
  (:require [circumspec.should :as s]))

(testing for-these
  (for-these [x y] (should (= (+ x 10) y))
             5 15
             0 10
             3 13
             325342342 325342352)
  (for-these [x y z] (should (= (+ x y) z))
             5 5 10
             0 0 0
             0 1 1
             -4 10 6))
