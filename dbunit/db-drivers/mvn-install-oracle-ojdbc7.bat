rem Convenience script to put Oracle DB driver jar in local Maven repo for building dbUnit

rem To use this script:
rem  1. download driver jar from Oracle website
rem  2. place the jar in directory containing this script and run this script

setlocal

set FILE=ojdbc7.jar
set GROUP=com.oracle
set ARTIFACT=ojdbc7
set VERSION=12.1.0.2

mvn install:install-file -Dfile=%FILE% -DgroupId=%GROUP% -DartifactId=%ARTIFACT% -Dversion=%VERSION% -Dpackaging=jar

endlocal
