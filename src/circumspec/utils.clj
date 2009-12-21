(ns circumspec.utils
  (:use clojure.contrib.pprint))

(defn pop-optional-args
  "Pops args from coll as/if they match preds. Used for binding forms
   that have optional arguments at the beginning."
  [preds coll]
  (if (seq preds)
    (if ((first preds) (first coll))
      (cons (first coll) (pop-optional-args (rest preds) (rest coll)))
      (cons nil (pop-optional-args (rest preds) coll)))
    (list coll)))

(defn class-symbol?
  [s]
  (class?
   (try
    (resolve s)
    (catch ClassNotFoundException e nil))))

(defmacro wtf
  "'What the form' is going on? Convenience for macroexpand."
  [form]
  `(pprint (macroexpand-1 '~form)))

(defn pps
  "Pretty print into a string"
  [x]
  (with-out-str (pprint x)))

