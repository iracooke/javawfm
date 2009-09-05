/**
 * 
 */
package jfm.model;

/** Exception associated with the LPX solver status. 
 * @author iracooke
 *
 */
public class GLPKException extends Exception {
	public GLPKException(String message){
		super(message);
	}
}
