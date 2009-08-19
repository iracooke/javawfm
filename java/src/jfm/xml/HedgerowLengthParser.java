package jfm.xml;
import jfm.model.*;

public class HedgerowLengthParser extends JFMObjectParser {
	HedgerowLengthParser(ObjectParser parent_){
		parent=parent_;
		this.registerAttribute("maxlen", mandatoryAttribute);
		this.registerAttribute("historical", mandatoryAttribute);
		registerAttribute("creationCost",mandatoryAttribute);
		registerAttribute("destructionCost",mandatoryAttribute);
		registerAttribute("maintenanceCost",mandatoryAttribute);
		registerAttribute("discountRate",mandatoryAttribute);
	//	registerAttribute("x",optionalAttribute);
	//	registerAttribute("y",optionalAttribute);
	}
	@Override
	public void initializeObject() throws XMLObjectException,
			XMLSyntaxException {
		// TODO Auto-generated method stub
		double maxl=Double.parseDouble(this.getNamedAttribute("maxlen"));
		double hist=Double.parseDouble(this.getNamedAttribute("historical"));
		double create=Double.parseDouble(this.getNamedAttribute("creationCost"));
		double dest=Double.parseDouble(this.getNamedAttribute("destructionCost"));
		double maint=Double.parseDouble(this.getNamedAttribute("maintenanceCost"));
		double disc=Double.parseDouble(this.getNamedAttribute("discountRate"));
		HedgerowLengthComponent theObj=null;
		obj=new HedgerowLengthComponent(maxl,hist,create,dest,maint,disc);
	}

	@Override
	protected boolean isPrimitive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String name() {
		return "Hedgerows";
	}

	@Override
	public String parsesNode() {
		// TODO Auto-generated method stub
		return "hedgerows";
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Hedgerows";
	}

}
