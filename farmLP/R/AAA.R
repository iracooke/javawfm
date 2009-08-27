
.onLoad <- function(libname,pkgname){
	require(methods)
	browser()
	jloc = system.file("java",package="farmLP")
	#	  jpars=c("-Xmx500m",paste(c("-Djava.library.path=",jloc),collapse=""))
	#	print(jpars)
	#	.jinit(parameters=jpars) # this starts the JVM
	#	.jpackage(pkgname,jars="*",nativeLibrary=FALSE)
	name=pkgname
	jars="*"
	if (!rJava:::.jniInitialized) .jinit()
	classes <- system.file("java", package=name, lib.loc=NULL)
	if (nchar(classes)) {
		rJava:::.jaddClassPath(classes)
		if (length(jars)) {
			if (length(jars)==1 && jars=='*') {
				jars <- grep(".*\\.jar",list.files(classes,full.names=TRUE),TRUE,value=TRUE)
				if (length(jars)) rJava:::.jaddClassPath(jars)
				} else rJava:::.jaddClassPath(paste(classes,jars,sep=.Platform$file.sep))
			}
		}

	#	jniloc <- system.file("jnilibs", package=pkgname)
	#	print(jniloc)
	path=paste(c(jloc,"libfarmLP.jnilib"),collapse="/")
	rJava:::.jaddLibrary(name, lib)
#	.jcall(rJava:::.rJava.class.loader, "V", "addRLibrary", as.character(libname)[1], as.character(path)[1])

#	print(.jaddLibrary(paste(c(jloc,"libfarmLP.jnilib"),collapse="/"),TRUE))
#	print(.jaddLibrary("libfarmLP.jnlib",jloc))
}

setClass("FarmRepresentation")
setClass("Farm",representation(model="jobjRef",cropNames="vector"),prototype(model=.jnull("jfm/r/SimpleFarmRepresentation")),contains="FarmRepresentation")
setClass("CompositeFarm",representation(model="jobjRef",cropNames="vector"),prototype(model=.jnull("jfm/r/CompositeFarmRepresentation")),contains="FarmRepresentation")

