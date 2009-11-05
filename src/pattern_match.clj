;;
;; Pattern matching similar to ML / Haskell.
;;
;; Copyright (c) 2009 by Tim Lopez. Licensed under Eclipse Public License 1.0.
;;
;; Some examples:
;;
;; signum function: 
;;     (match x 
;;       0 "zero"
;;       n :when (< n 0) "negative"
;;       _ "positive")
;;
;; count identical elements in the same location in two lists:
;;     (defn count= [ lst1 lst2 ]
;;       (loop [ a lst1 b lst2 count 0 ]
;;         (match [a b]
;;            [[e & at] [e & bt]]  (recur at bt (inc count)) 
;;            [[_ & at] [_ & bt]]  (recur at bt count) 
;;            _                    count)))
;;
;; _      -- matches anything
;; [ a ]  -- match one element list only
;; [ a b ] -- match two elements only
;; [ a a ] -- matches two element list, both elements the same
;; [ 0 a ] -- matches two element list, first element is "0"
;; [ _ a _ ] -- three elements, don't care about first and third
;; [ [ a b] [ a b] ] -- matches 2 x two element identical lists
;; [ h & t ] -- matches lists of length 1 and above
;;
 
(ns
    #^{:author "Tim Lopez"
       :doc "Pattern matching per Haskell/ML"}
  pattern-match
  (:use [clojure.contrib.pprint.utilities :only (map-passing-context)]))

;; build-match*, given an lvalue for a destructured bind, build an
;; equivalent lvalue with a set of checks such that the match can be
;; verified. Example:
;;
;;      pattern: [ [ a b ] a ]
;;      turns into:  [ [ a b & g1 ] g2 ] 
;;      ... with checks to make sure g1 is nil and g2 == a
;;

(defn- build-match*
  [k 
   { :keys [form bindings equal-checks not-nil is-nil] :as context 
    :or { bindings #{} equal-checks #{} not-nil #{} is-nil #{} } }]
  (let [g (gensym)]
  (cond
   (coll? k) (let [has-body (some #(= % '&) k)
                   k* (if (not has-body) (conj k '& 'nil) k)
                   [nf ctx] (map-passing-context build-match* context k*)]
               ; if an explicit & body form was passed in, remove
               ; non-nil check on it if one didn't already exist
               [nf (if (and has-body (not ((context :not-nil #{}) (last nf))))
                     (assoc ctx :not-nil (disj (ctx :not-nil) (last nf)))
                     ctx)])
   (= k '_) [g (assoc context :not-nil (conj not-nil g))]
   (= k 'nil) [g (assoc context :is-nil (conj is-nil g))]
   (= k '&) ['& context]
   (or (string? k) (number? k)) [g (assoc context :equal-checks (conj equal-checks `(= ~g ~k)))]
   (symbol? k) (if (bindings k)
                 [g (assoc context :equal-checks (conj equal-checks `(= ~g ~k)))]
                 [k (assoc context :not-nil (conj not-nil k) :bindings (conj bindings k))])
)))

;; given the variable and a set of match cases, build the macro.
(defn- build-match [v body]
  (if (< (count body) 2) nil
      (let [has-when (= (nth body 1) :when)
            condition (take (if has-when 4 2) body)
            rest-of-body (drop (if has-when 4 2) body)
            params (nth condition 0)
            [nf ctx] (build-match* params {})]
        (let [action (drop (if has-when 3 1) condition)
              when-check (if has-when (list (nth condition 2)))
              not-nil (map (fn [v] `(not (nil? ~v))) (ctx :not-nil))
              is-nil (map (fn [v] `(nil? ~v)) (ctx :is-nil))
              equal-checks (ctx :equal-checks)]
          `(let ~(vector nf v) 
             (if (and ~@not-nil ~@is-nil ~@equal-checks ~@when-check)
               ~@action
               ~(build-match v rest-of-body)))
          ))))

(defmacro match 
  "This performs simple pattern matching a la ML / Haskell.  The base
  syntax is (match value pattern action pattern2 action2 ...).
  The patterns follow the typical destructured bind syntax, but there
  are checks added to make the pattern matching stricter; for example,
  a pattern of [a b] will only match against a two element vector. The
  same symbol can be used multiple times to indicate equality, so [a
  a] means 'a vector of two elements, both the same.'
  The pattern :when condition action syntax can be used to specify a
  conditional that is checked before the pattern is invoked."  
  [v & body]
  (build-match v body))

(defmacro defnp
  "This defines a function taking a single value that goes through an
  implicit case statement.  For example: (defnp signum 0
  0 n :when (< n 0) -1 _ 1)"
  [fn-name & patterns]
  `(defn ~fn-name [x#] (match x# ~@patterns)))
