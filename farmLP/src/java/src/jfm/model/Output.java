package jfm.model;
import java.util.*;

import jfm.model.Crop.CropCopy;
import jfm.model.Types.*;
import jfm.utils.*;
import jfm.lp.*;
/** Static functions to extract solution information from model objects. 
 * @author Ira Cooke  */
public final class Output {
	public static String oneLineSummary(Farm farm){
		StringBuffer buff=new StringBuffer();
		Map<Double,CropType> cropAreas=new HashMap<Double,CropType>();
		for(CropType ct:farm.cropping.baseCropTypes()){
			cropAreas.put(farm.cropping.getCrop(ct).getSolvedArea(),ct);
		}
		buff.append("Total: "+MathPrint.f2.format(farm.landUse.getArea())+" ");
		
		List<Double> areas = new ArrayList<Double>(cropAreas.keySet());
		Collections.sort(areas);
		int numDisplay=Math.min(areas.size(),6);
		for(int i=0;i<numDisplay;i++){
			Double a=areas.get(areas.size()-1-i);
			buff.append(cropAreas.get(a).shortName+": "+MathPrint.f2.format(a)+" ");
		}
		return buff.toString();
	}
	
	public static String formulaChecks(Farm farm){
		StringBuffer buff=new StringBuffer();
		for (Crop cp:farm.cropping.getCrops().values()){
			buff.append(cp.type+"\n");
			buff.append(cp.printFormulaCheck(farm.location()));
		}
		return buff.toString();
	}
	
	/** Print a breakdown of the profit components for a Farm. 
	 * @param farm The farm to summarize 
	 * @param useMarginals Flag whether to print marginal (per ha) values or total values 
	 * */
	public static String profitSummary(Farm farm,boolean useMarginals){
		StringBuffer buff=new StringBuffer();
		double totalProfit=0;
		for(CropType ct:farm.cropping.baseCropTypes()){
			for ( CropCopy cc:farm.cropping.getCrop(ct).getYearCopies()){
				if ( cc.getSolvedArea() > 0 ){
					Map<String,Double> profits=cc.getProfitSummary(useMarginals);
					buff.append(cc.name()+" "+profits.get("Total")+" "+profits.get("Gross")+" "+profits.get("OpPenalty")+" "+profits.get("OpFuel"));
					buff.append("\n");
					totalProfit+=profits.get("Total");
				}
			}
		}
		buff.append("Total: "+totalProfit);
		return buff.toString();
	}
	/** Print a breakdown of the profit components for a Farm. 
	 * @param farm The farm to summarize 
	 * @param useMarginals Flag whether to print marginal (per ha) values or total values 
	 * */
	public static String detailedObjectiveSummary(Farm farm){
		StringBuffer buff=new StringBuffer();
		int columnWidth=15; // Allow 15 characters per column
		Map<ObjectiveType,Objective> objs=farm.getObjectives();
		for(ObjectiveType obj:objs.keySet()){
			buff.append(obj+":"+MathPrint.f2.format(farm.getValueForObjective(obj))+" "+
					MathPrint.f2.format(objs.get(obj).scaleFactor())+" "+
					MathPrint.f2.format(objs.get(obj).scaleFactor()*farm.getValueForObjective(obj))+"\n");			
		}
		for(CropType ct:farm.cropping.baseCropTypes()){
			for ( CropCopy cc:farm.cropping.getCrop(ct).getYearCopies()){
				if ( cc.getSolvedArea() > 0 ){
					buff.append(cc.getDetailedProfitBreakdown(columnWidth));
				}
			}
		}
		return buff.toString();
	}
	
	/** Print a summary of the workload in each period */
	public static String workloadSummary(Farm farm){
		StringBuffer buff=new StringBuffer();
		Map<Integer,LinkedHashMap<Object,Double>> workLoad=farm.workers.getWorkload();
		if ( farm.objectives().containsKey(ObjectiveType.FREETIME)){
			FreeTimeComponent ftc=(FreeTimeComponent)farm.getModelComponent(ModelComponent.MCType.FREETIME);
			Map<Integer,Double> ft=ftc.getSolvedFreeTime();
			for (int p=0;p<workLoad.size();p++){
				Map<Object,Double> work=workLoad.get(p);
				work.put(ObjectiveType.FREETIME, ft.get(p));
			}
		}
		ArrayList<Object> masterKeys=new ArrayList<Object>(workLoad.get(0).keySet());
		for( Object key:masterKeys){
			buff.append(key+",");
		}
		buff.deleteCharAt(buff.length()-1);
		buff.append("\n");
		for(int p=0;p<farm.numPeriods;p++){
			for ( Object key:masterKeys){
				buff.append(MathPrint.f2.format(workLoad.get(p).get(key))+",");
			}
			buff.deleteCharAt(buff.length()-1);
			buff.append("\n");
		}
		
		return buff.toString();
	}
	/** Print a summary of the solved model components for this farm. 
	 * @param farm The farm to be summarized 
	 * @param detailed Flag whether to print complete detail or not 
	 * */
	public static String solution(Farm farm,boolean detailed){
		StringBuffer buff = new StringBuffer();
		for (Objective op:farm.objectives().values()){
			buff.append(op.type+": " +MathPrint.df2.format(farm.getValueForObjective(op.type))+"\n");
		}
		if ( farm.getComponents().keySet().contains(ModelComponent.MCType.HEDGEROWS) ){			
			buff.append("\n-- Hedge Length: "+farm.getValueForObjective(ObjectiveType.HEDGEROWS)+"\n");
		}

		if ( farm.getComponents().keySet().contains(ModelComponent.MCType.ELSOPTIONS)){
			ELSOptionsComponent comp = (ELSOptionsComponent)farm.getModelComponent(ModelComponent.MCType.ELSOPTIONS);
			buff.append("\n-- ELS Options Summary --\n"+comp.printSolution());
		}
		
		
		buff.append("\n"+farm.cropping.printSolution(farm,detailed)+" \n");
		buff.append(farm.rotations.printSolution()+"\n ");
		buff.append(farm.workers.printSolution(farm));
		buff.append("\n"+farm.workers.printFixedCosts(farm)+"\n");
		
		return buff.toString();
	}
	
	
	
	public static String csvWorkPlan(Farm farm){
		return farm.cropping.printCSVWorkPlan();
	}
}
