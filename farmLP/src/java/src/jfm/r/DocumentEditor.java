package jfm.r;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class DocumentEditor {
	public static int findAttribute(Node node,String attName){
		NamedNodeMap attributes=node.getAttributes();
		for( int i=0;i<attributes.getLength();i++){
			String name=attributes.item(i).getNodeName();
			if ( name.equals(attName)){
				return i;
			}
		}
		return -1;
	}
	
	
}
