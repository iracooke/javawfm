/**
 * 
 */
package jfm.xml;
import jfm.model.Types;
import jfm.model.Types.ELSCode;
import java.util.*;
import jfm.model.ELSOptionsComponent;
/**
 * @author iracooke
 *
 */
public class ELSOptionsParser extends JFMObjectParser {
	ELSOptionsParser(ObjectParser parent_){
		parent=parent_;
		this.registerAttribute("paymentRate", mandatoryAttribute);
		this.registerAttribute("pointsRate", mandatoryAttribute);
		this.registerAttribute("options", mandatoryAttribute);
	}
	/* (non-Javadoc)
	 * @see jfm.xml.ObjectParser#initializeObject()
	 */
	@Override
	public void initializeObject() throws XMLObjectException,
			XMLSyntaxException {
		double payr = Double.parseDouble(this.getNamedAttribute("paymentRate"));
		double pointsr = Double.parseDouble(this.getNamedAttribute("pointsRate"));
		
		String[] optionnames = this.getNamedAttribute("options").split(",");
		ArrayList<ELSCode> codes=new ArrayList<ELSCode>();
		for( int i=0;i<optionnames.length;i++){
			ELSCode elsc = Types.xmlToELSCode(optionnames[i]);
			codes.add(elsc);
		}
		obj=new ELSOptionsComponent(payr,pointsr,codes);
		
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
		return "ELS Options ";
	}

	/* (non-Javadoc)
	 * @see jfm.xml.ObjectParser#parsesNode()
	 */
	@Override
	public String parsesNode() {
		// TODO Auto-generated method stub
		return "elsoptions";
	}

	/* (non-Javadoc)
	 * @see jfm.xml.ObjectParser#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "elsoptions";
	}

}
