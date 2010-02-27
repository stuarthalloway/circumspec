(ns circumspec.runner-test
  (:use circumspec)
  (:require [circumspec.runner :as r]))

(describe r/with-timing
  (it "adds nsec timings"
    (let [x (r/with-timing {:success 1})]
      (should (= 2 (count x)))
      (should (= 1 (:success x)))
      (should (instance? Long (:nsec x))))))

(describe r/tally
  (it "sums the success, failure, error, and pending keys in sequence of results"
    (for-these [tallied result-seq] (should (= tallied (r/tally result-seq)))
               nil []
               
               {} [{}]
               
               {:success 3} [{:success 1}{:success 2}]
               
               {:success 1 :error 2 :failure 3 :pending 4}
               [{:success 1} {:error 2 :pending 2} {:failure 3 :pending 2}]
                
               )))
