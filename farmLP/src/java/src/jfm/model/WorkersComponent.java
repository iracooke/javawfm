package jfm.model;


import java.util.*;

import jfm.lp.*;
import jfm.model.Crop.CropCopy;
import jfm.model.Types.*;
import jfm.utils.*;

/** Holds all Worker objects and constructs all related matrix variables and constraints. 
 * 
 *  @author Ira Cooke */
public final class WorkersComponent extends ModelComponent  {
	private static String constraintTag="Workers";
	private final HashMap<WorkerType,HashMap<WorkerSubType,Worker>> workers;
	public String name(){return "workers";};
	public void setFormulaVariables(){}
	
	public WorkersComponent copy(){
		HashMap<WorkerSubType,Worker> wkrscpy= new HashMap<WorkerSubType,Worker>();
		for ( WorkerType wt:workers.keySet()){
			HashMap<WorkerSubType,Worker> oldmap=workers.get(wt);
			for ( WorkerSubType wst:oldmap.keySet()){
				wkrscpy.put(wst, oldmap.get(wst).copy());
			}
		}
		return new WorkersComponent(wkrscpy);
	}
	/** Create a new instance. 
	 * @param workers_ A Map from WorkerType to instances of individual Worker objects */
	public WorkersComponent(HashMap<WorkerSubType,Worker> workers_){
		super(ModelComponent.MCType.WORKERS);
		requireObjective(ObjectiveType.PROFIT);
		addConstraintBuilder(new ResourcesConstraint());
		// Use the list of worker sub-types to build the workers heirarchy
		workers=new HashMap<WorkerType,HashMap<WorkerSubType,Worker>>();
		for ( Map.Entry<WorkerSubType, Worker> entry:workers_.entrySet()){
			Worker wkr=entry.getValue();
			if (!workers.containsKey(wkr.type)){
				workers.put(wkr.type, new HashMap<WorkerSubType,Worker>());
			}
			if ( workers.get(wkr.type).containsKey(wkr.subType)){
				throw new Error("The subtype "+wkr.subType+" is already defined for "+wkr.type);
			} else {
				workers.get(wkr.type).put(wkr.subType, wkr);
			}
		}
	}
	
	public HashMap<WorkerSubType,Worker> getWorkers(WorkerType wt ){
		if ( workers.containsKey(wt)){
			return workers.get(wt);		
		} else {
			throw new Error("Attempt to get Worker list for "+wt+" but no such worker type is defined");
		}
	}
	/*
	public double getSolutionForType(WorkerType wt){
		return workers.get(wt).dependents.get(0).solution();
	}
	*/
	// METHODS ASSOCIATED WITH STRUCTURAL VARIABLES 
	protected void initializeStructure() {
		/*
		for(WorkerSubType wst:WorkerSubType.values()){
			Worker wp=workers.get(wt);
			if (wp!=null){
			*/
		for(HashMap<WorkerSubType,Worker> wlist:workers.values()){
			for ( Worker wp:wlist.values()){
				MatrixVariable newVariable=new MatrixVariable(-wp.getAnnualCost(),
						wp.lowerBound,wp.upperBound,
						wp.glpkBoundType,wp.glpkVariableType,matrix.numCols(),ObjectiveType.PROFIT);
				newVariable.setTag(wp.subType.shortName);
				matrix.addVariable(newVariable);	
				wp.registerParent(this);			
				wp.registerVariable(newVariable,0);				
			}
		}
	}
	protected void updateStructure(){
		for(HashMap<WorkerSubType,Worker> wlist:workers.values()){
			for(Worker wp:wlist.values()){
				wp.updateStructure(this);
			}
		}
		structureUpdateDone();
	}

