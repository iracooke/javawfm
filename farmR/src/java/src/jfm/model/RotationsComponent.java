package jfm.model;
import java.text.DecimalFormat;
import java.util.*;

import jfm.lp.*;
import jfm.model.Crop.CropCopy;
import jfm.model.Types.*;


/** Calculates rotational constraints and holds data on rotation areas. 
 * @author Ira Cooke  */
public final class RotationsComponent extends ModelComponent {
	private static String constraintTag="Rotations";
//	private final Cropping cropping;
//	private final Location location;
	private final int numPeriods;
	private final Map<CropYear,HashMap<CropYear,RotationPenalty>> rotationPenalties =new HashMap<CropYear,HashMap<CropYear,RotationPenalty>>();
	
	public String name(){return "rotations";};
	public RotationsComponent(int numPeriods_,CroppingComponent cropping,Location loc){
		super(ModelComponent.MCType.ROTATIONS);
		requireObjective(ObjectiveType.PROFIT);
		numPeriods=numPeriods_;
/*
		for ( CropYear toyr:cropping.cropYears()){
			CropCopy toCrop=cropping.getCrop(toyr.base).getCopy(toyr.copyYear);
			HashMap<CropYear,RotationPenalty> frommap=new HashMap<CropYear,RotationPenalty>();
			for ( CropYear fromyr:cropping.cropYears()){
				CropCopy fromCrop=cropping.getCrop(fromyr.base).getCopy(fromyr.copyYear);
				double pen=0;
				double[] costandyield= cropping.getRotationPenalty(toCrop.diseaseType(),fromCrop.diseaseType());
//				pen+=costandyield[0]*toCrop.primaryPrice()*toCrop.primaryYield();
				pen+=toCrop.costOfYieldPenalty(costandyield[0],loc);
				pen+=costandyield[1];
				RotationPenalty penaltyp = new RotationPenalty(pen);
				frommap.put(fromyr, penaltyp);
			}
			rotationPenalties.put(toyr,frommap);
		}*/

		/** Construct the basic penalty matrix but with zero values to be filled in later */
		for ( CropYear toyr:cropping.cropYears()){
			HashMap<CropYear,RotationPenalty> frommap=new HashMap<CropYear,RotationPenalty>();
			for ( CropYear fromyr:cropping.cropYears()){
				double pen=0;
				RotationPenalty penaltyp = new RotationPenalty(pen);
				frommap.put(fromyr, penaltyp);
			}
			rotationPenalties.put(toyr,frommap);
		}

		addConstraintBuilder(new LastOpRotationConstraint());
		addConstraintBuilder(new SeqFirstOpRotationConstraint());
		addConstraintBuilder(new NonSeqFirstOpRotationConstraint());
		
	}
	public RotationsComponent copy(){
		throw new Error("Bare copy() called on rotations. Should use copy(Cropping,Location) instead");
	}
	RotationsComponent copy(CroppingComponent newcropping,Location loc){
		return new RotationsComponent(numPeriods,newcropping,loc);
	}
	

	// METHODS ASSOCIATED WITH STRUCTURAL VARIABLES

	protected void initializeStructure() throws BadModelException {
		CroppingComponent cropping=getParent().cropping;

		setRotationPenalties(getParent().location());
		Set<CropYear> cropYearsSet=cropping.cropYears();
		for(CropYear toCrop:cropYearsSet){
			boolean canRotateTo=false;
			for(CropYear fromCrop:cropYearsSet){
				if ( isAllowed(toCrop,fromCrop,cropping)){										
					canRotateTo=true;
					Operation handoverfromop=cropping.getCrop(fromCrop.base).getCopy(fromCrop.copyYear).handOverOperation();
					RotationPenalty penaltyp=rotationPenalties.get(toCrop).get(fromCrop);
					penaltyp.registerParent(this);			
					for(Integer phandover:handoverfromop.unfoldedAllowedSet()){
						MatrixVariable newVariable=new MatrixVariable(-penaltyp.penalty(),
								0.0,0.0,
								LPX.LPX_LO,LPX.LPX_CV,matrix.numCols(),ObjectiveType.PROFIT);
						newVariable.setTag(fromCrop.base+"_"+fromCrop.copyYear+"_to_"+toCrop.base+"_"+toCrop.copyYear+"_"+phandover);
						matrix.addVariable(newVariable);	
						penaltyp.registerVariable(newVariable,phandover);
					}
				}
			}
			if ( !canRotateTo){
				throw new BadModelException("The model is badly specified. It is impossible to rotate to crop "+toCrop.base);
			}
		}		
	}
	public void setFormulaVariables(){}

