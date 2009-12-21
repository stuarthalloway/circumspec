(ns circumspec
  (:refer-clojure :exclude (assert)))

(defn- migrate
  "Like immigrate, but takes a map of ns -> varnames."
  [ns-var-map]
  (doseq [[ns syms] ns-var-map]
    (require ns)
    (doseq [sym syms]
      (let [var (ns-resolve ns sym)]
        (let [sym (with-meta sym (assoc (meta var) :ns *ns*))]
          (if (.hasRoot var)
            (intern *ns* sym (.getRoot var))
            (intern *ns* sym)))))))

(migrate
 {'circumspec.context '[describe it]
  'circumspec.for '[for-these]
  'circumspec.assert '[assert warn-unless]})

