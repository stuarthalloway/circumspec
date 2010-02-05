(ns circumspec.for-all
  (:use [clojure.contrib.seq-utils :only (rand-elt)]))

(def *size* 100)

(defmacro defgenerator [sym & forms]
  `(def ~sym ~@forms))

(defn data-name-for-generator-coll
  [generator-sym]
  (symbol (str generator-sym "*")))

(defmacro generator-coll
  [sym coll]
  (let [data-name (data-name-for-generator-coll sym)]
    `(do
       (def ~data-name ~coll)
       (defn ~sym [] (rand-elt ~data-name)))))

(defn choose-from
  [& generators]
  (fn [] ((rand-elt generators))))

(defn list-of
  [& generators]
  (let [gen (apply choose-from generators)]
    (fn [] (take (rand-int *size*) (repeatedly gen)))))

(defn string-of
  [& generators]
  (let [gen (apply list-of generators)]
    (fn [] (apply str (gen)))))

(defn symbol-of
  [& generators]
  (let [gen (apply string-of generators)]
    (fn [] (symbol (gen)))))

(generator-coll famous-string [nil ""])
(generator-coll famous-whitespace " \t\n")
(generator-coll digits "1234567890")
(generator-coll ascii-upper "ABCDEFGHIJKLMNOPQRSTUVWXYZ")
(generator-coll ascii-lower "abcdefghijklmnopqrstuvwxyz")
(defgenerator ascii-alpha (choose-from ascii-upper ascii-lower))

(defmacro for-all
  [bindings & body]
  `(dotimes [_# *size*]
     (let ~bindings
       ~@body)))

