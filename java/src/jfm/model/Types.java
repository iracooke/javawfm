package jfm.model;

import java.util.HashMap;
import jfm.lp.ModelComponent.MCType;
import jfm.xml.XMLSyntaxException;

/** Defines all basic types.  
 * \todo Think of a more elegant way to implement the functionality of the Types class 
 * @author Ira Cooke */
public final class Types{
	public enum ObjectiveType {
		PROFIT ("profit"),
		CURVEDPROFIT("curvedprofit"),
		WINTERSTUBBLE ("winterstubble"),
		CURVEDWINTERSTUBBLE ("curvedwinterstubble"),
		CURVEDCROPCOMPLEXITY("curvedcropcomplexity"),
		CROPCOMPLEXITY ("cropcomplexity"),
		HEDGEROWS("hedgerows"),
		CURVEDHEDGEROWS("curvedhedgerows"),
		FREETIME ("freetime"),
		CURVEDFREETIME ("curvedfreetime"),
		MOTADRISK ("motadrisk"),
		VARRISK ("varrisk"),
		CURVEDVARRISK ("curvedvarrisk"),
		CURVEDMOTADRISK ("curvedmotadrisk"),
		ELSCOMPLEXITY ("elscomplexity"),
		CURVEDELSCOMPLEXITY ("curvedelscomplexity"),
		DITCHES ("ditches"),
		CURVEDDITCHES ("curvedditches"),
		SOS2DUMMY ("sos2dummy"); // Special Objective Type used for variables which should never be rescaled or appear in the solution. In particular this is used by SOS2 variables to represent the y value of the curve that will in turn be bound to the true objective;
		public final String xmlname;
		private ObjectiveType(String name){
			xmlname=name;
		}
	}
	
	public enum ELSCode {
		EB1 ("Hedgerow management both sides","eb1",MCType.ELSBOUNDARIES, 22*10),
		EB2 ("Hedgerow management one side","eb2",MCType.ELSBOUNDARIES,11*10),
		EB3 ("Enhanced hedgerow management","eb3",MCType.ELSBOUNDARIES,42*10),
		EB6 ("Ditch management","eb6",MCType.ELSBOUNDARIES,24*10),
		EB10 ("Combined hedge and ditch management","eb10",MCType.ELSBOUNDARIES,56*10),
		EF6 ("Overwintered stubbles","ef6",MCType.STUBBLES,120),
		EE3 ("6m buffer strips","ee3",MCType.FIELDMARGINS,400),
		EE2 ("4m buffer strips","ee2",MCType.FIELDMARGINS,400),
		EF1 ("Field corner management","ef1",MCType.FIELDMARGINS,400)
		;
		
		String description;
		public final double defaultPoints;
		public final String xmlname;
		public MCType parentComponentType;
		private ELSCode(String description_,String name_,MCType mctype_,double defaultPoints_){
			description=description_;
			xmlname=name_;
			defaultPoints=defaultPoints_;
		}
		
	}
	
	/* public enum RotationType {
		WHEAT ("wheat"),
		BARLEY ("barley"),
		BRASSICA ("brassica"),
		POTATOES ("potatoes"),
		SUGARBEET ("sugarbeet"),
		SETASIDE ("setaside"),
		DRIEDPEAS ("driedpeas"),
		PEASBEANS ("peasbeans"),
		NONE ("none");
		public final String xmlname;
		private RotationType(String name_){
			xmlname=name_;
		}
	}*/
	
	public enum DiseaseType {
		WHEAT ("wheat"),
		OATS ("oats"),
		BARLEY ("barley"),
		BRASSICA ("brassica"),
		POTATOES ("potatoes"),
		SUGARBEET ("sugarbeet"),
		SETASIDE ("setaside"),
		DRIEDPEAS ("driedpeas"),
		PEASBEANS ("peasbeans"),
		LINSEED ("linseed"),
		NONE ("none");
		public final String xmlname;
		private DiseaseType(String name_){
			xmlname=name_;
		}
	}
	
