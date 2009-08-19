package jfm.model;

import jfm.lp.*;
import jfm.lp.ConstraintBuilder.CBType;
import jfm.model.Types.ObjectiveType;

/** This is as rough as guts.  For now it's not even a part of the model that gets solved 
 * All we do with this class is store a historical hedgerow length */
public abstract class BoundaryComponent extends ModelComponent {

	private final double historicalLength;
	private final double discountRate;
	private final double maintainCost;
	private final double maxLength;
	protected MatrixVariable lengthCounter=null;
	private final ObjectiveType otype;
	private final MCType mctype;

	protected final SOS2Primitive profit; // Contribution to profit from this model component is non-linear
//	private SOS2Primitive attitude=null;
	
	public BoundaryComponent(MCType mctype_,ObjectiveType otype_,double maxLen_,double hist,double createCost,double destCost,double maintain,double discountRate_){
		super(mctype_);
		mctype=mctype_;
		otype=otype_;
		historicalLength=hist;
		maxLength=maxLen_;
		if ( historicalLength > maxLength){
			throw new Error("Historical Length cannot be greater than max hedge length");
		}
		discountRate=discountRate_;
	/*Use this version to get everything in terms of total lifetime costs 
	 * 	maintainCost=maintain/discountRate;
		double[] xProfitVals=new double[]{-historicalLength,0,maxLength};
		double[] yProfitVals=new double[]{-destCost*historicalLength,
				-maintainCost*historicalLength,
				-maintainCost*(maxLength)-createCost*(maxLength-historicalLength)
		};*/
		
		// Instead we use discount rates to translate into yearly costs 
		maintainCost=maintain;
		double[] xProfitVals=new double[]{-historicalLength,0,maxLength};
		double[] yProfitVals=new double[]{-destCost*historicalLength*discountRate,
				-maintainCost*historicalLength,
				-maintainCost*(maxLength)-createCost*discountRate*(maxLength-historicalLength) };
	
//		System.out.println("Hedges cost "+destCost+" to destroy "+yProfitVals[0]+" "+yProfitVals[1]+" "+yProfitVals[2]);
		
		// Because the hedgerow costs (contribution to profit) are non-linear we need this SOS2 primitive
		profit=new SOS2Primitive("HedgerowProfit",Types.ObjectiveType.PROFIT,xProfitVals,yProfitVals);
		profit.registerParent(this);

		//		attitude=new SOS2Primitive;
	
		this.requireObjective(Types.ObjectiveType.PROFIT); // Financial impact of hedges

		// TODO Check that this really isn't required .. related to FarmerMOU.java
	//	this.requireObjective(Types.ObjectiveType.DUMMY);
		
		
		this.requireObjective(otype);// Utility impact of hedges
	}
	
	/*
	public void setAttitude(double[] x,double[] y){
		this.requireObjective(ObjectiveType.HEDGEROWS);
//		getParent().addObjective(new Objective(ObjectiveType.HEDGEROWS));
		// Shift x values so they represent changes from the historical
		for(int i=0;i<x.length;i++){
			x[i]=x[i]-historicalLength;
		}
		
		attitude=new SOS2Primitive("hedgeAttitude",ObjectiveType.HEDGEROWS,x,y);
		attitude.registerParent(this);
	}*/
	
	int getBoundaryCounterColumn(){
		return lengthCounter.column();
	}
	
	public double getSolvedLength(){
		double plen=profit.getSolvedValue();
/*		if (attitude!=null){
			double alen=attitude.getSolvedValue();
			if ( !JFMMath.isZero(plen -alen )){
				throw new Error("Mismatch between solved lengths "+plen+" "+alen);
			}
		}*/
		return plen+historicalLength;
	}
	
	@Override
	public ModelComponent copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void initializeStructure() throws BadModelException {
		MatrixVariable newVariable=new MatrixVariable(1.0,
				0.0,0.0,
				LPX.LPX_LO,LPX.LPX_CV,matrix.numCols(),otype);
			newVariable.setTag("boundarylength "+mctype.toString());
			matrix.addVariable(newVariable);	
			lengthCounter=newVariable;
			// The above is just a counter for hedge length it will be bound to the hedgerow attitude variable
			// Since it is a counter it has no intrinsic value in itself
			
		/* The profit objective is a piecewise linear function with three pieces */
		profit.initializeStructure();
		
/*		if ( attitude!=null){
			attitude.initializeStructure();
		}*/
	}
	
	
	

	@Override
	protected String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFormulaVariables() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void updateStructure() {
		// TODO Auto-generated method stub

	}

}
