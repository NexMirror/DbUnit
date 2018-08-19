#!/bin/bash
set -e

echo "Killing DB2..."
su - db2inst1 -c "db2stop"
exit