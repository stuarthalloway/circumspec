(ns pending-feature
  (:refer-clojure :exclude (assert))
  (:use circumspec
        [circumspec.utils :only (java-props->sh-args)]
        clojure.contrib.shell-out))

(def java-cmd
  ["java" "-Xmx1G"])

(def classpath
  ["-cp" "test:src:classes:features:lib/clojure.jar:lib/clojure-contrib.jar"])

(def clojure-cmd
  ["clojure.main" "-e" "(use 'circumspec.cli) (run-tests)"])

(defn run-circumspec-args
  [props]
  (concat java-cmd classpath (java-props->sh-args props) clojure-cmd))

(defn run-circumspec-tests
  [props]
  (apply sh (run-circumspec-args props)))

(describe "a single pending test"
  (assert "dot reporting"
    (= "P\n"
       (run-circumspec-tests {:circumspec.test-regex "pending-example$"
                              :circumspec.report-function "dot"
                              :circumspec.test-dir "features"
                              :circumspec.colorize "false"}))))