	// \TODO check this!!!!!
	private void setRotationPenalties(Location loc){
		CroppingComponent cropping = getParent().cropping;		
		Set<CropYear> cropYearsSet=cropping.cropYears();
		for(CropYear toCrop:cropYearsSet){
			for(CropYear fromCrop:cropYearsSet){
				if ( isAllowed(toCrop,fromCrop,cropping)){					
					Types.CropType toD=cropping.getCrop(toCrop.base).type;
					Types.DiseaseType fD=cropping.getCrop(fromCrop.base).diseaseType;
					CropCopy ccp = cropping.getCrop(toCrop.base).getCopy(toCrop.copyYear);
			//		System.out.println("Setting penalty "+toD+" "+fD+" "+cropping.getRotationPenalty(toD, fD)[0]);
					rotationPenalties.get(toCrop).get(fromCrop).setPenalty(cropping.getRotationPenalty(toD, fD),ccp,loc);
				}
			}
		}
	}
	
	protected void updateStructure(){
		for(Map<CropYear,RotationPenalty> rpto:rotationPenalties.values()){
			for ( RotationPenalty rp:rpto.values()){
				rp.updateStructure(this);
			}
		}
		structureUpdateDone();
	}
	

	/** Returns true if the specified from and to crop combination is permitted. 
	 * @param to Crop being rotated to. 
	 * @param from Crop being rotated from. 
	 * @param cropping The cropping object containing to and from. 
	 * */
	private boolean isAllowed(CropYear to,CropYear from,CroppingComponent cropping){
		CropCopy tocrop=cropping.getCrop(to.base).getCopy(to.copyYear);
		CropCopy fromcrop=cropping.getCrop(from.base).getCopy(from.copyYear);
		Rotation toCpRot=cropping.getCrop(tocrop.type()).rotation();
		// First just check for absolute forbidden rotations specific to this crop 
		if ( toCpRot.isForbidden(fromcrop.diseaseType())){
			return false;
		}
		
		if ( tocrop.diseaseType() == DiseaseType.NONE){ // Always allow rotation to crops with NONE as disease type
			return true; // This is a special rule to allow setaside or non-rotational crops
		}
		if ( to.copyYear == 0 && (fromcrop.diseaseType() != tocrop.diseaseType()) ){
			return true; // If starting a new crop then we cannot do so from the same disease type
		}
		if ( fromcrop.diseaseType()==tocrop.diseaseType()){
			if ( from.copyYear+1 == to.copyYear){
				return true;
			}
			Crop cp = cropping.getCrop(to.base);
			if ( from.copyYear == to.copyYear && cp.continuousCroppingAllowed 
					&& to.copyYear==cp.getYearCopies().size()-1 ){
				return true;
			}
		}
		
		return false;
	}
	/** Constrain the area of the last operation of each crop to equal the amount rotating from that crop to all other crops */
	public final class LastOpRotationConstraint extends ConstraintBuilder {
		public LastOpRotationConstraint(){
			super(ConstraintBuilder.CBType.LASTOPROT,ModelComponent.MCType.ROTATIONS);
		}
		protected void build(){
		int row=matrix.numRows();
		CroppingComponent cropping=getParent().cropping;
		Set<CropYear> cropYearsSet=cropping.cropYears();
		for(CropYear fromyr:cropYearsSet){
			CropCopy fromcrop=cropping.getCrop(fromyr.base).getCopy(fromyr.copyYear);
			Operation lastfromop = fromcrop.handOverOperation(); 
			MatrixRow rp=new MatrixRow(0,0,LPX.LPX_FX,
					row,constraintTag,type().tag);
			matrix.addRow(rp);		
			row++;
			for(Integer p:lastfromop.unfoldedAllowedSet()){
				rp.addElement(new MatrixElement(lastfromop.getDependentColumn(p),1));
				for ( CropYear toyr:cropYearsSet){
					if ( isAllowed(toyr,fromyr,cropping) ){			
						rp.addElement(new MatrixElement(rotationPenalties.get(toyr).get(fromyr).getDependentColumn(p),-1));
					}
				}
			}		
		}
	}
	}
	
