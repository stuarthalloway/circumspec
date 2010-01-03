(ns circumspec.report.nested
  (:use circumspec.report
        [circumspec.colorize :only (colorize)]
        [circumspec.assert :only (default-fail-message default-error-message)]
        [clojure.contrib.str-utils :only (str-join)]))

(def indents
  (iterate #(str "  " %) ""))

(defn indent
  "Indent string s to level."
  [level s]
  (str (nth indents level) s))

(defn- descriptions-match
  [[indentation old-desc new-desc]]
  (= old-desc new-desc))

(defn- add-indentation
  [[level _ new-desc]]
  (indent level new-desc))

(defn context-string
  [last-result this-result]
  (->> (map vector
        (iterate inc 0)
        (concat (result-context last-result) (repeat nil))
        (concat (result-context this-result)))
       (drop-while descriptions-match)
       (map add-indentation)
       (str-join "\n")))

(defn active-line
  [result]
  (let [context-depth (count (:context result))]
    (indent context-depth (:name result))))

(defn story-string
  [result]
  (let [context-depth (count (:context result))]
    (str-join "\n" (map #(indent context-depth %) (:story result)))))

(defn result-string
  [result]
  (let [active-line (active-line result)]
    (cond
     (fail? result) (failure-string (str active-line " FAILED\n" (default-fail-message result)))
     (error? result) (error-string (str active-line " ERROR\n" (default-error-message result)))
     (pending? result) (pending-string (str active-line " PENDING\n"))
     :default (success-string active-line))))

(defn report-string
  [last-result this-result]
  (str-join
   "\n"
   (remove empty? [(success-string (context-string last-result this-result))
                   (success-string (story-string this-result))
                   (result-string this-result)])))

(defn report
  [results]
  (doseq [[last-result this-result] (partition 2 1 (cons nil results))]
    (println (report-string last-result this-result))))

