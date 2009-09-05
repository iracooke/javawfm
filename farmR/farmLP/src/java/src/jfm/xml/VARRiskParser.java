/**
 * 
 */
package jfm.xml;

/**
 * @author iracooke
 *
 */
public class VARRiskParser extends JFMObjectParser {
	VARRiskParser(ObjectParser parent_){
		parent=parent_;
		this.registerAttribute("alpha", mandatoryAttribute);
		this.registerAttribute("offset",mandatoryAttribute);
	}
	/* (non-Javadoc)
	 * @see jfm.xml.ObjectParser#initializeObject()
	 */
	@Override
	public void initializeObject() throws XMLObjectException,
			XMLSyntaxException {
		// TODO Auto-generated method stub
		double alpha = Double.parseDouble(this.getNamedAttribute("alpha"));
		double offset = Double.parseDouble(this.getNamedAttribute("offset"));
		obj=new jfm.model.VARRiskComponent(alpha,offset);
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
		// TODO Auto-generated method stub
		return "VARRisk";
	}

	/* (non-Javadoc)
	 * @see jfm.xml.ObjectParser#parsesNode()
	 */
	@Override
	public String parsesNode() {
		// TODO Auto-generated method stub
		return "varrisk";
	}

	/* (non-Javadoc)
	 * @see jfm.xml.ObjectParser#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "VAR Risk";
	}

}
