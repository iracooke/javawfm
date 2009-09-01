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

setRisk<-function(doc,value){
    doc=.jcall("jfm/r/MOUDocumentEditor","Lorg/w3c/dom/Document;","setRisk",doc,as.double(value))
    return(doc)
}

setWeightForObjective <- function(doc,objective,value){
  doc=.jcall("jfm/r/MOUDocumentEditor","Lorg/w3c/dom/Document;","setObjectiveUAttribute",doc,objective,"weight",as.character(value))
  return(doc)
}

setCropComplexity <- function(doc,wtval,xstring){
  doc=.jcall("jfm/r/MOUDocumentEditor","Lorg/w3c/dom/Document;","setCropComplexity",doc,as.double(wtval),xstring)
  return(doc)
}

