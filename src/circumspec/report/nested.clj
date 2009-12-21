(ns circumspec.report.nested
  (:use circumspec.report
        [circumspec.colorize :only (colorize)]
        [circumspec.assert :only (default-fail-message default-error-message)]
        [clojure.contrib.str-utils :only (str-join)]))

(defn- indent [s] (str " " s))

(defn- descriptions-match
  [[indentation old-desc new-desc]]
  (= old-desc new-desc))

(defn- add-indentation
  [[indentation _ new-desc]]
  (str indentation new-desc))

(defn context-string
  [last-result this-result]
  (->> (map vector
        (iterate indent "")
        (concat (result-context last-result) (repeat nil))
        (concat (result-context this-result) [(colorize (result-name this-result) (result-color this-result))]))
       (drop-while descriptions-match)
       (map add-indentation)
       (str-join "\n")))

(defn status
  [result]
  (cond
   (fail? result) (failure-string (str " FAILED\n" (default-fail-message result)))
   (error? result) (error-string (str " ERROR\n" (default-error-message result)))
   :default ""))

(defn report
  [results]
  (doseq [[last-result this-result] (partition 2 1 (cons nil results))]
    (print (success-string (context-string last-result this-result)))
    (println (status this-result))))