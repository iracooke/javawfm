pkg=farmR_1.0
rm -rf /tmp/pkg
mkdir /tmp/pkg
R CMD BUILD farmR
R_ARCH=/i386 R CMD INSTALL -l /tmp/pkg $pkg.tar.gz
R_ARCH=/ppc R CMD INSTALL -l /tmp/pkg --libs-only $pkg.tar.gz
tar fvcz $pkg.tgz -C /tmp/pkg `echo $pkg|awk -F_ '{print $1}'`
rm -rf /tmp/pkg
