package jfm.model;

import java.util.*;

import jfm.model.Types.DiseaseType;
import jfm.model.Types.CropType;



/** Cost and yield penalties for rotating to a particular DiseaseType. 
 * 
 * This class defines the value of penalties for rotation from 
 * any disease type to one in particular (many from .. single to ). The values of these 
 * penalties are not specific to a particular period and therefore this class is not a 
 * ModelPrimitive.  The equivalent ModelPrimitive class is RotationPenalty, which 
 * defines the penalty in a particular period. The rotation penalties in the RotationPenalty class 
 * are typically derived from those in this class
 * 
 * @author Ira Cooke */
public final class Rotation {
	private final Map<DiseaseType,double[]> rotPenalties=new HashMap<DiseaseType,double[]>();
	private final Set<DiseaseType> forbiddenDiseases=new HashSet<DiseaseType>();
	/** Each pair of numbers here is a yield penalty and a cost penalty in that order */
	/** The disease class of the crop to which rotation is to */
	public final CropType to;
	public Rotation(CropType totype,Map<DiseaseType,double[]> rps,Set<DiseaseType> forbidden){
		rotPenalties.putAll(Collections.unmodifiableMap(rps));
		forbiddenDiseases.addAll(forbidden);
		to=totype;
	}
	public double[] getPenalty(DiseaseType fromType){
		if ( rotPenalties!=null){
			if ( rotPenalties.containsKey(fromType)){
				return rotPenalties.get(fromType);
			} 
		} 
		double[] nocosts={0,0};
		return nocosts;
	}
	public boolean isForbidden(DiseaseType dis){
		return forbiddenDiseases.contains(dis);
	}
	public Rotation copy(){
		return new Rotation(to,rotPenalties,forbiddenDiseases);
	}
}
