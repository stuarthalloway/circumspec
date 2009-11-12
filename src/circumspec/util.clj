(ns circumspec.util
  (:use clojure.contrib.find-namespaces))

(defn test-namespaces
  "Find all test namespaces in a dir"
  [dir]
  (filter
   #(re-find #"-test$" (str  %)) 
   (find-namespaces-in-dir (java.io.File. dir))))

