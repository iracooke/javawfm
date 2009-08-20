package jfm.lp;

import jfm.xml.XMLSyntaxException;
import java.util.*;

public abstract class LPPeer {
	public enum Solver {
		GLPK("glpk"),CLP("cbc");
		public final String xmlName;
		Solver(String xmln){
			xmlName=xmln;
		}
	}
	public final Solver type;
	protected LPPeer(Solver type_){
		type=type_;
	}
	public static LPPeer create(Matrix matrix_,Solver slvr){
		switch (slvr){
		case GLPK:
			return (LPPeer)(new GLPKPeer(matrix_));
		case CLP:
			return (CBCPeer)(new CBCPeer(matrix_));
//			throw new Error("CLP Solver Not Implemented");
		default:
			throw new Error("Unreckognised Solver type"+slvr);
		}
	}
	private static Map<String,Solver> xmlToSolverType=new HashMap<String,Solver>();
	static{
		for(Solver v:Solver.values()){
			xmlToSolverType.put(v.xmlName, v);
		}
	}
	public static Solver xmlToSolverType(String str) throws XMLSyntaxException {
		if (xmlToSolverType.containsKey(str)){
			return xmlToSolverType.get(str);
		} else {
			throw new XMLSyntaxException("The string "+str+" does not correspond to a solver type");		
		}
	}
	
	// Interface methods
	public abstract void setTermOut(LPX onoff);
	protected abstract LPX solve();
}