	public enum WorkerType {
		LABOUR ("Labour","LABR","labour",0,1),
		// Machine types 
		TRACTOR ("Tractor","TRAC","tractor",140,100),
		SPRAYER ("Sprayer","SPYR","sprayer",20,2000),
		COMBINE ("Combine","CMBN","combine",168,14),
		BALER ("Baler","BALR","baler",50,5),
		HARROW ("Power Harrow","HARR","powerharrow",0,100),
		POTHARV ("Potato Harvester","PHAR","potato-harvester",50,100),
		SBHARV ("SugarBeet Harvester","SBHA","sugarbeet-harvester",70,100),
		NOMACHINE ("No Machine","NONE","no-machine",0,0);

		public final String name;
		public final String shortName;
		public final String xmlname;
		public final double power;
		public final double size;
		
		public double litresPerHour(){
			return 0.16*power;
//			return 0.167*power;
		}
		
		private WorkerType(String n,String shortn,String xmln,double pow,double sze){
			name=n; shortName=shortn;xmlname=xmln;power=pow;size=sze;
		}
	}
	
	public enum WorkerSubType {
		// Labour sub-types
		LABOUR ("Labour","LABR","labour"),
		FARMER ("Farmer","FRMR","farmer"),
		HARVESTER ("Combine","COMB","combine"),
		TRACTOR ("Tractor","TRAC","tractor"),
		SPRAYER ("Sprayer","SPYR","sprayer"),
		COMBINE ("Combine","CMBN","combine"),
		BALER ("Baler","BALR","baler"),
		HARROW ("Power Harrow","HARR","powerharrow"),
		POTHARV ("Potato Harvester","PHAR","potato-harvester"),
		SBHARV ("SugarBeet Harvester","SBHA","sugarbeet-harvester");

		public final String name;
		public final String shortName;
		public final String xmlname;
		/*
		public double litresPerHour(){
			return 0.167*power;
		}*/
		
		private WorkerSubType(String n,String shortn,String xmln){
			name=n; shortName=shortn;xmlname=xmln;
		}
	}
	
	
	public enum CropType {
		// Arable crop definitions from FBS 
		WINTERWHEAT ("Winter Wheat"," WW ","winterwheat"),
		SPRINGWHEAT ("Spring Wheat"," SW ","springwheat"),
		MIXEDWHEAT ("Mixed Wheat", " MW ","mixedwheat"),
		DURUMWHEAT ("Durum Wheat", " DW ","durumwheat"),
		TRITICALE ("Triticale", "TRIT","triticale"),
		WINTERBARLEY ("Winter Barley","WIBA","winterbarley"),
		SPRINGBARLEY ("Spring Barley","SPBA","springbarley"),
		MIXEDBARLEY ("Mixed Barley","MXBR","mixedbarley"),
		WINTEROATS ("Winter Oats","WOAT","winteroats"),
		SPRINGOATS ("Spring Oats","SOAT","springoats"),
		MIXEDOATS ("Mixed Oats","MOAT","mixedoats"),		
		RYE ("Rye","RYE ","rye"),
		MIXEDCEREALS ("Mixed Cereals","MCER","mixedcereals"),
		GRAINMAIZE ("Grain Maize","MAIZ","grainmaize"),
		FEEDBEANS ("Feed Beans","FBEA","feedbeans"),// Generic terms for feed peas and beans 
		FEEDPEAS ("Fead Peas","FPEA","feedpeas"),
		DRIEDPEAS ("Dried Peas","DRPE","driedpeas"),
		PROTEIN ("Protein ","PROT","protein"),
		EARLYPOTATOES ("Early Potatoes","EPOT","earlypotatoes"),
		PROCPOTATOES ("Processing Potatoes","PPOT","procpotatoes"),
		WAREPOTATOES ("Ware Potatoes","WPOT","warepotatoes"),
		SEEDPOTATOES ("Seed Potatoes","SPOT","seedpotatoes"),
		MIXEDPOTATOES ("Mixed Potatoes","MXPO","mixedpotatoes"),
		SUGARBEET ("Sugar Beet","SGBT","sugarbeet"),
		FLAX ("Flax","FLAX","flax"),
		LINSEED ("Linseed","LINS","linseed"),
		WOSRNDL ("Winter Oilseed Rape Not Double Low","WOSR","wosrndl"),
		WOSRDL ("Winter Oilseed Rape Double Low","WOSR","wosrdl"),
		SOSRNDL ("Spring Oilseed Rape Not Double Low","SOSR","sosrndl"),
		SOSRDL ("Spring Oilseed Rape Double Low","SOSR","sosrdl"),
		OOSR ("Other Oilseed Rape","OOSR","oosr"),
		OILSEEDS ("Other Oilseed Crops","OSED","otheroilseeds"),
		HEMP ("Hemp","HEMP","hemp"),
		HOPS ("Hops","HOPS","hops"),
		MEDICINALS ("Medicinals","MEDS","medicinals"),
		HERBAGESEED ("Herbage Seed","HSED","herbageseed"),
		OTHERARABLE ("Other Arable","OARA","otherarable"),
		
