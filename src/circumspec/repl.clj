(ns circumspec.repl
  (:use circumspec.config
        [circumspec.utils :only (with-re-defn)]
        [circumspec.runner :only (namespace-result-seq)]))

(defn re-test [& namespaces]
  (with-re-defn
    (doseq [n namespaces]
      (require :reload-all n)))
  ((report-function) (namespace-result-seq namespaces)))

