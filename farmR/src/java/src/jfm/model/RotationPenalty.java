package jfm.model;
import jfm.lp.ModelPrimitive;
import jfm.model.Crop.*;
import jfm.model.Types.ObjectiveType;
/** Penalty for a particular to-from rotation combination in a particular period. 
 * To understand the relationship between this class and the related Rotation class it is 
 * useful to note that for every Rotation instance there should be at least nPeriods instances
 *  of this class. 
 *  
 *  @author Ira Cooke */
public final class RotationPenalty extends ModelPrimitive {
	/** Financial Penalty per unit area for this rotation */
	private double penalty; 
	public RotationPenalty(double pen){
		penalty=pen;
	}
	public double penalty(){return penalty;};
	public void setPenalty(double[] yieldAndCost,CropCopy cp,Location loc){
		penalty=0;
		penalty+=cp.costOfYieldPenalty(yieldAndCost[0],loc);
		penalty+=yieldAndCost[1];
//		System.out.println("Penalty set to "+penalty+" "+yieldAndCost[0]+" "+yieldAndCost[1]);
	}
	
	public double area(int p){
		if ( dependentExists(p)){
			return getDependent(p).solution();
		} else {
			return 0;
		}
	}
	
	protected void updateStructure(Object caller){
		for ( Integer p:dependentsKeys()){
			setCoefficient(ObjectiveType.PROFIT,-penalty,p);
		}
	}
	
	public String name(){ return "Rotation Penalty";};
}
