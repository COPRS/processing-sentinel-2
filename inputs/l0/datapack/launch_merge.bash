#!/bin/bash
usage() {
  echo "  usage:   launch_merge.bash <workplanRootPath> <scenarioName>"
  echo "    example: launch_merge.bash  /home/user/workplan scenario1"
}

check_arguments() {
  if [ -z "$1" ] ; then
    echo "missing <workplanRootPath> as first argument"
    usage
    exit 0
  fi
  if [ -z "$2" ] ; then
    echo "missing <scenarioName> as second argument"
    usage
    exit 0
  fi
}

WP_SCENARIO_ROOT=$1
SCENARIO_NAME=$2

# Check argument availability
check_arguments ${WP_SCENARIO_ROOT} ${SCENARIO_NAME}

export LAUNCH_DIR=`pwd`
export CHAIN_HOME=$LAUNCH_DIR
export LD_LIBRARY_PATH={LAUNCH_DIR}/lib/

export WPDIR=${WP_SCENARIO_ROOT}/${SCENARIO_NAME}/app_data

java -Dlog4j.configuration=./conf/log4j.xml -Xmx6g -Djava.library.path=$LD_LIBRARY_PATH -cp ${LAUNCH_DIR}/lib/*:${LAUNCH_DIR}/lib/dpc-telemetry-proc.jar s2.pdgs.dpc.telemetry.proc.msiMerge.MSIMergeProcessing -cmd "${WPDIR}/step_MSI_SAD_Merge/launch_1/job_order_l0c_${SCENARIO_NAME}_1_MSI_SAD_Merge.xml" -identity Processing >${WPDIR}/step_MSI_SAD_Merge/launch_1/traces_typ_merge1.log

if [[ "$(ls -A $WP_SCENARIO_ROOT/sadch1)" || "$(ls -A $WP_SCENARIO_ROOT/sadch2)" ]]
then
java -Dlog4j.configuration=./conf/log4j.xml -Xmx6g -Djava.library.path=$LD_LIBRARY_PATH -cp ${LAUNCH_DIR}/lib/*:${LAUNCH_DIR}/lib/dpc-telemetry-proc.jar s2.pdgs.dpc.telemetry.proc.msiSadMerge.MSISADMergeProcessing -cmd "${WPDIR}/step_MSI_SAD_Merge/launch_2/job_order_l0c_${SCENARIO_NAME}_2_MSI_SAD_Merge.xml" -identity Processing > ${WPDIR}/step_MSI_SAD_Merge/launch_2/traces_typ_merge2.log
fi

