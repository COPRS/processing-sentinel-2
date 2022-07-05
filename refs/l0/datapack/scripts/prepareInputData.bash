#!/usr/bin/env bash

usage() {
  echo "  usage:   prepareInputData.bash <workplansHomePath> <scenarioName>"
  echo "    example: prepareInputData.bash /home/user/workplans scenario1"
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
}

WORKPLANS_HOME=$1
SCENARIO=$2


# Check argument availability
check_arguments ${WORKPLANS_HOME} ${SCENARIO}

WORKPLAN_STEP_DATA=${WORKPLANS_HOME}/${SCENARIO}/steps_data

echo "{WORKPLAN_STEP_DATA=" ${WORKPLAN_STEP_DATA}
echo "AUX_DATA_HOME=" ${AUX_DATA_HOME}

# -----------
# INIT_LOC_L0 
# -----------
# -> DEM (symbolic link)
cd ${WORKPLAN_STEP_DATA}/DEM/DEM_GLOBEF
ln -s ${AUX_DATA_HOME}/S2__OPER_DEM_GLOBEF_PDMC_20091210T235100_S20091210T235134  S2__OPER_DEM_GLOBEF_PDMC_20091210T235100_S20091210T235134
echo "ln -s " ${AUX_DATA_HOME}/S2__OPER_DEM_GLOBEF_PDMC_20091210T235100_S20091210T235134 " ${WORKPLAN_STEP_DATA}/DEM/DEM_GLOBEF/S2__OPER_DEM_GLOBEF_PDMC_20091210T235100_S20091210T235134"




