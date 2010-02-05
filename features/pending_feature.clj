(ns pending-feature
  (:use circumspec 
        [feature-helpers :only (run-circumspec-tests)]))

(testing "a single pending test"
  (let [results (run-circumspec-tests {:circumspec.test-regex "pending-example$"
                                       :circumspec.report-function "dot"
                                       :circumspec.test-dir "features"
                                       :circumspec.colorize "false"})]
    (should
      (re-find #"P\n0 success, 0 failure, 0 error, 1 pending \[\d+ msec\]\n" (:out results)))
    (should
      (= (:exit results) 0))
    (should
      (= (:err results) ""))))