\name{FarmRepresentation-class}
\Rdversion{1.1}
\docType{class}
\alias{FarmRepresentation-class}
\alias{cropArea,FarmRepresentation-method}
\alias{cropNames,FarmRepresentation-method}
\alias{eo,FarmRepresentation-method}
\alias{isSolved,FarmRepresentation-method}
\alias{model,FarmRepresentation-method}
\alias{objectiveNames,FarmRepresentation-method}
\alias{objectiveScaleFactors,FarmRepresentation-method}
\alias{objectiveValues,FarmRepresentation-method}
\alias{profit,FarmRepresentation-method}
\alias{setInputCost,FarmRepresentation-method}
\alias{solve,FarmRepresentation-method}

\title{Class "FarmRepresentation" }
\description{Abstract Class representing Farm model objects. Provides generic methods for solving farm models, setting parameters and extracting results}
\section{Objects from the Class}{A virtual Class: No objects may be created from it.}
\section{Methods}{
	\describe{
  	\item{cropArea}{\code{signature(farm = "FarmRepresentation", cropName = "character")}: Extract the solved area of a named crop }
    \item{cropNames}{\code{signature(farm = "FarmRepresentation")}: Get a list of crop names for this farm }
    \item{eo}{\code{signature(farm = "FarmRepresentation")}: Get the enterprise output of a solved farm }
    \item{isSolved}{\code{signature(farm = "FarmRepresentation")}: Determine whether a farm is solved }
    \item{model}{\code{signature(farm = "FarmRepresentation")}: Get a reference to the internal model object (a java object) }
    \item{objectiveNames}{\code{signature(farm = "FarmRepresentation")}: Get a list of the names of objectives used when calculating the solution }
    \item{objectiveScaleFactors}{\code{signature(farm = "FarmRepresentation")}: Get the scale factors used to weight objectives }
    \item{objectiveValues}{\code{signature(farm = "FarmRepresentation")}: Get the solved values of objectives }
    \item{profit}{\code{signature(farm = "FarmRepresentation")}: Get the profit of a solved farm }
    \item{setInputCost}{\code{signature(farm = "FarmRepresentation", inputName = "character", inputCost = "numeric")}: Set the cost of a named input type }
    \item{solve}{\code{signature(a = "FarmRepresentation")}: Solve a farm model }
	 }
}
\note{ This class is largely implemented in Java and requires the rJava package }
\references{ see http://code.google.com/p/javawfm/ }
\author{ Ira Cooke }


\seealso{
	See Also,  \code{\linkS4class{Farm}}, \code{\linkS4class{CompositeFarm}}
}
\examples{
showClass("FarmRepresentation")
}
\keyword{classes}
