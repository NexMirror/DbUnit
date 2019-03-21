#!/bin/bash
set -e

/opt/mssql/bin/sqlservr &
echo "Sleeping for 30 while DB starts..."
sleep 30
echo "Creating DbUnit db/user/password"
/opt/mssql-tools/bin/sqlcmd -S localhost -U SA -P 'yourStrong(!)Password' -i /mssql-setup.sql

while sleep 60; do
  ps aux | grep sqlservr | grep -q -v grep
  SQLSERVER_STATUS=$?
  if [ $SQLSERVER_STATUS -ne 0 ]; then
    echo "SQL Server stopped, exiting"
    exit 1
  fi
done