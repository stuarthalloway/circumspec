(ns test
  (:use circumspec 
        clojure.contrib.str-utils))

(defn test-files
  "Please make this helper part of some core lib somewhere..."
  []
  (filter
   #(re-find #"_test.clj$" %) 
   (map
    #(.getName %)
    (file-seq (java.io.File. "test")))))

(defn run-tests!
  "Run all tests. Exit the VM with an error code if a test
   fails, unless keep-alive is true."
  ([] (run-tests! false))
  ([keep-alive]
     (reset! circumspec/registered-descriptions [])
     (let [names (map #(re-sub #".clj" "" %) (test-files))]
       (doseq [f names]
         (load f))
       (let [result (run-tests)]
         (if keep-alive
           result
           (System/exit (if result 0 -1)))))))