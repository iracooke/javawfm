\name{defaultArableFarmParameters}
\Rdversion{1.1}
\alias{defaultArableFarmParameters}
\alias{defaultArableObjectiveParameters}

\title{
Functions to create default Parameters
}
\description{
Create \code{FarmParameters} or \code{ObjectiveParameters} objects from default values 
}
\usage{
defaultArableFarmParameters()
defaultArableObjectiveParameters()
}
\value{
Both functions return an object of class \code{Parameters}. 
\code{defaultArableFarmParameters} returns a \code{FarmParameters} object 
\code{defaultArableObjectiveParameters} returns a \code{ObjectiveParameters} object
}
\author{
Ira Cooke
}
\seealso{
	See also \code{\linkS4class{ObjectiveParameters}} and \code{\linkS4class{FarmParameters}} 
}
\examples{
	op=defaultArableObjectiveParameters()
	fp=defaultArableFarmParameters()
	show(op) # Dump Objective parameters to screen in xml format
	show(fp) # Dump the Farm Parameters to screen in xml format
}
