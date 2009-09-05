# Implementation of the Farm class #
#
####################################


################### Constructor #######################
# Create a new Farm object from its economic parameters
#######################################################
Farm <- function(farm.params=defaultArableFarmParameters(),obj.params=NULL) { 
	farm=new("Farm")
	if ( class(farm.params)=="FarmParameters"){
		farm@model=.jnew("jfm/r/SimpleFarmRepresentation",document(farm.params))
	} else {
		farm@model=.jnew("jfm/r/SimpleFarmRepresentation",farm.params)
	}
	
	if ( !is.null(obj.params)){
		set(farm,obj.params)
	}
	
	farm@cropNames=.jcall(farm@model,"[Ljava/lang/String;","cropNames")   
	farm
}

####### Functions for setting particular parameters #####
#
#########################################################
# Set the multi objective preferences for a farm object. #
set.mou <- function(farm,mou){
	if ( class(mou)=="ObjectiveParameters"){
		.jcall(model(farm),"V","createAndApplyMOU",document(mou))
	} else {
		.jcall(model(farm),"V","createAndApplyMOU",mou)
	}
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
setMethod("set", signature(farm="Farm",input="ObjectiveParameters"), function(farm,input) set.mou(farm,input)) 
setMethod("set", signature(farm="Farm",input="character"), function(farm,input) set.mou(farm,input))

