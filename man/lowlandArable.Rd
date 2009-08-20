\name{lowlandArable}
\alias{lowlandArable}
\alias{getLowlandFarmsInCounties}
\title{Filtering lowland farms}
\description{
	Return subsets of farms
}
\usage{
	getLowlandFarmsInCounties(data,countypolygons,soilandrainfall,arableThreshold=0.85,altitudeThreshold=1)
	lowlandArable(data,arableThreshold,altitudeThreshold)
}
\arguments{
  \item{data}{FBS Dataset containing the farms to be filtered as rows}
  \item{countypolygons}{SpatialPolygonsDataFrame containing the polygons to be used as a filter}
  \item{soilandrainfall}{Usual soil and rainfall dataframe eg \code{data(SoilRainfall)} }
  \item{arableThreshold}{Proportion of farm in arable in order to be classified as an arable farm}
  \item{altitudeThreshold}{FBS altitude code specifying maximum altitude to be lowland}
}
\value{
	data frame with only those lowland farms within specified counties. Attaches soil and rainfall data for the county as a side effect
}
#\seealso{
#}
#\examples{}
\keyword{}
