;; http://gist.github.com/255883
;; even more enhanced version with that allows ret val override and better prompt
 
;; Slightly enhanced version of Alex Osborne's debug-repl (http://gist.github.com/252421)
;; Typing () quits the debug REPL, making it possible to continue without terminating the
;; input stream by typing Ctrl-D.
 
;; Inspired by George Jahad's version: http://georgejahad.com/clojure/debug-repl.html
 
(ns clojure.contrib.debug
  [:require clojure.main])
 
(defmacro local-bindings
  "Produces a map of the names of local bindings to their values."
  []
  (let [symbols (map key @clojure.lang.Compiler/LOCAL_ENV)]
    (zipmap (map (fn [sym] `(quote ~sym)) symbols) symbols)))
 
(declare *locals*)
(defn eval-with-locals
  "Evals a form with given locals. The locals should be a map of symbols to
values."
  [locals form]
  (binding [*locals* locals]
    (eval
     `(let ~(vec (mapcat #(list % `(*locals* '~%)) (keys locals)))
        ~form))))
 
(defn dr-read
  [request-prompt request-exit]
  (let [input (clojure.main/repl-read request-prompt request-exit)]
    (if (= input '())
      request-exit
      input)))
 
(def level 0)
(def counter (atom 1000))
(defn inc-counter []
  (swap! counter inc))
 
(def element (atom nil))
 
(def quit-dr-exception
     (proxy [Exception java.util.Enumeration] []
       (nextElement [] @element)))
 
(defn quit-dr [ & form]
  (reset! element (first form))
  (throw quit-dr-exception))
 
(defn caught [exc]
  (if (= (.getCause exc) quit-dr-exception)
    (throw quit-dr-exception)
    (clojure.main/repl-caught exc)))
 
(defmacro debug-repl
  "Starts a REPL with the local bindings available."
  ([]
     '(debug-repl nil))
  ([form]
     `(let [counter# (inc-counter)
            eval-fn# (partial eval-with-locals (local-bindings))]
        (try
         (binding [level (inc level)]
           (clojure.main/repl
            :prompt #(print (str "dr-" level "-" counter# " => "))
            :eval eval-fn#
            :read dr-read
            :caught caught))
         (catch Exception e#
           (if (= e# quit-dr-exception)
             (if-let [new-form# (.nextElement quit-dr-exception)]
               (eval-fn# new-form#)
               (eval-fn# ~form))
             (throw e#)))))))
 