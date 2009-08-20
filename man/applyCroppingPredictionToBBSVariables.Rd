\name{applyCroppingPredictionToBBSVariables}
\alias{applyCroppingPredictionToBBSVariables}
\title{Substitute Predicted cropping data for data recorded at bbs sites}
\description{
    Create a spatial data frame by combining bbs habitat variables with a subset of those variables that can be predicted by another model
}
\usage{
  applyCroppingPredictionToBBSVariables(vars,cropping)
}
\arguments{
  \item{vars}{BBS Habitat variable SpatialPointsDataFrame}
  \item{cropping}{Cropping variable data.frame as output by \code{data.frame(farm)} where \code{farm} is a FarmRepresentation object }
}
\value{
  SpatialPointsDataFrame containing some original bbs variables and some substituted variables
}
\seealso{
  \code{\link{bbsPredict}}.
}
#\examples{}
\keyword{}
