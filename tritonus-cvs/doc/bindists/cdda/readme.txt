CDDA support for Java Sound
===========================

Version: 2002-03-30

Overview
--------
This package contains a plug-in for the Java 2 platform, 
which enables you to read/rip/grab/digitally extract the audio
data from audio CDs (CDDA -> Compact Disc Digital Audio).
Included are compiled libraries for Linux/i386.

All files are part of Tritonus, an open source implementation
of the Java Sound API. Tritonus is under the GNU Library 
General Public License. The source code of the plug-in is 
contained in the tritonus distribution.


Features
--------
The plugin enables you to digitally read audio CDs. This is
very useful when you want to archive CD's in highest quality.


Requirements
------------
- A Java 2 platform. It should work with JDK1.2.x (then
  you need a full installation of tritonus), but
  Version 1.3 or higher is recommended. You can download a 
  JDK or JRE from http://java.sun.com . This documentation 
  assumes a Sun JDK1.4 installation.
- The library tritonus_share.jar.
  You can download it from http://www.tritonus.org/plugins.html
  or compile tritonus.
- On Linux/i386:
  The plugin needs the shared libraries of cdparanoia III version 9.8,
  which you can download from http://www.xiph.org/paranoia/ cdparanoia
  is included in most modern Linux distributions as a package.


Installation of the cdda plugin on Linux/i386
---------------------------------------------
1. Copy tritonus_cdda.jar, and tritonus_share.jar in 
   the Extension Directory (see below).
2. Copy libtritonuscdparanoia.so in the Binary Extension
   Directory.
3. If necessary, install cdparanoia . It may be necessary
   (as root) to include /usr/local/lib to /etc/ld.so.conf 
   and then run "ldconfig".

Linux directories
-----------------
You can find out the installation directory of the JDK by issuing
"which java". I get the follwing result:
/usr/local/java2/bin/java 
which means that JDK home is /usr/local/java2. The existence of
the "jre" directory in JDK home usually means that you have installed
the JDK (rather than JRE).

Extension Directory:
  For JDK: jre/lib/ext in JDK home,
    e.g. /usr/local/java2/jre/lib/ext
  For JRE: lib/ext in JRE home, 
    e.g. /usr/local/jre1.3.0_02/lib/ext
  Alternatively, you may also place the jars in the classpath.
  Then you can place them where ever you want:
  export CLASSPATH=tritonus_share.jar:tritonus_mp3.jar:$CLASSPATH
Binary Extension Directory:
  For JDK: jre/lib/i386 in JDK home,
    e.g. ls /usr/local/java2/jre/lib/i386
  For JRE: bin in JRE home, 
    e.g. ls /usr/local/jre1.3.0_02/lib/i386


Test
----
At the moment, no real documentation exists, except the
talk that Matthias and I gave at the JavaOne 2002 conference.
The session slides and the demo program can be downloaded
from http://www.jsresources.org/apps/ripper/



Contact
-------
When you encounter problems or want to tell me that it works,
feel free to contact me at
florian@tritonus.org. You can also subscribe to a Tritonus
mailing list at
http://lists.sourceforge.net/lists/listinfo/tritonus-user
and write your email to the list.


Download
--------
The latest version of this plug-in can be downloaded from
http://www.tritonus.org in the plugins section.


Copyright
---------
This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published
by the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

The GNU Library General Public License is included in the file "LGPL".

(c) 2002 by Matthias Pfisterer and Florian Bomers
