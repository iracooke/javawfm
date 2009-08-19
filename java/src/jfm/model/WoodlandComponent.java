package jfm.model;

import jfm.lp.*;

public class WoodlandComponent extends ModelComponent {
	private double historical=-1;
	private double creationCost=-1;
	private double destructionCost=-1;
	private double maintainenceCost=-1;
	
	public WoodlandComponent(){
		super(ModelComponent.MCType.WOODLAND);
		// Add constraint builders
	}
	
	public class Area extends ModelPrimitive{

		public String name(){
			return "Woodland Area";
		}
		// Only the profit objective need be updated because the other obj has a fixed coeff
		protected void updateStructure(Object caller){
//			this.setCoefficient(Types.ObjectiveType.PROFIT, -creationCost, 0);
//			this.setCoefficient(Types.ObjectiveType.PROFIT, -destructionCost, 1);
//			this.setCoefficient(Types.ObjectiveType.PROFIT, -maintainenceCost, 2);
		}
	}
	
	@Override
	public ModelComponent copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void initializeStructure() throws BadModelException {
		// TODO Auto-generated method stub

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
