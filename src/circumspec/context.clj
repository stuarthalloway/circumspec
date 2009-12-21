(ns circumspec.context
  (:use [clojure.contrib.def :only (defvar)]
        [clojure.contrib.str-utils :only (re-gsub)]
        [clojure.contrib.seq-utils :only (flatten)]
        [circumspec.should :only (reorder-form)]))

(defvar *context* ()
  "Stack of contexts")

(defn context-form?
  [f]
  (boolean (and (sequential? f)
                (#{'describe 'it} (first f)))))

(defn expand-body-forms
  [desc forms]
  (when (seq forms)
    `(it ~desc ~@forms)))

(defn expand-subcontext-forms
  [desc forms]
  (when (seq forms)
    `(binding [*context* (conj *context* '~desc)]
       ~@forms)))

(defn dasherize [s]
  (re-gsub #"\s+" "-" s))

(defn test-function-name
  "Create a test function name. If the provided desc is a var,
   append suffix to prevent name collision between var and test.
   If desc is a human friendly string, dasherize it."
  [desc]
  (symbol (if (var? desc)
            (str (.sym desc) "-test")
            (dasherize (str desc)))))

(defmacro describe
  "Execute forms with desc pushed onto the spec context."
  [desc & forms]
  (let [desc (if (symbol? desc) (resolve desc) desc)
        body-forms (remove context-form? forms)
        context-forms (filter context-form? forms )]
    `(do
       ~(expand-body-forms desc body-forms)
       ~(expand-subcontext-forms desc context-forms))))

;; TODO: if defn collides with an existing symbol, stop for name collision
(defmacro it
  "Create a test function named after desc, recording
   the context in metadata"
  [desc & forms]
  `(defn ~(with-meta (test-function-name desc) `{:circumspec/spec true
                                                 :circumspec/name ~desc
                                                 :circumspec/context *context*})
     "Generated test from the it macro."
     []
     ~@(map reorder-form forms)))

(defn spec?
  "Does var refer to a spec?"
  [var]
  (assert (var? var))
  (boolean (:circumspec/spec (meta var))))

(defn spec-name
  "Name of a spec"
  [var]
  (assert (var? var))
  (:circumspec/name (meta var)))

(defn spec-description
  "Description of a spec (:context and :name)"
  [var]
  (assert (var? var))
  {:context (:circumspec/context (meta var))
   :name (:circumspec/name (meta var))})

(defn ns-vars
  [ns]
  (map second (ns-publics ns)))


(defmulti spec-vars #(if (sequential? %) :namespaces :namespace))

(defmethod spec-vars :namespaces
  [namespaces]
  (flatten
   (map spec-vars namespaces)))

(defmethod spec-vars :namespace
  [ns]
  (require ns)
  (filter spec? (ns-vars ns)))




