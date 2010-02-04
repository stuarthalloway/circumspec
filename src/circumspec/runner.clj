(ns circumspec.runner
  (:require [circumspec.config :as config]
            [circumspec.raw :as raw])
  (:use circumspec.test
        [circumspec.test :only (test-description)]
        [circumspec.locator :only (tests)]
        [clojure.contrib.error-kit :only (with-handler handle continue-with)]))

;; TODO: rename to -result, with test-description as result-template
(defn success-result
  [test-description]
  (assoc test-description :success 1))

(defn pending-result
  [test-description]
  (assoc test-description :pending 1))

(defn fail-result
  [test-description assert-failed-exception]
  (merge (.details assert-failed-exception)
         test-description
         {:failure 1}))

(defn error-result
  [test-description throwable]
  (merge test-description
         {:error 1
          :throwable throwable}))

(defn base-result
  [test-description story]
  (assoc test-description :story story))

(defmacro with-timing
  "Time body, which should return a map. Merge the execution
   time into the result under the :nsec key."
  [& body]
  `(let [start# (System/nanoTime)
         result# (do ~@body)]
     (assoc result# :nsec (- (System/nanoTime) start#))))

(def *current-test* nil)
(def *story* nil)

(defn run-test
  [var]
  (binding [*current-test* (test-description var)
            *story* []]
    (if (pending? var)
      (pending-result *current-test*)
      (try
       (with-timing
         (@var)
         (success-result (base-result *current-test* *story*)))
       (catch circumspec.AssertFailed afe
         (fail-result (base-result *current-test* *story*) afe))
       (catch Throwable t
         (error-result (base-result *current-test* *story*) t))))))

(defn test-results
  [tests]
  (map run-test tests))

(defn tally
  [result-seq]
  (reduce
   #(apply merge-with + %&)
   (map #(select-keys % [:success :failure :error :pending]) result-seq)))

(defn report-tally
  [tally]
  (let [{:keys [success failure error pending nsec]} tally]
    (println
     (apply format "%d success, %d failure, %d error, %d pending [%d msec]"
            (map #(or % 0) [success failure error pending (quot nsec 1000000)])))))

(defn exit-code
  [tally]
  (cond
   (:error tally) 2
   (:failure tally) 1
   :default 0))

(defn run-tests
  "Runs all tests for current configuration, or as
   passed in via tests."
  ([] (run-tests (tests)))
  ([tests]
     (let [start (System/nanoTime)
           results (test-results tests)
           tally (assoc (tally results) :nsec (- (System/nanoTime) start))
           report (config/report-function)]
       (report results)
       (raw/dump-results results)
       (report-tally tally)
       tally)))

(defn run-tests-and-exit
  "Run tests and exit the process"
  [& args]
  (let [tally (apply run-tests args)]
    (shutdown-agents)
    (System/exit (exit-code tally))))






