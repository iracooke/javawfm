
setRelativeCostForInput <- function(profitDoc,inputName,val){
  profitDoc=.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setRelativeAttributeOfTagFilteredByAttribute",profitDoc,"input",as.double(val),"type",inputName,"unitCost")
  profitDoc
}

setRelativeFertilizerCost <- function(profitDoc,val){
  profitDoc=setRelativeCostForInput(profitDoc,"N fertiliser",val)
  profitDoc=setRelativeCostForInput(profitDoc,"P fertiliser",val)
  profitDoc=setRelativeCostForInput(profitDoc,"K fertiliser",val)
  profitDoc
}
setRelativeSeedCost <- function(profitDoc,val){
  profitDoc=setRelativeCostForInput(profitDoc,"Seed Amount of crop",val)
  profitDoc
}
setRelativePesticideCost <- function(profitDoc,val){
  profitDoc=setRelativeCostForInput(profitDoc,"BGHerbicide",val)
  profitDoc=setRelativeCostForInput(profitDoc,"WOHerbicide",val)
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
  profitDoc                             # Several other types ? Why?
}



setRelativePriceForCrop <- function(profitDoc,cropName,val){
  profitDoc=.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setRelativePriceForCrop",profitDoc,as.double(val),cropName)
  profitDoc
}

setRelativeCerealPrice <- function(profitDoc,val){
  cereals=c("winterwheat","winterbarley","springbarley")
  for(cer in cereals){
    profitDoc=setRelativePriceForCrop(profitDoc,cer,val)
  }
  profitDoc
}

setRelativeOilseedsPrice <- function(profitDoc,val){
  setRelativePriceForCrop(profitDoc,"wosr",val)
}

setRelativeRootsPrice <- function(profitDoc,val){
  setRelativePriceForCrop(profitDoc,"warepotatoes",val)
  setRelativePriceForCrop(profitDoc,"sugarbeet",val)
}

setRelativeProteinPrice <- function(profitDoc,val){
  proteins=intersect(c("winterbeans","springbeans","driedpeas"),doc.cropNames(profitDoc))
  for(leg in proteins){
    profitDoc=setRelativePriceForCrop(profitDoc,leg,val)
  }
  profitDoc
}

setRelativeRotationPenalties <- function(profitDoc,val){
  profitDoc=.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setRelativeRotationPenalties",profitDoc,as.double(val))
  profitDoc
}
setRelativeLabourRequirements <- function(profitDoc,val){
  profitDoc=.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setRelativeWorkrateFormulas",profitDoc,as.double(val))
  profitDoc
}



setRelativeYieldForCrop <- function(profitDoc,cropName,val){
  profitDoc=.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setMultiplierOnYieldFormulaForCrop",profitDoc,as.double(val),cropName)
  profitDoc
}