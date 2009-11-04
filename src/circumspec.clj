(ns circumspec
  (:use clojure.test
        clojure.contrib.pprint
        clojure.contrib.str-utils))

(defmacro wtf [form]
  `(pprint (macroexpand-1 '~form)))

;; this whole thing will be rewritten
(defmulti
  reorder
  (fn [args]
    (cond
     (= 2 (count args)) (if (symbol? (second args)) :symbol :predicate)
     (= 3 (count args)) (if (= 'throw (second args)) :throw :positive-assertion)
     (and (= 4 (count args)) (= 'not (second args))) :negative-assertion
     :default (throw (RuntimeException. "Rats! You sank my battleship")))))

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

