#!/bin/bash
# Copyright 2023 CS Group
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


launch_step_python() {
  echo "Launching python: $*"
  python "$@"
  ret=$?
#  if [ $ret -ne 0 ]; then
#    echo "Error while launching code $ret , retry once"
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

if [ $# -lt 9 ]; then
  echo "Launch_L2_TL.sh PRODUCT_L1C_TL PRODUCT_L1C_DS PRODUCT_L2A_DS AUX_DIR STATIC_AUXS WORK_DIR OUT_DIR ACQUISITION_STATION PROCESSING_CENTER"
  exit 1
fi


set -o pipefail

PRODUCT_L1C_TL=$1
PRODUCT_L1C_DS=$2
PRODUCT_L2A_DS=$3
AUX_DIR=$4
STATIC_AUXS=$5
WORK_DIR=$6
OUT_DIR=$7
ACQUISITION_STATION=$8
PROCESSING_CENTER=${9}


if [ $# -eq 10 ]; then
  NB_THREADS=${10}
fi


# Script surrounding the orchestrator launch to prepare a context based in folders:
CUR_DIR="$(
  cd "$(dirname "$0")" || exit
  pwd -P
)"

#LOGLVL=INFO
if [ -z ${LOGLVL+x} ]; then
  echo "LOGLVL is not set, default to INFO"
  LOGLVL=INFO
fi

# CFI PATHS
SEN2COR_PATH=/home/Sen2Cor-02.10.03-Linux64/
COMPRESS_TILE_IMAGE_PATH=/dpc/app/CompressTileImage/06.01.00/
FORMAT_METADATA_DS_L2A_PATH=/dpc/app/s2ipf/FORMAT_METADATA_DS_L2A/06.01.00/
FORMAT_METADATA_TL_L2A_PATH=/dpc/app/s2ipf/FORMAT_METADATA_TILE_L2A/06.01.00/
OLQC_PATH=/dpc/app/s2ipf/OLQC/06.01.00/


mkdir -p "${WORK_DIR}"
if [[ ! -d "${WORK_DIR}" ]]; then
  echo "${WORK_DIR} folder can't be created and doesnt exists"
  exit 1
fi


if [ -z "${PRODUCT_L1C_TL}" ]; then
  echo "Tile L1C not available"
  exit 1
fi
echo "Tile L1C : ${PRODUCT_L1C_TL}"
if [ -z "${PRODUCT_L2A_DS}" ]; then
  echo "Datastrip L2 not available"
  exit 1
fi
echo "Datastrip L2: $PRODUCT_L2A_DS"

if [ -z "${PRODUCT_L1C_DS}" ]; then
  echo "Datastrip L1C not available"
  exit 1
fi
echo "Datastrip L1: $PRODUCT_L1C_DS"

# Find the needed GIPPs
# GIP_L2ACSC
GIP_L2ACSC=$(find_one_file $AUX_DIR/S2IPF-GIPP "*GIP_L2ACSC*xml")
if [ -z "${GIP_L2ACSC}" ]; then
  echo "Impossible to find a file GIP_L2ACSC in $AUX_DIR, using default"
  GIP_L2ACSC=${SEN2COR_PATH}/lib/python2.7/site-packages/sen2cor/cfg/L2A_CAL_SC_GIPP.xml
fi
echo "GIP_L2ACSC : $GIP_L2ACSC"
# GIP_L2ACAC
GIP_L2ACAC=$(find_one_file $AUX_DIR/S2IPF-GIPP "*GIP_L2ACAC*xml")
if [ -z "${GIP_L2ACAC}" ]; then
  echo "Impossible to find a file GIP_L2ACAC in $AUX_DIR, using default"
  GIP_L2ACAC=${SEN2COR_PATH}/lib/python2.7/site-packages/sen2cor/cfg/L2A_CAL_AC_GIPP.xml
fi
echo "GIP_L2ACAC : $GIP_L2ACAC"
# GIP_L2ACFG
GIP_L2ACFG=$(find_one_file $AUX_DIR/S2IPF-GIPP "*GIP_L2ACFG*xml")
if [ -z "${GIP_L2ACFG}" ]; then
  echo "Impossible to find a file GIP_L2ACFG in $AUX_DIR, using default"
  GIP_L2ACFG=${SEN2COR_PATH}/lib/python2.7/site-packages/sen2cor/cfg/L2A_GIPP.xml
fi
echo "GIP_L2ACFG : $GIP_L2ACFG"
# GIP_PROBA2
GIP_PROBA2=$(find_one_file $AUX_DIR/S2IPF-GIPP "*GIP_PROBA2*xml")
if [ -z "${GIP_PROBA2}" ]; then
  echo "$(date +%Y%m%d%H%M) : Impossible to find a file GIP_PROBA2 in $AUX_DIR, exit"
  exit 1
fi
echo "GIP_PROBA2 : $GIP_PROBA2"
# GIP_JP2KPA
GIP_JP2KPA=$(find_one_file $AUX_DIR/S2IPF-GIPP "S2*GIP_JP2KPA*xml")
if [ -z "${GIP_JP2KPA}" ]; then
  echo "$(date +%Y%m%d%H%M) : Impossible to find a file GIP_JP2KPA in $AUX_DIR, exit"
  exit 1
fi
echo "GIP_JP2KPA : $GIP_JP2KPA"
# GIP_OLQCPA
GIP_OLQCPA=$(find_one_file $AUX_DIR/S2IPF-GIPP "*GIP_OLQCPA*zip")
if [ -z "${GIP_OLQCPA}" ]; then
  echo "$(date +%Y%m%d%H%M) : Impossible to find a file GIP_OLQCPA in $AUX_DIR, exit"
  exit 1
fi
echo "GIP_OLQCPA : $GIP_OLQCPA"

# Find the L2 DEM
DEM_DIR=${STATIC_AUXS}/S2IPF-DEML2
if [[ ! -d ${DEM_DIR} ]]; then
  echo ${DEM_DIR}" folder doesnt exists"
  exit 1
fi

# Find the ESACCI
ESACCI_DIR=${STATIC_AUXS}/S2IPF-ESACCI
if [[ ! -d ${ESACCI_DIR} ]]; then
  echo ${ESACCI_DIR}" folder doesnt exists"
  exit 1
fi
# install CCI in Sen2Cor install folder
for p in "${ESACCI_DIR}"/*
do
    echo "Linking $p to $SEN2COR_PATH/lib/python2.7/site-packages/sen2cor/aux_data/"
    ln -s "$p" $SEN2COR_PATH"/lib/python2.7/site-packages/sen2cor/aux_data/"
done

TEMP_WORK="$WORK_DIR/tmp_l2_tl_$(date +%Y%m%d%H%M%S)"
TEMP_OUT="$WORK_DIR/out_l2_tl_$(date +%Y%m%d%H%M%S)"

echo "$(date +%Y%m%d%H%M) : Treating L1C product : $PRODUCT_L1C_TL , tmp: ${TEMP_WORK} , out: ${TEMP_OUT}"

# Replace DEM in GIPP CFG
mkdir -p "${TEMP_WORK}"/cfg
NEW_GIP_L2CFG=${TEMP_WORK}/cfg/$(basename "${GIP_L2ACFG}")
cp "${GIP_L2ACFG}" "${NEW_GIP_L2CFG}"
sed -i 's+<DEM_Directory>.*</DEM_Directory>+<DEM_Directory>'${DEM_DIR}'</DEM_Directory>+' "${NEW_GIP_L2CFG}"
sed -i 's+<DEM_Reference>.*</DEM_Reference>+<DEM_Reference>CopernicusDEM90</DEM_Reference>+' "${NEW_GIP_L2CFG}"
sed -i 's+<row0>.*</row0>+<row0>OFF</row0>+' "${NEW_GIP_L2CFG}"
sed -i 's+<col0>.*</col0>+<col0>OFF</col0>+' "${NEW_GIP_L2CFG}"
sed -i 's+<Nr_Threads>.*</Nr_Threads>+<Nr_Threads>1</Nr_Threads>+' "${NEW_GIP_L2CFG}"
if [ -n "${NB_THREADS}" ]; then
  sed -i 's+<Nr_Threads>.*</Nr_Threads>+<Nr_Threads>'${NB_THREADS}'</Nr_Threads>+' "${NEW_GIP_L2CFG}"
fi
sed -i 's+<Force_Exit_On_DEM_Error>.*</Force_Exit_On_DEM_Error>+<Force_Exit_On_DEM_Error>FALSE</Force_Exit_On_DEM_Error>+' "${NEW_GIP_L2CFG}"

# If threads asked patch JP2KPA to use multithread in kakadu
NEW_GIP_JP2KAK=${GIP_JP2KPA}
#if [ -n "${NB_THREADS}" ]; then
#  NEW_GIP_JP2KAK=${TEMP_WORK}/cfg/$(basename "${GIP_JP2KPA}")
#  cp "${GIP_JP2KPA}" "${NEW_GIP_JP2KAK}"
#  sed -i 's+<NUMBER_OF_THREAD>.*</NUMBER_OF_THREAD>+<NUMBER_OF_THREAD>'${NB_THREADS}'</NUMBER_OF_THREAD>+g' "${NEW_GIP_JP2KAK}"
#fi

# Exctract L1C DS and TL
#if [ -d "$PRODUCT_L1C_TL" ];then
#  echo "$(date +%Y%m%d%H%M) : Failed to decompress $PROD"
#    exit 1
#  fi
#else
#  echo "$(date +%Y%m%d%H%M) : Product no more reachable $PRODUCT_L1C_TL"
#  exit 1
#fi

WORK_TL="$PRODUCT_L1C_TL"
WORK_DS="$PRODUCT_L1C_DS"
MTD=$(find "${WORK_DS}" -maxdepth 2 -type f -name "*MTD_L1C_DS*xml")
if [ -z "$MTD" ]; then
  echo "No MTD found under $WORK_DS"
  exit 1
fi

SENSING_START=$(grep 'DATASTRIP_SENSING_START' "$MTD" | sed 's+.*<DATASTRIP_SENSING_START>++' | sed 's+</DATASTRIP_SENSING_START>++' | sed 's+Z+000+')
SENSING_STOP=$(grep 'DATASTRIP_SENSING_STOP' "$MTD" | sed 's+.*<DATASTRIP_SENSING_STOP>++' | sed 's+</DATASTRIP_SENSING_STOP>++' | sed 's+Z+000+')
echo "Datastrip sensing start : $SENSING_START"
echo "Datastrip sensing stop : $SENSING_STOP"

# Create JobOrder
JOB_ORDER=${TEMP_WORK}/cfg/JobOrderL2.xml
cp "${CUR_DIR}"/job_order_ChaineL2.xml "$JOB_ORDER"
sed -i 's+\@\@\@ACQUISITION_STATION\@\@\@+'$ACQUISITION_STATION'+' "$JOB_ORDER"
sed -i 's+\@\@\@PROCESSING_STATION\@\@\@+'$PROCESSING_CENTER'+'  "$JOB_ORDER"
sed -i 's+\@\@\@ARCHIVING_CENTER\@\@\@+'$PROCESSING_CENTER'+' "$JOB_ORDER"
sed -i 's+\@\@\@PROCESSING_CENTER\@\@\@+'$PROCESSING_CENTER'+' "$JOB_ORDER"
sed -i 's+\@\@\@NB_THREADS\@\@\@+1+' "$JOB_ORDER"
sed -i 's+\@\@\@MSI_L1C_DS\@\@\@+'"$WORK_DS"'+' "$JOB_ORDER"
sed -i 's+\@\@\@MSI_L1C_TL\@\@\@+'"$WORK_TL"'+' "$JOB_ORDER"
sed -i 's+\@\@\@GIP_L2ACSC\@\@\@+'"$GIP_L2ACSC"'+' "$JOB_ORDER"
sed -i 's+\@\@\@GIP_L2ACAC\@\@\@+'"$GIP_L2ACAC"'+' "$JOB_ORDER"
sed -i 's+\@\@\@GIP_L2ACFG\@\@\@+'"$NEW_GIP_L2CFG"'+' "$JOB_ORDER"
sed -i 's+\@\@\@GIP_PROBA2\@\@\@+'"$GIP_PROBA2"'+' "$JOB_ORDER"
sed -i 's+\@\@\@GIP_JP2KPA\@\@\@+'"$NEW_GIP_JP2KAK"'+' "$JOB_ORDER"
sed -i 's+\@\@\@GIP_OLQCPA\@\@\@+'"$GIP_OLQCPA"'+' "$JOB_ORDER"
sed -i 's+\@\@\@SEN2COR_DIR_INSTALL\@\@\@+'"$SEN2COR_PATH"'+' "$JOB_ORDER"
sed -i 's+\@\@\@OLQC_DIR_INSTALL\@\@\@+'"$OLQC_PATH"'+' "$JOB_ORDER"
sed -i 's+\@\@\@COMPRESS_TILE_IMAGE_DIR_INSTALL\@\@\@+'"$COMPRESS_TILE_IMAGE_PATH"'+' "$JOB_ORDER"
sed -i 's+\@\@\@FORMAT_METADATA_DS_DIR_INSTALL\@\@\@+'"$FORMAT_METADATA_DS_L2A_PATH"'+' "$JOB_ORDER"
sed -i 's+\@\@\@FORMAT_METADATA_TL_DIR_INSTALL\@\@\@+'"$FORMAT_METADATA_TL_L2A_PATH"'+' "$JOB_ORDER"
mkdir -p "${TEMP_WORK}"/work
sed -i 's+\@\@\@WORKING\@\@\@+'"${TEMP_WORK}/work"'+' "$JOB_ORDER"
mkdir -p "${TEMP_OUT}/L2_TL"
mkdir -p "${TEMP_OUT}/L2_KPI"
sed -i 's+\@\@\@MSI_L2A_DS\@\@\@+'"$(dirname "$PRODUCT_L2A_DS")"'+' "$JOB_ORDER"
sed -i 's+\@\@\@MSI_L2A_TL\@\@\@+'"${TEMP_OUT}/L2_TL"'+' "$JOB_ORDER"
sed -i 's+\@\@\@KPI_L2A\@\@\@+'"${TEMP_OUT}/L2_KPI"'+' "$JOB_ORDER"

echo "$(date +%Y%m%d%H%M) : Start L2"
launch_step_python "$CUR_DIR/WrapperCore/Sen2corLauncher.py" --joborder "$JOB_ORDER" --mode TL
retVal=$?
echo "Sen2Cor exit code : "$retVal
if [ $retVal -ne 0 ]; then
  echo "FAILED to process $PRODUCT_L1C_TL"
  exit 1
fi

mkdir -p "${OUT_DIR}"/INV_L2A_TL
find "${TEMP_OUT}"/L2_TL/ -mindepth 1 -print0 | while read -rd $'\0' g; do
  echo "Moving $g to ${OUT_DIR}/INV_L2A_TL/"
  mv "$g" "${OUT_DIR}/INV_L2A_TL/"
done

mkdir -p "${OUT_DIR}"/KPI_L2A_TL
find "${TEMP_OUT}"/L2_KPI/ -mindepth 1 -print0 | while read -rd $'\0' g; do
  echo "Moving $g to ${OUT_DIR}/KPI_L2A_TL/"
  mv "$g" "${OUT_DIR}/KPI_L2A_TL/"
done

exit 0
