(ns circumspec.cli
  (:require [circumspec.report dot nested])
  (:use [circumspec.colorize :only (*colorize*)])
  (:use [circumspec.runner :only (namespace-result-seq test-namespaces)]))

;; TODO: macro to define and document properties
(defn- colorize?
  "Should we colorize, based on system property circumspec.colorize?"
  []
  (not (#{"no" "false"} (System/getProperty "circumspec.colorize"))))

(defn report-fn
  "Choose a report function. To use a different report function, find 
   or create a circumspec.report.xxx namespsace with a report function,
   and specify it with -Dcircumspec.report=xxx on the command line."
  []
  (ns-resolve
   (symbol (str "circumspec.report." (System/getProperty "circumspec.report" "nested")))
   'report))

(defn test-regex
  "Gets test-regex from -Dcircumspec.test-regex. Default to -test$"
  []
  (java.util.regex.Pattern/compile
   (System/getProperty "circumspec.test-regex" "-test$")))

(defn test-dir
  "Gets test-dir from -Dcircumspec.test-dir. Default to test"
  []
  (System/getProperty "circumspec.test-dir" "test"))

(defn run-tests
  "Runs all tests. Assumes tests are in test directory an have namespaces
   ending in -test."
  []
  (binding [*colorize* (colorize?)]
    ((report-fn)
     (namespace-result-seq
      (test-namespaces (test-dir) (test-regex)))))

  (shutdown-agents))