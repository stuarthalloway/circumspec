#!/bin/sh
CLASSPATH=classes:src:test:features

for f in lib/*.jar; do
    CLASSPATH=$CLASSPATH:$f
done

java -cp $CLASSPATH jline.ConsoleRunner clojure.main -i bin/repl.clj -r