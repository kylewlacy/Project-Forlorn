#/bin/bash
clear
cd $(dirname $0)
javac -Xlint:deprecation -cp ../bukkit.jar -d ./output ./*.java
cp plugin.yml ./output
cd ./output
jar -cf ../../Divided.jar ./*
cd ..