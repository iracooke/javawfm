#include "CBC.h"

//! Constructor for the CBC solver 
CBC::CBC(JNIEnv *env,jdoubleArray soln_j,jdoubleArray pvector_j,jdoubleArray cbounds_j,
		jintArray cboundtypes_j, jdoubleArray pmatrixv_j,jintArray pmatrixri_j,jintArray pmatrixci_j, 
		jdoubleArray rbounds_j, jintArray rboundtypes_j,
		jintArray columntypes_j,jobjectArray rownames_j,jobjectArray colnames_j)
{
	arraysLoaded=FALSE;
	matrixLoaded=FALSE;
	needsInitialSolve=TRUE;
	solver=0;
	
	// Translate java arguments into C and store their values as instance variables
	getArrayElements(env,soln_j,pvector_j,cbounds_j,cboundtypes_j,
			pmatrixv_j,pmatrixri_j,pmatrixci_j,rbounds_j,rboundtypes_j,columntypes_j,rownames_j,colnames_j);

	// Clean up array storage without copying results .. this allows java to GC the objects
	releaseArrayElements(env,soln_j,pvector_j,cbounds_j,cboundtypes_j,
			pmatrixv_j,pmatrixri_j,pmatrixci_j,rbounds_j,rboundtypes_j,columntypes_j,JNI_ABORT);
	
}


//! Contruct the constraint matrix 
void CBC::loadMatrix(){
	nrows=rboundtypessize;
	ncols=cboundtypessize;
	double *col_lb       = new double[ncols];//the column lower bounds
	double *col_ub       = new double[ncols];//the column upper bounds
	for(int c = 0; c < ncols;c++){
		switch(cboundtypes[c]){
		case LPX_LO:	
			col_lb[c]=cbounds[2*c];
			col_ub[c]=solver->getInfinity();
			break;
		case LPX_UP:
			col_lb[c]=-1.0*solver->getInfinity();
			col_ub[c]=cbounds[2*c+1];
			break;
		case LPX_DB:
			col_lb[c]=cbounds[2*c];
			col_ub[c]=cbounds[2*c+1];
			break;
		case LPX_FX:
			if ( cbounds[2*c] != cbounds[2*c+1]){
				printf("Fixed variable has incompatible bounds");
				exit(1);
			}
			col_lb[c]=cbounds[2*c];
			col_ub[c]=cbounds[2*c];// Is this a source of error?
			break;
		case LPX_FR:
			col_lb[c]=-1.0*solver->getInfinity();
			col_ub[c]=solver->getInfinity();
			break;
		default:
			std::cout << "Unrecognised column bound type " << cboundtypes[c] << std::endl;
			exit(1);
		}
	}
//	printf("Done columns \n");
	double *row_lb = new double[nrows]; //the row lower bounds
	double *row_ub = new double[nrows]; //the row upper bounds

	
	for( int r=0;r<nrows;r++){
		switch(rboundtypes[r]){
		case LPX_LO:
			row_lb[r]=rbounds[2*r];
			row_ub[r]=solver->getInfinity();
			break;
		case LPX_UP:
			row_lb[r]=-1.0*solver->getInfinity();
			row_ub[r]=rbounds[2*r+1];
			break;
		case LPX_DB:
			row_lb[r]=rbounds[2*r];
			row_ub[r]=rbounds[2*r+1];
			break;
		case LPX_FX:
			if ( rbounds[2*r] != rbounds[2*r+1]){
				printf("Fixed variable has incompatible bounds");
				exit(1);
			}
			row_lb[r]=rbounds[2*r];
			row_ub[r]=rbounds[2*r]; //Lower bound only used
			break;
		default:
			printf("Unrecognised row bound type \n");
			exit(1);			           
		}

	}
//	printf("done rows \n");
	int* ri = new int[matrixsize];
	int* ci = new int[matrixsize];	
	double* vrc=new double[matrixsize];
	int maxri=0;
	int maxci=0;
	for ( int i = 0 ; i < matrixsize ; i++){
		ri[i]=pmatrixri[i];
		ci[i]=pmatrixci[i];
		vrc[i]=pmatrixv[i];
		if ( ri[i] > maxri ){
			maxri=ri[i];
		}
		if ( ci[i] > maxci ){
			maxci = ci[i];
		}
	}
//	std::cout << "maxr " << maxri << " maxci " << maxci << std::endl;
	CoinPackedMatrix *matrix =  new CoinPackedMatrix(true,ri,ci,vrc,matrixsize);
//	printf("created cpm \n");
//	std::cout << "CPM has " << matrix->getNumCols() << " and " <<
//		matrix->getNumRows() << " row and " << matrix->getNumElements() << " elements " << std::endl;
	solver->loadProblem(*matrix, col_lb, col_ub, pvector, row_lb, row_ub);
//	printf("loaded problem \n");
//	std::cout << "Solver thinks it has " << solver->getNumCols() << " and " <<
//	solver->getNumRows() << " row and " << solver->getNumElements() << " elements " << std::endl;
	
	int realncols=solver->getNumCols(); // Empty columns can mean these are different
//	std::cout<< "There were "<<ncols-realncols<<" columns unused"<<std::endl;
	for ( int c=realncols;c<ncols;c++){
		std::cout<< colnames_c[c]<< std::endl;
	}
	for ( int c=0;c<realncols;c++){
		solver->setColName(c,colnames_c[c]);
		switch(columntypes[c]){
		case LPX_CV:
			solver->setContinuous(c);
			break;
		case LPX_IV:
			solver->setInteger(c);
			problemType=LPX_MIP;
			break;
		default:
			printf("Unrecognised column type \n");
			exit(1);
		}
	}
	for ( int r=0;r<nrows;r++){
		solver->setRowName(r,(std::string)rownames_c[r]);
	//	std::cout<<solver->getRowName(r,100)<<std::endl;
	}
	
	needsInitialSolve=TRUE; // Problem structure could have changed
	matrixLoaded=TRUE;
	delete matrix;
	delete ri;
	delete ci;
	delete vrc;
	delete col_lb;
	delete col_ub;
	delete row_ub;
	delete row_lb;
}

