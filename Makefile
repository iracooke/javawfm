
default	:
	cp /Users/iracooke/Sources/EclipseWS/javawfm/jfm.jar ./farmland/java/
	cp /Users/iracooke/Library/Java/Extensions/libjfmnativecbc.jnilib ./farmland/java/
	cp /Users/iracooke/Library/Java/Extensions/libjfmnativeglpk.jnilib ./farmland/java/
	R CMD build farmland
	R CMD INSTALL farmland_1.0.tar.gz

reludatafiles = ./relu/data/*.RData
relumanfiles = ./relu/man/*.Rd
reluotherfiles = ./relu/*

relu	:	$(reludatafiles) $(relumanfiles) $(reluotherfiles)
	R CMD build relu
	R CMD INSTALL relu_1.0.tar.gz



check	:
	R CMD check farmland
	R CMD check relu
