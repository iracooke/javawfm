/**
 * 
 */
package jfm;
/**
 * @author iracooke
 *
 */

import jfm.model.WorkableHoursCalculator;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub



/*		double d1= Double.parseDouble(args[0]);
		double d2 = Double.parseDouble(args[1]);
		double d3= Double.parseDouble(args[2]);
		double d4= Double.parseDouble(args[3]);
		System.out.println(d1+" "+d2+" "+d3+" "+d4);
		double w = wcalc.calculateWorkableHours(d1,d2,d3,d4);*/
		double w = WorkableHoursCalculator.workableHours(100, 120, 1.5, 600);
		System.out.println(w);
	}

}
