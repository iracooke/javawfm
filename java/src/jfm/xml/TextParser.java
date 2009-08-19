package jfm.xml;
import jfm.model.Types;
import jfm.model.Types.*;


/** Parses generic comma separated value text fields */
public class TextParser extends JFMObjectParser {
	private int length = -1;
	public TextDataType dataType=null;
	public OperationType opType=null;
	public WorkerType wkType=null;
	public TextParser(ObjectParser parent_){
//		System.out.println("new csvparser with parent "+parent_);
		parent=parent_;
		registerAttribute("length",JFMObjectParser.optionalAttribute);
		registerAttribute("data",JFMObjectParser.mandatoryAttribute);
		registerAttribute("wktype",JFMObjectParser.optionalAttribute);
		registerAttribute("optype",JFMObjectParser.optionalAttribute);
	}
	
	@Override
	public void initializeObject() throws XMLObjectException, XMLSyntaxException {
		try {
		if (textContent==null){ throw new XMLSyntaxException("CSV elements must contain text");};
		if ( attributeHasValue("length")){
			length=Integer.parseInt(getNamedAttribute("length"));
		}
		if ( length > -1 & textContent.split(",").length != length){
			throw new XMLSyntaxException(this.printNameAndAttributes()+" specifies "+length+
					" entries but "+textContent.split(",").length+" supplied at "
					+parent.printNameAndAttributes());
		}
//		obj = textContent.split(",");
		obj=textContent;
		if ( attributeHasValue("data")){
			dataType=Types.xmlToTextDataType(getNamedAttribute("data"));
		} else {
			throw new XMLSyntaxException("data is a mandatory attribute for csv fields");
		}
		if (attributeHasValue("optype")){
			opType=Types.xmlToOperationType(getNamedAttribute("optype"));
		}
		if ( attributeHasValue("wktype")){
			wkType=Types.xmlToWorkerType(getNamedAttribute("wktype"));
		}
		} catch (NumberFormatException ex){
			throw new XMLSyntaxException("Bad number format for attribute");
		} catch (XMLSyntaxException tex){
			tex.printStackTrace();
			throw new XMLSyntaxException(tex.getMessage());
		}
		
	}
	public String name(){ 
		if ( dataType!=null ){
			return dataType.xmlname;
		} else {
			return "textparser";
		}
	}
	public String[] getCSV(){
		String objalias=(String)obj;
		return objalias.split(",");
	}
	@Override
	public String parsesNode() {
		return "csv";
	}

	@Override
	public String toString() {
		return "CSVParser";
	}
	protected boolean isPrimitive(){ return true;};


}
