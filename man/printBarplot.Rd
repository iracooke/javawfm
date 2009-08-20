\name{printBarplot}
\alias{printBarplot}
\title{Print a barplot }
\description{
    Print a barplot from a list of SpatialDataFrameObjects to a file
}
\usage{
  printBarplot(datalist,name,w=10,h=8)
}
\arguments{
  \item{datalist}{List of  SpatialPointsDataFrame object to be summarized in the plot}
  \item{name,w,h}{Arguments to \code{startEPSDevice} }
}
\value{
    Used for it's side effects
}
\seealso{
  \code{\link{startEPSDevice,spatialListBarplot,landscapeColSums}}.
}
#\examples{}
\keyword{}
