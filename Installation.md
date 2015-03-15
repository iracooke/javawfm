# Requirements #

Before installing JFM ensure that you have a working Java Runtime Environment on your machine. Your Java should be version 1.5 or later. Recent versions of Mac OSX have this pre-installed. If you don't already have it, java is a free download from http://www.java.com/getjava


# Basic Install #
To Install the basic package without to ability to change the source code should just download the appropriate file for your system. jfmwin.x.zip for windows or jfmmac.x.zip for mac .. where x is the version number. Unpack these zip files and follow the instructions in the README file.




# Developer Install #
Most regular development of JFM is done in Java, so you will almost certainly not
need the C++ sources required to build the native LP Solver Library. (Of course you will
need a pre-compiled version of this library installed to run your code .. see above).

_Step 1: Download the Source_
The source code for JFM is available at http://code.google.com/p/javawfm/
I recommend checking out this source using eclipse via the subversion plugin.
Eclipse is a free download from http://www.eclipse.org, and instructions on installing the subversion plugin for eclipse can be found at http://subclipse.tigris.org/install.html.

Once subclipse is installed, the jfm source code can be checked out as follows;
  1. Go to File->New->Project
  1. Select "Checkout Projects from SVN" under the SVN tab
  1. Select "Create a new repository location", and enter;
> > `http://javawfm.googlecode.com/svn/trunk/`
  1. Note that if you want to commit changes you will need to use the https version of this repository and obtain a password (see instructions at http://code.google.com/p/javawfm/source/checkout )
  1. Select the folder "java"
  1. Click Next and then Finish
  1. You should now see the New Project Wizard. Select "Java Project" from the list and click next
  1. Call the project javawfm
  1. Leave all defaults as they are and click next
  1. Click finish

_Step 2: Automate the Build Process_
  1. Once the JFM source code is checked out go to "Project->Properties"
  1. Select "builders" from the left hand list and click "new"
  1. Select "Ant Builder" from the list of options
  1. Type "makejar" into the name field for the new builder
  1. In the buildfile field select "Browse Workspace" and select the file makejar.xml under the javawfm folder.
  1. Click OK.

_Step 3: Do a Build_
  1. Build the project by selecting "Build All" from the project menu.

_Step 4: Run the program_
  1. Put your newly created jfm.jar file into your CLASSPATH.  If you find that changes you've made are not showing up when you run the code, this is probably because another jfm.jar file somewhere else is taking priority. To fix this, either put your new jfm.jar into the place where the old one resides ( by overwriting it), or change your CLASSPATH variable so that your javawfm build directory comes before any other directories.
  1. Run the code using

`java -Xss1m jfm.SimpleApp example.xml`

Sometimes you might also need more memory so use

`java -Xmx1m jfm.SimpleQApp example.xml`

# Native Library Development Install #
In the unlikely event that you want to make changes to the native library you will need to install GLPK. You will also need to check out the c++ sources (under http://javawfm.googlecode.com/svn/trunk/native ), and change the makefile to suit your system.

_Installing GLPK_
  1. Download a recent version of GLPK (4.23 at the time of writing) from http://www.gnu.org/software/glpk/
  1. On Unix, issue configure and then make.
```
 	./configure 
 	make
 	sudo make install			
```
This will install GLPK in `/usr/local/`
It might be necessary to specify `--disable-shared` in order for compilation to work on some systems (eg Mac OSX).
Compiling the native library on windows requires some additional tools. I used the MinGW compiler in conjunction with msys as I could not get the process to work using cygwin.