/**
 * 
 */
package jfm.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author iracooke
 *
 */
public abstract class ObjectParser {
	/** A reference to the parent parser object */
	protected ObjectParser parent;
	/** If the xml element corresponding to this object encloses text it will be stored here*/
	protected String textContent = null;
	/** The concrete object being built by the parser */
	protected Object obj=null;
	/** A map of child parser names to lists of child parser objects */
	private Map<String,ArrayList<ObjectParser>> childParsers =new HashMap<String,ArrayList<ObjectParser>>();
	/** A map of attribute names to an integer specifying whether they are mandatory or not*/
	private Map<String,Integer> attributeTypes = new HashMap<String,Integer>();
	/** A map of attribute names to values (values are kept as strings) */
	private Map<String,String> attributeValues= new HashMap<String,String>();
	/** Access the object being parsed */
	public Object getObject(){
		return obj;
	}

	protected static int mandatoryAttribute = 0;
	protected static int optionalAttribute = 1;
	

	public abstract ObjectParser newNamedParser(String name) throws XMLSyntaxException,XMLObjectException;
	
	
	
	protected String printNameAndAttributes(){
		StringBuffer buff = new StringBuffer();
		buff.append(parsesNode()+" "+attributeValues);
		return buff.toString();
	}
	protected String printParsingTrace(){
		StringBuffer buff = new StringBuffer();
		if ( parent!=null){
			buff.append("\n      at depth "+printNameAndAttributes());
			return buff.append(parent.printParsingTrace()).toString();
		} else {
			return ("\n      at depth "+printNameAndAttributes());
		}
	}
	
	protected ObjectParser getParent(){return parent;};
	/** Searches up the ObjectParser tree until it finds an ObjectParser that was defined with a null parent. 
	 * Returns that object as the root of the tree */
	protected ObjectParser getRootParent(){
		if ( parent != null ){
			return parent.getRootParent();
		} else {
			return this;
		}
	}
	
	/** asks if this parser corresponds to a primitive parser. 
	 * A primitive parser contains no child Elements but may contain text */
	protected abstract boolean isPrimitive();
	
	/** Traverses the parsing tree to discover the depth of the current node*/
	protected int getNodeDepth(int start){
		if ( parent != null){
			return parent.getNodeDepth(start++);
		} else {
			return start;
		}
	}
	/** Constructs the concrete object being parsed. This should be called after 
	 * all child Elements have been parsed so that the children can be used in 
	 * constructing the parent */
	public abstract void initializeObject() throws XMLObjectException,XMLSyntaxException;
	protected void registerAttribute(String name,int type){
//		Message.debug("Registered attribute "+name+" "+attributeTypes, this);
		attributeTypes.put(name, type);
	}
	
	protected ArrayList<ObjectParser> getParserList(String name) throws XMLObjectException {
		if ( !childParsers.containsKey(name)){			
			throw new XMLObjectException("Attempt to access undefined parser "+name+" of "+this.toString());
		}
		return childParsers.get(name);
	}
	protected boolean parserListExists(String name){
		return childParsers.containsKey(name);
	}
	
/*	private String printHashMap(HashMap map){
		StringBuffer buff = new StringBuffer();
		Set<String> keys = map.entrySet();
		
	}
*/	
	private void getAndSetAttributes(Node e) throws XMLSyntaxException {

		/** Get the attributes for the node associated with this parser 
		 * and then for each attribute set its value, provided it has been registered */
		NamedNodeMap attr = e.getAttributes();
		HashMap<String,Integer> attributes = new HashMap<String,Integer>();
		attributes.putAll(attributeTypes);
//		Message.verbose(Message.high, attr.getLength()+" attributes for "+e.getNodeName(), this);		
		for ( int nd = 0 ; nd < attr.getLength(); nd++){
			String name=attr.item(nd).getNodeName();
			String val = attr.item(nd).getNodeValue();
			if ( attributeTypes.containsKey(name)){
				attributeValues.put(name,val);
				attributes.remove(name);
//				Message.debug("removed att "+name+" size "+attributes.size()+" "+attributes, this);
			} else {
				StringBuffer abuff=new StringBuffer();
				for ( int i = 0 ; i< attr.getLength(); i++){
					abuff.append("\n"+attr.item(i));
				}
				throw new XMLSyntaxException("Attribute "+name+
						" is not supported for node "+e.getNodeName()+"\n"+
						attributes+abuff+"\n");
			}
		}
		if (attributes.containsValue(mandatoryAttribute)){
			
			throw new XMLSyntaxException("Some mandatory attributes not specified when parsing "+e.getNodeName());
		}

	}
	public boolean attributeHasValue(String name){
		return attributeValues.containsKey(name);
	}
	
