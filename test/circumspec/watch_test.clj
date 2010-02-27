(ns circumspec.watch-test
  (:use circumspec)
  (:require [circumspec.watch :as w]))

(testing-fn w/source-ns->test-ns
  ('foobar => 'foobar-test))

(testing-fn w/test-ns->source-ns
  ('foobar-test => 'foobar))

(testing-fn w/test-namespace
  ("src/circumspec/runner.clj" => 'circumspec.runner-test)
  ("test/circumspec/runner_test.clj" => 'circumspec.runner-test))

(testing w/mark-watched
  (binding [w/last-watched-atom (atom {})]
    (w/mark-watched 111 ['circumspec.runner-test])
    (should (= {'circumspec.runner-test 111}
               @w/last-watched-atom))
    (should (= 111 (w/last-watched "src/circumspec/runner.clj")))
    (should (= 111 (w/last-watched "test/circumspec/runner_test.clj")))))

(testing "finding changed namespaces"
  (binding [w/last-watched-atom (atom {})]
    (should (seq (w/find-recent-namespaces-in-dir "src")))))
