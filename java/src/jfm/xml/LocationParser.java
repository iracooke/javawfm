package jfm.xml;
import jfm.model.Location;

/** Parses a location object */
final class LocationParser extends JFMObjectParser {

	public LocationParser(ObjectParser parent_){
		parent=parent_;
		registerAttribute("soil",JFMObjectParser.mandatoryAttribute);
		registerAttribute("rain",JFMObjectParser.mandatoryAttribute);
	}

	public void initializeObject() throws XMLObjectException, XMLSyntaxException {
		FarmParser top = (FarmParser)getRootParent();
		int numPeriods = Integer.parseInt(top.getNamedAttribute("nperiods"));
		double soilt=Double.parseDouble(this.getNamedAttribute("soil"));
		double rain=Double.parseDouble(this.getNamedAttribute("rain"));

		
		obj=new Location(soilt,rain,numPeriods);

	}


	protected boolean isPrimitive() {
		return false;
	}

	// In this case there is only one location type so this function is irrelevant for now
	public String name() {
		return "";
	}

	@Override
	public String parsesNode() {
		return "location";
	}

	@Override
	public String toString() {
		return "Location";
	}

}
