package jfm.model;
import java.util.*;

import jfm.model.Types.VariableType;
import jfm.model.WorkableHoursCalculator;
import jfm.xml.XMLSyntaxException;

/** Representation of physical characteristics of the farm such as soiltype and rainfall. 
 * Defines functions for calculating location dependent variables such as the number of workable hours 
 * available in particular periods of the year 
 * @author Ira Cooke 
 * \todo fix the Location class. Make it immutable (Important so that updates are done correctly) */
public final class Location extends VariableHolder { 
//	private double soiltype;
//	private double rainfall;
//	private double summernetevap;
	private final int numPeriods;
	private final double[] hoursCache;
	private final double[] freeTimeHoursCache;

	public Location(double st,double rf,int numPeriods_){
		setVariable(VariableType.SOILTYPE,st);
		setVariable(VariableType.RAINFALL,rf);
		numPeriods=numPeriods_;
		hoursCache=new double[numPeriods];
		freeTimeHoursCache=new double[numPeriods];
		refreshHoursCache();
	}
	
	
	public Location copy(){
		return new Location(getVariable(VariableType.SOILTYPE),getVariable(VariableType.RAINFALL),numPeriods);
	}
	
	private static HashMap<Integer,double[] > freeTimeHoursMap = new HashMap<Integer,double[]>();
	static{
		double[] hours26=new double[26];		
		hours26[0]=80;		
		hours26[1]=80;
		hours26[2]=80;
		hours26[3]=80;
		hours26[4]=85;
		hours26[5]=90;
		hours26[6]=100;
		hours26[7]=110;
		hours26[8]=120;
		hours26[9]=120;
		hours26[10]=120;
		hours26[11]=120;
		hours26[12]=120;
		hours26[13]=120;
		hours26[14]=120;
		hours26[15]=120;
		hours26[16]=120;
		hours26[17]=120;
		hours26[18]=120;
		hours26[19]=110;
		hours26[20]=100;
		hours26[21]=90;
		hours26[22]=90;
		hours26[23]=90;
		hours26[24]=90;
		hours26[25]=90;
		double[] hours13=new double[13];
		for(int i=0;i<13;i++){
			hours13[i]=hours26[i*2]+hours26[i*2+1];
		}
		double[] hours2=new double[2];
		hours2[0]=140*13;
		hours2[1]=140*13;

		freeTimeHoursMap.put(26, hours26);
		freeTimeHoursMap.put(13,hours13);
		freeTimeHoursMap.put(2, hours2);
	}
	
	private static HashMap<Integer,double[] > hoursMap = new HashMap<Integer,double[]>();
	static{
		double[] hours26=new double[26];
		double[] hours13=new double[13];
		double[] hours2=new double[2];
		/*
		hours26[0]=51;		
		hours26[1]=51;
		hours26[2]=50;
		hours26[3]=50;
		hours26[4]=78;
		hours26[5]=78;
		hours26[6]=88;
		hours26[7]=88;
		hours26[8]=118;
		hours26[9]=118;
		hours26[10]=120.4;
		hours26[11]=122.4;
		hours26[12]=125.4;
		hours26[13]=125.4;
		hours26[14]=118.4;
		hours26[15]=118.6;
		hours26[16]=110.6;
		hours26[17]=110.6;
		hours26[18]=110.6;
		hours26[19]=91.0;
		hours26[20]=79.8;
		hours26[21]=69.5;
		hours26[22]=56.3;
		hours26[23]=36.1;
		hours26[24]=36.9;
		hours26[25]=36.7;
		*/
		
		hours26[0]=0.0;		
		hours26[1]=0.0;
		hours26[2]=0.0;
		hours26[3]=0.7;
		hours26[4]=20.0;
		hours26[5]=47.8;
		hours26[6]=75.7;
		hours26[7]=103.5;
		hours26[8]=118.4;
		hours26[9]=118.4;
		hours26[10]=118.4;
		hours26[11]=118.4;
		hours26[12]=118.4;
		hours26[13]=118.4;
		hours26[14]=118.4;
		hours26[15]=117.3;
		hours26[16]=114.5;
		hours26[17]=100.0;
		hours26[18]=86.1;
		hours26[19]=72.3;
		hours26[20]=58.4;
		hours26[21]=44.6;
		hours26[22]=30.7;
		hours26[23]=16.9;
		hours26[24]=3.6;
		hours26[25]=0.0;		
		
		for ( int i=0;i<13;i++){
			hours13[i]=hours26[2*i]+hours26[2*i+1];
		}
		for( int i=0;i<2;i++){
			hours2[i]=0;
			for ( int j=0;j<13;j++){
				hours2[i]+=hours26[i*13+j];
			}
		}
		hoursMap.put(26, hours26);
		hoursMap.put(13, hours13);
		hoursMap.put(2, hours2);
	}
	public static HashMap<String,Integer> month=new HashMap<String,Integer>();
	static {
		month.put("Jan",0);
		month.put("Feb",1);
		month.put("Mar",2);
		month.put("Apr",3);
		month.put("May",4);
		month.put("Jun",5);
		month.put("Jul",6);
		month.put("Aug",7);
		month.put("Sep",8);
		month.put("Oct",9);
		month.put("Nov",10);
		month.put("Dec",11);
	}
	
