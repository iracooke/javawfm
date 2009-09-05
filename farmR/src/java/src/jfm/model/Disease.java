package jfm.model;


import jfm.model.Types.*;

/** Definition associated with particular DiseaseType. 
 * Defines the minimum number of years break required between crops of its base or associated 
 * DiseaseType 
 * 
 * <em> immutable </em>
 * @author Ira Cooke */
public final class Disease {
	public final int minBreak; // minimum break between crops of this class
	public final DiseaseType base;
	public final DiseaseType associated;
	/** Construct a disease object. 
	 * @param base_ The base DiseaseType 
	 * @param assoc The associated DiseaseType 
	 * @param minB The minimum number of years break between crops of this disease class */
	public Disease(DiseaseType base_,DiseaseType assoc,int minB) {
		base=base_;
		if ( assoc == null){
			assoc=Types.DiseaseType.NONE;
		}

		associated=assoc;
		minBreak=minB;
	}
	/** Return a clone of this disease object. This is a complete deep copy */
	public Disease copy(){
		return new Disease(base,associated,minBreak);
	}
} 
