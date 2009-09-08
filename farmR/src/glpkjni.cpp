#include "glpkjni.h"

// ---- Helper functions ---- //

/** Search for Integer variables. If none found then the problem is a standard continuous (LPX_LP). 
 * Otherwise it becomes a Mixed Integer Problem (MIP)*/
int GLPK::getProblemType(){
	int type = LPX_LP; // Default value
	int i;
	for ( i = 0 ; i < ncols;i++){
		if ( columntypes[i]== LPX_IV ){
			type=LPX_MIP;			
		}
	}
	return type;
}


/** Make some basic checks on the sizes of arrays used to construct the problem.*/
void GLPK::setAndCheckMatrixSizes(){
	nrows=rboundtypessize;
	ncols=vectorsize;
}


void GLPK::initProblem(){
	if ( arraysLoaded ){
		initSettings();
		initStructure();
		initConstraints();	
	} else {
		printf("Error: can't initialise problem. Arraysl not loaded \n");
		exit(1);
	}
}

void GLPK::initSettings(){
	setAndCheckMatrixSizes();

  	// Update the problem size 
  	int oldnrows = lpx_get_num_rows(lp);
  	int oldncols = lpx_get_num_cols(lp);
  	int rowdiff = nrows-oldnrows;
  	int coldiff = ncols-oldncols;
  	if ( rowdiff < 0 || coldiff < 0 ){
//  		printf("Problem got smaller: this operation is not supported \n");
//  		  		  		exit(1);
 // 		printf("Problem got smaller: oldr %d oldc %d , newr %d newc %d",oldnrows,oldncols,nrows,ncols);
  		lpx_delete_prob(lp);// If the problem gets smaller we just start from scratch
  		lp=lpx_create_prob();
  		rowdiff=nrows;
  		coldiff=ncols;
  	}
  	
  	problemType = getProblemType();
	lpx_set_class(lp,problemType);	
	// --- Iteration Limits if desired ---- //
//	lpx_set_int_parm(lp,LPX_K_ITLIM,100);	
//	lpx_set_real_parm(lp,LPX_K_TMLIM,60);

	// Set the lp solver to maximise the objective 
  	lpx_set_obj_dir((LPX*)lp,LPX_MAX);
  	
  	if ( rowdiff > 0 ){
  		lpx_add_rows(lp,rowdiff);
  	} 
  	if ( coldiff > 0 ){
  		lpx_add_cols(lp,coldiff);
  	} 
}



void GLPK::initStructure(){
	int i,j;
	for( i = 0; i < ncols;i++){
		if ( problemType == LPX_MIP){
			lpx_set_col_kind(lp,i+1,columntypes[i]);
		}
//		printf("setting bounds for col %d to %d,%f,%f \n",i+1,cboundtypes[i],cbounds[2*i],cbounds[2*i+1]);
		lpx_set_col_bnds(lp,i+1,cboundtypes[i],cbounds[2*i],cbounds[2*i+1]);
		lpx_set_obj_coef(lp,i+1,pvector[i]);	
		lpx_set_col_name(lp,i+1,colnames_c[i]);
//		printf("set obj coeff for col %d to %f \n",i+1,pvector[i]);
	}
}

void GLPK::initConstraints(){
	int i,j;
	// Go through and set bounds on all rows. Remember array indexing is from 1 to n 
	for( i = 0;i < nrows;i++){
//		printf("setting bounds for row %d to %d,%f,%f \n",i+1,rboundtypes[i],rbounds[2*i],rbounds[2*i+1]);
		lpx_set_row_bnds(lp,i+1,rboundtypes[i],rbounds[2*i],rbounds[2*i+1]);
//		printf("Setting row name with length %d ",strlen(rownames_c[i]));
//		for ( int j=0;j<strlen(rownames_c[i]);j++){
//			printf("%c",rownames_c[i][j]);
//		}
//		printf("\n");
		lpx_set_row_name(lp,i+1,rownames_c[i]);
	}  	
	int ia[matrixsize+1],ja[1+matrixsize];
	double ar[1+matrixsize];
	for ( i = 0 ; i < matrixsize ; i++){
		ia[i+1]=pmatrixri[i]+1;
		ja[i+1]=pmatrixci[i]+1;
		ar[i+1]=pmatrixv[i];
	}
	// Load matrix into the lp object
	lpx_load_matrix(lp,matrixsize,ia,ja,ar); 
}

