#
#  makefile for lame JNI classes
#

#
#       Borland specific directives ---
#
.SWAP
.AUTODEPEND

# edit this to reflect your installation of JDK !
JDK_HOME=E:\jdks\jdk1.3.0_02

#
# Set tool and version names:

CPP        = bcc32
CPP32      = cpp32

# Defines
DEFINES = -DUSE_BLADENC_API

#DEFINES = $(DEFINES) -D_DEBUG

#
# Set the various flags:

CCLINKOPT = -lGn
# uncomment the following line for debug compile
#DBGOPT= -v -N -x -xp

CCLINKOPT = $(CCLINKOPT) -tWM

#options for single thread:
#CCLINKOPT = $(CCLINKOPT) -tWM-

#
# C: console
# D: DLL
# 
DEFOPTS=-tWD

#
# Set any compiler options

# -4: i486 
# -5: Pentium
# -6: Pentium Pro/Pentium II/III options
# -O2: optimize for speed
# -OS: Pentium instruction scheduling
CCOPTS = -w -w-8004 -w-8057 -4 -O2 -OS -q

#Compile flags:
CPPFLAGS= $(CCOPTS) $(DBGOPT)  $(ENVOPTS) $(DEFOPTS) $(CCLINKOPT) $(DEFINES)
LINKFLAGS=$(CCLINKOPT) $(DEFOPTS) -lq -L$(MAKEDIR)\..\lib 
INCLUDES= -I$(MAKEDIR)\..\include;$(MAKEDIR)\..\include\windows;$(JDK_HOME)\include;$(JDK_HOME)\include\win32

########################   Targets    #################################
TARGETS=lametritonus.dll
OBJECTS=bladenc.obj org_tritonus_lowlevel_lame_Lame.obj

all: $(TARGETS)

install: all
	copy $(TARGETS) $(JDK_HOME)\jre\bin

lametritonus.dll: $(OBJECTS)
	$(CPP) $(LINKFLAGS) -e$* {$? }

clean:
   -@if exist *.obj del *.obj                 >nul
   -@if exist *.lib del *.lib                 >nul
   -@if exist *.exe del *.exe                 >nul
   -@if exist *.dll del *.dll                 >nul
   -@if exist *.tds del *.tds                 >nul


###########################  Implicit Rules #######################
.c.obj:
    $(CPP) $(CPPFLAGS) $(INCLUDES) -c {$? }

.c.i:
    $(CPP32) $(CPPFLAGS) -c -Sr -Sd {$? }

