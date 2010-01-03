(ns circumspec.report.nested-test
  (:refer-clojure :exclude [assert])
  (:use circumspec circumspec.report.nested))

(describe-function indent
  (0 "foo" => "foo")
  (1 "bar" => "  bar")
  (2 "baz" => "    baz"))

(describe-function narrative-string
  (nil => "")
  ({:context ["level 1"]} => "")
  ({:context ["level 1" "level 2"], :story ["given this" "then that"]}
    => "    given this\n    then that"))