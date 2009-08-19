\name{farmland-package}
\alias{farmland-package}
\alias{farmland}
\docType{package}
\title{
A model of arable farmland
}
\description{
A mixed integer description of an arable farm. Finds optimal farming plans given economic and social preference information
}
\details{
\tabular{ll}{
Package: \tab farmland\cr
Type: \tab Package\cr
Version: \tab 1.0\cr
Date: \tab 2008-09-17\cr
License: \tab GPL-3 \cr
LazyLoad: \tab yes\cr
}
Todo
}
\author{
Ira R. Cooke

Maintainer: Ira R. Cooke <iracooke@gmail.com>
}
\references{
}
\keyword{ package }
\seealso{
}
\examples{
# Test the package
  fm=Farm(pathForFarmDBFilename("example.profit"))
  fm=set(fm,pathForFarmDBFilename("example.mou"))
  fm=solvefm(fm)
summary(fm)
}
