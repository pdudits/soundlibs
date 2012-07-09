#!/bin/sh
for i in /usr/share/java/tritonus_*.jar
do
    ln -sf $i
done
