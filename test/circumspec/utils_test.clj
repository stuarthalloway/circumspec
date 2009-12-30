(ns circumspec.utils-test
  (:refer-clojure :exclude [assert])
  (:use circumspec circumspec.utils
        clojure.contrib.with-ns))

(describe pop-optional-args
  (for-these [output input] (assert (= output (apply pop-optional-args input)))
             [[1 2]] [[] [1 2]]
             [nil [1 2]] [[string?] [1 2]]
             [1 [2]] [[integer?] [1 2]]
             [1 2 []] [[odd? even?] [1 2]]
             ['throws? IllegalArgumentException [0]] [[symbol? class?] ['throws? IllegalArgumentException 0]]))

(describe class-symbol?
  (for-these [output input] (assert (= output (class-symbol? input)))
             false 'dribble.dribble.Shoot
             true 'String
             true 'java.lang.String))

(describe java-props->sh-args
  (for-these [output input] (assert (= output (java-props->sh-args input)))
             ["-Dfoo=bar"] {:foo "bar"}
             ["-Dbaz=quux"] {"baz" "quux"}
             ["-Done=1" "-Dtwo=2"] {:one "1" :two "2"}))

(describe resolve!
  (it "resolves variables if possible"))

(describe defn!
  (it "lets you define a function once"
    (assert (= (with-temp-ns (eval '(do
                                      (circumspec.utils/defn! fn-defined-only-once [] :retval)
                                      (fn-defined-only-once))))
               :retval)))
  (it "throws an exception if you try to define a function twice"
    (assert (throws?
             clojure.lang.Compiler$CompilerException #"^java.lang.RuntimeException: The name example-fn is already bound in sym.*$"
             (with-temp-ns (eval '(do
                                    (circumspec.utils/defn! example-fn [])
                                    (circumspec.utils/defn! example-fn []))))))))
