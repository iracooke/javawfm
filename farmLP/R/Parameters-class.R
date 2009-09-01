# Abstract Parameters class #
#
# Defines generics that apply to all subclasses
####################################

if (!isGeneric("document")){
   setGeneric("document", function(params) standardGeneric("document"))
}
setMethod("document", "Parameters", function(params) params@document)


if (!isGeneric("show")){
   setGeneric("show", function(object) standardGeneric("show"))
}
setMethod("show", "Parameters", function(object) 
cat(.jcall("jfm/r/FarmDocumentEditor","S","dump",document(object)),"\n")
)