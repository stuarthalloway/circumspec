#!/bin/sh
CLASSPATH=src:classes:examples/the-rspec-book

for f in lib/*.jar; do
    CLASSPATH=$CLASSPATH:$f
done

java $@ -Dcircumspec.test-dir=examples/the-rspec-book -Dcircumspec.test-regex='-(feature|test)$' -Xmx1G -cp $CLASSPATH clojure.main -e "(use 'circumspec.cli) (run-tests)"