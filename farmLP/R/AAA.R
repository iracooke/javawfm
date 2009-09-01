
# This function is a replacement for .jpackage to be used on OSX because it's really hard to reliably get a jnilib into the right place for .jpackage to search for it on OSX (ie into libs/arch in the package directory )
osxjPackage<-function(name,jars="*"){
	jloc = system.file("java",package="farmLP")
	if (!rJava:::.jniInitialized) .jinit()
	classes <- system.file("java", package=name, lib.loc=NULL)
	if (nchar(classes)) {
		rJava:::.jaddClassPath(classes)
		if (length(jars)) {
			if (length(jars)==1 && jars=='*') {
				jars <- grep(".*\\.jar",list.files(classes,full.names=TRUE),TRUE,value=TRUE)
				if (length(jars)) rJava:::.jaddClassPath(jars)
			} 
			else rJava:::.jaddClassPath(paste(classes,jars,sep=.Platform$file.sep))
		}
	}

	path=paste(c(jloc,"libfarmLP.jnilib"),collapse="/")
	rJava:::.jaddLibrary(name, path)
}


.onLoad <- function(libname,pkgname){
	require(methods)	
	# On linux and windows the native lib is in libs whereas in osx its in the java directory
	if (nchar(system.file("libs",package="farmLP"))){		
		.jpackage(pkgname,nativeLibrary=TRUE)
	} else {
		osxjPackage(pkgname)
	}
}

setClass("FarmRepresentation")
setClass("Farm",representation(model="jobjRef",cropNames="vector"),prototype(model=.jnull("jfm/r/SimpleFarmRepresentation")),contains="FarmRepresentation")
setClass("CompositeFarm",representation(model="jobjRef",cropNames="vector"),prototype(model=.jnull("jfm/r/CompositeFarmRepresentation")),contains="FarmRepresentation")
setClass("Parameters")
setClass("FarmParameters",representation(document="jobjRef"),contains="Parameters")
setClass("ObjectiveParameters",representation(document="jobjRef"),contains="Parameters")
