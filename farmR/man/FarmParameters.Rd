\name{FarmParameters}
\Rdversion{1.1}
\alias{FarmParameters}
\alias{setSolverType}
\alias{getSolverType}
\alias{setPriceForCrop}
\alias{setSubsidyForCrop}
\alias{setRelativePriceForCrop}
\alias{setRelativeRotationPenalties}
\alias{setRelativeLabourRequirements}
\alias{setRelativeYieldForCrop}
\alias{setRelativeCost}
\title{
Create and set values on FarmParameters objects
}
\description{
Create FarmParameters objects from xml files or from the defaults and customise by setting parameters
}
\usage{
	FarmParameters(file = defaultArableFarmParameters())
	setSolverType(params,solver)
	getSolverType(params)
	setPriceForCrop(params,newPrice,cropName)
	setSubsidyForCrop(params,newSubsidy,cropName)
	setRelativePriceForCrop(params,cropName,val)
	setRelativeRotationPenalties(params,val)
	setRelativeLabourRequirements(params,val)
	setRelativeYieldForCrop(params,cropName,val)
	setRelativeCost(params,val,costType="Input",inputName="N fertiliser")
}

\arguments{
  \item{file}{
A character string with the full path to an xml document containing the farm parameters
}
 \item{params}{
A FarmParameters object
}
  \item{solver}{
Character string with the name of the core solver to be used. Available values are \code{"cbc"} or \code{"glpk"}
}
\item{newPrice}{
Numeric value of the absolute price per tonne for a crop
}
\item{cropName}{
Character string with the name of the crop. A list of possible values can be obtained with \code{cropNames(params)}
}
\item{newSubsidy}{
Numeric value of the absolute subsidy value per hectare for a crop
}
\item{val}{
Numeric multiplier to be applied to the existing value of a parameter. A value of 1.0 leaves the parameter unchanged.
}
\item{costType}{
A character string with one of the following values \code{"Input","Machinery","Fuel","Labour","AreaSubsidy"} corresponding to different types of farming costs
}
\item{inputName}{
Name of an input cost (required for costType="Input"). Available values depend on which values have been defined in params but defaults are \code{"N fertiliser","P fertiliser","K fertiliser","BGHerbicide","WOHerbicide","Seed Amount of crop"}
}

}
\details{
When creating FarmParameters objects xml document provided is parsed and stored internally as a java object. 
The raw xml can be viewed using the \code{show} command.
Values in the xml document can be changed using the various set commands.

Use functions of the form setXXX() to set absolute values
Use functions of the form setRelativeXXX() to set a multiplier on an existing value

}

\value{
\code{FarmParameters} returns an object of the class \code{FarmParameters}
}

\author{
Ira Cooke
}

\examples{
	params=FarmParameters()
	show(params) # Dumps xml to the screen. It may be more useful to write to a file using the write command
}


