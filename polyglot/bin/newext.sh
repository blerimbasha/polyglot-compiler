#!/bin/sh
#
# newext.sh - create a new Polyglot extension from the skeleton
#
# This script creates a copy of polyglot/src/skel, instantiating
# it with the name of the extension.
#
########################################################################

usage_error() {
    echo "$0: too few arguments"
    echo "Usage: $0 small large ext"
    echo "  where small = small name of language"
    echo "        large = large name of language"
    echo "        ext   = file extension for language"
    echo "  e.g.:"
    echo "        $0 op OP op"
    echo "        $0 polyj PolyJ pj"
    exit 1
}

check() {
    if [ -z $1 ]; then
        usage_error
    fi
}

skel_small="skel"
skel_large="Skel"
skel_ext="sx"

ext_small=$1; check $ext_small; shift
ext_large=$1; check $ext_large; shift
ext_ext=$1; check $ext_ext; shift

subst_small="s%$skel_small%$ext_small%g"
subst_large="s%$skel_large%$ext_large%g"
subst_ext="s%$skel_ext%$ext_ext%g"

sed_opt="-e $subst_small -e $subst_large -e $subst_ext"

(
cd `dirname $0`
for i in `find skel -path '*CVS*' -prune -o -print`; do
    if [ -d $i ]; then
        j=`echo $i | sed $sed_opt`
        mkdir $j
    elif [ -f $i ]; then
        j=`echo $i | sed $sed_opt`
        sed $sed_opt $i >$j
    fi
done
)
