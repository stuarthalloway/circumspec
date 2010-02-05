(ns circumspec.should
  (use [clojure.contrib.def :only (defvar)]
       [clojure.test :only (function? *testing-contexts* report)]
       [clojure.contrib.debug :only (debug-repl)]
       [circumspec.utils :only (pop-optional-args class-symbol? pps)]))

(defvar *debug* nil
  "Set to true to make should failures bounce into debug repl.")

(defmacro local-bindings
  "Produces a map of the names of local bindings to their values."
  []
  (let [symbols (map key @clojure.lang.Compiler/LOCAL_ENV)]
    (zipmap (map (fn [sym] `(quote ~sym)) symbols) symbols)))

;; TODO: refactor, default-message
(defn default-fail-message
  "Default string message for a ahould failure/warning."
  [{expected :expected
    actual :actual
    message :message
    :as options}]
  (let [message-prefix (if message (str message "\n") nil)]
    (str
     "circumspec should-assertion failed:\n"
     message-prefix
     "expected " (pps expected)
     "actual " (pps actual))))

(defn default-error-message
  [{throwable :throwable
    message :message}]
  (let [message-prefix (if message (str message "\n") nil)]
    (str
     "circumspec exception:\n"
     message-prefix
     "thrown " (with-out-str (.printStackTrace throwable (java.io.PrintWriter. *out*))))))

(defn should-repl [options]
  (println (default-fail-message options))
  (println "Starting debug repl. Type () to end repl and propagate failure.")
  (debug-repl))

(defn fail
  [options]
  (when *debug* (should-repl options))
  (throw (new circumspec.AssertFailed (:msg options) options)))

(defn message-map
  [message]
  (if message
    {:message message}
    {}))

;; TODO: intelligence for false/false case
;; TODO: chain out on first 
(defmulti should-body
  (fn [form message]
    (when (seq? form)
      (cond
       (= 'throws? (first form)) :throws
       (function? (first form)) :predicate))))

(defmethod should-body :default
  [form message]
  `(let [value# ~form]
     (if value#
       true
       (fail (merge (message-map ~message) {:expected '~form :actual value#})))))

(defmethod should-body :predicate
  [[pred & args :as form] message]
  `(let [values# (list ~@args)
         result# (apply ~pred values#)]
     (if result#
       true
       (fail (merge (message-map ~message) {:expected '~form :actual (list '~'not (cons '~pred values#))})))))

;; TODO: make private, and make describe automatically expose private vars
(defn match-fn
  [x]
  (if (instance? java.util.regex.Pattern x) re-find =))

(defn string-or-regex?
  [x]
  (or (string? x)
      (instance? java.util.regex.Pattern x)))

(defn should-exception-matches
  [expected-message throwable message]
  (if expected-message
    (let [actual (.getMessage throwable)]
      (if ((match-fn expected-message) expected-message actual)
        true
        (fail (merge (message-map ~message) {:expected expected-message :actual actual}))))
    true))

(defmethod should-body :throws
  [throws-args message]
  (let [[_ expected-type expected-message body] (pop-optional-args [symbol? class-symbol? string-or-regex?] throws-args)]
    `(let [failed?# (Object.)]
       (if (= failed?# (try
                        (do
                          ~@body
                          failed?#)
                        (catch ~expected-type expected-instance#
                          (should-exception-matches ~expected-message expected-instance# ~message))))
         (fail (merge (message-map ~message) {:expected ~expected-type :actual nil}))))))

(defmacro should
  "Evaluate expression and throw an exception if it is not logical
   true. Second arg can be a string with additional message,
   or a map including :message plus any other information you
   want to pass to the handler."
  ([form]
     `(should ~form nil))
  ([form message]
     `~(should-body form message)))





