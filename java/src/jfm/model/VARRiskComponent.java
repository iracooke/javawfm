/**
 * 
 */
package jfm.model;

import jfm.lp.ConstraintBuilder;
import jfm.lp.LPX;
import jfm.lp.MatrixElement;
import jfm.lp.MatrixRow;
import jfm.lp.MatrixVariable;
import jfm.lp.ModelComponent;
import jfm.model.Crop.CropCopy;
import jfm.model.MOTADRiskComponent.MOTADRiskValueConstraint;
import jfm.model.Types.ObjectiveType;

/**
 * @author iracooke
 *
 */
public class VARRiskComponent extends ModelComponent {

	private MatrixVariable riskVar=null;
	private double alpha=0;
	private double offset=0;
	public VARRiskComponent(double alpha_,double offset_){
		super(ModelComponent.MCType.VARRISK);
		
		requireObjective(ObjectiveType.VARRISK);
		addConstraintBuilder(new VARRiskValueConstraint());

		alpha=alpha_;
		offset=offset_;
		
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
		// We use a single variable representing total risk. 
		// It's coefficient is 1 as it is just a counter
		// We will constrain its value to the actual risk value later
		MatrixVariable newVariable=new MatrixVariable(1.0,
				0.0,0.0,
				LPX.LPX_LO,LPX.LPX_CV,matrix.numCols(),ObjectiveType.VARRISK);
		newVariable.setTag("varrisk");
		riskVar=newVariable; // Keep a reference to this variable for later use in the constraint function
		matrix.addVariable(newVariable);	
	}

	/* (non-Javadoc)
	 * @see jfm.lp.ModelComponent#name()
	 */
	@Override
	protected String name() {
		// TODO Auto-generated method stub
		return "VarRisk";
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
		// TODO Auto-generated method stub

	}

	public final class VARRiskValueConstraint extends ConstraintBuilder {
		public VARRiskValueConstraint(){
			super(ConstraintBuilder.CBType.VARRISKVALUE,ModelComponent.MCType.VARRISK);
		}
		protected void build(){
			int row=matrix.numRows();
			CroppingComponent cropping=getParent().cropping;
			// Just need a single row to constrain the value of risk to be correct
			MatrixRow rowpointer =new MatrixRow(0,0,LPX.LPX_FX,
					row,"VARRiskValue",type().tag);
			matrix.addRow(rowpointer);		
			row++;
			rowpointer.addElement(new MatrixElement(riskVar.column(),1)); // Counter
			
			for(Crop cp:cropping.getCrops().values()){	
				for( CropCopy cpp:cp.getYearCopies()){
					rowpointer.addElement(new MatrixElement(cpp.getDependentColumn(0),-1*Math.abs(cpp.getVARRisk(alpha,offset))));
				}
			}
			
		}
	}
	
}
