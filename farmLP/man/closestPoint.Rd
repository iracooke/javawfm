\name{closestPoint}
\alias{closestPoint}
\title{Finding Closest Points}
\description{
  From a SpatialPointsDataFrame or SpatialPoints object \code{inPoints} find the index of the closest to the point \code{point}
}
\usage{
  load(point,inPoints)
}
\arguments{
  \item{point}{An object for which the function \code{coordinates(point)} will return the coordinates of a single point in latlong format}
  \item{inPoints}{A SpatialPoints or SpatialPointsDataFrame object containing the points to be selected from}
}
\value{
  The index of the closest point in \code{inPoints}
}
\seealso{
  \code{\link{closestPointIndices}}.
}
#\examples{}
\keyword{}