	/** Irrespective of period constrain the area of the first operation of a crop to be no more than the total area rotating to that crop */
	public final class NonSeqFirstOpRotationConstraint extends ConstraintBuilder {			
		public NonSeqFirstOpRotationConstraint(){
			super(ConstraintBuilder.CBType.NONSEQFIRSTOPROT,ModelComponent.MCType.ROTATIONS);
		}
		protected void build(){
			int row=matrix.numRows();
			CroppingComponent cropping=getParent().cropping;
			Set<CropYear> cropYearsSet=cropping.cropYears();
			for(CropYear toyr:cropYearsSet){ 
				CropCopy tocrop=cropping.getCrop(toyr.base).getCopy(toyr.copyYear);
				Operation firstop = tocrop.operations().get(0);
				MatrixRow rp =new MatrixRow(0,0,LPX.LPX_LO, 
					row,constraintTag,type().tag);							
				matrix.addRow(rp);		// One constraint per crop 
				row++;	
				Set<Integer> unfoldedfirstopallow=firstop.unfoldedAllowedSet();
				for ( CropYear fromyr:cropYearsSet){ // Loop from crops including copies 
					if ( isAllowed(toyr,fromyr,cropping)){ // Exclude any disallowed to-from crop combinations 
						for ( Integer pstart:unfoldedfirstopallow){
							rp.addElement(new MatrixElement(firstop.getDependentColumn(pstart),-1)); // Area of starting operation 
							Operation lastop=cropping.getCrop(fromyr.base).getCopy(fromyr.copyYear).handOverOperation();
							for (Integer endp:lastop.unfoldedAllowedSet() ){	
							// Total area rotating to this crop 
								rp.addElement(new MatrixElement(rotationPenalties.get(toyr).get(fromyr).getDependentColumn(endp),1));
							}
						}
					}
				}
			}	
		}	
	}
	/** Constrain the area of the first operation of a crop up to a given period to be no more than the total area 
	 * rotating to that crop up to that period */
		public final class SeqFirstOpRotationConstraint extends ConstraintBuilder {
			public SeqFirstOpRotationConstraint(){
				super(ConstraintBuilder.CBType.SEQFIRSTOPROT,ModelComponent.MCType.ROTATIONS);
			}
		protected void build(){

		int row=matrix.numRows();
		CroppingComponent cropping=getParent().cropping;
		Set<CropYear> cropYearsSet=cropping.cropYears();
		MatrixRow rp=null;
		boolean newRow=false;
		for(CropYear toyr:cropYearsSet){ // Loop to crops including copies 
			CropCopy tocrop=cropping.getCrop(toyr.base).getCopy(toyr.copyYear);
			Operation firstop = tocrop.operations().get(0);
			Set<Integer> unfoldedfirstopallow=firstop.unfoldedAllowedSet();
			for(Integer pstart:unfoldedfirstopallow){ // Loop unfolded periods for the tocrop start operation
				newRow=true;				// Start a new constraint for each start period 
				for ( CropYear fromyr:cropYearsSet){ // Loop from crops including copies 
					if ( isAllowed(toyr,fromyr,cropping)){ // Exclude any disallowed to-from crop combinations 
						Operation lastop=cropping.getCrop(fromyr.base).getCopy(fromyr.copyYear).handOverOperation();
						Set<Integer> foldedendallow=lastop.foldedAllowedSet();
						if (foldedendallow.contains(pstart)){ // There is an overlap so we need a constraint 
							if (newRow){
								rp =new MatrixRow(0,0,LPX.LPX_LO,
										row,constraintTag,type().tag);							
								matrix.addRow(rp);		
								rp.addElement(new MatrixElement(firstop.getDependentColumn(pstart),-1)); 
								row++;					
								newRow=false;
							}
							for (Integer endp:lastop.unfoldedAllowedSet() ){	
								if ( Farm.wrapPeriod(endp, numPeriods) <= pstart ){
									rp.addElement(new MatrixElement(rotationPenalties.get(toyr).get(fromyr).getDependentColumn(endp),1));
								}
							}
						}
					}
				}
			}
		}
	}
		}
	

	public String toString(){
		StringBuffer outstring = new StringBuffer();
		outstring.append("--- Rotations ---");
//		outstring.append(printPenaltyMatrix());
		return outstring.toString();
	}
	
