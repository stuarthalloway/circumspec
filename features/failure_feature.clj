(ns failure-feature
  (:refer-clojure :exclude (assert))
  (:use circumspec 
        [feature-helpers :only (run-circumspec-tests)]))

(testing "some tests that should fail"
  (let [results (run-circumspec-tests {:circumspec.test-regex "failure-example$"
                                       :circumspec.report-function "dot"
                                       :circumspec.test-dir "features"
                                       :circumspec.colorize "false"})]
    (assert
      (= (:out results)  "FFFFFF\n0 success, 6 failure, 0 error, 0 pending\n"))
    (assert
      (= (:exit results) 1))
    (assert
      (= (:err results) ""))))