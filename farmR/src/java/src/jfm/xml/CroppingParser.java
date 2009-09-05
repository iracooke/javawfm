package jfm.xml;
import java.util.*;

import jfm.model.Crop;
import jfm.model.CroppingComponent;
import jfm.model.Disease;
import jfm.model.Limit;
import jfm.model.Rotation;
import jfm.model.Types.*;


/** Parses a cropping object */
final class CroppingParser extends JFMObjectParser {
	
	public CroppingParser(ObjectParser parent_){
		parent=parent_;
	}
	
	public void initializeObject() throws XMLSyntaxException,XMLObjectException {
		CroppingComponent objalias =new CroppingComponent();

		// Collect the diseases
		for ( int i = 0 ; i < getParserList("disease").size();i++){
			Disease dis=(Disease)getParserList("disease").get(i).getObject();
			objalias.addDisease(dis);
		}
		// Collect the rotation objects
/*		for ( int i = 0 ; i < getParserList("rotation").size();i++){
			Rotation rot=(Rotation)getParserList("rotation").get(i).getObject();
			objalias.addRotation(rot);
		}*/
		
		// collect crops and check diseases and rotations have been defined
		for ( int i = 0 ; i < getParserList("crop").size();i++){
			Crop cp=(Crop)getParserList("crop").get(i).getObject();
			objalias.addCrop(cp);
		}

		if ( parserListExists("limit")){
			for (int i=0;i<getParserList("limit").size();i++){
				Limit lim=(Limit)getParserList("limit").get(i).getObject();
				objalias.addLimit(lim);
			}
		}
		if ( !objalias.diseasesAndRotationsComplete()){
			throw new Error("Diseases and rotations not complete");
		}
		obj = objalias;
	}
	public String toString(){return "CroppingParser";};
	public String parsesNode(){ return "cropping";};
	protected boolean isPrimitive(){ return false;};
	public String name(){return "cropping";};
}
