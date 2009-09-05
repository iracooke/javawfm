\name{FarmRepresentation-class}
\Rdversion{1.1}
\docType{class}
\alias{FarmRepresentation-class}
\alias{cropArea,FarmRepresentation,character-method}
\alias{cropNames,FarmRepresentation-method}
\alias{eo,FarmRepresentation-method}
\alias{guts,FarmRepresentation-method}
\alias{isSolved,FarmRepresentation-method}
\alias{model,FarmRepresentation-method}
\alias{objectiveNames,FarmRepresentation-method}
\alias{objectiveScaleFactors,FarmRepresentation-method}
\alias{objectiveValues,FarmRepresentation-method}
\alias{profit,FarmRepresentation-method}
\alias{setInputCost,FarmRepresentation,character,numeric-method}
\alias{show,FarmRepresentation-method}
\alias{solvelp,FarmRepresentation-method}

\title{Class "FarmRepresentation" }
\description{Abstract Class providing generic methods to access properties of Farm objects}
\section{Objects from the Class}{A virtual Class: No objects may be created from it.}
\section{Methods}{
  \describe{
    \item{cropArea}{\code{signature(farm = "FarmRepresentation", cropName = "character")}: The area in hectares of the named crop. Should only be called on solved farm objects }
    \item{cropNames}{\code{signature(object = "FarmRepresentation")}: A string vector with the names of all crops defined for this farm }
    \item{eo}{\code{signature(farm = "FarmRepresentation")}: Enterprise output of the farm }
    \item{guts}{\code{signature(object = "FarmRepresentation")}: Dump the internal representation of the farm to the screen. This is not pretty }
    \item{isSolved}{\code{signature(farm = "FarmRepresentation")}: Returns 1 if the farm has been solved, 0 if not }
    \item{model}{\code{signature(farm = "FarmRepresentation")}: Get a reference to the internal java reference representing this farm }
    \item{objectiveNames}{\code{signature(object = "FarmRepresentation")}: A string vector with the names of objectives that will be optimised in a call to solvelp }
    \item{objectiveScaleFactors}{\code{signature(farm = "FarmRepresentation")}: A numeric vector with the relative weights (normalized) of objectives }
    \item{objectiveValues}{\code{signature(farm = "FarmRepresentation")}: Get the values of the objectives. Should only be called for solved farms }
    \item{profit}{\code{signature(farm = "FarmRepresentation")}: Get the value of the profit objective }
    \item{setInputCost}{\code{signature(farm = "FarmRepresentation", inputName = "character", inputCost = "numeric")}: Set the value of a particular input (potential values are \code{"Input","Machinery","Fuel","Labour","AreaSubsidy"}) }
    \item{show}{\code{signature(object = "FarmRepresentation")}: Print a summary of the Farm to the screen }
    \item{solvelp}{\code{signature(farm = "FarmRepresentation")}: Solve the Model }
	 }
}

\author{ Ira Cooke }
\seealso{
	See also \code{\linkS4class{Farm}} \code{\linkS4class{CompositeFarm}} and for concrete subclasses
}
\examples{
showClass("FarmRepresentation")
}
\keyword{classes}
