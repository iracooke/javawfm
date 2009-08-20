package jfm.model;

import java.util.*;

import jfm.lp.ModelPrimitive;
import jfm.model.Crop.CropCopy;
import jfm.model.Types.*;
import jfm.utils.JFMMath;
import jfm.utils.MathPrint;
import jfm.xml.XMLSyntaxException;


/** Controls yield and cost penalties, workrates, machine requirements and constraints on an operation. 
 * Holds all the data relating to an instance of an operation on a single crop. It is important to 
 * note that because this is a model primitive class a separate instance is required for every year of 
 * every crop to ensure that the final LP matrix is correctly constructed.  
 * 
 * @author Ira Cooke*/
public final class Operation extends ModelPrimitive {
	private final int numPeriods;
	private int printLeadingDigits=2;
	private final HashMap<Integer,Double> yieldPenalties =new HashMap<Integer,Double>();
	private final HashMap<Integer,Double> costPenalties = new HashMap<Integer,Double>();
	private final HashMap<Integer,Double> minAreas = new HashMap<Integer,Double>();
	private boolean hasMinAreas=false;
	private final HashMap<Integer,Double> fuelCostCached =new HashMap<Integer,Double>();
	private final HashMap<Integer,Double> penaltyCostCached =new HashMap<Integer,Double>();
	private final Formula workrateF;
	private final HashMap<WorkerType,Integer> numMachines;
	private final WorkerType firstMachine;
	private CropCopy parentCrop=null;
	public final OperationType type;
	public final WorkabilityType workability;
	private boolean isSequential = true;
	private boolean isHandOver = false;
	private boolean isAttached=false;
	public final int gap;

	/** Return a working copy of the Operation. */
	public Operation copy(){
		Operation op = new Operation(type,numPeriods,workability,workrateF,numMachines,gap,firstMachine);
//		op.setAllowable(periodsAllowed);
		op.setYieldPenalties(yieldPenalties);
		op.setCostPenalties(costPenalties);
		op.setMinAreas(minAreas);
		if ( hasMinAreas ){ op.hasMinAreas=true;};
//		op.setHoursRequiredForWorker(hoursRequired);
		if ( isHandOver){ op.setHandOver(); };
		if ( !isSequential){ op.setNonSequential(); };
		return op;
	}
	/** Return the number of machines of a particular type required for this operation */
	Integer numRequiredForWorkerType(WorkerType wtype){
		if ( !numMachines.containsKey(wtype)){
			throw new Error("The worker type "+wtype+" is not defined for operation "+type);
		}
		return numMachines.get(wtype);
	}
	/** Returns true if this operation requires workers of the type of its argument */
	boolean requiresWorker(WorkerType wtype){
		return numMachines.containsKey(wtype);
	}
	
	Set<WorkerType> workerSet(){
		return numMachines.keySet();
	}
	
	/** Construct a new operation 
	 * @param type_ The operation type 
	 * @param nP The number of periods in the farming year (required to wrap periods )
	 * @param wkability The workability type 
	 * @param formul A Formula giving the workrate as a function of location 
	 * @param numMachines_ The number of machines of each WorkerType required 
	 * @param gap_ The gap in periods to be enforced between this operation and the next 
	 *  */
	public Operation(OperationType type_,int nP,WorkabilityType wkability,Formula formul,
			Map<WorkerType,Integer> numMachines_,int gap_,WorkerType firstMach) {
		gap=gap_;
		type=type_;
		numPeriods=nP;
		workability=wkability;
		numMachines=(HashMap<WorkerType,Integer>)numMachines_;	
		firstMachine=firstMach;
		// This variable never changes for the life of the operation .. for now
		workrateF=formul;
		if ( formul!=null){
			workrateF.setVariable(VariableType.SIZEFIRSTMACHINE,firstMachine.size);
		}
	}
	public void attachToCrop(CropCopy parent){
		if ( !isAttached){
			parentCrop=parent;		
			isAttached=true;
		} else {
			throw new Error("An Operation cannot be attached to more than one crop");
		}
	}
	
