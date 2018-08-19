#!/bin/bash
set -e

/docker-entrypoint.sh postgres &
echo "Sleeping for 30 while DB starts..."
sleep 30