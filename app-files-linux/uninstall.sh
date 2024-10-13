#!/bin/bash

if [ "$EUID" -ne 0 ]; then 
  echo "This script should be executed as root." 
  exit 1
fi

rm -rf /opt/theroundtable/
rm -rf /usr/share/applications/the-round-table.desktop