	protected void setFormulaVariables(){
		Farm parentFarm = parentComponent.getParent();
		Location loc = parentFarm.location();
		if ( workrateF!=null){
			for ( VariableType ltype:loc.variableSet()){
		//		if ( loc.valueChanged(ltype)){
					workrateF.setVariable(ltype, loc.getVariable(ltype));
		//		}
			}
			for ( CropInput cinp: parentCrop.baseCrop().inputSet()){
		//		if ( cinp.valueChanged(cinp.associatedVariable)){
					workrateF.setVariable(cinp.associatedVariable, cinp.getVariable(cinp.associatedVariable));
		//		}
			}
			workrateF.setVariable(VariableType.PRIMARYYIELD, parentCrop.baseCrop().getPrimaryYield());
			workrateF.setVariable(VariableType.SECONDARYYIELD, parentCrop.baseCrop().getSecondaryYield());
		}
	}
	
	protected void updateStructure(Object caller){
		Farm parentFarm=parentComponent.getParent();		
		List<Integer> pallow = new ArrayList<Integer>(unfoldedAllowedSet());
		Collections.sort(pallow); // We need to ensure that columns for each set of periods 
		for(Integer p:pallow){
			setCoefficient(ObjectiveType.PROFIT,-cost(p,(CropCopy)caller,parentFarm.location(),parentFarm.fuelPrice()),p);
		}
	}
	
	/** Calculate the cost of an operation. 
	 * @param period The period in the year
	 * @param cp The CropCopy on which this operation is occuring 
	 * @param loc The location information used to calculate workrates 
	 * @param fuelPrice Price of fuel in pounds / liter */	
	public double cost(int period,CropCopy cp,Location loc,double fuelPrice){
		double penaltyCost=0;
		double fuelCost=0;
		setFormulaVariables();
		if ( yieldPenalties.containsKey(period)){
			penaltyCost=cp.grossMarginLossForYieldPenalty(yieldPenalties.get(period));
			penaltyCost+=costPenalties.get(period);
			penaltyCostCached.put(period, penaltyCost);
		} else {			
			throw new Error("Attempt to get cost for unknown period in operation "+this.toString());
		}
		if ( workrateF!=null){
			double fuel=0;
			for ( WorkerType wt:numMachines.keySet()){
				// Adding in cost of fuel per hectare
				fuel+=wt.litresPerHour();
			}
			if ( workrate(loc) <=0){ 
				throw new Error("Can't set cost for operation because workrate is "+workrateF.calculateValue()+" in op "+type+" with wkrateF "+workrateF.getFormula());
			}
			fuelCost+=fuel*fuelPrice*workrateF.calculateValue();
		}
		fuelCostCached.put(period, fuelCost);
		return fuelCost+penaltyCost;
	}
	
	
	public double solvedPenaltyCost(){
		double[] sol=getSolution(); // Should be sorted by period;
		if ( sol.length != penaltyCostCached.size()){
			throw new Error("Can't calculated solved penalty cost because of array length mismatch");
		}
		double total=0;
		int i=0;
		for ( Double cost:penaltyCostCached.values()){
			total+=cost*sol[i];
			i++;
		}
		return total;
	}
	public double solvedArea(int period){
		return getDependent(period).solution();
	}
	public double solvedFuelCost(){

		double[] sol=getSolution(); // Should be sorted by period;
		if ( sol.length != fuelCostCached.size()){
			throw new Error("Can't calculated solved fuel cost because of array length mismatch");
		}
		double total=0;
		int i=0;
		for ( Double cost:fuelCostCached.values()){
			total+=cost*sol[i];
			i++;
		}
		return total;
	}
	
	double workrate(Location loc){
		if ( !isAttached ){
			throw new Error("Cannot get workrate for operation because it is not attached to a crop");
		}
		if ( workrateF!=null){
			return workrateF.calculateValue();
		} else {
			return 0.0;
		}
	}
	
	// These are just for the copy constructor 
	private void setYieldPenalties(HashMap<Integer,Double> oldpenalties){
		for(Integer i:oldpenalties.keySet()){
			yieldPenalties.put(i, oldpenalties.get(i));
		}
	}
	private void setCostPenalties(HashMap<Integer,Double> oldpenalties){
		for(Integer i:oldpenalties.keySet()){
			costPenalties.put(i,oldpenalties.get(i));
		}
	}
	private void setMinAreas(HashMap<Integer,Double> oldminareas){
		for(Integer i:oldminareas.keySet()){
			minAreas.put(i,oldminareas.get(i));
		}
	}
	
	public void setMinAreas(int[] perAllow,double[] mina) throws XMLSyntaxException {
		hasMinAreas=true;
		if ( perAllow.length != mina.length){ throw new XMLSyntaxException("Periods length mismatch "+perAllow.length+" vs "+mina.length);};		
		for (int i = 0 ; i < perAllow.length;i++){
			minAreas.put(perAllow[i],mina[i]);
		}		
		requireMatrixRebuild();
	}
	
