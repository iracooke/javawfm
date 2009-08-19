package jfm.model;
import java.util.*;

import jfm.model.Types.VariableType;
import jfm.model.Types;
import jfm.xml.XMLSyntaxException;
/** \internal Evaluates and holds a mathematical expression. 
 * 
 * Handles expressions containing *,+,-,/,^, and () as well as certain named variables {VAR}
 * 
 * \todo Add caching of formula results but force recalculation in VariableHolder's change
 * 
 * @author Ira Cooke
 * */
public final class Formula {
	private Node root;
	private String originalString;
	private boolean variablesChanged=true;
	private double cachedValue=0;
	private final Set<VariableType> requiredVariables=new HashSet<VariableType>();
	private Set<VariableType> unsetVariables=new HashSet<VariableType>();
	private final Map<VariableType,Double> variableValues=new HashMap<VariableType,Double>();
	/** Construct the formula from a string */
	public Formula(String input) {
//		originalString=input;
	//	System.out.println("Initializing formula "+originalString);
		parse(input);/*
		if ( isBranchNode(input)){
			root = new BranchNode();
		} else {
			root = new LeafNode();
		}
		try {
			root.parse(input);
		} catch (Exception ex){
			throw new Error("Error parsing formula"+ex.getMessage());
		}*/
	}
	/** Give the formula a new value by getting it to parse a string */
	public void parse(String input)  {
		originalString=input;
	//	System.out.println("Parsing formula "+originalString+"\n");
		if ( isBranchNode(input)){
			root = new BranchNode();
		} else {
			root = new LeafNode();
		}
		try {
			root.parse(input);
		} catch (Exception ex){
			throw new Error("Error parsing formula"+ex.getMessage());
		}
		variablesChanged=true;
	}
	/** Copies the formula but requires variables to be set for the new formula*/
	public Formula copy() {
		Formula newF = new Formula(originalString);
		return newF;
	}
	public String getFormula(){return originalString;};
	
	public Set<VariableType> requiredVariables(){
		return Collections.unmodifiableSet(requiredVariables);
	}
	
	public void setVariable(VariableType type,double val){
		if ( requiredVariables.contains(type)){
			if ( variableValues.containsKey(type) ){
				if (val!=variableValues.get(type)){
					variablesChanged=true;
				} 
			} else {
				variablesChanged=true;
			}
			variableValues.put(type, val);
			unsetVariables.remove(type);
		} 
	}
	
	public double calculateValue(){
		if ( variablesChanged ){
			if ( unsetVariables.size()>0){
				
				throw new Error("There were unset variables in the formula "+originalString+"\n"+unsetVariables);
			}
			cachedValue=root.calculateValue();
			variablesChanged=false;
//			System.out.println(originalString+" Formula value = "+cachedValue);
//			throw new Error("Stop");
		} 
		return cachedValue;
	}
	


	private enum FOperator {
		TIMES ('*'),
		MINUS ('-'),
		PLUS ('+'),
		DIVIDE ('/'),
		POWER ('^');
		char token;
		FOperator(char t){
			token=t;
		}
		
		public static FOperator charToOp(char c){
			switch(c){
			case '*': return TIMES;
			case '/': return DIVIDE;
			case '-': return MINUS;
			case '+': return PLUS;
			case '^': return POWER;
			default:
				throw new Error("The operator "+c+"is not recognised");
			}
		}
		public double operate(double lhs,double rhs){
			switch(token){
			case '*': return lhs*rhs;
			case '/': return lhs/rhs;
			case '-': return lhs-rhs;
			case '+': return lhs+rhs;
			case '^': return Math.pow(lhs, rhs);
			default:
				throw new Error("token for "+this+" is not a mathematical operator");
			}
		}
	}
	private String getBracedVariable(String fString,int ci){
		char c;
		int openBracketCount=1;
		for( int i=ci;i<fString.length();i++){
			c=fString.charAt(i);
			if (c=='{'){
				openBracketCount++;
			}
			if ( c=='}'){
				openBracketCount--;
			}
			if ( openBracketCount == 0 ){
				return fString.substring(ci-1, i+1);
			}
		}
		throw new Error("Reached End of String but no closing brace found");
	}
	
