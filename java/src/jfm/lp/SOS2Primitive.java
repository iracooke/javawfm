/**
 * 
 */
package jfm.lp;
import java.util.*;

import jfm.model.Types.ObjectiveType;

/**
 * @author iracooke
 *
 */
public class SOS2Primitive extends ModelPrimitive {
	private final double[] xVals;
	private final double[] yVals;
	private final ObjectiveType objType;
	private final String label;
	
	public SOS2Primitive(String label_,ObjectiveType objType_,double[] x,double[] y){
		xVals=x;
		yVals=y;
		objType=objType_;
		label=label_;
		if ( xVals.length != yVals.length){
			throw new Error("x and y lengths differ");
		}
	}
	
	/** Creates all necessary binary variables and weighting variables to deal with the 
	 * piecewise linear function expressed by xVals and yVals */
	public void initializeStructure(){
		if ( !this.isRegistered()){
			throw new Error("Can't initialize structure for unregistered SOS2Primitive");
		}
		Matrix matrix=parentComponent.matrix;
		int col=0;
		// First create a variable for the x value itself but have it contribute nothing to the objective 
		MatrixVariable newVariable=new MatrixVariable(0,0,1.0,LPX.LPX_FR,LPX.LPX_CV,matrix.numCols(),ObjectiveType.SOS2DUMMY);
		newVariable.setTag(objType+label+"_x");
		matrix.addVariable(newVariable);
		registerVariable(newVariable,col);
		col++;
		// Now create the weighting variables 
		for(int i=0;i<xVals.length;i++){
			newVariable=new MatrixVariable(yVals[i],
					0.0,1.0,
					LPX.LPX_DB,LPX.LPX_CV,matrix.numCols(),objType);
			newVariable.setTag(objType+label+"y"+i);
			matrix.addVariable(newVariable);
			registerVariable(newVariable,col);
			col++;
		}
		// Now the binary variables
		for(int i=0;i<(xVals.length-1);i++){
			newVariable=new MatrixVariable(0,
					0.0,1.0,
					LPX.LPX_DB,LPX.LPX_IV,matrix.numCols(),ObjectiveType.SOS2DUMMY);
			newVariable.setTag(label+"b"+i);
			matrix.addVariable(newVariable);
			registerVariable(newVariable,col);
			col++;
		}		
	}
	public double getSolvedValue(){
		return getSolution()[0];
	}
	
	/** This creates a link between the variable associated with this SOS2 Primitive object 
	 * and a particular objective. Sets the coefficient on the SOS x to 1*/
	public static void bindToObjective(SOS2Primitive prim,Objective obj,Matrix matrix){
// 		prim.getDependent(0).setCoefficientForObjective(prim.objType, 1);
		int row=matrix.numRows();
		MatrixRow rowpointer =new MatrixRow(0,0,LPX.LPX_FX,
				row,"Bind","BindingRow"+prim.objType);
		matrix.addRow(rowpointer);
		row++;
		rowpointer.addElement(new MatrixElement(prim.getDependentColumn(0),-1));
//		System.out.println("building binding row with "+obj.variables().size()+" "+obj.type);
		for(MatrixVariable var:obj.variables()){
			rowpointer.addElement(new MatrixElement(var.column(),var.getCoefficientForObjective(obj.type)));
		}
		matrix.flagForAlwaysRebuild();
	}
	
	/** Creates a link between two SOS variables so that they have exactly the same value */
	public static void buildLink(SOS2Primitive left,SOS2Primitive right,Matrix matrix){
		int row=matrix.numRows();
		// First the reference row 		
		MatrixRow rowpointer =new MatrixRow(0,0,LPX.LPX_FX,
				row,"Link","LinkingRow"+left.label+right.label);
		matrix.addRow(rowpointer);
		row++;
		rowpointer.addElement(new MatrixElement(left.getDependentColumn(0),-1));
		rowpointer.addElement(new MatrixElement(right.getDependentColumn(0),1));
	}
	
	public static void buildConstraints(SOS2Primitive constraint,Matrix matrix){
//		System.out.println("Building constraints for "+constraint.label+" "+constraint.objType);
		int row=matrix.numRows();
		// First the reference row 		
		MatrixRow rowpointer =new MatrixRow(0,0,LPX.LPX_FX,
				row,constraint.label,"ReferenceRow");
		matrix.addRow(rowpointer);		
		row++;
		rowpointer.addElement(new MatrixElement(constraint.getDependentColumn(0),-1));
		for(int i=0;i<constraint.xVals.length;i++){
			rowpointer.addElement(new MatrixElement(constraint.getDependentColumn(i+1),constraint.xVals[i]));
		}
		
		// Now the convexity row
		rowpointer =new MatrixRow(1,1,LPX.LPX_FX,
				row,constraint.label,"ConvexityRow");
		matrix.addRow(rowpointer);		
		row++;
		for(int i=0;i<constraint.xVals.length;i++){
			rowpointer.addElement(new MatrixElement(constraint.getDependentColumn(i+1),1));
		}
		
		int firstBinVarColumn=constraint.xVals.length+1;
		// Constraint to ensure one and only one non-zero binary variable
		rowpointer =new MatrixRow(1,1,LPX.LPX_FX,
				row,constraint.label,"BinaryNZRow");
		matrix.addRow(rowpointer);		
		row++;
		for(int i=0;i<(constraint.xVals.length-1);i++){
			rowpointer.addElement(new MatrixElement(constraint.getDependentColumn(i+firstBinVarColumn),1));
		}
		
		// Now we need several rows to ensure the SOS2 property
		for( int i=0;i<constraint.xVals.length;i++){
			rowpointer =new MatrixRow(0,0,LPX.LPX_LO,
					row,constraint.label,"SOS2Row"+i);
			matrix.addRow(rowpointer);		
			row++;
			rowpointer.addElement(new MatrixElement(constraint.getDependentColumn(i+1),-1));
			for( int b=0;b<2;b++){
				int bc=i-b+firstBinVarColumn; // Column index for associated binary variable
				if ( bc >=firstBinVarColumn && bc < (firstBinVarColumn+constraint.xVals.length-1)){
					rowpointer.addElement(new MatrixElement(constraint.getDependentColumn(bc),1));
				}
			}
			
		}
		

	}
	
	/* (non-Javadoc)
	 * @see jfm.lp.ModelPrimitive#name()
	 */
	@Override
	public String name() {
		return "SOS2 Primitive";
	}

	protected void updateStructure(Object caller) {} // Variables not mutable

}
