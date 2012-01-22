#/bin/bash
clear
cd $(dirname $0)
javac -Xlint:deprecation -cp /Users/kyle/Documents/Minecraft\ Server/Bukkit/bukkit.jar -d ./output ./*.java
cp -v plugin.yml ./output
cd ./output
jar -cvf /Users/kyle/Documents/Minecraft\ Server/Bukkit/plugins/Divided.jar ./*
cd ..