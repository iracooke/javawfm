rm farmR/inst/java/farmR.dll
mv farmR/src .
mv farmR/config* .
cp farmR.dll farmR/inst/java/
R CMD BUILD farmR
rm farmR/inst/java/farmR.dll
mv src farmR/
mv config* farmR/