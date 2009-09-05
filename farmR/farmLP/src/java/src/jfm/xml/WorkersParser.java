package jfm.xml;



import java.util.*;

import jfm.model.Worker;
import jfm.model.WorkersComponent;
import jfm.model.Types.*;


/** Parses a Workers object */
final class WorkersParser extends JFMObjectParser {
	
	public WorkersParser(ObjectParser parent_){	
		parent=parent_;

	}
	public void initializeObject() throws XMLSyntaxException,XMLObjectException{
		HashMap<WorkerSubType,Worker> workers=new HashMap<WorkerSubType,Worker>();
		for ( int w = 0 ; w < getParserList("labour").size(); w++){
			Worker wp = (Worker)getParserList("labour").get(w).getObject();
//			int index = getWorkerIndexFromId(wp.type);
			workers.put(wp.subType,wp);
		}
		for ( int w = 0 ; w < getParserList("machine").size(); w++){
			Worker wp = (Worker)getParserList("machine").get(w).getObject();
//			int index = getWorkerIndexFromId(wp.type);
			workers.put(wp.subType,wp);
		}		
		obj=new WorkersComponent(workers);
	}
	public String name(){ return "workers";};
	
	public String toString(){return "WorkersParser";};
	public String parsesNode(){ return "workers";};
	protected boolean isPrimitive(){ return false;};
}
