package jfm.model;

import java.text.DecimalFormat;
import java.util.*;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.*;

import jfm.lp.ModelPrimitive;
import jfm.model.Types.CropType;
import jfm.model.Types.DiseaseType;
import jfm.model.Types.ObjectiveType;
import jfm.model.Types.VariableType;

/** Controls price, yield, subsidy,input costs, disease and operation information for a crop. 
 *  Acts as a container for the CropCopy objects which represent years of successive cropping 
 *  and contain the actual operations objects. Provides functions to get and set all of its 
 *  variables. 
 *  @author Ira Cooke */
public final class Crop {
	private final List<CropCopy> yearCopies=new ArrayList<CropCopy>(); // Container for crop copies 
	// --- Crop data -- //		
	private double primaryPrice=0;// primary price per tonne 
	private double secondaryPrice=0;// secondary price per tonne 
	private double subsidy = 0; // subsidy value per hectare 
	private double priceRisk = 0; // absolute deviation in price 
	private double yieldRisk = 0; // absolute deviation in yield 
	private double eostdev=0;
//	private double primaryYield=0; // base yield in tonnes per hectare 
	private Formula primaryYield=new Formula("0");
	private Formula secondaryYield=new Formula("0"); // secondary yield in tonnes per hectare 
	private Map<VariableType,CropInput> inputCosts=new HashMap<VariableType,CropInput>();// input costs details
	private double[] yieldReductions={0}; // reductions in yield from successive cropping
	private double[] selfRotCosts={0};// financial penalties due to successive cropping
	public final CropType type; // Basic type of this crop 
	public final DiseaseType diseaseType; 
	public final boolean continuousCroppingAllowed;
	private boolean formulaVariablesSet=false;
	private final Rotation rotation;
	private double 	approximateFixedCostsPerHectare = 200; 

	/** Return a working copy of this Crop. 
	 * Performs a deep copy of the object including all crop copies and 
	 * crop financial and yield data.*/
	Crop copy(){
		List<Operation> theops = yearCopies.get(0).operations();
		Crop cpy=new Crop(type,diseaseType,rotation.copy(),continuousCroppingAllowed);
		// Copy the input costs object
		Map<VariableType,CropInput> inputsCopy=new HashMap<VariableType,CropInput>();
		for ( CropInput in:inputCosts.values()){
			inputsCopy.put(in.associatedVariable, in.copy());
		}
		cpy.setGrossMarginDetails(primaryYield.copy(),secondaryYield.copy(),primaryPrice,secondaryPrice,subsidy,priceRisk,yieldRisk,inputsCopy);		
		cpy.setSuccessiveRotationCosts(yieldReductions,selfRotCosts);
		for ( Operation op:theops){
			cpy.addOperation(op.copy());
		}
		return cpy;
	}
	/** Constructs a bare Crop object and populates it with a single crop copy (the first year) */
	public Crop(CropType cropType_,DiseaseType diseaseType_,Rotation rotation_,boolean allowContinuous)  {
		type=cropType_;
		rotation=rotation_;
		diseaseType=diseaseType_;
		continuousCroppingAllowed=allowContinuous;
		createCopies();
	}	
	
	public Rotation rotation(){return rotation;};
	/** Set the costs of successive rotations as a percentage of base yield 
	 * and as fixed costs. Both arguments are deep copied to private member variables. 
	 * After setting the rotation costs copies are created up to the length of the rotational cost vector. 
	 * Both arguments must therefore have the same length. 
	 * Note that a side effect of this function is to completely zero the operations lists in each CropCopy object.
	 * @param yieldReductions_ A vector of percentage yield reductions [0-100] 
	 * @param selfRotCosts_ A vector of fixed costs per hectare */
	public void setSuccessiveRotationCosts(double[] yieldReductions_,double[] selfRotCosts_){
//		System.out.println("Successives "+yieldReductions.length);
		if ( yieldReductions_.length!=selfRotCosts_.length){
			throw new Error("Yield Reductions and Self Rotation Costs must be vectors of equal length");
		}
		int size=yieldReductions_.length;
		yieldReductions=new double[size];
		selfRotCosts=new double[size];
		for(int i=0;i<size;i++){
			yieldReductions[i]=yieldReductions_[i];
			selfRotCosts[i]=selfRotCosts_[i];
		}
		createCopies();
	}
	
