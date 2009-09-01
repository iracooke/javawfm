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


#### Set absolute values for prices and subsidies ###
#
#####################################################
setPriceForCrop<-function(doc,newPrice,cropName){
    doc=.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setPriceForCrop",doc,as.double(newPrice),cropName)
    return(doc)
}

setSubsidyForCrop <- function(doc,newSubsidy,cropName){
  doc=.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setSubsidyForCrop",doc,as.double(newSubsidy),cropName)
  return(doc)
}


#### Functions for setting relative prices, rotation penalties, labour requirements and yields ###
#
#########################################################################################
setRelativePriceForCrop <- function(profitDoc,cropName,val){ profitDoc=.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setRelativePriceForCrop",profitDoc,as.double(val),cropName)
  profitDoc
}

setRelativeRotationPenalties <- function(profitDoc,val){  profitDoc=.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setRelativeRotationPenalties",profitDoc,as.double(val))
  profitDoc
}

setRelativeLabourRequirements <- function(profitDoc,val){  profitDoc=.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setRelativeWorkrateFormulas",profitDoc,as.double(val))
  profitDoc
}

setRelativeYieldForCrop <- function(profitDoc,cropName,val){
 profitDoc=.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setMultiplierOnYieldFormulaForCrop",profitDoc,as.double(val),cropName)
  profitDoc
}


###### Set farming costs relative to the default #####
#
######################################################
setRelativeCost<-function(document,value,costType="Input",inputName="N fertiliser"){
switch(name,
	Input=setRelativeCostForInput(document,inputName,value),
	Machinery=setRelativeMachineryCost(document,value),
	Fuel=setRelativeFuelCost(document,value),
	Labour=setRelativeLabourCost(document,value),
	AreaSubsidy=setRelativeAreaSubsidy(document,value),
	stop("costType unknown"))
}


#### Internal functions used by setRelativeCost ######
#
######################################################
setRelativeCostForInput <- function(profitDoc,inputName,val){
	profitDoc=.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setRelativeAttributeOfTagFilteredByAttribute",profitDoc,"input",as.double(val),"type",inputName,"unitCost")
	profitDoc
}

setRelativeMachineryCost <- function(profitDoc,val){
  profitDoc=.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setRelativeAttributeOfTagFilteredByAttribute",profitDoc,"machine",as.double(val),"nil","nil","cost")
  profitDoc
}

setRelativeFuelCost <- function(profitDoc,val){
  profitDoc=.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setRelativeAttributeOfTagFilteredByAttribute",profitDoc,"farm",as.double(val),"nil","nil","fuelprice")
  profitDoc
}
setRelativeLabourCost <- function(profitDoc,val){
 profitDoc=.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setRelativeAttributeOfTagFilteredByAttribute",profitDoc,"labour",as.double(val),"nil","nil","cost")
  profitDoc
}

setRelativeAreaSubsidy <- function(profitDoc,val){
  profitDoc=.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setRelativeAttributeOfTagFilteredByAttribute",profitDoc,"crop",as.double(val),"nil","nil","subsidy")
  profitDoc                       
}



