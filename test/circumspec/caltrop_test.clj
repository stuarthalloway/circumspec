(ns circumspec.caltrop-test
  (:use circumspec circumspec.caltrop clojure.contrib.pprint clojure.walk)
  (:require [circumspec.context :as c]))

(describe "woot"
  (caltrops (+ 4 4) (+ 6 6))
  (describe "subwooter"
    (caltrops (+ 5 5))))


