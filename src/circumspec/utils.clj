(ns circumspec.utils
  (:use clojure.contrib.pprint
        [clojure.contrib.seq-utils :only (flatten)]
        [clojure.contrib.java-utils :only (as-str)]
        [clojure.contrib.str-utils :only (re-gsub)]))

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

(defn ns-wipe
  "Unbind all symbols in a namespace"
  [ns]
  (when (find-ns ns)
    (doseq [sym (map first (ns-publics ns))]
      (ns-unmap ns sym))))

(defn pps
  "Pretty print into a string"
  [x]
  (with-out-str (pprint x)))

(defn java-props->sh-args
  "Convert map of java environment props into args usable in
   a call to shell-out."
  [props-map]
  (map
   (fn [[k v]] (str "-D" (as-str k) "=" v))
   props-map))

(defn resolve!
  "Like core/resolve, but throw if not found."
  [s]
  (or
   (resolve s)
   (throw (RuntimeException. (str "Unable to resolve " s " in " *ns*)))))

(def *allow-re-defn* false)

(defmacro with-re-defn
  [& forms]
  `(binding [*allow-re-defn* true]
     ~@forms))

(defmacro defn! 
  "Like defn, but raises an error if the name already is bound."
  [name & forms]
  `(let [v# (def ~name)]
     (if (and (not *allow-re-defn*) (.hasRoot v#))
       (throw (RuntimeException. (str "The name " '~name " is already bound in " *ns*)))
       (defn ~name ~@forms))))

(defn denamespace
  "Remove the namespace portion of a name string."
  [s]
  (re-gsub #".*/" "" s))


