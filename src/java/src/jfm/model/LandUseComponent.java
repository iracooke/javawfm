package jfm.model;

import jfm.lp.ConstraintBuilder;
import jfm.lp.LPX;
import jfm.lp.MatrixElement;
import jfm.lp.MatrixRow;
import jfm.lp.ModelComponent;
import jfm.model.Crop.CropCopy;

public class LandUseComponent extends ModelComponent {
	// Mandatory land-use component!
	public final CroppingComponent cropping;
	// Optional
//	private Hedgerows hedgerows=null;// Might stay null
	private WoodlandComponent woodland=null;
	
	public double area; // Total farm area 
	private static String constraintTag="landUse";
	public LandUseComponent(CroppingComponent cropping_){
		super(ModelComponent.MCType.LANDUSE);
		cropping=cropping_;
		addConstraintBuilder(new TotalAreaConstraint());
	}

	/** Get the total area cropped. 
	 * @return The total area cropped */
	public double getArea(){return area;};
	/** Set the total area cropped 
	 * @param newArea The new total area cropped */	
	public void setArea(double newArea){
		area=newArea;
		cropping.requireStructureUpdate();
	}

	public ModelComponent copy() {
		throw new Error("Should not be called");
	}
	/*
	public double getHedgeLength(){
		if(hedgerows!=null){
			return hedgerows.getSolvedLength();
		} else {
			return 0;
		}
	}*/


	// Has no internal structure of its own .. acts as a constraint maker only 
	protected void initializeStructure() throws BadModelException {}

	protected String name() {
		return "LandUse";
	}

	// None to set 
	public void setFormulaVariables() {}


	protected void updateStructure() {
		// No structure to update
	}


	/** Constrains the total area to be equal to the area of all crops. 
	 * 
	 * 
	 * @author Ira Cooke 
	 * 
	 * */
	public final class TotalAreaConstraint extends ConstraintBuilder {
		public TotalAreaConstraint(){
			super(ConstraintBuilder.CBType.TOTALAREA,ModelComponent.MCType.LANDUSE);
		}
		protected void build(){
			int row=matrix.numRows();
			MatrixRow rowpointer =new MatrixRow(area,area,LPX.LPX_FX,
					row,constraintTag,type().tag);
			matrix.addRow(rowpointer);		
			row++;
			// Add cropping crops to this constraint
			for(CropYear ct:cropping.cropYears()){
				CropCopy cp=cropping.getCrop(ct.base).getCopy(ct.copyYear);
				rowpointer.addElement(new MatrixElement(cp.getDependentColumn(0),1));
			}
			
			// Hedgerows don't need to be added here but woodlands would do!
			if ( woodland!=null){
				throw new Error("Woodlands not yet implemented");
			}
		}
	}
	
	
}