void GLPK::getArrayElements(JNIEnv *env,jdoubleArray soln_j,jdoubleArray pvector_j,jdoubleArray cbounds_j,
		jintArray cboundtypes_j, jdoubleArray pmatrixv_j, jintArray pmatrixri_j,jintArray pmatrixci_j,
		jdoubleArray rbounds_j, jintArray rboundtypes_j,
		jintArray columntypes_j,jobjectArray rownames_j,jobjectArray colnames_j){
	matrixsize=env->GetArrayLength(pmatrixv_j);
	vectorsize=env->GetArrayLength(pvector_j);	
	rboundssize=env->GetArrayLength(rbounds_j);
	cboundssize=env->GetArrayLength(cbounds_j);
	rboundtypessize=env->GetArrayLength(rboundtypes_j);
	cboundtypessize=env->GetArrayLength(cboundtypes_j);
	columntypessize=env->GetArrayLength(columntypes_j);
	pmatrixv=env->GetDoubleArrayElements(pmatrixv_j,0);
	pmatrixri=env->GetIntArrayElements(pmatrixri_j,0);
	pmatrixci=env->GetIntArrayElements(pmatrixci_j,0);
	soln=env->GetDoubleArrayElements(soln_j,0);
	pvector=env->GetDoubleArrayElements(pvector_j,0);
	cbounds=env->GetDoubleArrayElements(cbounds_j,0);
	rbounds=env->GetDoubleArrayElements(rbounds_j,0);
	cboundtypes=env->GetIntArrayElements(cboundtypes_j,0);
	rboundtypes=env->GetIntArrayElements(rboundtypes_j,0);
	columntypes=env->GetIntArrayElements(columntypes_j,0);
	rownames_c=new char*[rboundtypessize];	
	for ( int i=0;i<rboundtypessize;i++){
		jcharArray rowarray=(jcharArray)env->GetObjectArrayElement(rownames_j,i);
		int rowlen=env->GetArrayLength(rowarray);
		rownames_c[i] = new char[rowlen+1];
		jchar* rowelements =env->GetCharArrayElements(rowarray,0);
		for ( int j=0;j<rowlen;j++){
			rownames_c[i][j]=(char)rowelements[j];
		}
		rownames_c[i][rowlen]='\0'; // This is really important. The char sequences must be null terminated
		env->ReleaseCharArrayElements(rowarray,rowelements,0);
	}
	colnames_c=new char*[vectorsize];
	for ( int i=0;i<vectorsize;i++){
		jcharArray colarray=(jcharArray)env->GetObjectArrayElement(colnames_j,i);
		int len=env->GetArrayLength(colarray);
		colnames_c[i]=new char[len+1];
		jchar* elements=env->GetCharArrayElements(colarray,0);
		for ( int j=0;j<len;j++){
			colnames_c[i][j]=(char)elements[j];
		}
		colnames_c[i][len]='\0';
		env->ReleaseCharArrayElements(colarray,elements,0);
	}
	
	arraysLoaded=TRUE;
	pvectorLoaded=TRUE;
}

void GLPK::releaseArrayElements(JNIEnv *env,jdoubleArray soln_j,jdoubleArray pvector_j,jdoubleArray cbounds_j,
		jintArray cboundtypes_j, jdoubleArray pmatrixv_j,jintArray pmatrixri_j,jintArray pmatrixci_j, 
		jdoubleArray rbounds_j, jintArray rboundtypes_j,
		jintArray columntypes_j,int method){

	env->ReleaseDoubleArrayElements(pmatrixv_j,pmatrixv,method);
	env->ReleaseIntArrayElements(pmatrixci_j,pmatrixci,method);
	env->ReleaseIntArrayElements(pmatrixri_j,pmatrixri,method);
	env->ReleaseDoubleArrayElements(soln_j,soln,method);
	env->ReleaseDoubleArrayElements(pvector_j,pvector,method);
	env->ReleaseDoubleArrayElements(rbounds_j,rbounds,method);
	env->ReleaseDoubleArrayElements(cbounds_j,cbounds,method);
	env->ReleaseIntArrayElements(rboundtypes_j,rboundtypes,method);
	env->ReleaseIntArrayElements(cboundtypes_j,cboundtypes,method);
	env->ReleaseIntArrayElements(columntypes_j,columntypes,method);
	for ( int i=0;i<rboundtypessize;i++){
		delete rownames_c[i];		
	}
	delete rownames_c;
	for ( int i=0;i<vectorsize;i++){
		delete colnames_c[i];
	}
	delete colnames_c;
	arraysLoaded=FALSE;
	pvectorLoaded=FALSE;
}


void GLPK::getStructArrayElements(JNIEnv *env,jdoubleArray soln_j,jdoubleArray pvector_j,jdoubleArray cbounds_j,
		jintArray cboundtypes_j,jintArray columntypes_j){
	soln=env->GetDoubleArrayElements(soln_j,0);
	pvector=env->GetDoubleArrayElements(pvector_j,0);
	cbounds=env->GetDoubleArrayElements(cbounds_j,0);
	cboundtypes=env->GetIntArrayElements(cboundtypes_j,0);
	columntypes=env->GetIntArrayElements(columntypes_j,0);
	pvectorLoaded=TRUE;
}
void GLPK::releaseStructArrayElements(JNIEnv *env,jdoubleArray soln_j,jdoubleArray pvector_j,jdoubleArray cbounds_j,
		jintArray cboundtypes_j,jintArray columntypes_j){
	env->ReleaseDoubleArrayElements(soln_j,soln,0);
	env->ReleaseDoubleArrayElements(pvector_j,pvector,0);
	env->ReleaseDoubleArrayElements(cbounds_j,cbounds,0);
	env->ReleaseIntArrayElements(cboundtypes_j,cboundtypes,0);
	env->ReleaseIntArrayElements(columntypes_j,columntypes,0);
	pvectorLoaded=FALSE;
}


