/**
 * 
 */
package jfm.model;

import java.util.HashSet;
import java.util.Set;

import jfm.lp.ConstraintBuilder;
import jfm.lp.LPX;
import jfm.lp.MatrixElement;
import jfm.lp.MatrixRow;
import jfm.lp.MatrixVariable;
import jfm.lp.ModelComponent;
import jfm.lp.ModelComponent.MCType;
import jfm.model.Types.ELSCode;
import jfm.model.Types.ObjectiveType;

/** For 2,4,and 6 m buffer strips.  Not yet implemented. Needs to have a cost that varies depending on crop. 
 * Should probably assume lowest gross margin crop and then implement constraints to ensure no more than available per crop
 * unit area.
 * 
 * @author iracooke
 *
 */
public class FieldMarginsComponent extends ELSOptionComponent {
	public static Set<ELSCode> elscodes = new HashSet<ELSCode>();
	static {
		elscodes.add(ELSCode.EE3);
		elscodes.add(ELSCode.EE2);
		elscodes.add(ELSCode.EF1);
	}
	
	public FieldMarginsComponent(double perhaEstablishmentCost,double perhaMaintenanceCost,double discountRate){ 
		super(ModelComponent.MCType.FIELDMARGINS);
		requireObjective(ObjectiveType.PROFIT);
		
		double perAreaCost= perhaEstablishmentCost*discountRate + perhaMaintenanceCost;
		
		options.put(ELSCode.EE3,new ELSOption(ELSCode.EE3,perAreaCost,ELSCode.EE3.defaultPoints));
		options.put(ELSCode.EE2,new ELSOption(ELSCode.EE2,perAreaCost,ELSCode.EE2.defaultPoints));
		options.put(ELSCode.EF1,new ELSOption(ELSCode.EF1,perAreaCost,ELSCode.EF1.defaultPoints));
		

		
		
	}
	/* (non-Javadoc)
	 * @see jfm.lp.ModelComponent#copy()
	 */
	@Override
	public ModelComponent copy() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jfm.lp.ModelComponent#initializeStructure()
	 */
	@Override
	protected void initializeStructure() throws BadModelException {
		for(ELSCode ecode:elscodes){
			MatrixVariable newVariable=new MatrixVariable(-options.get(ecode).costPerUnit,0.0,0.0,LPX.LPX_LO,LPX.LPX_CV,matrix.numCols(),ObjectiveType.PROFIT);
			newVariable.setTag(ecode.toString());	
			matrix.addVariable(newVariable);	
			options.get(ecode).registerVariable(newVariable, 0);
			options.get(ecode).registerParent(this);
		}
	}

	/* (non-Javadoc)
	 * @see jfm.lp.ModelComponent#name()
	 */
	@Override
	protected String name() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jfm.lp.ModelComponent#setFormulaVariables()
	 */
	@Override
	public void setFormulaVariables() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see jfm.lp.ModelComponent#updateStructure()
	 */
	@Override
	protected void updateStructure() {
		for(ELSOption op:options.values()){
			op.updateStructure(this);
		}
		structureUpdateDone();
	}
	
	public final class FieldMarginsMaxAreaConstraint extends ConstraintBuilder {
		public FieldMarginsMaxAreaConstraint(){
			super(ConstraintBuilder.CBType.FIELDMARGINSMAXAREA,ModelComponent.MCType.FIELDMARGINS);
		}
		protected void build(){
			// Not yet implemented
		}
	}

}
