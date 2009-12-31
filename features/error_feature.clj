(ns error-feature
  (:refer-clojure :exclude (assert))
  (:use circumspec 
        [feature-helpers :only (run-circumspec-tests)]))

(testing "a single error test"
  (let [results (run-circumspec-tests {:circumspec.test-regex "error-example$"
                                       :circumspec.report-function "dot"
                                       :circumspec.test-dir "features"
                                       :circumspec.colorize "false"})]
    (assert
      (= (:out results)  "E\n0 success, 0 failure, 1 error, 0 pending\n"))
    (assert
      (= (:exit results) 2))
    (assert
      (= (:err results) ""))))