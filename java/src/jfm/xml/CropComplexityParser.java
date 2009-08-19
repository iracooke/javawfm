package jfm.xml;

public class CropComplexityParser extends JFMObjectParser {
	CropComplexityParser(ObjectParser parent_){
		parent=parent_;
		this.registerAttribute("threshold", this.optionalAttribute);
	}
	@Override
	public void initializeObject() throws XMLObjectException,
			XMLSyntaxException {
		double threshold=0.001; // very small as default
		if ( this.attributeHasValue("threshold")){
			threshold=Double.parseDouble(this.getNamedAttribute("threshold"));
		}
		obj=new jfm.model.CropComplexityComponent(threshold);

	}

	@Override
	protected boolean isPrimitive() {
		return false;
	}

	@Override
	public String name() {
		return "Crop Complexity parser";
	}

	@Override
	public String parsesNode() {
		return "cropcomplexity";
	}

	@Override
	public String toString() {
		return "CropComplexity";
	}

}
