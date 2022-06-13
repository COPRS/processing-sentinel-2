#!/bin/bash

#
# bash launcher_L0u_step1_raw2cadu.sh ${FOLDER_PRODUCT} ${work} ${out}
#
# ${FOLDER_PRODUCT} should be "DCS_02_L20191003131732787001008_dat" for example
#
# example of launch:
# sudo bash launcher_L0u_step1_raw2cadu.sh ${ESA_DATAPACK}/S2L0DataPack/Test/S2L0IPF/data/S2-L0u-DATA/raw_data/S2A/DCS_02_L20191003131732787001008_dat ./work_L0u/ ./out_L0
#
#

get_origin_date(){
    local PROD_DSIB=$(find $1/ch_1/ -name "*DSIB*xml")
    DSIB_DATE=$(cat $PROD_DSIB | grep "time_start" | sed 's+.*<time_start>++' | sed 's+Z</time_start>++')
    echo "${DSIB_DATE}"
}


cleanup(){
    echo -e "Cleaning up data in installation folders "
    rm -f /usr/local/components/facilities/DPC-CORE-l0pack-ing-typ/DPC-CORE-l0pack-ing-typ-*/ingestion/conf/Generic_Archive_Request.xml*
    rm -f /usr/local/components/facilities/DPC-CORE-l0pack-ing-typ/DPC-CORE-l0pack-ing-typ-*/ingestion/conf/Production_Request_L0u.xml*
    rm -f /usr/local/components/facilities/DPC-CORE-l0pack-ing-typ/DPC-CORE-l0pack-ing-typ-*/ingestion/conf/EISPProcTime2Orbit.log.*.gz
    rm -rf /usr/local/components/facilities/DPC-CORE-l0pack-ing-typ/DPC-CORE-l0pack-ing-typ-*/ingestion/conf/DATA/archive/* 
    rm -rf /usr/local/components/facilities/DPC-CORE-l0pack-ing-typ/DPC-CORE-l0pack-ing-typ-*/ingestion/conf/DATA/outputFiles/*
    echo -r  "preventively kill all the possibly remaining jobs"
    killall -9 EISPProcessor || true
    killall -9 launch_eisp_ing_typ.bash || true
    killall -9 launch_eisp.bash || true
    killall -9 launch_merge.bash || true
    killall -9 launch_telemetry.bash || true
}

CUR_DIR="$( cd "$(dirname "$0")" ; pwd -P )"

set -o pipefail

if [ $# -lt 10 ]; then
    echo "launcher.sh PRODUCT AUXS REP_WORK REP_OUT REP_HKTM REP_SAD REP_KPI AQUI_STATION PROC_STATION SAT_ID"
    exit 1
fi

cleanup


DATAPACK_HOME=${CUR_DIR}

PRODUCT=$1
AUXS=$2
REP_WORK=$3
REP_OUT=$4
REP_HKTM=$5
REP_SAD=$6
REP_KPI=$7
ACQUI_STATION=$8
PROC_STATION=$9
SAT_ID=${10}
WORKSPACE_HOME=${REP_WORK}

echo "DATAPACK_HOME:    $DATAPACK_HOME"
echo "PRODUCT:          $PRODUCT"
echo "AUXS:             $AUXS"
echo "REP_WORK:         $REP_WORK"
echo "REP_OUT:          $REP_OUT"
echo "REP_KPI:          $REP_KPI"
echo "REP_HKTM:         $REP_HKTM"
echo "REP_SAD:          $REP_SAD"
echo "ACQUI_STATION:    $ACQUI_STATION"
echo "PROC_STATION:     $PROC_STATION"
echo "SAT_ID:           $SAT_ID"
echo "WORKSPACE_HOME:   $WORKSPACE_HOME"

# verify stuffs
if [[ ! -d $PRODUCT ]];then
    echo $PRODUCT" folder doesn't exists"
    exit 1
fi
if [[ ! -d $AUXS ]];then
    echo $AUXS" folder doesn't exists"
    exit 1
fi
if [[ ! -d $REP_OUT ]];then
    mkdir -p $REP_OUT
    if [[ ! -d $REP_OUT ]];then
	echo $REP_OUT" folder can't be created and doesnt exists"
	exit 1
    fi
fi
if [[ ! -d $REP_HTKM ]];then
    mkdir -p $REP_HKTM
    if [[ ! -d $REP_HKTM ]];then
	echo $REP_HKTM" folder can't be created and doesnt exists"
	exit 1
    fi
fi
if [[ ! -d $REP_SAD ]];then
    mkdir -p $REP_SAD
    if [[ ! -d $REP_SAD ]];then
	echo $REP_SAD" folder can't be created and doesnt exists"
	exit 1
    fi
fi
if [[ ! -d $REP_KPI ]];then
    mkdir -p $REP_KPI
    if [[ ! -d $REP_KPI ]];then
	echo $REP_KPI" folder can't be created and doesnt exists"
	exit 1
    fi
fi
if [[ ! -d $REP_WORK ]];then
    mkdir -p $REP_WORK
    if [[ ! -d $REP_WORK ]];then
	echo $REP_WORK" folder can't be created and doesnt exists"
	exit 1
    fi
fi

# copy data
echo
echo -e "Copying data..."
cp -r ${DATAPACK_HOME}/inputs ${WORKSPACE_HOME}
cp -r ${DATAPACK_HOME}/scripts ${WORKSPACE_HOME}
cp -r ${DATAPACK_HOME}/tools ${WORKSPACE_HOME}
cp    ${DATAPACK_HOME}/*.sh ${WORKSPACE_HOME}

#set permissions
for s in $(ls ${WORKSPACE_HOME}/*.sh)
do
	chmod 755 ${s}
done

for s in $(ls ${WORKSPACE_HOME}/scripts/*)
do
	chmod 755 ${s}
done

echo
echo -e "Workspace ${WORKSPACE_HOME} created"
cd ${WORKSPACE_HOME}

export WORKPLANS_HOME=${REP_WORK}/workplans/

echo -e "Patching processing station in DPC installation : /usr/local/components/facilities/DPC-CORE-l0pack-ing-typ/DPC-CORE-l0pack-ing-typ-*/*/conf/standalone/DPC_config_center.xml"
sed -i 's+<center>.*</center>+<center>'$PROC_STATION'</center>+' /usr/local/components/facilities/DPC-CORE-l0pack-ing-typ/DPC-CORE-l0pack-ing-typ-*/*/conf/standalone/DPC_config_center.xml
sed -i 's+<archiving_center>.*</archiving_center>+<archiving_center>'$PROC_STATION'</archiving_center>+' /usr/local/components/facilities/DPC-CORE-l0pack-ing-typ/DPC-CORE-l0pack-ing-typ-*/*/conf/standalone/DPC_config_center.xml

# find needed GIPP to extract informations
# GIP DATATI
GIP_DATATI=$(find $AUXS -name '*GIP_DATATI*xml' | tail -n 1)
if [ -z $GIP_DATATI ]
then
	echo "No GIP_DATATI found in $AUXS"
	exit 1
fi
# AUX UT1UTC
AUX_UT1UTC=$(find $AUXS -name '*AUX_UT1UTC*txt' | tail -n 1)
if [ -z $AUX_UT1UTC ]
then
	echo "No AUX_UT1UTC found in $AUXS"
	exit 1
fi
# GIP ATMSAD
GIP_ATMSAD=$(find $AUXS -name '*GIP_ATMSAD*xml' | tail -n 1)
if [ -z $GIP_ATMSAD ]
then
	echo "No GIP_ATMSAD found in $AUXS"
	exit 1
fi
# GIP ATMIMA
GIP_ATMIMA=$(find $AUXS -name '*GIP_ATMIMA*xml' | tail -n 1)
if [ -z $GIP_ATMIMA ]
then
	echo "No GIP_ATMIMA found in $AUXS"
	exit 1
fi

# Extract informations
# GIP DATATI
THEORETICAL_LINE_PERIOD=$(cat $GIP_DATATI | grep 'THEORETICAL_LINE_PERIOD' | sed 's+.*<THEORETICAL_LINE_PERIOD[^>]*>++' | sed 's+</THEORETICAL_LINE_PERIOD>++')
GPS_TIME_TAI=$(cat $GIP_DATATI | grep 'GPS_TIME_TAI' | sed 's+.*<GPS_TIME_TAI[^>]*>-++' | sed 's+</GPS_TIME_TAI>++')
# AUX UT1UTC
TAI_TO_UTC="-"$(cat $AUX_UT1UTC | grep 'TAI-UTC = ' | sed 's+TAI-UTC =++' | sed 's+\..*++' | sed 's+[ ]*++')
# GIP ATMSAD
ORBIT_OFFSET=$(cat $GIP_ATMSAD | grep 'ORBIT_OFFSET' | sed 's+.*<ORBIT_OFFSET>++' | sed 's+</ORBIT_OFFSET>++')
TOTAL_ORBIT=$(cat $GIP_ATMSAD | grep 'TOTAL_ORBITS' | sed 's+.*<TOTAL_ORBITS>++' | sed 's+</TOTAL_ORBITS>++')
# GIP ATMIMA
SP_MIN_SIZE=$(cat $GIP_ATMIMA | grep 'SP_MIN_SIZE' | sed 's+.*<SP_MIN_SIZE>++' | sed 's+</SP_MIN_SIZE>++')


# Print values
echo "THEORETICAL_LINE_PERIOD :      $THEORETICAL_LINE_PERIOD"
echo "GPS_TIME_TAI :                 $GPS_TIME_TAI"
echo "TAI_TO_UTC :                   $TAI_TO_UTC"
echo "ORBIT_OFFSET :                 $ORBIT_OFFSET"
echo "TOTAL_ORBIT :                  $TOTAL_ORBIT"
echo "SP_MIN_SIZE :                  $SP_MIN_SIZE"

# JobOrder template
JO=${REP_WORK}/inputs/jobOrdersTemplates/job_order_eisp_ing_typ.xml
echo -e "Use the JobOrder template $JO"
cp $JO ${REP_WORK}/job_order_eisp_ing_typ.xml
JO=${REP_WORK}/job_order_eisp_ing_typ.xml
echo -e "Use the JobOrder $JO"

# Configure validation scenario to 'run'
export SCENARIO=run 

# Find the latest installation folder on INV_MTD
INV_INSTALL='/usr/local/components/facilities/DPC-CORE-l0pack-ing-typ/'$(ls '/usr/local/components/facilities/DPC-CORE-l0pack-ing-typ/' | grep 'DPC-CORE-l0pack-ing-typ' | sort | tail -n 1)
if [ ! -d $INV_INSTALL ]
then
	echo "No INV installation folder found in /usr/local/components/facilities/DPC-CORE-l0pack-ing-typ/"
	exit 1
fi

#Create the execution workspace
echo -e "Create the execution workspace"
for d in app_data/wp_data \
    steps_data \
    steps_data/WORKING \
    steps_data/L0U_DUMP \
    steps_data/L0U_DUMP/imgch1 \
    steps_data/L0U_DUMP/imgch2 \
    steps_data/L0U_DUMP/sadch1 \
    steps_data/L0U_DUMP/sadch2 \
    steps_data/L0U_DUMP/MSIMerge \
    steps_data/L0U_DUMP/MSISADMerge \
    steps_data/L0U_DUMP/SADPDI \
    steps_data/L0U_DUMP/PR \
    steps_data/RAW
do
    path=${WORKPLANS_HOME}/${SCENARIO}/${d}
    echo "Create $path ..."
    mkdir -p $path
done

OUTPUT_CADU_DIR=${WORKPLANS_HOME}/${SCENARIO}/steps_data/L0U_DUMP/
OUTPUT_REP_PASS_E_DIR=${WORKPLANS_HOME}/${SCENARIO}/steps_data/L0U_DUMP/scenario/app_data/EISP_DIR

#Find CADU name
CADU=$PRODUCT
echo "CADU found: "$CADU
DSIB=$(find $CADU/ch_1 -name '*DSIB\.xml' | tail -n 1)
if [ -z $DSIB ]
then
	echo "No DSIB found in $CADU/ch_1"
	exit 1
fi
CADU_TIME=$(cat $DSIB | grep 'time_start' | sed 's+.*<time_start>++' | sed 's+</time_start>++' | sed 's+-++g' | sed 's+:++g' | sed 's+Z++')

echo "CADU time: $CADU_TIME"

chmod -R 777 ${WORKPLANS_HOME}

#Patch jobOrder
sed -i 's+\*\*\*MissionID\*\*\*+'$SAT_ID'+g' ${JO}
sed -i 's+\*\*\*WORKING\*\*\*+'$REP_WORK'/workplans/'$SCENARIO'/+g' ${JO}
sed -i 's+\*\*\*CADU\*\*\*+'$CADU'+g' ${JO}
sed -i 's+\*\*\*DownlinkTime\*\*\*+'$CADU_TIME'+g' ${JO}
sed -i 's+\*\*\*AcquisitionStart\*\*\*+'$CADU_TIME'+g' ${JO}
sed -i 's+\*\*\*AcquisitionStation\*\*\*+'$ACQUI_STATION'+g' ${JO}
sed -i 's+\*\*\*ProcessingStation\*\*\*+'$PROC_STATION'+g' ${JO}
sed -i 's+\*\*\*TheoreticalLinePeriod\*\*\*+'$THEORETICAL_LINE_PERIOD'+g' ${JO}
sed -i 's+\*\*\*MinimumPacketLength\*\*\*+'$SP_MIN_SIZE'+g' ${JO}
sed -i 's+\*\*\*GPSToTAI\*\*\*+'$GPS_TIME_TAI'+g' ${JO}
sed -i 's+\*\*\*TAIToUTC\*\*\*+'$TAI_TO_UTC'+g' ${JO}
sed -i 's+\*\*\*OrbitOffset\*\*\*+'$ORBIT_OFFSET'+g' ${JO}
sed -i 's+\*\*\*CyclicOrbitOffset\*\*\*+'$TOTAL_ORBIT'+g' ${JO}

echo "JO: ${JO}"

echo -e "Launches the 'launch_eisp_ing_typ.bash' ..."
${INV_INSTALL}/ingestion/scripts/launch_eisp_ing_typ.bash ${JO}

#return_code=$?
retVal=$?
if [ $retVal -ne 0 ]; then
    echo "Error while launching the EISP, cleaning up ..."
    cleanup
    exit 1	
fi

echo -e "Move the internal(s) CADU output (DTxx) (${OUTPUT_CADU_DIR}/DT*) to ${REP_OUT}"
for p in $(find ${OUTPUT_CADU_DIR} -name 'DT*')
do
    echo "--> Move $p into ${REP_OUT} ..."
    mv ${p} ${REP_OUT}/
    echo $(get_origin_date $PRODUCT) > ${REP_OUT}/$(basename ${p})/DS/originDate.txt
    echo $(basename $PRODUCT) > ${REP_OUT}/$(basename ${p})/DS/sessionId.txt
done

echo -e "Generating SAD from ${OUTPUT_CADU_DIR}/*SADATA*) to ${REP_SAD}"
for p in $(find ${OUTPUT_CADU_DIR} -name '*AUX_SADATA*')
do
    echo "--> Tar $p into ${REP_SAD} ..."
    tar cf ${REP_SAD}/$(basename ${p}).tar -C $(dirname $p) $(basename $p)
done


echo -e "Generating HKTM from ${OUTPUT_CADU_DIR}/<date>/*HKTM*) to ${REP_HKTM}"
for p in $(find ${OUTPUT_CADU_DIR} -mindepth 1 -maxdepth 2 -name '*PRD_HKTM*')
do
    echo "--> Tar $p into ${REP_HKTM}/$(basename ${p} | sed 's+.SAFE+.tar+')"
    tar cf ${REP_HKTM}/$(basename ${p} | sed 's+.SAFE+.tar+') -C $(dirname $p) $(basename $p)
done


echo -e "Copying the KPI files to ${REP_KPI}"

for p in $(find ${OUTPUT_REP_PASS_E_DIR} -name '*REP_PASS_E*')
do
	echo "--> Move $p into ${REP_KPI} ..."
	mv ${p} ${REP_KPI}/.$(basename ${p})
	mv ${REP_KPI}/.$(basename ${p}) ${REP_KPI}/$(basename ${p})
done





