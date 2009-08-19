package jfm.lp;
import java.util.*;

import jfm.model.Types.ObjectiveType;


/** A class to encapsulate information on LP model objectives. 
 * 
 *  Relationships between objectives and matrix coefficients in jfm can be summarised by the graph.
 *  \dot digraph objectives {
 	Objective -> MatrixVariable [color=blue];
 	Objective -> scaleFactor;
 	MatrixVariable -> coefficient [color=blue];
 	MatrixVariable -> Objective [color=blue];
 } \enddot
 A blue arrow indicates a one to multiple relationship. Note that each objective refers
 to several MatrixVariable objects, and each MatrixVariable refers to several objectives, each of which will have a 
 corresponding coefficient. It is important that when a new objective is added, that each relevant MatrixVariable be
  informed and vice-versa. These relationships  are maintained as follows;
 - When a complete new build of the matrix occurs ( ie after new variables are added, or new objectives added ) the list 
 of objectives contained within each MatrixVariable (assumed correct at this point) is used to construct a list of 
 MatrixVariable references for each Objective object held by the matrix.
 - Calls to the function ModelPrimitive.setCoefficientAndRegisterObjective are used to register Objectives 
 to MatrixVariable objects. Whenever this is done it should trigger a complete matrix rebuild 
 since the reciprocal relationships between Objectives and Variables will no longer be up to date.
 
 Thus each Objective holds a scaleFactor value which applies to a list of variables. Each variable 
 will have a constant coefficient for each objective, \f$ a_{ij} \f$, which is modified by the scaleFactor to obtain 
 the contribution of that variable to a particular objective. The value of the jth objective in  
 a multiple objective framework will therefore be;
 \f[ O_j = c_j \sum_i a_{ij} x_i \f]
 where \f$ c_j \f$ is the scalefactor for that objective.
 
 The actual application of scale factors occurs just prior to the matrix being packaged up 
 for solution by the solver instance. This task is performed by the update function of 
 the primitive matrix. This function loops over all MatrixVariable objects and executes the following code;
 \code
 	structure[cv.column()]=0;
	for ( ObjectiveType ot:cv.objectivesSet()){
		structure[cv.column()]+=cv.getCoefficientForObjective(ot)*objectives.get(ot).scaleFactor();
	}
\endcode
which ensures that the effective coefficient for each variable i is;
\f[ c^{eff}_i=\sum_j a_{ij}c_i \f]

Retrieving the values of objectives can be done via the function Matrix.PrimitiveMatrix.getObjectiveValue

The relationship between objectives, ModelComponent, objects and the Matrix object are summarized as follows;
\dot digraph objmodmat {
	Matrix->ModelComponent [color=blue];
	ModelComponent->ObjectiveType [color=blue];
	Matrix->Objective [color=blue];
	ObjectiveType->Objective;	
} \enddot

Thus a Matrix object will have several ModelComponent objects.  The variables contained within a ModelComponent refer to a particular subset of all the objectives 
in the problem and these are identified via their ObjectiveType.  The Matrix object needs to construct a universal list of all the ObjectiveTypes in the problem
from its set of ModelComponent objects.  It then needs to ensure that a single Objective instance corresponding to each type is held by the Matrix.

   */
public final class Objective {
	private final List<MatrixVariable> variables =new ArrayList<MatrixVariable>();
	public final ObjectiveType type;
	
	private double scaleFactor=1; 
	/** -- For curved objectives only --- */
	public  final boolean isCurved;
	private final double[] yVals;
	private final double[] xVals;
	private final Objective linkedToObj;
	private final SOS2Primitive curve;
	
	/** -- End Curved Objectives stuff */
	public List<MatrixVariable> variables(){
		return Collections.unmodifiableList(variables);
	}
	
	public Objective(ObjectiveType tp){
		type=tp;
		isCurved=false;
		yVals=null;
		xVals=null;
		curve=null;
		linkedToObj=null;
	}

	public Objective(ModelComponent parent,ObjectiveType tp,Objective linkedTo,double[] x,double[] y){
		xVals=x;
		yVals=y;
		type=tp;
		isCurved=true;
		linkedToObj=linkedTo;
		/** Here we replace the old objective with the curved one */
		scaleFactor=linkedTo.scaleFactor();
		linkedTo.setScaleFactor(0);
		curve=new SOS2Primitive(type.toString(),tp,xVals,yVals);
		curve.registerParent(parent);
		parent.registerCurve(curve);
	}
	
	public void buildConstraints(Matrix matrix){
		SOS2Primitive.buildConstraints(curve, matrix);
		SOS2Primitive.bindToObjective(curve,linkedToObj,matrix);		
	}
	
	public void initializeStructure(){
		if (curve!=null){
			curve.initializeStructure();
		} else {
			throw new Error("Can't initialize structure on non-curved objective");
		}
	}
	
	public Objective copy(){
		return new Objective(type);
	}
	void clearVariables(){ variables.clear();};
	void registerVariable(MatrixVariable newvar){
		variables.add(newvar);
	}
	
	public void setScaleFactor(double wt){
		scaleFactor=wt;
	}	
	public double scaleFactor(){return scaleFactor;};

}	
