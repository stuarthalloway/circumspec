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

(defn fail?
  [result]
  (boolean (result :fail)))

(defn error?
  [result]
  (boolean (result :error)))

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
   :default :green))