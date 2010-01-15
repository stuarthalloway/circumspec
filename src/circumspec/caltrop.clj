(ns circumspec.caltrop
  (:require [clojure.contrib.str-utils2 :as s])
  (:use circumspec
        clojure.set
        [clojure.contrib.duck-streams :only (spit make-parents)]
        [circumspec.should :only (fail)]
        [circumspec.context :only (*context*)]))

(defn caltrop-name
  [form]
  (with-out-str (pr form)))

(defn regression-file
  ([] (regression-file *ns*))
  ([ns] (java.io.File. (-> "caltrops/"
                           (str ns)
                           (s/replace "." "/")
                           (s/replace "-" "_")))))

(defn regression-data-for-current-testfile
  []
  (try
   (read-string (slurp (.getPath (regression-file))))
   (catch java.io.FileNotFoundException _ {})))

(defn regression-data-for-current-test
  []
  (get (regression-data-for-current-testfile) *context*))

(defn caltrop-match
  [expected]
  (fn [form]
    `(testing ~(caltrop-name form)
       (should (= ~form ~(expected form))))))

(defn caltrop-pending
  [form]
  `(testing ~(caltrop-name form)))

(defn caltrop-missing
  [form]
  `(testing ~(caltrop-name form)
     (fail {:expected '~form :actual "Regression data has not matching test"})))

(def caltrop-baseline
  (ref {}))

(defn save-baseline!
  []
  (for [[k v] @caltrop-baseline]
    (doto (regression-file k)
      (make-parents)
      (spit v))))

(defn caltrop-write-baseline
  [form]
  `(testing ~(caltrop-name form)
     (dosync
      (alter caltrop-baseline assoc-in [~(regression-filename) ~*context* '~form] ~form))))

(defmacro caltrops-write
  "Capture caltrop data to write"
  [& forms]
  `(do
     ~@(map caltrop-write-baseline forms)))

(defmacro caltrops-test
  "Run the forms and compare the results with those in file filename.
   Fail if any are different"
    [& forms]
    (let [baseline (regression-data-for-current-test)
          expected (set (keys baseline))
          provided (set forms)
          missing (difference expected provided)
          pending (difference provided expected)
          matching (intersection expected provided)]
      `(do
         ~@(map (caltrop-match baseline) matching)
         ~@(map caltrop-pending pending)
         ~@(map caltrop-missing missing))))



