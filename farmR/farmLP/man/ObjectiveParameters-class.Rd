\name{ObjectiveParameters-class}
\Rdversion{1.1}
\docType{class}
\alias{ObjectiveParameters-class}
\alias{set,Farm,ObjectiveParameters-method}
\alias{objectiveNames,ObjectiveParameters-method}

\title{Class "ObjectiveParameters" }
\description{Represents multiple objective utility parameters for a Farm or CompositeFarm object}
\section{Objects from the Class}{
Objects can be created by calls to the function \code{\link{ObjectiveParameters}}.
}
\section{Slots}{
	 \describe{
    \item{\code{document}:}{Object of class \code{"jobjRef"} which is a reference to the underlying java object that holds the parameter data }
  }
}
\section{Extends}{
Class \code{"\linkS4class{Parameters}"}, directly.
}
\section{Methods}{
  \describe{
    \item{set}{\code{signature(farm = "Farm", input = "ObjectiveParameters")}: Used to set the ObjectiveParameters for a single Farm object. Must be done before the first call to \code{solvelp} 
	} 
	\item{objectiveNames}{\code{signature(object="ObjectiveParameters")}: Get a string vector with the objective parameters
	}
	}
}

\author{ Ira Cooke }
\seealso{	
	See also \code{\linkS4class{FarmParameters}} 
}
\examples{
showClass("ObjectiveParameters")
}

\keyword{classes}
