(ns circumspec.raw-test
  (:refer-clojure :exclude [assert])
  (:use circumspec circumspec.raw))

;; TODO: better filename convention
(describe dump-file
  (it "creates a file in .circumspec/raw"
    (let [file (dump-file)]
      (assert (instance? java.io.File file))
      (assert (re-find #"\.circumspec/raw/.*" (.toString file))))))

(describe dump-results
  (it "writes complete restuls to a file"
    (let [file (dump-results (take 2 (repeat {:sample true})))]
      (assert (= "{:sample true}\n{:sample true}\n" (slurp (.toString file))))
      (assert (.delete file) "deleting sample result data"))))