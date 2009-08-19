/**
 * 
 */
package jfm.model;

import java.util.*;

import jfm.lp.ConstraintBuilder;
import jfm.lp.LPX;
import jfm.lp.MatrixElement;
import jfm.lp.MatrixRow;
import jfm.lp.MatrixVariable;
import jfm.lp.ModelComponent;
import jfm.model.Crop.CropCopy;
import jfm.model.Types.CropType;
import jfm.model.Types.*;
import jfm.model.Types.ObjectiveType;
import jfm.model.Types.WorkabilityType;
/** Model for free time available in each period. 
 * At present this tends to give results where the farmer gets huge amounts of freetime. 
 * This is because we don't yet model the possibility of casual or contract labour to 
 * take the need for permanent contractors down during peak times. 
 * @author iracooke
 *
 */
public class FreeTimeComponent extends ModelComponent {
	private ArrayList<Double> preferenceWeights = new ArrayList<Double>();
	private Map<Integer,MatrixVariable> freeTimeByPeriod = new HashMap<Integer,MatrixVariable>();
	public FreeTimeComponent(){
		super(ModelComponent.MCType.FREETIME);
		requireObjective(ObjectiveType.FREETIME);
		addConstraintBuilder(new FreeTimeVResourcesConstraint());
		addConstraintBuilder(new FreeTimeMaxConstraint());
	}
	public FreeTimeComponent copy(){
		return new FreeTimeComponent();
	}
	public void setFormulaVariables(){}

	public Map<Integer,Double> getSolvedFreeTime(){
		Map<Integer,Double> ft=new HashMap<Integer,Double>();
		for(Integer p:freeTimeByPeriod.keySet()){
			ft.put(p,freeTimeByPeriod.get(p).solution());
		}
		return ft;
	}
	
	/** Ensures that the farmer cannot have more than his entire life in free time. 
	 * This avoids an infeasibility which could occur if you put too much value 
	 * on free time and the model would hire labour to provide it */
	public final class FreeTimeMaxConstraint extends ConstraintBuilder {
		public FreeTimeMaxConstraint(){
			super(ConstraintBuilder.CBType.FREETIMEMAX,ModelComponent.MCType.FREETIME);
		}
		protected void build(){
			Farm parent = getParent();
			Location loc = parent.location();
			int numPeriods=parent.numPeriods;
			int row=matrix.numRows();			
			// Get this from workers
			HashMap<WorkerSubType,Worker> wmap=parent.workers.getWorkers(WorkerType.LABOUR);
			if ( !wmap.containsKey(WorkerSubType.FARMER)){
				throw new Error("Can't add constraint to free time objective because no Farmer WorkerSubType defined ");
			}
			Worker fmrp=wmap.get(WorkerSubType.FARMER);
			for ( int p=0;p<numPeriods;p++){ // Loop Periods 
				MatrixRow rp=new MatrixRow(0,0,LPX.LPX_LO,
						row,"FreeTime","Freetime_in_p_"+p);
				matrix.addRow(rp);	
				row++;

				rp.addElement(new MatrixElement(freeTimeByPeriod.get(p).column(),-1)); // Free time being used as free time
				double avail=loc.availableFreeTimeHours(p);				
				rp.addElement(new MatrixElement(fmrp.getDependentColumn(0),avail)); // Total free time available
			}
		}
	}
	
	public final class FreeTimeVResourcesConstraint extends ConstraintBuilder {
		public FreeTimeVResourcesConstraint(){
			super(ConstraintBuilder.CBType.FREETIMEVRESOURCES,ModelComponent.MCType.FREETIME);
		}
		protected void build(){
			Farm parent = getParent();
			CroppingComponent cropping = parent.cropping;
			Location location = parent.location();
			int numPeriods=parent.numPeriods;
			int row=matrix.numRows();			
			// Get this from workers
			HashMap<WorkerSubType,Worker> wmap=parent.workers.getWorkers(WorkerType.LABOUR);
			if ( !wmap.containsKey(WorkerSubType.FARMER)){
				throw new Error("Can't add constraint to free time objective because no Farmer WorkerSubType defined ");
			}
			Worker fmrp=wmap.get(WorkerSubType.FARMER);
			HashMap<Integer,MatrixRow> rowlist = new HashMap<Integer,MatrixRow>();
			MatrixRow rp=null;
			for ( int p=0;p<numPeriods*Farm.maxYears;p++){ // Loop Periods 
				int periodicp = Farm.wrapPeriod(p,numPeriods);
				if ( !rowlist.containsKey(periodicp)){
					rp=new MatrixRow(0,0,LPX.LPX_LO,
							row,"FreeTime","Freetime_in_"+p);
					matrix.addRow(rp);	
					rowlist.put(periodicp, rp);
					row++;
				}
				rp=rowlist.get(periodicp);
				rp.addElement(new MatrixElement(freeTimeByPeriod.get(periodicp).column(),-1)); // Free time being used as free time
				double avail=location.availableFreeTimeHours(periodicp);				
				rp.addElement(new MatrixElement(fmrp.getDependentColumn(0),avail)); // Total free time available
				// And we need to account for time freed up by other workers 
				for ( Worker otherlab:wmap.values()){
					if ( otherlab.subType!=WorkerSubType.FARMER){
						// Work hours not free time hours
						rp.addElement(new MatrixElement(otherlab.getDependentColumn(0),location.availableHours(periodicp)));
					}
				}
				for(CropYear cropyr:cropping.cropYears()){
					CropCopy cp=cropping.getCrop(cropyr.base).getCopy(cropyr.copyYear);
					List<Operation> cpOps=cp.operations();
					int numops = cpOps.size();
					for ( int o=0;o<numops;o++){ // Loop operations for this crop
						Operation op = cpOps.get(o);
						if ( op.unfoldedAllowedSet().contains(p) && op.requiresWorker(WorkerType.LABOUR)){	
							double required=op.workrate(location)*op.numRequiredForWorkerType(WorkerType.LABOUR);
							rp.addElement(new MatrixElement(op.getDependentColumn(p),-required)); // Free time used up by work
						}
					}
				}
			}
		}
	} 
	
	/* (non-Javadoc)
	 * @see jfm.lp.ModelComponent#initializeStructure()
	 */
	@Override
	protected void initializeStructure() {
		freeTimeByPeriod.clear();
		
		Farm parent = getParent();
		// If pref weights not set then just use the default
		if ( preferenceWeights.size()==0){
			for(int p=0;p<parent.numPeriods;p++){
				preferenceWeights.add(1.0);
			}
		}
		
		if ( preferenceWeights.size()!=parent.numPeriods){
			throw new Error("preference weights on free time do not match the number of periods ");
		}
		for (int p=0;p<parent.numPeriods;p++){
			MatrixVariable newVariable=new MatrixVariable(preferenceWeights.get(p),
					0.0,0.0,
					LPX.LPX_LO,LPX.LPX_CV,matrix.numCols(),ObjectiveType.FREETIME);
		newVariable.setTag("ft_"+p);
		matrix.addVariable(newVariable);	
		freeTimeByPeriod.put(p,newVariable);
		}
	}

	/* (non-Javadoc)
	 * @see jfm.lp.ModelComponent#name()
	 */
	@Override
	protected String name() {		
		return "Free Time";
	}

	/* (non-Javadoc)
	 * @see jfm.lp.ModelComponent#updateStructure()
	 */
	@Override
	protected void updateStructure() {
		// TODO Auto-generated method stub

	}

}
