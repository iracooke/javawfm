package jfm.lp;

import java.util.*;

import jfm.model.Types.ObjectiveType;

/** \internal This class provides a link between model concept classes such as crop, disease, operation etc and their corresponding matrix variables. 
 * Model concept classes which contribute variables to the constraint matrix should extend this class */
public abstract class ModelPrimitive  {
	private final Map<Integer,MatrixVariable> dependents=new HashMap<Integer,MatrixVariable>();
	protected ModelComponent parentComponent = null;
	private boolean isRegistered=false;
	public abstract String name();
	public boolean isRegistered(){return isRegistered;};
	public Set<ObjectiveType> objectives(){
		Set<ObjectiveType> objectives=new HashSet<ObjectiveType>();
		for(MatrixVariable mv:dependents.values()){
			objectives.addAll(mv.objectivesSet());
		}
		return Collections.unmodifiableSet(objectives);
	};
	
	/** Sets the coefficient \f$ a_{ij} \f$ for variable i and objective j. Also registers the objective j with variable i*/
	public void setCoefficientAndRegisterObjective(ObjectiveType otype,double coeff,Integer di){
		if ( !dependents.containsKey(di)){
			throw new Error("Can't set contribution to objective for dependent "+di+" bec ause it doesn't exist");
		}
		dependents.get(di).addObjective(otype);
		setCoefficient(otype,coeff,di);
	}
	
/*	public void incrementCoefficientForExistingObjective(ObjectiveType otype,double coeff,Integer di){
		if ( !dependents.containsKey(di)){
			throw new Error("Can't set contribution to objective for dependent "+di+" bec ause it doesn't exist");
		}
		if ( !dependents.get(di).objectivesSet().contains(otype)){
			throw new Error("Can't increment objective because it is not yet set");
		}
		incrementCoefficient(otype,coeff,di);
	}*/
	
	public Set<Integer> dependentsKeys(){ return dependents.keySet();};
	public int getDependentColumn(int index){
		if ( dependents.containsKey(index)){
			return dependents.get(index).column();
		}else {
			throw new Error("Attempt to get undefined dependent with index "+index);
		}
	}
	protected MatrixVariable getDependent(int index){
		if ( dependents.containsKey(index)){
			return dependents.get(index);
		}else {
			throw new Error("Attempt to get undefined dependent with index "+index+" when max is "+dependents.size());
		}
	}
	protected boolean dependentExists(int index){
		return dependents.containsKey(index);
	}
	
	protected abstract void updateStructure(Object caller);
	public void deRegister(){
		parentComponent=null;
	}
	public void registerParent(ModelComponent parent){
		if ( parent == parentComponent ){
			return;
		}
		if (parentComponent ==null){
			parentComponent=parent;
			isRegistered=true;
		}else {
			throw new Error("Attempt to register "+parent.name()+" to "+this.name()+" but "+parentComponent.name()+" already registered as parent \n " +
					"This could happen because you are using a primitive from one farm on another \n You should call deRegister on this component " +
					"before you do so. ");
		}
	}

	public final void registerVariable(MatrixVariable var,Integer key){
		dependents.put(key,var);
	}
	
	public void setCoefficient(ObjectiveType ot,double newval,Integer key){
		if ( isRegistered){
			parentComponent.addModifiedVariableToStack(dependents.get(key));
			dependents.get(key).setCoefficientForObjective(ot,newval);
		}		
	}
	/*
	public void incrementCoefficient(ObjectiveType ot,double newval,Integer key){
		if ( isRegistered){
			parentComponent.addModifiedVariableToStack(dependents.get(key));
			dependents.get(key).setCoefficientForObjective(ot, dependents.get(key).getCoefficientForObjective(ot)+newval);
		}
	}*/
	
	public void requireMatrixRebuild(){
		if(isRegistered){
			parentComponent.requireMatrixRebuild();
		} 
		
	}
	public double[] getCoefficients(ObjectiveType ot){
		double[] coeff=new double[dependents.size()];
		if ( isRegistered ){
			int i=0;
			List<Integer> keys=new ArrayList<Integer>(dependents.keySet());
			Collections.sort(keys);
			for(Integer p:keys){
				if ( dependents.get(p).objectivesSet().contains(ot)){
					coeff[i]=dependents.get(p).getCoefficientForObjective(ot);
				} else {
					coeff[i]=0.0;
				}
			}
		}
		return coeff;
	}
	/** Get the solved values for each of the dependent variables
	 * associated with this ModelPrimitive.  
	 * The solution is returned as a primitive array 
	 * of doubles. The array returned is sorted according to the default sort order 
	 * of the dependents, which in the standard case will be the numerical order 
	 * of columns */
	protected double[] getSolution(){
		double[] solution=new double[dependents.size()];
		jfm.utils.JFMMath.doubleZero(solution);
		if ( isRegistered ){
			int i=0;
			List<Integer> keys=new ArrayList<Integer>(dependents.keySet());
			Collections.sort(keys);
			for(Integer p:keys){
				solution[i]=dependents.get(p).solution();
				i++;
			}
		}
		return solution;
	}

}