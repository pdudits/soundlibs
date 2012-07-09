#!/bin/sh
for i in /usr/share/java/*.jar
do
    ln -sf $i
done
