\name{FarmParameters-class}
\Rdversion{1.1}
\docType{class}
\alias{FarmParameters-class}
\alias{cropNames,FarmParameters-method}

\title{Class "FarmParameters" }
\description{Encapsulates an xml document containing detailed parameter information for the Farm model}
\section{Objects from the Class}{
Objects can be created by calls to the function \code{\link{FarmParameters}}.
}
\section{Slots}{
	 \describe{
    \item{\code{document}:}{Object of class \code{"jobjRef"}: a reference to an underlying java object containing the xml file }
  }
}
\section{Extends}{
Class \code{"\linkS4class{Parameters}"}, directly.
}
\section{Methods}{
  \describe{
    \item{cropNames}{\code{signature(object = "FarmParameters")}: returns a vector of strings with the names of crops defined in the parameter file }
	 }
}

\author{ Ira Cooke }
\seealso{
	See also \code{\linkS4class{ObjectiveParameters}} 
}
\examples{
showClass("FarmParameters")
}
\keyword{classes}
