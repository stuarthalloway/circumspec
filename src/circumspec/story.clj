(ns circumspec.story
  (:refer-clojure :exclude [when and])
  (:use [circumspec.runner :only (*story*)]))

(defn run-with-story
  [line f]
  (set! *story* (conj *story* line))
  (f))

;; temporary impl
(defmacro given
  [line & body]
  `(let [f# (fn [] ~@body)]
     (run-with-story ~line f#)))

(defmacro when
  [line & body]
  `(let [f# (fn [] ~@body)]
     (run-with-story ~line f#)))

(defmacro then
  [line & body]
  `(let [f# (fn [] ~@body)]
     (run-with-story ~line f#)))

(defmacro and
  [line & body]
  `(let [f# (fn [] ~@body)]
     (run-with-story ~line f#)))