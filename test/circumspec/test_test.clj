(ns circumspec.test-test
  (:use circumspec circumspec.for-all)
  (:require [circumspec.test :as t]))

(testing t/make-test-name
  (let [whitespacey-string (string-of alpha-ascii famous-whitespace (constantly \/))]
    (for-all [s (whitespacey-string)]
      (when-not (empty? s)
        (should (not (re-find #" |\t|\n|/|\." (t/make-test-name s))))))))

(testing t/=>-assertion?
  (should (false? (t/=>-assertion? nil)))
  (should (false? (t/=>-assertion? [])))
  (should (false? (t/=>-assertion? '[a])))
  (should (false? (t/=>-assertion? '[a =>])))
  (should (false? (t/=>-assertion? '[=> a])))
  (should (false? (t/=>-assertion? '[a => b c])))
  (should (true? (t/=>-assertion? '[a => b])))
  (should (true? (t/=>-assertion? '[a b => c]))))

(testing t/rewrite-=>
  (should (= (t/rewrite-=> '+ '[1 2 => 3])
             '(circumspec.should/should (clojure.core/= (+ 1 2) 3)))))

(testing t/testing-fn
  (should (= '(circumspec.utils/defn! add-test
                "Generated test from the testing-fn macro."
                []
                  (circumspec.should/should (clojure.core/= (add 1 2) 3))
                  (circumspec.should/should (clojure.core/= (add 4 5) 9)))
             (macroexpand-1
              '(circumspec.test/testing-fn add (1 2 => 3) (4 5 => 9))))))

(testing t/test-function-name
    (should (= 'foo-test (t/test-function-name 'foo)) "symbol that does not resolve")
    (should (= 'last-test (t/test-function-name 'last)) "symbol that resolves")
    (should (= 'spec-vars-test (t/test-function-name 't/spec-vars)) "namespace-abbrev prefixed symbol")
    (should (= 'last-test (t/test-function-name 'clojure.core/last)) "namespace prefixed symbol"))



