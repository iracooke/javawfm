/**
 * 
 */
package jfm.mou;

import java.util.HashMap;
import java.util.Map;

import jfm.xml.ObjectParser;
import jfm.xml.XMLObjectException;
import jfm.xml.XMLSyntaxException;
import jfm.mou.FarmerMOUParser;
import jfm.mou.ObjectiveUParser;

/**
 * @author iracooke
 *
 */
public abstract class MOUObjectParser extends ObjectParser {
	private static final int FarmerMOUParser =0;
	private static final int ObjectiveUParser=1;

	/** A map from the names of xml Elements to corresponding parsers */
	private static Map<String,Integer> parserTypes = new HashMap<String,Integer>();
	static {
		parserTypes.put(new FarmerMOUParser(null).parsesNode(), FarmerMOUParser);
		parserTypes.put(new ObjectiveUParser(null).parsesNode(),ObjectiveUParser);
	}
	/** Create a parser from the name of an xml element 
	 * @params name the name of the element for which a parser is to be created */
	public ObjectParser newNamedParser(String name) throws XMLSyntaxException,XMLObjectException{
		if ( !parserTypes.containsKey(name)){
			throw new XMLSyntaxException("The keyword "+name+" does not correspond to a parser");
		}
		int parserType = parserTypes.get(name);

		switch (parserType){
			case FarmerMOUParser: 
				return new FarmerMOUParser(this);
			case ObjectiveUParser:
				return new ObjectiveUParser(this);
			default: 
				throw new XMLObjectException("No parser defined for valid "+name+" already listed in parserTypes ");
		}		
	}

}