	/* Create the actual crop objects for the base crop and copies. Needs to 
	 * be called after self rotation costs are set or when a new object is created */
	private void createCopies(){
		yearCopies.clear();
		CropCopy nextcrop=new CropCopy(this);
		nextcrop.setYear(0);
		yearCopies.add(nextcrop);
		int ncopies=yieldReductions.length;
		for(int i=1;i<ncopies;i++){
			nextcrop= nextcrop.createNextCrop(); 
			yearCopies.add(nextcrop);
		}
	}
	void deRegisterCopies(){
		for ( CropCopy cpyp: yearCopies){
			cpyp.deRegister();
			for ( Operation op: cpyp.operations()){
				op.deRegister();
			}
		}
	}
	/** Add a new operation to this crop. 
	 * Creates a separate copy of its argument for every year copy of this crop. 
	 * The order in which operations are added represents their required sequence 
	 * on the farm 
	 * @param newOp The new operation to be added to this base crop. */
	public void addOperation(Operation newOp){
		for ( CropCopy cp:yearCopies){
			cp.addOperation(newOp.copy());
		}
	}
	

	/** Returns an immutable view of Crops corresponding to the year copies for this Crop.
	 * Note that there is a small overhead with this function because 
	 * it creates a new List object */
	public List<CropCopy> getYearCopies(){
		return Collections.unmodifiableList(yearCopies);
	}	
	
	
	/** Get the copy of this crop corresponding to a particular year of sequential cropping.
	 * Throws an Error if this year is not defined for the crop */
	public CropCopy getCopy(int year){
		if ( yearCopies.size() > year ){
			return yearCopies.get(year);
		} else {
			throw new Error("The requested crop year "+year+" is not defined for "+type);
		}
	}
	
	/** Ensure that all formulas used by the Crop have references to the 
	 * variables that they require. */
	private void setFormulaVariables(){
		if ( yearCopies.get(0).isRegistered()){
			Location loc = yearCopies.get(0).getLocation();
			setFormulaVariables(loc);
		}
	}
	
	/** Make sure that all the possible input variables for formulas are set 
	 * Care needs to be taken to make sure this is function is properly maintained when 
	 * new Formula variables are added.*/
	public void setFormulaVariables(Location loc){
//		System.out.println("Setting formula variables ");
		// Deal with objects that could be changed externally and which affect the formulas
		for ( CropInput cinp:inputCosts.values()){
				primaryYield.setVariable(cinp.associatedVariable,cinp.getVariable(cinp.associatedVariable));
				secondaryYield.setVariable(cinp.associatedVariable, cinp.getVariable(cinp.associatedVariable));
		}
			primaryYield.setVariable(VariableType.SOILTYPE,loc.getVariable(VariableType.SOILTYPE));
			primaryYield.setVariable(VariableType.RAINFALL,loc.getVariable(VariableType.RAINFALL));
			secondaryYield.setVariable(VariableType.SOILTYPE,loc.getVariable(VariableType.SOILTYPE));
			secondaryYield.setVariable(VariableType.RAINFALL,loc.getVariable(VariableType.RAINFALL));

			formulaVariablesSet=true;
	}
	
	
	/* Should be called whenever a change is made to the financial or yield data */
	private void recalculateGrossMargins(){	
		setFormulaVariables();
		for (CropCopy cp:yearCopies){
			cp.recalculateGrossMargin();
		}
	}
	
	/** Set the primary yield in tonnes per hectare.  */
	public void resetPrimaryYield(String newYieldFormulaString){
//		System.out.println("Setting yield to "+newYieldFormulaString);
		primaryYield.parse(newYieldFormulaString);
//		if ( this.formulaVariablesSet){
//			System.out.println(primaryYield.calculateValue());
//		}
		recalculateGrossMargins();
	}
	/** Set the secondary yield in tonnes per hectare */
	public void resetSecondaryYield(String newYieldFormulaString){
		secondaryYield.parse(newYieldFormulaString);
		recalculateGrossMargins();
	}
	
