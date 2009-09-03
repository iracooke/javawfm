package jfm.lp;

import jfm.lp.Matrix.PrimitiveMatrix;

public class CBCPeer extends LPPeer {
	/** A reference to the primitive matrix residing within the parent lp::matrix 
	 * object */
	private PrimitiveMatrix matrix;
	/** A pointer to the c++ instance of the LP solver object associated with this GLPKPeer*/
	private long lpobj=0;
	// ---- Native interface functions ---- //
	/** Create a new LP */
	private native long create(double[] soln,double[] pvector, 
			double[] cbounds,int[] cboundtypes,double[] pmatrixv, int[] pmatrixri,int[] pmatrixci,
			double[] rbounds, int[] rboundtypes,int[] columntypes,char[][] rownames,char[][] colnames);
	/** Turn the terminal output on or off */
	private native void setTermOut(long p,int flag);
	/** Free memory allocated for the LP */
	private native void destroy(long p);
	/** Fast method for obtaining a solution to the LP where only the column coefficients have changed but no 
	 * changes to constraints need to be accounted for. Only slightly faster than full solve. Untested */
	private native int solveWithNewCoefficients(double[] soln,double[] pvector,double[] cbounds,int[] cboundtypes,int[] columntypes,long p);
	/** Solve the LP contained within the matrix object. The solution will be returned in @param soln. This method 
	 * is slightly slow as it reconstructs the entire matrix */
	private native int solveWithNewProblem(double[] soln,double[] pvector, 
			double[] cbounds,int[] cboundtypes,double[] pmatrixv, int[] pmatrixri,int[] pmatrixci,
			double[] rbounds, int[] rboundtypes,int[] columntypes,char[][] rownames,char[][] colnames,long p);
	
	
	/** Native function for creating a new LP object */
	protected CBCPeer(Matrix matrix_){
		super(LPPeer.Solver.CLP);
		matrix=matrix_.primitiveMatrix;
		lpobj = create(matrix.solution,matrix.structure,matrix.columnBounds,
				matrix.columnBoundTypes,matrix.matrixElements,matrix.matrixRowIndexes,matrix.matrixColIndexes,
				matrix.rowBounds,matrix.rowBoundTypes,matrix.columnTypes,matrix.rowNames,matrix.colNames);
	}
	
	/** Solve the LP problem  object */
	protected LPX solve(){
		int status;
		if ( matrix.constraintsChanged){
			status=solveWithNewProblem(matrix.solution,matrix.structure,matrix.columnBounds,
				matrix.columnBoundTypes,matrix.matrixElements,matrix.matrixRowIndexes,matrix.matrixColIndexes,
				matrix.rowBounds,matrix.rowBoundTypes,matrix.columnTypes,matrix.rowNames,matrix.colNames,lpobj);
		} else {
			// This is the default and we do it even when there are no changes
			// Doesn't work			status=solveWithNewCoefficients(matrix.solution,matrix.structure,matrix.columnBounds,matrix.columnBoundTypes,matrix.columnTypes,lpobj);
			status=solveWithNewProblem(matrix.solution,matrix.structure,matrix.columnBounds,
									   matrix.columnBoundTypes,matrix.matrixElements,matrix.matrixRowIndexes,matrix.matrixColIndexes,
									   matrix.rowBounds,matrix.rowBoundTypes,matrix.columnTypes,matrix.rowNames,matrix.colNames,lpobj);
		}
		matrix.acceptChanges();
		matrix.commitSolution();
		return LPX.intToGLPKType(status);
	}
	
	/** Set terminal output on or off. 
	 * @param flag Either LPX_TERMON or LPX_TERMOFF */
	public void setTermOut(LPX flag){
		if ( flag != LPX.LPX_TERMOFF && flag != LPX.LPX_TERMON){
			throw new Error("Only LPX_TERMON or LPX_TERMOFF are allowed ");
		}
		setTermOut(lpobj,flag.toCPP());
	}
	
	/** destroy native object if not already done */
    public synchronized void destroy() {
//    	System.out.print("Deleting GLPKPeer ");
        if (lpobj != 0) {
            destroy(lpobj);
            lpobj = 0;
        }
  //      System.out.print(" done \n");
    }

    /** destroy native object if it's still around when finalize called from garbage collection */
    public void finalize() {
    //	System.out.print("Garbage collecting native object ");
        destroy();
     //   System.out.print(" done \n");
    }
	/** Load native LPX solving library */
	static {
	//	System.out.println("Loading native library");
	//	String jlpath = System.getProperty("java.library.path");
	//	System.out.println(jlpath);
		System.loadLibrary("farmLP");		
	//	System.out.println("Loaded");
	}

}
