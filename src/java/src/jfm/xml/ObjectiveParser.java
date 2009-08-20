package jfm.xml;

import jfm.lp.Objective;
import jfm.model.Types;
import jfm.model.Types.ObjectiveType;

/** Parses and Objective object */
final class ObjectiveParser extends JFMObjectParser {
	ObjectiveType id;
	
	public ObjectiveParser(ObjectParser parent_){
		parent=parent_;
		registerAttribute("id",JFMObjectParser.mandatoryAttribute);
	}
	@Override
	public void initializeObject() throws XMLObjectException, XMLSyntaxException {
		try {
			id = Types.xmlToObjectiveType(this.getNamedAttribute("id"));
		} catch ( XMLSyntaxException ex ){
			ex.printStackTrace();
			throw new XMLSyntaxException(ex.getMessage());
		}
		obj=new Objective(id);
	}

	@Override
	protected boolean isPrimitive() {
		return false;
	}

	@Override
	public String name() {
		if ( id!=null){
			return id.xmlname;
		}else {
			if ( this.attributeHasValue("id")){
				try {
					return this.getNamedAttribute("id");
				} catch (XMLObjectException ex){
					throw new Error(ex.getMessage());
				}
			}
			return "objective";
		}
	}

	@Override
	public String parsesNode() {
		return "objective";
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "ObjectiveParser";
	}

}
