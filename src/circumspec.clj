(ns circumspec
  (:use clojure.contrib.pprint
        clojure.contrib.str-utils
        pattern-match))

(def registered-descriptions (atom []))

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
  `(should (~(symbol "throw?") ~exc ~input)))
                
(defmethod reorder :symbol [[input sym]]
  `(should (~(symbol (str (name sym) "?")) ~input)))

(defmethod reorder :predicate [[input predicate]]
  `(should (~predicate ~input)))

(defmethod reorder :positive-assertion [[actual f expected]]
  `(should
    (~f ~actual ~expected)))

(defmethod reorder :negative-assertion [[actual skipnot f expected]]
  `(should
    (not (~f ~actual ~expected))))

(def junk-words #{'should 'be})

(defn polish
  "Pronounced 'paulish'"
  [args]
  (reorder (remove junk-words args)))

(defn into-delimited [desc]
  (symbol (re-sub #"\s+" "-" desc)))

(defn describe [desc & its]
  (swap! registered-descriptions conj [desc its]))

(defmacro it [desc & forms]
  `[~desc '(do ~@(map polish forms))])

(defn run-tests []
  (doseq [[desc tests] @registered-descriptions]
    (println desc)
    (doseq [[testdesc code] tests]
      (println (str "- " testdesc))
      (eval code))))

(defmacro throw? [exception form]
  `(try
    (do ~form
        false
        )
    (catch ~exception ignored#
      true
      )))

(defmacro should [assertion]
  `(let [res# ~assertion]
     (if (not res#)
       (do (print "FAILURE FOR ")
           (pprint '~assertion)) 

       )
     )
  )
