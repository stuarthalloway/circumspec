(ns circumspec.should
  (use [clojure.contrib.def :only (defvar)]
       [clojure.test :only (function? *testing-contexts* report)]
       [clojure.contrib.debug :only (debug-repl)]
       [clojure.contrib.error-kit :only (deferror throw-msg raise handle)]
       [circumspec.utils :only (pop-optional-args class-symbol? pps)]))

(defvar *debug* nil
  "Set to true to make should failures bounce into debug repl.")

(defn warn?
  "Look at should failed options to see if the failure should
   warn instead of raising."
  [options]
  (boolean (options :warn)))

;; TODO: refactor, default-message
(defn default-fail-message
  "Default string message for a ahould failure/warning."
  [{expected :expected
    actual :actual
    message :message
    :as options}]
  (let [message-prefix (if message (str message "\n") nil)]
    (str
     "circumspec "
     (if (warn? options) "warning" "should-assertion failed")
     ":\n"
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

(deferror should-failed []
  [options]
  (merge options 
         {:msg (default-fail-message options)
          :unhandled (fn [options] (throw (new circumspec.AssertFailed (:msg options) options)))}))

(defn should-repl [options]
  (println (default-fail-message options))
  (println "Starting debug repl. Type () to end repl and propagate failure.")
  (debug-repl))

(defmulti fail
  (fn [options]
    (when *debug* (should-repl options))
    (cond
     (warn? options) :warn
     :default :error)))

(defmethod fail :warn [options]
  (let [message (default-fail-message options)]
    (binding [*out* *err*]
      (println message)))
  false)

(defmethod fail :error [options]
  (raise should-failed options))

;; TODO: intelligence for false/false case
;; TODO: chain out on first 
(defmulti should-body
  (fn [form options]
    (when (seq? form)
      (cond
       (= 'throws? (first form)) :throws
       (function? (first form)) :predicate))))

(defmethod should-body :default
  [form options]
  `(let [value# ~form]
     (if value#
       true
       (fail (merge ~options {:expected '~form :actual value#})))))

(defmethod should-body :predicate
  [[pred & args :as form] options]
  `(let [values# (list ~@args)
         result# (apply ~pred values#)]
     (if result#
       true
       (fail (merge ~options {:expected '~form :actual (list '~'not (cons '~pred values#))})))))

;; TODO: make private, and make describe automatically expose private vars
(defn match-fn
  [x]
  (if (instance? java.util.regex.Pattern x) re-find =))

(defn string-or-regex?
  [x]
  (or (string? x)
      (instance? java.util.regex.Pattern x)))

(defn should-exception-matches
  [options expected-message throwable]
  (if expected-message
    (let [actual (.getMessage throwable)]
      (if ((match-fn expected-message) expected-message actual)
        true
        (fail (merge options {:expected expected-message :actual actual}))))
    true))

(defmethod should-body :throws
  [throws-args options]
  (let [[_ expected-type expected-message body] (pop-optional-args [symbol? class-symbol? string-or-regex?] throws-args)]
    `(let [failed?# (Object.)]
       (if (= failed?# (try
                        (do
                          ~@body
                          failed?#)
                        (catch ~expected-type expected-instance#
                          (should-exception-matches ~options ~expected-message expected-instance#))))
         (fail (merge ~options {:expected ~expected-type :actual nil}))        ))))

(defn as-should-options
  "Coerce should options from caller convenience to form used
   in implementation."
  [str-or-map]
  (if (string? str-or-map) {:message str-or-map} str-or-map))

(defmacro should
  "Evaluate expression and throw an exception if it is not logical
   true. Second arg can be a string with additional message,
   or a map including :message plus any other information you
   want to pass to the handler."
  ([form]
     `(should ~form nil))
  ([form options]
     `~(should-body form (as-should-options options))))

(defmacro warn-unless
  "form *should* be true, but this requirement is not necessary
   for correct functioning. Functions like should, except that
   fails default to stderr warnings instead of error-kit raises.
   Useful as a design tool, especially when integrating with
   other code."
  ([form]
     `(should ~form {:warn true}))
  ([form options]
     `(should ~form (assoc (as-should-options ~options) :warn true))))




