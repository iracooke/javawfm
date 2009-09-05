/**
 * 
 */
package jfm.r;

import javax.xml.parsers.*;
import javax.xml.*;

import org.w3c.dom.*;

import java.io.*;

import jfm.xml.XMLObjectException;

/**
 * @author iracooke
 *
 */
public class MOUDocumentEditor {

	public static Document setObjectiveUAttribute(Document doc,String objName,String attName,String newAttValue){
		NodeList objectives = doc.getElementsByTagName("ObjectiveU");
		
		for(int n =0; n < objectives.getLength(); n++){
			Node obj = objectives.item(n);
			int typeAtt =DocumentEditor.findAttribute(obj,"type"); 
			if (typeAtt  == -1 ){
				throw new Error("type attribute not defined for an ObjectiveU node");
			}
			if ( obj.getAttributes().item(typeAtt).getNodeValue().equals(objName) ){
				int attId = DocumentEditor.findAttribute(obj,attName);
				if ( attId == -1 ){
					throw new Error("No "+attName+" attribute defined for "+objName);
				}
				obj.getAttributes().item(attId).setNodeValue(newAttValue);
				return doc;
			}
			
		}
		throw new Error("No "+objName+" Objective defined");
	}
	
	public static String getObjectiveUAttribute(Document doc,String objName,String attName){
		NodeList objectives = doc.getElementsByTagName("ObjectiveU");
		
		for(int n =0; n < objectives.getLength(); n++){
			Node obj = objectives.item(n);
			int typeAtt =DocumentEditor.findAttribute(obj,"type"); 
			if (typeAtt  == -1 ){
				throw new Error("type attribute not defined for an ObjectiveU node");
			}
			if ( obj.getAttributes().item(typeAtt).getNodeValue().equals(objName) ){
				int attId = DocumentEditor.findAttribute(obj,attName);
				if ( attId == -1 ){
					throw new Error("No "+attName+" attribute defined for "+objName);
				}
				return obj.getAttributes().item(attId).getNodeValue();
			}
			
		}
		throw new Error("No "+objName+" Objective defined");
	}
	
	
	public static String[] objectiveNames(Document doc){
		NodeList objectives = doc.getElementsByTagName("ObjectiveU");
		String[] names=new String[objectives.getLength()];
		for(int n =0; n < objectives.getLength(); n++){
			Node objective = objectives.item(n);
			int idAtt =DocumentEditor.findAttribute(objective,"type"); 
			if (idAtt  == -1 ){
				throw new Error("type attribute not defined for objective node");
			}
			names[n]=objective.getAttributes().item(idAtt).getNodeValue();
			
		}
		return names;
	}
	
	
	public static Document setRisk(Document doc,double newRiskValue){
		return setObjectiveUAttribute(doc,"varrisk","weight",Double.toString(newRiskValue));
	}
	
	public static Document setCropComplexity(Document doc,double newCCWeight,String newX){
		doc=setObjectiveUAttribute(doc,"cropcomplexity","weight",Double.toString(newCCWeight));
		return setObjectiveUAttribute(doc,"cropcomplexity","x",newX);
	}
	
}
