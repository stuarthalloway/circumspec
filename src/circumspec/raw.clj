(ns circumspec.raw
  (:import java.io.File)
  (:use [clojure.contrib.duck-streams :only (with-out-writer make-parents)]))

(defn dump-file
  "Create dump file name for a test run"
  []
  (File. ".circumspec/raw/" (.toString (java.util.Date.))))

(defn dump-results
  "Spit results into a file, return java.io.File instance."
  [results]
  (let [file (dump-file)]
    (make-parents file)
    (with-out-writer file
      (doseq [result results]
        (prn result)))
    file))