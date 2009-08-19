/**
 * 
 */
package jfm.model;
import jfm.model.Types.VariableType;
/** Object to hold information on an input cost for a crop
 * @author iracooke
 *
 */
public class CropInput extends VariableHolder {
	/** The amount of the input required in each successive year the crop can be grown
	 * Must remain final because other formulae depend on this*/
	private final double[] amounts;
	/** The formula variable associated with this input*/
	public final VariableType associatedVariable;
	/** Cost per unit for the input */
	private double unitCost;
	public CropInput(double[] amnts,double uCost,VariableType var){
		amounts=amnts;
		unitCost=uCost;
		associatedVariable =var;
		setVariable(var,amounts[0]);
	}
	
	public double getCost(int cropYear){
		if ( cropYear >= amounts.length){
			throw new Error("No amount specified for "+associatedVariable+" in year "+cropYear);
		} else {
			return amounts[cropYear]*unitCost;
		}
	}
	/** Perform a deep copy */
	public CropInput copy(){
		double[] newAmounts=new double[amounts.length];
		for( int i=0;i<amounts.length;i++){
			newAmounts[i]=amounts[i];			
		}
		return new CropInput(newAmounts,unitCost,associatedVariable);
	}
	
	public void setUnitCost(double c){
		unitCost=c;
	}
	
}
