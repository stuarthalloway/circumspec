;; Hopefully temporary, should roll own mocking.
(ns circumspec.contrib-mocking
  (:require [clojure.contrib.mock :as mock])
  (:use [clojure.contrib.ns-utils :only (immigrate)]
        [circumspec.should :only (fail)])) 

(immigrate 'clojure.contrib.mock)

(defn report-problem
 "This function is designed to be used in a binding macro to override
the report-problem function in clojure.contrib.mock. Instead of printing
the error to the console, the error is logged via clojure.test."
 {:dynamic true}
 [fn-name expected actual msg]
 (fail {:message (str msg " Function name: " fn-name),
        :expected expected,
        :actual actual}))


(defmacro expect [& body]
  "Use this macro instead of the standard c.c.mock expect macro to have
failures reported through clojure.test."
  `(binding [mock/report-problem report-problem]
     (mock/expect ~@body)))


