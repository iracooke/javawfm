package jfm.model;

import java.util.*;

import jfm.lp.LPX;
import jfm.model.Types.*;


/** A simple constraint which can be built by the user.
 *  
 * At present the only type of Limit constraints are those on crop areas. 
 * 
 * <em> immutable </em>
 * 
 * @author Ira Cooke
 * */
public final class Limit {
	public final LPX type;
	public final double min;
	public final double max;
	public final LimitType limitType;
	private List<Object> subjectIdentifiers; // This gets made immutable in the constructor 
	
	/** Construct a new Limit on the area of a specified crop. 
	 * @param cptype The CropType on which this limit applies 
	 * @param glpktype One of LPX.LPX_FX, LPX.LPX_UP or LPX.LPX_LO specifying the type of constraint. 
	 * @param min_ The minimum area (not used if glpktype equals LPX.LPX_UP)
	 * @param max_ The maximum area (not used if glpktype equals LPX.LPX_LO ) 
	 * */
	public static Limit CropAreaLimit(CropType cptype,LPX glpktype,double min_,double max_){
		List<Object> subjects=new ArrayList<Object>();
		subjects.add((Object)cptype);
		return new Limit(subjects,LimitType.AREA,glpktype,min_,max_);
	}
	/** Construct an area Limit on the area of a several crops. 
	 * @param cptypes A list of the CropType on which this limit applies 
	 * @param glpktype One of LPX.LPX_FX, LPX.LPX_UP or LPX.LPX_LO specifying the type of constraint. 
	 * @param min_ The minimum area (not used if glpktype equals LPX.LPX_UP)
	 * @param max_ The maximum area (not used if glpktype equals LPX.LPX_LO ) 
	 * */
	public static Limit CropAreaLimit(Set<CropType> cptypes,LPX glpktype,double min_,double max_){
		List<Object> subjects=new ArrayList<Object>();
		for(CropType cptype:cptypes){
			subjects.add((Object)cptype);
		}
		return new Limit(subjects,LimitType.AREA,glpktype,min_,max_);
	}
	
	
	/** Construct an arbitrary type of limit */
	public Limit(List<Object> subjects_,LimitType lt,LPX type_,double min_,double max_){
		subjectIdentifiers=Collections.unmodifiableList(subjects_);
		type=type_;
		min=min_;
		max=max_;
		limitType=lt;
	}
	List<CropType> getCropSubjects(){
		List<CropType> retlist=new ArrayList<CropType>();
		for(Object obj:subjectIdentifiers){
			if ( obj instanceof CropType){
				retlist.add((CropType)obj);
			} else {
				throw new Error("Limit subjects should be of a uniform object type");
			}
		}
		return retlist;
	}
	public Limit copy(){
		return new Limit(subjectIdentifiers,limitType,type,min,max);
	}
}
	

