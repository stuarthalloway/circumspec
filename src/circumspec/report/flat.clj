(ns circumspec.report.flat
  (:use circumspec.report
        [circumspec.assert :only (default-fail-message)]))


(def report-fns {:failure (fn [result] (str (result-name result) " FAILED\n" (default-fail-message result)))
                 :error (fn [result] (str (result-name result) " ERROR"))
                 :success (fn [result] (result-name result))})

(defn result-text
  [result]
  (cond
   (fail? result) (failure-string ((:failure report-fns) result))
   (error? result) (error-string ((:error report-fns) result))
   :default (success-string ((:success report-fns) result))))

(defn report
  [results]
  (doseq [i results]
    (println (result-text i))
    (flush)))