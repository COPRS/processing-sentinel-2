#!/usr/bin/env bash

export JAVA_HOME=/wrapper/jdk
export PATH=/wrapper/jdk/bin:$PATH
export LD_LIBRARY_PATH=/wrapper/jdk/lib:$LD_LIBRARY_PATH

/wrapper/jdk/bin/java -jar /wrapper/app.jar
