#!/bin/bash
set -e

/entrypoint.sh mysqld &
echo "Sleeping for 30 while DB starts..."
sleep 30