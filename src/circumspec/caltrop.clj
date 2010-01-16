(ns circumspec.caltrop
  (:require [clojure.contrib.str-utils2 :as s]
            [circumspec.config :as config])
  (:use circumspec
        clojure.set
        [clojure.contrib.def :only (defalias)]
        [circumspec.context :only (*context*)]
        [clojure.contrib.duck-streams :only (spit make-parents)]
        [circumspec.should :only (fail)]))

(defn caltrop-name
  [form]
  (str (gensym (str (with-out-str (pr form)) "_"))))

(defn regression-file
  ([] (regression-file *ns*))
  ([ns] (-> "caltrops/"
            (str ns)
            (s/replace "." "/")
            (s/replace "-" "_"))))

(defn regression-data-for
  [ns]
  (try
   (read-string (slurp (regression-file ns)))
   (catch java.io.FileNotFoundException _ {})))

(defn caltrop-test
  [form]
  `(testing ~(caltrop-name form)
     (let [ns-regress# (regression-data-for ~*ns*)]
       (if-let [context-regress# (ns-regress#  *context*)]
         (if (contains? context-regress# '~form)
           (should (= ~form (context-regress# '~form)))
           (fail {:expected (str "Regression data for " '~form) :actual "None found"}))
         (fail {:expected (str "Regression data for " '~form) :actual "None found"})))))

(def caltrop-baseline
  (ref {}))

(defn save-baseline!
  []
  (doseq [[k v] @caltrop-baseline]
    (doto (java.io.File. k)
      (make-parents)
      (spit v))))

(defn caltrop-save-baseline*
  [form]
  `(it ~(caltrop-name form)
     (dosync
      (alter caltrop-baseline assoc-in [~(regression-file) *context* '~form] ~form))))

(defmacro caltrops-save-baseline
  "Capture caltrop data to write"
  [& forms]
  `(do
     ~@(map caltrop-save-baseline* forms)))

(defmacro caltrops-test
  "Run the forms and compare the results with those in file filename.
   Fail if any are different"
  [& forms]
  `(do
     ~@(map caltrop-test forms)))

(if (config/write-caltrops)
  (do
    (defalias caltrops caltrops-save-baseline))
  (do
    (defalias caltrops caltrops-test)))



