#!/bin/bash

usage() {
    echo "Usage: $0 --product (path_to_products) --out (path_to_write_results) 
                     --gipp (path to OLQC gipps) --OLQC (path to OLQC script) --instances n --samerepo"
    echo "Example: $0 "
    echo "Launches test with the specified context"
}

if [ $# -lt 8 ]; then
    usage
    exit 1
fi

cleanup() {
    echo "Cleanup"
    kill $(jobs -p)
}

#find test cases root folder
S2IPF_TEST_CASES_ROOT_FOLDER=${PWD}
SHELL_BASE_DIR=$(dirname $(readlink -f ${BASH_SOURCE}))
INSTANCES=1

trap cleanup INT TERM HUP

#parse args
while [ $# -ne 0 ]
do
    case $1 in
	"--product")
            PRODUCT=$(readlink -f $2)
            shift 2
            ;;
	"--out")
            OUT=$(readlink -f $2)
            shift 2
            ;;
	"--gipp")
	    GIPP=$(readlink -f $2)
	    shift 2
	    ;;
	"--OLQC")
	    OLQC=$(readlink -f $2)
	    shift 2
	    ;;
	"--samerepo")
	    REPO=True
	    shift 1
	    ;;
	"--instances")
	    INSTANCES=$2
	    shift 2
	    ;;
	*)
	    echo "Unrecognized parameter: "$1
	    usage
	    exit 1
    esac
done

if [ ! -d $PRODUCT ];then
    echo "PRODUCT folder doesn't exists"
    usage
    exit 1
fi

if [ ! -d $OUT ];then
    echo "OUT folder doesn't exists"
    usage
    exit 1
fi

if [ ! -d $GIPP ];then
    echo "GIPP folder doesn't exists"
    usage
    exit 1
fi

if [ ! -x $OLQC ];then
    echo "OLQC script is not valid"
    usage
    exit 1
fi

if [ $INSTANCES -lt 1 ];then
    echo "Can not run less than 1 instance"
    exit 1
fi

OLQC_EXE_DIR=$OLQC
OLQC_PRODUCT=$PRODUCT
IDPORCH_GIPP_DIR=$GIPP
IDPORCH_PROCESSING_DIR=$OUT
S2IPF_VAL_PROCESSING_DIR=$OUT
IDPORCH_LOG_DIR=$OUT/log
mkdir -p $IDPORCH_LOG_DIR


touch $IDPORCH_LOG_DIR/olqclaunch.log
logfile=$IDPORCH_LOG_DIR/olqclaunch.log

echo "######### Starting OLQC test  #########">> $logfile
echo "######### Starting OLQC test  #########"

#Initilializing OLQC execution environment
dateof=$(date +%Y%m%d%H%M%S)
tasktable=$IDPORCH_PROCESSING_DIR/Tasktable_${dateof}/OLQC
persistent=$S2IPF_VAL_PROCESSING_DIR/OLQC_PERSISTENT


echo "OLQC persistent directory set to: "$persistent
echo "OLQC persistent directory set to: "$persistent >> $logfile

echo "Processing in folder : "$tasktable  >> $logfile
echo "Processing in folder : "$tasktable
inputdir=$tasktable/input
outputdir=$tasktable/output
tmpdir=$tasktable/tmp
mkdir -p $inputdir/GIPP
mkdir -p $outputdir/REPORT
mkdir -p $tmpdir
mkdir $tasktable/log

