

Farm <- function(econ) { # TODO: Type checks for arguments
  farm=new("Farm")
  farm@model=.jnew("jfm/r/SimpleFarmRepresentation",econ)
  farm@cropNames=.jcall(farm@model,"[Ljava/lang/String;","cropNames")   
  farm
}

# For Setting Parameter values on the farm object
set.mou <- function(farm,mou){
  .jcall(model(farm),"V","createAndApplyMOU",mou)
  farm
}

set.sugarbeet <- function(sfarm,coords,SBFactories,haulagePerTonnePerKm,maxSBHaulageDistance){
  sugarDists=spDistsN1(coordinates(SBFactories),coords,longlat=TRUE)
  minDist=min(sugarDists)
  if ( minDist > maxSBHaulageDistance ){
    haulagePerTonnePerKm=10000
  }
  .jcall(model(sfarm),"V","setDistanceFromSugarbeetFactory",minDist,haulagePerTonnePerKm)
}

if (!isGeneric("set")){
  setGeneric("set", function(farm,input,...) standardGeneric("set"))
#  setGeneric("set", function(farm,SBFactories,haulage,maxhaulage) standardGeneric("set"))
}
setMethod("set", signature(farm="Farm",input="jobjRef"), function(farm,input) set.mou(farm,input)) #objRef should be an XML document ref
setMethod("set", signature(farm="Farm",input="character"), function(farm,input) set.mou(farm,input))


solve.farm <- function(farm){
  .jcall(model(farm),"I","solve")
  farm
}
if (!isGeneric("solvefm"))
  setGeneric("solvefm", function(farm) standardGeneric("solvefm"))   
setMethod("solvefm","FarmRepresentation",function(farm) solve.farm(farm))

isSolved.farm<-function(farm){
  .jcall(model(farm),"I","isSolved")    
}
if (!isGeneric("isSolved"))
  setGeneric("isSolved", function(farm) standardGeneric("isSolved"))   
setMethod("isSolved","FarmRepresentation",function(farm) isSolved.farm(farm))
