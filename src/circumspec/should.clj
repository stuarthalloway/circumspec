(ns circumspec.should
  (:use pattern-match clojure.walk))

;; TODO: extend pattern matching to match literal symbols
;; and clean all the :when clauses
(defnp reorder-type
  [_ s b p] :when
  (and (= 'should s)
       (= 'be b))
  (if (symbol? p) :symbol-predicate :predicate)
  
  [_ s t & _]
  :when (and (= 'should s)
             (= 'throw t))
  :throw-assertion
  
  [_ s _ _]
  :when (= 'should s)
  :positive-assertion
  
  [_ s n _ _]
  :when (and (= 'not n)
             (= 'should s))
  :negative-assertion
  
  x
  :default)              

(defmulti
  reorder
  reorder-type)

(defmethod reorder :positive-assertion [[actual should f expected]]
  `(circumspec/assert (~f ~actual ~expected)))

(defmethod reorder :negative-assertion [[actual s n f expected]]
  `(circumspec/assert
    (not (~f ~actual ~expected))))

(defmethod reorder :symbol-predicate [[input s b pred]]
  `(circumspec/assert (~(symbol (str (name pred) "?")) ~input)))

(defmethod reorder :predicate [[input s b pred]]
  `(circumspec/assert (~pred ~input)))

(defmethod reorder :throw-assertion [[input s t & more]]
  `(circumspec/assert (~'throws? ~@more ~input)))

(defmethod reorder :default [form] 
  form)

(defn reorder-form
  [form]
  (prewalk #(if (sequential? %)
              (reorder %)
              %)
           form))

