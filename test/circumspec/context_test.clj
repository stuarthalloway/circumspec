(ns circumspec.context-test
  (:refer-clojure :exclude (assert)) 
  (:use circumspec)
  (:require [circumspec.context :as c]))

(describe "circumspec.context"
  (testing "c/calls-describe?"
    (for-these [result form] (assert (= result (c/calls-describe? form)))
               false nil
               false '(random stuff)
               true '(describe "stuff")
               false '(not a describe)
               true '(foo (describe "inner"))))

  (describe c/test-function-metadata
    (testing "with a non-empty body"
      (assert (= {:circumspec/spec true
                  :circumspec/name "foo"
                  :circumspec/context 'circumspec.context/*context*}
                 (c/test-function-metadata "foo" '(list)))))
    (testing "tests with empty bodies are pending"
      (assert (= {:circumspec/spec true
                  :circumspec/name "foo"
                  :circumspec/context  'circumspec.context/*context*
                  :circumspec/pending true}
                 (c/test-function-metadata "foo" '())))))

  (testing "=>-assertion?"
    (assert (false? (c/=>-assertion? nil)))
    (assert (false? (c/=>-assertion? [])))
    (assert (false? (c/=>-assertion? '[a])))
    (assert (false? (c/=>-assertion? '[a =>])))
    (assert (false? (c/=>-assertion? '[=> a])))
    (assert (false? (c/=>-assertion? '[a => b c])))
    (assert (true? (c/=>-assertion? '[a => b])))
    (assert (true? (c/=>-assertion? '[a b => c]))))

  (testing "rewrite-=>"
    (assert (= (c/rewrite-=> '+ '[1 2 => 3])
               '(circumspec.assert/assert (clojure.core/= (clojure.core/apply + [1 2]) 3)))))

  (testing "describe-function"
    (assert (= '(circumspec.utils/defn! add-test
                  "Generated test from the describe-function macro."
                  []
                  (circumspec.assert/assert (clojure.core/= (clojure.core/apply add [1 2]) 3))
                  (circumspec.assert/assert (clojure.core/= (clojure.core/apply add [4 5]) 9)))
               (macroexpand-1
                '(circumspec.context/describe-function add (1 2 => 3) (4 5 => 9))))))

  (testing "test-function-name"
    (assert (= 'foo-test (c/test-function-name 'foo)) "symbol that does not resolve")
    (assert (= 'last-test (c/test-function-name 'last)) "symbol that resolves")
    (assert (= 'spec-vars-test (c/test-function-name 'c/spec-vars)) "namespace-abbrev prefixed symbol")
    (assert (= 'last-test (c/test-function-name 'clojure.core/last)) "namespace prefixed symbol"))

  (describe-function +
    (1 2 => 3)
    (4 5 => 9)))