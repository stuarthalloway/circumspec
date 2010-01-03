(ns circumspec.story
  (:use [circumspec.runner :only (*story*)]))

(defn run-with-story
  [line f]
  (set! *story* (conj *story* line))
  (f))

(defmacro given
  [line & body]
  `(let [f# (fn [] ~@body)]
     (run-with-story ~line f#)))