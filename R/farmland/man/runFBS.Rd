\name{runFBS}
\alias{runFBS}
\title{Run a farm model over a set of FBS farms}
\description{
  To run a farming model with profit description contained in the file "Profit.xml" over an FBS dataframe, \cite{farmdata} with function \code{applyFarmData} use;
  \code{runFBS("Profit.xml",farmdata,applyFarmData)}
  The variables in farmdata should correspond to those required by the function \code{applyFarmData} to customize the model inputs for each farm.
  Depending on the number of farms this calculation can take some time to perform. A composite farm is constructed for each farm that contains several individual farm models, one for each soil and rainfall type present. 
}
\usage{
  runFBS(profit,farmdata,fbstranslate,...,mou=NULL,nfert=0.37,pfert=0.26,kfert=0.20,progress=TRUE)
}
\arguments{
  \item{profit}{Name of a file containing a profit description of the model in farmland xml format}
  \item{farmdata}{A dataframe with specific farm information contained in each row. Column variables should correspond to those required by the \code{fbstranslate} function }
  \item{fbstranslate}{ A user supplied function to customize a farm object based on \code{farmdata}}
  \item{...}{Additional arguments to \code{fbstranslate}}
  \item{mou}{multi-objective utility description}
  \item{progress}{whether to show progress during calculation}
  \item{nfert, pfert, kfert}{Cost of nitrogen, phosphate and potassium fertilizers in UK pounds per kilogram}
}
\value{
  A list. The first element is a dataframe with complete farmland model predictions at each point. The second element is a list of indices for points where no solution could be found to the farm plan.
}
\seealso{
  \code{\link{Farm,runSpatial}}.
}
#\examples{}
\keyword{}
