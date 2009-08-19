/** \internal The lp package defines general classes used for translating model concepts into a Linear Programme and for solving and retrieving solutions.
 * \dot digraph lppackage
	{
		Matrix [style=filled];
		Matrix [fillcolor = red];
		Matrix -> MatrixVariable [color=blue];
		Matrix -> MatrixRow [color=blue];
		Matrix -> Objective [color=blue];
		Matrix -> ModelComponent [color=blue];
		ModelComponent -> ModelPrimitive [color=blue];
		ModelComponent -> ConstraintBuilder;
		ModelPrimitive -> MatrixVariable [style=dotted];
		MatrixRow -> MatrixElement [color=blue];
		MatrixVariable -> Coefficient;
		MatrixVariable -> Objective [color=blue];
		MatrixElement -> Value;
		Objective -> Coefficient;
		Objective -> MatrixVariable [color=blue];
	} \enddot
	A one to many relationship is shown in blue. Optional relationships are shown with dotted lines.
	*
	*/


package jfm.lp;

import java.util.*;
import java.io.*;

import jfm.model.Farm;
import jfm.model.Types.ObjectiveType;
import jfm.model.BadModelException;
/** Java representation of the LP problem. Keeps track of all matrix elements and provides 
 * routines for updating the matrix and extracting data from it. 
 * 
 *
 * 
 * */
public final class Matrix {
	/** A Stack of MatrixVariables that have been modified since the last solve operation */
	private final Stack<MatrixVariable> modifiedVariables = new Stack<MatrixVariable>();
	/* A complete list of all constraints (rows) in this LP problem */
	private final List<MatrixRow> constraints = new ArrayList<MatrixRow>();
	/* A complete list of all variables (columns) in this LP problem */
	private final List<MatrixVariable> variables = new ArrayList<MatrixVariable>();
	/* A List of objectives with relevant weights */
	private final Map<ObjectiveType,Objective> objectives;
	/* The components of the problem. The order of these components determines the order in which they are built. 
	 * in some cases one component requires another to be built first so this order is important  */
	private final Set<ModelComponent> components = new LinkedHashSet<ModelComponent>();
	/* Representation of all the data contained in this matrix as arrays of primitive 
	 * types. This representation is required for interfacing with GLPKPeer and the solver*/
	public final PrimitiveMatrix primitiveMatrix;
	/* The Peer object for interfacing with LPX to solve the LP */
	private final LPPeer solver;
	/* Flag a complete rebuild of the problem (required for anything other than changes in coefficients */
	private boolean needsBuild=true;
	/** Flag that we always need a full rebuild because some constraints depend on the coefficients */
	private boolean alwaysFullRebuild=false;
	/* Status of the LP solution as returned by the solver */
	private LPX solutionStatus=LPX.LPX_UNDEF;

	/** Disable terminal output from the solver object */
	public void disableSolverOutput(){
		solver.setTermOut(LPX.LPX_TERMOFF);
	}
	/** Enable terminal output from the solver object */
	public void enableSolverOutput(){
		solver.setTermOut(LPX.LPX_TERMON);
	}
	/** Flag the need for a complete rebuild of the matrix before the next solve */
	public void flagForRebuild(){needsBuild=true;};
	public void flagForAlwaysRebuild(){alwaysFullRebuild=true;};
	/** Returns true if this matrix needs a complete rebuild of structure and constraints */
	public boolean needsRebuild(){ 
		return needsBuild;
	}
	/** Get the solution status of the solver embedded within this matrix */
	public LPX getSolutionStatus(){return solutionStatus;};
	
	public Map<ObjectiveType,Objective> objectives(){
		return Collections.unmodifiableMap(objectives);
	}
	
	/** Add a new Objective. */
	public void addObjective(Objective obj){
//		System.out.println("Adding objective "+obj+" to matrix ");
		objectives.put(obj.type, obj);
		needsBuild=true;
	}
	
	/** Add a new matrix variable (column) to the problem */
	public void addVariable(MatrixVariable v){
		variables.add(v);
		flagForRebuild();
	}
	/** Add a new matrix row (constraint) to the problem */
	public void addRow(MatrixRow r){ 
		constraints.add(r);
		flagForRebuild();
	};
	