/** This doesn't work*/
jint CBC::solveWithNewCoefficients(JNIEnv *env,jdoubleArray soln_j,jdoubleArray pvector_j,jdoubleArray cbounds_j,
		jintArray cboundtypes_j,jintArray columntypes_j){
	// Get elements of pvector only	
	getStructArrayElements(env,soln_j,pvector_j,cbounds_j,cboundtypes_j,columntypes_j);
//	printf("New coeffs solve \n");
	if ( !matrixLoaded ){
		std::cout<< "Matrix not loaded. Try solveWithNewProblem \n" << std::endl;
		exit(1);
	}
//	int numcols=solver->getNumCols();
//	const double* oldcoeff=solver->getObjCoefficients();

//	for ( int c=0;c<numcols ;c++){
//		if ( oldcoeff[c] != pvector[c]){
//			std::cout << " old " << oldcoeff[c] << " new " << pvector[c] << " " << c << std::endl;
//		}
//		solver->setObjCoeff(c,pvector[c]);
		solver->setObjective(pvector);
//	}
//	delete oldcoeff;
	// And what about bounds? // for now assume column bounds stay the same.
//	printf("Solving with new coeffs \n");
//	solver->setupForRepeatedUse(0,0);
	solve();
	releaseStructArrayElements(env,soln_j,pvector_j,cbounds_j,cboundtypes_j,columntypes_j);
	return solutionStatus;
}

void CBC::getStructArrayElements(JNIEnv *env,jdoubleArray soln_j,jdoubleArray pvector_j,jdoubleArray cbounds_j,
		jintArray cboundtypes_j,jintArray columntypes_j){
	soln=env->GetDoubleArrayElements(soln_j,0);
	pvector=env->GetDoubleArrayElements(pvector_j,0);
	cbounds=env->GetDoubleArrayElements(cbounds_j,0);
	cboundtypes=env->GetIntArrayElements(cboundtypes_j,0);
	columntypes=env->GetIntArrayElements(columntypes_j,0);
	pvectorLoaded=TRUE;
}
void CBC::releaseStructArrayElements(JNIEnv *env,jdoubleArray soln_j,jdoubleArray pvector_j,jdoubleArray cbounds_j,
		jintArray cboundtypes_j,jintArray columntypes_j){
	env->ReleaseDoubleArrayElements(soln_j,soln,0);
	env->ReleaseDoubleArrayElements(pvector_j,pvector,0);
	env->ReleaseDoubleArrayElements(cbounds_j,cbounds,0);
	env->ReleaseIntArrayElements(cboundtypes_j,cboundtypes,0);
	env->ReleaseIntArrayElements(columntypes_j,columntypes,0);
	pvectorLoaded=FALSE;
}