	/** Set the primary price per tonne */
	public void resetPrimaryPrice(double newP){ 
		primaryPrice=newP;
		recalculateGrossMargins();

	};
	
	/** Set the price risk value */
	public void resetPriceRisk(double r){
		priceRisk=r*0.01;
//		recalculateGrossMargins();
	}
	
	/** Set the yield risk value */
	public void resetYieldRisk(double r){
		yieldRisk=r*0.01;
//		recalculateGrossMargins();
	}
	
	public void resetEOStandardDeviation(double eostdev_){
		eostdev=eostdev_;
	}
	public void resetApproximateFixedCostsPerHectare(double fc){
		approximateFixedCostsPerHectare=fc;
	}
	
	/** Reset an input cost */
	public void resetInputCost(VariableType inputType,double newUnitCost){
		CropInput inVar = this.inputCosts.get(inputType);
		if ( inVar == null ){
			// Do nothing
//			throw new Error("Attempt to set cost for undefined input variable");
		} else {
			inVar.setUnitCost(newUnitCost);
			recalculateGrossMargins();
		}
	}
	
	
	/** Return the primary price per tonne of this crop */
	public double getPrimaryPrice(){ return primaryPrice;};
	
	/** Return the total solved area of all CropCopies. */
	public double getSolvedArea(){
		double total=0;
		for(CropCopy cp:yearCopies){
			total+=cp.getSolvedArea();
		}
		return total;
	}
	/** Set the secondary price per tonne */
	public void resetSecondaryPrice(double newP){
		secondaryPrice=newP;
		recalculateGrossMargins();
	}
	/** Set the subsidy value per hectare */
	public void resetSubsidy(double newS){
		subsidy=newS;
		recalculateGrossMargins();
	}
	/** Set the input cost details for a particular input type 
	 * */
	public void resetInput(VariableType vtype,CropInput inp){
		inputCosts.put(vtype, inp);
		recalculateGrossMargins();

	}
	public Collection<CropInput> inputSet(){
		return Collections.unmodifiableCollection(inputCosts.values());
	}

	
	/** Function for setting all financial data at once. 
	 * When building a crop object for the first time this function must be used to avoid unsetVariable errors
	 * due to formulas that depend on more than one quantity.
	 * 
	 * This function should be used if multiple financial data need to be set as it 
	 * performs just one recalculation of the gross margins rather than one 
	 * for each piece of data. 
	 * @param primY The primary yield
	 * @param secY The secondary yield
	 * @param primP The primary price 
	 * @param secP The secondary price 
	 * @param subs The subsidy value 
	 * @param inputs The total input costs (positive number) */
	public void setGrossMarginDetails(Formula primY,Formula secY,double primP,double secP,double subs,double prisk_,double yrisk_,
			Map<VariableType,CropInput> inputs){
		priceRisk=prisk_;
		yieldRisk=yrisk_;
		primaryYield=primY;
		secondaryYield=secY;
		primaryPrice=primP;
		secondaryPrice=secP;
		subsidy=subs;
		inputCosts=inputs;
		recalculateGrossMargins();
	}
	void checkFormulaVariablesSet(){
		if ( !formulaVariablesSet ){
			throw new Error("Attempt to calculate formula of crop but variables not yet set for formula ");
		}
	}
	public double getPrimaryYield(){
		checkFormulaVariablesSet();
		return primaryYield.calculateValue();
	}
	double getSecondaryYield(){
		checkFormulaVariablesSet();
		return secondaryYield.calculateValue();
	}
			
	/** Primitive model type representing a single crop in a single year. 
	 * @author Ira Cooke */
	class CropCopy extends ModelPrimitive {
		private int copyYear=0;
		private final Crop parentCrop;
		private Operation handOverOperation=null;
		private final List<Operation> operations=new ArrayList<Operation>();
		public Location getLocation(){
			if ( isRegistered()){
				return parentComponent.getParent().location();
			} else {
				return null;
			}
		}
/*		public Crop parentCrop(){
			if ( isRegistered() ){
				return parentComponent.getParent().cropping.getCrop(type);
			} else {
				throw new Error("Attempt to get parentCrop on unregistered CropCopy");
			}
		}
	*/	
		CropCopy(Crop parent){
			super();
			parentCrop=parent;
		}
		
