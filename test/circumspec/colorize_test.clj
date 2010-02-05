(ns circumspec.colorize-test
  (:use circumspec
        circumspec.colorize
        circumspec.for-all)
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
      (for-all [s (unicode)]
               (should (= (str s) (colorize s :green))))))
  (it "no-ops when *colorize* is false"
    (binding [config/colorize (constantly false)]
      (for-all [s (unicode-string)]
               (should (= s (colorize s :green)))))))