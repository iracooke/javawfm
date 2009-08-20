parseXMLDocument<-function(filename){
    doc=.jcall("jfm/model/Farm","Lorg/w3c/dom/Document;","parseDocument",filename)
    return(doc)
}

setRiskInMOUDocument<-function(doc,value){
    doc=.jcall("jfm/r/MOUDocumentEditor","Lorg/w3c/dom/Document;","setRisk",doc,as.double(value))
    return(doc)
}

setWeightInMOUDocument <- function(doc,objective,value){
  doc=.jcall("jfm/r/MOUDocumentEditor","Lorg/w3c/dom/Document;","setObjectiveUAttribute",doc,objective,"weight",as.character(value))
  return(doc)
}

setCropComplexityInMOUDocument <- function(doc,wtval,xstring){
  doc=.jcall("jfm/r/MOUDocumentEditor","Lorg/w3c/dom/Document;","setCropComplexity",doc,as.double(wtval),xstring)
  return(doc)
}

setPriceInProfitDocument<-function(doc,newPrice,cropName){
    doc=.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setPriceForCrop",doc,as.double(newPrice),cropName)
    return(doc)
}

#setPricesInProfitDocument<-function(doc,priceVector){#
#	cropNames=names(priceVector)
#	for( c in 1:length(cropNames)){
#		setPriceInProfitDocument(doc,priceVector[c],cropNames[c])
#	}
#	doc
#}#

setPricesInProfitDocument <- function(doc,pricesVector){
  jfmCropNames=.jcall("jfm/r/FarmDocumentEditor","[Ljava/lang/String;","cropNames",doc)
  for( c in 1:length(jfmCropNames)){
    fbsCName=modelCropnameToFBSCropname(jfmCropNames[c])
    sval=get.var(fbsCName,pricesVector)
    setPriceInProfitDocument(doc,sval,jfmCropNames[c])
  }  
  doc  
}

setSubsidyInProfitDocument <- function(doc,newSubsidy,cropName){
  doc=.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setSubsidyForCrop",doc,as.double(newSubsidy),cropName)
  return(doc)
}

#setSubsidiesInProfitDocument<-function(doc,subsidyVector){#
#	cropNames=names(subsidyVector)
#	for( c in 1:length(cropNames)){
#		setSubsidyInProfitDocument(doc,subsidyVector[c],cropNames[c])
#	}
#	doc
#}##



setSubsidiesInProfitDocument <- function(doc,subsidyVector){
  jfmCropNames=.jcall("jfm/r/FarmDocumentEditor","[Ljava/lang/String;","cropNames",doc)
  for( c in 1:length(jfmCropNames)){
    fbsCName=modelCropnameToFBSCropname(jfmCropNames[c])
    sval=get.var(fbsCName,subsidyVector)
    setSubsidyInProfitDocument(doc,sval,jfmCropNames[c])
  }  
  doc  
}
