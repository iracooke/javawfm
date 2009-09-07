#ifndef GLPKJNI_H_
#define GLPKJNI_H_
extern "C" {
	#include "glpk.h"
}
#include <iostream>
#include <stdlib.h>
#include <math.h>
#include <jni.h>

#define FALSE 0
#define TRUE 1
class GLPK
{
private:
	LPX *lp;
	int nrows;
	int ncols;
  	int problemType;
  	int arraysLoaded;
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
	int getProblemType();
	void setAndCheckMatrixSizes();
	void initProblem();
	void initSettings();
	void initStructure();
	void initConstraints();
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
	void Solve();
public:
	GLPK(JNIEnv *env,jdoubleArray soln_j,jdoubleArray pvector_j,jdoubleArray cbounds_j,
			jintArray cboundtypes_j, jdoubleArray pmatrixv_j,jintArray pmatrixri_j,jintArray pmatrixci_j, 
			jdoubleArray rbounds_j, jintArray rboundtypes_j,
			jintArray columntypes_j,jobjectArray rownames_j,jobjectArray colnames_j);
	
	jint solveWithNewCoefficients(JNIEnv *env,jdoubleArray soln_j,jdoubleArray pvector_j,jdoubleArray cbounds_j,
			jintArray cboundtypes_j,jintArray columntypes_j);
	jint solveWithNewProblem(JNIEnv *env,jdoubleArray soln_j,jdoubleArray pvector_j,jdoubleArray cbounds_j,
			jintArray cboundtypes_j, jdoubleArray pmatrixv_j,jintArray pmatrixri_j,jintArray pmatrixci_j,
			jdoubleArray rbounds_j, jintArray rboundtypes_j,
			jintArray columntypes_j,jobjectArray rownames_j,jobjectArray colnames_j);
	void setTermOut(JNIEnv *env,jint flag);
	virtual ~GLPK();
};


#endif /*GLPKJNI_H_ */