/** R API classes to allow the Java Farm Model to be called from R
 * 
 */
package jfm.r;
import jfm.lp.LPX;
import jfm.model.*;
import jfm.model.Types.CropType;
import jfm.model.Types.VariableType;
import jfm.model.Types.ObjectiveType;
import jfm.mou.*;
import org.w3c.dom.Document;
import jfm.xml.XMLSyntaxException;
/** Wrapper class for a Farm model object that can be easily instantiated and manipulated from R
 * @author iracooke
 * 
 */
public class SimpleFarmRepresentation extends FarmRepresentation {
	protected final Farm farmObject;
	
	
	public SimpleFarmRepresentation(String xmlFileName){
		farmObject=Farm.fromXML(xmlFileName);
	}
	
	public SimpleFarmRepresentation(Document doc){
		farmObject=Farm.fromXML(doc);
	}
	
	public void setYieldForCrop(String xmlCropName,double yield){
		try {
			Crop cp = farmObject.cropping.getCrop(Types.xmlToCropType(xmlCropName));
			cp.resetPrimaryYield(String.valueOf(yield));
		} catch (XMLSyntaxException ex) {
			throw new Error (ex.getMessage());
		}
	}
	
	public void setPriceForCrop(String xmlCropName,double price){
		try {
			Crop cp = farmObject.cropping.getCrop(Types.xmlToCropType(xmlCropName));
			cp.resetPrimaryPrice(price);
		} catch (XMLSyntaxException ex) {
			throw new Error (ex.getMessage());
		}
	}
	
	public void setSubsidyForCrop(String xmlCropName,double subsidy){
		try {
			Crop cp = farmObject.cropping.getCrop(Types.xmlToCropType(xmlCropName));
			cp.resetSubsidy(subsidy);
		} catch (XMLSyntaxException ex) {
			throw new Error (ex.getMessage());
		}
	}
	

	
	public void setInputCost(String xmlInputName,double unitCost) {
		try {
			VariableType vt = Types.xmlToVariableType(xmlInputName);
			for ( CropType ct : farmObject.cropping.baseCropTypes()){
				Crop cp = farmObject.cropping.getCrop(ct);
				cp.resetInputCost(vt, unitCost);				
			}
		} catch (XMLSyntaxException ex) {
			throw new Error (ex.getMessage());
		}
		
	}
	
	public void setFarmArea(double area){
		farmObject.landUse.setArea(area);
	}	
	

	public void applyMOU(FarmerMOU mou){
		FarmerMOU.applyToFarm(mou,farmObject);
	}
	
	public void createAndApplyMOU(String mouName){
		FarmerMOU mou = FarmerMOU.fromXML(mouName);
		applyMOU(mou);
	}
	
	public void createAndApplyMOU(Document doc){
		FarmerMOU mou = FarmerMOU.fromXML(doc);
		applyMOU(mou);
	}
	
	public String solutionSummary(){
		return Output.solution(farmObject, false);		
	}
	public void solutionDetails(){
		System.out.println(Output.solution(farmObject, true));
	}

	public double profit(){
		return farmObject.getValueForObjective(ObjectiveType.PROFIT);
	}
	
	public double eo(){
		return farmObject.getEnterpriseOutput();
	}
	
	public void setSoilTypeAndRainfall(double st,double rf){
		farmObject.setLocation(new Location(st,rf,farmObject.numPeriods));
	}
	
	public void fixAreaOfCrop(double areaLimit,String xmlCropName){
		try {
			CropType cptype = Types.xmlToCropType(xmlCropName);
			Limit cpLimit = Limit.CropAreaLimit(cptype,LPX.LPX_FX,areaLimit,areaLimit);
			farmObject.cropping.addLimit(cpLimit);
		} catch (XMLSyntaxException ex){
			throw new Error(ex.getMessage());
		}
	}
	
	public void lowerLimitAreaOfCrop(double areaLimit,String xmlCropName){
		try {
			CropType cptype = Types.xmlToCropType(xmlCropName);
			Limit cpLimit = Limit.CropAreaLimit(cptype,LPX.LPX_LO,areaLimit,areaLimit);
			farmObject.cropping.addLimit(cpLimit);
		} catch (XMLSyntaxException ex){
			throw new Error(ex.getMessage());
		}
	}
	
