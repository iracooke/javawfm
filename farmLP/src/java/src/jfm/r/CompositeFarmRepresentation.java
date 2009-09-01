/**
 * 
 */
package jfm.r;

import jfm.model.Crop;
import jfm.model.Types;
import jfm.model.Location;
import jfm.model.Types.CropType;
import jfm.mou.FarmerMOU;
import java.util.*;
import jfm.utils.JFMMath;
import jfm.utils.MathPrint;
import jfm.xml.XMLSyntaxException;
import jfm.lp.LPX;
import jfm.model.GLPKException;
import jfm.model.BadModelException;
/**
 * @author iracooke
 *
 */
public class CompositeFarmRepresentation extends FarmRepresentation {
	private final double solutionThreshold;
	private int isSolved=0;
	
	private ArrayList<SimpleFarmRepresentation> farmObjects = new ArrayList<SimpleFarmRepresentation>();
	private ArrayList<Double> weights=new ArrayList<Double>();
	private ArrayList<Double> soils=new ArrayList<Double>();
	private ArrayList<Double> rainfall=new ArrayList<Double>();
	
	/**
	 * 
	 */
	public CompositeFarmRepresentation() {
		solutionThreshold=0.5;
	}
	
	public CompositeFarmRepresentation(double st){
		solutionThreshold=st;
	}
	
	public void fixAreaOfCrop(double areaLimit,String xmlCropName){
		for(SimpleFarmRepresentation farmObject:farmObjects){
			farmObject.fixAreaOfCrop(areaLimit, xmlCropName);
		}
	}
	
	public void lowerLimitAreaOfCrop(double areaLimit,String xmlCropName){
		for(SimpleFarmRepresentation farmObject:farmObjects){
			farmObject.lowerLimitAreaOfCrop(areaLimit, xmlCropName);
		}
	}
	
	public void boundLimitAreaOfCrop(double areaMin,double areaMax,String xmlCropName){
		for(SimpleFarmRepresentation farmObject:farmObjects){
			farmObject.boundLimitAreaOfCrop(areaMin, areaMax, xmlCropName);
		}
	}
	
	public void setInputCost(String xmlInputName,double unitCost) {
		for(SimpleFarmRepresentation farmObject:farmObjects){
			farmObject.setInputCost(xmlInputName, unitCost);
		}
		
	}
	
	
	public void reset(){
		for (SimpleFarmRepresentation farmObject:farmObjects){
			farmObject.reset();
		}
	}
	
	
	public void setYieldForCrop(String xmlCropName,double yield){
		for(SimpleFarmRepresentation farmObject:farmObjects){
			farmObject.setYieldForCrop(xmlCropName, yield);
		}
	}
	
	public void setPriceForCrop(String xmlCropName,double price){
		for(SimpleFarmRepresentation farmObject:farmObjects){
			farmObject.setPriceForCrop(xmlCropName, price);
		}
	}
	
	public void setSubsidyForCrop(String xmlCropName,double subsidy){
		for ( SimpleFarmRepresentation farmObject:farmObjects){
			farmObject.setSubsidyForCrop(xmlCropName, subsidy);
		}
	}
	

	
	
	public void addFarm(SimpleFarmRepresentation farm,double soil,double rainfall_,double weight){
		farm.setSoilTypeAndRainfall(soil,rainfall_);
		
		farmObjects.add(farm);
		weights.add(weight);
		soils.add(soil);
		rainfall.add(rainfall_);
	}

	/* (non-Javadoc)
	 * @see jfm.r.FarmRepresentation#applyMOU(jfm.mou.FarmerMOU)
	 */
	@Override
	public void applyMOU(FarmerMOU mou) {
		// TODO Auto-generated method stub
		for(SimpleFarmRepresentation f: farmObjects){
			f.applyMOU(mou);
		}
	}

	/* (non-Javadoc)
	 * @see jfm.r.FarmRepresentation#createAndApplyMOU(java.lang.String)
	 */
	@Override
	public void createAndApplyMOU(String mouName) {
		FarmerMOU mou= FarmerMOU.fromXML(mouName);
		this.applyMOU(mou);
	}

	/* (non-Javadoc)
	 * @see jfm.r.FarmRepresentation#isSolved()
	 */
	@Override
	public int isSolved() {
		return isSolved;
	}

	/* (non-Javadoc)
	 * @see jfm.r.FarmRepresentation#profit()
	 */
	@Override
	public double profit() {
		double weightedProfit=0;
		// Normalize weights first
		for(int i=0;i<farmObjects.size();i++){
			weightedProfit+=weights.get(i)*farmObjects.get(i).profit();
		}
		return weightedProfit;
	}
	
