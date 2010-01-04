(ns circumspec.cli-test
  (:use circumspec
        circumspec.cli))

(describe "tally"
  (it "sums the success, failure, error, and pending keys in sequence of results"
    (for-these [tallied result-seq] (should (= tallied (tally result-seq)))
               nil []
               
               {} [{}]
               
               {:success 3} [{:success 1}{:success 2}]
               
               {:success 1 :error 2 :failure 3 :pending 4}
               [{:success 1} {:error 2 :pending 2} {:failure 3 :pending 2}]
                
               )))