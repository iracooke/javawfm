
/** Classes which define the main components of a farming model. 
 * @author Ira Cooke
 * 
 * Consists of ModelComponent implementations (Cropping, Rotations, Workers), ModelPrimitive implementations (Crop, Operations, RotationPenalty, Worker ) as well as 
 * Type definitions (CropYear, Disease, Rotation ).  There are also several classes to perform miscellaneous functions including Formula ( formula parsing), Limit (holds 
 * limit information), Location (holds location information ), Output ( static functions for extracting data) and Type ( a list of enums). 
 * 
 * The three ModelComponents perform the key functions required to construct the LP matrix, including adding variables (columns) and constraints (rows). These classes also 
 * act as containers for their respective ModelPrimitive classes.  
 * 
 * The heirarchy of accessible model objects is;

 \dot digraph mobjs {
 	Model->Cropping;
 	Cropping->Crop [color=blue];
 	Cropping->Disease [color=blue];
 	Cropping->Rotation [color=blue];
 	Cropping->Limit [color=blue];
	Crop->Operation [color=blue];
 	Model->Rotations;
 	Rotations->RotationPenalty [color=blue];
 	Model->Workers;
 	Workers->Worker [color=blue];
 
 } \enddot
	Where blue arrows indicate a one to many relationship and black arrows indicate a one to one relationship. 
	Cropping is by far the largest and most complex ModelComponent object, in which most constraints are implemented.
 * */
package jfm.model;


import java.io.File;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.*;

import org.w3c.dom.*;

import java.io.*;

import jfm.lp.LPX;
import jfm.lp.Matrix;
import jfm.lp.ModelComponent;
import jfm.lp.ModelComponent.MCType;
import jfm.model.Types.ELSCode;
import jfm.lp.Objective;
import jfm.model.Types.*;
import jfm.xml.FarmParser;

/** Container class for all components which define the model.  
 * 
 * 
 * \note This class exposes its main model components as public members. It is intended that 
 * these components should be changed outside the Farm object and these changes 
 * will be reflected in the internal matrix. 
 * 
 * @author Ira Cooke
 * */
public final class Farm {	
	/** Land Use */
	public LandUseComponent landUse;
	/** The cropping object */
	public CroppingComponent cropping;
	/** The rotations object */
	public RotationsComponent rotations;
	/** The workers object */
	public WorkersComponent workers;
	/** The model objectives */
	private static Set<MCType> compulsoryComponents=new HashSet<MCType>();
	static {
		compulsoryComponents.add(MCType.LANDUSE);
		compulsoryComponents.add(MCType.CROPPING);
		compulsoryComponents.add(MCType.WORKERS);
		compulsoryComponents.add(MCType.ROTATIONS);
	}
	
	
//	public final HashMap<ObjectiveType,Objective> objectives;

	/** This is the complete list of components ... the aliases above are for convenience only */
	Set<ModelComponent> components=new LinkedHashSet<ModelComponent>();
	
	
	private final Matrix matrix;	// LP matrix to be solved 
	/** The number of periods into which a year is divided */
	public final int numPeriods; // It is not possible to modify this on the fly
	/** The number of years used to calculate periodic boundary conditions (ie length of the periodic time box) */
	public static int maxYears=2; // This is also unmodifiable

	private double fuelPrice;
	private double interestRate;
	private Location location;	
	private LPX solutionStatus=null;
	
	
	/** Builds a matrix from the model components and solves it.
	 * @param exceptionOnFail If true the program will exit if the solver fails to find an optimal solution 
	 * @return The solution status of the solver object @see LPX 
	 * */
	public LPX solve(boolean exceptionOnFail) throws GLPKException , BadModelException {
		setFormulaVariables();
		LPX status=matrix.updateAndSolve();
		solutionStatus=status;
		if ( exceptionOnFail && status != LPX.LPX_OPT){
			StringBuffer message=new StringBuffer();
			matrix.printCSV("fail_matrix.csv");
			message.append("Matrix written to file matrix.csv");
			throw new GLPKException(message.toString());
		} else {
			//matrix.printCSV("matrix.csv");
			return status;
		}
	}
	
