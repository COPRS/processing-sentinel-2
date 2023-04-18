#!/bin/bash

launch_step_python() {
  echo "Launching python: $*"
  python "$@"
  ret=$?
#  if [ $ret -ne 0 ]; then
#    echo "Error while launching code $ret , retry once"
#    #remove_work "${TEMP_}"/*
#    python "$@"
#    ret=$?
#  fi
  if [ $ret -ne 0 ]; then
    echo "Error while launching code $ret ..."
    return 1
  fi
  return 0
}

remove_work() {
  if [ "$LOGLVL" != "DEBUG" ]; then
    echo "Removing $*"
    rm -rf "$@"
  fi
}

find_one_file() {
  one_file=$(find "$1" -name "$2" | head -n 1)
  echo "$one_file"
}

if [ $# -lt 7 ]; then
  echo "Launch_L2_DS.sh PRODUCT_L1C_DS GIPP_DIR STATIC_AUXS WORK_DIR OUT_DIR ACQUISITION_STATION PROCESSING_CENTER"
  exit 1
fi

set -o pipefail

PRODUCT_L1C_DS=$1
GIPP_DIR=$2
STATIC_AUXS=$3
WORK_DIR=$4
OUT_DIR=$5
ACQUISITION_STATION=$6
PROCESSING_CENTER=${7}

# Script surrounding the orchestrator launch to prepare a context based in folders:
CUR_DIR="$(
  cd "$(dirname "$0")" || exit
  pwd -P
)"

# CFI PATHS
SEN2COR_PATH=/home/Sen2Cor-02.10.03-Linux64/
COMPRESS_TILE_IMAGE_PATH=/dpc/app/CompressTileImage/06.01.00/
FORMAT_METADATA_DS_L2A_PATH=/dpc/app/s2ipf/FORMAT_METADATA_DS_L2A/06.01.00/
FORMAT_METADATA_TL_L2A_PATH=/dpc/app/s2ipf/FORMAT_METADATA_TILE_L2A/06.01.00/
OLQC_PATH=/dpc/app/s2ipf/OLQC/06.01.00/


if [[ ! -d "${WORK_DIR}" ]]; then
  echo "${WORK_DIR} folder doesnt exists"
  exit 1
fi

#LOGLVL=INFO
if [ -z ${LOGLVL+x} ]; then
  echo "LOGLVL is not set, default to INFO"
  LOGLVL=INFO
fi

echo "Treating: $PRODUCT_L1C_DS"

# Find the needed GIPPs
# GIP_L2ACSC
GIP_L2ACSC=$(find_one_file "$GIPP_DIR" "*GIP_L2ACSC*xml")
if [ -z "${GIP_L2ACSC}" ]; then
  echo "Impossible to find a file GIP_L2ACSC in $AUX_DIR, using default"
  GIP_L2ACSC=${SEN2COR_PATH}/lib/python2.7/site-packages/sen2cor/cfg/L2A_CAL_SC_GIPP.xml
fi
echo "GIP_L2ACSC : "$GIP_L2ACSC
# GIP_L2ACAC
GIP_L2ACAC=$(find_one_file $GIPP_DIR "*GIP_L2ACAC*xml")
if [ -z "${GIP_L2ACAC}" ]; then
  echo "Impossible to find a file GIP_L2ACAC in $AUX_DIR, using default"
  GIP_L2ACAC=${SEN2COR_PATH}/lib/python2.7/site-packages/sen2cor/cfg/L2A_CAL_AC_GIPP.xml
fi
echo "GIP_L2ACAC : "$GIP_L2ACAC
# GIP_L2ACFG
GIP_L2ACFG=$(find_one_file $GIPP_DIR "*GIP_L2ACFG*xml")
if [ -z "${GIP_L2ACFG}" ]; then
  echo "Impossible to find a file GIP_L2ACFG in $AUX_DIR, using default"
  GIP_L2ACFG=${SEN2COR_PATH}/lib/python2.7/site-packages/sen2cor/cfg/L2A_GIPP.xml
fi
echo "GIP_L2ACFG : "$GIP_L2ACFG
# GIP_PROBA2
GIP_PROBA2=$(find_one_file $GIPP_DIR "*GIP_PROBA2*xml")
if [ -z "${GIP_PROBA2}" ]; then
  echo "Impossible to find a file GIP_PROBA2 in $AUX_DIR, exit"
  exit 1
