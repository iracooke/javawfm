/**
 * 
 */
package jfm.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jfm.lp.ConstraintBuilder;
import jfm.lp.LPX;
import jfm.lp.MatrixElement;
import jfm.lp.MatrixRow;
import jfm.lp.MatrixVariable;
import jfm.lp.ModelComponent;
import jfm.model.Types.CropType;
import jfm.model.Types.ObjectiveType;
import jfm.model.Types.OperationType;
import jfm.model.Types.WorkerSubType;
import jfm.model.Types.WorkerType;
import jfm.model.Types.ELSCode;
import jfm.model.ELSOption;
import jfm.model.ELSOption.*;

/**
 * @author iracooke
 *
 */
public class StubblesComponent extends ELSOptionComponent {
	private int startPeriod;
	private int endPeriod;
	private double subsidy; // Per unit area payment for winter stubbles
//	private MatrixVariable subsidyVar=null;
	public static ELSCode elscode = ELSCode.EF6;

	private static Set<OperationType> harvestOps=new HashSet<OperationType>();
	private static Set<OperationType> ploughOps = new HashSet<OperationType>();
	static {
		harvestOps.add(OperationType.COMBINE);	
		harvestOps.add(OperationType.HARVEST);
		ploughOps.add(OperationType.PLOUGH);
		ploughOps.add(OperationType.BROADCAST);
	}
	
	public StubblesComponent(int endP,double cashSubsidy_){
		super(ModelComponent.MCType.STUBBLES);
		requireObjective(ObjectiveType.WINTERSTUBBLE);
		requireObjective(ObjectiveType.PROFIT);
		addConstraintBuilder(new WinterStubbleEqualityConstraint());
//		startPeriod=startP;
		endPeriod=endP;
		subsidy=cashSubsidy_;
		options.put(elscode,new ELSOption(elscode,0,elscode.defaultPoints));
	}
	public StubblesComponent copy(){
		return new StubblesComponent(endPeriod,subsidy);
	}
	
	public void setSubsidy(double subs){
		subsidy=subs;
		this.requireMatrixRebuild();
	}
	
	public void setFormulaVariables(){}
	/* (non-Javadoc)
	 * @see jfm.lp.ModelComponent#initializeStructure()
	 */
	@Override
	protected void initializeStructure() {
		Farm farm = getParent();
	
		startPeriod=farm.numPeriods-1; 
		// ie we assume any harvest will do .. otherwise we need to get fancy about rotations.
		CroppingComponent cropping = farm.cropping;
		
		// We create a single matrix variable to hold the overall amount of winter stubble for the purposes of adding up profits from it
		MatrixVariable newVariable=new MatrixVariable(subsidy,0.0,0.0,LPX.LPX_LO,LPX.LPX_CV,matrix.numCols(),ObjectiveType.PROFIT);
		newVariable.setTag("Total Winter Stubble");	
		matrix.addVariable(newVariable);
		options.get(elscode).registerVariable(newVariable, 0);
		options.get(elscode).registerParent(this);
		
//		System.out.println("Initialising stubble");
		// Make the Stubble Objective apply to all crops 
		for ( CropType ct:cropping.baseCropTypes()){

			if ( ct!=CropType.SETASIDE){
				for ( Crop.CropCopy cp: cropping.getCrop(ct).getYearCopies()){
				
					Operation harvest=getMatchingOperation(harvestOps,cp);
					Operation plough=getMatchingOperation(ploughOps,cp);
					
					Set<Integer> allowedPeriodsHarv=harvest.unfoldedAllowedSet();
					for( int p=farm.numPeriods;p<=startPeriod+farm.numPeriods;p++){
					
						if ( allowedPeriodsHarv.contains(p)){
							harvest.setCoefficientAndRegisterObjective(ObjectiveType.WINTERSTUBBLE, 1, p);
				//			harvest.incrementCoefficientForExistingObjective(ObjectiveType.PROFIT, subsidy, p);
	//						throw new Error("Setting coeff");
							// Look to see when it gets ploughed
							Set<Integer> allowedPeriodsPlgh=plough.unfoldedAllowedSet();
							for ( int pp=0;pp<=farm.numPeriods+endPeriod;pp++){
								if ( allowedPeriodsPlgh.contains(pp)){
									plough.setCoefficientAndRegisterObjective(ObjectiveType.WINTERSTUBBLE, -1, pp);
				//					plough.incrementCoefficientForExistingObjective(ObjectiveType.PROFIT, -subsidy, pp);
			//						throw new Error("Setting ceff");
								}
							}
						}
					}/*
					Set<Integer> allowedPeriodsPlgh=plough.unfoldedAllowedSet();
//					System.out.println("Plough "+allowedPeriodsHarv);
					for ( int  p=0;p<=farm.numPeriods+endPeriod;p++){
						if ( allowedPeriodsPlgh.contains(p)){
							plough.setCoefficientAndRegisterObjective(ObjectiveType.WINTERSTUBBLE, -1, p);
	//						throw new Error("Setting ceff");
						}
					}
					*/
				}				
			} else {
//				Crop.CropCopy cp = cropping.getCrop(ct).getYearCopies().get(0); // Always only one setaside year
//				cp.setCoefficientAndRegisterObjective(ObjectiveType.WINTERSTUBBLE, 1, 0);
				// Setaside should not contribute
			}
			
		}
	}


	public static Operation getMatchingOperation(Set<OperationType> opList,Crop.CropCopy cp){
		List<Operation> ops=cp.operations();
		for(Operation op:ops){
			if ( opList.contains(op.type)){
				return op;
			}
		}
		throw new Error("No matching operation for crop "+cp.type()+" in list "+opList);
	}
	
