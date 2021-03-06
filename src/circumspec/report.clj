(ns circumspec.report
  (:use [circumspec.colorize :only (colorize)]))

(defn failure-string
  [text]
  (colorize text :red))

(defn error-string
  [text]
  (colorize text :red))

(defn success-string
  [text]
  (colorize text :green))

(defn pending-string
  [text]
  (colorize text :yellow))

(defn fail?
  [result]
  (boolean (:failure result)))

(defn error?
  [result]
  (boolean (:error result)))

(defn pending?
  [result]
  (boolean (:pending result)))

(defn result-name
  [result]
  (:name result))

(defn result-context
  [result]
  (:context result))

(defn result-color
  [result]
  (cond
   (fail? result) :red
   (error? result) :red
   (pending? result) :yellow
   :default :green))