(ns circumspec.runner
  (:use circumspec.test
        [circumspec.test :only (test-description)]
        [clojure.contrib.error-kit :only (with-handler handle continue-with)]
        [clojure.contrib.find-namespaces :only (find-namespaces-in-dir)]))

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

(defn test-result-seq
  [test-vars]
  (map run-test test-vars))

(defn namespace-result-seq
  [namespaces]
  (test-result-seq (tests namespaces)))

(defn test-namespaces
  "Find test namespaces in basedir matching regexp"
  [basedir regexp]
  (->> (find-namespaces-in-dir (java.io.File. basedir))
      (map str)
      (filter (partial re-find regexp))
      (map symbol)))


