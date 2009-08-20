package jfm.model;


import jfm.model.Types.CropType;

/** \internal Identifier class for a particular crop in a particular year. 
 * Typically used as a key in hashmaps 

 * <em> immutable </em>
 *
 * @author Ira Cooke */
final class CropYear {
	public final CropType base;
	public final int copyYear;
	CropYear(CropType b,int y){
		base=b;
		copyYear=y;
	}
	public boolean equals(Object other){
		if ( this == other ){ return true;};
		if ( other instanceof CropYear){
			CropYear oth=(CropYear)other;
			if ( oth.base==base&& oth.copyYear==copyYear){
				return true;
			} 
		}
		return false;
	}
	public int hashCode(){
		int hash=7;
		hash=31*hash+base.hashCode();
		hash=31*hash+copyYear;
		return hash;
	}

	
}