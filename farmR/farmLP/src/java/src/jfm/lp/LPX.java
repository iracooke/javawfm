package jfm.lp;

import java.util.HashMap;

import jfm.xml.XMLSyntaxException;

/** \internal row and column bound types defined in the C headers for glpk 
 *  It is essential to ensure that the same integer 
 *  values are used in this class and in the header file glpk.h. A quick check should be 
 *  made whenever upgrading to a new version of glpk. This class is valid for glpk-4.23 */
public enum LPX {
	/** Free variable */
	LPX_FR (110,"lpx_fr"),
	/** Variable with lower bound */
	LPX_LO (111,"lpx_lo"),
	/** Variable with upper bound */
	LPX_UP (112,"lpx_up"),
	/** Double bounded (upper and lower) variable */
	LPX_DB (113,"lpx_db"),
	/** Fixed variable */
	LPX_FX (114,"lpx_fx"),
	
	LPX_LP (100,"lpx_lp"),
	LPX_MIP (101,"lpx_mip"),
	LPX_CV (160,"lpx_cv"),
	LPX_IV (161,"lpx_iv"),
	
	LPX_TERMON (1,"lpx_termon"),
	LPX_TERMOFF(0,"lpx_termoff"),
	
	// Status variables 
	LPX_NOTOPT(179,"lpx_notopt"),
	LPX_OPT (180,"lpx_opt"),// solution is optimal
	LPX_FEAS (181,"lpx_feas"), //solution is feasible; 
	LPX_INFEAS (182,"lpx_infeas"),//solution is infeasible; 
	LPX_NOFEAS (183,"lpx_nofeas"),// problem has no feasible solution; 
	LPX_UNBND (184,"lpx_unbnd"),//problem has unbounded solution; 
	LPX_UNDEF (185,"lpx_undef"); 

	
	/** The integer corresponding to this enum which corresponds to the definition in glpk.h */
	private final int toCPP;
	public final String xmlname;
	private LPX(int intequiv,String xmln){
		toCPP=intequiv;
		xmlname=xmln;
	}
	/** The integer value corresponding to this LPX row or column type to 
	 * be used in C++ code */
	public int toCPP(){return toCPP;};

	/** Return the LPX enum corresponding to an xml string of the same name */
	public static LPX xmlToGLPKType(String str) throws XMLSyntaxException {
		if (xmlToGLPKType.containsKey(str)){
			return xmlToGLPKType.get(str);
		} else {
			throw new XMLSyntaxException("The string "+str+" does not correspond to a LPX type");		
		}
	}
	public static LPX intToGLPKType(int iglpk) {
		if (intToGLPKType.containsKey(iglpk)){
			return intToGLPKType.get(iglpk);
		} else {
			throw new Error("The integer "+iglpk+" does not correspond to a LPX type");		
		}
	}
	
	private static HashMap<Integer,LPX> intToGLPKType=new HashMap<Integer,LPX>();
	static {
		for(LPX lt:LPX.values()){
			intToGLPKType.put(lt.toCPP, lt);
		}
	}
	
	private static HashMap<String,LPX>xmlToGLPKType =new HashMap<String,LPX>();
	static{
		for(LPX lt:LPX.values()){
			xmlToGLPKType.put(lt.xmlname, lt);
		}
	}
}
