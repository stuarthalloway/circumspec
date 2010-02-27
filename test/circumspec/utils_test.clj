(ns circumspec.utils-test
  (:use circumspec circumspec.utils circumspec.for-all
        clojure.contrib.with-ns))

(describe class-symbol?
  (testing "always returns a boolean"
    (let [ascii-symbol (symbol-of alpha-ascii)]
      (for-all [s (ascii-symbol)]
               (when-not (= s (symbol "")) ;; clojure bug? empty symbol barfs
                 (should (contains? #{true false} (class-symbol? s)))))))
  (testing "returns true for names imported in our namespace"
    (for-all [s (class-symbol *ns*)]
             (should (true? (class-symbol? s))))))

(testing pop-optional-args
  (for-these [output input] (should (= output (apply pop-optional-args input)))
             [[1 2]] [[] [1 2]]
             [nil [1 2]] [[string?] [1 2]]
             [1 [2]] [[integer?] [1 2]]
             [1 2 []] [[odd? even?] [1 2]]
             ['throws? IllegalArgumentException [0]] [[symbol? class?] ['throws? IllegalArgumentException 0]]))

(testing-fn class-symbol?
  ('dribble.dribble.Shoot => false)
  ('String => true)
  ('java.lang.String => true))

(testing-fn java-props->sh-args
  ({:foo "bar"} => ["-Dfoo=bar"])
  ({"baz" "quux"} => ["-Dbaz=quux"])
  ({:one "1" :two "2"} => ["-Done=1" "-Dtwo=2"]))

(describe resolve!
  (it "resolves variables if possible"))

(describe defn!
  (it "lets you define a function once"
    (should (= (with-temp-ns (eval '(do
                                      (circumspec.utils/defn! fn-defined-only-once [] :retval)
                                      (fn-defined-only-once))))
               :retval)))
  (it "throws an exception if you try to define a function twice"
    (should (throws?
             clojure.lang.Compiler$CompilerException #"^java.lang.RuntimeException: The name example-fn is already bound in sym.*$"
             (with-temp-ns (eval '(do
                                    (circumspec.utils/defn! example-fn [])
                                    (circumspec.utils/defn! example-fn []))))))))
