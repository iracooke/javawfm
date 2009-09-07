
.packageGlobals<-new.env()


hackjClassPath<-function(name,jars="*"){
	jloc = system.file("java",package="farmR")
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
}

winjPackage<-function(name,jars="*"){
	.jpackage(name,nativeLibrary=TRUE)
#	hackjClassPath(name)
#	jloc=system.file("libs",package="farmR")

#	path=paste(c(jloc,"farmR.dll"),collapse="/")
#	rJava:::.jaddLibrary(name, path)
}

# This function is a replacement for .jpackage to be used on OSX because it's really hard to reliably get a jnilib into the right place for .jpackage to search for it on OSX (ie into libs/arch in the package directory )
osxjPackage<-function(name,jars="*"){
	hackjClassPath(name)
	libloc=system.file("libs",package="farmR")

	path=paste(c(libloc,.Platform$r_arch,"libfarmR.jnilib"),collapse="/")
	rJava:::.jaddLibrary(name, path)
}

.onLoad <- function(libname,pkgname){
	require(methods)	
	si=Sys.info()
	if ( !is.null(si)){
		if ( si["sysname"]== "Darwin"){
			osxjPackage(pkgname)
		} else if ( si["sysname"]=="Windows"){
			winjPackage(pkgname)
		} else {
			.jpackage(pkgname,nativeLibrary=TRUE)
		}		
	} else {
		cat("Warning: System not recognised. attempting to load jni library using defaults\n")
		.jpackage(pkgname,nativeLibrary=TRUE)
	}
}


.onAttach<-function(libname,pkgname){
	si=Sys.info()
	if ( !is.null(si)){
		if ( si["sysname"]=="Windows"){
			.packageGlobals$supportedSolvers<-c("glpk")
			.packageGlobals$defaultSolver<-"glpk"
			return()
		}
	}
	# Define global package variables
	.packageGlobals$supportedSolvers<-c("cbc","glpk")
	.packageGlobals$defaultSolver<-"cbc"	
}

setClass("FarmRepresentation")
setClass("Farm",representation(model="jobjRef",cropNames="vector"),prototype(model=.jnull("jfm/r/SimpleFarmRepresentation")),contains="FarmRepresentation")
setClass("CompositeFarm",representation(model="jobjRef",cropNames="vector"),prototype(model=.jnull("jfm/r/CompositeFarmRepresentation")),contains="FarmRepresentation")
setClass("Parameters")
setClass("FarmParameters",representation(document="jobjRef"),contains="Parameters")
setClass("ObjectiveParameters",representation(document="jobjRef"),contains="Parameters")
