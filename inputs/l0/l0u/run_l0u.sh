#!/bin/bash

INV_INSTALL="/usr/local/components/facilities/DPC-CORE-l0pack-ing-typ/DPC-CORE-l0pack-ing-typ-3.0.3"

WORKSPACE="/workspace"
OUTPUT="$WORKSPACE/OUTPUT"
WORKPLANS=$WORKSPACE/workplans
SCENARIO=$WORKPLANS/scenario

JOB_ORDER=$1

#cleanup(){
#    echo -e "Clean up data in installation folders"
#    rm -f /usr/local/components/facilities/DPC-CORE-l0pack-ing-typ/DPC-CORE-l0pack-ing-typ-*/ingestion/conf/Generic_Archive_Request.xml*
#    rm -f /usr/local/components/facilities/DPC-CORE-l0pack-ing-typ/DPC-CORE-l0pack-ing-typ-*/ingestion/conf/Production_Request_L0u.xml*
#    rm -f /usr/local/components/facilities/DPC-CORE-l0pack-ing-typ/DPC-CORE-l0pack-ing-typ-*/ingestion/conf/EISPProcTime2Orbit.log.*.gz
#    rm -rf /usr/local/components/facilities/DPC-CORE-l0pack-ing-typ/DPC-CORE-l0pack-ing-typ-*/ingestion/conf/DATA/archive/*
#    rm -rf /usr/local/components/facilities/DPC-CORE-l0pack-ing-typ/DPC-CORE-l0pack-ing-typ-*/ingestion/conf/DATA/outputFiles/*
#    echo -r  "Kill all remaining jobs"
#    killall -9 EISPProcessor || true
#    killall -9 launch_eisp_ing_typ.bash || true
#    killall -9 launch_eisp.bash || true
#    killall -9 launch_merge.bash || true
#    killall -9 launch_telemetry.bash || true
#    echo -e "Clean up workspace"
#    rm -rf $SCENARIO
#}

#cleanup

#echo -e "Create workplans tree"
#for folder in \
#    app_data/wp_data \
#    steps_data \
#    steps_data/L0U_DUMP \
#    steps_data/L0U_DUMP/imgch1 \
#    steps_data/L0U_DUMP/imgch2 \
#    steps_data/L0U_DUMP/MSIMerge \
#    steps_data/L0U_DUMP/MSISADMerge \
#    steps_data/L0U_DUMP/PR \
#    steps_data/L0U_DUMP/sadch1 \
#    steps_data/L0U_DUMP/sadch2 \
#    steps_data/L0U_DUMP/SADPDI \
#    steps_data/RAW \
#    steps_data/WORKING
#do
#    path=${SCENARIO}/${folder}
#    echo "Create $path ..."
#    mkdir -p $path
#done
#
#chmod -R 775 $SCENARIO

echo "Starting EISP processing."

if ! "${INV_INSTALL}"/ingestion/scripts/launch_eisp_ing_typ.bash "${JOB_ORDER}"
then
    echo "Error during EISP processing. Cleaning up ..."
    cleanup
    exit 1	
fi

#OUTPUT_CADU_DIR=${SCENARIO}/steps_data/L0U_DUMP/
#OUTPUT_REP_PASS_E_DIR=${SCENARIO}/steps_data/L0U_DUMP/scenario/app_data/EISP_DIR
#
#echo -e "Move the internal(s) CADU output (DTxx) (${OUTPUT_CADU_DIR}/DT*) to ${OUTPUT}"
#for p in $(find ${OUTPUT_CADU_DIR} -name 'DT*')
#do
#    echo "--> Move $p into ${REP_OUT} ..."
#    mv ${p} ${REP_OUT}/
#    echo $(get_origin_date $PRODUCT) > ${REP_OUT}/$(basename ${p})/DS/originDate.txt
#    echo $(basename $PRODUCT) > ${REP_OUT}/$(basename ${p})/DS/sessionId.txt
#done
#
#echo -e "Generating SAD from ${OUTPUT_CADU_DIR}/*SADATA*) to ${REP_SAD}"
#for p in $(find ${OUTPUT_CADU_DIR} -name '*AUX_SADATA*')
#do
#    echo "--> Tar $p into ${REP_SAD} ..."
#    tar cf ${REP_SAD}/$(basename ${p}).tar -C $(dirname $p) $(basename $p)
#done
#
#
#echo -e "Generating HKTM from ${OUTPUT_CADU_DIR}/<date>/*HKTM*) to ${REP_HKTM}"
#for p in $(find ${OUTPUT_CADU_DIR} -mindepth 1 -maxdepth 2 -name '*PRD_HKTM*')
#do
#    echo "--> Tar $p into ${REP_HKTM}/$(basename ${p} | sed 's+.SAFE+.tar+')"
#    tar cf ${REP_HKTM}/$(basename ${p} | sed 's+.SAFE+.tar+') -C $(dirname $p) $(basename $p)
#done
#
#
#echo -e "Copying the KPI files to ${REP_KPI}"
#
#for p in $(find ${OUTPUT_REP_PASS_E_DIR} -name '*REP_PASS_E*')
#do
#	echo "--> Move $p into ${REP_KPI} ..."
#	mv ${p} ${REP_KPI}/.$(basename ${p})
#	mv ${REP_KPI}/.$(basename ${p}) ${REP_KPI}/$(basename ${p})
#done
