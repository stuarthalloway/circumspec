(ns circumspec.for)

(defmacro for-these [names expr & table]
  `(do
     ~@(map
        (fn [args]
          `(let [~@(interleave names args)] ~expr ))
        (partition (count names) table))))
