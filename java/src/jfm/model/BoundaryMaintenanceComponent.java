/**
 * 
 */
package jfm.model;

import java.util.Set;

import jfm.lp.ConstraintBuilder;
import jfm.lp.LPX;
import jfm.lp.MatrixElement;
import jfm.lp.MatrixRow;
import jfm.lp.MatrixVariable;
import jfm.lp.ModelComponent;
import jfm.model.Types.ELSCode;
import jfm.model.Types.ObjectiveType;
import java.util.*;
/**
 * @author iracooke
 *
 */
public class BoundaryMaintenanceComponent extends ELSOptionComponent {

	public static Set<ELSCode> elscodes = new HashSet<ELSCode>();
	static {
		elscodes.add(ELSCode.EB1);
		elscodes.add(ELSCode.EB2);
		elscodes.add(ELSCode.EB3);
		elscodes.add(ELSCode.EB6);
		elscodes.add(ELSCode.EB10);		
	}
	
	// Create a default boundary option object
	public BoundaryMaintenanceComponent(double eb1cost,double eb2cost,double eb3cost,double eb6cost,double eb10cost){
		// Feed in options for EB1,EB2,EB3,EB6,EB1,EB10 etc here as an array
		super(ModelComponent.MCType.ELSBOUNDARIES);
		requireObjective(ObjectiveType.HEDGEROWS);
		requireObjective(ObjectiveType.DITCHES);
		requireObjective(ObjectiveType.PROFIT);
		addConstraintBuilder(new ConserveHedgeLengthConstraint());
		addConstraintBuilder(new ConserveDitchLengthConstraint());
//		startPeriod=startP;

		options.put(ELSCode.EB1,new ELSOption(ELSCode.EB1,eb1cost,ELSCode.EB1.defaultPoints));
		options.put(ELSCode.EB2,new ELSOption(ELSCode.EB2,eb1cost,ELSCode.EB2.defaultPoints));
		options.put(ELSCode.EB3,new ELSOption(ELSCode.EB3,eb1cost,ELSCode.EB3.defaultPoints));
		options.put(ELSCode.EB6,new ELSOption(ELSCode.EB6,eb1cost,ELSCode.EB6.defaultPoints));
		options.put(ELSCode.EB10,new ELSOption(ELSCode.EB10,eb1cost,ELSCode.EB10.defaultPoints));

	};
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
	
	public final class ConserveHedgeLengthConstraint extends ConstraintBuilder {
		public ConserveHedgeLengthConstraint(){
			super(ConstraintBuilder.CBType.ELSHEDGELENGTH,ModelComponent.MCType.ELSBOUNDARIES);
		}
		protected void build(){
			Farm farm = getParent();
			int row=matrix.numRows();			
			MatrixRow rp=new MatrixRow(0,0,LPX.LPX_LO,row,"ELS Conserve Hedges","ELS Conserve Hedges");
			matrix.addRow(rp);	
			row++;
			BoundaryComponent hedgesmc = (BoundaryComponent)farm.getModelComponent(MCType.HEDGEROWS);
			rp.addElement(new MatrixElement(hedgesmc.getBoundaryCounterColumn(),1));
			// Add an element to this row for each of the hedge options
			rp.addElement(new MatrixElement(options.get(ELSCode.EB1).getDependentColumn(0),-1));
			rp.addElement(new MatrixElement(options.get(ELSCode.EB2).getDependentColumn(0),-1));
			rp.addElement(new MatrixElement(options.get(ELSCode.EB3).getDependentColumn(0),-1));
			rp.addElement(new MatrixElement(options.get(ELSCode.EB10).getDependentColumn(0),-1));
		}
	}
	
	public final class ConserveDitchLengthConstraint extends ConstraintBuilder {
		public ConserveDitchLengthConstraint(){
			super(ConstraintBuilder.CBType.ELSDITCHLENGTH,ModelComponent.MCType.ELSBOUNDARIES);
		}
		protected void build(){
			Farm farm = getParent();
			int row=matrix.numRows();			
			MatrixRow rp=new MatrixRow(0,0,LPX.LPX_LO,row,"ELS Hedges and Ditches","ELS Hedges and Ditches");
			matrix.addRow(rp);	
			row++;
			DitchLengthComponent ditchesmc = (DitchLengthComponent)farm.getModelComponent(MCType.DITCHES);
			rp.addElement(new MatrixElement(ditchesmc.getBoundaryCounterColumn(),1));
			rp.addElement(new MatrixElement(options.get(ELSCode.EB6).getDependentColumn(0),-1));
			rp.addElement(new MatrixElement(options.get(ELSCode.EB10).getDependentColumn(0),-1));
		}
	}

}
