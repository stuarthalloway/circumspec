(ns circumspec.for-all
  (:use [clojure.contrib.seq-utils :only (rand-elt)]))

(def *size* 100)

(defmacro defgenerator
  "Like def, but may eventually add metadata to identify generators."
  [sym & forms]
  `(def ~sym ~@forms))

(defn data-name-for-generator-coll
  "A collection-based generator foo exposes its collection through
   the name foo*."
  [generator-sym]
  (symbol (str generator-sym "*")))

(defmacro generator-coll
  "Create var sym* pointing to the collection, and var
   sym pointing to a fn that returns a random element
   from the collection."
  [sym coll]
  (let [data-name (data-name-for-generator-coll sym)]
    `(do
       (def ~data-name ~coll)
       (defn ~sym [] (rand-elt ~data-name)))))

(defn choose-from
  "Return a function that chooses from one of the generators at
   random."
  [& generators]
  (fn [] ((rand-elt generators))))

(defn list-of
  "Create a list of items taken at random from generators. List
   size is random from *size*."
  [& generators]
  (let [gen (apply choose-from generators)]
    (fn [] (take (rand-int *size*) (repeatedly gen)))))

(defn string-of
  "Return results of list-of as a string."
  [& generators]
  (let [gen (apply list-of generators)]
    (fn [] (apply str (gen)))))

(defn symbol-of
  "Return results of list-of as a symbol"
  [& generators]
  (let [gen (apply string-of generators)]
    (fn [] (symbol (gen)))))

(generator-coll famous-string [nil ""])
(generator-coll famous-whitespace " \t\n")
(generator-coll digits "1234567890")
(generator-coll ascii-upper "ABCDEFGHIJKLMNOPQRSTUVWXYZ")
(generator-coll ascii-lower "abcdefghijklmnopqrstuvwxyz")
(defgenerator ascii-alpha (choose-from ascii-upper ascii-lower))

(defn class-symbol
  "Randomly selects one of the classes in the given namespace."
  [in-namespace]
  (rand-elt (keys (ns-imports in-namespace))))

(def *generated-values* nil)

(defmacro for-all
  "body should contain tests that pass for all possible values
   in binding. Currently picks *size* at random to test."
  [bindings & body]
  (let [var-names (take-nth 2 bindings)]
  `(dotimes [_# *size*]
     (let ~bindings
       ;; (println ~(zipmap (map keyword var-names) var-names)) 
         ~@body))))


  
  