	/** In order to provide compatibility with the WFM this class is needed. 
	 * Each date represents the first day in a given period. The periods given are 
	 * unfolded periodic .. in order to fold use period-(year-1)*26 */
	public static ArrayList<GregorianCalendar> periods26=new ArrayList<GregorianCalendar>();
	static {
		periods26.add(new GregorianCalendar(2000,month.get("Jul"),16,0,0));//14
		periods26.add(new GregorianCalendar(2000,month.get("Jul"),30,0,0));//15
		periods26.add(new GregorianCalendar(2000,month.get("Aug"),13,0,0));//16
		periods26.add(new GregorianCalendar(2000,month.get("Aug"),27,0,0));//17
		periods26.add(new GregorianCalendar(2000,month.get("Sep"),10,0,0));//18
		periods26.add(new GregorianCalendar(2000,month.get("Sep"),24,0,0));//19
		periods26.add(new GregorianCalendar(2000,month.get("Oct"),8,0,0));//20
		periods26.add(new GregorianCalendar(2000,month.get("Oct"),22,0,0));//21
		periods26.add(new GregorianCalendar(2000,month.get("Nov"),5,0,0));//22
		periods26.add(new GregorianCalendar(2000,month.get("Nov"),19,0,0));//23
		periods26.add(new GregorianCalendar(2000,month.get("Dec"),3,0,0));//24
		periods26.add(new GregorianCalendar(2000,month.get("Dec"),17,0,0));//25
		periods26.add(new GregorianCalendar(2001,month.get("Jan"),1,0,0));//26
		periods26.add(new GregorianCalendar(2001,month.get("Jan"),15,0,0));//27
		periods26.add(new GregorianCalendar(2001,month.get("Jan"),29,0,0));//28
		periods26.add(new GregorianCalendar(2001,month.get("Feb"),12,0,0));//29
		periods26.add(new GregorianCalendar(2001,month.get("Feb"),26,0,0));//30
		periods26.add(new GregorianCalendar(2001,month.get("Mar"),12,0,0));//31
		periods26.add(new GregorianCalendar(2001,month.get("Mar"),26,0,0));//32
		periods26.add(new GregorianCalendar(2001,month.get("Apr"),9,0,0));//33
		periods26.add(new GregorianCalendar(2001,month.get("Apr"),23,0,0));//34
		periods26.add(new GregorianCalendar(2001,month.get("May"),7,0,0));//35
		periods26.add(new GregorianCalendar(2001,month.get("May"),21,0,0));//36
		periods26.add(new GregorianCalendar(2001,month.get("Jun"),4,0,0));//37
		periods26.add(new GregorianCalendar(2001,month.get("Jun"),18,0,0));//38
		periods26.add(new GregorianCalendar(2001,month.get("Jul"),1,0,0));//39
		periods26.add(new GregorianCalendar(2001,month.get("Jul"),16,0,0));//40
		periods26.add(new GregorianCalendar(2001,month.get("Jul"),30,0,0));//41
		periods26.add(new GregorianCalendar(2001,month.get("Aug"),13,0,0));//42
		periods26.add(new GregorianCalendar(2001,month.get("Aug"),27,0,0));//43
		periods26.add(new GregorianCalendar(2001,month.get("Sep"),10,0,0));//44
		periods26.add(new GregorianCalendar(2001,month.get("Sep"),24,0,0));//45
		periods26.add(new GregorianCalendar(2001,month.get("Oct"),8,0,0));//46
		periods26.add(new GregorianCalendar(2001,month.get("Oct"),22,0,0));//47
		periods26.add(new GregorianCalendar(2001,month.get("Nov"),5,0,0));//48
		periods26.add(new GregorianCalendar(2001,month.get("Nov"),19,0,0));//49
		periods26.add(new GregorianCalendar(2001,month.get("Dec"),3,0,0));//50
		periods26.add(new GregorianCalendar(2001,month.get("Dec"),16,0,0));//51
	}
	
