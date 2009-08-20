\name{runSpatial}
\alias{runSpatial}
\title{Run a farm model over every pixel in a map}
\description{
  To run a farming model with profit description contained in the file "Profit.xml" over a SpatialPointsDataFrame \code{soilAndRainfall} use;
  \code{runSpatial("Profit.xml",soilAndRainfall)}
  Depending on the number of points this calculation can take some time to perform. A composite farm is constructed for each pixel that contains several individual farm models, one for each soil and rainfall type present. 
}
\usage{
  runSpatial(profit,soilandrainfall,mou=NULL,SBFactories=NULL,haulagePerTonnePerKm=0.12,nfert=0.37,pfert=0.26,kfert=0.20)
}
\arguments{
  \item{profit}{Name of a file containing a profit description of the model in farmland xml format}
  \item{soilandrainfall}{A SpatialPointsDataFrame that must at least contain the variables RF,X0.5,X0.75,X1.0,X1.25,X1.5,X1.75,X2.0,X2.25,X2.5 and coordinates of all the points they correspond to }
  \item{mou}{ Name of a file containing a multi-objective utility description in farmland xml format}
  \item{SBFactories}{SpatialPointsDataFrame with the coordinates positions of sugarbeet factories in lat long format}
  \item{haulagePerTonnePerKm}{Haulage cost of sugarbeet per tonne per kilometer}
  \item{nfert, pfert, kfert}{Cost of nitrogen, phosphate and potassium fertilizers in UK pounds per kilogram}
}
\value{
  A list. The first element is a SpatialPointsDataFrame with complete farmland model predictions at each point. The second element is a list of indices for points where no solution could be found to the farm plan.
}
\seealso{
  \code{\link{Farm}}.
}
#\examples{}
\keyword{}