		/** @return an immutable view of the operations for this CropCopy. 
		 * Note that there is a small overhead in creating a new List object when this is called */
		List<Operation> operations(){
			return Collections.unmodifiableList(operations);
		}
		
		/** Return the Variance at Risk measure for this crop */
		public double getVARRisk(double alpha,double offset){
			// Assume normally distributed losses. VAR is equal to the alpha quantile of this loss distribution
			
			if ( parentCrop.type==Types.CropType.SETASIDE){
				return 0;
			}
			
			double stdEO = eostdev*0.01*grossOutputLessSubsidy();
			
			if ( stdEO == 0 ){
				return 0;
//				throw new Error ("Zero stdeviation for "+parentCrop.type);
			}
			
			
			
			double meanEO = grossMargin();
			
			NormalDistribution ndist = new NormalDistributionImpl(meanEO,stdEO);
			
			double var;
			try {
				var = ndist.inverseCumulativeProbability(alpha);
			} catch (MathException ex){
				throw new Error(ex.getMessage());
			}
			var = var - totalInputs() - selfRotCosts[copyYear]-approximateFixedCostsPerHectare-offset;
			if ( var > 0 ){
				var = 0;
			}
		//	System.out.println(parentCrop.type+" "+var+" m:"+meanLoss+" stdv:"+stdLoss);
			
			return var;	
		}
		
		/** Return the absolute deviation in gross margin of this crop */
		public double getMOTADRisk(){ 
//			double risk = (1-(1-yieldRisk)*(1-priceRisk));
	//		return risk*grossMargin();
			if ( parentCrop.type==Types.CropType.SETASIDE){
				return 0;
			}
			double stdEO = eostdev*0.01*grossOutputLessSubsidy()*1.0;
			return stdEO;
		};

		/** Return the name of the CropType associated with the Crop of which this is a copy */
		public String name(){return type.name;};
		
		/* Calculate the total input cost for this cropyear */
		private double totalInputs(){
			double total=0;
			for(CropInput in:inputCosts.values()){
				if ( in.associatedVariable == Types.VariableType.TRANSPORT){					
					total+=in.getCost(copyYear)*getPrimaryYield()*(1-yieldReductions[copyYear]);
				} else {
					total+=in.getCost(copyYear);
				}
			}
			return total;
		}
		
		/** @return The solved area for this crop copy */
		public double getSolvedArea(){
			return getSolution()[0];
		}
		
		/** Calculates the enterprise output (as in FBS data) which represents the total value of the crop. 
		 * Penalties due to yield reductions are not taken into account here although they probably should*/
		private double grossOutputLessSubsidy(){
			return grossMargin()+totalInputs()+selfRotCosts[copyYear]-subsidy;
		}
		
		/* Calculate the gross margin given a particular yield penalty */
		private double grossMargin(double yieldPenalty){
			double marg=0;
			double mod=(1-yieldReductions[copyYear]-yieldPenalty);
			
			marg+=(primaryPrice*primaryYield.calculateValue()+secondaryPrice*secondaryYield.calculateValue())*mod+subsidy-totalInputs()-selfRotCosts[copyYear];
//			if ( marg < 0 ){
//				System.out.println(totalInputs()+" "+primaryPrice+" "+primaryYield.calculateValue());
//			}
			
			return marg;
		}
		
		public double baseGrossMargin(){
			double marg=0;
			double mod=1;
			marg+=(primaryPrice*primaryYield.calculateValue()+secondaryPrice*secondaryYield.calculateValue())*mod+subsidy-totalInputs()-selfRotCosts[copyYear];
			return marg;
		}
		/* Return the DiseaseType associated with the Crop of which this is a copy */
		DiseaseType diseaseType(){return diseaseType;};
		/* Return the CropType associated with the Crop of which this is a copy */
		CropType type(){return type;};
		
