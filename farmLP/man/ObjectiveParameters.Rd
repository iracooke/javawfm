\name{ObjectiveParameters}
\Rdversion{1.1}
\alias{ObjectiveParameters}
\alias{setWeightForObjective}
\alias{getWeightForObjective}
\title{
Create ObjectiveParameters objects
}
\description{
Create ObjectiveParameters objects from xml files or from the defaults
}
\usage{
ObjectiveParameters(file = defaultArableObjectiveParameters())
setWeightForObjective(params,objective,value)
getWeightForObjective(params,objective)
}

\arguments{
  \item{file}{
A character string with the full path to an xml document containing the parameters
}
\item{params}{
An ObjectiveParameters object
}
\item{objective}{
A character string identifying the objective. Available values can be obtained via \code{objectiveNames(params)}
}
\item{value}{
New weighting for objective. Objectives are renormalized so only the relative values assigned to different objectives are important.
}
}
\details{
Parses the xml document provided and stores it internally as a java object. The raw xml can be viewed using the \code{show} command
}
\value{
\code{ObjectiveParameters} returns an object of the class \code{ObjectiveParameters}
\code{getWeightForObjective} returns the relative weight of the specified objective
\code{objectiveNames} returns a string vector with the names of objectives
}

\author{
Ira Cooke
}

\examples{
	parameters=ObjectiveParameters()
	show(parameters)
	
	names=objectiveNames(parameters)
	weights=sapply(names,function(x) getWeightForObjective(parameters,x))
	
}
