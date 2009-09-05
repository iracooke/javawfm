// Pure JNI code for the CBCPeer class here

#include <jni.h>
#include "./CBC.h"
#include "./jfm_lp_CBCPeer.h"
//#include <iostream>


/*! Create a new CBCPeer object with a given LP matrix */
JNIEXPORT jlong JNICALL
Java_jfm_lp_CBCPeer_create(JNIEnv *env, jobject,jdoubleArray soln_j,
		jdoubleArray pvector_j,jdoubleArray cbounds_j,
				jintArray cboundtypes_j, jdoubleArray pmatrixv_j,
				jintArray pmatrixri_j,jintArray pmatrixci_j, 
				jdoubleArray rbounds_j, jintArray rboundtypes_j,
				jintArray columntypes_j,jobjectArray rownames_j,jobjectArray colnames_j) {	
	CBC* ptr;
	
	ptr = new CBC(env,soln_j,pvector_j,cbounds_j,cboundtypes_j,pmatrixv_j,pmatrixri_j,pmatrixci_j, rbounds_j, rboundtypes_j,columntypes_j,rownames_j,colnames_j);
	return (jlong)ptr;
}

/*! Solve a given LP matrix. We always use this function when solving */
JNIEXPORT jint JNICALL
Java_jfm_lp_CBCPeer_solveWithNewProblem(JNIEnv *env, jobject,jdoubleArray soln_j,
		jdoubleArray pvector_j,jdoubleArray cbounds_j,
				jintArray cboundtypes_j, jdoubleArray pmatrixv_j,jintArray pmatrixri_j,jintArray pmatrixci_j, 
				jdoubleArray rbounds_j, jintArray rboundtypes_j,
				jintArray columntypes_j,jobjectArray rownames_j,jobjectArray colnames_j,jlong objptr) {	
	CBC* ptr = (CBC*)objptr;
	jint returnval=ptr->solveWithNewProblem(env,soln_j,pvector_j,cbounds_j,
			cboundtypes_j, pmatrixv_j, pmatrixri_j,pmatrixci_j,
			rbounds_j, rboundtypes_j,
			columntypes_j,rownames_j,colnames_j);
	return returnval;
}

/*! Unsupported function for solving a matrix whose coefficients have changed but matrix has remained the same */
/*JNIEXPORT jint JNICALL
Java_jfm_lp_CBCPeer_solveWithNewCoefficients(JNIEnv *env, jobject,jdoubleArray soln_j,
		jdoubleArray pvector_j,jdoubleArray cbounds_j,
		jintArray cboundtypes_j,jintArray columntypes_j,jlong objptr) {	
	CBC* ptr = (CBC*)objptr;
	ptr->solveWithNewCoefficients(env,soln_j,pvector_j,cbounds_j,cboundtypes_j,columntypes_j);
}*

/*! Also unsupported. For setting whether to spew output to the user or not */
/*JNIEXPORT void JNICALL
Java_jfm_lp_CBCPeer_setTermOut(JNIEnv *env, jobject, jint objptr,jlong flag) {
	CBC* ptr=(CBC*)objptr;
	ptr->setTermOut(env,flag);
}*/

/*! Release memory associated with the solver */
JNIEXPORT void JNICALL
Java_jfm_lp_CBCPeer_destroy(JNIEnv *, jobject, jlong objptr) {
	CBC* ptr = (CBC*)objptr;
	delete ptr;
}
