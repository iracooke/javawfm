package jfm.xml;

import java.util.*;

import jfm.lp.*;
import jfm.model.*;
import jfm.model.Farm;
import jfm.model.Location;
import jfm.model.RotationsComponent;
import jfm.model.WorkersComponent;
import jfm.model.Types.ObjectiveType;


/** Parses a Farm object */
public final class FarmParser extends JFMObjectParser {

	public FarmParser(ObjectParser parent_){
		parent=parent_;
		registerAttribute("fuelprice",JFMObjectParser.mandatoryAttribute);
		registerAttribute("interestrate",JFMObjectParser.mandatoryAttribute);
		registerAttribute("nperiods",JFMObjectParser.mandatoryAttribute);
		registerAttribute("solver",JFMObjectParser.mandatoryAttribute);
	}
	

	@Override
	public void initializeObject() throws XMLSyntaxException, XMLObjectException{
		LandUseComponent landuse=(LandUseComponent)getParserList("landuse").get(0).getObject();
		CroppingComponent cropping = landuse.cropping;
//		Hedgerows hedges=landuse.getHedgerows();
		WorkersComponent workers = (WorkersComponent)getParserList("workers").get(0).getObject();
		Location location=(Location)getParserList("location").get(0).getObject();
		int numPeriods=Integer.parseInt(getNamedAttribute("nperiods"));
		double fuelp=Double.parseDouble(getNamedAttribute("fuelprice"));
		double interestr=Double.parseDouble(getNamedAttribute("interestrate"));
		jfm.lp.LPPeer.Solver solver=jfm.lp.LPPeer.xmlToSolverType(getNamedAttribute("solver"));
		RotationsComponent rotations=new RotationsComponent(numPeriods,cropping,location);

		/** These model components are special because they are a mandatory requirement 
		 * for any Farm object 
		 */
		Set<ModelComponent> modelComponents=new LinkedHashSet<ModelComponent>();// Order is very important
		
		modelComponents.add(landuse);
		modelComponents.add(cropping);
		modelComponents.add(workers);
		modelComponents.add(rotations);
		// Now look for optional model components
	//	if ( hedges!=null){
	//		modelComponents.add(hedges);
	//	}
		String[] optionalModelComponents={new StubblesParser(null).parsesNode(),
				new CropComplexityParser(null).parsesNode(),
				new FreeTimeParser(null).parsesNode(), new MOTADRiskParser(null).parsesNode(), new VARRiskParser(null).parsesNode(),
				new HedgerowLengthParser(null).parsesNode(), new BoundaryMaintenanceParser(null).parsesNode()
				, new ELSOptionsParser(null).parsesNode(), new DitchLengthParser(null).parsesNode()};
		for( String cname:optionalModelComponents){
			if ( parserListExists(cname)){
				modelComponents.add((ModelComponent)getParserList(cname).get(0).getObject());
			} 
		}
		
		// Initialize the farm with the profit model
//		obj = new Farm(location,fuelp,interestr,solver,numPeriods, cropping,rotations,workers);
		try {
			obj = new Farm(location,fuelp,interestr,solver,numPeriods, modelComponents);
		} catch (BadModelException ex){
			throw new Error(ex.getMessage());
		}
		Farm objalias = (Farm)obj;
		
		// Now add in models and associated objective for additional model components if defined
		HashMap<ObjectiveType,Objective> objectives=new HashMap<ObjectiveType,Objective>();
		if ( parserListExists("objective")){
			for(ObjectParser o:getParserList("objective")){
				Objective ob=(Objective)o.obj;
				objectives.put(ob.type, ob);
				if ( parserListExists(ob.type.xmlname)){
					objalias.addComponent((ModelComponent)getParserList(ob.type.xmlname).get(0).getObject());
					objalias.addObjective(ob);
				} else {
					throw new Error("The objective "+ob.type+" was defined but no model exists for this objective");
				}
			}
		}

	}
	public String toString(){return "FarmParser";};
	public String parsesNode(){ return "farm";};
	protected boolean isPrimitive(){ return false;};
	public String name(){ return "farm";};
}
