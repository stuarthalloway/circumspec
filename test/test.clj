(ns test
  (:use circumspec
        circumspec.util
        clojure.contrib.str-utils))

(defn run-tests!
  "Run all tests. Exit the VM with an error code if a test
   fails, unless keep-alive is true."
  ([] (run-tests! false))
  ([keep-alive]
     (reset! circumspec/registered-descriptions [])
     (doseq [f (test-namespaces "test")]
       (require f))
     (let [result (run-tests)]
       (if keep-alive
         result
         (System/exit (if result 0 -1))))))