fi
echo "GIP_PROBA2 : $GIP_PROBA2"
# GIP_JP2KPA
GIP_JP2KPA=$(find_one_file $GIPP_DIR "S2*GIP_JP2KPA*xml")
if [ -z "${GIP_JP2KPA}" ]; then
  echo "Impossible to find a file GIP_JP2KPA in $AUX_DIR, exit"
  exit 1
fi
echo "GIP_JP2KPA : $GIP_JP2KPA"
# GIP_OLQCPA
GIP_OLQCPA=$(find_one_file $GIPP_DIR "*GIP_OLQCPA*zip")
if [ -z "${GIP_OLQCPA}" ]; then
  echo "Impossible to find a file GIP_OLQCPA in $AUX_DIR, exit"
  exit 1
fi
echo "GIP_OLQCPA : $GIP_OLQCPA"

# Find the L2 DEM
DEM_DIR=${STATIC_AUXS}/S2IPF-DEML2
if [[ ! -d ${DEM_DIR} ]]; then
   echo "${DEM_DIR} folder doesnt exists"
   exit 1
fi

# Find the L2 DEM
ESACCI_DIR=${STATIC_AUXS}/S2IPF-ESACCI
if [[ ! -d ${ESACCI_DIR} ]]; then
   echo "${ESACCI_DIR} folder doesnt exists"
   exit 1
