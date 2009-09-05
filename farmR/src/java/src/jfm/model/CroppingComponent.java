
package jfm.model;


import java.text.DecimalFormat;
import java.util.*;

import jfm.lp.*;
import jfm.model.Crop.CropCopy;
import jfm.model.Types.*;


/** Contains all crops and constructs matrix variables and constraints relating to crop and operation areas.
 * This is the key model component class for the model. It contains all crop objects, definitions for 
 * all disease and rotation types, the overall area of cropping (farm area) and the limit 
 * constraints which may be controlled by the user. Exposes its underlying crops via the 
 * \c getCrop function to allow them to be externally manipulated.
 * 
 * @author Ira Cooke */
public final class CroppingComponent extends ModelComponent {	
	// ---- Cropping Data ---- //
	private final Map<DiseaseType,Disease> diseases=new HashMap<DiseaseType,Disease>(); 
//	private final Map<CropType,Rotation> rotations=new HashMap<CropType,Rotation>();
	private final List<Limit> limits= new ArrayList<Limit>(); // Limits on Areas or other quantities for crops
	private final Map<CropType,Crop> crops=new HashMap<CropType,Crop>(); // List of base crops 
	private final Set<CropYear> cropYearsSet=new LinkedHashSet<CropYear>();// Handy aliase for each of the crop years across all types 
	private static String constraintTag="Cropping";
//	private  double cropArea; // Total crop area avail on this farm 
	
	/** Gets a specified disease object.  
	 * @return The specified disease 
	 * @param type The desired disease type 
	 * */
	public Disease getDisease(DiseaseType type){
		if ( diseases.containsKey(type)){
			return diseases.get(type);
		} else {
			throw new Error("Attempt to get undefined disease of type "+type);
		}
	}
	/** Attempt to replace a disease type with a new Disease object. 
	 * @param type The disease type to be replaced 
	 * @param newDisease The new disease object to be associated with type 
	 * @return true if the operation was successful
	 * */
	public boolean replaceDisease(DiseaseType type,Disease newDisease){
		if ( diseases.containsKey(type)){
			diseases.put(type, newDisease);
			return true;
		} else {
			return false;
		}
	}
	
	/** Returns the crop copy corresponding to a particular year of a particular CropType.
	 * If the requested crop type is not present an Error is thrown.  
	 * @param baseType The base type of the requested crop. */
	public Crop getCrop(CropType baseType){
		if ( !crops.containsKey(baseType)){
			throw new Error("Request for undefined crop type "+baseType);
		}
		return crops.get(baseType);
	}
	
	
	
	public void setFormulaVariables(){
//		System.out.println("Setting fvars for cropping "+crops.values().size());
		for ( Crop cp: crops.values()){
//			System.out.println("Setting vars for "+cp.type);
			cp.setFormulaVariables(getParent().location());
		}
	}
	
	/** @return A map from crop types to copies of the crops in this object. 
	 * Returned crops are deregistered from the parent object */
	public Map<CropType,Crop> copyCrops(){
		Map<CropType,Crop> cps = new LinkedHashMap<CropType,Crop>();
		for ( Crop cp: crops.values()){
			Crop cpcpy=cp.copy();
			cpcpy.deRegisterCopies();
			cps.put(cp.type, cpcpy);
		}
		return cps;
	}
	
	/** @return An unmodifiable view of all the crops . */
	public Map<CropType,Crop> getCrops(){
		Map<CropType,Crop> cps = Collections.unmodifiableMap(crops);
		return cps;
	}
	/** Returns an immutable view of the set of all base CropTypes for this Cropping object. */
	public Set<CropType> baseCropTypes(){
		return Collections.unmodifiableSet(crops.keySet());
	}
	/** Returns an immutable view of the set of all CropYears for this Cropping object. */
	public Set<CropYear> cropYears(){
		return Collections.unmodifiableSet((cropYearsSet));
	}
	
//	public List<Limit> getLimits(){return Collections.unmodifiableList(limits);};
	public void clearCrops(){
		crops.clear();
		cropYearsSet.clear();
		requireMatrixRebuild();
	}
	
