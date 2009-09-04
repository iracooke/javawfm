# Implementation of the ObjectiveParameters class #
#
####################################


################### Constructor #######################
# Create a new ObjectiveParameters object 
#######################################################
ObjectiveParameters <- function(file=defaultArableObjectiveParameters()) { 
	ObjectiveParameters=new("ObjectiveParameters")
	if (class(file) == "jobjRef"){
		ObjectiveParameters@document=file
	} else {
		if ( !is.character(file)){
			stop("file argument must be a full path or a document object ref")
		}
		ObjectiveParameters@document=parseXMLDocument(file)
	}
	return(ObjectiveParameters)
}

# Extracting the names of objectives
if (!isGeneric("objectiveNames")){
  setGeneric("objectiveNames", function(object) standardGeneric("objectiveNames"))
}
setMethod("objectiveNames","ObjectiveParameters",function(object){
	.jcall("jfm/r/MOUDocumentEditor","[S","objectiveNames",document(object))
})

setRisk<-function(params,value){
	.jcall("jfm/r/MOUDocumentEditor","Lorg/w3c/dom/Document;","setRisk",document(params),as.double(value))
    return(params)
}

setWeightForObjective <- function(params,objective,value){
	.jcall("jfm/r/MOUDocumentEditor","Lorg/w3c/dom/Document;","setObjectiveUAttribute",document(params),objective,"weight",as.character(value))
  return(params)
}

getWeightForObjective<-function(params,objective){
	return(.jcall("jfm/r/MOUDocumentEditor","S","getObjectiveUAttribute",document(params),objective,"weight"))
}

setCropComplexity <- function(params,wtval,xstring){
	.jcall("jfm/r/MOUDocumentEditor","Lorg/w3c/dom/Document;","setCropComplexity",document(params),as.double(wtval),xstring)
  return(params)
}