	protected String getNamedAttribute(String name) throws XMLObjectException {
		if ( !attributeValues.containsKey(name)){
			throw new XMLObjectException("Attempt to get attribute "+name+" not associated with object "+this.toString());
		}
		return attributeValues.get(name);
	}
	/*
	public double getNamedFloatAttribute(String name) throws Exception {
		if ( !floatAttrMap.containsKey(name)){
			throw new Exception();
		}
		return floatAttrMap.get(name);
	}
	*/
	public abstract String parsesNode();
	private boolean nodeValidForParser(Node e) throws XMLSyntaxException{
		String nodeName = e.getNodeName();
		if ( !this.parsesNode().contentEquals(nodeName)){
			throw new XMLSyntaxException("Attempt to parse node "+nodeName+" with "+this.toString());
		}
		return true;
	}
	

	private void addParser(ObjectParser newParser,String name){
		// First check to see if parsers of this type exist yet and if not create a list for them
		if (!childParsers.containsKey(name)){
			childParsers.put(name, new ArrayList<ObjectParser>());
		}
		ArrayList<ObjectParser> parserList = childParsers.get(name);
		parserList.add(newParser);
	}
	private void getText(Element e) throws XMLObjectException {
		if (textContent!=null){ throw new XMLObjectException("This element contains more than one text node");};
		for (Node child = e.getFirstChild(); 
        child != null;
        child = child.getNextSibling())
		{
			short type = child.getNodeType();
			if ( type == Node.TEXT_NODE ){
				textContent=child.getNodeValue();
			}
		}
	}

	public void parse(Element e) throws XMLSyntaxException,XMLObjectException {
//		long startmem =0;
//		if ( this instanceof ModelParser ){
			//		Message.verbose(Message.high, "parsing "+e.getNodeName(), this);
//			startmem=relu.utils.MemoryMeasurer.getUsedMemory();
//		}
	try {
		nodeValidForParser(e);

		getAndSetAttributes(e);			
		// If the parser is for a primitive then just get the text otherwise look for children
		if ( isPrimitive() ){
			getText(e);
		} else {
			NodeList childNodes = e.getChildNodes();
			for ( int i = 0;i < childNodes.getLength();i++){			
				if ( childNodes.item(i) instanceof Element ){				
					Element child = (Element)childNodes.item(i);
//					Message.verbose(Message.high, "Child "+child.getNodeName()+" of "+e.getNodeName(), this);
				
//					short type = child.getNodeType();
					String childName = child.getNodeName();
					ObjectParser childParser = newNamedParser(childName);
//					if ( childParser.parent == null ){
//						System.out.println("New child "+childParser.toString()+" with name "+childName+" has null parent ");	
//					}
//					System.out.println("New child "+childParser.toString()+" with name "+childName+" has parent "+childParser.parent.toString());
					addParser(childParser,childName);
					childParser.parse(child);
				}
			}
		}
		
		//---
		
//		Message.verbose(Message.high, "initializing Object "+e.getNodeName(), this);
		initializeObject();
//		Message.verbose(Message.high, "done parsing "+e.getNodeName(), this);
		} catch (XMLException ex) { // Catches both object and syntax exceptions
			if ( parent != null ){
				System.out.print("at "+e.getNodeName()+"  "+this.name()+" ");
				System.out.println(ex.getMessage());
				if ( ex.getClass() == XMLSyntaxException.class){
					throw new XMLSyntaxException("");
				} else {
					throw new XMLObjectException("");
				}
			} else {
				System.out.print("at "+e.getNodeName()+"  ");
				System.out.println(ex.getMessage());
				if ( ex.getClass() == XMLSyntaxException.class){
					throw new XMLSyntaxException("");
				} else {
					throw new XMLObjectException(ex.getMessage());
				}
			}
		}
//		if ( this instanceof ModelParser ){
//			long endmem=relu.utils.MemoryMeasurer.getUsedMemory();
//			System.out.println("Used "+(endmem-startmem)+" when allocating "+e.getNodeName());
//		}
//		System.out.println("Created "+e.getNodeName());
	}
	public abstract String name();
	public abstract String toString();
	
}
