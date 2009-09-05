package jfm.xml;
import jfm.model.StubblesComponent;

public class StubblesParser extends JFMObjectParser {
	public StubblesParser(ObjectParser parent_){
		parent=parent_;
		registerAttribute("endPeriod",JFMObjectParser.mandatoryAttribute);
		registerAttribute("subsidy",JFMObjectParser.mandatoryAttribute);
	}
	
	@Override
	public void initializeObject() throws XMLObjectException,
			XMLSyntaxException {
		int endPeriod=Integer.parseInt(getNamedAttribute("endPeriod"));
		double subsidy=Double.parseDouble(getNamedAttribute("subsidy"));

		obj=new StubblesComponent(endPeriod,subsidy);
	}

	@Override
	protected boolean isPrimitive() {
		return false;
	}

	@Override
	public String name() {
		return "Stubbles Parser";
	}

	@Override
	public String parsesNode() {
		return "stubbles";
	}

	@Override
	public String toString() {
		return "Stubbles";
	}

}
