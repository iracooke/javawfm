/**
 * 
 */
package jfm.r;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.*;

/**
 * Defines a bunch of static functions for directly modifying xml farm description objects
 * @author iracooke
 *
 */
public class FarmDocumentEditor {
	public static String dump(Document doc){
		return(DocumentEditor.printNode(doc, " "));
	}
	
	public static String[] cropNames(Document doc){
		NodeList crops = doc.getElementsByTagName("crop");
		String[] names=new String[crops.getLength()];
		for(int n =0; n < crops.getLength(); n++){
			Node crop = crops.item(n);
			int idAtt =DocumentEditor.findAttribute(crop,"id"); 
			if (idAtt  == -1 ){
				throw new Error("id attribute not defined for crop node");
			}
			names[n]=crop.getAttributes().item(idAtt).getNodeValue();

		}
		return names;
	}
	public static Node getNodeForCropAttribute(Document doc,String cropName,String attName){
		NodeList crops = doc.getElementsByTagName("crop");
		
		for(int n =0; n < crops.getLength(); n++){
			Node crop = crops.item(n);
			int idAtt =DocumentEditor.findAttribute(crop,"id"); 
			if (idAtt  == -1 ){
				throw new Error("id attribute not defined for crop node");
			}
			if ( crop.getAttributes().item(idAtt).getNodeValue().equals(cropName) ){
				int attId = DocumentEditor.findAttribute(crop,attName);
				if ( attId == -1 ){
					throw new Error("No attribute "+attName+" defined for crop");
				}
				return crop.getAttributes().item(attId);
			}		
//			System.out.println(crop);
	//		System.out.println(cropName);
		}
		throw new Error("No crop named "+cropName);		
	}
	
	
	
	public static ArrayList<Node> getNodesForAttributeOfTagFilteredByAttribute(Document doc,String tagName,String filterAttribute,String filterAttributeValue,String attName){
	//	System.out.println(tagName+" "+filterAttribute+" "+filterAttributeValue+" "+attName);
		ArrayList<Node> nodes = new ArrayList<Node>();
		NodeList tags = doc.getElementsByTagName(tagName);		
		for ( int n=0;n<tags.getLength();n++){
			Node tag = tags.item(n);
			int attId = DocumentEditor.findAttribute(tag, filterAttribute);
			if (attId  != -1 || filterAttribute.equals("nil")){
//				System.out.println(tag.getAttributes().item(attId).getNodeValue());
				if ( filterAttribute.equals("nil") || tag.getAttributes().item(attId).getNodeValue().equals(filterAttributeValue)  ){
//					System.out.println(tag.getNodeName());	
					int updatedAttId = DocumentEditor.findAttribute(tag, attName);
					if ( updatedAttId != -1 ){
						nodes.add(tag.getAttributes().item(updatedAttId));
					}
				}
			}	
		}
		return nodes;
	}
	
	
	
	public static Document setRelativeAttributeOfTagFilteredByAttribute(Document doc,String tagName,double multiplier,String filterAttribute,String filterAttributeValue,String attName){
		ArrayList<Node> nodes = getNodesForAttributeOfTagFilteredByAttribute(doc,tagName,filterAttribute,filterAttributeValue,attName);
		for( Node n: nodes){
			double oldval=Double.parseDouble(n.getNodeValue());
//			System.out.println("Setting "+n.getNodeName()+attName+" of "+filterAttribute+ " "+tagName+" to "+multiplier*oldval);
			n.setNodeValue(Double.toString(multiplier*oldval));
		}
		return doc;
	}
	
	public static Document setAttributeOfTagFilteredByAttribute(Document doc,String tagName,String newValue,String filterAttribute,String filterAttributeValue,String attName){
		ArrayList<Node> nodes = getNodesForAttributeOfTagFilteredByAttribute(doc,tagName,filterAttribute,filterAttributeValue,attName);
		for( Node n: nodes){
			n.setNodeValue(newValue);
		}
		return doc;
	}
	
