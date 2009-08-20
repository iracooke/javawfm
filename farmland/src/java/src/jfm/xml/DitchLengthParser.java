/**
 * 
 */
package jfm.xml;

import jfm.model.DitchLengthComponent;

/**
 * @author iracooke
 *
 */
public class DitchLengthParser extends JFMObjectParser {

	DitchLengthParser(ObjectParser parent_){
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
	
	/* (non-Javadoc)
	 * @see jfm.xml.ObjectParser#initializeObject()
	 */
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
		obj =new DitchLengthComponent(maxl,hist,create,dest,maint,disc);

	}

	/* (non-Javadoc)
	 * @see jfm.xml.ObjectParser#isPrimitive()
	 */
	@Override
	protected boolean isPrimitive() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see jfm.xml.ObjectParser#name()
	 */
	@Override
	public String name() {
		return "Ditches";
	}

	/* (non-Javadoc)
	 * @see jfm.xml.ObjectParser#parsesNode()
	 */
	@Override
	public String parsesNode() {
		// TODO Auto-generated method stub
		return "ditches";
	}

	/* (non-Javadoc)
	 * @see jfm.xml.ObjectParser#toString()
	 */
	@Override
	public String toString() {
		return "ditches";
	}

}
