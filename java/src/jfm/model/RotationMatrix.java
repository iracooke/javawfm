package jfm.model;
import jfm.model.Types.CropType;
import jfm.utils.*;

import java.text.DecimalFormat;
import java.util.*;


public class RotationMatrix {
	public final int size;
//	private final Map<CropType,Map<CropType,Double>> matrix=new HashMap<CropType,Map<CropType,Double>>();
	public final double[][] rmatrix;
	public final double[] rvector; // Proportions of each crop
	private final List<CropType> cropTypes=new ArrayList<CropType>();
	public List<CropType> cropTypes(){
		return Collections.unmodifiableList(cropTypes);
	}
	
	/** Given a random number between 0 and 1 and the previous crop type, pick a new crop 
	 * from the rotation matrix for this farm */
	public CropType pickNewCrop(CropType prev,double random){
		int fi=-1;
		if ( prev==null || !cropTypes.contains(prev)){ // First time we need to allocate a crop 
			JFMMath.doubleZero(rvector); // Create the cropping vector
			// Need an initial crop 
			for(int i=0;i<rmatrix.length;i++){
				for(int j=0;j<rmatrix.length;j++){
					rvector[i]+=rmatrix[i][j];
				}
			}
			fi=JFMMath.chooseWeightedRandom(rvector, random);
		} else {
			fi=cropTypes.indexOf(prev);
		}
		CropType next=null;
		double[] pickP=new double[rmatrix[fi].length];
		double totFi=JFMMath.sum(rmatrix[fi]);
		if ( totFi == 0 ){
			throw new Error("Rotation matrix should not contain any zero columns or rows \n"+
					this.toString()+"\n");
		}
		pickP[0]=rmatrix[fi][0]/totFi;
		for( int i=1;i<pickP.length;i++){
			pickP[i]=pickP[i-1]+rmatrix[fi][i]/totFi;						
			if ( pickP[i]>= random && next==null){
				next=cropTypes.get(i);
			}
		}
		if ( !JFMMath.isZero(pickP[pickP.length-1]-1)){
			throw new Error("rmatrix unnormalizable?!");
		}
		return next;
	}
	
	public RotationMatrix(Set<CropType> crops){
		size=crops.size();
		cropTypes.addAll(crops);
		rmatrix=new double[size][size];
		rvector=new double[size];
		JFMMath.doubleZero(rvector);
		JFMMath.doubleZero(rmatrix);
		// Create zero padded matrix
//		for ( int f=0;f<cropTypes.size();f++){
//			for( int t=0;t<cropTypes.size();t++){
//				rmatrix[f][t]=0.0;
//				row.put(t, 0.0);
//			}
/*			matrix.put(f, row);			*/
//		}
	}
	public void addElement(CropType from,CropType to,Double area){
		checkIndices(from,to);
		int f=cropTypes.indexOf(from);
		int t=cropTypes.indexOf(to);
		
		rmatrix[f][t]+=area;
	}
	private void checkIndices(CropType from,CropType to){

		if ( !cropTypes.contains(from)){
			throw new Error("From crop type not recognised by RotationMatrix");
		}		
		if ( !cropTypes.contains(to)){
			throw new Error("To crop type not recognised by RotationMatrix");
		}
	}
	
	public double getArea(CropType from,CropType to){
		checkIndices(from,to);
		int f=cropTypes.indexOf(from);
		int t=cropTypes.indexOf(to);
		return rmatrix[f][t];
	}
	
	public boolean isValid(){
		// Check rows and columns to see that areas add up in the correct way.
		double[] fromAreas=new double[size];
		JFMMath.doubleZero(fromAreas);
		double[] toAreas=new double[size];
		JFMMath.doubleZero(toAreas);
		
		for(int f=0;f<size;f++){
			for(int t=0;t<size;t++){
				fromAreas[f]+=rmatrix[f][t];
				toAreas[t]+=rmatrix[f][t];
			}
		}
		
		for(int c=0;c<size;c++){
			if ( JFMMath.isZero(toAreas[c] -fromAreas[c])){
				return false;
			}
		}
		return true;
	}
	
	public String toString(){

		DecimalFormat d3 = new DecimalFormat("#000");
		StringBuffer buff=new StringBuffer();
		// Write from Crops accross the top
		buff.append("   ");
		for(int f=0;f<rmatrix.length;f++){
			buff.append(cropTypes.get(f).shortName+" ");
		}
		buff.append("\n");
		for(int t=0;t<rmatrix[0].length;t++){
			buff.append(cropTypes.get(t).shortName+" ");
			for(int f=0;f<rmatrix.length;f++){
				buff.append(d3.format(rmatrix[f][t])+" ");
			}
			buff.append("\n");
		}
		return buff.toString();
	}
	
}
