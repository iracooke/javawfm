
numCrops <- function(farmdatum){
  fbsCNames=fbsCropNames()
  nc=0
  for( c in 1:length(fbsCNames)){
    fbsCName=toupper(fbsCNames[c])
    eostd=get.var(paste(c(fbsCName,"area"),collapse="."),farmdatum)    
    if ( length(eostd) > 1 ){
      browser()
    }
    if ( length(eostd) == 0 ){
      eostd=farmdatum$areaSetAside
    }
    if ( eostd > 0 ){
      nc=nc+1
    }
  }
  nc
}

makePairedTotals <- function(pairedResults){ # Makes totals suitable for a barplot
  sim=sapply(1:length(pairedResults),function(x) mean(pairedResults[[x]]$sim/250))
  ac=sapply(1:length(pairedResults),function(x) mean(pairedResults[[x]]$ac/250))

  simerr=sapply(1:length(pairedResults),function(x) sqrt(var(pairedResults[[x]]$sim/250)/length(pairedResults[[x]]$sim)))
  heights=data.frame(cbind(sim,ac))
  list(heights,simerr)
}



makePathname <- function(pdname,withPrices,fix,r,cc,starty,endy){ 
  dname=makeDir(pdname)
  fname=paste(c("R",r,starty,endy,"RData"),collapse=".")
  if ( cc > 0 ){
    fname=paste(c("R",r,"CC",cc,starty,endy,"RData"),collapse=".")
  }
  if ( withPrices){
    fname=paste(c("withPrices.",fname),collapse="")
  }
  if ( length(fix) > 0 ){
    for(fc in fix){
      fname=paste(c(substr(fc,1,2),fname),collapse=".")
    }
    fname=paste(c("FX",fname),collapse="")
  }
  pathname=paste(c(dname,fname),collapse="/")
  pathname
}

makeDir <- function(pdname){
  dirname=paste(c("data/",pdname),collapse="")
  dir.create(dirname, showWarnings = FALSE, recursive = TRUE)
  dirname=paste(c(dirname,"/"),collapse="")
  dirname
}

rbindlist <- function(data){
  rbdata=data.frame()
  for( y in 1:length(data)){
    tmpdata=data[[y]]
    rbdata=rbind(rbdata,tmpdata)
  }
  rbdata
}

resetFarm <- function(farm){
  .jcall(farm,"V","reset")
}