	// METHODS ASSOCIATED WITH CONSTRAINTS 
	/** Constrain the resources required to be no greater than those provided by the Labour and Machinery. 
	 * 
	 * @author Ira Cooke
	 * 
	 * */
	public final class ResourcesConstraint extends ConstraintBuilder{
		public ResourcesConstraint(){
			super(ConstraintBuilder.CBType.RESOURCES,ModelComponent.MCType.WORKERS);
		}
		public void build(){
			int numPeriods=getParent().numPeriods;
			CroppingComponent cropping=getParent().cropping;
			Location location=getParent().location();
			int row=matrix.numRows();
		
//			boolean newRow=false;
			MatrixRow rp=null;

			for(WorkabilityType wkability:WorkabilityType.values()){	// Loop Workability Types					
				for(WorkerType wktype:workers.keySet()){
					HashMap<WorkerSubType,Worker> wlist=workers.get(wktype);
					HashMap<Integer,MatrixRow> rowlist = new HashMap<Integer,MatrixRow>();
					for ( int p=0;p<numPeriods*Farm.maxYears;p++){ // Loop Periods 
					int periodicp = Farm.wrapPeriod(p,numPeriods);
					
//						newRow=true; 
						for(CropYear cropyr:cropping.cropYears()){
							CropCopy cp=cropping.getCrop(cropyr.base).getCopy(cropyr.copyYear);
//							Crop cp = cropping.crops.get(cropyr.base).get(cropyr.copyYear);
							List<Operation> cpOps=cp.operations();
							int numops = cpOps.size();
							for ( int o=0;o<numops;o++){ // Loop operations for this crop
								Operation op = cpOps.get(o);
								if ( op.workability.percentHours<=wkability.percentHours && op.unfoldedAllowedSet().contains(p) && op.requiresWorker(wktype)){
//								if ( newRow == true ){
									if ( !rowlist.containsKey(periodicp)){ // A constraint is needed for every periodic period.
										rp=new MatrixRow(0,0,LPX.LPX_LO,
												row,constraintTag,"Hours_for_"+wktype.name+"_in_p_"+periodicp);
										matrix.addRow(rp);		
										rowlist.put(periodicp, rp);
										row++;
				//					newRow=false;
										double avail=location.availableHours(periodicp)*wkability.percentHours;
										for ( Worker wp:wlist.values()){
											rp.addElement(new MatrixElement(wp.getDependentColumn(0),avail));
										}
									}
									rp=rowlist.get(periodicp);
									double required=op.workrate(location)*op.numRequiredForWorkerType(wktype);
									rp.addElement(new MatrixElement(op.getDependentColumn(p),-required)); // Resource requirements for every unfolded period are added to the total for the folded period
								}
							}	
						}
					}
				}
			}
		}
	}
	
