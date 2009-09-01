
######## Generic Functions ######
#      
#################################

# Methods for extracting slots from FarmRepresentation objects #
#
################################################################
if (!isGeneric("model")){
   setGeneric("model", function(farm) standardGeneric("model"))
}
setMethod("model", "FarmRepresentation", function(farm) farm@model)

if (!isGeneric("cropNames")){
  setGeneric("cropNames", function(object) standardGeneric("cropNames"))
}
setMethod("cropNames", "FarmRepresentation", function(object) object@cropNames)


# Methods for displaying FarmRepresentation objects #
#
################################################################
if (!isGeneric("show")){
	setGeneric("show", function(object) standardGeneric("show"))
}
setMethod("show","FarmRepresentation",function(object){
	cat(.jcall(model(object),"Ljava/lang/String;","solutionSummary"),"\n")
})

if (!isGeneric("guts")){
	setGeneric("guts", function(object) standardGeneric("guts"))
}
setMethod("guts","FarmRepresentation",function(object){
		cat(.jcall(model(object),"S","toString"),"\n")
})



# Getting state information about FarmRepresentation objects #
#
##############################################################
if (!isGeneric("isSolved")){
	setGeneric("isSolved", function(farm) standardGeneric("isSolved"))   
}
setMethod("isSolved","FarmRepresentation",function(farm)
{
	.jcall(model(farm),"I","isSolved")    
})


# Getting information from solved farm objects #
#
################################################

# Extracting Profit #
if (!isGeneric("profit")){
	setGeneric("profit",function(farm) standardGeneric("profit"))
}
setMethod("profit","FarmRepresentation",function(farm){
	if ( !isSolved(farm) ){
		return(-1)
		#    warning("Can only calculate profit for solved farms. Call solveFarm first")
	}
	.jcall(model(farm),"D","profit")
})

# Extracting enterprise output
if (!isGeneric("eo")){
	setGeneric("eo", function(farm) standardGeneric("eo"))
}
setMethod("eo","FarmRepresentation",function(farm){
	if ( !isSolved(farm) ){
		return(-1)
		#    warning("Can only calculate eo for solved farms. Call solveFarm first")
	}
	.jcall(model(farm),"D","eo")
})

# Extracting crop area
if (!isGeneric("cropArea")){
  setGeneric("cropArea", function(farm,cropName) standardGeneric("cropArea"))
}
setMethod("cropArea",signature(farm="FarmRepresentation",cropName="character"),function(farm,cropName){
	if ( !isSolved(farm)){
		return(-1)
		#    warning("Can only calculate crop areas for solved farms. Call solveFarm first")
	}
	.jcall(model(farm),"D","areaOfCropNamed",cropName)
})

# Extracting crop prices
if (!isGeneric("cropPrice")){
	setGeneric("cropPrice", function(farm,cropName) standardGeneric("cropPrice"))
}
setMethod("cropPrice",signature(farm="FarmRepresentation",cropName="character"),function(farm,cropName){
	if ( !isSolved(farm)){
		return(-1)
		#    warning("Can only calculate crop prices for solved farms. Call solveFarm first")
	}
	.jcall(model(farm),"D","priceOfCropNamed",cropName)
})

# Extracting crop yields
if (!isGeneric("cropYield")){
  setGeneric("cropYield", function(farm,cropName) standardGeneric("cropYield"))
}
setMethod("cropYield",signature(farm="FarmRepresentation",cropName="character"),function(farm,cropName){
	if ( !isSolved(farm)){
		return(-1)
		#    warning("Can only calculate crop prices for solved farms. Call solveFarm first")
	}
	.jcall(model(farm),"D","yieldOfCropNamed",cropName)
})

# Extracting the names of objectives
if (!isGeneric("objectiveNames")){
  setGeneric("objectiveNames", function(farm) standardGeneric("objectiveNames"))
}
setMethod("objectiveNames","FarmRepresentation",function(farm){
	.jcall(model(farm),"[Ljava/lang/String;","objectiveNames")
})

#Extracting the solved values of objectives
if (!isGeneric("objectiveValues")){
	setGeneric("objectiveValues", function(farm) standardGeneric("objectiveValues"))
}
setMethod("objectiveValues","FarmRepresentation",function(farm){
	.jcall(model(farm),"[D","objectiveValues")
})

# Extracting scale factors used for objectives
if (!isGeneric("objectiveScaleFactors")){
	setGeneric("objectiveScaleFactors", function(farm) standardGeneric("objectiveScaleFactors"))
}
setMethod("objectiveScaleFactors","FarmRepresentation",			function(farm){
	.jcall(model(farm),"[D","objectiveScaleFactors")
})


# Functions for setting specific parameter values on FarmRepresentation objects #
#
#################################################################################

#Setting costs for named input types
if (!isGeneric("setInputCost")){
	setGeneric("setInputCost", function(farm,inputName,inputCost) standardGeneric("setInputCost"))
}
setMethod("setInputCost",signature(farm="FarmRepresentation",inputName="character",inputCost="numeric"),			function(farm,inputName,inputCost){
	.jcall(model(farm),"V","setInputCost",inputName,inputCost)
})


# Coercion from FarmRepresentation and subclasses into data frames #
#
####################################################################
 
as.data.frame.FarmRepresentation = function(x, row.names, optional, ...) {
  farm=c()
  nms=c()
  cropAreas=sapply(1:length(cropNames(x)),function(i) cropArea(x,cropNames(x)[i]) )
  cropPrices=sapply(1:length(cropNames(x)),function(i) cropPrice(x,cropNames(x)[i]))
  cropYields=sapply(1:length(cropNames(x)),function(i) cropYield(x,cropNames(x)[i]))
  farm=append(c(profit(x),eo(x)),cropAreas)
  farm=append(farm,cropPrices)
  farm=append(farm,cropYields)
  nms=append(c("profit","eo"),cropNames(x))
  nms=append(nms,sapply(cropNames(x),function(i) paste(c(i,"simprice"),collapse=".")))
  nms=append(nms,sapply(cropNames(x),function(i) paste(c(i,"simyield"),collapse=".")))
  names(farm) <- nms
  df=data.frame(farm)
  df
}

as.data.frame.CompositeFarm = function(x, row.names, optional, ...) {
  as.data.frame.FarmRepresentation(x,row.names,optional, ... )
}


as.data.frame.Farm = function(x, row.names, optional, ...) {
  as.data.frame.FarmRepresentation(x,row.names,optional, ... )
}

setAs("Farm","data.frame",function(from) as.data.frame.FarmRepresentation(from) )
setAs("CompositeFarm","data.frame",function(from) as.data.frame.FarmRepresentation(from) )
setAs("FarmRepresentation","data.frame",function(from) as.data.frame.FarmRepresentation(from) )



# Generic solvelp function for Farm objects #
# 
# Unfortunately I can't seem to figure out how to completely suppress output from Cbc here
# so making many calls to solvelp can produce alot of junk output.
###########################################

if (!isGeneric("solvelp")){
	setGeneric("solvelp",function(farm,dumpFail="") standardGeneric("solvelp"))
}
setMethod("solvelp",signature(farm = "FarmRepresentation"),function (farm,dumpFail="") 
{
	.jcall(model(farm),"I","solve",dumpFail);
})
