\name{bbsPredict}
\alias{bbsPredict}
\title{Predict Bird Abundance for a BBS Square}
\description{
    Calculate the expected number of transect sections occupied by a species for a bbs transect
}
\usage{
  bbsPredict(bbsPoint,bbsModel)
}
\arguments{
  \item{bbsPoint}{SpatialPoint or SpatialPointsDataFrame with all habitat input variables}
  \item{bbsModel}{data.frame with model coefficients for a single species for all the habitat vars in bbsPoint}
}
\value{
  Expected number of transect sections
}
\seealso{
  \code{\link{applyCroppingPredictionToBBSVariables}}.
}
#\examples{}
\keyword{}