	/** Gets a map from Period to anothermap with details of the workload in that period
	 * for each workertype, and workability type 
	 * */
	public Map<Integer,LinkedHashMap<Object,Double>> getWorkload(){

		// Holds the final result. Maps from a period ( Integer ) to a data structure with information about the workload in that period
		Map<Integer,LinkedHashMap<Object,Double>> result=new HashMap<Integer,LinkedHashMap<Object,Double>>();

		
		int numPeriods=getParent().numPeriods;
		CroppingComponent cropping=getParent().cropping;
		Location location=getParent().location();

		
		for ( int periodicp=0;periodicp<numPeriods;periodicp++){ // Loop over unfolded list of periods 
			// Work out the periodic equivalent for this period 
	//		int periodicp = Farm.wrapPeriod(p,numPeriods);

			// Create the object to hold data on this time period
			// The data held should be .. in order ;;
			// Available hours in the period
			// Number of hours used for each workability type .. 
			// Number of hours used for each machine type .. correcting for machine numbers and requirements
			LinkedHashMap<Object,Double> totals=new LinkedHashMap<Object,Double>();

			totals.put(0, location.availableHours(periodicp));	

			for(WorkabilityType wkability:WorkabilityType.values()){	// Loop Workability Types			
				// Create a temporary object with the total usage for each worker type in this workability
				Map<WorkerType,Double> wkbltot=new HashMap<WorkerType,Double>();
				for(CropYear cropyr:cropping.cropYears()){				// Loop over Crops
					CropCopy cp=cropping.getCrop(cropyr.base).getCopy(cropyr.copyYear);
					List<Operation> cpOps=cp.operations();
					int numops = cpOps.size();
					for ( int o=0;o<numops;o++){ // Loop operations for this crop
						Operation op = cpOps.get(o);
						for ( int yr=0;yr<2;yr++){
							int p = periodicp+numPeriods*yr;
							if ( op.workability.percentHours<=wkability.percentHours && op.unfoldedAllowedSet().contains(p) ){	// Check if this op should contribute to the workability total in this period		
								for(WorkerType wktype:op.workerSet()){ // Calculate totals for each worker type
									HashMap<WorkerSubType,Worker> wlist=workers.get(wktype);
									double nworkers=0;
									for ( Worker wp:wlist.values()){
										nworkers+=wp.getSolvedNumber();
									}
								// Calculate the time taken for this particular worker
									double timetaken = op.workrate(location)*op.numRequiredForWorkerType(wktype)*op.solvedArea(p)/nworkers;

/*								
								if ( periodicp == 17 && wktype==WorkerType.TRACTOR && op.solvedArea(p)>0){
									System.out.println(op.solvedArea(p)+" "+op.numRequiredForWorkerType(wktype)+" "+" "+op.workrate(location)+" "+nworkers+" "+timetaken);
								}*/
								// Apend the time taken for this crop / op combo to the total for this worker type
									if ( wkbltot.containsKey(wktype)){
										timetaken+=wkbltot.get(wktype);
									}
									wkbltot.put(wktype, timetaken);
								}
							}
						}	
					}
				}
				// Now that we have a list of workload values for each worker type we choose the largest one and record it against that workability type
				// The largest is chosen because it tells us how close we are to the constraint.
				if ( wkbltot.size()>0 ){
					totals.put(wkability, Collections.max(wkbltot.values()));
				//	if (  wkability==WorkabilityType.R100){
				//		System.out.println(periodicp+" Totals "+location.availableHours(periodicp)+","+Collections.max(wkbltot.values())+"["+wkbltot.values());
				//	}
				} else {
					totals.put(wkability, 0.0);
				}
			}
			
			// Now we calculate separately, the workload on each machine type
			for(WorkerType wktype:workers.keySet()){
				double wkrtot=0;
				HashMap<WorkerSubType,Worker> wlist=workers.get(wktype);
					for(CropYear cropyr:cropping.cropYears()){
						CropCopy cp=cropping.getCrop(cropyr.base).getCopy(cropyr.copyYear);
						List<Operation> cpOps=cp.operations();
						int numops = cpOps.size();
						for ( int o=0;o<numops;o++){ // Loop operations for this crop
							Operation op = cpOps.get(o);
							for ( int yr=0;yr<2;yr++){
								int p = periodicp+numPeriods*yr;
								if ( op.unfoldedAllowedSet().contains(p) && op.requiresWorker(wktype)){								
									double nworkers=0;
									for ( Worker wp:wlist.values()){
										nworkers+=wp.getSolvedNumber();
									}
//								if ( nworkers > 0 ){
									wkrtot+=op.workrate(location)*op.numRequiredForWorkerType(wktype)*op.solvedArea(p)/nworkers;
//								}
								}
							}
						}
					}	
				totals.put(wktype, wkrtot);
			}
			/*
			if ( result.containsKey(periodicp) ){
				Map<Object,Double> etot=result.get(periodicp);
				for(Object key:totals.keySet()){
					if ( key != (Integer)0 ){
						double newval = etot.get(key)+totals.get(key);
						etot.put(key, newval);
					}
				}
			} else {*/
			result.put(periodicp, totals);
		}
		return result;
	}
	

	
	public String printSolution(Farm model){
		StringBuffer outstring = new StringBuffer();
		outstring.append("--- Workers ---\n");
		for(HashMap<WorkerSubType,Worker> wlist:workers.values()){
			for ( Worker wp:wlist.values()){
				outstring.append(wp.subType+": "+MathPrint.f1.format(wp.getSolvedNumber())+"\n");
			}
		}
		return outstring.toString();
	}
	
	public String printFixedCosts(Farm model){
		double fc=0;
		for(HashMap<WorkerSubType,Worker> wlist:workers.values()){
			for ( Worker wp:wlist.values()){
				fc+=wp.getSolvedNumber()*wp.getAnnualCost();
			}
		}
		return ("Fixed Costs: "+fc);
	}
	
}
