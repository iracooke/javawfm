/**
 * 
 */
package jfm.model;


import jfm.model.Types.WorkerType;
import jfm.model.Types.WorkerSubType;
import jfm.lp.LPX;
/** Specific Worker subclass describing labour. 
 * @author iracooke
 *
 */
public class Labour extends Worker {
	private double annualCost;
	
	public Labour copy(){
		return new Labour(type,subType,annualCost,glpkVariableType,upperBound,lowerBound,glpkBoundType);
	}
	public double getAnnualCost(){
		return annualCost;
	}

	/** Construct a new Labour instance. 
	 * @param type_ The Worker type 
	 * @param annualCost_ The Annual Wages for a Labourer 
	 * @param glpkVariableType One of either LPX.LPX_IV or LPX.LPX_CV for integer(full-time) or continuous (part time) labour 
	 */
	public Labour(WorkerType type_,WorkerSubType subtype_,double annualCost_,LPX glpkType,double lb,double ub,LPX boundType){
		super(type_,subtype_,glpkType,lb,ub,boundType);
		annualCost=annualCost_;
	}
}
