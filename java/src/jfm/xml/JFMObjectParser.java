package jfm.xml;

import org.w3c.dom.*;

import java.util.*;


/** Defines a generic parser object which implements parsing functions 
 * to create a concrete object of the same name
 * 
 *  \todo Combine the parserTypes variable and methods into an enum
 *  */
abstract class JFMObjectParser extends ObjectParser {

	private static final int CroppingParser =0;
	private static final int CropParser =1;
	private static final int CSVParser=2;
	private static final int OperationParser =3;
	private static final int MachineParser=4;
	private static final int LabourParser=5;
	private static final int WorkersParser =6;
	private static final int DiseaseParser=7;
	private static final int LocationParser=8;
	private static final int RotationParser=9;
	private static final int ObjectiveParser=10;
	private static final int LimitParser=11;
	private static final int CropInputParser=12;
	private static final int StubblesParser=13;
	private static final int FreeTimeParser=14;
	private static final int CropComplexityParser=15;
	private static final int LandUseParser=16;
	private static final int HedgerowsParser=17;
	private static final int MOTADRiskParser=18;
	private static final int ELSOptionsParser=19;
	private static final int BoundaryMaintenanceParser=20;
	private static final int DitchLengthParser=21;
	private static final int VARRiskParser=22;

	/** A map from the names of xml Elements to corresponding parsers */
	private static Map<String,Integer> parserTypes = new HashMap<String,Integer>();
	static {
		parserTypes.put(new CroppingParser(null).parsesNode(), CroppingParser);
		parserTypes.put(new CropParser(null).parsesNode(),CropParser);
		parserTypes.put(new TextParser(null).parsesNode(), CSVParser);
		parserTypes.put(new OperationParser(null).parsesNode(), OperationParser);
		parserTypes.put(new MachineParser(null).parsesNode(), MachineParser);
		parserTypes.put(new LabourParser(null).parsesNode(), LabourParser);
		parserTypes.put(new WorkersParser(null).parsesNode(), WorkersParser);
		parserTypes.put(new DiseaseParser(null).parsesNode(), DiseaseParser);
		parserTypes.put(new LocationParser(null).parsesNode(), LocationParser);
		parserTypes.put(new RotationParser(null).parsesNode(), RotationParser);
		parserTypes.put(new ObjectiveParser(null).parsesNode(), ObjectiveParser);
		parserTypes.put(new LimitParser(null).parsesNode(),LimitParser);
		parserTypes.put(new CropInputParser(null).parsesNode(), CropInputParser);
		parserTypes.put(new StubblesParser(null).parsesNode(), StubblesParser);
		parserTypes.put(new FreeTimeParser(null).parsesNode(), FreeTimeParser);
		parserTypes.put(new CropComplexityParser(null).parsesNode(), CropComplexityParser);
		parserTypes.put(new LandUseParser(null).parsesNode(), LandUseParser);
		parserTypes.put(new HedgerowLengthParser(null).parsesNode(), HedgerowsParser);
		parserTypes.put(new MOTADRiskParser(null).parsesNode(), MOTADRiskParser);
		parserTypes.put(new ELSOptionsParser(null).parsesNode(), ELSOptionsParser);
		parserTypes.put(new BoundaryMaintenanceParser(null).parsesNode(), BoundaryMaintenanceParser);
		parserTypes.put(new DitchLengthParser(null).parsesNode(), DitchLengthParser);
		parserTypes.put(new VARRiskParser(null).parsesNode(), VARRiskParser);
		
	}
	/** Create a parser from the name of an xml element 
	 * @params name the name of the element for which a parser is to be created */
	public JFMObjectParser newNamedParser(String name) throws XMLSyntaxException,XMLObjectException{
		if ( !parserTypes.containsKey(name)){
			throw new XMLSyntaxException("The keyword "+name+" does not correspond to a parser");
		}
		int parserType = parserTypes.get(name);

		switch (parserType){
			case CroppingParser: 
				return new CroppingParser(this);
			case CropParser:
				return new CropParser(this);
			case CSVParser:
				return new TextParser(this);
			case OperationParser:
				return new OperationParser(this);
			case MachineParser:
				return new MachineParser(this);
			case LabourParser:
				return new LabourParser(this);
			case WorkersParser:
				return new WorkersParser(this);
			case DiseaseParser:
				return new DiseaseParser(this);
			case RotationParser:
				return new RotationParser(this);
			case LocationParser:
				return new LocationParser(this);
			case ObjectiveParser:
				return new ObjectiveParser(this);
			case LimitParser:
				return new LimitParser(this);
			case CropInputParser:
				return new CropInputParser(this);
			case StubblesParser:
				return new StubblesParser(this);
			case FreeTimeParser:
				return new FreeTimeParser(this);
			case CropComplexityParser:
				return new CropComplexityParser(this);
			case LandUseParser:
				return new LandUseParser(this);
			case HedgerowsParser:
				return new HedgerowLengthParser(this);
			case MOTADRiskParser:
				return new MOTADRiskParser(this);
			case ELSOptionsParser:
				return new ELSOptionsParser(this);
			case BoundaryMaintenanceParser:
				return new BoundaryMaintenanceParser(this);
			case DitchLengthParser:
				return new DitchLengthParser(this);
			case VARRiskParser:
				return new VARRiskParser(this);
			default: 
				throw new XMLObjectException("No instructions to create parser "+name);
		}		
	}
	
}
