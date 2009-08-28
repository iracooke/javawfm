# Implementation of the Farm class #
#
####################################


################### Constructor #######################
# Create a new Farm object from its economic parameters
#######################################################
Farm <- function(econ) { 
  farm=new("Farm")
  farm@model=.jnew("jfm/r/SimpleFarmRepresentation",econ)
  farm@cropNames=.jcall(farm@model,"[Ljava/lang/String;","cropNames")   
  farm
}

####### Functions for setting particular parameters #####
#
#########################################################
# Set the multi objective preferences for a farm object. #
set.mou <- function(farm,mou){
  .jcall(model(farm),"V","createAndApplyMOU",mou)
  farm
}

# Set input variables required for sugarbeet production #
set.sugarbeet <- function(sfarm,coords,SBFactories,haulagePerTonnePerKm,maxSBHaulageDistance){

  sugarDists=spDistsN1(coordinates(SBFactories),coords,longlat=TRUE)
  minDist=min(sugarDists)
  if ( minDist > maxSBHaulageDistance ){
    haulagePerTonnePerKm=10000
  }
  .jcall(model(sfarm),"V","setDistanceFromSugarbeetFactory",minDist,haulagePerTonnePerKm)
}

# Make the set.mou function into a generic so it can take a jobjRef or a character arg
if (!isGeneric("set")){
  setGeneric("set", function(farm,input,...) standardGeneric("set"))
}
setMethod("set", signature(farm="Farm",input="jobjRef"), function(farm,input) set.mou(farm,input)) 
setMethod("set", signature(farm="Farm",input="character"), function(farm,input) set.mou(farm,input))

