startEPSDevice <- function(name,w=10,h=8){
  postscript(paste(c(name,"eps"),collapse="."), width = w, height = h,
             horizontal = FALSE, onefile = FALSE,
             paper = "special")
}


printBarplot <- function(datalist,name,w=10,h=8){
  startEPSDevice(name,w,h)
  spatialListBarplot(datalist)
  dev.off()
}
