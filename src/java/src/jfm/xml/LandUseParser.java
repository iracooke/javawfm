package jfm.xml;
import jfm.model.*;
public class LandUseParser extends JFMObjectParser {

	public LandUseParser(ObjectParser parent_){
		parent=parent_;
		registerAttribute("area",JFMObjectParser.mandatoryAttribute);
	}
	
	@Override
	public void initializeObject() throws XMLObjectException,
			XMLSyntaxException {
		// We'll always have at least a cropping object 
		CroppingComponent cropping=(CroppingComponent)getParserList("cropping").get(0).getObject();
		LandUseComponent objalias=new LandUseComponent(cropping);
		objalias.setArea(Double.parseDouble(getNamedAttribute("area")));

		obj=objalias;
	}

	@Override
	protected boolean isPrimitive() {
		// TODO Auto-generated method stub
		return false;
	}

	public String name() {
		return "LandUse";
	}

	public String parsesNode() {
		return "landuse";
	}


	public String toString() {
		return "Land Use";
	}

}