	/** Clear all the Limits. */
	public void clearLimits(){
		limits.clear();
		requireMatrixRebuild();
	}
	/** Add a Limit to this Cropping object. 
	 * This function does not copy its argument when adding 
	 * @param lim A new limit to be added */
	public void addLimit(Limit lim){
		limits.add(lim);
		requireMatrixRebuild();
	}
	
	// CONSTRUCTOR //
	public CroppingComponent(){
		super(ModelComponent.MCType.CROPPING);
		requireObjective(ObjectiveType.PROFIT);
		addConstraintBuilder(new OperationAreaConstraint());
		addConstraintBuilder(new OperationSequencingConstraint());
		addConstraintBuilder(new NonSequentialOperationConstraint());
		addConstraintBuilder(new DiseaseConstraint());
		addConstraintBuilder(new OperationMinAreaConstraint());
		addConstraintBuilder(new AreaLimitConstraint());
	}

	
	/** Add a new type of Crop and its associated year copies.
	 * This function does not copy its argument when adding. 
	 * Throws an Error if the type of crop to be added is already present.
	 * @param newCrop A Crop object of the type to be added */
	public void addCrop(Crop newCrop){
		CropType type=newCrop.type;
		if ( crops.containsKey(type)){
			throw new Error("Can't add crop of type "+type+" because there is already a crop with that type");
		}
		for ( int i=0;i<newCrop.getYearCopies().size();i++){
			cropYearsSet.add(new CropYear(type,i));
		}
		crops.put(type,newCrop);
//		addRotation(newCrop.rotation());
		requireMatrixRebuild();
	}
	
	/** Add a new Disease corresponding to a particular base DiseaseType.
	 * This function does not copy its argument when adding.
	 * Throws an Error is the base type of the new disease is already present.
	 * @param newDisease A Disease object to be added */
	public void addDisease(Disease newDisease){
		DiseaseType baseType=newDisease.base;
		if ( diseases.containsKey(baseType)){
			throw new Error("Can't add disease of type "+baseType+" because a disease with that type already exists ");
		}
		diseases.put(baseType, newDisease);
		requireMatrixRebuild();
	}
	
	/** Add a new Rotation penalty specification corresponding to a DiseaseType in the crops being rotated to.
	 * This function does not copy its argument when adding.
	 * @param newRotation The Rotation object specifying the penalties */
/*	private void addRotation(Rotation newRotation){
		CropType toType = newRotation.to;
		if ( rotations.containsKey(toType)){
			throw new Error("Can't add rotation of disease type "+toType+" because a rotation penalty for that disease already exists ");
		}
	//	System.out.println("Adding: "+toType+": "+newRotation.getPenalty(toType)[0]+" "+newRotation.getPenalty(toType)[1]);
		rotations.put(toType, newRotation);
		requireMatrixRebuild();
	}*/
	
	
	/** Create a working copy of all the objects contained within this Cropping object. 
	 * Performs a deep copy on all mutable objects such as crops, limits and rotations, and a shallow copy 
	 * of immutable objects such as diseases and rotations. Changes in the copied object will not 
	 * be reflected in the original.
	 * @return A working copy of the current object */
	public CroppingComponent copy(){
		CroppingComponent cpcpy = new CroppingComponent();		
		for ( Crop cb:crops.values()){
			cpcpy.addCrop(cb.copy());
		}
		for(Disease dp:diseases.values()){
			cpcpy.addDisease(dp);
		}
		/*
		for(Rotation rp:rotations.values()){
			cpcpy.addRotation(rp.copy());
		}*/
		for(Limit lm:limits){
			cpcpy.addLimit(lm.copy());
		}
		return cpcpy;
	}
	
	
	/** Get a list of all the CropCopy objects which have a non-zero solved area */
	List<CropCopy> cropsUsed(){
		List<CropCopy> thelist=new ArrayList<CropCopy>();
		for ( CropYear cy:cropYearsSet){
			CropCopy cp=crops.get(cy.base).getCopy(cy.copyYear);
			if ( cp.getSolvedArea() > 0 ){
		//		System.out.println(cp.name()+cp.getSolvedArea());
				thelist.add(cp);
			}
		}
		return thelist;
	}
	
