/** \internal The utils package */
package jfm.utils;
import java.util.*;

/** Defines a range of initializer functions for primitive arrays and vectors */
public class JFMMath {
	public static int chooseWeightedRandom(double[] weights,double rand){
//		double[] probs=new double[weights.length];
//		for(int i=0;i<weights.length;i++){
//			System.out.print(weights[i]+" ");
//		}
//		System.out.print(rand+" "+sum(weights)+" ");
		double total=sum(weights);
		if ( total==0){
			throw new Error("Weights must not sum to zero!");
		}
		double prob=0;
		for(int i=0;i<weights.length;i++){
			prob+=weights[i]/total;
			if ( prob >= rand){
//				System.out.print(prob+" .. chose "+i+"\n");
				return i;
			}
		}
		throw new Error("Unexpected error when choosing weighted random "+prob+" "+total);
	}
	
	public static double sum(List<Double> arr){
		double tot=0;
		for(Double d:arr){
			tot+=d;
		}
		return tot;
	}
	
	public static double avArray(ArrayList<Double> arr){
		if ( arr.size()==0){
			return 0;
		}
		double tot=0;
		for(Double d:arr){
			tot+=d;
		}
		return tot/arr.size();
	}
	
	
	public static double max(double[] arr){
		double max=arr[0];
		for( double e:arr){
			if ( e>max){max=e;}
		}
		return max;
	}
	public static double min(double[] arr){
		double min=arr[0];
		for( double e:arr){
			if ( e<min){min=e;}
		}
		return min;
	}
	public static double[] seq(double min,double max,int n){
		double range=max-min;
		double del=range/(n-1);
		double[] sq=new double[n];
		sq[0]=min;
		for(int i=0;i<(n-1);i++){
			sq[i+1]=sq[i]+del;
		}
		return sq;
	}
	

	// 	Floating point numerical tolerance is 1e-16 but we use 1e-12 .
	public static double numTol = 1e-8;
	
	public static int sum2(int[][] array){
		int sum = 0;
		for ( int i = 0 ; i < array.length; i++){
			for ( int j = 0 ; j < array[0].length; j++){
				sum+=array[i][j];
			}
		}
		return sum;
	}
	public static double sum(double[] vec){
		double sum=0;
		for ( int i = 0 ; i < vec.length;i++){
			sum+=vec[i];
		}
		return sum;
	}
	public static double sum(Double[] vec){
		double sum=0;
		for ( int i = 0 ; i < vec.length;i++){
			sum+=vec[i];
		}
		return sum;
	}
	public static double[] pDistFromCounts(int[] vec){
		double[] dist=new double[vec.length];
		double total=sum(vec);
		if ( total == 0){
			throw new Error("Cant generate distribution if all counts are zero");
		}
		for(int i=0;i<vec.length;i++){
			dist[i]=vec[i]/total;
		}
		return dist;
	}
	public static int sum(int[] vec){
		int sum=0;
		for(int v:vec){
			sum+=v;
		}
		return sum;
	}
	
	public static void normalize(ArrayList<Double> vec){
		double total=JFMMath.sum(vec);
		if ( total == 0){
			throw new Error("Can't normalize zero vector");
		} else {
			for(int i=0;i<vec.size();i++){
				vec.set(i, vec.get(i)/total);				
			}
		}
	}
	
	public static void normalize(double[] vec){
		double s=sum(vec);
		if ( s == 0 ){
			throw new Error("Can't normalize zero vector");
		}
		for(int i=0;i<vec.length;i++){
			vec[i]/=s;
		}
	}
	
	public static int rowSubtotal(int rcfrom, int r,int[][] m){
		int sum=0;
		for ( int c= rcfrom;c<m.length;c++){
			sum+=m[r][c];
		}
		return sum;
	}
	
	public static int colSubtotal(int rcfrom, int c,int[][] m){
		int sum=0;
		for ( int r= rcfrom;r<m.length;r++){
			sum+=m[r][c];
		}
		return sum;
	}
	
	public static int sign(int val){
		if ( val < 0 ){
			return -1;
		}
		if ( val == 0 ){ return 0;};
		if ( val > 0 ){ return 1;};
		return 0;
	}
	
	public static boolean isZero(double val){
		if ( val*val < numTol ){
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isZero(int[] vec){
		for ( int i = 0 ; i < vec.length;i++){
			if ( vec[i] != 0 ){ return false;}
		}
		return true;
	}
	public static int largestNZElement(int[] vec){
		int larg = 0;
		for ( int i = 0 ; i < vec.length;i++){
			if ( vec[i] >= vec[larg] ){
				larg=i;
			}
		}
		if ( vec[larg] == 0 ){
			return -1;
		} else {
			return larg;
		}
	}
	
	public static void matrixnull(Object[][] array){
		for ( int i = 0 ; i < array.length; i++){
			for ( int j = 0 ; j < array[0].length; j++){
				array[i][j] = null;
			}
		}
	}
	public static void string(String[] vec,String val){
		for ( int i =0; i< vec.length;i++){
			vec[i]=val;
		}
	}
	
	public static void intZero1(int[] vec){
		for ( int i = 0 ; i < vec.length;i++){
			vec[i]=0;
		}
	}
	public static void intMinus1(int[] vec){
		for ( int i = 0 ; i < vec.length;i++){
			vec[i]=-1;
		}
	}
	
	public static void intZero2(int[][] array){
		for ( int i = 0 ; i < array.length; i++){
			for ( int j = 0 ; j < array[0].length; j++){
				array[i][j] = 0;
			}
		}
	}
	
	public static void doubleZero(double[] array){
		for (int i = 0 ; i < array.length;i++){
			array[i]=0.0;
		}
	}
	
	public static void doubleZero(double[][] array){
		for (int i = 0 ; i < array.length;i++){
			for ( int j = 0 ; j < array[0].length;j++){
				array[i][j]=0.0;
			}
		}
	}
}
