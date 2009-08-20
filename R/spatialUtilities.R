closestPoint <- function(point,inPoints){
  distances=spDistsN1(coordinates(inPoints),coordinates(point),longlat=TRUE)
  index=which(distances==min(distances))
  if ( length(index) > 1 )
    return(index[0])

  index
}

closestPointIndices <- function(ofPoints,inPoints){
  sapply(1:length(ofPoints@data[,1]),function(i) closestPoint(ofPoints[i,],inPoints))
}

# TODO: Add a function argument so you can customize what this does to summarize each column
landscapeColSums <- function(datalist){ # Returns a matrix by taking colsums from a list of spatial data frames with the same data columns
  df=NULL
  for(li in 1:length(datalist)){
    datali=datalist[[li]]@data    
    if ( is.null(df)){
      df=data.frame(s=colSums(datali))
    } else {
      df=data.frame(df,s=colSums(datali))
    }    
  }
  t(df)
}

# TODO: When landscapeColSums gets updated to take a function argument this needs updating too
spatialListBarplot <- function(datalist,ins=-0.13,marg=c(5,4,4,10),normalize=TRUE,leg=NULL,lcolr=NULL,...){
  cs=landscapeColSums(datalist)
  cs=cs[,-1] # Remove the profit variable
  if (normalize){
    cs=cs/rowSums(cs)
  }
  if(is.null(leg)){
    leg=names(datalist[[1]]@data[1,][-1])
  }
  if(is.null(lcolr)){
    lcolr=palette(rainbow(length(leg)))
  }
  if ( names(dev.cur()) == "postscript"){ # margins must be passed directly to par
    par(mar=marg)
    barplot(t(cs),col=lcolr,...)
  } else {
    barplot(t(cs),col=lcolr,mar=marg,...)
  }
  legend("right",legend=leg,fill=lcolr,xjust=1,inset=ins,xpd=TRUE)
}
