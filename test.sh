#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
JAVAFILES="$( find $DIR -name '*.java' -print )"
cd $DIR/test
TESTCLASSES="$( find . -name '*.java' -print | cut -d/ -f2- | cut -d. -f1 | sed 's/\//./g' )"
javac -sourcepath $DIR/src:$DIR/test -classpath $DIR/junit.jar:$DIR/bin -d $DIR/bin $JAVAFILES
java -classpath $DIR/junit.jar:$DIR/bin org.junit.runner.JUnitCore $TESTCLASSES
