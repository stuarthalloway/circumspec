(ns circumspec.runner
  (:use circumspec.context
        [clojure.contrib.error-kit :only (with-handler handle continue-with)]
        [clojure.contrib.find-namespaces :only (find-namespaces-in-dir)]))

;; TODO: rename to -result, with spec-description as result-template
(defn success-result
  [spec-description]
  (assoc spec-description :success 1))

(defn pending-result
  [spec-description]
  (assoc spec-description :pending 1))

(defn fail-result
  [spec-description assert-failed-exception]
  (merge (.details assert-failed-exception)
         spec-description
         {:failure 1}))

(defn error-result
  [spec-description throwable]
  (merge spec-description
         {:error 1
          :throwable throwable}))

(defn base-result
  [spec-description story]
  (assoc spec-description :story story))

(defmacro with-timing
  "Time body, which should return a map. Merge the execution
   time into the result under the :nsec key."
  [& body]
  `(let [start# (System/nanoTime)
         result# (do ~@body)]
     (assoc result# :nsec (- (System/nanoTime) start#))))

(def *current-spec* nil)
(def *story* nil)

(defn run-spec
  [var]
  (binding [*current-spec* (spec-description var)
            *story* []]
    (if (pending? var)
      (pending-result *current-spec*)
      (try
       (with-timing
         (@var)
         (success-result (base-result *current-spec* *story*)))
       (catch circumspec.AssertFailed afe
         (fail-result (base-result *current-spec* *story*) afe))
       (catch Throwable t
         (error-result (base-result *current-spec* *story*) t))))))

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


