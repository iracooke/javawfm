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