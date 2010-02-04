(ns error-feature
  (:use circumspec 
        [feature-helpers :only (run-circumspec-tests)]))

(describe "a single error test"
  (testing "with dot reporting"
    (let [results (run-circumspec-tests {:circumspec.test-regex "error-example$"
                                         :circumspec.report-function "dot"
                                         :circumspec.test-dir "features"
                                         :circumspec.colorize "false"})]
      (should
       (= (:out results)  "E\n0 success, 0 failure, 1 error, 0 pending\n"))
      (should
       (= (:exit results) 2))
      (should
       (= (:err results) ""))))
  (testing "a nested error indents the stack trace"
    (let [results (run-circumspec-tests {:circumspec.test-regex "error-example$"
                                         :circumspec.test-dir "features"
                                         :circumspec.colorize "false"})]
      (should
       (re-find
        #"^error-example
  a single error
    nested to demonstrate report indentation ERROR
      circumspec exception:
      thrown java.lang.RuntimeException: boom
      \tat error_"
        (:out results))))))