	public int numCols(){
		return variables.size();
	}
	public int numRows(){
		return constraints.size();
	}
	public LPPeer.Solver getSolverType(){
		return solver.type;
	}
	
	// ---- CONSTRUCTOR AND INITIALIZATION FUNCTIONS --- // 
	/** Construct an LP Matrix object given a set of model components 
	 * Adds the default primary objective ( PROFIT) with coefficient 1 
	 * Performs a complete build of the matrix from scratch.
	 * Instantiates a solver and primitive matrix */
	public Matrix(Set<ModelComponent> cmpIn,Map<ObjectiveType,Objective> objin,LPPeer.Solver solverType) throws BadModelException {
		for(ModelComponent mc:cmpIn){
			addComponent(mc);
		}
		objectives = objin;
		doCompleteBuild();
		primitiveMatrix=new PrimitiveMatrix(this);
		solver=LPPeer.create(this, solverType);
//		solver=LPPeer.create(this, LPPeer.Solver.GLPK);
		
		modifiedVariables.clear();		
	}

	/** Builds the main matrix from scratch.  
	 * First clears the variables list and all the objectives variable lists.
	 * Then builds all structural variables and constraints.
	 * Updates mapping of variables to objectives 
	 */
	private void doCompleteBuild() throws BadModelException {
	//	System.out.println("Completely rebuilding matrix ");
		// build structural variables 
		variables.clear(); // Start from scratch
		for ( ModelComponent component:components){
				component.initializeStructure();
		}
	
		
		// Setup dependencies and coefficients for multiple objectives 
		// Clear the old lists defining relationships between objectives and variables 
		for(Objective obj:objectives.values()){
			obj.clearVariables();
		}
		boolean needsDummy=false;
		for(Objective obj:objectives.values()){
			if ( obj.isCurved){
				obj.initializeStructure();
				if ( !objectives.containsKey(ObjectiveType.SOS2DUMMY)){
					needsDummy=true;
				}
			}
		}
		if(needsDummy){
			objectives.put(ObjectiveType.SOS2DUMMY, new Objective(ObjectiveType.SOS2DUMMY)); // Create the dummy objective type if needed
		}
		// Build new lists 
		for(MatrixVariable var:variables){
			for ( ObjectiveType ot:var.objectivesSet()){
				if ( objectives.containsKey(ot)){
					objectives.get(ot).registerVariable(var);
				} else {
					throw new Error("A Variable with tag "+var.tag()+" was defined for "+ot+" but no concrete object exists for this objective which is one of" +var.objectivesSet());
				}
			}
		}
		constraints.clear(); // Start from scratch
		
		// This would be the right place to initialize curved objectives 
		for(Objective obj:objectives.values()){
			if ( obj.isCurved){
				obj.buildConstraints(this);
			}
		}
		
		// build constraints 
		for ( ModelComponent component:components){
			component.buildConstraints();
		}
		needsBuild=false;
	}
	
	/** Add a new Model component */
	public void addComponent(ModelComponent newComp){
		if ( components.contains(newComp)){
			throw new Error("The component "+newComp.name()+" is already defined for this matrix");
		}
		newComp.registerMatrix(this);
		components.add(newComp);
		needsBuild=true;
	}
	
	/** Update the matrix and request a solution from the solver 
	 * @return The solution status of the solver object as a LPX enum value @see jfm.model.GLPK*/
	public LPX updateAndSolve() throws BadModelException {
	//	System.out.println("Solving with objectives "+objectives.keySet());
		if ( needsBuild || alwaysFullRebuild ){
			doCompleteBuild();
			if (primitiveMatrix!=null){
				primitiveMatrix.fullReset(this);
			} else {
				throw new Error("Why does this happen? primitiveMatrix is null"); // ?? Do nothing .. the only time this happens is on first initialization.
			}
		} else {	
	//		System.out.println("No rebuild needed");
			updateStructure();
			if (primitiveMatrix!=null){
				if ( solver.type == LPPeer.Solver.CLP){
					primitiveMatrix.updateCoefficients();// Fast but slightly less safe option
				} else {
					// small updates not properly implemented for GLPK
					primitiveMatrix.fullReset(this);// Safe but slow option
				}
			} else {
				throw new Error("Why does this happen? primitiveMatrix is null"); // ?? Do nothing .. the only time this happens is on first initialization.
			}
		}
		
	//	this.printCSV("matrix.csv");
		solutionStatus=solver.solve();
		return solutionStatus;
	}