	public LPX solutionStatus (){
		return solutionStatus;
	}
	
	public Map<ObjectiveType,Objective> objectives(){
		return matrix.objectives();
	}
	
	public List<ObjectiveType> sortedObjectiveTypes(){
		ArrayList<ObjectiveType> ots = new ArrayList<ObjectiveType>(matrix.objectives().keySet());
		Collections.sort(ots);
		return Collections.unmodifiableList(ots);
	}

	public boolean needsRebuild(){
		if ( matrix != null ){
			return matrix.needsRebuild();
		} else {
			return true;
		}
	}
	
	/** Set the location object (soiltype and rainfall) for this Farm */
	public void setLocation(Location newLoc){
		location=newLoc;
		matrix.flagForRebuild();
	}
	public Location location(){return location;};

	public void setFuelPrice(double fp){
		fuelPrice=fp;
		cropping.updateStructure();
	}
	public double fuelPrice(){return fuelPrice;};
	
	public void setInterestRate(double ir){
		interestRate=ir;
		workers.updateStructure();
	}
	public double interestRate(){return interestRate();}

	/** Return the value of the baseObjective function for a particular ObjectiveType */
	public double getValueForObjective(ObjectiveType type){
		return matrix.primitiveMatrix.getObjectiveValue(type);
	}
	
	public double getEnterpriseOutput(){
		double eo=0;
		for(CropType ct: cropping.baseCropTypes()){
			for(Crop.CropCopy cp:cropping.getCrop(ct).getYearCopies()){
				eo+=cp.grossMargin()*cp.solvedArea();
			}
		}
		return eo;
	}
	
	public double getProfit(){
		return getValueForObjective(ObjectiveType.PROFIT);
	}
	
	public double getWinterStubble(){
		return getValueForObjective(ObjectiveType.WINTERSTUBBLE);
	}
	
	public void setWinterStubbleWeight(double wt){
		if (!this.objectives().containsKey(ObjectiveType.WINTERSTUBBLE)){
			throw new Error("Can't set hedge objective weight because no hedge objective defined");			
		}
		this.getObjectives().get(ObjectiveType.WINTERSTUBBLE).setScaleFactor(wt);
	}
	
	public double getHedgeLength(){
		return getValueForObjective(ObjectiveType.HEDGEROWS);
	}
	
	public void setHedgeWeight(double wt){
		if (!this.objectives().containsKey(ObjectiveType.HEDGEROWS)){
			throw new Error("Can't set hedge objective weight because no hedge objective defined");			
		}
		this.getObjectives().get(ObjectiveType.HEDGEROWS).setScaleFactor(wt);
	}
	
	public Set<ELSOption> getELSOptionSet(){
		Set<ELSOption> options=new HashSet<ELSOption>();
		for(ModelComponent mc:components){
			if ( mc instanceof ELSOptionComponent ){
				ELSOptionComponent mco=(ELSOptionComponent)mc;
				options.addAll(mco.getOptions());
			}
		}
		return options;
	}
	
	/*
	public void addObjective(Objective obj,ObjectiveBuilder bldr){
		matrix.addObjective(obj,bldr);
	}
	*/
	/*
	public void addObjectiveBuilder(ObjectiveBuilder bldr){
		matrix.addObjectiveBuilder(bldr);
	}
*/

	/** Disable terminal output from the solver object */
	public void disableSolverOutput(){
		matrix.disableSolverOutput();
	}
	/** Enable terminal output from the solver object */
	public void enableSolverOutput(){
		matrix.enableSolverOutput();
	}

