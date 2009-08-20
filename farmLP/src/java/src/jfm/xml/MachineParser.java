package jfm.xml;

import jfm.lp.LPX;
import jfm.model.Types;
import jfm.model.Machine;
import jfm.model.Types.*;

/** Parses a Worker object */
final class MachineParser extends JFMObjectParser {
	WorkerType type;
	public MachineParser(ObjectParser parent_){
		parent=parent_;
		registerAttribute("wktype",JFMObjectParser.mandatoryAttribute);
		registerAttribute("cost",JFMObjectParser.mandatoryAttribute);
		registerAttribute("replace",JFMObjectParser.mandatoryAttribute);
		registerAttribute("depreciationRate",JFMObjectParser.mandatoryAttribute);
		registerAttribute("repairCosts",JFMObjectParser.mandatoryAttribute);
		registerAttribute("glpkV",JFMObjectParser.optionalAttribute);
	}
	
	public void initializeObject() throws XMLSyntaxException,XMLObjectException{
		try {
		type = Types.xmlToWorkerType(getNamedAttribute("wktype"));
		double cost = Double.parseDouble(getNamedAttribute("cost"));
		int replace =Integer.parseInt(getNamedAttribute("replace"));
		double depRate = Double.parseDouble(getNamedAttribute("depreciationRate"));
		double repair = Double.parseDouble(getNamedAttribute("repairCosts"));
		
		LPX glpkVType;
		if ( this.attributeHasValue("glpkV")){
			glpkVType=LPX.xmlToGLPKType((getNamedAttribute("glpkV")));
		} else {
			glpkVType=LPX.LPX_CV;
		}
		obj = new Machine(type,cost,replace,depRate,repair,glpkVType);
		} catch (XMLSyntaxException tex){
			tex.printStackTrace();
			throw new XMLSyntaxException(tex.getMessage());
		}
	}

	public String toString(){return "MachineParser";};
	public String parsesNode(){ return "machine";};
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
			return "machine";
		}			
	}
	protected boolean isPrimitive(){ return false;};
}
