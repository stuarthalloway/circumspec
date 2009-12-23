(ns contrib-mocking-feature
  (:refer-clojure :exclude (assert))
  (:use circumspec 
        [feature-helpers :only (run-circumspec-tests)]))

(describe "a single test with failed mocking expectation"
  (let [results (run-circumspec-tests {:circumspec.test-regex "clojure-mocking-example$"
                                       :circumspec.report-function "dot"
                                       :circumspec.test-dir "features"
                                       :circumspec.colorize "false"})]
    (assert
      (= (:out results)  "F\n0 success, 0 failure, 1 error, 0 pending\n"))
    (assert
      (= (:exit results) 2))
    (assert
      (= (:err results) ""))))