		/* Get the base crop for this CropType */
		Crop baseCrop(){ return parentCrop;};
		
		/* Calculate the effect of a yield penalty in terms of a cost per hectare assuming fixed costs */
		double costOfYieldPenalty(double yieldPenalty,Location loc){
			return (primaryYield.calculateValue()*primaryPrice+secondaryYield.calculateValue()*secondaryPrice)*yieldPenalty;
		}
		/* Recalculate the gross margin and set the coefficient of 
		 * dependent MatrixVariable associated with this CropCopy */
		private void recalculateGrossMargin(){
			if ( parentComponent !=null){
				if ( !parentComponent.getParent().needsRebuild()){	
//					System.out.println(this.baseCrop().type+" "+grossMargin());
					setCoefficient(ObjectiveType.PROFIT,grossMargin(),0);
				} else {
					// This might fail if we have dramatic rebuilds required
					setCoefficient(ObjectiveType.PROFIT,grossMargin(),0);
//					throw new Error("I need a rebuild but I want to set my coefficient");
				}
			} else {
				// Not ready to calculate gross margins yet for this object 
			}
		}
		/* Return the gross margin assuming standard yield penalty for successive rotations */
		double grossMargin(){
			return grossMargin(0);
		}
		
		protected void updateStructure(Object caller){
			recalculateGrossMargin();
			for(Operation op:operations){
				op.updateStructure(this);
			}
		}

		/** Get a break down of the contributions to profit and loss for this CropCopy 
		 * @return A LinkedHashMap<String,Double> where the String key is the name for a profit contribution and Double is the value */
		public Map<String,Double> getProfitSummary(boolean useMarginals){
			Map<String,Double> profits = new LinkedHashMap<String,Double>();
			double div=1;
			if ( useMarginals){div=solvedArea();}
			profits.put("Total", (grossMargin()*solvedArea()-operationPenaltyCost()-operationFuelCost())/div);
			profits.put("Gross", grossMargin()*solvedArea()/div);
			profits.put("OpPenalty", -operationPenaltyCost()/div);
			profits.put("OpFuel", -operationFuelCost()/div);
			profits.put("Area", solvedArea());
			return profits;
		}
		
		
		
		/** Get a detailed breakdown of contributions to each objective in the solution for this crop copy
		 * @return A formatted string of detailed contributions to objectives */
		public String getDetailedProfitBreakdown(int columnWidth){
			StringBuffer buff = new StringBuffer();
			DecimalFormat d1 = new DecimalFormat("#0");
			String rowTitleFormat = "%1$30s";
			String rEF = "%1$15.1f";
			buff.append(name()+" "+this.copyYear+"\n");
			buff.append(String.format(rowTitleFormat, "Base Value:"));
			buff.append(String.format(rEF,solvedArea()*grossMargin())+" "+d1.format(grossMargin())+" "+d1.format(totalInputs())+"\n");
			buff.append(String.format(rowTitleFormat, "Rotation Cost:"));
			double[] yieldAndCostPenalties=this.parentComponent.getParent().rotations.getSolvedRotationPenaltyForCrop(this,this.parentComponent.getParent().location());
			buff.append(String.format(rEF,-yieldAndCostPenalties[0]));
			buff.append(String.format(rEF,-yieldAndCostPenalties[1])+"\n");
			for ( Operation op: operations){
				buff.append(String.format(rowTitleFormat, op.type+":"));
				buff.append(String.format(rEF, -op.solvedFuelCost()));
				buff.append(String.format(rEF, -op.solvedPenaltyCost())+"\n");
			}
			buff.append("\n\n");
			return buff.toString();
		}
		
		/** \internal Return the sum of all operation penalty costs for this CropCopy */
		private double operationPenaltyCost(){
			double cost=0;
			for(Operation op:operations){
				cost+=op.solvedPenaltyCost();
			}
			return cost;
		}
		/** \internal Return the total fuel cost for all operations on this CropCopy */
		private double operationFuelCost(){
			double cost=0;
			for(Operation op:operations){
				cost+=op.solvedFuelCost();
			}
			return cost;
		}



