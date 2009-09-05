#ifndef CBC_H_
#define CBC_H_

#include <coin/CbcModel.hpp>
#include <coin/OsiClpSolverInterface.hpp>
#include <jni.h>
#include <iostream>


#define FALSE 0
#define TRUE 1


/** Free variable */
#define LPX_FR 110
/** Variable with lower bound */
#define LPX_LO 111
/** Variable with upper bound */
#define LPX_UP 112
/** Double bounded (upper and lower) variable */
#define LPX_DB 113
/** Fixed variable */
#define LPX_FX 114	
#define	LPX_LP 100
#define LPX_MIP	101
#define LPX_CV 160
#define LPX_IV 161
#define LPX_NOTOPT 179;
#define LPX_OPT 180;
#define LPX_FEAS 181;
#define LPX_INFEAS 182;
#define LPX_NOFEAS 183;
#define LPX_UNBND 184;
#define LPX_UNDEF 185;

class CBC
{
private:
	OsiClpSolverInterface* solver;

	int nrows;
	int ncols;
  	int problemType;
  	int arraysLoaded;
  	int matrixLoaded;
  	int needsInitialSolve;
  	int solutionStatus;
  	int pvectorLoaded;
	double* pmatrixv;
	jint* pmatrixri;
	jint* pmatrixci;
	double* soln;
	double* pvector;
	double* cbounds;
	double* rbounds;
	jint* cboundtypes;
	jint* rboundtypes;
	jint* columntypes;
	char** colnames_c;
	char** rownames_c;
	jsize matrixsize,vectorsize,rboundssize,cboundssize,rboundtypessize,cboundtypessize,columntypessize;  	

	void getArrayElements(JNIEnv *env,jdoubleArray soln_j,jdoubleArray pvector_j,jdoubleArray cbounds_j,
				jintArray cboundtypes_j, jdoubleArray pmatrixv_j,jintArray pmatrixri_j,jintArray pmatrixci_j, 
				jdoubleArray rbounds_j, jintArray rboundtypes_j,
				jintArray columntypes_j,jobjectArray rownames_j,jobjectArray colnames_j);
		void releaseArrayElements(JNIEnv *env,jdoubleArray soln_j,jdoubleArray pvector_j,jdoubleArray cbounds_j,
				jintArray cboundtypes_j, jdoubleArray pmatrixv_j,jintArray pmatrixri_j,jintArray pmatrixci_j,
				jdoubleArray rbounds_j, jintArray rboundtypes_j,
				jintArray columntypes_j,int method);
		void getStructArrayElements(JNIEnv *env,jdoubleArray soln_j,jdoubleArray pvector_j,jdoubleArray cbounds_j,
				jintArray cboundtypes_j,jintArray columntypes_j);
		void releaseStructArrayElements(JNIEnv *env,jdoubleArray soln_j,jdoubleArray pvector_j,jdoubleArray cbounds_j,
				jintArray cboundtypes_j,jintArray columntypes_j);

		
		void loadMatrix();
		void initSolver();
		void solve();
public:
	CBC(JNIEnv *env,jdoubleArray soln_j,jdoubleArray pvector_j,jdoubleArray cbounds_j,
				jintArray cboundtypes_j, jdoubleArray pmatrixv_j,jintArray pmatrixri_j,jintArray pmatrixci_j, 
				jdoubleArray rbounds_j, jintArray rboundtypes_j,
				jintArray columntypes_j,jobjectArray rownames_j,jobjectArray colnames_j);
	jint solveWithNewCoefficients(JNIEnv *env,jdoubleArray soln_j,jdoubleArray pvector_j,jdoubleArray cbounds_j,
				jintArray cboundtypes_j,jintArray columntypes_j);
	jint solveWithNewProblem(JNIEnv *env,jdoubleArray soln_j,jdoubleArray pvector_j,jdoubleArray cbounds_j,
				jintArray cboundtypes_j, jdoubleArray pmatrixv_j,jintArray pmatrixri_j,jintArray pmatrixci_j,
				jdoubleArray rbounds_j, jintArray rboundtypes_j,
				jintArray columntypes_j,jobjectArray rownames_j,jobjectArray colnames_j);
		
	
	virtual ~CBC();
};

#endif /*CBC_H_*/
