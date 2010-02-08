(ns circumspec.report.nested-test
  (:use circumspec circumspec.report.nested)
  (:use [circumspec.config :only (colorize)]))

(testing-fn indent
  (0 "foo" => "foo")
  (1 "bar" => "  bar")
  (2 "baz" => "    baz"))

(testing-fn indent-lines
  (0 nil => nil)
  (0 "foo\nbar" => "foo\nbar")
  (1 "foo\nbar" => "  foo\n  bar"))

(testing-fn context-lines
  (nil nil => [])
  ({} {} => [])
  ({} {:context ["level 1"]} => ["level 1"])
  ({:context ["level 1"]} {:context ["level 1"]} => [])
  ({:context ["1"]} {:context ["1" "2"]} => ["  2"]))

(testing-fn story-lines
  (nil => [])
  ({:context ["level 1"]} => [])
  ({:context [], :story ["given this" "then that"]}
   => ["  given this" "  then that"]))

(describe result-string
  (it "correctly handles full example"
    (binding [colorize (constantly false)]
      (should (= (report-string {} {:success 1,
                                    :story ["a greeter" "I send it the greet message" "I should see 'Hello Circumspec!'"],
                                    :context [], :name "greeter says hello"})
                 "greeter says hello
  a greeter
  I send it the greet message
  I should see 'Hello Circumspec!'")))))