fi
# install CCI in Sen2Cor install folder
for p in "${ESACCI_DIR}"/*
do
    echo "Linking $p to $SEN2COR_PATH/lib/python2.7/site-packages/sen2cor/aux_data/"
    ln -s "$p" $SEN2COR_PATH"/lib/python2.7/site-packages/sen2cor/aux_data/"
done

TEMP_WORK="$WORK_DIR/tmp_l2_ds_$(date +%Y%m%d%H%M%S)"
TEMP_OUT="$WORK_DIR/out_l2_ds_$(date +%Y%m%d%H%M%S)"

echo "Treating L1C DS product : $PROD, originDate: ${ORIGIN_DATE} , tmp: ${TEMP_WORK} , out: ${TEMP_OUT}"

# Replace DEM in GIPP CFG
mkdir -p "${TEMP_WORK}"/cfg
NEW_GIP_L2CFG=${TEMP_WORK}/cfg/$(basename "${GIP_L2ACFG}")
cp ${GIP_L2ACFG} "${NEW_GIP_L2CFG}"
sed -i 's+<DEM_Directory>.*</DEM_Directory>+<DEM_Directory>'${DEM_DIR}'</DEM_Directory>+' "${NEW_GIP_L2CFG}"
sed -i 's+<DEM_Reference>.*</DEM_Reference>+<DEM_Reference>CopernicusDEM90</DEM_Reference>+' "${NEW_GIP_L2CFG}"
sed -i 's+<row0>.*</row0>+<row0>OFF</row0>+' "${NEW_GIP_L2CFG}"
sed -i 's+<col0>.*</col0>+<col0>OFF</col0>+' "${NEW_GIP_L2CFG}"
sed -i 's+<Force_Exit_On_DEM_Error>.*</Force_Exit_On_DEM_Error>+<Force_Exit_On_DEM_Error>FALSE</Force_Exit_On_DEM_Error>+' "${NEW_GIP_L2CFG}"
sed -i 's+<Nr_Threads>.*</Nr_Threads>+<Nr_Threads>1</Nr_Threads>+' "${NEW_GIP_L2CFG}"

# Exctract L1C DS
WORK_DS="${PRODUCT_L1C_DS}"
MTD=$(find "${WORK_DS}" -maxdepth 2 -type f -name "*MTD_L1C_DS*xml")
if [ -z "$MTD" ]; then
  echo "No MTD found under ${WORK_DS}"
  exit 1
fi
SENSING_START=$(grep 'DATASTRIP_SENSING_START' "$MTD" | sed 's+.*<DATASTRIP_SENSING_START>++' | sed 's+</DATASTRIP_SENSING_START>++' | sed 's+Z+000+')
SENSING_STOP=$(grep 'DATASTRIP_SENSING_STOP' "$MTD" | sed 's+.*<DATASTRIP_SENSING_STOP>++' | sed 's+</DATASTRIP_SENSING_STOP>++' | sed 's+Z+000+')
echo "Datastrip sensing start : $SENSING_START"
echo "Datastrip sensing stop : $SENSING_STOP"

# Create JobOrder
JOB_ORDER=${TEMP_WORK}/cfg/JobOrderL2.xml
cp "${CUR_DIR}/job_order_ChaineL2.xml" "$JOB_ORDER"
sed -i 's+\@\@\@ACQUISITION_STATION\@\@\@+'$ACQUISITION_STATION'+' "$JOB_ORDER"
sed -i 's+\@\@\@PROCESSING_STATION\@\@\@+'$PROCESSING_CENTER'+'  "$JOB_ORDER"
sed -i 's+\@\@\@ARCHIVING_CENTER\@\@\@+'$PROCESSING_CENTER'+' "$JOB_ORDER"
sed -i 's+\@\@\@PROCESSING_CENTER\@\@\@+'$PROCESSING_CENTER'+' "$JOB_ORDER"
sed -i 's+\@\@\@NB_THREADS\@\@\@+1+' "$JOB_ORDER"
sed -i 's+\@\@\@MSI_L1C_DS\@\@\@+'"$WORK_DS"'+' "$JOB_ORDER"
sed -i 's+\@\@\@GIP_L2ACSC\@\@\@+'"$GIP_L2ACSC"'+' "$JOB_ORDER"
sed -i 's+\@\@\@GIP_L2ACAC\@\@\@+'"$GIP_L2ACAC"'+' "$JOB_ORDER"
sed -i 's+\@\@\@GIP_L2ACFG\@\@\@+'"$NEW_GIP_L2CFG"'+' "$JOB_ORDER"
sed -i 's+\@\@\@GIP_PROBA2\@\@\@+'"$GIP_PROBA2"'+' "$JOB_ORDER"
sed -i 's+\@\@\@GIP_JP2KPA\@\@\@+'"$GIP_JP2KPA"'+' "$JOB_ORDER"
sed -i 's+\@\@\@GIP_OLQCPA\@\@\@+'"$GIP_OLQCPA"'+' "$JOB_ORDER"
sed -i 's+\@\@\@SEN2COR_DIR_INSTALL\@\@\@+'$SEN2COR_PATH'+' "$JOB_ORDER"
sed -i 's+\@\@\@OLQC_DIR_INSTALL\@\@\@+'$OLQC_PATH'+' "$JOB_ORDER"
sed -i 's+\@\@\@COMPRESS_TILE_IMAGE_DIR_INSTALL\@\@\@+'$COMPRESS_TILE_IMAGE_PATH'+' "$JOB_ORDER"
sed -i 's+\@\@\@FORMAT_METADATA_DS_DIR_INSTALL\@\@\@+'$FORMAT_METADATA_DS_L2A_PATH'+' "$JOB_ORDER"
sed -i 's+\@\@\@FORMAT_METADATA_TL_DIR_INSTALL\@\@\@+'$FORMAT_METADATA_TL_L2A_PATH'+' "$JOB_ORDER"
mkdir -p "${TEMP_WORK}/work"
sed -i 's+\@\@\@WORKING\@\@\@+'"${TEMP_WORK}/work"'+' "$JOB_ORDER"
mkdir -p "${TEMP_OUT}"/L2_DS
mkdir -p "${TEMP_OUT}"/L2_TL
mkdir -p "${TEMP_OUT}"/L2_KPI
echo "afff"
sed -i 's+\@\@\@MSI_L2A_DS\@\@\@+'"${TEMP_OUT}"/L2_DS'+' "$JOB_ORDER"
sed -i 's+\@\@\@MSI_L2A_TL\@\@\@+'"${TEMP_OUT}"/L2_TL'+' "$JOB_ORDER"
sed -i 's+\@\@\@KPI_L2A\@\@\@+'"${TEMP_OUT}"/L2_KPI'+' "$JOB_ORDER"

echo "Start L2"
launch_step_python "$CUR_DIR/WrapperCore/Sen2corLauncher.py" --joborder "$JOB_ORDER" --mode DS
retVal=$?
echo "RetVal "$retVal
if [ $retVal -ne 0 ]; then
   echo "FAILED to process product $PRODUCT_L1C_DS"
   exit 1
fi

mkdir -p ${OUT_DIR}/INV_L2A_DS
mv "${TEMP_OUT}"/L2_DS/* "${OUT_DIR}/INV_L2A_DS"

mkdir -p ${OUT_DIR}/KPI_L2A_DS
mv "${TEMP_OUT}"/L2_KPI/* "${OUT_DIR}/KPI_L2A_DS"