	/** Perform a minimal update to the matrix coefficients. 
	 * Commit new coefficient values for changed variables to the primitive matrix
	 * update baseObjective coefficients
	 * */
	private void updateStructure(){
	//	System.out.println("Updating structure ");
		for ( ModelComponent component:components){
	//		if ( component.requiresStructureUpdate()){
				component.updateStructure();
	//		}
		}
		while (!modifiedVariables.empty()){
//			System.out.println("Popping a modified variable");
			modifiedVariables.pop().commitToMatrix(primitiveMatrix);
		}
//		System.out.println("Updating baseObjective coefficient");
	}

	private int totalNumElements(){
		int total=0;
		for (int i=0;i<constraints.size();i++){
			total+=constraints.get(i).elements().size();
		}
		return total;
	}
	/** This function permits low level matrix primitives to add their modified variables 
	 * to the centralized stack stored in this object. These variables will be updated 
	 * when updateStructure() is called */
	void addModifiedVariableToStack(MatrixVariable var){
		if ( !modifiedVariables.contains(var)){
			modifiedVariables.add(var);
		}
	};

	/** Version of the constraint matrix in which data is held entirely as arrays of 
	 * primitives These primitive arrays are passed directly to native methods via GLPKPeer */
	public class PrimitiveMatrix {
		/** After solving, this variable holds the LP solution */
		public double[] solution=null;
		/** Coefficients for all of the variables (columns) in the problem */
		public double[] structure=null;
		/** Values of elements in the constraint matrix. Row and column indices 
		 * for each value are held in matrixRowIndexes and matrixColIndexes respectively */
		public double[] matrixElements=null;
		/** Row indices for matrixElements */
		public int[] matrixRowIndexes=null;
		/** Column indices for matrix elements */
		public int[] matrixColIndexes=null;
		/** An array of lower/upper bound pairs for column variables */
		public double[] columnBounds = null;
		/** An array of lower/upper row bound value pairs */
		public double[] rowBounds = null;
		/** Bound types for each column, as defined in LPX */
		public int[] columnBoundTypes=null;
		/** Bound types for each row as defined in LPX */
		public int[] rowBoundTypes=null;
		/** The type (ie LPX_CV or LPX_IV ) for each column */
		public int[] columnTypes=null;	
		/** An array of character strings corresponding to row names */
		public char[][] rowNames=null;
		/** An array of character strings corresponding to column names */
		public char[][] colNames=null;
		
		boolean structureChanged=true;
		boolean constraintsChanged=true;
		public void acceptChanges(){
			structureChanged=false;
			constraintsChanged=false;
		}
		/** Create a new Primitive Matrix */
		public PrimitiveMatrix(Matrix matrix){
			fullReset(matrix);
		}
		
		/** Reset the Primitive Matrix from scratch making sure 
		 * that it reflects the Matrix 
		 * */
		public void fullReset(Matrix matrix){
			solution=new double[matrix.variables.size()];
			structure=new double[matrix.variables.size()];
			int totalNumElements=matrix.totalNumElements();
			matrixElements=new double[totalNumElements];
			matrixRowIndexes=new int[totalNumElements];
			matrixColIndexes=new int[totalNumElements];
			columnBounds=new double[structure.length*2];
			rowBounds=new double[matrix.constraints.size()*2];
			columnBoundTypes=new int[structure.length];
			rowBoundTypes=new int[matrix.constraints.size()];
			columnTypes=new int[structure.length];
			rowNames=new char[matrix.constraints.size()][];
			colNames=new char[matrix.variables.size()][];
			for ( MatrixVariable cv: variables){
				setVariable(cv);
			}
			// Construct matrix elements for constraints 
			int element=0;
			for ( int row=0;row<rowBoundTypes.length;row++){
				MatrixRow cr=matrix.constraints.get(row);
				for(MatrixElement currel:cr.elements()){
					matrixElements[element]=currel.value;
					if ( currel.value == 0 ){
						throw new Error("Zero matrix element"+row+" of "+rowBoundTypes.length);
					}
					matrixRowIndexes[element]=row;
					matrixColIndexes[element]=currel.colIndex;
					element++;
				}
				rowBounds[row*2]=cr.lowerBound;
				rowBounds[row*2+1]=cr.upperBound;
				rowBoundTypes[row]=cr.type.toCPP();
				rowNames[row]=cr.tag.toCharArray();
			}
			structureChanged=true;
			constraintsChanged=true;
		}
		/** Do a minimal update .. just the objective coefficients */
		void updateCoefficients(){
//			System.out.println("Updating");
			for ( MatrixVariable cv: variables){
//				double orig=structure[cv.column()];
				setVariable(cv);				
//				if ( structure[cv.column()]!=orig){
//					System.out.println("Different "+structure[cv.column()]+" "+orig);
//				}
			}
			structureChanged=true;
		}
		
