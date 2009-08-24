
.onLoad <- function(libname,pkgname){
  require(methods)

debug()
#  jloc = system.file("java",package="farmLP")
#  jpars=c("-Xmx500m",paste(c("-Djava.library.path=",jloc),collapse=""))
#  .jinit(parameters=jpars) # this starts the JVM
  .jpackage(pkgname,jars="*",nativeLibrary=TRUE)
}

setClass("FarmRepresentation")
setClass("Farm",representation(model="jobjRef",cropNames="vector"),prototype(model=.jnull("jfm/r/SimpleFarmRepresentation")),contains="FarmRepresentation")
setClass("CompositeFarm",representation(model="jobjRef",cropNames="vector"),prototype(model=.jnull("jfm/r/CompositeFarmRepresentation")),contains="FarmRepresentation")