		double grossMarginLossForYieldPenalty(double penalty){
			return (grossMargin()-grossMargin(penalty));
		}

		
		/* Get the area of the final solution for this crop if it exists */
		double solvedArea(){
			return getDependent(0).solution();
		}
		/* Get the hand over Operation. */
		Operation handOverOperation(){
			if (handOverOperation==null){
				throw new Error("No hand over operation defined for crop "+name()+" "+copyYear);
			} 
			return handOverOperation;
		}

		/* Add an operation.  Does not create a copy of its argument. The order 
		 * in which operations are added determines the sequence order for the 
		 * purposes of imposing sequential operation constraints. 
		 * Throws an Error if the operation already exists for this crop */
		void addOperation(Operation newOp){
			if ( operations.contains(newOp)){
				throw new Error("The Operation has already been added ");
			}
			if ( newOp.isHandOver()){
				handOverOperation=newOp;
			}
			newOp.attachToCrop(this);
			operations.add(newOp);
		}
		
		/* Return a working copy of this crop. */
/*		private CropCopy copy(){
			CropCopy cp =new CropCopy(parentCrop);
			for ( Operation op:operations){
				cp.addOperation(op.copy());
			}
			cp.setYear(this.copyYear);
			return cp;
		}
*/
		
		/* Check to see if there are any further copies to be added to the base Crop object 
		 * and creates a new empty crop with an incremented copyYear if this is the case  */
		CropCopy createNextCrop(){
			if (!hasNext()){
				throw new Error("Attempt to createNextCrop when the maximum number of copies has already been reached");
			}
			CropCopy cp = new CropCopy(parentCrop);
			cp.setYear(this.copyYear+1);
			return cp;
		}

		private boolean hasNext(){
			if ( yieldReductions.length > copyYear+1){
				return true;
			} else {
				return false;
			}
		}
		
		/* Set the copy year  */
		private void setYear(int ny){
			copyYear=ny;
		}
		/* Get the copy year */
		int copyYear(){return copyYear;};
		
//		 ---- REPORTING ---- //

		/** Print this object as a string 
		 * @return a string object with a description of this crop */
		public String toString(){
			StringBuffer outstring = new StringBuffer();
			outstring.append("Crop: ");
			outstring.append(name());
			for(Operation op:operations){
				outstring.append(op+"\n");
			}
			return outstring.toString();
		}
		

		String printSolution(char sep){
			Location loc = parentComponent.getParent().location();
			StringBuffer buff=new StringBuffer();
			buff.append(this.name()+"\n");
			for(Operation op:operations){
//				buff.append(op.printWorkRateDetails(loc)+',');
				buff.append(op.printSolution(sep));
				buff.append('\n');
			}
			return buff.toString();
		}
		
	}
	


	/** Print a quick check of the formulas. This is mostly for debugging */
	public String printFormulaCheck(Location loc){
		StringBuffer buff=new StringBuffer();
		buff.append("Yield1: "+jfm.utils.MathPrint.df3.format(primaryYield.calculateValue())+"\n");
		buff.append("Yield2: "+jfm.utils.MathPrint.df3.format(secondaryYield.calculateValue())+"\n");
		for(Operation op:yearCopies.get(0).operations){
			buff.append(op.type+": "+jfm.utils.MathPrint.df3.format(op.workrate(loc))+"\n");
		}
		buff.append("\n");
		return buff.toString();
	}
	/** Print this object as a string 
	 * @return a string object with a description of this crop */
	public String toString(){
		StringBuffer outstring = new StringBuffer();
		outstring.append("Crop: ");
		outstring.append(type);
		outstring.append(" ");
		outstring.append("Price: ");
		outstring.append(primaryPrice);
		outstring.append(" ");
		outstring.append("Yield: ");
		outstring.append(primaryYield);
		outstring.append(" ");
		outstring.append("Subsidy: ");
		outstring.append(subsidy);
		outstring.append(" Disease Class: "+diseaseType);
		outstring.append("Num copies "+yearCopies.size()+"\n");
		outstring.append(yearCopies.get(0)+"\n");
		return outstring.toString();
	}
	
}
