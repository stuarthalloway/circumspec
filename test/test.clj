(ns test
  (:use clojure.test
        clojure.contrib.str-utils))

(defn test-files
  "Please make this helper part of some core lib somewhere..."
  []
  (filter
   #(re-find #"_test.clj$" %) 
   (map
    #(.getName %)
    (file-seq (java.io.File. "test")))))

(defn run-tests! []
  (doseq [f (test-files)]
    (println f)
    (load (re-sub #".clj" "" f))
    (run-tests)))