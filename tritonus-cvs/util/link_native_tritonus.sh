#!/bin/sh
for i in /usr/lib/libtritonus*.so
do
    ln -sf $i
done
