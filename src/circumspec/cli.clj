(ns circumspec.cli
  (:require [circumspec.report dot nested])
  (:use [circumspec.config])
  (:use [circumspec.colorize :only (*colorize*)])
  (:use [circumspec.runner :only (namespace-result-seq test-namespaces)]))

(defn run-tests
  "Runs all tests. Assumes tests are in test directory an have namespaces
   ending in -test."
  []
  (binding [*colorize* (colorize)]
    ((report-function)
     (namespace-result-seq
      (test-namespaces (test-dir) (test-regex)))))

  (shutdown-agents))