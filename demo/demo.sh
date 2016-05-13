#!/bin/bash

export PATH=../bin:$PATH

mkdir -p lib
../gradlew -p .. build
javac -d . -cp ../build/libs/*.jar *.java
jar cvf lib/demo.jar *.class
rm *.class

cat en-sample.conllu | udapi.groovy Read::CoNLLU RehangPrepositions Write::CoNLLU > prepositions-up.conllu
