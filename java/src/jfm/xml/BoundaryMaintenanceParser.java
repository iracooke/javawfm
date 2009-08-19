/**
 * 
 */
package jfm.xml;
import jfm.model.BoundaryMaintenanceComponent;

/**
 * @author iracooke
 *
 */
public class BoundaryMaintenanceParser extends JFMObjectParser {
	BoundaryMaintenanceParser(ObjectParser parent_){
		parent=parent_;
		this.registerAttribute("eb1cost", mandatoryAttribute);
		this.registerAttribute("eb2cost", mandatoryAttribute);
		this.registerAttribute("eb3cost", mandatoryAttribute);
		this.registerAttribute("eb6cost", mandatoryAttribute);
		this.registerAttribute("eb10cost", mandatoryAttribute);
	}
	
	/* (non-Javadoc)
	 * @see jfm.xml.ObjectParser#initializeObject()
	 */
	@Override
	public void initializeObject() throws XMLObjectException,
			XMLSyntaxException {
		double eb1c=Double.parseDouble(this.getNamedAttribute("eb1cost"));
		double eb2c=Double.parseDouble(this.getNamedAttribute("eb2cost"));
		double eb3c=Double.parseDouble(this.getNamedAttribute("eb3cost"));
		double eb6c=Double.parseDouble(this.getNamedAttribute("eb6cost"));
		double eb10c=Double.parseDouble(this.getNamedAttribute("eb10cost"));
		obj=new BoundaryMaintenanceComponent(eb1c,eb2c,eb3c,eb6c,eb10c);
	}

	/* (non-Javadoc)
	 * @see jfm.xml.ObjectParser#isPrimitive()
	 */
	@Override
	protected boolean isPrimitive() {
		return false;
	}

	/* (non-Javadoc)
	 * @see jfm.xml.ObjectParser#name()
	 */
	@Override
	public String name() {
		return "Boundary Maintenance";
	}

	/* (non-Javadoc)
	 * @see jfm.xml.ObjectParser#parsesNode()
	 */
	@Override
	public String parsesNode() {
		return "boundarymaintenance";
	}

	/* (non-Javadoc)
	 * @see jfm.xml.ObjectParser#toString()
	 */
	@Override
	public String toString() {
		return "boundarymaintenance";
	}

}
