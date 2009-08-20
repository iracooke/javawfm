package jfm.lp;


import java.util.*;

import jfm.model.*;
import jfm.model.Types.ObjectiveType;

/** \internal Abstract class for sub-models that contribute objectives, variables or constraints to the matrix 
 * The ModelComponent abstract class defines a template on which you can build new conceptually distinct sections of the 
 * overall model. For example three ModelComponent instance classes are used to create the main profit model, Cropping, Rotations and Workers.
 * New components of the model that correspond to new objectives should always be defined by extending the ModelComponent abstract class. 
 * 
 * Creating a new instance of ModelComponent
 - Overload the ModelComponent class. If you use an IDE like eclipse it should create necessary method stubs for you automatically.
 - Write a public constructor for the new class. This constructor should include a call to the constructor of the parent ( this class ). If you are defining a brand new ModelComponent you will need to add its name to the MCType enum
 - Add code to initialise the MatrixVariable objects that this component uses in the function initialiseStructure().. see CropComplexity for a simple example and Cropping for a more complex one. Sometimes (eg Stubbles ) you will not need to create new MatrixVariable objects but add references to a new objective to existing ones.
 - Add a ConstraintBuilder class to builder your constraints if needed.
 - Add a call to addConstraintBuilder in the constructor to add your constraint builder to the initialization process.
 - Overload the name() function.
 - Document your new ModelComponent 
 - Document any constraints you make by making an addition to the file dox/constraints.dox
 
 Examples of ModelComponents are; Cropping, Rotations, Workers, CropComplexity, FreeTime, Stubbles .. see the code for these classes for inspiration!
 
 * 
 * */
public abstract class ModelComponent  {
	public enum MCType {
		CROPPING,ROTATIONS,WORKERS,STUBBLES,CROPCOMPLEXITY,FREETIME,HEDGEROWS,DITCHES,WOODLAND,LANDUSE,MOTADRISK,VARRISK,ELSOPTIONS,ELSBOUNDARIES,FIELDMARGINS;
	}
	private final Set<ObjectiveType> requiredObjectives=new HashSet<ObjectiveType>();
	public final MCType type;
	private Farm parent=null;
	protected SOS2Primitive curve=null;
	private boolean requiresStructureUpdate=true;
	public final Map<ConstraintBuilder.CBType,ConstraintBuilder> constraintBuilders = new LinkedHashMap<ConstraintBuilder.CBType,ConstraintBuilder>();
	protected void requireObjective(ObjectiveType objT){
		requiredObjectives.add(objT);
	}
	public Set<ObjectiveType> getRequiredObjectives(){
		return Collections.unmodifiableSet(requiredObjectives);
	}
	public abstract void setFormulaVariables();
	public abstract ModelComponent copy();
	public void registerCurve(SOS2Primitive curve_){
		curve=curve_;
	}
	/** Add a new constraint builder object. */

	public void addConstraintBuilder(ConstraintBuilder blder){
		if ( blder.associatedWith() != this.type ){
			throw new Error("Constraint builder "+blder.type()+" is not associated with "+this.type);
		}
		if ( constraintBuilders.containsKey(blder.type())){
			throw new Error("A ConstraintBuilder of type "+blder.type()+" already exists ");
		}
		constraintBuilders.put(blder.type(), blder);
	}
	/** Remove a constraint builder object of a particular type. 
	 * */
	public void removeConstraintBuilder(ConstraintBuilder.CBType cname){
		if ( constraintBuilders.containsKey(cname)){
			constraintBuilders.remove(cname);
		} else {
			throw new Error("Can't remove "+cname+" because it is already gone");
		}
	}
	
	public void setParent(Farm parent_){
		parent=parent_;
		requireMatrixRebuild();
	}
	public Farm getParent(){ return parent;};
	/** A private alias for the structural variables relevant to this matrix component */


	protected abstract void initializeStructure() throws BadModelException ;
	
	/** Should be called if any changes are made to variables used to calculate structure */
	protected abstract void updateStructure();
	
	protected Matrix matrix=null;

	protected void buildConstraints(){
		for(ConstraintBuilder blder:constraintBuilders.values()){
			blder.build();
		}
	}

	protected ModelComponent(MCType type_){
		type=type_;
	}
	/** Register a pointer to the matrix which holds this object */
	public void registerMatrix(Matrix matrix_){
		matrix=matrix_;
	}
	public void requireStructureUpdate(){ requiresStructureUpdate=true;}
	public void structureUpdateDone(){requiresStructureUpdate=false;};
	boolean requiresStructureUpdate(){return requiresStructureUpdate;};
	protected void requireMatrixRebuild(){
		if(matrix!=null){
			matrix.flagForRebuild();
		} 
	}

	void addModifiedVariableToStack(MatrixVariable var){
		matrix.addModifiedVariableToStack(var);
	}
	
	public String printConstraints(Farm model){
		return "Not implemented";
	}
	protected abstract String name();
}

