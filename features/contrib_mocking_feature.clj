(ns contrib-mocking-feature
  (:use circumspec 
        [feature-helpers :only (run-circumspec-tests)]))

(testing "a single test with failed mocking expectation"
  (let [results (run-circumspec-tests {:circumspec.test-regex "contrib-mocking-example$"
                                       :circumspec.report-function "dot"
                                       :circumspec.test-dir "features"
                                       :circumspec.colorize "false"})]
    (should
      (= (:out results)  "F\n0 success, 1 failure, 0 error, 0 pending\n"))
    (should
      (= (:exit results) 1))
    (should
      (= (:err results) ""))))