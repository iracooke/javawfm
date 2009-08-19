package jfm.utils;


import java.text.DecimalFormat;

/** Defines a range of functions to print vectors and arrays. 
 * Also defines a random collections of basic mathematical functions */
public final class MathPrint {

	public static DecimalFormat d1 = new DecimalFormat("#0");
	public static DecimalFormat d3 = new DecimalFormat("#000");
	public static DecimalFormat f0 = new DecimalFormat("#.0");
	public static DecimalFormat f1 = new DecimalFormat("#0.0");
	public static DecimalFormat f2 = new DecimalFormat("#00.0");
	public static DecimalFormat f3 = new DecimalFormat("#000.0");
	public static DecimalFormat df2 = new DecimalFormat("#0.00");
	public static DecimalFormat df3 = new DecimalFormat("#0.000");
	
	public static String prettyPrint(double[] values,String[] names){
		StringBuffer buff = new StringBuffer();
		for ( int i = 0 ; i < values.length;i++){
			buff.append(names[i]+": "+values[i]+"\n");
		}
		return buff.toString();
	}
	

	
	public static String printVector(int[] m){
		StringBuffer outstring = new StringBuffer();
		outstring.append("   ");
		for ( int x=0;x<m.length;x++){
			if ( m[x] >= 0 ){
					outstring.append("  ");
			} else {	
				outstring.append(" ");
			}
			outstring.append(d3.format(m[x]));				
		}
		outstring.append("\n");
		return outstring.toString();
	}
	public static String printVector(double[] m,int mxdigits,char sep){
		StringBuffer outstring = new StringBuffer();
		DecimalFormat f;
		for ( int x=0;x<m.length;x++){
			switch (mxdigits){
			case 0:
				f=f0;
				break;
			case 1:
				f=f1;
				break;
			case 2:
				f=f2;
				break;
			case 3:
				f=f3;
				break;
			default:
				f=f0;
			}
			outstring.append(f.format(m[x]));
				outstring.append(sep);
		}
	//	outstring.append("\n");
		return outstring.toString();
	}
	
	public static String printMatrix(int[][] m){
		StringBuffer outstring = new StringBuffer();
		for ( int x=0;x<m.length;x++){
			for (int y=0;y<m[0].length;y++){
				outstring.append(d3.format(m[x][y]));
				outstring.append(" ");
			}
			outstring.append("\n");
		}
		return outstring.toString();
	}
	
	public static String printMatrix(double[][] m){
		StringBuffer outstring = new StringBuffer();
		for ( int x=0;x<m.length;x++){
			for (int y=0;y<m[0].length;y++){
				outstring.append(d3.format(m[x][y]));
				outstring.append(" ");
			}
			outstring.append("\n");
		}
		return outstring.toString();
	}
	
	public static String printMatrixDF3(double[][] m){
		StringBuffer outstring = new StringBuffer();
		for ( int x=0;x<m.length;x++){
			for (int y=0;y<m[0].length;y++){
				outstring.append(df3.format(m[x][y]));
				outstring.append(" ");
			}
			outstring.append("\n");
		}
		return outstring.toString();
	}
	

	public static String printMatrix(double[] m,int nr,int nc){
		if ( m.length < nr*nc ){ 
			throw new Error("The vector of size "+m.length+" cannot be printed as a "+nr+"x"+nc+" matrix");
		}
		StringBuffer outstring = new StringBuffer();
		for ( int x=0;x<nr;x++){
			for (int y=0;y<nc;y++){
				if ( m[x*nc+y]>=0){
					outstring.append(" ");
				}
				outstring.append(MathPrint.d1.format(m[x*nc+y]));
				outstring.append(" ");
			}
			outstring.append("\n");
		}
		return outstring.toString();
	}
	
	public static String printMatrix(String[] cnames,int[][] m){
		StringBuffer outstring = new StringBuffer();
		outstring.append("     ");
		for ( int x=0;x<m.length;x++){
			outstring.append(cnames[x]);
			outstring.append(" ");
		}
		outstring.append("\n");
		for ( int x=0;x<m.length;x++){
			outstring.append(cnames[x]);
			outstring.append(" ");
			for (int y=0;y<m[0].length;y++){
				outstring.append(d3.format(m[x][y]));
				outstring.append("  ");
			}
			outstring.append("\n");
		}
		return outstring.toString();
	}
	public static boolean isMultiple(int factor,int value){
		double frac = value/(double)factor;
		return JFMMath.isZero(frac - Math.floor(frac));
	}
	
}
