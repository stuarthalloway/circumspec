(ns pending-feature
  (:refer-clojure :exclude (assert))
  (:use circumspec 
        [feature-helpers :only (run-circumspec-tests)]))

(testing "a single pending test"
  (let [results (run-circumspec-tests {:circumspec.test-regex "pending-example$"
                                       :circumspec.report-function "dot"
                                       :circumspec.test-dir "features"
                                       :circumspec.colorize "false"})]
    (assert
      (= (:out results)  "P\n0 success, 0 failure, 0 error, 1 pending\n"))
    (assert
      (= (:exit results) 0))
    (assert
      (= (:err results) ""))))