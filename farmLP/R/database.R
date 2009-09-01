defaultArableFarmParameters<-function(){
	file=system.file("xml/defra.MOTAD.NCdp.lins.sw.sosr.profit",package="farmLP")
	return(parseXMLDocument(file))
}

defaultArableObjectiveParameters<-function(){
	file=system.file("xml/motadrisk.mou",package="farmLP")
	return(parseXMLDocument(file))
}
