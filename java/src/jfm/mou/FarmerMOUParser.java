/**
 * 
 */
package jfm.mou;

import jfm.xml.ObjectParser;
import jfm.xml.XMLObjectException;
import jfm.xml.XMLSyntaxException;
import jfm.mou.FarmerMOU;
import jfm.mou.ObjectiveU;

/**
 * @author iracooke
 *
 */
public class FarmerMOUParser extends MOUObjectParser {
	FarmerMOUParser(ObjectParser parent_){
		parent=parent_;
	}
	@Override
	public void initializeObject() throws XMLObjectException,
			XMLSyntaxException {
		FarmerMOU mou=new FarmerMOU();
		if ( parserListExists("ObjectiveU")){
			for ( ObjectParser objParser:getParserList("ObjectiveU")){
				ObjectiveU obj=(ObjectiveU)objParser.getObject();
				mou.addObjective(obj);
			}
		}
		obj=mou;
	}

	@Override
	protected boolean isPrimitive() {
		return false;
	}

	@Override
	public String name() {
		return "Farmer MOU";
	}

	@Override
	public String parsesNode() {
		// TODO Auto-generated method stub
		return "farmermou";
	}

	@Override
	public String toString() {
		return "FarmerMOUParser";
	}

}
