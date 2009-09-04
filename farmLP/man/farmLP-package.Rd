\name{farmLP-package}
\Rdversion{1.1}
\alias{farmLP-package}
\alias{farmLP}
\docType{package}
\title{
Mixed Integer model of an Arable Farm
}
\description{
A mixed integer description of an arable farm. Finds optimal farming plans given economic and social preference information.
}
\details{
\tabular{ll}{
Package: \tab farmLP\cr
Type: \tab Package\cr
Version: \tab 1.0\cr
Date: \tab 2009-08-20\cr
License: \tab GPL-3\cr
Suggests: \tab sp\cr
LazyLoad: \tab yes\cr
Depends: \tab rJava,methods\cr
Packaged: \tab Thu Aug 20 16:03:11 2009; iracooke\cr
Built: \tab R 2.9.1; universal-apple-darwin8.11.1; 2009-09-04 13:10:43 UTC; unix\cr
}

Index:
\preformatted{
CompositeFarm           CompositeFarm
CompositeFarm-class     Class "CompositeFarm"
Farm                    Farm
Farm-class              Class "Farm"
FarmParameters          Create and set values on FarmParameters objects
FarmParameters-class    Class "FarmParameters"
FarmRepresentation-class
                        Class "FarmRepresentation"
ObjectiveParameters     Create ObjectiveParameters objects
ObjectiveParameters-class
                        Class "ObjectiveParameters"
Parameters-class        Class "Parameters"
SBFactories             Locations of Sugarbeet factories in the UK
defaultArableFarmParameters
                        Functions to create default Parameters
}

}
\author{
Ira R. Cooke

Maintainer: Ira R. Cooke <iracooke@gmail.com>
}
\keyword{ package }

\examples{
farm=Farm()

crops=cropNames(farm)

solvelp(farm)
areas=sapply(crops,function(x) cropArea(farm,x))


}
