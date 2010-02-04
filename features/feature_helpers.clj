(ns feature-helpers
  (:use [clojure.contrib.classpath :only (classpath)]
        [clojure.contrib.str-utils :only (str-join)]
        [circumspec.utils :only (java-props->sh-args)]
        clojure.contrib.shell-out))

(def java-cmd
  ["java" "-Xmx1G"])

(def classpath-args
  ["-cp" (str-join ":" (classpath))])

(def clojure-cmd
  ["clojure.main" "-e" "(use 'circumspec.runner) (run-tests-and-exit)"])

(defn run-circumspec-args
  [props]
  (concat java-cmd classpath-args (java-props->sh-args props) clojure-cmd))

(defn run-circumspec-tests
  [props]
  (apply sh :return-map true (run-circumspec-args props)))

