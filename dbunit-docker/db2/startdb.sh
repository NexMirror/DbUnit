#!/bin/bash
set -e

echo "Starting DB2"
su - db2inst1 -c "db2start"
echo "Creating DbUnit db/user/password"
su - db2inst1 -c "db2 create db DBUNIT"