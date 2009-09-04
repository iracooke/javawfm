\name{CompositeFarm}
\Rdversion{1.1}
\alias{CompositeFarm}
%- Also NEED an '\alias' for EACH other topic documented here.
\title{
CompositeFarm
}
\description{
Creates Composite Farm Objects
}
\usage{
CompositeFarm(farmParams, mou = NULL, mouweights = c(1), soildata, SBFactories = NULL, haulagePerTonnePerKm = 0.12, maxSBHaulageDistance = 200)
}
\arguments{
  \item{farmParams}{
A \code{FarmParameters} object to be used for all component farms
}
  \item{mou}{
A single \code{ObjectiveParameters} object or a list of such objects specifying the objective parameters used to define the component farms. If a list is supplied the total number of component farms will be the length of this list multiplied by the number of soil classes. 
}
  \item{mouweights}{
If supplied this specifies the weightings to be used for each of the objective parameters supplied in the \code{mou} argument. The lengths of \code{mouweights} and \code{mou} must be equal.
}
  \item{soildata}{
A \code{data.frame} or \code{SpatialDataFrame} with a single row and items named (RF,X0.5,X0.75,X1.0,X1.25,X1.5,X1.75,X2.0,X2.25,X2.5) representing the yearly rainfall in mm and the proportion of land in each of the soiltype classes. All items must be present.
}
  \item{SBFactories}{
\code{SpatialDataFrame} object with the Locations of sugarbeet factories to be used to calculate sugarbeet transport costs. \code{data(SBFactories)} provides an appropriate dataframe. Requires the sp package
}
  \item{haulagePerTonnePerKm}{
Cost of sugarbeet haulage in pounds
}
  \item{maxSBHaulageDistance}{
Maximum allowable haulage distance for sugarbeet in km
}
}
\details{
Constructs itself from component farms, each of which will represent a particular combination of soiltype and objective parameters. The total number of component farms constructed will be \code{length(mou)*length(soildata)}
}

\author{
Ira Cooke
}
\note{
If using spatial data as arguments (eg for soildata or SBFactories) this requires the sp package.
}


\examples{

	fp=FarmParameters()
	op=ObjectiveParameters()
	localConditions=data.frame(RF=600.0,X0.5=0,X0.75=0.1,X1.0=0.1,X1.25=0.5,X1.5=0.1,X1.75=0,X2.0=0,X2.25=0,X2.5=0.2)
	cfarm=CompositeFarm(fp,op,1.0,localConditions)
	solvelp(cfarm)
	show(cfarm)
}
% Add one or more standard keywords, see file 'KEYWORDS' in the
% R documentation directory.