	private String getBracket(String fString,int ci){
		char c;
		int openBracketCount=1;
		for( int i=ci;i<fString.length();i++){
			c=fString.charAt(i);
			if (c=='('){
				openBracketCount++;
			}
			if ( c==')'){
				openBracketCount--;
			}
			if ( openBracketCount == 0 ){
				return fString.substring(ci-1, i+1);
			}
		}
		throw new Error("Reached End of String but no closing bracket found "+fString);
	}
	private String getNumber(String fString,int ci){
		char c;
		for( int i=ci;i<fString.length();i++){
			c=fString.charAt(i);
	//		System.out.print(c);
			if ( !Character.isDigit(c) && c!='.'){
				return fString.substring(ci, i);
			}			
		}
		return fString.substring(ci);
	}
	

	private boolean isBranchNode(String str){
		if ( str.contains("*") || str.contains("-") || str.contains("+") || str.contains("/") || str.contains("^")){
			return true;
		} else {
			return false;
		}
	}
	
	private abstract class Node {
		protected String originalString="";
		public String originalString(){return originalString;};
		abstract void parse(String fString) throws XMLSyntaxException ;
		abstract double calculateValue();
		protected Node assignNode(String str) throws XMLSyntaxException{
			Node theNode;
			if ( isBranchNode(str)){
				theNode=new BranchNode();
			} else {
				theNode=new LeafNode();
			}
			theNode.parse(str);
			return theNode;
		}
	}
	
	private class LeafNode extends Node {
		private double value=0;
		private VariableType var=null;
		public void parse(String fString) throws XMLSyntaxException{
			fString=stripUnwantedBrackets(fString);
			originalString=fString;
			fString=stripBraces(fString);
			
			
			if ( Types.variableTypeExists(fString)){
				// This leaf is a variable. Register that we require it.
				var=Types.xmlToVariableType(fString);
				requiredVariables.add(var);
				unsetVariables.add(var);
			} else {
				value=Double.parseDouble(fString);
			}
		}
		double calculateValue(){
			if ( var == null){
				return value;
			} else {
				if ( variableValues.containsKey(var)){
					return variableValues.get(var);
				} else {
					throw new Error("No value specified for "+var);
				}
			}
		}
	}
	
	private String stripBraces(String str){
		char c=str.charAt(0);
		if ( c != '{'){
			return str;
		} else {
			return str.substring(1, str.length()-1);
		}
	}
	
	private String stripUnwantedBrackets(String str){
		char c=str.charAt(0);
		if ( c != '('){
			return str;
		}
		int openBracketCount=1;
		
		for( int i=1;i<str.length()-1;i++){
			c=str.charAt(i);
			if (c=='('){
				openBracketCount++;
			}
			if ( c==')'){
				openBracketCount--;
			}
			if ( openBracketCount == 0 ){
				return str;
			}
		}
		if ( openBracketCount == 1 && str.charAt(str.length()-1)==')'){
			return str.substring(1, str.length()-1);
		} else {
			throw new Error("Unclosed bracket when attempting to strip excess ");
		}
	}

	private class BranchNode extends Node {
		FOperator op;
		Node LHS;
		Node RHS;
		double calculateValue(){
			//System.out.println(LHS.originalString);
			//System.out.println(RHS.originalString);
			double lhv=LHS.calculateValue();
			double rhv=RHS.calculateValue();
	//		System.out.println("--------------------");
	//		System.out.println(lhv+" "+op+" "+rhv+" is:: ");
	//		System.out.println(" -- "+LHS.originalString+" "+op+" "+RHS.originalString);
			return op.operate(lhv, rhv);
		}
		
