#!/bin/sh

# Convenience script to put Oracle DB driver jar in local Maven repo for building dbUnit

# To use this script:
# 1. download driver jar from Oracle website
# 2. place the jar in directory containing this script and run this script

FILE=ojdbc14.jar
GROUP=com.oracle
ARTIFACT=ojdbc14
VERSION=10.2.0.4.0

mvn install:install-file -Dfile=$FILE -DgroupId=$GROUP -DartifactId=$ARTIFACT -Dversion=$VERSION -Dpackaging=jar
