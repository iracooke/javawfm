/**
 * 
 */
package jfm.model;

import java.util.*;

/** 
 * @author iracooke
 *
 */
public class FormulaParser {
	private BranchNode root;
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
	
	private enum NodeType {
		BRANCH, LEAF;
	}
		
	public FormulaParser(String input){
		root = new BranchNode();
		root.parse(input);
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
				return fString.substring(ci, i);
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
				return fString.substring(ci, i);
			}
		}
		throw new Error("Reached End of String but no closing bracket found");
	}
	private String getNumber(String fString,int ci){
		char c;
		for( int i=ci;i<fString.length();i++){
			c=fString.charAt(i);
			if ( !Character.isDigit(c) && c!='.'){
				return fString.substring(ci, i);
			}			
		}
		return fString.substring(ci);
	}
	
	private abstract class Node {
		abstract void parse(String fString);

		private boolean isBranchNode(String str){
			if ( str.contains("*") || str.contains("-") || str.contains("+") || str.contains("/") || str.contains("^")){
				return true;
			} else {
				return false;
			}
		}
		void assignNode(String str,Node theNode){
			if ( isBranchNode(str)){
				theNode=new BranchNode();
			} else {
				theNode=new LeafNode();
			}
			theNode.parse(str);
		}
	}
	private class LeafNode extends Node {
		public double value=0;
		public void parse(String fString){
			value=Double.parseDouble(fString);
		}
	}

	private class BranchNode extends Node {
		FOperator op;
		Node LHS;
		Node RHS;
		public void parse(String fString){
			// Here we split the string into a LHS and RHS and pass them on to the child nodes
			ArrayList<String> operands=new ArrayList<String>();
			ArrayList<FOperator> operators = new ArrayList<FOperator>();
			for(int ci=0; ci < fString.length();ci++){
				char c = fString.charAt(ci);
				if(Character.isDigit(c) || (c=='.')){
					// We have a simple number so get it;
					String number=getNumber(fString,ci);
					operands.add(number);
					ci+=number.length()+1;
					c=fString.charAt(ci);
				}
				
				switch(c){
				case '(':
					String brac=getBracket(fString,ci+1);					
					operands.add(brac);
					ci+=brac.length()+1;
					break;
				case ')':
					throw new Error("Unopened close bracket encountered in formula");
				case '{':
					String brace=getBracedVariable(fString,ci+1);
					operands.add(brace);
					ci+=brace.length()+1;
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
					throw new Error("Unrecognised character in formula"+c);
				}
			}
			// Quick check to make sure correct number of operators and operands
			if ( operators.size()+1 != operands.size()){
				throw new Error("There must be exactly one fewer operators than operands");
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
			lstrbuff.append(operands.get(0));
			for ( int i=1;i<=separatingOp;i++){
				lstrbuff.append(operators.get(i-1).token);
				lstrbuff.append(operands.get(i));				
			}
			rstrbuff.append(operands.get(separatingOp+1));
			for ( int i=separatingOp+2;i<operands.size();i++){
				rstrbuff.append(operators.get(i-1).token);
				rstrbuff.append(operands.get(i));
			}
			
			assignNode(lstrbuff.toString(),LHS);
			assignNode(rstrbuff.toString(),RHS);
			
		}
	}
	
	
	
}

