(ns circumspec.util-test
  (:use circumspec circumspec.util clojure.contrib.seq-utils))

(describe
 "namespace utils"
 (it
  "should find namespaces"
  ((includes? (test-namespaces "test") 'circumspec-test) should = true))
 (it
  "should find nested namespaces"
  ((includes? (test-namespaces "test") 'circumspec.util-test) should = true)))

