#!/bin/sh
CLASSPATH=classes:src:test

for f in lib/*.jar; do
    CLASSPATH=$CLASSPATH:$f
done

java -cp $CLASSPATH jline.ConsoleRunner clojure.main -e "(use 'test) (run-tests!)"