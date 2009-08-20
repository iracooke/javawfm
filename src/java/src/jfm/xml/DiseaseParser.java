package jfm.xml;

import jfm.model.Disease;
import jfm.model.Types;
import jfm.model.Types.DiseaseType;

/** Parses a disease object */
final class DiseaseParser extends JFMObjectParser {
	DiseaseType id;
	
	public DiseaseParser(ObjectParser parent_){
		parent=parent_;
		registerAttribute("id",JFMObjectParser.mandatoryAttribute);
		registerAttribute("assoc",JFMObjectParser.optionalAttribute);
		registerAttribute("break",JFMObjectParser.optionalAttribute);
	}
	
	@Override
	public void initializeObject() throws XMLObjectException, XMLSyntaxException {		
		id = Types.xmlToDiseaseType(this.getNamedAttribute("id"));
		DiseaseType assoc=Types.DiseaseType.NONE;
		int minBreak=0; // Default value of the number of break years in between crops 
		if ( this.attributeHasValue("assoc")){
			assoc=Types.xmlToDiseaseType(this.getNamedAttribute("assoc"));
		}
		try {
			if ( this.attributeHasValue("break")){
				minBreak=Integer.parseInt(this.getNamedAttribute("break"));
			}
		} catch (NumberFormatException ex){
			throw new XMLSyntaxException(ex.getMessage());
		}
		obj = new Disease(id,assoc,minBreak);
	}
	public String name(){ return id.xmlname;};

	@Override
	protected boolean isPrimitive() {
		return false;
	}

	@Override
	public String parsesNode() {
		return "disease";
	}

	@Override
	public String toString() {
		return "DiseaseParser";
	}

}