/** Releases the old solver object and constructs and entirely new one from scratch */
jint CBC::solveWithNewProblem(JNIEnv *env,jdoubleArray soln_j,jdoubleArray pvector_j,jdoubleArray cbounds_j,
		jintArray cboundtypes_j, jdoubleArray pmatrixv_j,jintArray pmatrixri_j,jintArray pmatrixci_j, 
		jdoubleArray rbounds_j, jintArray rboundtypes_j,
		jintArray columntypes_j,jobjectArray rownames_j,jobjectArray colnames_j){
	getArrayElements(env,soln_j,pvector_j,cbounds_j,cboundtypes_j,
			pmatrixv_j,pmatrixri_j,pmatrixci_j,rbounds_j,rboundtypes_j,columntypes_j,rownames_j,colnames_j);
//	printf("Solving with new prob \n");
	if ( solver ){
		solver->releaseClp();
	} 
	solver=new OsiClpSolverInterface();
	solver->setIntParam(OsiNameDiscipline,0);//0 to have machine names 1 to use human names
	solver->setHintParam(OsiDoReducePrint);
	solver->setObjSense(-1); // Setup the solver to maximize
	solver->messageHandler()->setLogLevel(0);
	
	loadMatrix();
	solve();
	// Clean up array storage and copy results back to input
	releaseArrayElements(env,soln_j,pvector_j,cbounds_j,cboundtypes_j,
			pmatrixv_j,pmatrixri_j,pmatrixci_j,rbounds_j,rboundtypes_j,columntypes_j,0);
	return solutionStatus;
}

void CBC::solve(){
//	loadMatrix();
	// Modify the problem by reloading the matrix
	if (problemType==LPX_MIP){
//		std::cout << "Branch and bound " << std::endl;
		if ( !needsInitialSolve){
			solver->setupForRepeatedUse(0,0);
		}
		solver->branchAndBound();
//		std::cout << "Again "<< std::endl;
//		solver->branchAndBound();
//		solver->setupForRepeatedUse();
	} else {
		if ( needsInitialSolve){
			std::cout << "Initial solving " << std::endl;
			solver->initialSolve();
			needsInitialSolve=FALSE;
		} else {
			std::cout << "Resolving " << std::endl;
			solver->resolve();
		}
	}
	
	 if ( solver->isProvenOptimal() ) { 
		 const double *solution;
		 solution = solver->getColSolution();
		 for(int i = 0;i < ncols;i++){		
			 soln[i]=solution[i];		
		 }
//		 delete solution;
		 solutionStatus=LPX_OPT;
//		 std::cout<<"again" << std::endl;
//		 solver->branchAndBound();
//		 solver->writeLp("feaslp");	
	   } else {			   
		   // Check other status functions.  What happened?
		   if (solver->isProvenPrimalInfeasible()){
			   std::cout << "Problem is proven to be infeasible." << std::endl;
		   }
		   if (solver->isProvenDualInfeasible()){
			   std::cout << "Problem is proven dual infeasible." << std::endl;
		   }
		   if (solver->isIterationLimitReached()){
			   std::cout << "Reached iteration limit." << std::endl;
		   }
		   solutionStatus=LPX_NOTOPT;
//		   std::cout << "Didn't find optimal solution." << std::endl;
		   solver->writeLp("fail_problem.lp");
	   }

}


/*! Translates all the java data into C data and sets instance variables in the CBC object with their values */
void CBC::getArrayElements(JNIEnv *env,jdoubleArray soln_j,jdoubleArray pvector_j,jdoubleArray cbounds_j,
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

void CBC::releaseArrayElements(JNIEnv *env,jdoubleArray soln_j,jdoubleArray pvector_j,jdoubleArray cbounds_j,
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

CBC::~CBC()
{
//	printf("Deleting the CBC peer object \n\n\n");
	if ( arraysLoaded ){
		printf("Error: attempt to delete LP object with JNI arrays Loaded \n");
		exit(1);
	}
//	solver->releaseClp();
	delete solver;
}