		// Catch All Crop Definition
		MISC ("Miscellaneous","MISC","misc"),
		
		// Generic Crop Types 
		WOSR ("Winter OilSeed Rape","WOSR","wosr"),
		SOSR ("Spring OilSeed Rape","SOSR","sosr"),
		
		// Crops We don't model yet but which are in the FBS
		FRESHFRUITANDVEG ("Fresh Fruit and Veg","FFVG","freshfruitandveg"),
		ORCHARD ("Orchard Fruit and Berries","ORCH","orchardfruit"),
		MUSHROOMS ("Mushrooms","MUSH","mushrooms"),
		NURSERYANDFLOWERS ("Nursery and Flowers","NURS","nurseryandflowers"),
		ENERGY ("Energy Crops","ENER","energy"),
		GRAZING ("Grass and Grazing","GRAS","grass"),
		FODDER ("Fodder","FODD","fodder"),
		MISCLU ("Misc Land Use","MSCU","misclanduse"),
		BYPRODUCTS("By Products","BYPR","byproducts"),
		PROCESSED("Processed Products","PROS","processed"),
		
		// Standard Set Aside Crops 
		SUGARBEETSA ("Sugar Beet SA","SBSA","sugarbeetsa"),
		WOSRSA ("Winter Oilseed Rape SA","WRSA","wosrsa"),
		SOSRSA ("Spring Oilseed Rape SA","SRSA","sosrsa"),
		OILSEEDSSA("Oilseeds SA","OSSA","oilseedssa"),
		OTHERARABLESA ("Other Arable SA","OASA","otherarablesa"),
		WINTERWHEATSA ("Winter Wheat SA","WWSA","winterwheatsa"),
		SPRINGWHEATSA ("Spring Wheat SA","SWSA","springwheatsa"),
		LINSEEDSA("Linseed SA","LSSA","linseedsa"),
		MISCSA("Miscellaneous SA","MSSA","miscsa"),
		SPRINGBARLEYSA("Spring Barley SA","SBSA","springbarleysa"),
		WINTERBARLEYSA("Winter Barley SA","WBSA","winterbarleysa"),
		MIXEDCEREALSSA("Mixed Cereals SA","MXSA","mixedcerealssa"),
		FEEDBEANSSA("Feed Beans SA","FBSA","feedbeanssa"),
		GRAZINGSA("Grazing SA","GRSA","grazingsa"),
		ENERGYSA("Energy SA","ENSA","energysa"),
		FODDERSA("Fodder SA","FDSA","foddersa"),
		SETASIDE ("Set Aside","SETA","setaside"),
		
		
		// Other
		UNKNOWN("Unknown","UNKN","unknown"),
		SUGARBEETQUOTALEASED ("Sugar Beet Quota Leased","SBQU","sugarbeetquotaleased"),
		SPRINGBEANS ("Spring Beans","SPBE","springbeans"),
		WINTERBEANS ("Winter Beans","WIBE","winterbeans");
		public final String name;
		public final String shortName;
		public final String xmlname;

		private CropType(String n,String shortn,String xmln){
			name=n; shortName=shortn;xmlname=xmln;
		}
	}
	
	public enum WorkabilityType {
		R28 ("r28",0.28),
		R60 ("r60",0.6),
		R70 ("r70",0.7),
		R80 ("r80",0.8),
		R90 ("r90",0.9),
		R100 ("r100",1.0);
		public final String xmlname;
		public final double percentHours;
		private WorkabilityType (String xml,double pchours){
			xmlname=xml;
			percentHours=pchours;
		}
	}
	
