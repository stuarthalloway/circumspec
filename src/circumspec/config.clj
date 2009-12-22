(ns circumspec.config)

;; TODO: enumerate the legal property names and complain on startup?
;;       generalize to utils?
(defmacro defconfig
  "Define a Java config property of the form -Dcircumspec.name=...
   Constructor, if specified, converts and validates from string
   to the type needed."
  ([name docs default-value]
     `(defconfig ~name ~docs ~default-value identity))
  ([name docs default-value constructor]
     (assert (symbol? name))
     (assert (string? docs))
     (assert (string? default-value))
     `(defn ~name
        ~(str docs "\nDefault value is " default-value
              "\nSpecify using -Dcircumspec." name "=...")
        []
        (~constructor
         (System/getProperty ~(str "circumspec." name) ~default-value)))))

(defconfig colorize
  "Should console output be colorized."
  "true"
  #(not (#{"no" "false"} %)))

(defconfig report-function
  "Choose a report function. To use a different report function, find 
   or create a circumspec.report.xxx namespsace with a report function"
  "nested"
  #(let [report-namespace (symbol (str "circumspec.report." %))]
     (require report-namespace)
     (ns-resolve report-namespace 'report)))

(defconfig test-regex
  "Regex to match test namespaces."
  "-test$"
  #(java.util.regex.Pattern/compile %))

(defconfig test-dir
  "Dir to search for tests"
  "test")
