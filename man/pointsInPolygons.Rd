\name{pointsInPolygons}
\alias{pointsInPolygons}
\title{Filtering points by polygons}
\description{
	Returns the subset of points contained within the specified polygons
}
\usage{
  pointsInPolygons(points,polygons)
}
\arguments{
  \item{points}{SpatialPointsDataFrame containing the points to be filtered}
  \item{polygons}{SpatialPolygonsDataFrame containing the polygons to be used as a filter}
}
\value{
  SpatialPointsDataFrame consisting of only those points contained within the specified polygons
}
#\seealso{
#}
#\examples{}
\keyword{}
