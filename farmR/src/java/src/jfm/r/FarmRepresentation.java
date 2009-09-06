/**
 * 
 */
package jfm.r;
import jfm.model.Crop;
import jfm.model.Types;
import jfm.mou.FarmerMOU;
import jfm.xml.XMLSyntaxException;

/**
 * @author iracooke
 *
 */
public abstract class FarmRepresentation {
	
	public abstract void applyMOU(FarmerMOU mou);
	
	public abstract void createAndApplyMOU(String mouName);

	public abstract String solutionSummary();

	public abstract void solutionDetails();

	public abstract String[] cropNames();
	
	public abstract String[] objectiveNames();
	
	public abstract double[] objectiveValues();
	
	public abstract double[] objectiveScaleFactors();
	
//	public abstract double[] utilityContributions();

	public abstract double profit();
	
	public abstract double eo();
	
	public abstract int isSolved();
	
	public abstract String solver();
	
	public abstract void setDistanceFromSugarbeetFactory(double distance,double costPerKmPerHa);
	
	/** A wrapper for solve that can be easily called from rJava */
	public abstract int solve(String failDump);

	public abstract double areaOfCropNamed(String name);
	public abstract double priceOfCropNamed(String name);
	public abstract double yieldOfCropNamed(String name);
	
	public abstract double areaOfWinterStubble();
	
	public abstract void fixAreaOfCrop(double areaLimit,String xmlCropName);
	
	public abstract void reset();
	
	public abstract void lowerLimitAreaOfCrop(double areaLimit,String xmlCropName);
	public abstract void boundLimitAreaOfCrop(double areaMin,double areaMax,String xmlCropName);
	
	
	public abstract void setYieldForCrop(String xmlCropName,double yield);
	
	public abstract void setPriceForCrop(String xmlCropName,double price);
	
	public abstract void setSubsidyForCrop(String xmlCropName,double subsidy);
	
	public abstract void setInputCost(String xmlInputName,double unitCost);
	
	public abstract String toString();
	
}
