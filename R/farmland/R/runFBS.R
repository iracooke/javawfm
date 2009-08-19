
fbsCropNames <- function(){
  return(c("winterwheat","springwheat","winterbarley","springbarley","warepotatoes","sugarbeet","wosr","sosr","linseed","driedpeas","feedbeans","feedpeas","setaside"))
}

jfmCropNames <- function(){
  return(c("winterwheat","springwheat","winterbarley","springbarley","warepotatoes","sugarbeet","wosr","sosr","linseed","driedpeas","springbeans","winterbeans","setaside"))
}

modelCropnameToFBSCropname <- function(cn){
  jfmNames=jfmCropNames()
  fbsNames=fbsCropNames()
  i=which(jfmNames==cn)
  fbsNames[i]
}

modellableAreaFBS <- function(fbsdatum,farm){
  cropXMLNames=cropNames(farm)
  cropXMLNames=cropXMLNames[-which(cropXMLNames=="setaside")]
  cropNames=sapply(1:length(cropXMLNames),function(x) modelCropnameToFBSCropname(cropXMLNames[x]))
  cropNames=toupper(cropNames)
  a=sapply(1:length(cropNames), function(c) get.var(paste(c(cropNames[c],"area"),collapse="."),fbsdatum))
  a=sum(a)+fbsdatum$areaSetAside
  a
}

modelvsactualFBS <- function(farm,data){ 
  cropXMLNames=cropNames(farm)
  cropXMLNames=cropXMLNames[-which(cropXMLNames=="setaside")]
  pairedData=list()
  ncrops=length(cropXMLNames)
  eotot=0
  for( c in 1:ncrops){
#    browser()
    fbsCName=toupper(modelCropnameToFBSCropname(cropXMLNames[c]))
    actual=get.var(paste(c(fbsCName,"area"),collapse="."),data)*(250/data$modellableArea)
    eotot=eotot+get.var(paste(c(fbsCName,"eo"),collapse="."),data)*(250/data$modellableArea)
    simulated=get.var(cropXMLNames[c],data)
    pairedData[[c]]=data.frame(simulated,actual)
  }
  

  actual = data$areaSetAside*(250/data$modellableArea) #Setaside
  simulated = get.var("setaside",data)
  pairedData[[ncrops+1]]=data.frame(simulated,actual)
  actual=data$totalmaineo-data$totalLabourCost
  simulated=data$profit
  pairedData[[ncrops+2]]=data.frame(simulated,actual)

  actual=eotot
  simulated=data$eo
  pairedData[[ncrops+3]]=data.frame(simulated,actual)
  
  pairedNames=c(cropXMLNames,"setaside","profit","eo")
  names(pairedData) <- pairedNames
  pairedData
}


makePairedResults <- function(pairedResults,groupLegumes=TRUE,groupBarley=FALSE,includeSB=TRUE){ # Useful summarizer for plotting
  legumeTitles=intersect(c("winterbeans","driedpeas","springbeans"),names(pairedResults))
  print(legumeTitles)
  legumes=pairedResults[legumeTitles][[1]]  
  for(l in 2:length(legumeTitles)){
    legumes=pairedResults[[l]]+legumes
  }
  barley=data.frame(pairedResults[["springbarley"]]+pairedResults[["winterbarley"]])

  titles=intersect(c("winterwheat","wosr","sugarbeet","setaside","warepotatoes","eo"),names(pairedResults))
  if ( !includeSB){
    titles=titles[-which(titles=="sugarbeet")]
  }
  
  if ( !groupBarley ){
    titles=append(titles,c("winterbarley","springbarley"))
  }
  if ( !groupLegumes ){
    titles=append(titles,legumeTitles)
  }
  pairedResults=pairedResults[titles]

  if (groupLegumes){
    pairedResults[[length(pairedResults)+1]]=legumes
    titles=c(titles,"legumes")
  }
  if ( groupBarley){
    pairedResults[[length(pairedResults)+1]]=barley
    titles=c(titles,"barley")
  }
  names(pairedResults) <- titles
  pairedResults
}


runFBS <- function(profit,farmdata,fbstranslate,...,mou=NULL,mouweights=c(1.0),nfert=0.37,pfert=0.26,kfert=0.20,progress=TRUE){
  output=NULL
  unsolved=c()
  for( i in 1:length(farmdata$RF)){
    farm=CompositeFarm(profit,mou,mouweights,farmdata[i,])
    setInputCost(farm,"N fertiliser",as.double(nfert))
    setInputCost(farm,"P fertiliser",as.double(pfert))
    setInputCost(farm,"K fertiliser",as.double(kfert))
    
    fbstranslate(farm,farmdata[i,],...)
    
    farm=solvefm(farm)
    if ( is.null(output)){
      output=data.frame(farm)
    } else {
      output=data.frame(output,farm)
    }
    if ( progress ){
      printProgress(i,farm)
    }
    if ( !isSolved(farm)){
      unsolved=append(unsolved,i)
    }
    rm(farm)
    gc()
  }

  if ( length(unsolved) > 0 ){
    cat("There were ",length(unsolved),"unsolved farms \n")
    print(unsolved)
#    farmdata=farmdata[-unsolved,]
  }
#  output=data.frame(t(output),farmdata)
  output=data.frame(t(output))
  output= list(output,unsolved)
  output
}
