parseXMLDocument<-function(filename){
    return(.jcall("jfm/model/Farm","Lorg/w3c/dom/Document;","parseDocument",filename))
}