		public void parse(String fString) throws XMLSyntaxException {
			// Strip unneccesary brackets
			fString=stripUnwantedBrackets(fString);
			originalString=fString;
			// Here we split the string into a LHS and RHS and pass them on to the child nodes
			ArrayList<String> operands=new ArrayList<String>();
			ArrayList<FOperator> operators = new ArrayList<FOperator>();
	//		System.out.println("Parsing "+fString);
//			System.out.println("");
			for(int ci=0; ci < fString.length();ci++){
				char c = fString.charAt(ci);
	//			System.out.print(c);
				if(Character.isDigit(c) || (c=='.')){
					// We have a simple number so get it;
					String number=getNumber(fString,ci);
//					System.out.println("Got Number "+number);
					operands.add(number);
					ci+=number.length();
					if ( ci < fString.length()){
						c=fString.charAt(ci);
					} else {
						break;
					}
				}
				
				switch(c){
				case '(':
					String brac=getBracket(fString,ci+1);	
//					System.out.println("Got Bracket "+brac);
					operands.add(brac);
					ci+=brac.length()-1;
					break;
				case ')':
					throw new Error("Unopened close bracket encountered in formula "+fString);
				case '{':
					String brace=getBracedVariable(fString,ci+1);

//					System.out.println("Got Brace "+brace);
					operands.add(brace);
					ci+=brace.length()-1;
					break;
				case '}':
					throw new Error("Unopened close brace encountered in formula");
				case '^':
					operators.add(FOperator.POWER);
					break;
				case '*':
					operators.add(FOperator.TIMES);
					break;
				case '/':
					operators.add(FOperator.DIVIDE);
					break;
				case '-':
					operators.add(FOperator.MINUS);
					break;
				case '+':
					operators.add(FOperator.PLUS);
					break;
				default:	
					if(Character.isDigit(c) || (c=='.')){
						// We have a simple number so get it;
						String number=getNumber(fString,ci);
//						System.out.println("Got Number "+number);
						operands.add(number);
						ci+=number.length();
					} else {
						throw new Error("Unrecognised character "+c+" in formula "+fString);
					}
				}
			}
		//	System.out.println("Ops "+operators+" opands "+operands);
			// Quick check to make sure correct number of operators and operands
			if ( operators.size()+1 != operands.size()){					
				StringBuffer buff = new StringBuffer();
				buff.append("Operator ");
				for(FOperator op:operators){
					buff.append(op+" ");
				}
				buff.append("\n");
				for(String s:operands){
					buff.append(s+"|");
				}
				throw new Error("There must be exactly one fewer operators than operands \n"+buff.toString());
			
			}
			// Now check if we actually just have a leaf node
			if ( operators.size() ==0){
				throw new Error("Branch Node must have at least one operator");
			}

			
			
			
			// Search for the separating Operator in order of operations order
			int separatingOp=operators.indexOf(FOperator.PLUS);
			if ( separatingOp == -1 ){
				separatingOp=operators.indexOf(FOperator.MINUS);
			}
			if (separatingOp == -1 ){
				separatingOp=operators.indexOf(FOperator.TIMES);
			}
			if ( separatingOp == -1 ){
				separatingOp=operators.indexOf(FOperator.DIVIDE);
			}
			if ( separatingOp == -1 ){
				separatingOp=operators.indexOf(FOperator.POWER);
			}
			if ( separatingOp == -1 ){
				throw new Error("No separating operation");
			}
			StringBuffer lstrbuff=new StringBuffer();
			StringBuffer rstrbuff=new StringBuffer();
//			lstrbuff.append("(");
			lstrbuff.append(operands.get(0));
			for ( int i=1;i<=separatingOp;i++){
				lstrbuff.append(operators.get(i-1).token);
				lstrbuff.append(operands.get(i));				
			}
//			lstrbuff.append(")");
//			rstrbuff.append("(");
//			System.out.println("Sep op "+operators.get(separatingOp));
			rstrbuff.append(operands.get(separatingOp+1));
			for ( int i=separatingOp+2;i<operands.size();i++){
				if ( operators.get(separatingOp) == FOperator.MINUS){
		//			System.out.println(operators.get(i-1));
					if ( operators.get(i-1) == FOperator.MINUS){
						rstrbuff.append(FOperator.PLUS.token);
					} else if ( operators.get(i-1) == FOperator.PLUS){
						rstrbuff.append(FOperator.MINUS.token);
					} else {
						rstrbuff.append(operators.get(i-1).token);
					}
				} else {
					rstrbuff.append(operators.get(i-1).token);
				}
				rstrbuff.append(operands.get(i));
			}
//			rstrbuff.append(")");
	//		System.out.println(rstrbuff);
			LHS=assignNode(lstrbuff.toString());
			RHS=assignNode(rstrbuff.toString());
			op=operators.get(separatingOp);
		}
	}
	
	
	
