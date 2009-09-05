package jfm.xml;

import java.util.*;

import jfm.lp.LPX;
import jfm.model.Limit;
import jfm.model.Types;
import jfm.model.Types.*;


/** Parses a limit object */
final class LimitParser extends JFMObjectParser {
	LimitType id;
	
	public LimitParser(ObjectParser parent_){
		parent=parent_;
		registerAttribute("id",JFMObjectParser.mandatoryAttribute);
//		registerAttribute("subjects",ObjectParser.mandatoryAttribute);
		registerAttribute("min",JFMObjectParser.mandatoryAttribute);
		registerAttribute("max",JFMObjectParser.mandatoryAttribute);
		registerAttribute("type",JFMObjectParser.mandatoryAttribute);
	}
	@Override
	public void initializeObject() throws XMLObjectException, XMLSyntaxException {
		double min=0;
		double max=0;
		LPX type=LPX.LPX_LO;
		ArrayList<Object> subjects=new ArrayList<Object>();
		try{
			id=Types.xmlToLimitType(this.getNamedAttribute("id"));
			min=Double.parseDouble(this.getNamedAttribute("min"));
			max=Double.parseDouble(getNamedAttribute("max"));
			type=LPX.xmlToGLPKType(getNamedAttribute("type"));
			if ( this.parserListExists("csv")){
				for ( int i = 0 ; i < getParserList("csv").size();i++){
					TextParser csvp = (TextParser)getParserList("csv").get(i);
					if ( csvp.dataType==Types.TextDataType.CROPTYPE){
						String[] xmlcptypes=csvp.getCSV();
						for( int j=0;j<xmlcptypes.length;j++){
							subjects.add(Types.xmlToCropType(xmlcptypes[j]));
						}
					}
				}
			} else {
				throw new XMLSyntaxException("Subjects must be defined for limits");
			}
		} catch (XMLSyntaxException ex){
			throw new XMLSyntaxException(ex.getMessage());
		} catch (NumberFormatException ex){
			throw new XMLSyntaxException(ex.getMessage());
		}
		obj=new Limit(subjects,id,type,min,max);
	}

	@Override
	protected boolean isPrimitive() {
		return false;
	}

	@Override
	public String name() {
		return "LimitParser";
	}

	@Override
	public String parsesNode() {		
		return "limit";
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
