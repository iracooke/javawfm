\name{Parameters-class}
\Rdversion{1.1}
\docType{class}
\alias{Parameters-class}
\alias{document,Parameters-method}
\alias{show,Parameters-method}

\title{Class "Parameters"  }
\description{Abstract class defining generic methods for Parameters objects}
\section{Objects from the Class}{A virtual Class: No objects may be created from it.}
\section{Methods}{
  \describe{
    \item{document}{\code{signature(params = "Parameters")}: Get the underlying java object representing the Parameters }
    \item{show}{\code{signature(object = "Parameters")}: Dump the parameters to screen }
	 }
}
\author{ Ira Cooke }
\seealso{
	See also \code{\linkS4class{ObjectiveParameters}} \code{\linkS4class{FarmParameters}} for concrete subclasses
}
\examples{
showClass("Parameters")
}
\keyword{classes}
