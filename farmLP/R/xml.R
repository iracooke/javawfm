parseXMLDocument<-function(filename){
    doc=.jcall("jfm/model/Farm","Lorg/w3c/dom/Document;","parseDocument",filename)
    return(doc)
}




