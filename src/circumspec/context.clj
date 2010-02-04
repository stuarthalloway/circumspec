(ns circumspec.context
  (:use [clojure.contrib.def :only (defvar)]
        [circumspec.utils :only (resolve! defn!)]))

(defvar *context* []
  "Active contexts")

(defn expand-subcontext-forms
  [desc forms]
  (when (seq forms)
    `(binding [*context* (conj *context* '~desc)]
      ~@forms)))

(defn test-function-metadata
  [desc forms]
  (merge {:circumspec/test true
          :circumspec/name desc
          :circumspec/context '(concat [(.name *ns*)] circumspec.context/*context*)}
         (if (empty? forms)
           {:circumspec/pending true}
           {})))

(defmacro describe
  "Execute forms with desc pushed onto the test context."
  [desc & forms]
  (let [desc (if (symbol? desc) (resolve! desc) desc)]
    `(do
       ~(expand-subcontext-forms desc forms))))







