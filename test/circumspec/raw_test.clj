(ns circumspec.raw-test
  (:use circumspec circumspec.raw))

;; TODO: better filename convention
(describe dump-file
  (it "creates a file in .circumspec/raw"
    (let [file (dump-file)]
      (should (instance? java.io.File file))
      (should (re-find #"\.circumspec/raw/.*" (.toString file))))))

(describe dump-results
  (it "writes complete restuls to a file"
    (let [file (dump-results (take 2 (repeat {:sample true})))]
      (should (= "{:sample true}\n{:sample true}\n" (slurp (.toString file))))
      (should (.delete file) "deleting sample result data"))))