	public double eo(){
		double weightedeo=0;
		// Normalize weights first
		for(int i=0;i<farmObjects.size();i++){
			weightedeo+=weights.get(i)*farmObjects.get(i).eo();
		}
		return weightedeo;
		
	}
	
	/** Just returns list of first element assumes all the same */
	public String[] cropNames(){
		return farmObjects.get(0).cropNames();
	}
	
	/** Just returns list of first element assumes all the same */
	public String[] objectiveNames(){
		return farmObjects.get(0).objectiveNames();
	}
	
	public double[] objectiveValues(){
		double[] weightedvalues=new double[objectiveNames().length];
		JFMMath.doubleZero(weightedvalues);
		for( int i=0;i<farmObjects.size();i++){
			double[] vals = farmObjects.get(i).objectiveValues();
			for( int j=0;j<vals.length;i++){
				weightedvalues[j]+=vals[j]*weights.get(i);
			}
		}
		return weightedvalues;		
	}
	
	public double[] objectiveScaleFactors(){
		double[] weightedvalues=new double[objectiveNames().length];
		JFMMath.doubleZero(weightedvalues);
		for( int i=0;i<farmObjects.size();i++){
			double[] vals = farmObjects.get(i).objectiveScaleFactors();
			for( int j=0;j<vals.length;i++){
				weightedvalues[j]+=vals[j]*weights.get(i);
			}
		}
		return weightedvalues;	
	}
	
	public double areaOfCropNamed(String name){
		double weightedArea=0;
		for( int i=0;i<farmObjects.size();i++){
			weightedArea+=weights.get(i)*farmObjects.get(i).areaOfCropNamed(name);
		}
		return weightedArea;		
	}
	
	public double priceOfCropNamed(String name){
		double weightedPrice=0;
		for( int i=0;i<farmObjects.size();i++){
			weightedPrice+=weights.get(i)*farmObjects.get(i).priceOfCropNamed(name);
		}
		return weightedPrice;		
	}
	
	public double yieldOfCropNamed(String name){
		double weightedYield=0;
		for( int i=0;i<farmObjects.size();i++){
			weightedYield+=weights.get(i)*farmObjects.get(i).yieldOfCropNamed(name);
		}
		return weightedYield;		
	}
	
	public double areaOfWinterStubble(){
		double weightedArea=0;
		for( int i=0;i<farmObjects.size();i++){
			weightedArea+=weights.get(i)*farmObjects.get(i).areaOfWinterStubble();
		}
		return weightedArea;		
	}

	/* (non-Javadoc)
	 * @see jfm.r.FarmRepresentation#setDistanceFromSugarbeetFactory(double, double)
	 */
	@Override
	public void setDistanceFromSugarbeetFactory(double distance,
			double costPerKmPerHa) {
		for ( SimpleFarmRepresentation f:farmObjects){
			f.setDistanceFromSugarbeetFactory(distance, costPerKmPerHa);
		}
	}

	/* (non-Javadoc)
	 * @see jfm.r.FarmRepresentation#solutionDetails()
	 */
	@Override
	public void solutionDetails() {
		// 
	}

	/* (non-Javadoc)
	 * @see jfm.r.FarmRepresentation#solutionSummary()
	 */
	@Override
	public String solutionSummary() {
		StringBuffer buff=new StringBuffer();
		String[] cnames=this.cropNames();
		for(int i=0;i<cnames.length;i++){
			buff.append(MathPrint.f2.format(this.areaOfCropNamed(cnames[i]))+" ");
		}
		buff.append("\n");
		return buff.toString();
	}

	/* (non-Javadoc)
	 * @see jfm.r.FarmRepresentation#solve()
	 */
	@Override
	public int solve(String failDump) {
		isSolved=0;
		JFMMath.normalize(weights);
		LPX[] status=new LPX[weights.size()];
		int i=0;
		// First try to solve everything
		try {
			for(SimpleFarmRepresentation f:farmObjects){			
				status[i]=f.farmObject.solve(false,failDump); 
				i++;
			}
		} catch (GLPKException ex){
			throw new Error("GLPKException "+ex.getMessage());
		} catch (BadModelException ex){
			throw new Error("BadModelException "+ex.getMessage());
		}
		
		double wttotal=0;
		double wtmissing=0;
		for ( i=0;i<status.length;i++){			
			wttotal+=weights.get(i);
			if ( status[i]!=LPX.LPX_OPT) {
				wtmissing+=weights.get(i);
				weights.set(i, 0.0); //Set the weight for this farm to zero  
			}
		}
		
		if ( wtmissing/wttotal > solutionThreshold){
			throw new Error("Weighted proportion "+wtmissing/wttotal+" of a composite farm failed to solve");
		} else {
			JFMMath.normalize(weights);			
		}
		isSolved=1;
		return LPX.LPX_OPT.toCPP();
	}

}
