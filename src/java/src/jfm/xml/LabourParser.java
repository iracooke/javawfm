/**
 * 
 */
package jfm.xml;

import jfm.model.Labour;
import jfm.model.Types.*;
import jfm.model.Types;
import jfm.lp.LPX;

/**
 * @author iracooke
 *
 */
public class LabourParser extends JFMObjectParser {

	WorkerType type;
	WorkerSubType subType;
	public LabourParser(ObjectParser parent_){
		parent=parent_;
		registerAttribute("wktype",JFMObjectParser.mandatoryAttribute);
		registerAttribute("wkstype",JFMObjectParser.mandatoryAttribute);
		registerAttribute("cost",JFMObjectParser.mandatoryAttribute);
		registerAttribute("glpkV",JFMObjectParser.mandatoryAttribute);
		registerAttribute("lowB",JFMObjectParser.optionalAttribute);
		registerAttribute("upB",JFMObjectParser.optionalAttribute);
		registerAttribute("glpkB",JFMObjectParser.optionalAttribute);
	}
	
	public void initializeObject() throws XMLSyntaxException,XMLObjectException{
		try {
		type = Types.xmlToWorkerType(getNamedAttribute("wktype"));
		subType=Types.xmlToWorkerSubType(getNamedAttribute("wkstype"));
		double cost = Double.parseDouble(getNamedAttribute("cost"));
		LPX glpkVType;
		glpkVType=LPX.xmlToGLPKType((getNamedAttribute("glpkV")));
		double lowb=0;
		if ( this.attributeHasValue("lowB")){
			lowb=Double.parseDouble(getNamedAttribute("lowB"));
		}
		double upb=0;
		if ( this.attributeHasValue("upB")){
			upb=Double.parseDouble(getNamedAttribute("upB"));
		}
		LPX glpkBType=LPX.LPX_LO; // default value
		if ( this.attributeHasValue("glpkB")){
			glpkBType=LPX.xmlToGLPKType(getNamedAttribute("glpkB"));
		}
		FarmParser top = (FarmParser)getRootParent();		
		obj = new Labour(type,subType,cost,glpkVType,lowb,upb,glpkBType);
		} catch (XMLSyntaxException tex){
			tex.printStackTrace();
			throw new XMLSyntaxException(tex.getMessage());
		}
/*
		Worker alias = (Worker)obj;
		for ( int i = 0 ; i < getParserList("csv").size();i++){
			TextParser csvp =(TextParser)getParserList("csv").get(i);
			String[] hours= (String[])csvp.getObject();
			OperationType optype=Types.xmlToOperationType(csvp.getNamedAttribute("optype"));
	//		alias.setHoursForOperationType(optype, TextConverter.toDouble(hours));
		}
*/
	}

	public String toString(){return "LabourParser";};
	public String parsesNode(){ return "labour";};
	public String name(){ 
		if ( type!=null){
			return type.xmlname;
		} else {
			if ( this.attributeHasValue("type")){
				try {
					return this.getNamedAttribute("type");
				} catch (XMLObjectException ex){
					throw new Error(ex.getMessage());
				}
			} 
			return "labour";
		}			
	}
	protected boolean isPrimitive(){ return false;};
}
