(ns circumspec.context
  (:use [clojure.contrib.def :only (defvar defalias)]
        [clojure.contrib.str-utils :only (re-gsub)]
        [clojure.contrib.seq-utils :only (flatten)]
        [circumspec.should :only (should)]
        [circumspec.utils :only (resolve! defn!)]))

(defvar *context* []
  "Active contexts")

(defn expand-subcontext-forms
  [desc forms]
  (when (seq forms)
    `(binding [*context* (conj *context* '~desc)]
       ~@forms)))

(defn dasherize [s]
  (re-gsub #"\s+" "-" s))

(defn denamespace [s]
  (re-gsub #".*/" "" s))

; TODO: does not work with macro names or ns prefixes
(defn test-function-name
  "Create a test function name. If the provided desc is a var,
   append suffix to prevent name collision between var and test.
   If desc is a human friendly string, dasherize it."
  [desc]
  (symbol (if (symbol desc)
            (str  (denamespace (str desc)) "-test")
            (dasherize (str desc)))))

(defn test-function-metadata
  [desc forms]
  (merge {:circumspec/spec true
          :circumspec/name desc
          :circumspec/context 'circumspec.context/*context*}
         (if (empty? forms)
           {:circumspec/pending true}
           {})))

(defn =>-assertion?
  [form]
  (and (sequential? form)
       (> (count form) 2)
       (= '=> (last (butlast form)))))

(defn rewrite-=>
  [fn form]
  (let [c (count form)]
    `(should
         (= (apply ~fn ~(apply vector (take (- c 2) form)))
            ~(last form)))))

(defmacro describe-function
  [fn-name & forms]
  `(defn! ~(with-meta (test-function-name fn-name) (test-function-metadata (resolve fn-name) forms))
     "Generated test from the describe-function macro."
     []
     ~@(map #(rewrite-=> fn-name %) forms)))

(declare describe)

;; TODO: namespace resolution
(defn calls-describe?
  [form]
  (letfn [(f [form]
             (cond
              (and (list? form) (= 'describe (first form))) true
              (sequential? form) (some f (rest form))
              :default false))]
    (boolean (f form))))

(defmacro describe
  "Execute forms with desc pushed onto the spec context."
  [desc & forms]
  (let [desc (if (symbol? desc) (resolve! desc) desc)]
    `(do
       ~(expand-subcontext-forms desc forms))))

(defmacro it
  "Create a test function named after desc, recording
   the context in metadata"
  [desc & forms]
  `(defn! ~(with-meta (test-function-name desc) (test-function-metadata desc forms))
     "Generated test from the it macro."
     []
     ~@forms))

(defalias testing it)

(defn spec?
  "Does var refer to a spec?"
  [var]
  (should (var? var))
  (boolean (:circumspec/spec (meta var))))

(defn spec-name
  "Name of a spec"
  [var]
  (should (var? var))
  (:circumspec/name (meta var)))

(defn pending?
  "Is spec pending?"
  [var]
  (should (var? var))
  (boolean (:circumspec/pending (meta var))))

(defn spec-description
  "Description of a spec (:context and :name)"
  [var]
  (should (var? var))
  {:context (:circumspec/context (meta var))
   :name (:circumspec/name (meta var))})

(defn ns-vars
  [ns]
  (map second (ns-publics ns)))


(defn spec-var
  [ns]
  (require ns)
  (filter spec? (ns-vars ns)))

(defn spec-vars
  [namespaces]
  (flatten
   (map spec-var namespaces)))





