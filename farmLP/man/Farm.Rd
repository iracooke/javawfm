\name{Farm}
\Rdversion{1.1}
\alias{Farm}
\title{
Farm
}
\description{
Create Farm objects 
}
\usage{
Farm(farm.params = defaultArableFarmParameters(), obj.params = NULL)
}
\arguments{
  \item{farm.params}{
An object of class \code{FarmParameters} or the name of an xml file from which to read the parameters
}
  \item{obj.params}{
An object of class \code{ObjectiveParameters} or the name an xml file from which to read the parameters
}
}

\value{
Returns a Farm object
}

\author{
Ira Cooke
}
\seealso{
See Also \code{\link{CompositeFarm}}
}
\examples{
	fm=Farm() # Create a default Farm object
	solvelp(fm) # Solve the farm
	show(fm)

}