	/** In order to provide compatibility with the WFM this class is needed. 
	 * Each date represents the first day in a given period. The periods given are 
	 * unfolded periodic .. in order to fold use period-(year-1)*26 */
	public static ArrayList<GregorianCalendar> periods13=new ArrayList<GregorianCalendar>();
	static {
		periods13.add(new GregorianCalendar(2000,month.get("Jul"),16,0,0));//7
		periods13.add(new GregorianCalendar(2000,month.get("Aug"),13,0,0));//8
		periods13.add(new GregorianCalendar(2000,month.get("Sep"),10,0,0));//9
		periods13.add(new GregorianCalendar(2000,month.get("Oct"),8,0,0));//10
		periods13.add(new GregorianCalendar(2000,month.get("Nov"),5,0,0));//11
		periods13.add(new GregorianCalendar(2000,month.get("Dec"),3,0,0));//12
		periods13.add(new GregorianCalendar(2001,month.get("Jan"),1,0,0));//13
		periods13.add(new GregorianCalendar(2001,month.get("Jan"),29,0,0));//14
		periods13.add(new GregorianCalendar(2001,month.get("Feb"),26,0,0));//15
		periods13.add(new GregorianCalendar(2001,month.get("Mar"),26,0,0));//16
		periods13.add(new GregorianCalendar(2001,month.get("Apr"),23,0,0));//17
		periods13.add(new GregorianCalendar(2001,month.get("May"),21,0,0));//18
		periods13.add(new GregorianCalendar(2001,month.get("Jun"),18,0,0));//19
		periods13.add(new GregorianCalendar(2001,month.get("Jul"),16,0,0));//20
		periods13.add(new GregorianCalendar(2001,month.get("Aug"),13,0,0));//21
		periods13.add(new GregorianCalendar(2001,month.get("Sep"),10,0,0));//22
		periods13.add(new GregorianCalendar(2001,month.get("Oct"),8,0,0));//23
		periods13.add(new GregorianCalendar(2001,month.get("Nov"),5,0,0));//24
		periods13.add(new GregorianCalendar(2001,month.get("Dec"),3,0,0));//25
	}
	
	public static ArrayList<GregorianCalendar> periods2=new ArrayList<GregorianCalendar>();
	static {
		periods2.add(new GregorianCalendar(2000,month.get("Jul"),16,0,0));//14
		periods2.add(new GregorianCalendar(2001,month.get("Jan"),15,0,0));//27
	}
	
