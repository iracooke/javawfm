
#Loads a dataset with all the xmlfiles usable by this project
econbase=list.files(path=paste(c(getwd(),"/../xml/"),collapse=""),pattern=".profit")
moubase=list.files(path=paste(c(getwd(),"/../xml/"),collapse=""),pattern=".mou")
econbase=append(econbase,moubase)

pathForFarmDBFilename <- function(name){ # Could return multiple matches. Up to user to check
  data(parameters)
  baseFiles[grep(name,baseFiles)]
}

makeFileGroup<-function(econs){
  econbasef=c()
  folderStr=""
  for ( fn in 1:length(econs)){
    pref=paste(c(R.home(),"/library/farmland/xml/"),collapse="")
    econbasef=append(econbasef,paste(c(pref,"/",folderStr,econs[fn]),collapse=""))
    econs[fn]=sub(".profit","",econs[fn])    
  }
    groupdata=data.frame(profit=econbasef,ID=econs,stringsAsFactors=FALSE)
  return(econbasef)
}

#DEFRADatabaseXMLFiles=makeFileGroup(econbase)
#BasicFarms=lapply(econbase,parseXMLDocument)

#profitDoc=parseXMLDocument(pathForFarmDBFilename(paste(c(pdname,"profit"),collapse=".")))

rm(econbase)
rm(makeFileGroup)