	public double[] getSolvedRotationPenaltyForCrop(CropCopy tocp,Location loc){
		CroppingComponent cropping=getParent().cropping;
		List<CropCopy> usedCrops = cropping.cropsUsed();
		
		CropYear toyr=new CropYear(tocp.type(),tocp.copyYear());
		double[] totalCost=new double[2];
		totalCost[0]=0;
		totalCost[1]=0;
		for ( CropCopy frcp:usedCrops){
			CropYear fromyr = new CropYear(frcp.type(),frcp.copyYear());
			if ( isAllowed(toyr,fromyr,cropping)){
				for(Integer p:cropping.getCrop(fromyr.base).getCopy(fromyr.copyYear).handOverOperation().unfoldedAllowedSet()){
					RotationPenalty rp = rotationPenalties.get(toyr).get(fromyr);
					double[] penalties=cropping.getRotationPenalty(tocp.type(), frcp.diseaseType());
					if ( rp.area(p) > 0 ){
						totalCost[0]+=rp.area(p)*tocp.costOfYieldPenalty(penalties[0],loc);
						totalCost[1]+=rp.area(p)*penalties[1];
					//	System.out.println(tocp.type()+" "+penalties[0]+" "+penalties[1]+"\n");
					}
				}
			}
		}
		return totalCost;
	}
	
	public RotationMatrix getSolvedRotationMatrix(){

		CroppingComponent cropping=getParent().cropping;
		List<CropCopy> usedCrops = cropping.cropsUsed();
		// Get a list of the crop types
		Set<CropType> cropTypes = new HashSet<CropType>();
		for ( CropCopy cp:usedCrops){
			cropTypes.add(cp.type());
		}
		RotationMatrix rmatrix=new RotationMatrix(cropTypes);

		for(CropCopy tocp:usedCrops){
			CropYear toyr=new CropYear(tocp.type(),tocp.copyYear());
			for ( CropCopy frcp:usedCrops){
				CropYear fromyr = new CropYear(frcp.type(),frcp.copyYear());
				double area=0;
				if ( isAllowed(toyr,fromyr,cropping)){
					for(Integer p:cropping.getCrop(fromyr.base).getCopy(fromyr.copyYear).handOverOperation().unfoldedAllowedSet()){
						area+=rotationPenalties.get(toyr).get(fromyr).area(p);
					}
				}
				rmatrix.addElement(fromyr.base, toyr.base, area);
			}
		}
		return rmatrix;
	}

	public String printSolution(){
		DecimalFormat d3 = new DecimalFormat("#000");
		CroppingComponent cropping=getParent().cropping;
		StringBuffer outstring = new StringBuffer();
		outstring.append("--- Rotation Matrix ---\n");
		List<CropCopy> usedCrops = cropping.cropsUsed();
		// Print from crops along the top 
		outstring.append("      ");
		for(CropCopy cp:usedCrops){
			outstring.append(cp.type().shortName+" ");
		}
		outstring.append("\n");
//		 Print from crops along the top 
		outstring.append("     ");
		for(CropCopy cp:usedCrops){
			outstring.append(" "+d3.format(cp.grossMargin())+" ");
		}
		outstring.append("\n");
		for(CropCopy tocp:usedCrops){
			outstring.append(tocp.type().shortName+" ");
			CropYear toyr=new CropYear(tocp.type(),tocp.copyYear());
			for ( CropCopy frcp:usedCrops){
				CropYear fromyr = new CropYear(frcp.type(),frcp.copyYear());
				double area=0;
				if ( rotationPenalties.get(toyr)!=null){
					if ( rotationPenalties.get(fromyr)!=null){
						for(Integer p:frcp.handOverOperation().unfoldedAllowedSet()){
							area+=rotationPenalties.get(toyr).get(fromyr).area(p);
						}
					}
				}
				/*
				if ( isAllowed(toyr,fromyr,cropping)){
					for(Integer p:cropping.getCrop(fromyr.base).getCopy(fromyr.copyYear).handOverOperation().unfoldedAllowedSet()){
						area+=rotationPenalties.get(toyr).get(fromyr).area(p);
					}
				}*/
				if ( area >=0){
					outstring.append(" ");
				}
				outstring.append(d3.format(area)+" ");				
			}
			outstring.append("\n");
		}
		return outstring.toString();
	}
	
}
