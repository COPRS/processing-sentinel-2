#!/bin/bash

usage() {
    echo "Usage: $0 [OPTIONS] test_case_id_OLQC"
    echo "--samerepo : use the same repo folderf for each instance (default no)"
    echo "--instances n : launch n instances of OLQC (default 1)"
    echo "Example: $0 --instances 3 --samerepo S2PDGS-TC-IPF-V1-OLQC-C-001"
    echo "Launches test with the specified id's"
}

if [ $# -lt 1 ]; then
    usage
    exit 1
fi


cleanup() {
    echo "Cleanup"
    kill $(jobs -p)
}



run() {
TC_ID=$(basename ${TC_ID})
echo "TC_ID: $TC_ID"

#check existence of test id
TC_FOLDER="$S2IPF_TEST_CASES_ROOT_FOLDER/$TC_ID"
if [ ! -d $TC_FOLDER ]; then
    echo "Could not find test case folder $TC_FOLDER"
    exit 1
fi

#source setenv
cd $TC_FOLDER
source setenv.sh 

if [ ! $OLQC_EXE_DIR ];then
    echo "OLQC_EXE_DIR should be defined"
    exit 1
fi

if [ ! $OLQC_PRODUCT ];then
    echo "OLQC_PRODUCTS should be defined"
    exit 1
fi


touch $IDPORCH_LOG_DIR/olqclaunch.log
logfile=$IDPORCH_LOG_DIR/olqclaunch.log

echo "######### Starting OLQC test: "$TC_ID "#########">> $logfile
echo "######### Starting OLQC test: "$TC_ID "#########"

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

echo "######### test $TC_ID finished ##########"
}

trap cleanup INT TERM HUP



#find test cases root folder
S2IPF_TEST_CASES_ROOT_FOLDER=${PWD}
SHELL_BASE_DIR=$(dirname $(readlink -f ${BASH_SOURCE}))
INSTANCES=1
while [ $# -ne 0 ]
do
  case $1 in
    "--samerepo")
	REPO=True
	shift 1
	;;
    "--instances")
	INSTANCES=$2
	shift 2
	;;
    *)
	TC_ID=$1 
	testlist=(${testlist[@]} $TC_ID)
	shift 1
        ;;
   esac
done


if [ $INSTANCES -lt 1 ];then
    echo "Can not run less than 1 instance"
    exit 1
fi

for id in ${testlist[@]}
do
    TC_ID=$id
    run
done