	/*
	public boolean isFormula=false;
	private double cachedValue=0;
	
	private enum ValueType {
		RAINFALL,SOILTYPE,DOUBLE;
	}
	
	private boolean isInitialized=false;
	private Field field=new Field(Operator.NONE);
	public String debugPrint(Location loc){
		StringBuffer buff=new StringBuffer();
		if ( isFormula){
			int[] depth={0};
			depth[0]=0;
			buff.append(field.originalString+"\n");
			buff.append(field.printSubFields(depth, loc));
			buff.append(cachedValue+"\n");
		}
		return buff.toString();
	}
	public String printFormula(){
		return field.originalString;
	}
	public double calc(Location loc){
		cachedValue=field.getValue(loc);
		return cachedValue;
	}
	public double cached(){return cachedValue;};
	public boolean isInitialized(){ return isInitialized;};
	public void initWithString(String str) throws XMLSyntaxException {
		field.initWithString(str);
		isInitialized=true;
	}
	private enum Operator{
		TIMES('*'),
		DIVIDE('/'),
		PLUS('+'),
		SUBTRACT('-'),
		END(' '),
		NONE(' ');
		public final char pattern;
		private Operator(char patt){
			pattern=patt;
		}
		
		public static Operator charToOp(char c){
			switch(c){
			case '*': return TIMES;
			case '/': return DIVIDE;
			case '-': return SUBTRACT;
			case '+': return PLUS;
			case ')': return END;
			default:
				return NONE;
			}
		}
		public double operate(double lhs,double rhs){
			switch(pattern){
			case '*': return lhs*rhs;
			case '/': return lhs/rhs;
			case '-': return lhs-rhs;
			case '+': return lhs+rhs;
			default:
				throw new Error("Pattern for "+this+" is not a mathematical operator");
			}
		}
	}
	
	


	private class Field implements Comparable<Field> {
		public String originalString;
		private ArrayList<Field> subfields=new ArrayList<Field>();
		private double[] value=null;
		private ValueType valueType;
		public final Operator next;
		public Operator parentOp=null;
		Field(Operator op_){
			next=op_;
		}
		public String printSubFields(int[] depth,Location loc){
			depth[0]++;
			StringBuffer buff = new StringBuffer();
			if ( subfields.size() > 0 ){
				int f=0;
				for ( Field fl:subfields){
					f++;
//					buff.append("n: "+f+" sz:"+fl.subfields.size()+" \n"+fl.printSubFields(depth,loc));
					buff.append(fl.printSubFields(depth,loc));
				}
			} else {
				for ( int i=0; i < depth[0];i++){
					buff.append("  -  ");
				}
				buff.append(" "+getValue(loc)+next+" \n");
			}
			depth[0]--;
			return buff.toString();
		}

		private ValueType getSystemValue(String val){
			return ValueType.SOILTYPE; 
		}
		public void setValue(String val) throws XMLSyntaxException {
//			System.out.println("Setting value "+val);
			Pattern systemValuePatt = Pattern.compile("\\{(.*)\\}");
			Matcher systemValueMat=systemValuePatt.matcher(val);
			
			if (systemValueMat.find()){
				valueType = getSystemValue(systemValueMat.group(1));
			} else {
				try {
					value=new double[1];
					valueType=ValueType.DOUBLE;
					value[0]=Double.valueOf(val);
				} catch ( NumberFormatException ex){
					throw new XMLSyntaxException("Badly formed numerical value in formula");
				}
			}
		}
		public void setSubFields(ArrayList<Field> subs){
			subfields.addAll(subs);
		}
		
		public double getValue(Location loc){
//			System.out.println("Getting value with "+subfields.size()+" subfields");
			if ( subfields.size() > 0){
//				System.out.println("-- getting value for first subfield of type "+subfields.get(0).next);
				Field previous=subfields.get(0);
				double val=previous.getValue(loc);
//				System.out.println("1st "+val+previous.next);
				//				System.out.println("-- got value for first subfield of type "+subfields.get(0).next+" with val "+" val "+val);
				for(int i=1;i<subfields.size();i++){
					Field nextf=subfields.get(i);
//					System.out.println("---- getting value for "+i+"th subfield of type "+nextf.next+" with val " + val);
					switch(previous.next){
					case TIMES:
						val*=nextf.getValue(loc);
//						System.out.println("T "+val);
						break;
					case PLUS:
						val+=nextf.getValue(loc);
//						System.out.println("P "+val);
						break;
					case SUBTRACT:
						val-=nextf.getValue(loc);
						break;
					case DIVIDE:
						val/=nextf.getValue(loc);
//						System.out.println("D "+val);
						break;
					case END:
//						System.out.println("---- got value for "+i+"th subfield of type "+nextf.next+" with val " + val);
						return val;
					}
//					System.out.println("---- got value for "+i+"th subfield of type "+nextf.next+" with val " + val);
					previous=nextf;
				}
				return val;
			} else {
//				System.out.println("Getting primitive value of type "+valueType);
				switch (valueType ){
				case RAINFALL:
					return loc.rainfall();
				case SOILTYPE:
					return loc.soiltype();
				case DOUBLE:
					return value[0];
				}
			}
			throw new Error("List of sub fields not closed with an END operator");
		}
		
		private String getBracketContents(String str,int[] index) throws XMLSyntaxException{
			int cindex=0;
	//		System.out.println("Getting contents of "+str+" from index "+index[0]);
			int opencount=0;
			while(cindex < str.length() ){			
				Character c=str.charAt(cindex);
	//			System.out.print(c);
				if ( c== ')'){
					if ( opencount == 1 ){
						index[0]+=cindex+1;
	//					System.out.print("\n");
						return str.substring(1,cindex);
					} else {
						opencount--;
					}
				}
				if ( c=='('){
					opencount++;
				}
				cindex++;
			}
			throw new XMLSyntaxException("missing terminating ) character in formula");
		}
		

		public void initWithString(String localstr) throws XMLSyntaxException {
			originalString=localstr;
			if ( localstr.matches("\\A\\d+\\.?\\d*\\z")){
				value=new double[1];
				value[0]=Double.parseDouble(localstr);
				valueType=ValueType.DOUBLE;
			} else { // We need to parse subfields				
				isFormula=true;
				int[] localindex={0};
				int[] depth={0};
	//			System.out.println("init with "+localstr);
				while(localindex[0]<localstr.length()){
//					System.out.println("\n formular substr "+localstr);
	//				System.out.println("\n formula substr "+localindex[0]+" sub "+localstr.substring(localindex[0]));			
					subfields.add(nextField(localstr,localindex,depth));
				}
			}
		}
		

		private Field returnForOperator(Operator op,String localstr,int[] globalindex,int cindex,int[] depth) throws XMLSyntaxException {
			depth[0]--;
			globalindex[0]+=cindex+1;
			Field fld=new Field(op);
			fld.setValue(localstr.substring(0,cindex));
			return fld;
		}
		
		private Field nextField(String globalstr,int[] globalindex,int[] depth) throws XMLSyntaxException {
			int cindex=0;
			depth[0]++;
			String localstr=globalstr.substring(globalindex[0]);
			while(cindex < localstr.length() ){			
				Character c=localstr.charAt(cindex);
	//			System.out.print(c);
				switch(c){
				case '*':				
					return returnForOperator(Operator.TIMES,localstr,globalindex,cindex,depth);
				case '/':
					return returnForOperator(Operator.DIVIDE,localstr,globalindex,cindex,depth);
				case '-':
					return returnForOperator(Operator.SUBTRACT,localstr,globalindex,cindex,depth);
				case '+':
					return returnForOperator(Operator.PLUS,localstr,globalindex,cindex,depth);				
				case '(':
					if ( cindex!=0){ throw new Error("this shouldn't happen");};
					String contents = getBracketContents(localstr,globalindex);
	//				System.out.println("Bracket contents "+contents);
	//				System.out.println("After getting bracket "+globalstr.substring(globalindex[0]));
					Operator op;
					
					if ( globalindex[0] < globalstr.length() ){
	//					System.out.println(" and new index is "+globalindex[0]+" with char "+globalstr.charAt(globalindex[0])+globalstr.charAt(globalindex[0]+1));
						op=Operator.charToOp(globalstr.charAt(globalindex[0]));
						globalindex[0]++;
					} else {
						op=Operator.END;
					}
					Field fld = new Field(op);
					fld.initWithString(contents);
					depth[0]--;
					return fld;
				}
				cindex++;
			}
			Field last=new Field(Operator.END);
			last.setValue(globalstr.substring(globalindex[0]));
			globalindex[0]+=cindex;
			return last;
//			throw new XMLSyntaxException("Badly formed formula. possible lack of closing bracket");
		}

		public boolean equals(Object other){
			if ( other instanceof Field){
				Field oth=(Field)other;
				if ( oth.next==next){
					return true;
				} 
			}
			return false;
		}
		public int hashCode(){
//			int hash=7;
//			hash=31*hash+rowIndex;
//			hash=31*hash+colIndex;
			return next.hashCode();
		}
		
//		 Allow fields to be sorted according to their operator type 
		public int compareTo(Field to){
			return this.next.compareTo(to.next);
		}
		
	}
	*/
}
