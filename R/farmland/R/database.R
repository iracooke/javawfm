pathForFarmDBFilename <- function(name){ # Could return multiple matches. Up to user to check
  data(parameters)
  baseFiles[grep(name,baseFiles)]
}
