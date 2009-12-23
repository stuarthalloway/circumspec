(ns failure-feature
  (:refer-clojure :exclude (assert))
  (:use circumspec 
        [feature-helpers :only (run-circumspec-tests)]))

(describe "a single failing test"
  (let [results (run-circumspec-tests {:circumspec.test-regex "failure-example$"
                                       :circumspec.report-function "dot"
                                       :circumspec.test-dir "features"
                                       :circumspec.colorize "false"})]
    (assert
      (= (:out results)  "F\n0 success, 1 failure, 0 error, 0 pending\n"))
    (assert
      (= (:exit results) 1))
    (assert
      (= (:err results) ""))))