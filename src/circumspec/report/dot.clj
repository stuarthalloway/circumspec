(ns circumspec.report.dot
  (:use circumspec.report))

(defn dot
  [result]
  (cond
   (fail? result) (failure-string \F)
   (error? result) (error-string \E)
   (pending? result) (pending-string \P)
   :default (success-string \.)))

(defn report
  [results]
  (doseq [i results]
    (print (dot i))
    (flush))
  (println))