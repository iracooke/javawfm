#include <jni.h>
#include "./GLPK.h"
#include "./jfm_lp_GLPKPeer.h"

JNIEXPORT jlong JNICALL
Java_jfm_lp_GLPKPeer_create(JNIEnv *env, jobject,jdoubleArray soln_j,
		jdoubleArray pvector_j,jdoubleArray cbounds_j,
				jintArray cboundtypes_j, jdoubleArray pmatrixv_j,
				jintArray pmatrixri_j,jintArray pmatrixci_j, 
				jdoubleArray rbounds_j, jintArray rboundtypes_j,
				jintArray columntypes_j,jobjectArray rownames_j,jobjectArray colnames_j) {	
	GLPK* ptr;
	ptr = new GLPK(env,soln_j,pvector_j,cbounds_j,cboundtypes_j,pmatrixv_j,pmatrixri_j,pmatrixci_j, rbounds_j, rboundtypes_j,columntypes_j,rownames_j,colnames_j);
	return (jlong)ptr;
}

JNIEXPORT jint JNICALL
Java_jfm_lp_GLPKPeer_solveWithNewProblem(JNIEnv *env, jobject,jdoubleArray soln_j,
		jdoubleArray pvector_j,jdoubleArray cbounds_j,
				jintArray cboundtypes_j, jdoubleArray pmatrixv_j,jintArray pmatrixri_j,jintArray pmatrixci_j, 
				jdoubleArray rbounds_j, jintArray rboundtypes_j,
				jintArray columntypes_j,jobjectArray rownames_j,jobjectArray colnames_j,jlong objptr) {	
	GLPK* ptr = (GLPK*)objptr;
	ptr->solveWithNewProblem(env,soln_j,pvector_j,cbounds_j,
			cboundtypes_j, pmatrixv_j, pmatrixri_j,pmatrixci_j,
			rbounds_j, rboundtypes_j,
			columntypes_j,rownames_j,colnames_j);
}

JNIEXPORT jint JNICALL
Java_jfm_lp_GLPKPeer_solveWithNewCoefficients(JNIEnv *env, jobject,jdoubleArray soln_j,
		jdoubleArray pvector_j,jdoubleArray cbounds_j,
		jintArray cboundtypes_j,jintArray columntypes_j,jlong objptr) {	
	GLPK* ptr = (GLPK*)objptr;
	ptr->solveWithNewCoefficients(env,soln_j,pvector_j,cbounds_j,cboundtypes_j,columntypes_j);
}


JNIEXPORT void JNICALL
Java_jfm_lp_GLPKPeer_setTermOut(JNIEnv *env, jobject, jlong objptr,jint flag) {
	GLPK* ptr=(GLPK*)objptr;
	ptr->setTermOut(env,flag);
}

JNIEXPORT void JNICALL
Java_jfm_lp_GLPKPeer_destroy(JNIEnv *, jobject, jlong objptr) {
	GLPK* ptr = (GLPK*)objptr;
	delete ptr;
}

    