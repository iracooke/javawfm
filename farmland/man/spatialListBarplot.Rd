\name{spatialListBarplot}
\alias{spatialListBarplot}
\title{Barplot from a list of SpatialPointsDataFrames}
\description{
    Make a nice plot by first calling \code{landscapeColSums} to summarise data frames.
}
\usage{
    spatialListBarplot(datalist,ins=-0.13,marg=c(5,4,4,10),normalize=TRUE,leg=NULL,lcolr=NULL,...)
}
\arguments{
  \item{datalist}{A list of SpatialDataFrame objects}
  \item{ins}{Inset of legend from the right axis}
  \item{marg}{Argument passed to \code{par} setting the margins of the plot} 
  \item{normalize}{Whether to normalize data across items in the list} 
  \item{leg}{Names to be used for columns in dataframes} 
  \item{lcolr}{Colors to be used in segments of the bars} 
}
\seealso{
  \code{\link{landscapeColSums}}.
}
#\examples{}
\keyword{}
