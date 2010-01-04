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

(defn context-lines
  "Return the context-lines for a result. Contexts nest, and appear before
   the name line."
  [last-result this-result]
  (->> (map vector
        (iterate inc 0)
        (concat (result-context last-result) (repeat nil))
        (concat (result-context this-result)))
       (drop-while descriptions-match)
       (map add-indentation)))

(defn name-line
  "Return the name line, appropriately indented based on the context."
  [result]
  (let [context-depth (count (:context result))]
    (indent context-depth (:name result))))

(defn story-lines
  "Story lines are within a single test. They appear after the name line,
   indented one level."
  [result]
  (let [context-depth (inc (count (:context result)))]
    (map #(indent context-depth %) (:story result))))

(defn report-lines
  "Build the report lines from context, name, and story."
  [last-result this-result]
  (concat (context-lines last-result this-result)
          [(name-line this-result)]
          (story-lines this-result)))

(defn join-lines
  [lines]
  (str-join "\n" (remove empty? lines)))

(defn result-string
  "Given the active line and a result, complete the line with the status
   message and colorize appropriately."
  [line result]
  (cond
   (fail? result) (failure-string (str line " FAILED\n" (default-fail-message result)))
   (error? result) (error-string (str line " ERROR\n" (default-error-message result)))
   (pending? result) (pending-string (str line " PENDING\n"))
   :default (success-string line))  )

(defn report-string
  "Colorize all the lines but the last in green, and the last based on
   the type of the result."
  [last-result this-result]
  (let [lines (report-lines last-result this-result)]
    (join-lines
     [(success-string (join-lines (butlast lines)))
      (result-string (last lines) this-result)])))

(defn report
  [results]
  (doseq [[last-result this-result] (partition 2 1 (cons nil results))]
    (println (report-string last-result this-result))))

