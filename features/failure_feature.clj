(ns failure-feature
  (:use circumspec 
        [feature-helpers :only (run-circumspec-tests)]))

(testing "some tests that should fail"
  (let [results (run-circumspec-tests {:circumspec.test-regex "failure-example$"
                                       :circumspec.report-function "dot"
                                       :circumspec.test-dir "features"
                                       :circumspec.colorize "false"})]
    (should
      (re-find #"FFFFF\n0 success, 5 failure, 0 error, 0 pending \[\d+ msec\]\n$" (:out results)  ))
    (should
      (= (:exit results) 1))
    (should
      (= (:err results) ""))))