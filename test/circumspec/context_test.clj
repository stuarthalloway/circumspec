(ns circumspec.context-test
  (:use circumspec)
  (:require [circumspec.context :as c]))

(describe "circumspec.context"
  (testing "c/calls-describe?"
    (for-these [result form] (should (= result (c/calls-describe? form)))
               false nil
               false '(random stuff)
               true '(describe "stuff")
               false '(not a describe)
               true '(foo (describe "inner"))))

  (describe c/test-function-metadata
    (testing "with a non-empty body"
      (should (= {:circumspec/spec true
                  :circumspec/name "foo"
                  :circumspec/context 'circumspec.context/*context*}
                 (c/test-function-metadata "foo" '(list)))))
    (testing "tests with empty bodies are pending"
      (should (= {:circumspec/spec true
                  :circumspec/name "foo"
                  :circumspec/context  'circumspec.context/*context*
                  :circumspec/pending true}
                 (c/test-function-metadata "foo" '())))))

  (testing "=>-assertion?"
    (should (false? (c/=>-assertion? nil)))
    (should (false? (c/=>-assertion? [])))
    (should (false? (c/=>-assertion? '[a])))
    (should (false? (c/=>-assertion? '[a =>])))
    (should (false? (c/=>-assertion? '[=> a])))
    (should (false? (c/=>-assertion? '[a => b c])))
    (should (true? (c/=>-assertion? '[a => b])))
    (should (true? (c/=>-assertion? '[a b => c]))))

  (testing "rewrite-=>"
    (should (= (c/rewrite-=> '+ '[1 2 => 3])
               '(circumspec.should/should (clojure.core/= (clojure.core/apply + [1 2]) 3)))))

  (testing "describe-function"
    (should (= '(circumspec.utils/defn! add-test
                  "Generated test from the describe-function macro."
                  []
                  (circumspec.should/should (clojure.core/= (clojure.core/apply add [1 2]) 3))
                  (circumspec.should/should (clojure.core/= (clojure.core/apply add [4 5]) 9)))
               (macroexpand-1
                '(circumspec.context/describe-function add (1 2 => 3) (4 5 => 9))))))

  (testing "test-function-name"
    (should (= 'foo-test (c/test-function-name 'foo)) "symbol that does not resolve")
    (should (= 'last-test (c/test-function-name 'last)) "symbol that resolves")
    (should (= 'spec-vars-test (c/test-function-name 'c/spec-vars)) "namespace-abbrev prefixed symbol")
    (should (= 'last-test (c/test-function-name 'clojure.core/last)) "namespace prefixed symbol"))

  (describe-function +
    (1 2 => 3)
    (4 5 => 9)))