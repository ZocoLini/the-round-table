#!/bin/bash

if [ "$EUID" -ne 0 ]; then 
  echo "This script should be executed as root." 
  exit 1
fi

# download the app zip file
wget https://lebastudios.org/api/v1/theroundtable/update/theroundtable-linux-x64.zip

# unzip the app
unzip theroundtable-linux-x64.zip

# remove the zip file
rm theroundtable-linux-x64.zip
rm installer.sh

mv theroundtable/theroundtable.desktop /usr/share/applications/the-round-table.desktop
mv theroundtable /opt/theroundtable

# Want to remove this line
chmod 777 -R /opt/theroundtable