	/** Create a Farm model object from a valid xml document object that has already been read in */
	public static Farm fromXML(Document doc){
		Farm farm;
		FarmParser parser = new FarmParser(null);
		NodeList farmNodes=doc.getElementsByTagName("farm");
		Element farmRoot = (Element)farmNodes.item(0);
		try {
			parser.parse(farmRoot);
			farm=(Farm)parser.getObject();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Error("Failed to parse model");
		}
		return farm;
	}
	
	public static Document parseDocument(String xmlFileName){
		File docFile = new File(xmlFileName);
		Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(docFile);
		} catch (java.io.IOException e) {
			throw new Error("Can't find the file "+e.getMessage()+" "+xmlFileName);
		} catch (Exception e) {
			throw new Error("Problem parsing the file."+xmlFileName);
		}
		return doc;
	}
	
	/** Create a Farm model object from a structured xml file. 
	 * Opens an xml document and parses it to produce a Farm object. Details on the xml parsing are implemented in jfm.xml.FarmParser 
	 * @param xmlFileName The complete path name to the xml input file from which the farm will be created */
	public static Farm fromXML(String xmlFileName) {			
		return fromXML(parseDocument(xmlFileName));
	}
	
	/** \internal Initializes the model components and creates matrix and solver object instances */
	public Farm(Location loc,final double fuelp,final double interestr,final jfm.lp.LPPeer.Solver solverType,final int np,Set<ModelComponent> modelComponents) throws BadModelException {
		fuelPrice=fuelp;
		interestRate=interestr;
		location=loc;
//		objectives=objs;
		// Note that it is important that the order added here reflects the order of structural variables 
		// Get Compulsory Model components
		Stack<MCType> compuls=new Stack<MCType>();
		Set<ObjectiveType> requiredObjectives=new HashSet<ObjectiveType>();
		compuls.addAll(compulsoryComponents);
		for ( ModelComponent mc: modelComponents){
			components.add(mc);
			mc.setParent(this);
			requiredObjectives.addAll(mc.getRequiredObjectives());
			switch(mc.type){
			case LANDUSE:
				landUse=(LandUseComponent)mc;
				compuls.remove(MCType.LANDUSE);
				break;
			case CROPPING:
				cropping=(CroppingComponent)mc;
				compuls.remove(MCType.CROPPING);
				break;
			case WORKERS:
				workers=(WorkersComponent)mc;
				compuls.remove(MCType.WORKERS);
				break;
			case ROTATIONS:
				rotations=(RotationsComponent)mc;
				compuls.remove(MCType.ROTATIONS);
				break;
			default:
				if ( compuls.size()!=0){
					throw new Error("Can't add non-compulsory ModelComponent "+mc.type+" until all compulsory components have been added \n");
				}
			}
		}
		if ( compuls.size()!=0 ){
			throw new Error("The Compulsory Model components "+compuls+" \n were not specified");
		}
		numPeriods=np;
		// Create all the required objectives with default weights ( 1.0)
		Map<ObjectiveType,Objective> objectives = new HashMap<ObjectiveType,Objective>();
		for(ObjectiveType otype:requiredObjectives){
			objectives.put(otype, new Objective(otype));
		}
		setFormulaVariables();
		matrix=new Matrix(components,objectives,solverType);
	}
	
	/** Needs to be called if any formula variable is altered after initial setup*/
	public void setFormulaVariables(){
//		System.out.println("Seeting farm vas");
		for ( ModelComponent mc:components){			
			mc.setFormulaVariables();
		}
	}
	public ModelComponent getModelComponent(ModelComponent.MCType type){
		for(ModelComponent mc:components){
			if ( mc.type==type){
				return mc;
			}
		}
		throw new Error("Model Component "+type+" not found");
	}
	
	public void addComponent(ModelComponent newComponent){
		components.add(newComponent);
		newComponent.setParent(this);
		matrix.addComponent(newComponent);
	}
	
	public void addObjective(Objective obj){
		matrix.addObjective(obj);
	}
	
	public Map<ObjectiveType,Objective> getObjectives(){
		return matrix.objectives();
	}
	public Map<ModelComponent.MCType,ModelComponent> getComponents(){
		Map<ModelComponent.MCType,ModelComponent> cmap = new HashMap<ModelComponent.MCType,ModelComponent>();
		for (ModelComponent mc:components){
			cmap.put(mc.type, mc);
		}
		return cmap;
	}
	
	/** Constructs a bare-bones template farm object from an existing farm object . 
	 * Creates a copy of the original farm. Clears crops from the copy. Clears Limits from the copy. 
	 * @return A new farm object that is a stripped down version of the supplied \c base farm. 
	 * @param base The base or template farm from which to construct the new farm */
	public static Farm createTemplate(Farm base) throws BadModelException {
		Farm bb = base.copy();
		bb.cropping.clearCrops();
		bb.cropping.clearLimits();
		return bb;
	}
	
	public Farm copy() throws BadModelException {
/*		HashMap<ObjectiveType,Objective> objscpy = new HashMap<ObjectiveType,Objective>();

		for(Objective op:objectives.values()){
			objscpy.put(op.type, op.copy());
		}*/
		CroppingComponent croppingcpy=cropping.copy();
		Location loccpy=location.copy();
		RotationsComponent rotscpy=rotations.copy(croppingcpy, loccpy);
		Set<ModelComponent> mcCopy=new HashSet<ModelComponent>();
		for(ModelComponent mc:components){
			ModelComponent mccpy=null;
			switch(mc.type){
				case CROPPING:
					mccpy=croppingcpy;
					break;
				case ROTATIONS:
					mccpy=rotscpy;
					break;
				default:
					mccpy=mc.copy();
			}
			mcCopy.add(mccpy);
		}
		Farm newFarm=new Farm(loccpy,fuelPrice,interestRate,matrix.getSolverType(),numPeriods,mcCopy);		
		Map<ObjectiveType,Objective> oldObjs=this.getObjectives();
		Map<ObjectiveType,Objective> newObjs=newFarm.getObjectives();
		// Copy the objective coefficients from old to new
		for ( ObjectiveType type:oldObjs.keySet()){
			Objective newobj=newObjs.get(type);
			newobj.setScaleFactor(oldObjs.get(type).scaleFactor());
		}

		return newFarm;
	}

	// Static version of the wrap period function
	static int wrapPeriod(int p,int numPeriods){
		int yr = (int)Math.floor(p/(double)numPeriods);
		return p-yr*numPeriods;
	}
	public String toString(){
		StringBuffer buff = new StringBuffer();
		buff.append(cropping +" \n "+rotations+" \n"+workers);
		return buff.toString();
	}

	/** Retrieve a Map from each ObjectiveType in the model to the corresponding 
	 * contribution of that objective to the total 
	 * */
	public Map<ObjectiveType,Double> getUnScaledObjectives(){
		Map<ObjectiveType,Double> unscaled = new HashMap<ObjectiveType,Double>();
		// First check solution status 
		if ( matrix.getSolutionStatus() == LPX.LPX_OPT){
			for ( ObjectiveType ob:matrix.objectives().keySet()){
				unscaled.put(ob, matrix.primitiveMatrix.getObjectiveValue(ob));
			}
		} else {
			throw new Error("Can't get objectives because the matrix is not at an optimal solution");
		}
		return unscaled;
	}
	/** Retrieve scaled objectives */
	public Map<ObjectiveType,Double> getScaledObjectives(){
		Map<ObjectiveType,Double> scaled = new HashMap<ObjectiveType,Double>();
		// First check solution status 
		if ( matrix.getSolutionStatus() == LPX.LPX_OPT){
			for ( Objective ob:matrix.objectives().values() ){
				scaled.put(ob.type, matrix.primitiveMatrix.getObjectiveValue(ob.type)*ob.scaleFactor());
			}
		} else {
			throw new Error("Can't get objectives because the matrix is not at an optimal solution");
		}
		return scaled;
	}
	

}
