(ns circumspec.watch
  (:import java.util.Date)
  (:require [circumspec.config :as config])
  (:use clojure.contrib.find-namespaces
        [circumspec.utils :only (ns-wipe)]
        [circumspec.runner :only (run-tests)]
        [circumspec.locator :only (tests test-namespaces)]
        [circumspec.report :only (pending-string)]
        [clojure.contrib.def :only (defvar)]
        [clojure.contrib.str-utils :only (re-sub)]
        [clojure.contrib.java-utils :only (as-file)]))
  
(defvar last-watched-atom
  (atom {})
  "Map of files to the last time they were watched.
   Nil if disabled.")

(defn source-ns->test-ns
  [source-ns]
  (symbol (str source-ns "-test")))

(defn test-ns->source-ns
  [test-ns]
  (symbol (re-sub #"-test$" "" (str test-ns))))

(defn test-namespace
  [file]
  (let [file-ns (second (read-file-ns-decl (as-file file)))]
    (if (.endsWith (str file-ns) "-test")
      file-ns
      (source-ns->test-ns file-ns))))

(defn last-watched
  "Last time a file's test-namespace was watched."
  [file]
  (get @last-watched-atom (test-namespace file) 0))

(defn mark-watched
  "Mark test namespaces as watched as of timestamp."
  [timestamp test-namespaces]
  (swap! last-watched-atom merge (zipmap test-namespaces (repeat timestamp))))

(defvar sleep-time 500
  "How long to sleep before waking and looking for
   changed code.")

(defn needs-test?
  "Is the file newer than the last-watched time for
   its test namespace?"
  [file]
  (boolean
   (when (.isFile file)
     (> (.lastModified file) (last-watched file)))))

(defn find-recent-namespaces-in-dir
  [dir]
  (let [original-csf? clojure-source-file?]
    (binding [clojure-source-file? (fn [f] (and (needs-test? f)
                                                (original-csf? f)))]
      (doall (find-namespaces-in-dir (as-file dir))))))

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
     (doseq [n namespaces]
       (try
        (require :reload (test-ns->source-ns n))
        (catch
            java.io.FileNotFoundException fnfe
          (println (pending-string (.getMessage fnfe)))))
       (ns-wipe n)
       (require :reload n))
     (apply run-tests (tests namespaces))
     nil))

(defn namespaces-to-test
  "Namespaces to test. Note that the watcher cannot watch
   itself."
  []
  (disj (set (concat (test-namespaces-for-changed-source-namespaces)
                     (changed-test-namespaces)))
        'circumspec.watch-test))

(defn run-watcher
  []
  (try
   (let [tests-begun (System/currentTimeMillis)
         namespaces (namespaces-to-test)]
     (when (seq namespaces)
       (println "\n\nWatcher awakened" (Date. tests-begun))
       (mark-watched tests-begun namespaces)
       (re-test namespaces)))
   (catch Exception e (.printStackTrace e))))

(def watch-agent
  (agent false))

(defn enabled?
  []
  @watch-agent)

(defn agent-watch-fn
  [enabled]
  (when enabled
    (Thread/sleep sleep-time)
    (run-watcher)
    (send-off watch-agent agent-watch-fn))
  enabled)

(defn watch
  "Run all tests, then watch directories for file changes. When
   something changes, run tests again. Call again if you want
   the whole suite."
  []
  (reset! last-watched-atom {})
  (send-off watch-agent (constantly true))
  (send-off watch-agent agent-watch-fn))

(defn stop
  "Stop watching.n"
  []
  (send-off watch-agent (constantly false)))



