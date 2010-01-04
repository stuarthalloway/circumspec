(ns circumspec.report.nested-test
  (:use circumspec circumspec.report.nested)
  (:use [circumspec.colorize :only (*colorize*)]))

(describe-function indent
  (0 "foo" => "foo")
  (1 "bar" => "  bar")
  (2 "baz" => "    baz"))

(describe-function context-lines
  (nil nil => [])
  ({} {} => [])
  ({} {:context ["level 1"]} => ["level 1"])
  ({:context ["level 1"]} {:context ["level 1"]} => [])
  ({:context ["1"]} {:context ["1" "2"]} => ["  2"]))

(describe-function story-lines
  (nil => [])
  ({:context ["level 1"]} => [])
  ({:context [], :story ["given this" "then that"]}
   => ["  given this" "  then that"]))

(describe result-string
  (it "correctly handles full example"
    (binding [*colorize* false]
      (should (= (report-string {} {:success 1,
                                    :story ["a greeter" "I send it the greet message" "I should see 'Hello Circumspec!'"],
                                    :context [], :name "greeter says hello"})
                 "greeter says hello
  a greeter
  I send it the greet message
  I should see 'Hello Circumspec!'")))))