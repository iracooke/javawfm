package jfm.model;

import java.util.Map;

import jfm.lp.ConstraintBuilder;
import jfm.lp.LPX;
import jfm.lp.MatrixElement;
import jfm.lp.MatrixRow;
import jfm.lp.MatrixVariable;
import jfm.lp.ModelComponent;
import jfm.model.Crop.CropCopy;
import jfm.model.CropComplexityComponent.CropCounterMaxConstraint;
import jfm.model.CropComplexityComponent.CropCounterMinConstraint;
import jfm.model.Types.CropType;
import jfm.model.Types.ObjectiveType;

/** Model component for the Risk objective */
public class MOTADRiskComponent extends ModelComponent {
	private MatrixVariable riskVar=null;
	public MOTADRiskComponent(){
		super(ModelComponent.MCType.MOTADRISK);
		
		requireObjective(ObjectiveType.MOTADRISK);
		addConstraintBuilder(new MOTADRiskValueConstraint());		
	}
	
	@Override
	public ModelComponent copy() {
		return new MOTADRiskComponent();
	}

	@Override
	protected void initializeStructure() throws BadModelException {
		// We use a single variable representing total risk. 
		// It's coefficient is 1 as it is just a counter
		// We will constrain its value to the actual risk value later
		MatrixVariable newVariable=new MatrixVariable(1.0,
				0.0,0.0,
				LPX.LPX_LO,LPX.LPX_CV,matrix.numCols(),ObjectiveType.MOTADRISK);
		newVariable.setTag("motadrisk");
		riskVar=newVariable; // Keep a reference to this variable for later use in the constraint function
		matrix.addVariable(newVariable);	
	}

	@Override
	protected String name() {
		return "MotadRisk";
	}

	@Override
	public void setFormulaVariables() {
		// Do nothing .. no formula variables required
	}

	@Override
	protected void updateStructure() {
		// No need to update as a constant coefficient of 1 is used
	}
	
	// 
	public final class MOTADRiskValueConstraint extends ConstraintBuilder {
		public MOTADRiskValueConstraint(){
			super(ConstraintBuilder.CBType.MOTADRISKVALUE,ModelComponent.MCType.MOTADRISK);
		}
		protected void build(){
			int row=matrix.numRows();
			CroppingComponent cropping=getParent().cropping;
			// Just need a single row to constrain the value of risk to be correct
			MatrixRow rowpointer =new MatrixRow(0,0,LPX.LPX_FX,
					row,"MOTADRiskValue",type().tag);
			matrix.addRow(rowpointer);		
			row++;
			rowpointer.addElement(new MatrixElement(riskVar.column(),1)); // Counter
			
			for(Crop cp:cropping.getCrops().values()){	
				for( CropCopy cpp:cp.getYearCopies()){
					rowpointer.addElement(new MatrixElement(cpp.getDependentColumn(0),-1*Math.abs(cpp.getMOTADRisk())));
				}
			}
		}
	}

}
