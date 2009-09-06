defaultArableFarmParameters<-function(){
	file=system.file("xml/defra.MOTAD.NCdp.lins.sw.sosr.profit",package="farmR")
	doc=parseXMLDocument(file)
	# Set default solver
	.jcall("jfm/r/FarmDocumentEditor","Lorg/w3c/dom/Document;","setAttributeOfTagFilteredByAttribute",doc,"farm",as.character(.packageGlobals$defaultSolver),"nil","nil","solver")
	doc
}

defaultArableObjectiveParameters<-function(){
	file=system.file("xml/motadrisk.mou",package="farmR")
	return(parseXMLDocument(file))
}
