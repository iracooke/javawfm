/**
 * 
 */
package jfm.model;

/**
 * @author iracooke
 * This class implements a single function whose purpose is to calculate the number of workable hours in a given period. 
 * The function is translated from the VB code in ACCELLFNwkhrs of RunACCEL.bas in the Cranfield/Silsoe Whole Farm Model code. 
 * The original code was written as a series of goto statements.  
 * I have reproduced this in java using a switch statement with four functions indicating the four goto labels in the original code
 * 
 */
public class WorkableHoursCalculator {
	private double Lhrday = 9.95;
	private double SD;
	private double FD,h;
	private double was,ALPHA,BETA,ACCELFNwkhrs,b1,b2,b3,x,LTI,isbeta,IDONE;
	private double HHH=0;
	private double ds,df;
	private double Arain,Asoil,Ad1,Ad2;

	public static double workableHours(double Ad1_,double Ad2_,double Asoil_,double Arain_){
		WorkableHoursCalculator calc = new WorkableHoursCalculator();
		return calc.calculateWorkableHours(Ad1_, Ad2_, Asoil_, Arain_);
	}
	
	/** The arguments in order are; The first day of the interval, the first day of the following interval, the soil type in WFM units, and the rainfall in mm */
    private double calculateWorkableHours(double Ad1_,double Ad2_,double Asoil_,double Arain_){
    	Ad1=Ad1_;
    	Ad2=Ad2_;
    	Asoil=Asoil_;
    	Arain=Arain_;
	
    	int state = 0;
    	while (state >= 0) {
    		switch (state) {
    		case 0: 
    			state=firstBit();	
    			break;
    		case 1:     			
    			state=firstLabel();
    			break;
    		case 2:
    			state=secondLabel();
    			break;
    		case 3:
    			state=thirdLabel();
    			break;
    		case 4:
    			state=fourthLabel();
    			break;
    		default:
    			throw new Error("Undefined state");
    		}
    	}	
    return ACCELFNwkhrs;
    }

    private int firstBit(){
	// Setting up the constants
	// Lhrday = basic average hours per day in summer


	// breakpoint constants
	b1 = 113; //part of formula W=9.95-(0.5075-0.00253*LTI)*(113-d) where d=day number
	    // 0.5 = start of January ie. integral between 0.5 and 1.5 gives hours for day 1
	b2 = 212; // break point for start August 212=31st July
	    //'W=9.95-0.18*d when d<(1.29LTI-155) and W=0.08(LTI-d) otherwise; where d=1 is first August
	b3 = 365; // break point for end of year

	// land type indicator
	// x = (1.257 - 0.257 * Asoil) * (Arain% * 0.001) + 0.462 * (Asoil - 1!) - 0.1
	x = (1.257 - 0.257 * Asoil) * (Arain * 0.001) + 0.762 * (Asoil - 1);

	if (Arain < 500) {
	    x = x - 0.005 * (500 - Arain);
	    // If X < 0.14 Then X = 0.14'max LTI of 200 to prevent upward slopes, now done at slope
	}
	// At this point x is on the order 1-2
	LTI = 20.6*x*x - 89*x + 212;
	// LTI is roughly 100?

	isbeta = (0.5075 - 0.00253 * LTI);  // 0.18 or so
	if( isbeta < 0.01) 
	    isbeta = 0.01;
	
	IDONE = 0;
	HHH = 0;

	// setup start and finish days for the period. Use periodic boundary conditions
	ds = Ad1 - 0.5;
	if (ds > b3) ds = ds - b3;

	df = Ad2 - 0.5; 
	if (df > b3) df = df - b3;
	return 1;// Goto first label
    }

    private int firstLabel(){//	FirstLabel: //15870
	if (ds < b1 - 1)  return 2; //15920 'b1%-1 since if Ady1%=113 => ds=112.5 and this is summer part of curve
	if (ds < b2)  return 3;//15950

	// Rem August to December
	SD = ds - b2; // 'Aug 1st is day 1
	if (df < b2) { //'2 parts of curve used
	    FD = b3 - 0.5 - b2; //'set final value to end of year
	    ds = 0.5; //'re-set ds to start of year
	} else {
	    IDONE = 1;
	    FD = df - b2;
	}

	//set up formula parameters
	was = 1.29 * LTI - 155; // place where lines meet
	if (SD > was) {//'(9.95 - 0.08 * LTI) / (0.1 * isbeta - 0.08)) Then
	    BETA = -(9.95 - isbeta * was / 10) / (LTI - was); // '-0.08
	    ALPHA = -BETA * LTI;
	} else {
	    ALPHA = Lhrday;
	    BETA = -isbeta / 10;
	}
	return 4;
    }

    private int secondLabel(){//	SecondLabel:// 15920  Rem January to April
	SD = ds;// 'Jan 1st = 1
	if (df > b1) { //'part of next bit of curve
	    FD = b1 - 0.5;
	    ds = b1 - 0.5;// 'set to start of next bit of curve
	} else {
	    FD = df;
	    IDONE = 1;
	}

	BETA = isbeta;
	ALPHA = Lhrday - b1 * BETA;
	return 4;
	//	continue FourthLabel; //  GoTo 16070
    }

    private int thirdLabel(){//	ThirdLabel: // 15950  Rem May to July
	SD = ds;
	if (df < b1 - 1 || df > b2 + 1){// Then 'crossing other parts of curve
	    FD = b2 + 0.5;
	    ds = b2 + 0.5 ;//'set to next part of curve
	} else {
	    IDONE = 1;
	    FD = df;
	}

	BETA = 0;
	ALPHA = Lhrday;
	return 4;
    }

    private int fourthLabel(){//	FourthLabel: // 16070 'Now calculate hours
	//'if graph goes below zero then is zero!
	if (ALPHA + BETA * SD <= 0) 
	    SD = -ALPHA / BETA;
	if (ALPHA + BETA * FD <= 0) 
	    FD = -ALPHA / BETA;
	// 'do integration between SD and FD
	h = (FD - SD) * (ALPHA + BETA * (FD + SD) / 2);
	if (h < 0) 
	    h = 0;

	HHH = HHH + h;
	if (IDONE == 0) // Ira Comment .. careful here is this a bitwise operator in VB?
	    return 1; // 15870 'covers more than one bit of curve
	//'If HHH < 70 Then HHH = HHH + 20
	//'Multiply by factor of 0.85 for unproductive time etc. (in line with Nix)
	ACCELFNwkhrs = HHH * 0.85;
	return -1;
    }

}
