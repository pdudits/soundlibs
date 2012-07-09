#! /bin/sh

# This script removes all Tritonus components from rt.jar.
# The removal includes the javax.sound.* classes and the
# service provider configuration files.
# Note that it does not remove Sun implementation components.

# The location of the rt.jar file to remove from.
# The value below is for newer kaffe installations.
# Adapt to your needs.
rt_jar=/usr/local/kaffe/jre/lib/rt.jar

# now remove...
zip -d ${rt_jar} org/tritonus/\*
zip -d ${rt_jar} javax/sound/\*
zip -d ${rt_jar} META-INF/services/javax.sound.\*

# end
