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
	
	
			   
	public static String printNode(Node node, String indent)  {
		StringBuffer buff=new StringBuffer();
        switch (node.getNodeType()) {
				
            case Node.DOCUMENT_NODE:
                buff.append("<xml version=\"1.0\">\n\n");
                // recurse on each child
                NodeList nodes = node.getChildNodes();
                if (nodes != null) {
                    for (int i=0; i<nodes.getLength(); i++) {
                        buff.append(printNode(nodes.item(i), indent + "  "));
                    }
                }
                break;
                
            case Node.ELEMENT_NODE:
                String name = node.getNodeName();
                buff.append(indent + "<" + name);
                NamedNodeMap attributes = node.getAttributes();
                for (int i=0; i<attributes.getLength(); i++) {
                    Node current = attributes.item(i);
                    buff.append(
									 " " + current.getNodeName() +
									 "=\"" + current.getNodeValue() +
									 "\"");
                }
                buff.append(">");
                
                // recurse on each child
                NodeList children = node.getChildNodes();
                if (children != null) {
                    for (int i=0; i<children.getLength(); i++) {
                        buff.append(printNode(children.item(i), indent + "  "));
                    }
                }
                
                buff.append("</" + name + ">");
                break;
				
            case Node.TEXT_NODE:
                buff.append(node.getNodeValue());
                break;
		}
//		buff.append("\n");
		return buff.toString();
    }    
    
	
	
	
}


