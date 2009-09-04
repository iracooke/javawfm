
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
	libloc=system.file("libs",package="farmLP")

	path=paste(c(libloc,.Platform$r_arch,"libfarmLP.jnilib"),collapse="/")
	rJava:::.jaddLibrary(name, path)
}


.onLoad <- function(libname,pkgname){
	require(methods)	
	# We need a special jpackage command on osx
	si=Sys.info()
	if ( !is.null(si)){
		if ( si["sysname"]== "Darwin"){
			osxjPackage(pkgname)
			return()
		}
	}
	
	.jpackage(pkgname,nativeLibrary=TRUE)
}

setClass("FarmRepresentation")
setClass("Farm",representation(model="jobjRef",cropNames="vector"),prototype(model=.jnull("jfm/r/SimpleFarmRepresentation")),contains="FarmRepresentation")
setClass("CompositeFarm",representation(model="jobjRef",cropNames="vector"),prototype(model=.jnull("jfm/r/CompositeFarmRepresentation")),contains="FarmRepresentation")
setClass("Parameters")
setClass("FarmParameters",representation(document="jobjRef"),contains="Parameters")
setClass("ObjectiveParameters",representation(document="jobjRef"),contains="Parameters")
