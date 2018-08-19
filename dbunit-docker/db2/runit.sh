#!/bin/bash
set -e

whoami
dir=$(pwd)
chown db2inst1 -R ${dir}
su - db2inst1 -c "mvn -f ${dir}/dbunit/dbunit/pom.xml -s ${dir}/dbunit/settings.xml -B -e -U -Pit-config,db2 verify"