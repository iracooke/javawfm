package jfm.lp;


/** \internal Holds the value and column index for a single matrix element. 
 * MatrixElement objects should typically be grouped into MatrixRow objects */
public final class MatrixElement implements Comparable<MatrixElement> {
//	protected final int rowIndex;
	public final int colIndex;
	public final double value;
	
	
	
	public MatrixElement(int c,double v){ 
//		rowIndex=r;
		colIndex=c;
		value=v;
	};
	public boolean equals(Object other){
		if ( other instanceof MatrixElement){
			MatrixElement oth=(MatrixElement)other;
			if ( oth.colIndex==colIndex){
				return true;
			} 
		}
		return false;
	}
	public int hashCode(){
//		int hash=7;
//		hash=31*hash+rowIndex;
//		hash=31*hash+colIndex;
		return colIndex;
	}
	public int compareTo(MatrixElement to){
		if (to.colIndex<this.colIndex){ 
			return 1;
		} 
		if ( to.colIndex>this.colIndex){
			return -1;
		} else {
			return 0;
		}
	}
}