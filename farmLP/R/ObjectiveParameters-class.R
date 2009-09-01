# Implementation of the FarmParameters class #
#
####################################


################### Constructor #######################
# Create a new FarmParameters object 
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



