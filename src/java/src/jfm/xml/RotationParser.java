package jfm.xml;

import java.util.HashMap;
import java.util.*;

import jfm.model.Rotation;
import jfm.model.Types;
import jfm.model.Types.*;


/** Parses a Rotation Object */
final class RotationParser extends JFMObjectParser {
	CropType id;
	public RotationParser(ObjectParser parent_){
		parent=parent_;
		registerAttribute("id",JFMObjectParser.mandatoryAttribute);
	}
	@Override
	public void initializeObject() throws XMLObjectException, XMLSyntaxException {
		id = Types.xmlToCropType(this.getNamedAttribute("id"));
		HashMap<DiseaseType,double[]> rotpenaltymap=new HashMap<DiseaseType,double[]>();
		Set<DiseaseType> forbid=new HashSet<DiseaseType>();
	//	System.out.println("Parsing rotation "+id);
		
		if ( this.parserListExists("csv")){
			try {
				for ( int i = 0 ; i < getParserList("csv").size();i++){
					TextParser csvp = (TextParser)getParserList("csv").get(i);
					String[] vals=csvp.getCSV();
					switch (csvp.dataType){
					case ROTPENALTY:
						DiseaseType dt = Types.xmlToDiseaseType(vals[0]);
						double[] rotcosts=new double[2];
						rotcosts[0]=Double.parseDouble(vals[1])*0.01;// Percentage yield cost
						rotcosts[1]=Double.parseDouble(vals[2]);
						rotpenaltymap.put(dt, rotcosts);
						break;
					case FORBIDDEN:
						for ( String str:vals){
							forbid.add(Types.xmlToDiseaseType(str));
						}
						break;
					default:
						throw new XMLSyntaxException("only rotpenalty or forbidden csv type allowed for rotations");
					}
				}
			} catch ( NumberFormatException ex ){
				throw new XMLSyntaxException(ex.getMessage());
			} 
		}
		obj=new Rotation(id,rotpenaltymap,forbid);
	}


	protected boolean isPrimitive() {
		return false;
	}

	@Override
	public String name() {
		if ( id!= null){
			return id.xmlname;
		} else {
			return "rotation";
		}
	}

	@Override
	public String parsesNode() {
		return "rotation";
	}

	@Override
	public String toString() {
		return "RotationParser";
	}

}