	public enum OperationType {
		PLOUGH ("Plough","PLGH","plough"),
		PLANT ("Plant","PLNT","plant"),
		BROADCAST ("Broadcast","BRDC","broadcast"),
		ROLL ("Roll","ROLA","roll"),
		SPRAY ("Spray","SPRA","spray"),
		FERT ("Fertilize","FERT","fert"),
		HARR ("Harrow","HARR","harrow"),
		COMBINE ("Combine","COMB","combine"),
		BALE ("Bale","BALE","bale"),
		RIDGING ("Ridging","RDGN","ridging"),
		HARVEST ("Harvest","HARV","harvest"),
		HOE ("Hoe","HOE ","hoe"),
		START ("Start","STRT","start"),
		END ("End","END ","end");
		public final String name;
		public final String shortName;
		public final String xmlname;
		private OperationType(String n,String shortn,String xmln){
			name=n; shortName=shortn;xmlname=xmln;
		}		
	}
	
	public enum TextDataType {
		ALLOW ("allow"),
		YIELDPENALTIES ("yieldpenalties"),
		COSTPENALTIES ("costpenalties"),
		MINAREA ("minarea"),
		HOURS ("hours"),
		YIELDREDUCTION ("yieldreduction"),
		DISEASE ("disease"),
		ROTPENALTY ("rotpenalty"),
		FORBIDDEN ("forbidden"),
		WKRATE ("wkrate"),
		MACHINES ("machines"),
		CROPTYPE("croptype"),
		RAW("raw");
		public final String xmlname;
		private TextDataType(String xmln){
			xmlname=xmln;
		}
	}

	public enum VariableType {
		NFERT ("N fertiliser"),
		PFERT ("P fertiliser"),
		KFERT ("K fertiliser"),
		BGHERB ("BGHerbicide"),
		WOHERB ("WOHerbicide"),	
		UNSPEC ("Unspecified"),	
		SIZEFIRSTMACHINE ("Size of first machine in system definition"),
		PRIMARYYIELD ("Primary yield of crop"),
		SECONDARYYIELD ("Secondary yield of crop"),
		SEEDAMOUNTOFCROP ("Seed Amount of crop"),
		SOILTYPE ("Soil Type"),
		RAINFALL ("Rain Fall"),
		TRANSPORT ("Transport");
		public final String xmlname;
		VariableType(String xn){
			xmlname=xn;
		}
		
	}
	

	public enum LimitType{
		AREA ("area");
		public final String xmlname;
		private LimitType(String xmln){
			xmlname=xmln;
		}
	}
	public static ELSCode xmlToELSCode(String str) throws XMLSyntaxException {
		if ( xmlToELSCode.containsKey(str)){
			return xmlToELSCode.get(str);
		} else {
			throw new XMLSyntaxException("The string "+str+" does not correspond to a variable type");
		}
	}
	
	public static VariableType xmlToVariableType(String str) throws XMLSyntaxException {
		if (xmlToVariableType.containsKey(str)){
			return xmlToVariableType.get(str);
		} else {
			throw new XMLSyntaxException("The string "+str+" does not correspond to a variable type");		
		}
	}
	
	public static LimitType xmlToLimitType(String str) throws XMLSyntaxException {
		if (xmlToLimitType.containsKey(str)){
			return xmlToLimitType.get(str);
		} else {
			throw new XMLSyntaxException("The string "+str+" does not correspond to a limit type");		
		}
	}
	public static WorkabilityType xmlToWorkabilityType(String str) throws XMLSyntaxException {
		if (xmlToWorkabilityType.containsKey(str)){
			return xmlToWorkabilityType.get(str);
		} else {
			throw new XMLSyntaxException("The string "+str+" does not correspond to a workability type");
		}
	}
	
	public static ObjectiveType xmlToObjectiveType(String str) throws XMLSyntaxException {
		if (xmlToObjectiveType.containsKey(str)){
			return xmlToObjectiveType.get(str);
		} else {
			throw new XMLSyntaxException("The string "+str+" does not correspond to an baseObjective type");
		}
	}
	/*
	public static RotationType xmlToRotationType(String str){
		if ( xmlToRotationType.containsKey(str)){
			return xmlToRotationType.get(str);
		} else {
			throw new Error("The string "+str+" does not correspond to a rotation type");
		}
	}
	*/
	public static DiseaseType xmlToDiseaseType(String str){
		if ( xmlToDiseaseType.containsKey(str)){
			return xmlToDiseaseType.get(str);
		} else {
			throw new Error("The string "+str+" does not correspond to a disease type");
		}
	}
	
