/**
 * 
 */
package jfm.mou;

import jfm.model.Types;
import jfm.model.Types.ObjectiveType;
import jfm.xml.ObjectParser;
import jfm.xml.XMLObjectException;
import jfm.xml.XMLSyntaxException;
import jfm.mou.ObjectiveU;

/**
 * @author iracooke
 *
 */
public class ObjectiveUParser extends MOUObjectParser {

	ObjectiveUParser(ObjectParser parent_){
		parent=parent_;
		registerAttribute("type", mandatoryAttribute);
		registerAttribute("min", optionalAttribute);
		registerAttribute("max", optionalAttribute);
		registerAttribute("x",optionalAttribute);
		registerAttribute("y",optionalAttribute);
		registerAttribute("weight",mandatoryAttribute);
		registerAttribute("units",mandatoryAttribute);
	}
	@Override
	public void initializeObject() throws XMLObjectException,
			XMLSyntaxException {
		try {
			ObjectiveType type=Types.xmlToObjectiveType(getNamedAttribute("type"));
			double min,max;

			double rw=Double.parseDouble(getNamedAttribute("weight"));
			String units=getNamedAttribute("units");
			ObjectiveU theObj=null;
			if ( this.attributeHasValue("min") && this.attributeHasValue("max")){
				min=Double.parseDouble(getNamedAttribute("min"));
				max=Double.parseDouble(getNamedAttribute("max"));
				theObj=new ObjectiveU(type,units,min,max,rw);
			} else {
				theObj=new ObjectiveU(type,units,0,0,rw);
				theObj.isCurved=true;
			}
			if ( this.attributeHasValue("x") && this.attributeHasValue("y")){
				String[] xstrs=this.getNamedAttribute("x").split(",");
				String[] ystrs=this.getNamedAttribute("y").split(",");
				if ( xstrs.length!=ystrs.length){
					throw new Error("x and y length mismatch");
				}
				double[] x=new double[xstrs.length];
				double[] y =new double[ystrs.length];
				for(int i=0;i<xstrs.length;i++){
					x[i]=Double.parseDouble(xstrs[i]);
					y[i]=Double.parseDouble(ystrs[i]);
				}
				theObj.setCurve(x, y);
			}
			
			obj=theObj;
		} catch (XMLSyntaxException ex){
			throw new XMLSyntaxException(ex.getLocalizedMessage());
		}

	}

	@Override
	protected boolean isPrimitive() {
		return false;
	}

	@Override
	public String name() {
		return "ObjectiveUParser";
	}

	@Override
	public String parsesNode() {
		return "ObjectiveU";
	}

	@Override
	public String toString() {
		return "ObjectiveU Parser";
	}

}