		void setVariable(MatrixVariable cv){	
			structure[cv.column()]=0;
			for ( ObjectiveType ot:cv.objectivesSet()){
				structure[cv.column()]+=cv.getCoefficientForObjective(ot)*objectives.get(ot).scaleFactor();
			}
			columnBounds[cv.column()*2]=cv.lowerBound;
			columnBounds[cv.column()*2+1]=cv.upperBound;
			columnBoundTypes[cv.column()]=cv.boundType.toCPP();
			columnTypes[cv.column()]=cv.varType.toCPP();
			colNames[cv.column()]=cv.tag().toCharArray();
			structureChanged=true;
		}
		
		void commitSolution(){		
			for(Objective obj:objectives.values()){
				for(MatrixVariable var:obj.variables()){
					var.setSolution(solution[var.column()]);
				}
			}
		}

		/** Retrieve the value of the Objective with a given type
		 * @param type The objective type whose value is to be retrieved.
		 * */
		public double getObjectiveValue(ObjectiveType type){
			Objective obj=objectives.get(type);
//			System.out.println("Getting value for objective "+type);
			double oval=0;
			for(MatrixVariable var:obj.variables()){
//				System.out.println("var "+solution[var.column()]);
				oval+=var.getCoefficientForObjective(type)*solution[var.column()];
			}
			return oval;
		}
		/** Retrieve the total Objective as the sum of all sub-objectives */
		public double getObjectiveValue(){
			double oval=0;
			for(Objective obj:objectives.values()){
				oval+=getObjectiveValue(obj.type);
			}
			return oval;
		}
	}
	
	
	/** Write an annotated version of the constraint matrix to a comma separated values 
	 * file. */
	public void printCSV(String filename){		
		PrintWriter writer;
		try {
			writer = new PrintWriter(new FileOutputStream(filename));
		} catch (FileNotFoundException ex){
			ex.printStackTrace();
			
			throw new Error("in printCSV");
		}
		// First print the column names
		writer.print("tag,"+"bound type,"+"low bound ,"+"up bound,"); // Leave the first column blank
		for(MatrixVariable cv:variables){
			writer.print(cv.tag()+",");
		}
		writer.println();
		
		// Then column coefficients
		writer.print(","+","+","+","); // Leave the first column blank
		for(MatrixVariable cv:variables){
			if ( cv.objectivesSet().size() >  1 ){
				for ( ObjectiveType ot:cv.objectivesSet()){
					writer.print(ot+"("+cv.getCoefficientForObjective(ot)+")");
				}
			} else {
				writer.print(cv.getPrimaryObjectiveCoefficient());
			}
			writer.print(",");
			
		}
		writer.println();

		for(MatrixRow cr:constraints){
			int col=0;
			List<MatrixElement> rowels= Arrays.asList(cr.elements().toArray(new MatrixElement[0]));
			Collections.sort(rowels);
			
			writer.print(cr.tag+","+cr.type+","+cr.lowerBound+","+cr.upperBound+",");
			for(MatrixElement next:rowels){
				if ( next.colIndex<col){ throw new Error("not sorted "+col+" "+next.colIndex);}
				while(col<next.colIndex){
					writer.print(", "); // blank columns
					col++;
				}
				writer.print(next.value);
			}
			writer.println();
		}
		
		writer.flush();
	}
	
}
