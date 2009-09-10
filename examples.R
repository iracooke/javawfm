library(farmR)

fp=FarmParameters()
risks=1:50

objectiveParams=lapply(risks,function(x) { op=ObjectiveParameters(); setWeightForObjective(op,"motadrisk",x); op })

farms=lapply(objectiveParams,function(x) { fm=Farm(fp,x); fm })
constrainArea(riskFarms[[1]],"setaside",20)
lapply(riskFarms,solvelp)

cropnames=cropNames(riskFarms[[1]])

# Get areas of all crops as a function of risk
cropareas=lapply(cropnames,function(x) sapply(riskFarms,function(y) cropArea(y,x)))
names(cropareas)<-cropnames



plot(risks,sapply(riskFarms,profit),xlab="Risk avoidance preference","Profit");

plot(risks,sapply(riskFarms,function(x) cropArea(x,"setaside")))

