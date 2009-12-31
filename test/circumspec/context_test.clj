(ns circumspec.context-test
  (:refer-clojure :exclude (assert)) 
  (:use circumspec)
  (:require [circumspec.context :as c]))

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