	public static WorkerType xmlToWorkerType(String str) throws XMLSyntaxException {
		if (xmlToWorkerType.containsKey(str)){
			return xmlToWorkerType.get(str);
		} else {
			throw new XMLSyntaxException("The string "+str+" does not correspond to a worker type");
		}
	}

	public static WorkerSubType xmlToWorkerSubType(String str) throws XMLSyntaxException {
		if (xmlToWorkerSubType.containsKey(str)){
			return xmlToWorkerSubType.get(str);
		} else {
			throw new XMLSyntaxException("The string "+str+" does not correspond to a worker sub type");
		}
	}
	
	public static OperationType xmlToOperationType(String str){
		if (xmlToOperationType.containsKey(str)){
			return xmlToOperationType.get(str);
		} else {
			throw new Error("The string "+str+" does not correspond to a operation type");
		}
	}
	public static TextDataType xmlToTextDataType(String str){
		if (xmlToTextDataType.containsKey(str)){
			return xmlToTextDataType.get(str);
		} else {
			throw new Error("The string "+str+" does not correspond to a csv type");
		}
	}
	public static CropType xmlToCropType(String str) throws XMLSyntaxException {
		if (xmlToCropType.containsKey(str)){
			return xmlToCropType.get(str);
		} else {
			throw new XMLSyntaxException("The string "+str+" does not correspond to a crop type");
		}
	}
	private static HashMap<String,ELSCode> xmlToELSCode=new HashMap<String,ELSCode>();
	private static HashMap<String,LimitType> xmlToLimitType=new HashMap<String,LimitType>();
	private static HashMap<String,WorkabilityType> xmlToWorkabilityType=new HashMap<String,WorkabilityType>();
	private static HashMap<String,ObjectiveType> xmlToObjectiveType=new HashMap<String,ObjectiveType>();
//	private static HashMap<String,RotationType> xmlToRotationType=new HashMap<String,RotationType>();
	private static HashMap<String,DiseaseType>xmlToDiseaseType = new HashMap<String,DiseaseType>();
	private static HashMap<String,WorkerType>xmlToWorkerType =new HashMap<String,WorkerType>();
	private static HashMap<String,WorkerSubType>xmlToWorkerSubType =new HashMap<String,WorkerSubType>();
	private static HashMap<String,CropType>xmlToCropType =new HashMap<String,CropType>();
	private static HashMap<String,OperationType>xmlToOperationType =new HashMap<String,OperationType>();
	private static HashMap<String,TextDataType>xmlToTextDataType =new HashMap<String,TextDataType>();
	private static HashMap<String,VariableType> xmlToVariableType=new HashMap<String,VariableType>();

	public static boolean variableTypeExists(String xmln){
		if ( xmlToVariableType.containsKey(xmln)){
			return true;
		} else {
			return false;
		}
	}
	
	static{
		for ( ELSCode c:ELSCode.values()){
			xmlToELSCode.put(c.xmlname, c);
		}
		for(VariableType v:VariableType.values()){
			xmlToVariableType.put(v.xmlname, v);
		}
		
		for(LimitType lt:LimitType.values()){
			xmlToLimitType.put(lt.xmlname, lt);
		}
		for(WorkabilityType wt:WorkabilityType.values()){
			xmlToWorkabilityType.put(wt.xmlname, wt);
		}
		for (ObjectiveType ot:ObjectiveType.values()){
			xmlToObjectiveType.put(ot.xmlname, ot);
		}
		/*
		for (RotationType rt: RotationType.values()){
			xmlToRotationType.put(rt.xmlname, rt);
		}*/
		for ( DiseaseType dc: DiseaseType.values()){
			xmlToDiseaseType.put(dc.xmlname, dc);
		}
		for (WorkerType wt:WorkerType.values()){
			xmlToWorkerType.put(wt.xmlname, wt);
		}
		for (WorkerSubType wt:WorkerSubType.values()){
			xmlToWorkerSubType.put(wt.xmlname, wt);
		}
		
		for (CropType ct:CropType.values()){
			xmlToCropType.put(ct.xmlname, ct);
		}
		for (OperationType ot:OperationType.values()){
			xmlToOperationType.put(ot.xmlname, ot);
		}	
		for (TextDataType cst:TextDataType.values()){
			xmlToTextDataType.put(cst.xmlname, cst);
		}	
		
	}
	
}
