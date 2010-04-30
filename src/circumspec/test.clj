(ns circumspec.test
  (:use [clojure.contrib.def :only (defalias)]
        [clojure.contrib.str-utils :only (re-gsub)]
        [circumspec.should :only (should)]
        [circumspec.context :only (test-function-metadata)]
        circumspec.utils))

(defn make-test-name
  "Make a legal test name: Convert whitespace, /, . to dashes. Append
   -test if collides with name of the thing itself"
  [s]
  (let [basename (re-gsub #"\s+|/|\." "-" s)]
    (if (and (resolve (symbol basename))
             (not (.endsWith basename "-test")))
      (str basename "-test")
      basename)))

; TODO: does not work with macro names or ns prefixes
(defn test-function-name
  "Create a test function name. If the provided desc is a var,
   append suffix to prevent name collision between var and test.
   If desc is a human friendly string, dasherize it."
  [desc]
  (symbol (if (symbol? desc)
            (str  (denamespace (str desc)) "-test")
            (make-test-name (str desc)))))

(defn =>-assertion?
  [form]
  (and (sequential? form)
       (> (count form) 2)
       (= '=> (last (butlast form)))))

(defn rewrite-=>
  [fn form]
  (let [c (count form)]
    `(should
         (= (~fn ~@(apply vector (take (- c 2) form)))
            ~(last form)))))

(defmacro testing-fn
  [fn-name & forms]
  `(defn! ~(with-meta (test-function-name fn-name) (test-function-metadata (resolve fn-name) forms))
     "Generated test from the testing-fn macro."
     []
     ~@(map #(rewrite-=> fn-name %) forms)))

(defmacro it
  "Create a test function named after desc, recording
   the context in metadata"
  [desc & forms]
  `(defn! ~(with-meta (test-function-name desc) (test-function-metadata desc forms))
     "Generated test from the it macro."
     []
     ~@forms))

(defalias testing it)
;; defalias crushes :meta, so
(alter-meta! #'testing assoc :macro true)

(defn test?
  "Does var refer to a test?"
  [var]
  (should (var? var))
  (boolean (:circumspec/test (meta var))))

(defn test-name
  "Name of a test"
  [var]
  (should (var? var))
  (:circumspec/name (meta var)))

(defn pending?
  "Is test pending?"
  [var]
  (should (var? var))
  (boolean (:circumspec/pending (meta var))))

(defn test-description
  "Description of a test (:context and :name)"
  [var]
  (should (var? var))
  {:context (:circumspec/context (meta var))
   :name (:circumspec/name (meta var))})

