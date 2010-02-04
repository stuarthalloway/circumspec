(ns circumspec.locator
  (:use [clojure.contrib.seq-utils :only (flatten)]
        [clojure.contrib.find-namespaces :only (find-namespaces-in-dir)]
        [circumspec.test :only (test?)])
  (:require [circumspec.config :as config]))

(defn ns-vars
  [ns]
  (map second (ns-publics ns)))

(defn test-namespaces
  "Find test namespaces in basedir matching regexp,
   or from config settings."
  ([] (test-namespaces (config/test-dir) (config/test-regex)))
  ([basedir regexp]
     (->> (find-namespaces-in-dir (java.io.File. basedir))
          (map str)
          (filter (partial re-find regexp))
          (map symbol))))

(defn tests
  ([] (tests (test-namespaces)))
  ([namespaces]
     (doseq [ns namespaces]
       (require ns))
     (flatten
      (map #(filter test? (ns-vars %)) namespaces))))