	double[] getRotationPenalty(CropType to,DiseaseType from){
		if ( crops.containsKey(to)){
			Rotation torot=crops.get(to).rotation();
			return torot.getPenalty(from);
		} else {
			double[] nocosts={0,0};
			return nocosts;
		}
	}
	
	Set<CropType> cropsWithDisease(DiseaseType dis){
		Set<CropType> cps=new HashSet<CropType>();
		if ( dis==DiseaseType.NONE){
			return cps; // If disease is None then we just return the empty set
		}
		for(CropType ctype: crops.keySet()){
			if ( crops.get(ctype).diseaseType==dis){
				cps.add(ctype);
			}
		}
		return cps;
	}
	
	/** Checks to make sure that all the required diseases and rotations have been defined 
	 * for the crops present */
	public boolean diseasesAndRotationsComplete(){
		for (Crop cp:crops.values()){
			if ( !diseases.containsKey(cp.diseaseType) && cp.diseaseType!=DiseaseType.NONE){
				return false;
			}
		}
		return true;
	}
	protected void initializeStructure(){
		Location location=getParent().location();
		for(CropType ct:crops.keySet()){
			List<CropCopy> clist=crops.get(ct).getYearCopies();
			for(int i=0;i<clist.size();i++){
				CropCopy cp=clist.get(i);
				cp.registerParent(this);		
				MatrixVariable newVariable=new MatrixVariable(cp.grossMargin(),
						0.0,1.0,
						LPX.LPX_LO,LPX.LPX_CV,matrix.numCols(),ObjectiveType.PROFIT);
				newVariable.setTag(ct.xmlname+"_"+cp.copyYear());
				matrix.addVariable(newVariable);	// The gross margin variable for this crop
				cp.registerVariable(newVariable,0);
				// Now the operation variables for this crop
				for(Operation op:cp.operations()){
					op.registerParent(this);
					List<Integer> pallow = new ArrayList<Integer>(op.unfoldedAllowedSet());
					Collections.sort(pallow); // We need to ensure that columns for each set of periods 
					for(Integer p:pallow){
						newVariable=new MatrixVariable(-op.cost(p,cp,location,getParent().fuelPrice()),0,0,
								LPX.LPX_LO,LPX.LPX_CV,matrix.numCols(),ObjectiveType.PROFIT);
								matrix.addVariable(newVariable);
					newVariable.setTag(ct.xmlname+"_"+cp.copyYear()+"_"+op.type.shortName+"_"+p);
					op.registerVariable(newVariable,p);
					}
				}
			}	
		}
	}
	
	
	protected void updateStructure(){
		for(Crop cp:crops.values()){
			for ( CropCopy cy:cp.getYearCopies()){
				cy.updateStructure(this);
			}
		}
		structureUpdateDone();
	}

	
	private final void buildLimitAreaConstraint(Limit lim){

		int row=matrix.numRows();
		MatrixRow rowpointer =new MatrixRow(lim.min,lim.max,lim.type,
				row,constraintTag,"AreaLimit");
		matrix.addRow(rowpointer);		
		row++;
		Set<CropType> limCrops=new HashSet<CropType>(lim.getCropSubjects());	
		Set<CropType> allCrops=crops.keySet();
		limCrops.retainAll(allCrops); // Ensures only crops that actually exist get this constraint applied;
		if ( limCrops.size() != lim.getCropSubjects().size()){
			throw new Error("One or more limit crops in the list \n"+lim.getCropSubjects()+
					"\n were not specified in the cropping model \n"+allCrops);
		}
		for(CropType ct:limCrops){
			List<CropCopy> cplist=crops.get(ct).getYearCopies();
			for( CropCopy cp:cplist){
				rowpointer.addElement(new MatrixElement(cp.getDependentColumn(0),1));
			}
		}
	}	
		