echo "Copying GIPPs from "$IDPORCH_GIPP_DIR  >> $logfile
echo "Copying GIPPs from "$IDPORCH_GIPP_DIR
for f in $IDPORCH_GIPP_DIR/*GIP_OLQCPA*;do
    cp -L $f $inputdir/GIPP
    GIPP_OLQCPA_NAME=`basename $f`
done
for f in $IDPORCH_GIPP_DIR/*GIP_PROBAS*;do
    cp -L $f $inputdir/GIPP
    GIPP_PROBAS_NAME=`basename $f`
done

#cp $IDPORCH_GIPP_DIR/*GIP_PROBAS* $inputdir/GIPP
#GIPP_PROBAS_NAME=`basename $IDPORCH_GIPP_DIR/*GIP_PROBAS*`
#echo $GIPP_PROBAS_NAME

for i in $(seq 0 $((--INSTANCES)) ); do
    sed -e '/\(\[OLQC_PROCESSING_DIR\]\)/s++'${tasktable}'+' $SHELL_BASE_DIR/Joborder_OLQC.xml  > ${tasktable}/Joborder_OLQC_$i.xml
    if [ ! $REPO ];then
	mkdir -p ${persistent}_$i
	sed -i -e '/\(\[OLQC_REPOSITORY\]\)/s++'${persistent}_$i'+' ${tasktable}/Joborder_OLQC_$i.xml
    else
	mkdir -p ${persistent}
	sed -i -e '/\(\[OLQC_REPOSITORY\]\)/s++'${persistent}'+' ${tasktable}/Joborder_OLQC_$i.xml
    fi
    sed -i -e '/\(\[OLQC_GIP_OLQCPA\]\)/s++'${inputdir}'/GIPP/'$GIPP_OLQCPA_NAME'+' ${tasktable}/Joborder_OLQC_$i.xml
    sed -i -e '/\(\[OLQC_GIP_PROBAS\]\)/s++'${inputdir}'/GIPP/'$GIPP_PROBAS_NAME'+' ${tasktable}/Joborder_OLQC_$i.xml
done

#generating inputs
echo "Copying input product from "$OLQC_PRODUCT  >> $logfile
echo "Copying input product from "$OLQC_PRODUCT
if [ -d $OLQC_PRODUCT ]; then
    cp -rL $OLQC_PRODUCT/* $inputdir/
fi
# filling JobOrder with the product to tests
echo "Generating JobOrder"  >> $logfile
echo "Generating JobOrder"
datastrip=$(ls $inputdir | grep _DS)
filenames=""
COUNT=0
for d in $datastrip;do
    filename="<File_Name>"$inputdir/$d"</File_Name>\n";
    sed -i -e '/\(\[OLQC_PRODUCTS\]\)/s++'${filename}'\[OLQC_PRODUCTS\]+' ${tasktable}/Joborder_OLQC_$COUNT.xml
    let "++COUNT"
    let "COUNT=COUNT%INSTANCES"
done

for f in $inputdir/*_GR_*;do 
    if [ -e $f ]; then 
	filename="<File_Name>$f</File_Name>\n"
	sed -i -e '/\(\[OLQC_PRODUCTS\]\)/s++'${filename}'\[OLQC_PRODUCTS\]+' ${tasktable}/Joborder_OLQC_$COUNT.xml
	let "++COUNT"
	let "COUNT=COUNT%INSTANCES"
    fi
done

for f in $inputdir/DB*/*;do 
    if [ -e $f ]; then 
	filename="<File_Name>$f</File_Name>\n"
	sed -i -e '/\(\[OLQC_PRODUCTS\]\)/s++'${filename}'\[OLQC_PRODUCTS\]+' ${tasktable}/Joborder_OLQC_$COUNT.xml
	let "++COUNT"
	let "COUNT=COUNT%INSTANCES"
    fi
done

for i in $(seq 0 $((--INSTANCES))); do
    sed -i -e '/\(\[OLQC_PRODUCTS\]\)/s+++' ${tasktable}/Joborder_OLQC_$i.xml
    #launch the OLQC on this
    echo "Launching OLQC" >> $logfile
    echo "Launching OLQC number $i"
    { time taskset -c $i $OLQC_EXE_DIR ${tasktable}/Joborder_OLQC_$i.xml 1> $tasktable/log/OLQC_$i.log 2> $tasktable/log/OLQC_$i.err ; echo "return code for OLQC $i = $?" ; } &
done

wait

echo "######### test finished ##########"

