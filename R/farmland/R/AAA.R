
#.onAttach <- function(libname, pkgname) {
#  print("Attaching")#
#	if( !require(methods) ) stop("we require methods for package farmland") 
#	where <- match(paste("package:", pkgname, sep=""), search()) 
#	.initfarmland(where)
#} 

.onLoad <- function(libname,pkgname){
  require(methods)
  print("Loading")
  .jinit(parameters="-Xmx500m") # this starts the JVM
  .jpackage(pkgname)

}

setClass("FarmRepresentation")
setClass("Farm",representation(model="jobjRef",cropNames="vector"),prototype(model=.jnull("jfm/r/SimpleFarmRepresentation")),contains="FarmRepresentation")
setClass("CompositeFarm",representation(model="jobjRef",cropNames="vector"),prototype(model=.jnull("jfm/r/CompositeFarmRepresentation")),contains="FarmRepresentation")

