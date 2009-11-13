(ns circumspec
  (:use [clojure.contrib pprint str-utils with-ns]
        pattern-match)
  (:import circumspec.ExpectationException
           java.util.regex.Pattern))

(def registered-descriptions (atom []))
(def assertions (atom 0))

(defmacro wtf
  "'What the form' is going on? Convenience for macroexpand."
  [form]
  `(pprint (macroexpand-1 '~form)))

(defnp typeof-is-expression
  [_ s] :when (symbol? s)      :symbol
  [_ _]                        :predicate
  [_ t _] :when (= 'throw t)   :throw
  [_ t _ _] :when (= 'throw t) :throw-with-matcher
  [_ _ _]                      :positive-assertion
  [_ n _ _] :when (= 'not n)   :negative-assertion
  [for & t] :when (= 'for-these for) :for-these-expression
  x                            (throw (RuntimeException.
                                       (apply str "Invalid is form: " x))))              

(defmulti
  reorder
  typeof-is-expression)

(defmethod reorder :throw-with-matcher [[input _ exc str]]
  `(should-throw? ~exc ~str ~input))

(defmethod reorder :throw [[input _ exc]]
  `(should-throw? ~exc ~input))
                
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

(defmethod reorder :for-these-expression [expression]
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
  `[:example (.name *ns*) ~desc '(do ~@(map polish forms))])

(defn print-spaces [n]
  (print (apply str (repeat n "  "))))

(defmulti run-test (fn [[type] _ _] type))

(defn print-throwable [throwable]
  (println "  " (.getMessage throwable))
  (comment (doseq [e (.getStackTrace throwable)]
             (println "  " (.toString e)))))

(defmethod run-test :example [[ignore ns-sym testdesc code] _ report]
  (print (str "- " testdesc))
  (try
   (do
     (eval
      (do
        (in-ns ns-sym)
        code))
     (println)
     (assoc report :examples (inc (:examples report))))
   (catch Throwable failure
;     (if (instance? failure ExpectationException)
       (do
         (println " (FAILED)")
         (print-throwable failure)
         (assoc report
           :examples (inc (:examples report))
           :failures (inc (:failures report))
           :failure-descriptions (conj (:failure-descriptions report) failure)))
;       (do
;         (println " (ERROR)")
;         (assoc report
;           :examples (inc (:examples report))
;           :errors (inc (:errors report))
;           :error-descriptions (conj (:error-descriptions report) failure)))
;       )
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
    @assertions        " assertions, "
    (report :examples) " examples, "
    (report :failures) " failures, "
    (report :errors)   " errors")))

(defn run-tests
  "Run tests, returning true if all pass."
  []
  (reset! circumspec/assertions 0)
  (let [result
        (reduce
         (fn [report describe]
           (run-test describe "" report))
         empty-report
         @registered-descriptions)]
    (output-report result)
    (= (+ (result :failures) (result :errors)) 0)))

(defmulti exception-should-match? (fn [matcher _ _] (class matcher)))

(defmethod exception-should-match? nil [_ _ _] true)

(defmethod exception-should-match? String [expected throwable form]
  (let [actual (.getMessage throwable)]
    (when-not (= expected actual)
      (throw (ExpectationException.
              (str "Expected "
                   form
                   " to throw exception message '"
                   expected
                   "', got '"
                   actual
                   "'"))))))

(defmethod exception-should-match? Pattern [expected throwable form]
  (let [actual (.getMessage throwable)]
    (when-not (re-find expected actual)
      (throw (ExpectationException.
              (str "Expected "
                   form
                   " to throw exception message /"
                   expected
                   "/, got '"
                   actual
                   "'"))))))

(defmacro should-throw?
  "Check to see whether form throws type exception. Possibly also check
   the exception itself with matcher, which can be a string or regexp to
   match against the exception message. Use strings for exact match,
   regexps for fuzzy or partial matches."
  ([ex-type form] `(should-throw? ~ex-type nil ~form))
  ([ex-type matcher form]
      `(when-not (try
                  (do
                    (swap! assertions inc)
                    ~form
                    false)
                  (catch ~ex-type expected#
                    (do
                      (exception-should-match? ~matcher expected# '~form)
                      true))
                  (catch Throwable t#
                    (throw (ExpectationException. (str "Expected " '~form " to throw " ~ex-type ", threw " t#)))))
         (throw (ExpectationException. (str "Expected " '~form " to throw " ~ex-type))))))

(defmacro should [assertion]
  `(let [res# ~assertion]
     (swap! assertions inc)
     (if (not res#)
       (throw (ExpectationException. (str '~assertion))))))

(defmacro for-these [names code cmp other & table]
  `(do
     ~@(map
        (fn [args]
          `(should
            (let [~@(interleave names args)] (~cmp ~code ~other))))
        (partition (count names) table))))
