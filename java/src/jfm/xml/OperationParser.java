package jfm.xml;

import java.util.*;

import jfm.model.Formula;
import jfm.model.Operation;
import jfm.model.Types;
import jfm.model.Types.*;
import jfm.utils.JFMMath;

/** Parse and Operation object */
final class OperationParser extends JFMObjectParser  {
	OperationType type;
	public OperationParser(ObjectParser parent_){
		parent=parent_;
		registerAttribute("type",JFMObjectParser.mandatoryAttribute);
		registerAttribute("workability",JFMObjectParser.mandatoryAttribute);
		registerAttribute("mingap",JFMObjectParser.optionalAttribute);
		registerAttribute("nonsequential",JFMObjectParser.optionalAttribute);
		registerAttribute("handover",JFMObjectParser.optionalAttribute);
	}
	public void initializeObject() throws XMLSyntaxException, XMLObjectException{
		FarmParser top = (FarmParser)getRootParent();
		try {
			int numPeriods = Integer.parseInt(top.getNamedAttribute("nperiods"));
			type = Types.xmlToOperationType(getNamedAttribute("type"));
			WorkabilityType workability=Types.xmlToWorkabilityType(getNamedAttribute("workability"));
		
			int[] perAllow=null;
			Map<WorkerType,Integer> numMachines=null;
			WorkerType firstMachine=Types.WorkerType.NOMACHINE;
			Formula wkrateF=null;
			double[] yieldpenalt=null;
			double[] costpenalt=null;
			double[] minareabyperiod=null;
			for ( int i = 0 ; i < getParserList("csv").size();i++){
				TextParser textp = (TextParser)getParserList("csv").get(i);
			// In all cases the length of the csv object should be numPeriods so check it even if length not specified
				if (textp.dataType!=null){			
					switch (textp.dataType){
					case ALLOW:
						perAllow=TextConverter.toPeriods(textp.getCSV(),numPeriods);
						break;
					case YIELDPENALTIES:
						yieldpenalt=TextConverter.toDouble(textp.getCSV());
						for ( int pen=0;pen< yieldpenalt.length;pen++){
							yieldpenalt[pen]*=0.01;
						}
						break;
					case COSTPENALTIES:
						costpenalt=TextConverter.toDouble(textp.getCSV());
						break;
					case MINAREA:
						minareabyperiod=TextConverter.toDouble(textp.getCSV());						
						for ( int pen=0;pen< minareabyperiod.length;pen++){
							minareabyperiod[pen]*=0.01;
						}
						break;
					case WKRATE:
						wkrateF = new Formula((String)textp.obj);
//						Location loc=new Location(1.5,500,10,26);
//						wkrateF.calc(loc);
//						System.out.print(wkrateF.debugPrint(loc));
//						System.out.println((String)csvp.obj+" from formula: "+wkrateF.calc(new Location(1.5,500,10,26)));
						break;
					case MACHINES:	
						numMachines=TextConverter.toNumMachines(textp.getCSV());
						firstMachine=numMachines.keySet().iterator().next();// A bit convoluted but this should get the first machine since numMachines is a linkedHashMap
						break;
					default:
						throw new XMLSyntaxException("The csv type "+textp.dataType+" is not valid for the operation tag");
					}
				}
			}	
			if ( perAllow ==null ){
				throw new XMLSyntaxException(" you must specify allowed periods for each operation");
			}
			if ( yieldpenalt == null){
				yieldpenalt=new double[perAllow.length];
				JFMMath.doubleZero(yieldpenalt);
			}
			if ( costpenalt == null){
				costpenalt=new double[perAllow.length];
				JFMMath.doubleZero(costpenalt);
			}
			
			if ( numMachines == null || wkrateF==null){
				if ( numMachines==null && wkrateF==null){
					numMachines=new HashMap<WorkerType,Integer>(); // Empty
//					wkrateF.initWithString("0.0");
				} else {
					throw new XMLSyntaxException("If numMachines or workrate are defined then both must be defined");
				}
			}
			int gap=0;
			if ( this.attributeHasValue("mingap")){ // Constraints for this are now implemented 
//				System.out.println("Warning: Gaps not implemented ");
				gap=Integer.parseInt(this.getNamedAttribute("mingap"));
			}
			obj = new Operation(type,numPeriods,workability,wkrateF,numMachines,gap,firstMachine);
			Operation objalias = (Operation)obj;
			objalias.setYieldPenalties(perAllow, yieldpenalt);
			objalias.setCostPenalties(perAllow,costpenalt);
			if ( minareabyperiod !=null){
				objalias.setMinAreas(perAllow,minareabyperiod); // This should only be set if it has nonzero elements 
			}
			if ( attributeHasValue("handover") ){
				String val = getNamedAttribute("handover");
				if ( val.equals("true") ){
					objalias.setHandOver();
				} else {
					throw new XMLSyntaxException("handover should only be set to true but was "+val);
				}
			}
			
			if ( attributeHasValue("nonsequential") ){
				String val = getNamedAttribute("nonsequential");
				if ( val.equals("true") ){
					objalias.setNonSequential();
				} else {
					throw new XMLSyntaxException("nonsequential should only be set to true but was "+val);
				}
			} 

		} catch (NumberFormatException ex){
			ex.printStackTrace();
			throw new XMLSyntaxException(ex.getMessage());
		} catch (XMLSyntaxException ex){
			ex.printStackTrace();
			throw new XMLSyntaxException(ex.getMessage());
		}

	}
	public String toString(){return "OperationParser";};
	public String parsesNode(){ return "operation";};
	protected boolean isPrimitive(){ return false;};
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
			return "operation";
		}
	};
}
