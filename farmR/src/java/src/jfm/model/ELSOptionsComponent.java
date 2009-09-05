package jfm.model;

import jfm.lp.ConstraintBuilder;
import jfm.lp.LPX;
import jfm.lp.MatrixElement;
import jfm.lp.MatrixRow;
import jfm.lp.MatrixVariable;
import jfm.lp.ModelComponent;
import jfm.lp.ModelComponent.MCType;
import jfm.model.Crop.CropCopy;
import jfm.model.Types.CropType;
import jfm.model.Types.ObjectiveType;
import java.util.*;
import jfm.model.ELSOption;
import jfm.model.Types.ELSCode;

public class ELSOptionsComponent extends ModelComponent {

	private final ArrayList<ELSCode> availableOptions;
	private final ELS elsObject;
	
	public ELSOptionsComponent(double paymentRate_,double pointsRate_,ArrayList<ELSCode> availableOptions_){
		super(ModelComponent.MCType.ELSOPTIONS);
		elsObject=new ELS(paymentRate_,pointsRate_);
		requireObjective(ObjectiveType.ELSCOMPLEXITY);
		requireObjective(ObjectiveType.PROFIT);
		availableOptions = availableOptions_;

		// Put in a check to make sure the availableOptions correspond to ELSOptionModels
		
		addConstraintBuilder(new AcceptanceToELSConstraint());
		//Add constraint builders to ensure profit
	}
	
	@Override
	public ModelComponent copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void initializeStructure() throws BadModelException {
		elsObject.registerParent(this);		
		MatrixVariable newVariable=new MatrixVariable(elsObject.getAnnualSubsidy(),
				0.0,1.0,
				LPX.LPX_DB,LPX.LPX_IV,matrix.numCols(),ObjectiveType.PROFIT);
		newVariable.setTag(elsObject.name());
		matrix.addVariable(newVariable);	// The gross margin variable for this crop
		elsObject.registerVariable(newVariable,0);
		
	}

	@Override
	protected String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFormulaVariables() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void updateStructure() {
		elsObject.updateStructure(this);
		structureUpdateDone();
	}
	
	// For now assume entire area is eligible. (Need to look into ELS rules to refine this ). Roughly true for most arable farms
	public double eligibleArea(){
		Farm farm = getParent();
		return farm.landUse.area;
	}
	
	public final class AcceptanceToELSConstraint extends ConstraintBuilder {
		public AcceptanceToELSConstraint(){
			super(ConstraintBuilder.CBType.ELSACCEPT,ModelComponent.MCType.ELSOPTIONS);
		}
		protected void build(){
			Farm farm = getParent();
			int row=matrix.numRows();			
			MatrixRow rp=new MatrixRow(0,0,LPX.LPX_UP,row,"ELSAccept","ELSAccept");
			matrix.addRow(rp);	
			row++;
			rp.addElement(new MatrixElement(elsObject.getDependentColumn(0),elsObject.getPointsRequirement()));
			Set<ELSOption> optionsSet=farm.getELSOptionSet();
			// Now go through each of the options and calculate points contributions from them
			for(ELSOption op:optionsSet){
				if ( availableOptions.contains(op.code)){
					rp.addElement(new MatrixElement(op.getDependentColumn(0),-op.pointsPerUnit));
				}
			}
		}
	}
	
	public String printSolution(){
		StringBuffer strb = new StringBuffer();
		Farm farm =getParent();
		Set<ELSOption> optionsSet=farm.getELSOptionSet();
		// Now go through each of the options and calculate points contributions from them
		double pointsTotal=0;
		for(ELSOption op:optionsSet){
			if ( availableOptions.contains(op.code)){
				double len=op.getSolvedValue();
				strb.append(op.code+" "+op.code.description+" "+ len+"\n");
				pointsTotal+=len*op.pointsPerUnit;
			}
		}
		strb.append("Points Total "+pointsTotal+" of required "+elsObject.getPointsRequirement()+"\n");
		strb.append("ELS Subsidy "+elsObject.getAnnualSubsidy()*elsObject.isInELS()+"\n");
		return strb.toString();
	}

}
