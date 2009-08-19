/**
 * 
 */
package jfm.model;

import jfm.lp.ModelPrimitive;
import jfm.model.Types.ObjectiveType;
import jfm.model.ELSOptionsComponent;
/**
 * @author iracooke
 *
 */
public class ELS extends ModelPrimitive {
	private final double paymentPerHectare;
	private final double pointsRequiredPerHectare;
	
	ELS(double paymentPerHectare_,double pointsRequiredPerHectare_){
		paymentPerHectare=paymentPerHectare_;
		pointsRequiredPerHectare=pointsRequiredPerHectare_;
	}
	/* (non-Javadoc)
	 * @see jfm.lp.ModelPrimitive#name()
	 */
	@Override
	public String name() {
		return "ELS";
	}
	public double isInELS(){
		return getSolution()[0];
	}
	
	public double getAnnualSubsidy(){
		ELSOptionsComponent parent=(ELSOptionsComponent)parentComponent;
		return parent.eligibleArea()*paymentPerHectare;		
	}
	
	public double getPointsRequirement(){
		ELSOptionsComponent parent=(ELSOptionsComponent)parentComponent;
		return parent.eligibleArea()*pointsRequiredPerHectare;
	}
	
	/* (non-Javadoc)
	 * @see jfm.lp.ModelPrimitive#updateStructure(java.lang.Object)
	 */
	@Override
	protected void updateStructure(Object caller) {
		setCoefficient(ObjectiveType.PROFIT,getAnnualSubsidy(),0);
	}

}
