package jfm.xml;

public class MOTADRiskParser extends JFMObjectParser {
	MOTADRiskParser(ObjectParser parent_){
		parent=parent_;
	}
	
	
	@Override
	public void initializeObject() throws XMLObjectException,
			XMLSyntaxException {

		obj=new jfm.model.MOTADRiskComponent();

	}

	@Override
	protected boolean isPrimitive() {
		return false;
	}

	@Override
	public String name() {
		return "MOTAD Risk Parser";
	}

	@Override
	public String parsesNode() {
		return "motadrisk";
	}

	@Override
	public String toString() {
		return "MOTAD Risk Parser";
	}

}
