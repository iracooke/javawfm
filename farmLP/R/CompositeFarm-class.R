# Implementation of the Composite Farm class #
#
# Usually used to represent a farming enterprise using a weighted sum of solutions from 
# farms with different soiltypes, objective weights and/or distances to sugarbeet factories
#
####################################


################### Constructor #######################
# Create a new Farm object from its economic parameters
#######################################################
CompositeFarm <- function(farmParams,mou=NULL,mouweights=c(1.0),soildata,SBFactories=NULL,haulagePerTonnePerKm=0.12,maxSBHaulageDistance=200) { 
	farm=new("CompositeFarm")
	farm@model=.jnew("jfm/r/CompositeFarmRepresentation")
	rf=0
	wt=c()
	if ( length(soildata$X0.5) > 1 ){
		rf=mean(soildata$RF)
		wt=c(mean(soildata$X0.5),mean(soildata$X0.75),mean(soildata$X1.0),mean(soildata$X1.25),mean(soildata$X1.5),mean(soildata$X1.75),mean(soildata$X2.0),mean(soildata$X2.5))
	} else {
		rf=soildata$RF
		wt=c(soildata$X0.5,soildata$X0.75,soildata$X1.0,soildata$X1.25,soildata$X1.5,soildata$X1.75,soildata$X2.0,soildata$X2.5)
	}
	sl=c(0.5,0.75,1.0,1.25,1.5,1.75,2.0,2.5)
	for( i in 1:length(sl)){
		if ( wt[i] > 0){
			if ( !is.list(mou)){
				mou=list(mou)
			}
			for( m in 1:length(mou)){
				sfarm=Farm(farmParams)
				if ( !is.null(mou[[m]])){
					set(sfarm,mou[[m]])
				}
				if ( !is.null(SBFactories) ){
					set.sugarbeet(sfarm,coordinates(soildata),SBFactories,haulagePerTonnePerKm,maxSBHaulageDistance)
				}
				.jcall(model(farm),"V","addFarm",model(sfarm),sl[i],rf,wt[i]*mouweights[m])
				rm(sfarm) # probably doesn't do much
			}
		}
	}
	farm@cropNames=.jcall(farm@model,"[Ljava/lang/String;","cropNames")
	farm
}









