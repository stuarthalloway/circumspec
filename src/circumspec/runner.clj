(ns circumspec.runner
  (:use circumspec.context
        [clojure.contrib.error-kit :only (with-handler handle continue-with)]
        [clojure.contrib.find-namespaces :only (find-namespaces-in-dir)])
  (:require [circumspec.assert :as ca]))

(defn pass-description
  [spec-description]
  (assoc spec-description :pass 1))

(defn fail-description
  [spec-description assert-failed-exception]
  (merge (.details assert-failed-exception)
         spec-description
         {:fail 1}))

(defn error-description
  [spec-description throwable]
  (merge spec-description
         {:error 1
          :throwable throwable}))

(defn run-spec
  [var]
  (let [spec-desc (spec-description var)]
    (try
     (@var)
     (pass-description spec-desc)
     (catch circumspec.AssertFailed afe
       (fail-description spec-desc afe))
     (catch Throwable t
       (error-description spec-desc t)))))

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


