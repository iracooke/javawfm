package jfm.xml;
import java.util.regex.*;
import java.util.*;
import jfm.model.Location;
import jfm.model.Types.*;


/** Defines methods for converting text field data into various formats */
public final class TextConverter {
	public static double[] toDouble(String[] vals) throws XMLSyntaxException{
		double[] rvals = new double[vals.length];
		try {
			for ( int i = 0 ; i < vals.length;i++){
				rvals[i]=Double.parseDouble(vals[i]);
			}
		} catch (NumberFormatException ex){
			throw new XMLSyntaxException("Bad double format ");
		}
		return rvals;
	}
	
	public static double[][] toDouble(String[] vals,int size) throws XMLSyntaxException{
		if ( size*size != vals.length){
			throw new XMLSyntaxException("Attempt to convert strings to square matrix but incorrect number of elements "+vals.length+" for matrix "+size+"x"+size);
		}
		double[][] rvals = new double[size][size];
		try {
			for ( int i = 0 ; i < size;i++){
				for(int j=0;j<size;j++){
					rvals[i][j]=Double.parseDouble(vals[i*size+j]);
				}
			}
		} catch (NumberFormatException ex){
			throw new XMLSyntaxException("Bad double format ");
		}
		return rvals;
	}
	
	public static int[] toInt(String[] vals) throws XMLSyntaxException{
		int[] rvals = new int[vals.length];
		try {
			for ( int i = 0 ; i < vals.length;i++){
				rvals[i]=Integer.parseInt(vals[i]);
			}
		} catch (NumberFormatException ex){
			throw new XMLSyntaxException("Bad integer format ");
		}
		return rvals;
	}
	
	private static Pattern workerPattern= Pattern.compile("(\\w*\\-?\\w*)\\s*(\\d+)?");
	public static Map<WorkerType,Integer> toNumMachines(String[] vals) throws XMLSyntaxException{
		Map<WorkerType,Integer> nmach=new LinkedHashMap<WorkerType,Integer>();// Need to make sure first machine stays first
		try {
		for( int w =0; w< vals.length;w++){
			Matcher wkmatch=workerPattern.matcher(vals[w]);
			if ( wkmatch.find()){
				int num=1;
				WorkerType wtype=jfm.model.Types.xmlToWorkerType(wkmatch.group(1));
				if ( wkmatch.group(2) != null ){
					num=Integer.parseInt(wkmatch.group(2));
				} 
				nmach.put(wtype, num);
			} else {
				throw new XMLSyntaxException("Not a valid format for wktype ");
			}
		}
		} catch (XMLSyntaxException tex){
			tex.printStackTrace();
			throw new XMLSyntaxException(tex.getMessage());
		}
		return nmach;
	}
	
	private static Pattern datePattern = Pattern.compile("(\\d)\\s*:\\s*(\\w{3})\\s*(\\d+)");

	
	/** Convert a comma separated string specifying from and to dates to an array of integers 
	 * specifying the periods between and including those dates. */
	public static int[] toPeriods(String[] fromto,int numPeriods) throws XMLSyntaxException{
		if ( fromto.length!=2){
			throw new XMLSyntaxException("periods should be specified as exactly 2 comma separated fields " +
					" but "+fromto.length+" specified ");
		}
		int[] periods;
		GregorianCalendar fromdate;
		GregorianCalendar todate;
		Matcher from=datePattern.matcher(fromto[0]);
		Matcher to=datePattern.matcher(fromto[1]);
		if ( from.find() && to.find()){
			if ( from.groupCount() == 3 && to.groupCount()==3){
				int fromperiod,toperiod;
				if ( Location.month.get(from.group(2))!=null){
					int fromyr=Integer.parseInt(from.group(1))-1;
					fromdate=
					new GregorianCalendar(2000+fromyr,Location.month.get(from.group(2)),
							Integer.parseInt(from.group(3)),0,0);

					fromperiod=Location.getWFMPeriod(fromdate,numPeriods);
//					fromperiod=(int)Math.floor(fromdate.get(fromdate.DAY_OF_YEAR)*(double)numPeriods/(double)365);
//					fromperiod+=numPeriods*(Integer.parseInt(from.group(1))-1);
			//		System.out.println("from p"+fromperiod);
				} else {
					throw new XMLSyntaxException("Incorrect month specification in from field ");
				}
				if ( Location.month.get(to.group(2))!=null){
					int toyr=Integer.parseInt(to.group(1))-1;
					todate=
						new GregorianCalendar(2000+toyr,Location.month.get(to.group(2)),Integer.parseInt(to.group(3)),0,0);
					toperiod=Location.getWFMPeriod(todate,numPeriods);
//					toperiod=(int)Math.floor(todate.get(todate.DAY_OF_YEAR)*(double)numPeriods/(double)365);
//					toperiod+=numPeriods*(Integer.parseInt(to.group(1))-1);
			//		System.out.println("to p "+toperiod);
				} else {
					throw new XMLSyntaxException("Incorrect month specification in to field ");
				}
				int pwidth=toperiod-fromperiod;
				if ( pwidth==0 ){ pwidth=1;};
				if ( pwidth > 0){
					periods=new int[pwidth];
					for ( int i=0;i<pwidth;i++){
						periods[i]=fromperiod+i;
					}
				} else {
					throw new XMLSyntaxException("from date must be before to date"+fromto[0]+" "+fromperiod+" to "+fromto[1]+" "+toperiod);
				}
				return periods;
			}
		}	
		
	throw new XMLSyntaxException("Failed to parse dates ");
//		return periods;
	}
	
}
