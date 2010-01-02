(ns circumspec.runner
  (:use circumspec.context
        [clojure.contrib.error-kit :only (with-handler handle continue-with)]
        [clojure.contrib.find-namespaces :only (find-namespaces-in-dir)])
  (:require [circumspec.assert :as ca]))

;; TODO: rename to -result, with spec-description as result-template
(defn success-description
  [spec-description]
  (assoc spec-description :success 1))

(defn pending-description
  [spec-description]
  (assoc spec-description :pending 1))

(defn fail-description
  [spec-description assert-failed-exception]
  (merge (.details assert-failed-exception)
         spec-description
         {:failure 1}))

(defn error-description
  [spec-description throwable]
  (merge spec-description
         {:error 1
          :throwable throwable}))

(defmacro with-timing
  "Time body, which should return a map. Merge the execution
   time into the result under the :nsec key."
  [& body]
  `(let [start# (System/nanoTime)
         result# (do ~@body)]
     (assoc result# :nsec (- (System/nanoTime) start#))))

(defn run-spec
  [var]
  (let [spec-desc (spec-description var)]
    (if (pending? var)
      (pending-description spec-desc)
      (try
       (with-timing
         (@var)
         (success-description spec-desc))
       (catch circumspec.AssertFailed afe
         (fail-description spec-desc afe))
       (catch Throwable t
         (error-description spec-desc t))))))

(defn spec-result-seq
  [spec-vars]
  (map run-spec spec-vars))

(defn namespace-result-seq
  [namespaces]
  (spec-result-seq (spec-vars namespaces)))

(defn test-namespaces
  "Find test namespaces in basedir matching regexp"
  [basedir regexp]
  (->> (find-namespaces-in-dir (java.io.File. basedir))
      (map str)
      (filter (partial re-find regexp))
      (map symbol)))


