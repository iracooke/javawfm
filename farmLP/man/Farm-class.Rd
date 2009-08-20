\name{Farm-class}
\docType{class}
\alias{Farm-class}
\alias{Farm}
\alias{model,Farm,Farm-method}
\alias{solveFarm,Farm,Farm-method}

\title{Class "Farm"}
\description{ Represents a model description of a farm }
\section{Slots}{
  \describe{
    \item{\code{model}:}{Internal reference to a java object containing the model description}
  }
}
\section{Methods}{
  \describe{
    \item{model}{\code{signature(obj = "jobjRef")}: retrieves the model reference }
    \item{solvefm}{\code{signature(farm = "FarmRepresentation")}: Solves the internal linear programme and stores its state in the model object }
    \item{summary}{\code{signature(farm = "FarmRepresentation")}: summarize object}
  }
}

\usage{
	Farm(modelDescription)
}

\arguments{
\item{modelDescription}{Path string to an xml file with model parameters}
}

\author{ Ira R. Cooke, \email{iracooke@gmail.com}
}

\examples{
	fm=Farm(pathForFarmDBFilename("example.profit"))
 	 fm=set(fm,pathForFarmDBFilename("example.mou"))
 	  fm=solvefm(fm)
	  summary(fm)
}

\keyword{classes}
