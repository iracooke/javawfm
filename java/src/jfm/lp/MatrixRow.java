package jfm.lp;
import java.util.*;

/** \internal Holds a set of MatrixElement objects corresponding to a single row of the constraint matrix. Each MatrixRow therefore constitutes a single constraint. */
public final class MatrixRow {
	public final double upperBound;
	public final double lowerBound;
	public final LPX type;
	public final int row;
	public final String setByComponent;
	private final Set<MatrixElement> elements =new LinkedHashSet<MatrixElement>();
	public final String tag;
	
	public Set<MatrixElement> elements(){
		return Collections.unmodifiableSet(elements);
	}
	
	public MatrixRow(double low,double up,LPX type_,int row_,String setBy,String tag_){
		upperBound=up;
		lowerBound=low;
		type=type_;
		row=row_;
		setByComponent=setBy;
		tag=tag_;
	}
	
	private void replaceElement(MatrixElement newElement){
		if (!elements.remove(newElement)){
			throw new Error("replaceElement should only be called on elements that already exist in the matrix");
		}
		// Adds the new element instead
		if (!elements.add(newElement)){
			throw new Error("This should not happen");
		}
	}
	/** At present this tries to support the use of zero valued elements to remove previously 
	 * set values */
	public boolean addElement(MatrixElement newElement){
		if ( newElement.value == 0 ){
			elements.remove(newElement); // Remove the element with this same column val
			return true;
		} else if (!elements.add(newElement)){
			replaceElement(newElement);
			return true;
		} 
		return true;
	}
}
