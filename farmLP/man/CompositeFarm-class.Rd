\name{CompositeFarm-class}
\Rdversion{1.1}
\docType{class}
\alias{CompositeFarm-class}

\title{Class "CompositeFarm"}
\description{A collection of Farm objects representing a farm with heterogenous soil or objective types}
\section{Objects from the Class}{
Objects can be created by calls to the function \code{\link{CompositeFarm}}.
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


\author{ Ira Cooke }
\seealso{
See also \code{\linkS4class{Farm}} and \code{\linkS4class{FarmRepresentation}} 
}
\examples{
	showClass("CompositeFarm")
}
\keyword{classes}
