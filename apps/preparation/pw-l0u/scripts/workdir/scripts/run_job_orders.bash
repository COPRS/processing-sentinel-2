#!/usr/bin/env bash

usage() {
  echo "  usage:   run_job_orders.bash <workplansHomePath> <scenarioName> <stepName> <firstLaunch> <lastLaunch> "
  echo "    example: run_job_orders.bash /home/user/workplans scenario1 FORMAT_ISP 1 12"
}

check_arguments() {
  if [ -z "$1" ] ; then
    echo "missing <workplansHomePath> as first argument"
    usage
    exit 0
  fi
  if [ -z "$2" ] ; then
    echo "missing <scenarioName> as second argument"
    usage
    exit 0
  fi
  if [ -z "$3" ] ; then
    echo "missing <stepName> as third argument" 
    usage
    exit 0
  fi
  if [ -z "$4" ] ; then
    echo "missing <firstLaunch> as fourth argument"
    usage
    exit 0
  fi
  if [ -z "$5" ] ; then
    echo "missing <lastLaunch> as last argument"
    usage
    exit 0
  fi
}

check_max_launchs() {
  # $1 is WORKPLAN_INSTANCE_STEP_ROOT
  # $2 is LAST_LAUNCH
  numdirs=("$1"/*)
  numdirs=${#numdirs[@]}
  if (( $2 > ${numdirs} )); then
    echo "Last launch number ${2} is too big! Maximum allowed is ${numdirs}. Please reduce last launch value."
    exit 0
  else
    echo "Last launch number is OK!"
  fi
}

get_ipf_infos() {
  # $1 is STEP_NAME
  # set IPF_NAME
  # set IPF_VERSION
  IPF_NAME=$1
  IPF_VERSION="04.04.00"
  if [ $1 ==  "OLQC-L0cGr" ] || [ $1 ==  "OLQC-L0cDs" ]; then
    IPF_NAME="OLQC"
  fi
  if [ $1 == "INIT_LOC_L0" ] ; then
    IPF_VERSION="04.04.04"
  fi
  if [ $1 ==  "FORMAT_METADATA_DS_L0C" ] || [ $1 ==  "FORMAT_METADATA_GR_L0C" ]; then
    IPF_VERSION="04.04.01"
  fi
  if [ $1 ==  "EISP-ProcTime2orbit" ] ; then
    STEP_NAME="EISP"
    IPF_NAME="EISPProcTime2Orbit"
    IPF_VERSION="EISP-4.4"
  fi
  if [ $1 ==  "EISP-Processor" ] ; then
    STEP_NAME="EISP"
    IPF_NAME="EISPProcessor"
    IPF_VERSION="EISP-4.4"
  fi
}

WORKPLANS_HOME=$1
SCENARIO=$2
STEP_NAME=$3
FIRST_LAUNCH=$4
LAST_LAUNCH=$5

# Check argument availability
check_arguments ${WORKPLANS_HOME} ${SCENARIO} ${STEP_NAME} ${FIRST_LAUNCH} ${LAST_LAUNCH}

get_ipf_infos ${STEP_NAME}
#echo "STEP_NAME=" ${STEP_NAME}
#echo "IPF_NAME=" ${IPF_NAME}
#echo "IPF_VERSION=" ${IPF_VERSION}

if [ $STEP_NAME ==  "EISP" ] ; then
  IPF_EXE_PATH=/usr/local/components/$STEP_NAME/bin/${IPF_NAME}
  cd ${WORKPLANS_HOME}/${SCENARIO}/steps_data/CADU/
else
  IPF_EXE_PATH=/dpc/app/s2ipf/${IPF_NAME}/${IPF_VERSION}/scripts/${IPF_NAME}.bash
fi 
WORKPLAN_INSTANCE_STEP=${WORKPLANS_HOME}/${SCENARIO}/app_data/step_${STEP_NAME}
#echo WORKPLAN_INSTANCE_STEP ${WORKPLAN_INSTANCE_STEP}

# check last launch value coherency
check_max_launchs ${WORKPLAN_INSTANCE_STEP} ${LAST_LAUNCH}

# patch THALES begin
export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:/dpc/app/cots/kakadu/kakadu/scripts:/dpc/app/cots/kakadu/kakadu/lib"
export PATH="$PATH:/dpc/app/cots/kakadu/kakadu/bin"
# patch THALES end

for i in $(seq ${FIRST_LAUNCH} ${LAST_LAUNCH}); do  
  JOB_ORDER=${WORKPLAN_INSTANCE_STEP}/launch_${i}/job_order_l0c_${SCENARIO}_${i}_${STEP_NAME}.xml
  echo JOB_ORDER=${JOB_ORDER}
  echo IPF_EXE_PATH=${IPF_EXE_PATH}
  ${IPF_EXE_PATH} ${JOB_ORDER}
done
echo "======================= EXECUTION STATUS ===================================================="
find ${WORKPLAN_INSTANCE_STEP}/ -name "ipf_report_file.xml" -exec grep -Hni "EXECUTION_STATUS"  {} \;