	public void setYieldPenalties(int[] perAllow,double[] penalt) throws XMLSyntaxException {
		if ( perAllow.length != penalt.length){ throw new XMLSyntaxException("Periods length mismatch "+perAllow.length+" vs "+penalt.length);};		
		for (int i = 0 ; i < perAllow.length;i++){
			yieldPenalties.put(perAllow[i],penalt[i]);
		}		
		requireMatrixRebuild();
	}
	
	public void setCostPenalties(int[] perAllow,double[] costp) throws XMLSyntaxException {
		if ( perAllow.length != costp.length){ throw new XMLSyntaxException("Periods length mismatch for cost penalties");}
		for(int i=0;i< perAllow.length;i++){
			costPenalties.put(perAllow[i],costp[i]);
		}
		requireMatrixRebuild();
	}
	
	public String name(){return "Operation";};
	public boolean hasMinAreas(){return hasMinAreas;};
	public double minArea(int period){
		if ( hasMinAreas){
			return minAreas.get(period);
		} else {
			throw new Error("Attempt to get min area but non defined for this operation");
		}
	}
	
	public void setNonSequential(){ 
		isSequential=false;
		requireMatrixRebuild();
	};
	public boolean isSequential(){return isSequential;};
	public void setHandOver(){ 
		isHandOver= true; 
		requireMatrixRebuild();
	};
	public boolean isHandOver(){ return isHandOver;};

	/** Return a set of allowed periods for this operation. 
	 * The set returned consists of unfolded periods (ie not wrapped around periodic boundaries )*/
	public Set<Integer> unfoldedAllowedSet(){
		return yieldPenalties.keySet();
	}
	/** Return a set of allowed periods for this operation. 
	 * The set returned consists of folded periods (ie wrapped around the periodic boundary )*/
	public Set<Integer> foldedAllowedSet(){
		HashSet<Integer> allow=new HashSet<Integer>();
		for (Integer p:yieldPenalties.keySet()){
			allow.add(Farm.wrapPeriod(p, numPeriods));
		}
		return allow;
	}

	public void setLeadingDigits(int ld){printLeadingDigits=ld;};
	
	public String printWorkRateDetails(Location loc){
		StringBuffer outstring = new StringBuffer();
		outstring.append(this.workrate(loc));
		outstring.append(',');
		outstring.append(this.workability);
		return outstring.toString();
	}
	
	public String printSolution(char sep){
		StringBuffer outstring = new StringBuffer();
		outstring.append(type.shortName);
		outstring.append(sep);
		double[] sol=getSolution();
		double[] fullsol=new double[numPeriods];
		JFMMath.doubleZero(fullsol);
		int i=0;
		//	To give sensible output we need to sort periods in numerical order
		ArrayList<Integer> pallow= new ArrayList<Integer>(unfoldedAllowedSet());
		Collections.sort(pallow);
		for(Integer p:pallow){
			fullsol[Farm.wrapPeriod(p, numPeriods)]=sol[i];
			i++;
		}

		outstring.append(MathPrint.printVector(fullsol,printLeadingDigits,sep));

		return outstring.toString();
	}
	
	public String toString(){
		StringBuffer outstring = new StringBuffer();
		outstring.append("Operation: "+type.shortName);
		outstring.append(" isHandOver: "+isHandOver);
		outstring.append(" NumPeriods: ");
		outstring.append(numPeriods);
		outstring.append(" \n");
//		outstring.append("Periods Allowed: "+ReluMath.printVector(periodsAllowed));
		outstring.append("Areas by Period:      "+MathPrint.printVector(getSolution(),2,' '));
		outstring.append("Penalties:            "+MathPrint.printVector(getCoefficients(ObjectiveType.PROFIT),2,' '));
		for ( ObjectiveType ot:objectives()){
			if ( ot != ObjectiveType.PROFIT){
				outstring.append(ot+":      "+MathPrint.printVector(getCoefficients(ot),2,' '));
			}
		}
		outstring.append("Hours Required: \n");
		for(WorkerType wt:WorkerType.values()){
//			outstring.append(wt.shortName+" "+ReluMath.printVector(hoursRequired.get(wt)));
		}

		return outstring.toString();
	}
	
}
