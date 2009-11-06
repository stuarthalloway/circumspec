(ns circumspec
  (:use [clojure.contrib pprint str-utils with-ns]
        pattern-match))

(declare for-all)

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
  [for & t] :when (= 'for-all for) :for-all-expression
  x                            (throw (RuntimeException.
                                       (apply str "Invalid is form: " x))))              

(defmulti
  reorder
  typeof-is-expression)

(defmethod reorder :throw-exception-string [[input _ exc str]]
  `(should (throw? ~exc ~input)))

(defmethod reorder :throw [[input _ exc]]
  `(should (throw? ~exc ~input)))
                
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

(defmethod reorder :for-all-expression [expression]
  expression)

(def junk-words #{'should 'be})

(defn polish
  "Pronounced 'paulish'"
  [args]
  (reorder (remove junk-words args)))

(defn into-delimited [desc]
  (symbol (re-sub #"\s+" "-" desc)))

(defn rewrite-describe [[name & rest]]
  (cons
   (if (= name 'describe)
     'describe-inner
     name) rest))

(defmacro describe [desc & its]
  `(describe-outer
    ~desc
    ~@(map rewrite-describe its)))

(defn describe-outer [desc & its]
  (swap! registered-descriptions conj [:describe desc its]))

(defn describe-inner [desc & its]
  [:describe desc its])

(defmacro it [desc & forms]
  `[:example ~desc '(do ~@(map polish forms))])

(defn print-spaces [n]
  (print (apply str (repeat n "  "))))

(defmulti run-test (fn [[type] _ _] type))

(defmethod run-test :example [[ignore testdesc code] _ report]
  (print (str "- " testdesc))
  (try
   (do
     (eval
      (do
        (in-ns 'circumspec)
        code))
     (println)
     (assoc report :examples (inc (:examples report))))   
   (catch Exception failure
     (println " (FAILED)")
     (assoc report
       :examples (inc (:examples report))
       :failures (inc (:failures report))
       :failure-descriptions (conj (:failure-descriptions report) failure))
     )))

(defmethod run-test :describe [[ignore desc tests] name-so-far report]
  (println)
  (println (str name-so-far desc))
  (reduce
   (fn [report test]
     (run-test test (str name-so-far desc " ") report))
   report
   tests))

(def empty-report {:examples 0
                   :failures 0
                   :errors 0
                   :failure-descriptions []
                   :error-descriptions []})

(defn output-report [report]
  (println)
  (println
   (str
    (report :examples) " examples, "
    (report :failures) " failures, "
    (report :errors)   " errors")))

(defn run-tests []
  (let [result
        (reduce
         (fn [report describe]
           (run-test describe "" report))
         empty-report
         @registered-descriptions)]
    (output-report result)
    (= (+ (result :failures) (result :errors)) 0)))

(defmacro throw? [exception form]
  `(try
    (do ~form
        false)
    (catch ~exception ignored#
      true
      )))

(defmacro should [assertion]
  `(let [res# ~assertion]
     (if (not res#)
       (throw (circumspec.ExpectationException. (str '~assertion))))))

(defmacro for-all [names code cmp other & table]
  `(do
     ~@(map
        (fn [args]
          `(should
            (let [~@(interleave names args)] (~cmp ~code ~other))))
        (partition (count names) table))))
