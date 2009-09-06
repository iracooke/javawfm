# This is a highly non-portable makefile for building windows binaries and reflects my total lack of windows knowledge 

# It works on my setup based on installing the following programs to a clean install of WinXP home edition running as a virtual machine
# 1 First compile the package jar (I do this on a unix system with ant installed)
# 2 Install mingw and minsys
# 3 Install Java EE SDK into /c/Sun/SDK  (yes this should have probably just been Java SE )


JAVA_HOME=/c/Sun/SDK/jdk/
JAVAH=$(JAVA_HOME)/bin/javah

# Modify this to the directory where the dll is getting built
SRCDIR=/home/Owner/Desktop/farmR/src

JAVA_INCLUDES=$(JAVA_HOME)/include $(JAVA_HOME)/include/win32

GLPKINSTDIR=$(SRCDIR)/depends/glpk/
GLPKINCDIR=$(GLPKINSTDIR)/include
GLPKLIBDIR=$(GLPKINSTDIR)/lib

PKG_CPPFLAGS+= $(JAVA_INCLUDES:%=-I%) -I$(GLPKINCDIR)

JARDIR = $(RINSTDIR)/java


OBJECTS = GLPK.o GLPKPeer.o

PKG_LIBDIRS=-L$(GLPKINSTDIR)/lib -L$(JAVA_HOME)/lib
PKG_LIBS=-lglpk 

SUBDIRSGLPK = depends/glpk-4.39/

.PHONY: all subdirs 

all: GLPK.o GLPKPeer.o
	$(CXX) -Wall -D_JNI_IMPLEMENTATION_ -Wl,--kill-at -shared -o libfarmR.dll --add-stdcall-alias   $(PKG_LIBDIRS) $(OBJECTS)  $(PKG_LIBS) 


subdirs:
	(cd depends/glpk-4.39/ && ./configure --enable-static --disable-shared --with-pic --prefix=$(GLPKINSTDIR))
	$(MAKE) install -C $(SUBDIRSGLPK)

jfm_lp_GLPKPeer.h: ./java/src/jfm/lp/GLPKPeer.java 
	$(JAVAH) -jni -classpath $(JARDIR)/javawfm.jar jfm.lp.GLPKPeer

GLPK.o: GLPK.cpp jfm_lp_GLPKPeer.h subdirs
	$(CXX) -c $< $(PKG_CPPFLAGS)

GLPKPeer.o: GLPKPeer.cpp jfm_lp_GLPKPeer.h subdirs
	$(CXX) -c $< $(PKG_CPPFLAGS)
	
