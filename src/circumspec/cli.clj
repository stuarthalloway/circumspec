(ns circumspec.cli
  (:require [circumspec.report dot nested])
  (:use [circumspec.config])
  (:require [circumspec.raw :as raw])
  (:use [circumspec.runner :only (test-results)]
        [circumspec.locator :only (tests)]))

(defn tally
  [result-seq]
  (reduce
   #(apply merge-with + %&)
   (map #(select-keys % [:success :failure :error :pending]) result-seq)))

(defn report-tally
  [tally]
  (println
   (apply format "%d success, %d failure, %d error, %d pending"
          (map #(get tally % 0) [:success :failure :error :pending]))))

(defn exit-code
  [tally]
  (cond
   (:error tally) 2
   (:failure tally) 1
   :default 0))

(defn run-tests
  "Runs all tests for current configuration."
  []
  (let [report (report-function)
        results (test-results (tests))
        tally (tally results)]
    (report results)
    (report-tally tally)
    (raw/dump-results results)
    (shutdown-agents)
    (System/exit (exit-code tally))))