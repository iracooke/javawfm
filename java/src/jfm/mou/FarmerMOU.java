/**
 * Multi Objective Utility package for the Java Farming Model 
 * 
 * 
 */
package jfm.mou;
import jfm.model.Types.ObjectiveType;

import java.io.File;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import jfm.model.*;
import jfm.lp.*;

/** Farmer Multi Objective Utility object. 
 * 
 * Contains objective functions and their relative weights for all of the objectives 
 * that contribute to a Farmer's overall utility. */
public class FarmerMOU {
	private final Map<ObjectiveType,Double> rawWeights=new HashMap<ObjectiveType,Double>();
	private final Map<ObjectiveType,Double> weights=new HashMap<ObjectiveType,Double>();
	private final Map<ObjectiveType,ObjectiveU> objectives=new HashMap<ObjectiveType,ObjectiveU>();
	public void addObjective(ObjectiveU obj){
		ObjectiveType t=obj.type;
		rawWeights.put(t,obj.rawWeight);
		objectives.put(t, obj);
		// Renormalise weights
		renormalizeWeights();
	}
	
	private void renormalizeWeights(){
		Double[] all = new Double[rawWeights.values().size()]; 
		rawWeights.values().toArray(all);
		double total=jfm.utils.JFMMath.sum(all);
		for(ObjectiveType type:rawWeights.keySet()){
			weights.put(type, rawWeights.get(type)/total);
		}
	}
	
	public double normalizedProfitWeight(){
		this.renormalizeWeights();
		return weights.get(ObjectiveType.PROFIT);		
	}
	
	public static Document parseMOUDocument(String fileName){
		File docFile = new File(fileName);
		Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(docFile);
		} catch (java.io.IOException e) {
			e.printStackTrace();
			throw new Error("Can't find the file "+fileName+" "+e.getLocalizedMessage());		
		} catch (Exception e) {
			e.printStackTrace();
			throw new Error("Problem parsing the file."+fileName+": "+e.getLocalizedMessage());
		}
		return doc;
	}
	
	public static FarmerMOU fromXML(String fileName){
		Document doc = parseMOUDocument(fileName);
		return(fromXML(doc));
	}
	
	public static FarmerMOU fromXML(Document doc){
		try {
			Element root = doc.getDocumentElement();
			FarmerMOUParser parser=new FarmerMOUParser(null);			
			parser.parse(root);
			return (FarmerMOU)parser.getObject();
		} catch (jfm.xml.XMLObjectException e){
			e.printStackTrace();
			throw new Error(e.getMessage());
		} catch (jfm.xml.XMLSyntaxException e){
			e.printStackTrace();
			throw new Error(e.getMessage());
		} 
	}
	
	// Curved objectives need an associated model component simply as a container to hold them and ensure that they have their constraints built. 
	// Since we do not have an overarching"objectives" model component we tend to simply add components to cropping.
	// TODO Create an "objectives" model component to allow building of objective constraints.	
	public static ModelComponent associatedModelComponent(ObjectiveType objType,Farm farm){
		ModelComponent.MCType mcType;
//		switch (objType){
	//	case HEDGEROWS: 
	//		mcType=ModelComponent.MCType.HEDGEROWS;
	//		break;
	//	default:
			mcType=ModelComponent.MCType.CROPPING;
	//	}
		return farm.getModelComponent(mcType);		
	}
	
	
	
	public static void applyToFarm(FarmerMOU mou,Farm farm){
		Map<ObjectiveType,Objective> farmObjs=farm.getObjectives();
		Stack<ObjectiveType> farmObjStack=new Stack<ObjectiveType>();
		farmObjStack.addAll(farmObjs.keySet());
	//	System.out.println(mou.weights.keySet());
		//System.out.println(farmObjs.keySet());
		try{
		for(ObjectiveType type:mou.weights.keySet()){
			if ( !farmObjs.containsKey(type)){
				throw new Error("Can't apply MOU to farm because ObjectiveType "+type+" is missing ");
			}
			Objective theObj=farmObjs.get(type);
			ObjectiveU objU=mou.objectives.get(type);
			// For curved objectives we assume the curve is correctly scales to give max utility of 100 and min of 0
			if ( objU.isCurved){
				theObj.setScaleFactor(mou.weights.get(type));	
				if ( objU.replacesObjective){
				//	System.out.println("Replacing curved objective"+objU.type+" with straight one");
					Objective theCurvedObj = new Objective(FarmerMOU.associatedModelComponent(theObj.type,farm),Types.xmlToObjectiveType("curved"+type.xmlname),theObj,objU.xVals(),objU.yVals());
					farm.addObjective(theCurvedObj);
					
				} else {
					throw new Error("Curved objective not replacing original .. should not happen");
					
				}
			} else {
				theObj.setScaleFactor(mou.weights.get(type)*mou.objectives.get(type).slope());
			}
			farmObjStack.remove(type);
		}
		if ( farmObjStack.size()>0){
			if ( farmObjStack.size()>1 || (farmObjStack.pop()!=ObjectiveType.SOS2DUMMY)   ){
				throw new Error("Some Objectives:  \n"+farmObjStack+" \n with unspecified weights when applying MOU");
			}
		}
		} catch ( Exception ex){
			ex.printStackTrace();
			throw new Error(ex.getMessage());
		}
		// Some special circumstances
		
		
		
	}
	
	
}