	/** Constrain the area of each crop to equal the area of the first operation for that crop. 
	 * 
	 * @author Ira Cooke 
	 * 
	 * */ 
	public final class OperationAreaConstraint extends ConstraintBuilder {
		public OperationAreaConstraint(){
			super(ConstraintBuilder.CBType.OPAREA,ModelComponent.MCType.CROPPING);
		}
		protected void build(){
			int row=matrix.numRows();
			for(CropType ct:crops.keySet()){
				for(CropCopy cp:crops.get(ct).getYearCopies()){
					MatrixRow rowpointer =new MatrixRow(0,0,LPX.LPX_FX,
							row,constraintTag,type().tag+"_"+ct);
					matrix.addRow(rowpointer);		
					row++;
					rowpointer.addElement(new MatrixElement(cp.getDependentColumn(0),-1));
					Operation op=cp.operations().get(0); // The first operation is the only one that is relevant here 
					for(Integer p:op.unfoldedAllowedSet()){
						rowpointer.addElement(new MatrixElement(op.getDependentColumn(p),1));
//					}
					}
				}
			}
		}
	}
	/** Constrain the total area of each operation (regardless of sequence) to equal the crop area. 
	 * 
	 * @author Ira Cooke 
	 * 
	 * */
	public final class NonSequentialOperationConstraint extends ConstraintBuilder{
		public NonSequentialOperationConstraint(){
			super(ConstraintBuilder.CBType.NONSEQOP,ModelComponent.MCType.CROPPING);
		}
		protected void build(){
			int row=matrix.numRows();
			for ( CropType ct:crops.keySet()){ // Loop Crops
				for(CropCopy cp:crops.get(ct).getYearCopies()){
					List<Operation> cropOps=cp.operations();
					int nops = cropOps.size();
					for ( int o=1;o<nops;o++){ // Loop operations 1 to max
						Operation op = cropOps.get(o);
						MatrixRow rowpointer =new MatrixRow(0,0,LPX.LPX_FX,
							row,constraintTag,type().tag+"_"+ct+"_"+op.type);
						matrix.addRow(rowpointer);						
						rowpointer.addElement(new MatrixElement(cp.getDependentColumn(0),-1)); // Make a constraint for each crop op combo
						row++;
						for ( Integer p:op.unfoldedAllowedSet()){
							rowpointer.addElement(new MatrixElement(op.getDependentColumn(p),1));
						}
					}
				}
			}
		}
	}
	/** Constrain the operational areas to conform to sequencing requirements. The sum of operation areas in a given period cannot exceed 
	 * the sum of areas of the preceeding operation up to that period (or up to an earlier period if a gap is enforced) */	
	public final class OperationSequencingConstraint extends ConstraintBuilder {

