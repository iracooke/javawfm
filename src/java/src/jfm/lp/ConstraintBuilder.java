package jfm.lp;

/** \internal Abstract class to encapsulate building a specific constraint equation. 
 * Every ModelComponent class should create internal subclasses to define constraints 
 * relevant to that component. By convention a single constraint should be possible 
 * to write in a single equation. The idea behind encapsulating constraint building in 
 * this way is to permit constraints to be removed or added during runtime. But note that 
 * copy operations will always return an object with the default constraints, so changes will need
 * to be re-done for copies. 
 * 
 * @author Ira Cooke */
public abstract class ConstraintBuilder  {
	public enum CBType {
		TOTALAREA ("TotalArea"),
		OPAREA ("OperationArea"),
		OPSEQ ("OperationSequence"),
		NONSEQOP ("NonSequentialOp"),
		DISEASE ("Disease"),
		OPMINAREA ("OperationMinArea"),
		AREALIMIT ("AreaLimit"),
		LASTOPROT ("HandoverOpRotation"),
		NONSEQFIRSTOPROT ("NonSeqStartOpRot"),
		SEQFIRSTOPROT ("SeqStartOpRot"),
		RESOURCES ("Resources"),
		CROPCOUNTERMIN ("CropCounterMin"),
		CROPCOUNTERMAX ("CropCounterMax"),
		FREETIMEVRESOURCES ("freetimevresources"),
		FREETIMEMAX ("freetimemax"),
		WSEQUAL("winterstubbleequality"),
		MOTADRISKVALUE ("motadriskvalue"),
		VARRISKVALUE ("varriskvalue"),
		HEDGEROWSOS2("hedgeprofitlink"),
		DITCHESSOS2("ditchprofitlink"),
		ELSACCEPT ("elsaccept"),
		ELSHEDGELENGTH ("elshedgelength"),
		ELSDITCHLENGTH ("elsditchlength"),
		FIELDMARGINSMAXAREA ("fieldmarginsarea")
		;
		
		public final String tag;
		private CBType(String t){
			tag=t;
		}
	}
	private final CBType type;
	private final ModelComponent.MCType associatedWith;
	public CBType type(){return type;}
	public ModelComponent.MCType associatedWith(){return associatedWith;};
	/** Build the constraints. 
	 * This should never be called outside of ModelComponent 
	 * */
	protected abstract void build();
	protected ConstraintBuilder(CBType type_,ModelComponent.MCType assoc){
		type=type_;
		associatedWith=assoc;
	}
}