	public static double stubbleAreaForRotation(CropType from,CropType to,int endP,Farm farm){
		boolean firstYr=true;
		if ( from == to ){
			firstYr=false;
		}
//		HashMap<Integer,Double> hatime = this.getHarvestTimingForCrop(from, firstYr, farm);
		HashMap<Integer,Double> pltime=StubblesComponent.getEndStubbleTimingForCrop(to, firstYr, endP, farm);
		double areaS=0;
		for(Double d:pltime.values()){
			areaS+=d;
		}
		return areaS;
	}
	
	// Get a map from integer period to area harvested
	public static HashMap<Integer,Double> getHarvestTimingForCrop(CropType from, boolean firstYr,Farm farm){
		CroppingComponent cropping=farm.cropping;
		int startPeriod = farm.numPeriods-1;
		HashMap<Integer,Double> ha = new HashMap<Integer,Double>();
		
		int yr=0;
		for ( Crop.CropCopy cp: cropping.getCrop(from).getYearCopies()){
			if ( firstYr && (yr > 0)){
				// Don't do anything
			} else if ( !firstYr && (yr==0)){
				// Do nothing
			} else {
				Operation harvest=getMatchingOperation(harvestOps,cp);
			
				Set<Integer> allowedPeriodsHarv=harvest.unfoldedAllowedSet();
				for( int p=farm.numPeriods;p<=startPeriod+farm.numPeriods;p++){			
					if ( allowedPeriodsHarv.contains(p)){
						double a=0;
						if ( ha.containsKey(p)){
							a=ha.get(p);
						}
						ha.put(p, harvest.solvedArea(p)+a);
					}
				}
			}
			yr++;
			
		}
		return ha;
	}
	
	public static HashMap<Integer,Double> getEndStubbleTimingForCrop(CropType to,boolean firstYr,int endP,Farm farm){
		CroppingComponent cropping=farm.cropping;
		HashMap<Integer,Double> pl = new HashMap<Integer,Double>();
		
		int yr=0;
		for ( Crop.CropCopy cp: cropping.getCrop(to).getYearCopies()){
			if ( firstYr && (yr > 0)){
				// Don't do anything
			} else if ( !firstYr && (yr==0)){
				// Do nothing
			} else {
				Operation plough=getMatchingOperation(ploughOps,cp);
				Set<Integer> allowedPeriodsPlgh=plough.unfoldedAllowedSet();
				for ( int pp=farm.numPeriods+endP;pp<farm.numPeriods*Farm.maxYears;pp++){
					if ( allowedPeriodsPlgh.contains(pp)){
						double a=0;
						if ( pl.containsKey(pp)){
							a=pl.get(pp);
						}
						pl.put(pp, plough.solvedArea(pp)+a);
					}
				}
			}
			yr++;
			
		}
		return pl;
	}
	
	/** Ensures that the Total Winter Stubble variable is actualy equal to the amount of WS */
	public final class WinterStubbleEqualityConstraint extends ConstraintBuilder {
		public WinterStubbleEqualityConstraint(){
			super(ConstraintBuilder.CBType.WSEQUAL,ModelComponent.MCType.STUBBLES);
		}
		protected void build(){
			Farm farm = getParent();
			int row=matrix.numRows();			
			CroppingComponent cropping=farm.cropping;
			// This goes through the same code as was used to build the structure
		
//			System.out.println("Initialising stubble");
			// Make the Stubble Objective apply to all crops 
			MatrixRow rp=new MatrixRow(0,0,LPX.LPX_UP,row,"WSProfit","WSProfit");
			matrix.addRow(rp);	
			row++;
			rp.addElement(new MatrixElement(options.get(elscode).getDependentColumn(0),1));
			
			for ( CropType ct:cropping.baseCropTypes()){

				if ( ct!=CropType.SETASIDE){
					for ( Crop.CropCopy cp: cropping.getCrop(ct).getYearCopies()){
					
						Operation harvest=getMatchingOperation(harvestOps,cp);
						Operation plough=getMatchingOperation(ploughOps,cp);
						
						Set<Integer> allowedPeriodsHarv=harvest.unfoldedAllowedSet();
						for( int p=farm.numPeriods;p<=startPeriod+farm.numPeriods;p++){
						
							if ( allowedPeriodsHarv.contains(p)){
//								harvest.setCoefficientAndRegisterObjective(ObjectiveType.WINTERSTUBBLE, 1, p);								
								rp.addElement(new MatrixElement(harvest.getDependentColumn(p),-1));
								// Look to see when it gets ploughed
								Set<Integer> allowedPeriodsPlgh=plough.unfoldedAllowedSet();
								for ( int pp=0;pp<=farm.numPeriods+endPeriod;pp++){
									if ( allowedPeriodsPlgh.contains(pp)){
//										plough.setCoefficientAndRegisterObjective(ObjectiveType.WINTERSTUBBLE, -1, pp);
										rp.addElement(new MatrixElement(plough.getDependentColumn(pp),1));
									}
								}
							}
						}
					}				
				} else {
					// No winterstubble from setaside contributes to profit
				}
				
			}
		}			
	}
	

	/* (non-Javadoc)
	 * @see jfm.lp.ModelComponent#name()
	 */
	@Override
	protected String name() {
		return "Stubbles";
	}

	/* (non-Javadoc)
	 * @see jfm.lp.ModelComponent#updateStructure()
	 */
	@Override
	protected void updateStructure() {
		structureUpdateDone();
	}

}
