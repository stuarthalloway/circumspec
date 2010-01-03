(ns circumspec.report.nested-test
  (:refer-clojure :exclude [assert])
  (:use circumspec circumspec.report.nested))

(describe-function indent
  (0 "foo" => "foo")
  (1 "bar" => "  bar")
  (2 "baz" => "    baz"))