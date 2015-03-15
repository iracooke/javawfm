# Using javawfm via the farmR package for R #

## Introduction ##

The easiest way to install and use the functionality of javawfm is via its R package equivalent, farmR.  farmR provides a relatively simple way to setup and solve simple models in the R environment.  In addition,  R provides methods for plotting, extracting data and organising work into scripts so that farm model results can be integrated with other analyses.

## Installation ##
  * First download and install R if you haven't already. Pre-packaged binaries of R are available from CRAN http://cran.r-project.org/ for Windows, Linux and Macintosh systems.
  * Install the package using R's package manager. Be sure to also install the dependencies rJava and sp as well.


## Getting Started ##
To make sure the package is loaded type
```
require(farmR)
```
at the R command prompt.


A basic farm can be created and solved as follows;
```
# Create a farm with default parameters
fm=Farm()
# Solve the farm model
solvelp(fm)
# Show basic information about the solution
show(fm)
# Extract key solution variables into an R data frame
data.frame(fm)
```


To get a list of the available methods and classes type
```
help(package="farmR") 
```
at the R command prompt.  For any of the classes or methods listed, help can be obtained by typing
```
help(methodOrObjectName)
```
where methodOrObjectName is the name of a method or object you want help on.