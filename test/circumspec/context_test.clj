(ns circumspec.context-test
  (:refer-clojure :exclude (assert)) 
  (:use circumspec)
  (:require [circumspec.context :as c]))

(describe
 c/context-form?
 (it "recognizes forms that establish circumspec context"
     (for-these [result form] (assert (= result (c/context-form? form)))
                false 1
                false '(foo)
                true '(describe)
                true '(it))))