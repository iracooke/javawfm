package jfm.xml;

public class FreeTimeParser extends JFMObjectParser {
	FreeTimeParser(ObjectParser parent_){
		parent=parent_;
	}
	@Override
	public void initializeObject() throws XMLObjectException,
			XMLSyntaxException {
		obj=new jfm.model.FreeTimeComponent();

	}

	@Override
	protected boolean isPrimitive() {
		return false;
	}

	@Override
	public String name() {
		return "Free Time Parser";
	}

	@Override
	public String parsesNode() {
		return "freetime";
	}

	@Override
	public String toString() {
		return "FreeTime";
	}

}
