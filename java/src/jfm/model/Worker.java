package jfm.model;
import jfm.lp.ModelPrimitive;
import jfm.model.Types.*;
import jfm.lp.LPX;
import jfm.model.Types.ObjectiveType;
/** Calculates and holds the annual cost of a machine or labourer.
 * 
 * <em> immutable  </em> 
 * 
 * @author Ira Cooke */
public abstract class Worker extends ModelPrimitive {
	public final WorkerType type;
	public final WorkerSubType subType;
	public final LPX glpkVariableType;
	public final Double upperBound;
	public final Double lowerBound;
	public final LPX glpkBoundType;
	public abstract double getAnnualCost();
	public double getSolvedNumber(){
		return getSolution()[0];
	}
	protected void updateStructure(Object caller){
		setCoefficient(ObjectiveType.PROFIT,-getAnnualCost(),0);
	}
	Worker(WorkerType type_,WorkerSubType subtype_,LPX glpkType_,double lb,double ub,LPX boundType){
		type=type_;
		subType=subtype_;
		glpkVariableType=glpkType_;
		upperBound=ub;
		lowerBound=lb;
		glpkBoundType=boundType;
	};
	public abstract Worker copy();
	


	public String name(){return subType.name;};

	
	public String toString(){
		StringBuffer outstring = new StringBuffer();
		outstring.append(type.name);
		outstring.append(subType.name);
		outstring.append(":  Hours \n");
		for(OperationType otp:OperationType.values()){
//			if(hours.containsKey(otp)){
//				outstring.append(otp.shortName);
//				outstring.append(" ");
//				outstring.append(ReluMath.printVector(hours.get(otp)));
//			}
		}
		return outstring.toString();
	}
}
