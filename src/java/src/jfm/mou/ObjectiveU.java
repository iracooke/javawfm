/**
 * 
 */
package jfm.mou;

import jfm.model.Types;

/** Data container for farmer survey data relating to a single objective. 
 * There is a one to one mapping 
 * between this objective and an objective that is actually implemented in the model */
public class ObjectiveU {
	private final double min;
	private final double max;
	public final double slope;
	public final double rawWeight;
	
	// --- FOR CURVED --- //

	public boolean isCurved=false;
	public boolean replacesObjective=false;
	private double[] xVals=null;
	private double[] yVals=null;
	
	/** Slopes of the satisfaction curve on each segment  */
	public final Types.ObjectiveType type;
	public final String units;
	ObjectiveU(Types.ObjectiveType type_,String units_,double min_,double max_,double rw){
		max=max_;
		min=min_;
		type=type_;
		units=units_;
		rawWeight=rw;
		slope=100/(max-min);
	}
	
	public void setCurve(double[] x,double[] y){
		xVals=x;
		yVals=y;
		isCurved=true;
		replacesObjective=true;
	}
	public double[] xVals(){
		if ( xVals!=null){
			return xVals;
		} else {
			throw new Error("x Vals is null");
		}
	}
	public double[] yVals(){
		if ( yVals!=null){
			return yVals;
		} else {
			throw new Error("Yvals is null");
		}
	};
	
	public double slope(){
		if ( isCurved ){
			throw new Error("Slope is not a valid quantity for curved Objectives");
		}
		return slope;
	};
	
	// -- UNUSED STUFF FOR PIECEWISE LINEAR OBJECTIVES --//
	/** Points on the satisfaction curve which maps natural units for the objective onto satisfaction 
	 * units */
//	private final List<Double> xVals = new ArrayList<Double>();
//	private final List<Double> yVals = new ArrayList<Double>();
//	private final List<Double> segmentSlopes = new ArrayList<Double>();
	/** Based on the points in the satisfaction vs objective value curve we work out the conversion 
	 * rate between the units of this objective and satisfaction */
/*	private void calculateSlopes(){
		if ( xVals.size()!= yVals.size()){
			throw new Error("xVals and yVals must have equal numbers of elements");
		}
		for ( int i=1;i<xVals.size();i++){
			double xdiff=xVals.get(i)-xVals.get(i-1);
			double ydiff=yVals.get(i)-yVals.get(i-1);
			segmentSlopes.add(ydiff/xdiff);
		}
	}
	*/
}
