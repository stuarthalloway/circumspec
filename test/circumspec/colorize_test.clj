(ns circumspec.colorize-test
  (:use circumspec
        circumspec.colorize))

(describe colorize
  (it "does not colorize empty strings!"
    (binding [*colorize* true]
      (should (= "" (colorize "" :green)))))
  (it "colors when *colorize* is true"
    (binding [*colorize* true]
      (should (= (str (char 27) "[32mfoo" (char 27) "[0m") (colorize "foo" :green)))))
  (it "no-ops when *colorize* is false"
    (binding [*colorize* false]
      (should (= "foo" (colorize "foo" :green))))))