	public void boundLimitAreaOfCrop(double areaMin,double areaMax,String xmlCropName){
		try {
			CropType cptype = Types.xmlToCropType(xmlCropName);
			Limit cpLimit = Limit.CropAreaLimit(cptype,LPX.LPX_DB,areaMin,areaMax);
			farmObject.cropping.addLimit(cpLimit);
		} catch (XMLSyntaxException ex){
			throw new Error(ex.getMessage());
		}
	}
	
	public void reset(){
		farmObject.cropping.clearLimits();
	}
	
	public String[] cropNames(){
		String[] names = new String[farmObject.cropping.baseCropTypes().size()];
		int i=0;
		for(CropType ct: farmObject.cropping.baseCropTypes()){
			names[i]=ct.xmlname;
			i++;	
		}
		return names;
	}
	
	public String[] objectiveNames(){
		String[] names = new String[farmObject.objectives().keySet().size()];
		int i=0;
		for(ObjectiveType ot:farmObject.sortedObjectiveTypes()){
			names[i]=ot.xmlname;
			i++;
		}
		return names;
	}
	
	public double[] objectiveValues(){
		double[] values=new double[farmObject.objectives().keySet().size()];
		int i=0;
		for(ObjectiveType ot:farmObject.sortedObjectiveTypes()){
			values[i]=farmObject.getValueForObjective(ot);
			i++;
		}
		return values;
	}
	
	public double[] objectiveScaleFactors(){
		double[] values=new double[farmObject.objectives().keySet().size()];
		int i=0;
		for(ObjectiveType ot:farmObject.sortedObjectiveTypes()){
			values[i]=farmObject.objectives().get(ot).scaleFactor();
			i++;
		}
		return values;
	}
	
	public double areaOfCropNamed(String name){
		CropType ct=null;
		try {
		ct = Types.xmlToCropType(name);
		} catch (XMLSyntaxException ex){
			throw new Error(ex.getMessage());
		}
		
		if ( farmObject.cropping.baseCropTypes().contains(ct)){
			return farmObject.cropping.getCrop(ct).getSolvedArea();			
		} else {
			return 0;
		}
	}
	
	public double priceOfCropNamed(String name){
		CropType ct=null;
		try {
		ct = Types.xmlToCropType(name);
		} catch (XMLSyntaxException ex){
			throw new Error(ex.getMessage());
		}
		
		if ( farmObject.cropping.baseCropTypes().contains(ct)){
			return farmObject.cropping.getCrop(ct).getPrimaryPrice();			
		} else {
			return 0;
		}
	}
	
	public double yieldOfCropNamed(String name){
		CropType ct=null;
		try {
		ct = Types.xmlToCropType(name);
		} catch (XMLSyntaxException ex){
			throw new Error(ex.getMessage());
		}
		
		if ( farmObject.cropping.baseCropTypes().contains(ct)){
			return farmObject.cropping.getCrop(ct).getPrimaryYield();			
		} else {
			return 0;
		}
	}
	
	public double areaOfWinterStubble() {		
		return farmObject.getValueForObjective(ObjectiveType.WINTERSTUBBLE);
	}
	
	public int isSolved(){
		if ( farmObject.solutionStatus() == jfm.lp.LPX.LPX_OPT){
			return 1;
		} else {
			return 0;
		}
	}
	
	public void setDistanceFromSugarbeetFactory(double distance,double costPerKmPerHa){
		
		if ( farmObject.cropping.baseCropTypes().contains(CropType.SUGARBEET)){
			Crop cp = farmObject.cropping.getCrop(CropType.SUGARBEET);
			double[] amounts={distance};
			
			CropInput inp=new CropInput(amounts,costPerKmPerHa,VariableType.TRANSPORT);
			cp.resetInput(VariableType.TRANSPORT, inp);		
		}
	}
	
	/** A wrapper for solve that can be easily called from rJava */
	public int solve(){
		try {
			LPX status = farmObject.solve(true);
		//	System.out.println(Output.solution(this, false));
			return status.toCPP();
		} catch (Exception ex){
			throw new Error(ex.getCause()+" "+ex.getMessage());
		}
	}
	
}
