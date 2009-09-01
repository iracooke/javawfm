\name{Farm-class}
\Rdversion{1.1}
\docType{class}
\alias{Farm-class}
\alias{set,Farm,jobjRef-method}
\alias{set,Farm,character-method}

\title{Class "Farm" }
\description{Represents a farm model for a single farm}
\section{Objects from the Class}{
Objects can be created by calls to \code{\link{Farm}} 

Alternatively a default arable farm can be created with a call to \code{\link{basicArableFarm}}, which provides a farm object initialized with parameters from the default database. 

}
\section{Slots}{
	 \describe{
    \item{\code{model}:}{Object of class \code{"jobjRef"} which is a reference to the internal java object representing the farm }
    \item{\code{cropNames}:}{Object of class \code{"vector"} representing the crop names }
  }
}
\section{Extends}{
Class \code{"\linkS4class{FarmRepresentation}"}, directly.
}
\section{Methods}{
  \describe{
    \item{set}{\code{signature(farm = "Farm", input = "jobjRef")}: Sets the multi objective preferences using a preference object. Typically created with a calls to \code{\link{parseXMLDocument}} or \code{\link{basicMOTADMOU}}}
    \item{set}{\code{signature(farm = "Farm", input = "character")}: Sets the multi objective preferences using an xml preference file }
	 }
}

\author{ Ira Cooke }

\seealso{
	See also \code{\linkS4class{CompositeFarm}} and \code{\linkS4class{FarmRepresentation}} 
}
\examples{
fm=Farm()
solve(fm)
print(data.frame(fm))
}
\keyword{classes}
