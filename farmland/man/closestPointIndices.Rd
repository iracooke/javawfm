\name{closestPointIndices}
\alias{closestPointIndices}
\title{Matching Indices of Spatial Points}
\description{
  From a SpatialPointsDataFrame or SpatialPoints object \code{inPoints} find the index of the closest to the point \code{point}
}
\usage{
  load(ofPoints,inPoints)
}
\arguments{
  \item{ofPoints}{A SpatialPoints or SpatialPointsDataFrame containing points for which closest indices will be derives}
  \item{inPoints}{A SpatialPoints or SpatialPointsDataFrame object containing the points to be selected from}
}
\value{
	An index vector of length equal to the number of points in \code{inPoints}
}
\seealso{
  \code{\link{closestPoint}}.
}
#\examples{}
\keyword{}
