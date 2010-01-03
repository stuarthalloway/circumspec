(ns circumspec.colorize-test
  (:use circumspec
        circumspec.colorize)
  (:refer-clojure :exclude (assert)))

(describe colorize
  (it "does not colorize empty strings!"
    (binding [*colorize* true]
      (assert (= "" (colorize "" :green)))))
  (it "colors when *colorize* is true"
    (binding [*colorize* true]
      (assert (= (str (char 27) "[32mfoo" (char 27) "[0m") (colorize "foo" :green)))))
  (it "no-ops when *colorize* is false"
    (binding [*colorize* false]
      (assert (= "foo" (colorize "foo" :green))))))