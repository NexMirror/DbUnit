#!/bin/bash
set -e

echo "Starting up and restarting Oracle"
/usr/sbin/startup.sh
/etc/init.d/oracle-xe stop
/etc/init.d/oracle-xe start
echo "Oracle is ready for test!"