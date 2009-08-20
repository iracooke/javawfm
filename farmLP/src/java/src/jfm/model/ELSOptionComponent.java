/**
 * 
 */
package jfm.model;

import jfm.lp.ModelComponent;
import jfm.lp.ModelComponent.MCType;
import jfm.lp.MatrixVariable;
import jfm.model.ELSOption;
import jfm.model.Types.ELSCode;
import java.util.*;
/**
 * @author iracooke
 *
 */
public abstract class ELSOptionComponent extends ModelComponent {

	protected HashMap<ELSCode,ELSOption> options=new HashMap<ELSCode,ELSOption>();

	public Set<ELSOption> getOptions(){
		HashSet<ELSOption> rv = new HashSet<ELSOption>(options.values());
		return Collections.unmodifiableSet(rv);
	}
	
	ELSOptionComponent(MCType type_){
		super(type_);
	};	
	
/*	protected ELSOption getOption(ELSCode code){
		for(ELSOption eo:options){
			if ( eo.code==code){
				return eo;
			}
		}
		
		throw new Error("Option "+code+" not found ");
		
	}*/
	
}
