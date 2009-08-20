/**
 * 
 */
package jfm;

import jfm.lp.LPX;
import jfm.model.BadModelException;
import jfm.model.Farm;
import jfm.model.GLPKException;
import jfm.model.Output;
import jfm.mou.FarmerMOU;

/**
 * @author iracooke
 *
 */
public class SocialApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			if ( args.length <= 2 ){
				Farm farm = Farm.fromXML(args[0]);
				if ( args.length==2){
					FarmerMOU fmou = FarmerMOU.fromXML(args[1]);
					FarmerMOU.applyToFarm(fmou, farm);
							
				}
//				farm = farm.copy();
				/*
				Stubbles stubblesComponent = new Stubbles(4);
				Objective theObjective=new Objective(ObjectiveType.WINTERSTUBBLE);
				theObjective.setScaleFactor(0);
				farm.addComponent((ModelComponent)stubblesComponent);
				farm.addObjective(theObjective);
				
				CropComplexity complexityComponent=new CropComplexity();
				theObjective=new Objective(ObjectiveType.CROPCOMPLEXITY);
				theObjective.setScaleFactor(0.0);
				farm.addComponent((ModelComponent)complexityComponent);
				farm.addObjective(theObjective);
				
				FreeTime freeTimeComponent = new FreeTime();
				theObjective = new Objective(ObjectiveType.FREETIME);
				theObjective.setScaleFactor(0);
				farm.addComponent((ModelComponent)freeTimeComponent);
				farm.addObjective(theObjective);*/
//				System.out.println(farm);
//				System.out.println(" Now Solving ");
				
				// Do a repeated solve test 
				/*
				double[] wwprices={75};
				Cropping cropping=farm.cropping;
				Crop ww = cropping.getCrop(Types.CropType.WINTERWHEAT);
				for ( int i=0;i < wwprices.length ;i++){
					System.out.println("--- SOLVING WITH PRICE "+wwprices[i]);
					ww.resetPrimaryPrice(wwprices[i]);
					farm.solve(true);
					System.out.println(Output.solution(farm, false));
				}*/
				
				
				if ( farm.solve(true) == LPX.LPX_OPT ){
					System.out.println(Output.solution(farm,false));
//					System.out.println(Output.formulaChecks(farm));
//					System.out.println(Output.detailedObjectiveSummary(farm));
//					FileWriter writer = new FileWriter("wkload.csv");
//					writer.write(Output.workloadSummary(farm));
//					writer.close();

//					writer = new FileWriter("wkplan.csv");
//					writer.write(Output.csvWorkPlan(farm));
//					writer.close();
				} else {
					System.out.println("The farm "+args[0]+"failed to solve");
				}
			} else {
				System.out.println("Wrong args");
			}
			} catch (GLPKException ex){
				throw new Error(ex.getMessage());
			}catch (BadModelException ex){
				throw new Error(ex.getMessage());
			} /*catch (IOException ex){
			}
				throw new Error(ex.getMessage());
			}*/
	}

}
