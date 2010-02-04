(ns circumspec.context-test
  (:use circumspec)
  (:require [circumspec.context :as c]))

(describe "circumspec.context"
  (describe c/test-function-metadata
    (testing "with a non-empty body"
      (should (= {:circumspec/test true
                  :circumspec/name "foo"
                  :circumspec/context '(concat [(.name *ns*)] circumspec.context/*context*)}
                 (c/test-function-metadata "foo" '(list)))))
    (testing "tests with empty bodies are pending"
      (should (= {:circumspec/test true
                  :circumspec/name "foo"
                  :circumspec/context  '(concat [(.name *ns*)] circumspec.context/*context*)
                  :circumspec/pending true}
                 (c/test-function-metadata "foo" '())))))

  (describe-function +
    (1 2 => 3)
    (4 5 => 9)))