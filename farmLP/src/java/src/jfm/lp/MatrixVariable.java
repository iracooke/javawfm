package jfm.lp;

import java.util.*;
import jfm.lp.Matrix.*;
import jfm.model.Types.*;

/** \internal Represents a single column variable in the LP problem */
public final class MatrixVariable {
	public final double upperBound;
	public final double lowerBound;
	public final LPX boundType;
	private int column;
//	private double coefficient;
	private double solution=0;
	public final LPX varType;
	private final ObjectiveType baseObjective;
	/** Maps from Objective types to coefficients */
	private final Map<ObjectiveType,Double> objectiveCoefficients=new HashMap<ObjectiveType,Double>();
	private String tag="";
//	public final VariableName name;
	public MatrixVariable(double coeff,double low,double up,LPX boundType_,LPX varType_,int col,ObjectiveType obj_){
//		coefficient=coeff;
		upperBound=up;
		lowerBound=low;
		boundType=boundType_;
		varType=varType_;
		column=col;		
		objectiveCoefficients.put(obj_, coeff);
		baseObjective=obj_;
//		name=varName;
	}
	protected void addObjective(ObjectiveType ot){
		objectiveCoefficients.put(ot,0.0);		
	};
	double getCoefficientForObjective(ObjectiveType ot){
		return objectiveCoefficients.get(ot);
	}
	
	double getPrimaryObjectiveCoefficient(){
		return objectiveCoefficients.get(baseObjective);
	}
	Set<ObjectiveType> objectivesSet(){return objectiveCoefficients.keySet();};

	public int column(){return column;}
//	public double coefficient(){return coefficient;};
	public double solution(){return solution;}; 
	public void setTag(String t){ tag=t;};
	public String tag(){return tag;};
	protected void setSolution(double val){solution=val;};
	void setCoefficientForObjective(ObjectiveType ot,double newvalue){
	/*	if ( objectiveCoefficients.containsKey(ot)){
			if ( objectiveCoefficients.get(ot) != newvalue && objectiveCoefficients.get(ot) != 0  ){
				System.out.println("Attempt to reassign objective coefficient "+ot+" to "+newvalue+"that was previously set to"+objectiveCoefficients.get(ot));
			}
		}*/
		objectiveCoefficients.put(ot, newvalue);
	}
/*
	void setCoefficient(double newvalue){
		coefficient=newvalue;
	};
	*/
	void commitToMatrix(PrimitiveMatrix pmatrix){
		pmatrix.setVariable(this);
	}

	public boolean equals(Object other){
		if (other instanceof MatrixVariable){
			MatrixVariable oth=(MatrixVariable)other;
			if (oth.column == this.column){
				return true;
			}
		}
		return false;
	}
	public int hashCode(){
		return column;
	}
	
}
