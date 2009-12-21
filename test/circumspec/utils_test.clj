(ns circumspec.utils-test
  (:refer-clojure :exclude (assert))
  (:use circumspec circumspec.utils))

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