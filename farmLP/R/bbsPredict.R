applyCroppingPredictionToBBSVariables <- function(vars,cropping){ # Need to calculate expected number of transect sections given a percentage of field area in LS
  const=1/25.0
  for( i in 1:nrow(vars)){
    vars[i,"barley"]=sum(cropping[i,"springbarley"],cropping[i,"winterbarley"],na.rm=TRUE)*const
    vars[i,"wheat"]=cropping[i,"winterwheat"]*const
    vars[i,"allbrassica"]=cropping[i,"wosr"]*const
    vars[i,"alllegume"]=sum(cropping[i,"winterbeans"],cropping[i,"springbeans"],na.rm=TRUE)*const
    vars[i,"beetpots"]=sum(cropping[i,"sugarbeet"],cropping[i,"warepotatoes"],na.rm=TRUE)*const
    vars[i,"springcrop"]=sum(cropping[i,"springbeans"],cropping[i,"springbarley"],cropping[i,"sugarbeet"],cropping[i,"warepotatoes"],na.rm=TRUE)*const
  }
  vars
}

contributionFromVar <- function(vname,bbsPoint,bbsModel,total){ 
  contrib=0
  if ( !is.na(bbsModel[1,vname]) )
    contrib=bbsModel[1,vname]*bbsPoint[vname]*bbsModel[3,vname] # Uses weight
  as.numeric(contrib)
}

bbsPredict <- function(bbsPoint,bbsModel){
  inVars = names(bbsModel)
  contributions=sapply(3:length(inVars),function(i) contributionFromVar(inVars[i],bbsPoint@data,bbsModel))
#  inv.logit(sum(contributions))
  exp(sum(contributions)) # TODO: Find out exactly what model was used. Here I have assumed poisson and log link
}
