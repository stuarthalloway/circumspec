(ns circumspec
  (:use clojure.test
        clojure.contrib.pprint
        clojure.contrib.str-utils
        pattern-match))

(defmacro wtf [form]
  `(pprint (macroexpand-1 '~form)))

(defnp typeof-is-expression
  [_ s] :when (symbol? s)      :symbol
  [_ _]                        :predicate
  [_ t _] :when (= 'throw t)   :throw
  [_ t _ _] :when (= 'throw t) :throw-exception-string
  [_ _ _]                      :positive-assertion
  [_ n _ _] :when (= 'not n)   :negative-assertion
  x                            (throw (RuntimeException.
                                      (apply str "Invalid is form" x))))              

(defmulti
  reorder
  typeof-is-expression)

(defmethod reorder :throw-exception-string [[input _ exc str]]
  `(is (~(symbol "thrown?") ~exc ~input)))

(defmethod reorder :throw [[input _ exc]]
  `(is (~(symbol "thrown?") ~exc ~input)))
                
(defmethod reorder :symbol [[input sym]]
  `(is (~(symbol (str (name sym) "?")) ~input)))

(defmethod reorder :predicate [[input predicate]]
  `(is (~predicate ~input)))

(defmethod reorder :positive-assertion [[actual f expected]]
  `(is
    (~f ~actual ~expected)))

(defmethod reorder :negative-assertion [[actual skipnot f expected]]
  `(is
    (not (~f ~actual ~expected))))

(def junk-words #{'should 'be})

(defn polish
  "Pronounced 'paulish'"
  [args]
  (reorder (remove junk-words args)))

(defn into-delimited [desc]
  (symbol (re-sub #"\s+" "-" desc)))

(defmacro describe [desc & its]
  `(deftest ~(into-delimited (str desc)) ~@its))

(defmacro it [desc & forms]
  `(do
     (testing ~desc
              ~@(map polish forms))))

