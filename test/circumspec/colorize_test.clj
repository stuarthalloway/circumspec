(ns circumspec.colorize-test
  (:use circumspec
        circumspec.colorize)
  (:require [circumspec.config :as config]))

(describe colorize
  (it "does not colorize empty strings!"
    (binding [config/colorize (constantly true)]
      (should (= "" (colorize "" :green)))))
  (it "colors when *colorize* is true"
    (binding [config/colorize (constantly true)]
      (should (= (str (char 27) "[32mfoo" (char 27) "[0m") (colorize "foo" :green)))))
  (it "handles single characters"
    (binding [config/colorize (constantly false)]
      (should (= "A" (colorize \A :green)))))
  (it "no-ops when *colorize* is false"
    (binding [config/colorize (constantly false)]
      (should (= "foo" (colorize "foo" :green))))))