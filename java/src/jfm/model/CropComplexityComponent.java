/**
 * 
 */
package jfm.model;


import jfm.lp.*;
import jfm.model.Crop.CropCopy;
import jfm.model.Types.CropType;
import jfm.model.Types.ObjectiveType;
import java.util.*;

/** 
 * @author iracooke
 *
 */
public class CropComplexityComponent extends ModelComponent {
	/** Threshold area of crop grown as a percentage of the farm size (inversed) */
	private double threshold=0;
	private Map<CropType,MatrixVariable> counter = new HashMap<CropType,MatrixVariable>();
	public CropComplexityComponent(double threshold_){
		super(ModelComponent.MCType.CROPCOMPLEXITY);
		threshold=threshold_;
		
		requireObjective(ObjectiveType.CROPCOMPLEXITY);
		addConstraintBuilder(new CropCounterMinConstraint());
		addConstraintBuilder(new CropCounterMaxConstraint());		
	}
	public CropComplexityComponent copy(){
		return new CropComplexityComponent(threshold);
	}
	
	/* (non-Javadoc)
	 * @see jfm.lp.ModelComponent#initializeStructure()
	 */
	@Override
	protected void initializeStructure() {
		counter.clear();
		Farm farm = getParent();
		CroppingComponent cropping = farm.cropping;
		Location location=getParent().location();
		for(CropType ct:cropping.baseCropTypes()){
			// For every crop type we create an Integer variable which is used as a counter 				
			MatrixVariable newVariable=new MatrixVariable(1.0,
					0.0,1.0,
					LPX.LPX_DB,LPX.LPX_IV,matrix.numCols(),ObjectiveType.CROPCOMPLEXITY);
				newVariable.setTag(ct.xmlname+"_cnt");
				matrix.addVariable(newVariable);	
				counter.put(ct, newVariable);
		}
	}
	public void setFormulaVariables(){}

	// The constraint which ensures that the counter is 0 when xi is less than the threshold for crop to be recognised.
	public final class CropCounterMinConstraint extends ConstraintBuilder {
		public CropCounterMinConstraint(){
			super(ConstraintBuilder.CBType.CROPCOUNTERMIN,ModelComponent.MCType.CROPCOMPLEXITY);
		}
		protected void build(){
			int row=matrix.numRows();
			CroppingComponent cropping=getParent().cropping;
			LandUseComponent landUse=getParent().landUse;
			for ( Map.Entry<CropType, MatrixVariable> entry:counter.entrySet()){
				MatrixRow rowpointer =new MatrixRow(0,0,LPX.LPX_LO,
						row,"CropComplexity",type().tag);
				matrix.addRow(rowpointer);		
				row++;
				rowpointer.addElement(new MatrixElement(entry.getValue().column(),-1*threshold*landUse.getArea()));
				Crop cp=cropping.getCrop(entry.getKey());
				for ( CropCopy cpcpy:cp.getYearCopies()){
					rowpointer.addElement(new MatrixElement(cpcpy.getDependentColumn(0),1));
				}
			}
			
		}
	}
	
	/** This constraint ensures that no single crop may 
	 * have an area greater than the total area and that if any 
	 * of a particular crop exists then the counter variable must equal 1.*/
	public final class CropCounterMaxConstraint extends ConstraintBuilder {
		public CropCounterMaxConstraint(){
			super(ConstraintBuilder.CBType.CROPCOUNTERMAX,ModelComponent.MCType.CROPCOMPLEXITY);
		}
		protected void build(){
			int row=matrix.numRows();
			CroppingComponent cropping=getParent().cropping;
			LandUseComponent landUse=getParent().landUse;
			for ( Map.Entry<CropType, MatrixVariable> entry:counter.entrySet()){
				MatrixRow rowpointer =new MatrixRow(0,0,LPX.LPX_LO,
						row,"CropComplexity",type().tag);
				matrix.addRow(rowpointer);		
				row++;
				rowpointer.addElement(new MatrixElement(entry.getValue().column(),landUse.getArea()));
				Crop cp=cropping.getCrop(entry.getKey());
				for ( CropCopy cpcpy:cp.getYearCopies()){
					rowpointer.addElement(new MatrixElement(cpcpy.getDependentColumn(0),-1));
				}
			}
			
		}
	}

	/* (non-Javadoc)
	 * @see jfm.lp.ModelComponent#name()
	 */
	@Override
	protected String name() {
		return "CropComplexity";
	}

	/* (non-Javadoc)
	 * @see jfm.lp.ModelComponent#updateStructure()
	 */
	@Override
	protected void updateStructure() {
		// TODO Auto-generated method stub

	}
	/**
	private class CropCounter extends ModelPrimitive {
		public String name(){ return "CropCounter";};
		protected void updateStructure(Object caller ) {
			// Do nothing. This Component has constant coefficients equal to 1
		}
	}
*/
}