		public OperationSequencingConstraint(){
			super(ConstraintBuilder.CBType.OPSEQ,ModelComponent.MCType.CROPPING);
		}
		protected void build(){
			int row=matrix.numRows();
			for(CropType ct:crops.keySet())  { // Loop Crops
				List<CropCopy> clist=crops.get(ct).getYearCopies();			
				for(int i=0;i<clist.size();i++){ // Loop crop copies
					CropCopy cp = clist.get(i);	
					List<Operation> cpOps=cp.operations();
					int nops = cpOps.size();
					for ( int o=1;o<nops;o++){ // Loop operations 1 to max
						Operation op = cpOps.get(o);
						if ( op.isSequential()){
							Operation prevop=cpOps.get(o-1);
						/* Because we are considering operations within a
						 single crop growth cycle we use the unfolded periods */
							Set<Integer> opallow=(Set<Integer>)op.unfoldedAllowedSet(); 
							Set<Integer> prevopallow=(Set<Integer>)prevop.unfoldedAllowedSet();
							Set<Integer> peitherallow=new HashSet<Integer>(opallow);						
							Set<Integer> pbothallow = new HashSet<Integer>(opallow);
							peitherallow.addAll(prevopallow);
							
							pbothallow.retainAll(prevopallow);
							/* For the case where a gap spans the allowable operations we need to 
							 * reconstruct the set of periods to include those in the gap */
							Set<Integer> pbothallowPlusGap = new HashSet<Integer>(pbothallow);
							int popmin=Collections.min(opallow);
							for ( int g=1;g<=prevop.gap;g++){
								int pgap=popmin-g;
								if ( prevopallow.contains(pgap)){
									pbothallowPlusGap.add(pgap);
								}
							}
							// Make a check to see if the gap is feasible
							if ( Collections.min(prevopallow)+prevop.gap > Collections.max(opallow)){
								throw new Error("The sequence of operations "+prevop.type+" followed by "+
										op.type+" in crop "+cp.name()+" has a sequencing infeasibility \n ---> check dates and mingap requirements \n");
							}
							
							if ( pbothallowPlusGap.size()>0){ // The intersection is not null
								for ( Integer pboth:pbothallowPlusGap){
									MatrixRow rowpointer =new MatrixRow(0,0,LPX.LPX_LO,
											row,constraintTag,type().tag+"_"+ct+"_"+op.type+"_p"+pboth);
									matrix.addRow(rowpointer);		
									row++;
									for ( int p=Collections.min(peitherallow);p<=pboth;p++){
										if ( opallow.contains(p)){
											rowpointer.addElement(new MatrixElement(op.getDependentColumn(p),-1)); // Make a constraint for each op,period,crop combo
										}
									//	if ( prevop.gap > 0 ){
									//		System.out.println(pbothallowPlusGap);
									//		throw new Error("Stop "+prevop.gap);
									//	}
										if ( prevopallow.contains(p) && (p+prevop.gap)<=pboth){ 
											rowpointer.addElement(new MatrixElement(prevop.getDependentColumn(p),1));
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	/** If specified, the area of a particular operation is constrained to be at least 
	 * a certain minimum percentage of the total crop area */
	public final class OperationMinAreaConstraint extends ConstraintBuilder {
		public OperationMinAreaConstraint(){
			super(ConstraintBuilder.CBType.OPMINAREA,ModelComponent.MCType.CROPPING);
		}
		protected void build(){
			int row=matrix.numRows();
			for(CropType ct:crops.keySet())  { // Loop Crops
				List<CropCopy> clist=crops.get(ct).getYearCopies();			
				for(int i=0;i<clist.size();i++){ // Loop crop copies
					CropCopy cp = clist.get(i);	
					List<Operation> cpOps=cp.operations();
					int nops = cpOps.size();
					for ( int o=0;o<nops;o++){ // Loop operations 1 to max
						Operation op = cpOps.get(o);
						if ( op.hasMinAreas()){
						
							Set<Integer> pallow=op.unfoldedAllowedSet();
							for ( Integer p:pallow){
								if (op.minArea(p)>0){
									MatrixRow rowpointer =new MatrixRow(0,0,LPX.LPX_LO,
											row,constraintTag,type().tag+"_"+ct+"_"+op.type+"_p"+p);
									matrix.addRow(rowpointer);		
									row++;
									rowpointer.addElement(new MatrixElement(op.getDependentColumn(p),1));
									rowpointer.addElement(new MatrixElement(cp.getDependentColumn(0),-op.minArea(p)));
								}
							}
						}
					}
				}
			}		
		}
	}
	
	/** Constrain the area of crops with a disease to be no greater than  */
	public final class DiseaseConstraint extends ConstraintBuilder{
		public DiseaseConstraint(){
			super(ConstraintBuilder.CBType.DISEASE,ModelComponent.MCType.CROPPING);
		}
		protected void build(){
			int row=matrix.numRows();
			for ( DiseaseType dt:diseases.keySet()){
				Disease dis=diseases.get(dt);
				Disease assocdis=diseases.get(dis.associated);
				MatrixRow rowpointer =new MatrixRow(0,0,LPX.LPX_LO,
					row,constraintTag,"disease_"+dt);
				matrix.addRow(rowpointer);		
				row++;
				Set<CropType> withdisease=cropsWithDisease(dt);
				Set<CropType> withassociated=cropsWithDisease(dis.associated);
				for(CropType croptype:crops.keySet()){
					CropCopy cp=crops.get(croptype).getCopy(0);
					if ( !withdisease.contains(croptype) && !withassociated.contains(croptype)){
						rowpointer.addElement(new MatrixElement(cp.getDependentColumn(0),1));
					} else {
						if ( withdisease.contains(croptype) && !withassociated.contains(croptype)){
							rowpointer.addElement(new MatrixElement(cp.getDependentColumn(0),-dis.minBreak));
						} else  if ( withassociated.contains(croptype) && !withdisease.contains(croptype)){
							rowpointer.addElement(new MatrixElement(cp.getDependentColumn(0),-assocdis.minBreak));
						} else {
							rowpointer.addElement(new MatrixElement(cp.getDependentColumn(0),-Math.min(assocdis.minBreak,dis.minBreak)));
						}					
					}
				}
			}
		}
	}
	/** Constrain the areas of specific crops. 
	 * @author Ira Cooke 
	 * 
	 * */
	public final class AreaLimitConstraint extends  ConstraintBuilder {

		public AreaLimitConstraint(){
			super(ConstraintBuilder.CBType.AREALIMIT,ModelComponent.MCType.CROPPING);
		}
		protected void build(){
			for(Limit lim:limits){
				switch(lim.limitType){
				case AREA:
					buildLimitAreaConstraint(lim);
					break;
				default:
					throw new Error("No constraint defined for limit type"+lim.limitType);
				}
			}	
		}
	}
	
	public String name(){return "cropping";};
	// ---- Reporting --- //
	public String toString(){
		StringBuffer outstring = new StringBuffer();
		outstring.append("--- Cropping --- \n");
		outstring.append("Total Area ");
		outstring.append("\n");
		for(Crop cp:crops.values()){
			outstring.append(cp);			
		}
		
		outstring.append("Diseases \n");
		for ( Disease ds:diseases.values()){
			outstring.append(ds);
		}/*
		outstring.append("Rotations \n");
		for ( Rotation rt:rotations.values()){
			outstring.append(rt);
		}*/
		outstring.append("Limits \n");
		for ( Limit lt:limits){
			outstring.append(lt);
		}
		return outstring.toString();
	}
	
	/** Pretty print the solved areas of each of the crops. 
	 * Optionally print the detailed breakdown of operations for each crop in 
	 * each period 
	 * 
	 * @param model The parent model holding this cropping object 
	 * @param detailed Flag whether or not to print detailed operations data */
	public String printSolution(Farm model,boolean detailed){
		StringBuffer outstring = new StringBuffer();
		DecimalFormat f1 = new DecimalFormat("#0.00");
		outstring.append("--- Cropping --- \n");
		outstring.append("Total Area ");
		outstring.append("\n");

		for ( CropYear cy:cropYearsSet){
			CropCopy cp = crops.get(cy.base).getCopy(cy.copyYear);
			if ( cp.solvedArea()>0){
				outstring.append(cp.name()+": "+f1.format(cp.solvedArea())+"\n");	
			}
		}
		if ( detailed ){
			outstring.append("\n");
			for ( CropYear cy:cropYearsSet){
				CropCopy cp = crops.get(cy.base).getCopy(cy.copyYear);
				if ( cp.solvedArea()>0){
					outstring.append(cp.printSolution(' '));			
				}
			}
		}
		return outstring.toString();
	}
	
	public String printCSVWorkPlan(){
		StringBuffer outstring = new StringBuffer();
		for ( CropYear cy:cropYearsSet){
			CropCopy cp = crops.get(cy.base).getCopy(cy.copyYear);
			outstring.append(cp.name()+"\n");
			if ( cp.solvedArea()>0){
				outstring.append(cp.printSolution(','));			
			}
		}
		return outstring.toString();
	}
	
}
