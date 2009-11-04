(ns base-spec
  (:use clojure.test))

(defmulti
  reorder
  (fn [args]
    (cond
     (= 3 (count args)) :positive
     (and (= 4 (count args)) (= 'not (second args))) :negative
     :default (throw (RuntimeException. "Rats! You sank my battleship")))))

(defmethod reorder :positive [[actual f expected]]
  `(is
    (~f ~actual ~expected)))

(defmethod reorder :negative [[actual skipnot f expected]]
  `(is
    (not (~f ~actual ~expected))))

(def junk-words #{'should 'be})

(defn polish [args]
  (reorder (remove junk-words args)))

(defmacro it [desc & forms]
  `(do
     (testing ~desc
              ~@(map polish forms))))

;; (comment
;;   "these will work"
;;   (describe
;;    "Circumspect"
;;    [:metadata]
;;    (it "is possible to do a basic assertion"
;;        (let [curvy-glyph? ...]
;;          (2 should have curvy-glyph))
;;        (2 should not = 3 ...)
;;        (2 should = 2)
;;        (2 should be #(< 1 % 3))
;;        (2 should be (fn [x] (< 1 x 3)))
;;        ((< 1 2 3) should be true)
;;        (2 should be numeric)
;;        ([1 2] should =set [2 1])
;;        (2/1 should not fail)
;;        (2/0 should fail)
;;        (2/0 should fail-with ArithmeticException)
;;        (2/0 should fail-with ArithmeticException "divide by zero"
;;             (2/0 should fail-with "divide by zero"
;;                  (2/0 should fail-with ArithmeticException #"zero"))          
;;             (2/0 should fail-with #"zero")))
 
 
;;    (it "is possible to do a multiple assertion"
;;        [:pending]
;;        (for-all [x y] (* x y) should = (* y x)
;;                 10 15
;;                 1  1
;;                 0  20))))

;; (comment
;;   "Ancient history"
;;   ((+ 1 1) in some places of the world be > 2)
;;   ((+ 1 1) should as-big-as 2)
;;   ((+ 1 1) should 2) 
;;   (should (+ 1 1) 2)
;;   (2 should be (< 1 % 3))   ;;hmmm....
;;   (2 should be x (< 1 x 3)) ;;hmmm....)