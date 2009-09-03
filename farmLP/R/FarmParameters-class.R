# Implementation of the FarmParameters class #
#
####################################


################### Constructor #######################
# Create a new FarmParameters object 
#######################################################
FarmParameters <- function(file=defaultArableFarmParameters()) { 
	FarmParameters=new("FarmParameters")
	if (class(file) == "jobjRef"){
		FarmParameters@document=file
	} else {
		if ( !is.character(file)){
			stop("file argument must be a full path or a document object ref")
		}
		FarmParameters@document=parseXMLDocument(file)
	}
	return(FarmParameters)
}

if (!isGeneric("cropNames")){
  setGeneric("cropNames", function(object) standardGeneric("cropNames"))
}
setMethod("cropNames", "FarmParameters", function(object) 
  .jcall("jfm/r/FarmDocumentEditor","[Ljava/lang/String;","cropNames",document(object))
)

### Set the solver type glpk or cbc #####
#
#########################################
setSolverType<-function(params,solver){
	.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setAttributeOfTagFilteredByAttribute",document(params),"farm",as.character(solver),"nil","nil","solver")
}

#### Set absolute values for prices and subsidies ###
#
#####################################################
setPriceForCrop<-function(params,newPrice,cropName){
	.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setPriceForCrop",document(params),as.double(newPrice),cropName)
    return(params)
}

setSubsidyForCrop <- function(params,newSubsidy,cropName){
	.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setSubsidyForCrop",document(params),as.double(newSubsidy),cropName)
  return(params)
}



#### Functions for setting relative prices, rotation penalties, labour requirements and yields ###
#
#########################################################################################
setRelativePriceForCrop <- function(params,cropName,val){ .jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setRelativePriceForCrop",document(params),as.double(val),cropName)
  params
}

setRelativeRotationPenalties <- function(params,val){  .jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setRelativeRotationPenalties",document(params),as.double(val))
params
}

setRelativeLabourRequirements <- function(params,val){  .jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setRelativeWorkrateFormulas",document(params),as.double(val))
  params
}

setRelativeYieldForCrop <- function(params,cropName,val){
	.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setMultiplierOnYieldFormulaForCrop",document(params),as.double(val),cropName)
  params
}


###### Set farming costs relative to the default #####
#
######################################################
setRelativeCost<-function(params,value,costType="Input",inputName="N fertiliser"){
switch(costType,
	Input=setRelativeCostForInput(params,inputName,value),
	Machinery=setRelativeMachineryCost(params,value),
	Fuel=setRelativeFuelCost(params,value),
	Labour=setRelativeLabourCost(params,value),
	AreaSubsidy=setRelativeAreaSubsidy(params,value),
	stop("costType unknown"))
}


#### Internal functions used by setRelativeCost ######
#
######################################################
setRelativeCostForInput <- function(params,inputName,val){
	.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setRelativeAttributeOfTagFilteredByAttribute",document(params),"input",as.double(val),"type",inputName,"unitCost")
	params
}

setRelativeMachineryCost <- function(params,val){
	.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setRelativeAttributeOfTagFilteredByAttribute",document(params),"machine",as.double(val),"nil","nil","cost")
  params
}

setRelativeFuelCost <- function(params,val){
	.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setRelativeAttributeOfTagFilteredByAttribute",document(params),"farm",as.double(val),"nil","nil","fuelprice")
  params
}




setRelativeLabourCost <- function(params,val){
	.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setRelativeAttributeOfTagFilteredByAttribute",document(params),"labour",as.double(val),"nil","nil","cost")
  params
}

setRelativeAreaSubsidy <- function(params,val){
	.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setRelativeAttributeOfTagFilteredByAttribute",document(params),"crop",as.double(val),"nil","nil","subsidy")
	params
}



