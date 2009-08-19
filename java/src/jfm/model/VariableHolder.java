/**
 * 
 */
package jfm.model;
import java.util.*;
import jfm.model.Types.VariableType;
/**
 * @author iracooke
 *
 */
public abstract class VariableHolder {
	private Map<VariableType,Double> vMap=new HashMap<VariableType,Double>();
//	private Map<VariableType,Boolean> valueChanged=new HashMap<VariableType,Boolean>();
	public void setVariable(VariableType type,Double value){
		vMap.put(type, value);
//		valueChanged.put(type, true);
	}
	public double getVariable(VariableType type){
		return vMap.get(type);
	}
/*	public boolean valueChanged(VariableType type){
		return valueChanged.get(type);
	}*/
	public Set<VariableType> variableSet(){
		return Collections.unmodifiableSet(vMap.keySet());
	}
}
