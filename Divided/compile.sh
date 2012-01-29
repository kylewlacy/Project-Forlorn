#/bin/bash
clear
cd $(dirname $0)
javac -Xlint:deprecation -cp "/Users/kyle/Documents/Minecraft Server/Bukkit/bukkit (1.1 R2).jar" -d ./output ./*.java
cp plugin.yml ./output
cd ./output
jar -cf /Users/kyle/Documents/Minecraft\ Server/Bukkit/plugins/Divided.jar ./*
cd ..