	public static HashMap<Integer,ArrayList<GregorianCalendar>> periodsmap=new HashMap<Integer,ArrayList<GregorianCalendar>>();
	static{
		periodsmap.put(26,periods26);
		periodsmap.put(13,periods13);
		periodsmap.put(2,periods2);
	}
	public static int getWFMPeriod(GregorianCalendar caldate,int nperiods) throws XMLSyntaxException {
		ArrayList<GregorianCalendar> periods = periodsmap.get(nperiods);		
		int wfmp;
		switch(nperiods){
		case 26:
			wfmp=14;
			break;
		case 13:
			wfmp=7;
			break;
		default:
			throw new XMLSyntaxException("Allowed numbers of periods are 2 26 or 13");
		}
		for(GregorianCalendar startperiod:periods ) {
			if(!startperiod.before(caldate)){
				return wfmp;
			}
			wfmp++;
		}
		throw new XMLSyntaxException("Date is outside WFM period range");
	}
	private double numberOfHoursInPeriodWithPeriods(int per,int numPeriods){
		int numPer = periodsmap.get(numPeriods).size();
		int offset=0;
		switch(numPeriods){
		case 26:
			offset=12;
			break;
		case 13:
			offset=6;
			break;
		case 2:
			offset=1;
			break;
		default:
			throw new Error("Allowed numbers of periods are 2 26 13");
		}
		per=per+offset;
		GregorianCalendar dstart;
		GregorianCalendar dfinish;
//		if ( per > numPer-1 ){
//			dstart=periodsmap.get(numPeriods).get(per-numPer);
//		} else {
		dstart = periodsmap.get(numPeriods).get(per);
//		}
		
		
		if ( per > numPer-2 ){
			dfinish=periodsmap.get(numPeriods).get(offset);
		} else {
			dfinish = periodsmap.get(numPeriods).get(per+1);
		}
	//	System.out.println("Start p "+dstart.get(GregorianCalendar.DAY_OF_YEAR)+" end: "+dfinish.get(GregorianCalendar.DAY_OF_YEAR));
		return WorkableHoursCalculator.workableHours(dstart.get(GregorianCalendar.DAY_OF_YEAR), dfinish.get(GregorianCalendar.DAY_OF_YEAR),getVariable(VariableType.SOILTYPE),getVariable(VariableType.RAINFALL));
	}
	
	private void refreshHoursCache(){
	//	System.out.println("Refreshing Hours Cache with: soil "+getVariable(VariableType.SOILTYPE)+" rf "+getVariable(VariableType.RAINFALL));
		for(int i=0;i<hoursCache.length;i++){
			// This is old code for when we needed to use fixed values 
//			hoursCache[i]=hoursMap.get(numPeriods)[i];
			
			hoursCache[i]=numberOfHoursInPeriodWithPeriods(i,numPeriods);
	//		System.out.println("Num Hours in period "+i+" "+hoursCache[i]+" "+freeTimeHoursMap.get(numPeriods)[i]);
			freeTimeHoursCache[i]=freeTimeHoursMap.get(numPeriods)[i];
		}
		
		
		
	}
	/*
	public double soiltype(){
		return soiltype;
	}
	public double rainfall(){
		return rainfall;
	}*/

	public double availableHours(int period){
//		System.out.println("In period "+period+" we get "+hoursCache[period]);
		return hoursCache[period];
	}
	public double availableFreeTimeHours(int period){
		return freeTimeHoursCache[period];
	}
	
	public void setSoilType(double soil){ 
		setVariable(VariableType.SOILTYPE,soil);
		refreshHoursCache();
	}
	public void setRainFall(double rain){ 
		setVariable(VariableType.RAINFALL,rain);
		refreshHoursCache();
	}
	public void set(double sl,double rn){
		setVariable(VariableType.SOILTYPE,sl);
		setVariable(VariableType.RAINFALL,rn);
		refreshHoursCache();
	}
	
	
}

