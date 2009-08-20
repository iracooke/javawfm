/** \internal This is the XML parser */
package jfm.xml;
import java.util.*;

import jfm.lp.LPX;
import jfm.model.*;
import jfm.model.Types;
import jfm.model.Types.*;


/** Parses a crop object */
final class CropParser extends JFMObjectParser {
	CropType id;
	
	public CropParser(ObjectParser parent_){
		parent=parent_;
		registerAttribute("yield",JFMObjectParser.mandatoryAttribute);
		registerAttribute("subsidy",JFMObjectParser.mandatoryAttribute);
		registerAttribute("price",JFMObjectParser.mandatoryAttribute);
//		registerAttribute("inputs",JFMObjectParser.mandatoryAttribute);
		registerAttribute("id",JFMObjectParser.mandatoryAttribute);
		registerAttribute("disease",JFMObjectParser.mandatoryAttribute);
		registerAttribute("yield2",JFMObjectParser.optionalAttribute);
		registerAttribute("price2",JFMObjectParser.optionalAttribute);
		registerAttribute("prisk",JFMObjectParser.optionalAttribute);
		registerAttribute("yrisk",JFMObjectParser.optionalAttribute);
		registerAttribute("eostdev",JFMObjectParser.optionalAttribute);
		registerAttribute("fixedCostsApprox",JFMObjectParser.optionalAttribute);
		registerAttribute("yieldReduction",JFMObjectParser.optionalAttribute);
		registerAttribute("selfRotCost",JFMObjectParser.optionalAttribute);
		registerAttribute("allowContinuous",JFMObjectParser.mandatoryAttribute);
		registerAttribute("xml:base",JFMObjectParser.optionalAttribute); // Allow this node to be included from external xml files
	}
	public void initializeObject() throws XMLSyntaxException,XMLObjectException{
		DiseaseType disease;
		Crop objalias;
		try {
			id = Types.xmlToCropType(this.getNamedAttribute("id"));
			disease=Types.xmlToDiseaseType(this.getNamedAttribute("disease"));
			Rotation rotation=null;
			if ( parserListExists("rotation")){
				rotation=(Rotation)getParserList("rotation").get(0).obj;
			} else {
				rotation=new Rotation(id,null,null); // Create a blank rotation
			}
			
			objalias=new Crop(id,disease,rotation,Boolean.parseBoolean(this.getNamedAttribute("allowContinuous")));
//			objalias.setPrimaryYield(Double.parseDouble(this.getNamedAttribute("yield")));
			objalias.resetPrimaryYield(this.getNamedAttribute("yield"));			
			objalias.resetSubsidy(Double.parseDouble(this.getNamedAttribute("subsidy")));
			objalias.resetPrimaryPrice(Double.parseDouble(this.getNamedAttribute("price")));
			if ( this.attributeHasValue("prisk") || this.attributeHasValue("yrisk")){
				if (!(attributeHasValue("prisk") && attributeHasValue("yrisk"))){
					throw new Error("If prisk is defined then so must drisk and vice versa");
				}
				double pr = Double.parseDouble(this.getNamedAttribute("prisk"));
				double yr = Double.parseDouble(this.getNamedAttribute("yrisk"));
				objalias.resetPriceRisk(pr);
				objalias.resetYieldRisk(yr);
			}
			if ( this.attributeHasValue("eostdev")){
				objalias.resetEOStandardDeviation(Double.parseDouble(this.getNamedAttribute("eostdev")));
			}
			if ( this.attributeHasValue("fixedCostsApprox")){
				objalias.resetApproximateFixedCostsPerHectare(Double.parseDouble(this.getNamedAttribute("fixedCostsApprox")));
			}
			
//			objalias.setInputCosts(Double.parseDouble(this.getNamedAttribute("inputs")));
			
			if ( this.attributeHasValue("yieldReduction") || this.attributeHasValue("selfRotCost")){
				double[] yieldReductions={0};
				double[] selfCosts={0};
				if ( !(this.attributeHasValue("yieldReduction") && this.attributeHasValue("selfRotCost"))){
					throw new XMLSyntaxException("If yieldReduction is defined then selfRotCost must be also be defined with the same length");
				}
				String[] splitstr = this.getNamedAttribute("yieldReduction").split(",");
				yieldReductions=new double[splitstr.length+1];// The first value represents the original year but values given in the xml file start with the second year
				yieldReductions[0]=0; 
				for ( int i=0;i<splitstr.length;i++){
					yieldReductions[i+1]=Double.parseDouble(splitstr[i]);
				}
				splitstr = this.getNamedAttribute("selfRotCost").split(",");
				if ( yieldReductions.length==splitstr.length+1){
					selfCosts=new double[splitstr.length+1];
					selfCosts[0]=0;
					for ( int i=0;i<splitstr.length;i++){
						selfCosts[i+1]=Double.parseDouble(splitstr[i]);
					}					
				} else {
					throw new XMLSyntaxException("yieldReduction and selfRotCost must have the same length");
				}
				objalias.setSuccessiveRotationCosts(yieldReductions, selfCosts);
//				System.out.println("num copies "+objalias.getYearCopies().size()+" for "+objalias.type);
			}
			
			
			if ( this.attributeHasValue("yield2") || this.attributeHasValue("price2")){
				if (!(this.attributeHasValue("yield2") && this.attributeHasValue("price2"))){
					throw new XMLSyntaxException("If yield2 is defined then price2 must also be defined");
				}
				
				objalias.resetSecondaryYield(getNamedAttribute("yield2"));
				objalias.resetSecondaryPrice(Double.parseDouble(this.getNamedAttribute("price2")));
			}
			
		} catch (NumberFormatException ex){
			throw new XMLSyntaxException("A numerical value in the definition of "+id.xmlname+" cannot be parsed as numeric"+ex.getMessage());
		} catch (XMLSyntaxException ex){
			throw new XMLSyntaxException(ex.getMessage());
		}


		if (parserListExists("operation")){			
			Iterator<ObjectParser> it=getParserList("operation").iterator();
			while(it.hasNext()){
				objalias.addOperation((Operation)it.next().getObject());
			}
		}
		if ( parserListExists("input")){
			for(ObjectParser op:getParserList("input")){
				CropInput ci=(CropInput)op.getObject();
				objalias.resetInput(ci.associatedVariable, ci);
			}
		}
		obj=objalias;
	}
	
	public String toString(){ return "CropParser";};
	public String parsesNode(){ return "crop";};
	public String name() { 
		if ( id!=null){
			return id.xmlname;
		}else {
			if ( this.attributeHasValue("id")){
				try {
					return this.getNamedAttribute("id");
				} catch (XMLObjectException ex){
					throw new Error(ex.getMessage());
				}
			}
			return "crop";
		}
	}
	protected boolean isPrimitive(){ return false;};
}
