
printProgress<-function(i,farm){
	cnames=cropNames(farm)
	cat(i," --- ")
	 for(c in 1:length(cnames)){
		ca=format(cropArea(farm,cnames[c]),digits=2)
	 	cat(ca," ")
	 }
	 cat("\n")
}

modellableAreaAgCensus <- function(agcdatum,farm){ # Returns modellable area for a 5x5km square relative to a 250Ha farm
  cropXMLNames=cropNames(farm)
  cropNames=sapply(1:length(cropXMLNames),function(x) modelCropnameToFBSCropname(cropXMLNames[x]))

  cropNames=toupper(cropNames)
  a=sapply(1:length(cropNames), function(c) get.var(paste(c(cropNames[c],"area"),collapse="."),agcdatum))
  a=sum(a)/10 # Converts to 250 Ha farm equivalent
  a
}

modelvsactualAgCensus <- function(farm,data){ # Data must include the modellableArea value
  cropXMLNames=cropNames(farm)
  pairedData=list()
  ncrops=length(cropXMLNames)
  for( c in 1:ncrops){
                                        #    browser()
    fbsCName=toupper(modelCropnameToFBSCropname(cropXMLNames[c]))
    actual=get.var(paste(c(fbsCName,"area"),collapse="."),data)*(250/data$modellableArea)
    simulated=get.var(cropXMLNames[c],data)
    pairedData[[c]]=data.frame(simulated,actual)
  }
  

  actual=seq(0,0,len=length(data[,1]))
  simulated=data$profit
  pairedData[[ncrops+1]]=data.frame(simulated,actual)
  
  actual=seq(0,0,len=length(data[,1]))
  simulated=data$eo
  pairedData[[ncrops+2]]=data.frame(simulated,actual)
  
  pairedNames=c(cropXMLNames,"profit","eo")
  names(pairedData) <- pairedNames
  pairedData
}


runSpatial <- function(profit,farmdata,agctranslate,...,mouweights=c(1.0),mou=NULL,SBFactories=NULL,haulagePerTonnePerKm=0.12,nfert=0.37,pfert=0.26,kfert=0.20,progress=TRUE){
  output=NULL
  unsolved=c()
  for( i in 1:length(farmdata$RF)){
    farm=CompositeFarm(profit,mou,mouweights,farmdata[i,],SBFactories,haulagePerTonnePerKm=haulagePerTonnePerKm)
    
    setInputCost(farm,"N fertiliser",as.double(nfert))
    setInputCost(farm,"P fertiliser",as.double(pfert))
    setInputCost(farm,"K fertiliser",as.double(kfert))
    agctranslate(farm,farmdata[i,],...)
    
    farm=solvefm(farm)
    if ( isSolved(farm)){
      if ( is.null(output)){
        output=data.frame(farm)
      } else {
        output=data.frame(output,farm)
      }
      if ( progress ){
        printProgress(i,farm)
      }
    } else {
      unsolved=append(unsolved,i)
    }
    rm(farm)
    gc()
  }
  if ( length(unsolved) > 0 ){
    cat("There were ",length(unsolved),"unsolved farms \n")
    print(unsolved)
  }
  output=SpatialPointsDataFrame(coordinates(farmdata),data.frame(t(output)))
  output=list(output,unsolved)
  output
}