GLPK::GLPK(JNIEnv *env,jdoubleArray soln_j,jdoubleArray pvector_j,jdoubleArray cbounds_j,
		jintArray cboundtypes_j, jdoubleArray pmatrixv_j,jintArray pmatrixri_j,jintArray pmatrixci_j, 
		jdoubleArray rbounds_j, jintArray rboundtypes_j,
		jintArray columntypes_j,jobjectArray rownames_j,jobjectArray colnames_j)
{
	arraysLoaded=FALSE;
	int i,j;
//	std::cout<<"about to get array Elements"<<std::endl;
	getArrayElements(env,soln_j,pvector_j,cbounds_j,cboundtypes_j,
			pmatrixv_j,pmatrixri_j,pmatrixci_j,rbounds_j,rboundtypes_j,columntypes_j,rownames_j,colnames_j);
//	std::cout<<"creating problem"<<std::endl;
	lp=lpx_create_prob();
//	std::cout<<"initing problem"<<std::endl;
	initProblem();
//	std::cout<< "about to release array Elements"<<std::endl;
	// Clean up array storage without copying results
	releaseArrayElements(env,soln_j,pvector_j,cbounds_j,cboundtypes_j,
			pmatrixv_j,pmatrixri_j,pmatrixci_j,rbounds_j,rboundtypes_j,columntypes_j,JNI_ABORT);
	
}


// --- Private solve function --- //
void GLPK::Solve(){
	int i,j;
	if ( pvector == NULL ){
		printf("Trying to solve but pvector is unitialized \n");
		exit(1);
	}
	// Call simplex solver.
	if ( problemType == LPX_MIP ){
	//	printf("Solving a mixed integer problem with %d integer variables \n",lpx_get_num_int(lp));	
		lpx_simplex(lp);
		lpx_integer(lp);
		int mip_status = lpx_mip_status(lp);
//		lpx_print_prob(lp,"./problem.txt");
//		lpx_print_mip(lp,"./mipsol.txt");
	} else {
		//  		printf("Solving simplex problem");
		lpx_simplex(lp);
//		lpx_print_prob(lp,"./problem.txt");
//		lpx_print_sol(lp,"./simplexsol.txt");
	}
	// Collate output back into pvector, to be returned to user.
	for( i = 0;i < ncols;i++){		
		if ( problemType == LPX_MIP ){
			soln[i]=lpx_mip_col_val(lp,i+1);	
		} else {
			soln[i]=lpx_get_col_prim(lp,i+1);		
		}
//		printf("Status of column %d is %d with value %f \n",i,lpx_get_col_stat(lp,i+1),lpx_get_col_prim(lp,i+1));
	//		printf("solution val %f \n",pvector[i]);
	}  	
}

// ---- Public solving functions to interface with JNI --- // 

jint GLPK::solveWithNewCoefficients(JNIEnv *env,jdoubleArray soln_j,jdoubleArray pvector_j,jdoubleArray cbounds_j,
		jintArray cboundtypes_j,jintArray columntypes_j){
	// Get elements of pvector only
	getStructArrayElements(env,soln_j,pvector_j,cbounds_j,cboundtypes_j,columntypes_j);
	initStructure();
	Solve();
	releaseStructArrayElements(env,soln_j,pvector_j,cbounds_j,cboundtypes_j,columntypes_j);
	return lpx_get_status(lp);
}


jint GLPK::solveWithNewProblem(JNIEnv *env,jdoubleArray soln_j,jdoubleArray pvector_j,jdoubleArray cbounds_j,
		jintArray cboundtypes_j, jdoubleArray pmatrixv_j,jintArray pmatrixri_j,jintArray pmatrixci_j, 
		jdoubleArray rbounds_j, jintArray rboundtypes_j,
		jintArray columntypes_j,jobjectArray rownames_j,jobjectArray colnames_j){
	getArrayElements(env,soln_j,pvector_j,cbounds_j,cboundtypes_j,
			pmatrixv_j,pmatrixri_j,pmatrixci_j,rbounds_j,rboundtypes_j,columntypes_j,rownames_j,colnames_j);
	initProblem();
	Solve();
	// Clean up array storage and copy results back to input
	releaseArrayElements(env,soln_j,pvector_j,cbounds_j,cboundtypes_j,
			pmatrixv_j,pmatrixri_j,pmatrixci_j,rbounds_j,rboundtypes_j,columntypes_j,0);
	return lpx_get_status(lp);
}

void GLPK::setTermOut(JNIEnv *env,jint flag){
	glp_term_out(flag);
}

GLPK::~GLPK()
{
//	std::cout<<"Deleting LP "<<std::endl;
	if ( arraysLoaded ){
		printf("Error: attempt to delete LP object with JNI arrays Loaded \n");
		exit(1);
	}
	lpx_delete_prob(lp);
}
