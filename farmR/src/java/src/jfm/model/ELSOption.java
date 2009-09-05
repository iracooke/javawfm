/**
 * 
 */
package jfm.model;

import jfm.lp.ModelPrimitive;
import jfm.model.Types.ObjectiveType;
import jfm.model.Types.ELSCode;

/**
 * @author iracooke
 *
 */
public class ELSOption extends ModelPrimitive {

	
	
	public final double pointsPerUnit;
	public final double costPerUnit;
	public final ELSCode code;
	
	public ELSOption(ELSCode code_,double cost_,double points_){
		code=code_;
		costPerUnit=cost_;
		pointsPerUnit=points_;
	}
	/* (non-Javadoc)
	 * @see jfm.lp.ModelPrimitive#name()
	 */
	@Override
	public String name() {
		return "ELSOption "+code;
	}
	
	Double getSolvedValue(){
		return getSolution()[0];
	}

	/* (non-Javadoc)
	 * @see jfm.lp.ModelPrimitive#updateStructure(java.lang.Object)
	 */
	@Override
	protected void updateStructure(Object caller) {
		setCoefficient(ObjectiveType.PROFIT,-costPerUnit,0);
	}

}
