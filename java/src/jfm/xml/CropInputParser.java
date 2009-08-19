/**
 * 
 */
package jfm.xml;
import jfm.model.Types;
import jfm.model.Types.VariableType;
import jfm.model.CropInput;
/**
 * @author iracooke
 *
 */
public class CropInputParser extends JFMObjectParser {

	public CropInputParser(ObjectParser parent_){
		parent=parent_;
		registerAttribute("type",JFMObjectParser.mandatoryAttribute);
		registerAttribute("unitCost",JFMObjectParser.mandatoryAttribute);
		registerAttribute("baseAmount",JFMObjectParser.mandatoryAttribute);
		registerAttribute("increments",JFMObjectParser.optionalAttribute);
	}
	
	/* (non-Javadoc)
	 * @see jfm.xml.ObjectParser#initializeObject()
	 */
	@Override
	public void initializeObject() throws XMLObjectException,
			XMLSyntaxException {
		try {
			VariableType associated=Types.xmlToVariableType(getNamedAttribute("type"));
			double unitCost = Double.parseDouble(getNamedAttribute("unitCost"));
			double baseAmount = Double.parseDouble(getNamedAttribute("baseAmount"));
			double[] amounts = {baseAmount}; // First increment is always the base amount
			if ( this.attributeHasValue("increments") ){
				String[] incrs=getNamedAttribute("increments").split(",");
				amounts = new double[incrs.length+1];
				amounts[0]=baseAmount;
				for( int i = 0 ; i < incrs.length;i++){
					amounts[i+1]=baseAmount+Double.parseDouble(incrs[i]);
				}
			}
			obj=new CropInput(amounts,unitCost,associated);
		} catch ( XMLSyntaxException ex){
			throw new XMLSyntaxException("Syntax exception when parsing input "+ex.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see jfm.xml.ObjectParser#isPrimitive()
	 */
	protected boolean isPrimitive() {
		return false;
	}

	/* (non-Javadoc)
	 * @see jfm.xml.ObjectParser#name()
	 */
	@Override
	public String name() {
		return "CropInputParser";
	}

	/* (non-Javadoc)
	 * @see jfm.xml.ObjectParser#parsesNode()
	 */
	@Override
	public String parsesNode() {
		return "input";
	}

	/* (non-Javadoc)
	 * @see jfm.xml.ObjectParser#toString()
	 */
	@Override
	public String toString() {
		return "CropInputParser";
	}

}
