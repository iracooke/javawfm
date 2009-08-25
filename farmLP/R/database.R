basicArableFarmParameterFile<-function(){
	file=system.file("xml/defra.MOTAD.NCdp.lins.sw.sosr.profit",package="farmLP")
	return(parseXMLDocument(file))
}

basicArableFarm<-function(){
	return(Farm(basicArableFarmParameterFile()))
}