	public static Document setRelativeWorkrateFormulas(Document doc,double multiplier){
//		ArrayList<Node> nodes = getNodesForAttributeOfTagFilteredByAttribute(doc,"csv","data","wkrate","wkrate");
		NodeList tags = doc.getElementsByTagName("csv");
		for( int n=0;n<tags.getLength();n++){
			Node csvNode=tags.item(n);
//			System.out.println(csvNode.getChildNodes().item(0).getNodeValue());
			int attId = DocumentEditor.findAttribute(csvNode, "data");
			if ( attId!=-1 && csvNode.getAttributes().item(attId).getNodeValue().equals("wkrate")){			
				String oldval=csvNode.getChildNodes().item(0).getNodeValue();
				csvNode.getChildNodes().item(0).setNodeValue("("+Double.toString(multiplier)+"*"+"("+oldval+"))");
//				System.out.println("New workrate is "+csvNode.getChildNodes().item(0).getNodeValue());
			}
		}
		return doc;
	}
	
	public static Document setRelativeRotationPenalties(Document doc,double multiplier){
		ArrayList<Node> nodes = getNodesForAttributeOfTagFilteredByAttribute(doc,"crop","nil","nil","yieldReduction");
		nodes.addAll(getNodesForAttributeOfTagFilteredByAttribute(doc,"crop","nil","nil","selfRotCost"));
		for(Node n : nodes){
			String csvtext = n.getNodeValue();
			String[] csvitems= csvtext.split(",");
			StringBuffer newcsvText=new StringBuffer();
			newcsvText.append(String.valueOf(Double.parseDouble(csvitems[0])*multiplier));
			for(int i=1;i<csvitems.length;i++){
				newcsvText.append(","+String.valueOf(Double.parseDouble(csvitems[i])*multiplier));
			}
			n.setNodeValue(newcsvText.toString());			
		//	System.out.println(n.getNodeValue());
		}
		return doc;
		
	}
	
	
	public static Document setAttributeForCrop(Document doc,double newValue,String cropName,String attName){		
		Node n=getNodeForCropAttribute(doc,cropName,attName);
		n.setNodeValue(Double.toString(newValue));
		return doc;
	}
	
	public static Document setStringAttributeForCrop(Document doc,String newValue,String cropName,String attName){		
		Node n=getNodeForCropAttribute(doc,cropName,attName);
		n.setNodeValue(newValue);
		return doc;
	}
	
	public static double getDoubleAttributeForCrop(Document doc,String cropName,String attName){
		Node n=getNodeForCropAttribute(doc,cropName,attName);
		return Double.parseDouble(n.getNodeValue());
	}
	
	public static String getStringAttributeForCrop(Document doc,String cropName,String attName){
		Node n=getNodeForCropAttribute(doc,cropName,attName);
		return n.getNodeValue();
	}
	
	public static Document setRelativePriceForCrop(Document doc,double priceChange,String cropName){
		double oldPrice = getDoubleAttributeForCrop(doc,cropName,"price");
		return setAttributeForCrop(doc,oldPrice*priceChange,cropName,"price");
	}
		
	public static Document setMultiplierOnYieldFormulaForCrop(Document doc,double priceChange,String cropName){
		String form=getStringAttributeForCrop(doc,cropName,"yield");
		form="("+String.valueOf(priceChange)+"*"+form+")";
	//	System.out.println(form);
		setStringAttributeForCrop(doc,form,cropName,"yield");
		return doc;
	}
	
	public static Document setPriceForCrop(Document doc,double newPrice,String cropName){
		return setAttributeForCrop(doc,newPrice,cropName,"price");
	}
	
	public static Document setSubsidyForCrop(Document doc,double newSubsidy,String cropName){
		return setAttributeForCrop(doc,newSubsidy,cropName,"subsidy");
	}
}
