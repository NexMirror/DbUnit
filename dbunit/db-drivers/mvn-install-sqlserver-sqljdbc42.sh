#!/bin/sh

# Convenience script to put Microsoft SQL Server DB driver jar in local Maven repo for building dbUnit

# To use this script:
# 1. download driver jar from Microsoft website
# 2. place the jar in directory containing this script and run this script

FILE=sqljdbc42.jar
GROUP=com.microsoft
ARTIFACT=sqljdbc42
VERSION=4.2.0

mvn install:install-file -Dfile=$FILE -DgroupId=$GROUP -DartifactId=$ARTIFACT -Dversion=$VERSION -Dpackaging=jar
