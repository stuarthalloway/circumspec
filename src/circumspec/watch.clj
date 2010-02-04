(ns circumspec.watch
  (:require [circumspec.config :as config])
  (:use clojure.contrib.find-namespaces
        [circumspec.utils :only (with-re-defn)]
        [circumspec.runner :only (run-tests)]
        [circumspec.locator :only (tests test-namespaces)]
        [clojure.contrib.java-utils :only (as-file)]))
  
(def last-watch (agent :disabled))

(defn enabled?
  []
  (number? @last-watch))

(defn last-watched
  []
  (if (enabled?) @last-watch 0))

(defn recent?
  [file]
  (> (.lastModified file) (last-watched)))

(defn source-ns->test-ns
  [source-ns]
  (symbol (str source-ns "-test")))

(defn find-recent-namespaces-in-dir
  [dir]
  (let [original-csf? clojure-source-file?]
    (binding [clojure-source-file? (fn [f] (and (recent? f)
                                                (original-csf? f)))]
      (find-namespaces-in-dir (as-file dir)))))

(defn try-ns
  [ns]
  (try
   (require ns)
   ns
   (catch java.io.FileNotFoundException _ nil)))

(defn test-namespaces-for-changed-source-namespaces
  []
  (for [src-ns (find-recent-namespaces-in-dir (config/src-dir))
        :let [test-ns (source-ns->test-ns src-ns)]
        :when (try-ns test-ns)] test-ns))

(defn changed-test-namespaces
  []
  (find-recent-namespaces-in-dir (config/test-dir)))

(defn re-test
  "Reload the namespaces and run the tests again. Uses
   :reload flag, not :reload-all to avoid odd loops. If
   that doesn't work for you, explicitly reload things."
  ([] (re-test (test-namespaces)))
  ([namespaces]
     (with-re-defn
       (doseq [n namespaces]
         (require :reload n)))
     (run-tests (tests namespaces))))

(defn namespaces-to-test
  []
  (set (concat (test-namespaces-for-changed-source-namespaces)
               (changed-test-namespaces))))

(defn agent-watch-fn
  [state]
  (if (number? state)
    (do
      (Thread/sleep 500)
      (let [tests-begun (System/currentTimeMillis)]
        (try
         (re-test (namespaces-to-test))
         (catch Exception e (.printStackTrace e)))
        (send-off *agent* agent-watch-fn)
        tests-begun))
    state))

(defn go
  []
  (send-off last-watch (constantly 0))
  (send-off last-watch agent-watch-fn))

(defn stop
  []
  (send-off last-watch (constantly :disabled)))



