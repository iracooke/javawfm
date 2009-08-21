

doc.cropNames <- function(docObject){
  .jcall("jfm/r/FarmDocumentEditor","[Ljava/lang/String;","cropNames",docObject)
}