/**
 * 
 */
package jfm.model;

import jfm.model.Types.WorkerType;
import jfm.model.Types.WorkerSubType;
import jfm.lp.LPX;
import jfm.xml.XMLSyntaxException;

/** Specific Worker subclass describing machinery. 
 * @author iracooke
 *
 */
public class Machine extends Worker {
	
	/** Calculated value of the annualCost */
	private double annualCost;
	private LPX glpkVType;
	public Machine copy(){
		Machine newm=null;
		try {
			newm=new Machine(type,annualCost,glpkVType);
		} catch (XMLSyntaxException ex){
			throw new Error("Invalid match between machine type and subtype");
		}
		return newm;
	}
	public double getAnnualCost(){
		return annualCost;
	}
	private Machine(WorkerType type_,double annualCost_,LPX glpkVType_) throws XMLSyntaxException {
		super(type_,Types.xmlToWorkerSubType(type_.xmlname),glpkVType_,0,0,LPX.LPX_LO);
		annualCost=annualCost_;
		glpkVType=glpkVType_;
	}
	/** Construct a new Machine instance. 
	 * @param type_ The Worker type 
	 * @param purchaseCost_ The as new purchase price
	 * @param replace The number of years after which the machine should be replaced 
	 * @param depRate The average rate of depreciation per year for the given replacement Interval (as  percentage)
	 * @param repair Average annual cost of repairs as a percentage of purchase price 
	 * */
	public Machine(WorkerType type_,double purchaseCost,int replace,double depRate,double repair,LPX glpkVType) throws XMLSyntaxException {
		super(type_,Types.xmlToWorkerSubType(type_.xmlname),glpkVType,0,0,LPX.LPX_LO);
		double depValue=purchaseCost;
		for(int i=0;i<replace;i++){
			depValue-=depValue*(depRate*0.01);
		}
		double lifetimeCost=purchaseCost-depValue+(repair*0.01*purchaseCost*replace);
		annualCost=lifetimeCost/(double)replace;		
//		System.out.println("Annual Cost for "+type_+" is "+annualCost);
	}
	
}
