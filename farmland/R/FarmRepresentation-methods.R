

if (!isGeneric("model"))
  setGeneric("model", function(farm) standardGeneric("model"))
setMethod("model", "FarmRepresentation", function(farm) farm@model)

if (!isGeneric("cropNames"))
  setGeneric("cropNames", function(farm) standardGeneric("cropNames"))
setMethod("cropNames", "FarmRepresentation", function(farm) farm@cropNames)

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


profit.farm<-function(farm){
  if ( !isSolved(farm) ){
    return(-1)
#    warning("Can only calculate profit for solved farms. Call solveFarm first")
  }
  .jcall(model(farm),"D","profit")
}

if (!isGeneric("profit"))
  setGeneric("profit", function(farm)
             standardGeneric("profit"))
setMethod("profit","FarmRepresentation",profit.farm)

eo.farm<-function(farm){
  if ( !isSolved(farm) ){
    return(-1)
#    warning("Can only calculate eo for solved farms. Call solveFarm first")
  }
  .jcall(model(farm),"D","eo")
}

if (!isGeneric("eo"))
  setGeneric("eo", function(farm)
             standardGeneric("eo"))
setMethod("eo","FarmRepresentation",eo.farm)

cropArea.default <- function(farm,cropName){
  if ( !isSolved(farm)){
    return(-1)
#    warning("Can only calculate crop areas for solved farms. Call solveFarm first")
  }
  .jcall(model(farm),"D","areaOfCropNamed",cropName)
}
if (!isGeneric("cropArea"))
  setGeneric("cropArea", function(farm,cropName)
             standardGeneric("cropArea"))
setMethod("cropArea","FarmRepresentation",cropArea.default)


objectiveNames.default <- function(farm){
  .jcall(model(farm),"[Ljava/lang/String;","objectiveNames")
}
if (!isGeneric("objectiveNames"))
  setGeneric("objectiveNames", function(farm)
             standardGeneric("objectiveNames"))
setMethod("objectiveNames","FarmRepresentation",objectiveNames.default)

objectiveValues.default <- function(farm){
  .jcall(model(farm),"[D","objectiveValues")
}
if (!isGeneric("objectiveValues"))
  setGeneric("objectiveValues", function(farm)
             standardGeneric("objectiveValues"))
setMethod("objectiveValues","FarmRepresentation",objectiveValues.default)


objectiveScaleFactors.default <- function(farm){
  .jcall(model(farm),"[D","objectiveScaleFactors")
}
if (!isGeneric("objectiveScaleFactors"))
  setGeneric("objectiveScaleFactors", function(farm)
             standardGeneric("objectiveScaleFactors"))
setMethod("objectiveScaleFactors","FarmRepresentation",objectiveScaleFactors.default)

setInputCost.default<-function(farm,inputName,inputCost){
    .jcall(model(farm),"V","setInputCost",inputName,inputCost)
}
if (!isGeneric("setInputCost"))
  setGeneric("setInputCost", function(farm,inputName,inputCost)
             standardGeneric("setInputCost"))
setMethod("setInputCost","FarmRepresentation",setInputCost.default)



#as.data.frame.FarmRepresentation = function(x, row.names, optional, ...) {
#  data.frame(profit(x))
#}

as.data.frame.FarmRepresentation = function(x, row.names, optional, ...) {
  farm=c()
  nms=c()
  cropAreas=sapply(1:length(cropNames(x)),function(i) cropArea(x,cropNames(x)[i]) )
  farm=append(c(profit(x),eo(x)),cropAreas)
  nms=append(c("profit","eo"),cropNames(x))
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




