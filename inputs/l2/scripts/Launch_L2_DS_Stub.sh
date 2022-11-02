#!/bin/bash

send_to_prip() {
  touch batch.sftp
  chmod 666 "$1"
  echo "Pushing $1 to PRIP ${PRIP_INGESTION_DIR}/ folder"
  echo "put $1 ${PRIP_INGESTION_DIR}/.$(basename "$1")" > batch.sftp
  echo "rename ${PRIP_INGESTION_DIR}/.$(basename "$1") ${PRIP_INGESTION_DIR}/$(basename "$1")" >>batch.sftp
  sshpass -p "${PRIP_PASSWORD}" sftp -oBatchMode=no -b batch.sftp "${PRIP_USERNAME}@${PRIP_URL}"
  retVal=$?
  if [ $retVal -ne 0 ]; then
    echo "Error while pushing $1, retry once"
    sleep 2
    sshpass -p "${PRIP_PASSWORD}" sftp -oBatchMode=no -b batch.sftp "${PRIP_USERNAME}@${PRIP_URL}"
    retVal=$?
    if [ $retVal -ne 0 ]; then
      echo "Error while pushing $1, skip"
    fi
  fi
  rm -f batch.sftp
  return $retVal
}

send_to_kpi() {
  touch batch.sftp
  echo "Pushing $1 to KPI $2 folder"
  chmod 666 "$1"
  echo "put $1 $2/.$(basename "$1")" >batch.sftp
  echo "rename $2/.$(basename "$1") $2/$(basename "$1")" >>batch.sftp
  sshpass -p "${KPI_PASSWORD}" sftp -oBatchMode=no -b batch.sftp "${KPI_USERNAME}@${KPI_URL}"
  retVal=$?
  if [ $retVal -ne 0 ]; then
    echo "Error while pushing $1, retry once"
    sleep 2
    sshpass -p "${KPI_PASSWORD}" sftp -oBatchMode=no -b batch.sftp "${KPI_USERNAME}@${KPI_URL}"
    retVal=$?
    if [ $retVal -ne 0 ]; then
      echo "Error while pushing $1, skip"
    fi
  fi
  rm -f batch.sftp
  return $retVal
}

launch_step() {
  echo "Launching : $*"
  bash "$@"
  ret=$?
  if [ $ret -ne 0 ]; then
    echo "Error while launching code $ret , retry once"
    remove_work "${TEMP_WORK}"
    bash "$@"
    ret=$?
  fi
  echo "RetVal :"$ret
  if [ $ret -ne 0 ]; then
    echo "Error while launching code $ret ..."
    return 1
  fi
  return 0
}

launch_step_python() {
  echo "Launching python: $*"
  echo "Launching python: $*" >> "${LOG_FILE}"
  python "$@"
  ret=$?
  if [ $ret -ne 0 ]; then
    echo "Error while launching code $ret , retry once"
    echo "Error while launching code $ret , retry once" >> "${LOG_FILE}"
    remove_work "${TEMP_WORK}"/*
    python "$@"
    ret=$?
  fi
  echo "RetVal :"$ret
  if [ $ret -ne 0 ]; then
    echo "Error while launching code $ret ..."
    echo "Error while launching code $ret ..." >> "${LOG_FILE}"
    return 1
  fi
  return 0
}
handle_return() {
  if [ "$1" -ne 0 ]; then
    echo "Error while launching ... $1"
    echo "Error treating $(basename "$(dirname "$2")")"
    echo "Error treating $(basename "$(dirname "$2")")" >>"${3}"
    echo "Error treating $(basename "$(dirname "$2")")" >>"${4}"
    echo rm -rf "${5}"/*
    echo "rm -rf ${5}/*" >>"${3}"
    remove_work "${5}"/*
    echo rm -rf "${6}"/*
    echo "rm -rf ${6}/*" >>"${3}"
    remove_work "${6}"/*
    echo rm -f /core.*
    rm -f /core.*
    echo "rm $(dirname "$7")/.lock_$(basename "$7")"
    echo "rm $(dirname "$7")/.lock_$(basename "$7")" >>"${3}"
    rm "$(dirname "$7")/.lock_$(basename "$7")"
    echo "rm $(dirname "$7")/.lock_$(basename "$7")_$HOST"
    echo "rm $(dirname "$7")/.lock_$(basename "$7")_$HOST" >>"${3}"
    rm "$(dirname "$7")/.lock_$(basename "$7")_$HOST"
  fi
  return "$1"
}

has_others_lock() {
  local LOCKS
  LOCKS=$(find "$(dirname "$1")" -maxdepth 1 -type f -name ".lock_$(basename "$1")_*" | grep -v "$HOST" | tail -n 1)
  if [ -z "$LOCKS" ]; then
    echo "NO"
  else
    echo "YES"
  fi
}

has_already_been_in_error(){
  local LOCKS
  LOCKS=$(find "$(dirname "$1")" -maxdepth 1 -type f -name ".lock_$(basename "$1")_*" | grep "$HOST" | tail -n 1)
  if [ -z "$LOCKS" ]; then
    echo "NO"
  else
    echo "YES"
  fi
}

has_too_much_errors() {
  local ERRORS
  ERRORS=$(find "$(dirname "$1")" -maxdepth 1 -type f -name ".error_$(basename "$1")_*" | grep -c "$HOST")
  if [ "$ERRORS" -gt 2 ]; then
    echo "YES"
  else
    echo "NO"
  fi
}

is_older_lock() {
  OUR_LOCK_DATE=$(($(stat -c%y "$(dirname "$1")/.lock_$(basename "$1")_$HOST" | sed 's+ \+.*++' | sed 's+-++g' | sed 's+ ++' | sed 's+:++g' | sed 's+\.\([0-9][0-9]\).*+\1+')))
  for l in $(find "$1" -maxdepth 1 -type f -name ".lock_$(basename "$1")_*" | grep -v "$HOST"); do
    IN_LOCK_DATE=$(($(stat -c%y "$l" | sed 's+ \+.*++' | sed 's+-++g' | sed 's+ ++' | sed 's+:++g' | sed 's+\.\([0-9][0-9]\).*+\1+')))
    if [ "$OUR_LOCK_DATE" -gt "$IN_LOCK_DATE" ]; then
      echo "NO"
      return
    fi
  done
  echo "YES"
  return
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

if [ $# -lt 2 ]; then
  echo "Launch_L2_DS.sh PRODUCTS_DIR TIME_SLEEP_LOOP"
  exit 1
fi

if [ -z ${PRIP_USERNAME+x} ]; then
  echo "PRIP_USERNAME is not set"
fi
if [ -z ${PRIP_URL+x} ]; then
  echo "PRIP_URL is not set"
fi
if [ -z ${PRIP_PASSWORD+x} ]; then
  echo "PRIP_PASSWORD is not set"
fi

if [ -z ${KPI_USERNAME+x} ]; then
  echo "KPI_USERNAME is not set"
fi
if [ -z ${KPI_URL+x} ]; then
  echo "KPI_URL is not set"
fi
if [ -z ${KPI_PASSWORD+x} ]; then
  echo "KPI_PASSWORD is not set"
fi

# PRIP ingestion dir
if [ -z ${PRIP_INGESTION_DIR+x} ]; then
  echo "PRIP_INGESTION_DIR is not set, default to files/data/PDIs_IN_QA_L2A"
  PRIP_INGESTION_DIR="files/data/PDIs_IN_QA_L2A"
fi
echo "PRIP_INGESTION_DIR= "${PRIP_INGESTION_DIR}

set -o pipefail
HOST=$(hostname)
echo "Running on $HOST"

PRODUCT_DIR=$1
TIME_SLEEP_LOOP=$2

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
if [ -z ${AUX_DIR+x} ]; then
  echo "AUX_DIR is not set, default to /mnt/shared/S2-STUB-AUX"
  AUX_DIR=/mnt/shared/S2-STUB-AUX-L2
fi
#AUX_DIR=/tera/IntegrationL1/aux/
#AUX_DIR=/tera/IntegrationL1/aux/
if [ -z ${STATIC_AUXS+x} ]; then
  echo "STATIC_AUXS is not set, default to /mnt/shared/S2-STATIC-AUXS"
  STATIC_AUXS=/mnt/shared/S2-STATIC-AUXS
fi
#STATIC_AUXS=/tera/IntegrationL1/static-aux

WORK_DIR=/mnt/local/L2_stub/work_ds_$HOST
OUT_DIR=/mnt/local/L2_stub/out_ds_$HOST
LOG_DIR=/mnt/shared/STUB_L2REPORTS/$HOST
PRIP_DIR=/mnt/local/STUB_PRIP_L2DS_$HOST
KPI_DIR=/mnt/local/STUB_KPI_L2DS_$HOST
#Local
#WORK_DIR=/tera/L2/L2_stub/work
#OUT_DIR=/tera/L2/L2_stub/out
#LOG_DIR=/tera/L2/STUB_L2REPORTS/$HOST
#PRIP_DIR=/tera/L2/STUB_PRIP
#KPI_DIR=/tera/L2/STUB_KPI

# CFI PATHS
SEN2COR_PATH=/home/Sen2Cor-02.10.01-Linux64/
COMPRESS_TILE_IMAGE_PATH=/dpc/app/CompressTileImage/05.00.00/
FORMAT_METADATA_DS_L2A_PATH=/dpc/app/s2ipf/FORMAT_METADATA_DS_L2A/05.00.01/
FORMAT_METADATA_TL_L2A_PATH=/dpc/app/s2ipf/FORMAT_METADATA_TILE_L2A/05.00.01/
OLQC_PATH=/dpc/app/s2ipf/OLQC/05.00.00/


mkdir -p "${WORK_DIR}"
if [[ ! -d "${WORK_DIR}" ]]; then
  echo "${WORK_DIR} folder can't be created and doesnt exists"
  exit 1
fi
remove_work "${WORK_DIR}"/tmp.*
mkdir -p "${OUT_DIR}"
if [[ ! -d "${OUT_DIR}" ]]; then
  echo "${OUT_DIR} folder can't be created and doesnt exists"
fi
remove_work "${OUT_DIR}"/tmp.*
mkdir -p "${KPI_DIR}"
if [[ ! -d "${KPI_DIR}" ]]; then
  echo "${KPI_DIR} folder can't be created and doesnt exists"
  exit 1
fi
mkdir -p "${PRIP_DIR}"
if [[ ! -d "${PRIP_DIR}" ]]; then
  echo "${PRIP_DIR} folder can't be created and doesnt exists"
  exit 1
fi
mkdir -p "${LOG_DIR}"
if [[ ! -d "${LOG_DIR}" ]]; then
  echo "${LOG_DIR} folder can't be created and doesnt exists"
  exit 1
fi

while true; do

  find "${PRODUCT_DIR}" -maxdepth 3 -type f -name "S2*L1C_DS*tar" >"${WORK_DIR}/ListDS.txt"
  shuf "${WORK_DIR}/ListDS.txt" > "${WORK_DIR}/ListDSRandom.txt"

  while IFS= read -r f; do
    remove_work "${WORK_DIR}"/tmp.*
    remove_work "${OUT_DIR}"/tmp.*

    PROD="$f"
    echo "Found $PROD"

    LOG_FILE=${LOG_DIR}/report_$(date +%Y%m%d%H%M)_$(basename "$PROD").log
    ERR_FILE=${LOG_DIR}/report_$(date +%Y%m%d%H%M)_$(basename "$PROD").err
    KPI_FILE=${LOG_DIR}/kpi_$(date +%Y%m%d%H%M)_$(basename "$PROD").log
    PRIP_FILE=${LOG_DIR}/prip_$(date +%Y%m%d%H%M)_$(basename "$PROD").log
    STATUS_FILE=${LOG_DIR}/status_$(date +%Y%m%d%H%M)_$(basename "$PROD").log

    # Has previous errors ?
    OWN_ERROR=$(has_already_been_in_error "$PROD")
    if [ "$OWN_ERROR" == "YES" ]; then
      echo "Product $PROD is in error on this host already"
      continue
    fi
    IN_ERROR=$(has_too_much_errors "$PROD")
    if [ "$IN_ERROR" == "YES" ]; then
      echo "Product $PROD is in error more than 3 times"
      continue
    fi
    if [ -f "$(dirname "$PROD")/.treated_$(basename "$PROD")" ]; then
      echo "Product already treated"
      continue
    fi
    if [ -f "$(dirname "$PROD")/.lock_$(basename "$PROD")" ]; then
      echo "Product $PROD is locked by another instance"
      continue
    fi

    # Find the Origin date in log
    # Test if originDate.txt exists
    if [ -f "$(dirname "$(dirname "$PROD")")/originDate.txt" ]; then
      echo "$(dirname "$(dirname "$PROD")")/originDate.txt found"
      ORIGIN_DATE=$(cat "$(dirname "$(dirname "$PROD")")/originDate.txt")
    else
      echo "OriginDate not available, skip"
      continue
    fi
    echo "Origin Date : ${ORIGIN_DATE}"

    # Find the needed GIPPs
    # GIP_L2ACSC
    GIP_L2ACSC=$(find_one_file $AUX_DIR/S2IPF-GIPP "*GIP_L2ACSC*xml")
    if [ -z "${GIP_L2ACSC}" ]; then
      echo "Impossible to find a file GIP_L2ACSC in $AUX_DIR, using default"
      GIP_L2ACSC=${SEN2COR_PATH}/lib/python2.7/site-packages/sen2cor/cfg/L2A_CAL_SC_GIPP.xml
    fi
    echo "GIP_L2ACSC : "$GIP_L2ACSC
    # GIP_L2ACAC
    GIP_L2ACAC=$(find_one_file $AUX_DIR/S2IPF-GIPP "*GIP_L2ACAC*xml")
    if [ -z "${GIP_L2ACAC}" ]; then
      echo "Impossible to find a file GIP_L2ACAC in $AUX_DIR, using default"
      GIP_L2ACAC=${SEN2COR_PATH}/lib/python2.7/site-packages/sen2cor/cfg/L2A_CAL_AC_GIPP.xml
    fi
    echo "GIP_L2ACAC : "$GIP_L2ACAC
    # GIP_L2ACFG
    GIP_L2ACFG=$(find_one_file $AUX_DIR "*GIP_L2ACFG*xml")
    if [ -z "${GIP_L2ACFG}" ]; then
      echo "Impossible to find a file GIP_L2ACFG in $AUX_DIR, using default"
      GIP_L2ACFG=${SEN2COR_PATH}/lib/python2.7/site-packages/sen2cor/cfg/L2A_GIPP.xml
    fi
    echo "GIP_L2ACFG : "$GIP_L2ACFG
    # GIP_PROBA2
    GIP_PROBA2=$(find_one_file $AUX_DIR "*GIP_PROBA2*xml")
    if [ -z "${GIP_PROBA2}" ]; then
      echo "Impossible to find a file GIP_PROBA2 in $AUX_DIR, exit"
      exit 1
    fi
    echo "GIP_PROBA2 : $GIP_PROBA2"
    # GIP_JP2KPA
    GIP_JP2KPA=$(find_one_file $AUX_DIR "S2*GIP_JP2KPA*xml")
    if [ -z "${GIP_JP2KPA}" ]; then
      echo "Impossible to find a file GIP_JP2KPA in $AUX_DIR, exit"
      exit 1
    fi
    echo "GIP_JP2KPA : $GIP_JP2KPA"
    # GIP_OLQCPA
    GIP_OLQCPA=$(find_one_file $AUX_DIR "*GIP_OLQCPA*zip")
    if [ -z "${GIP_OLQCPA}" ]; then
      echo "Impossible to find a file GIP_OLQCPA in $AUX_DIR, exit"
      exit 1
    fi
    echo "GIP_OLQCPA : $GIP_OLQCPA"

    # Find the L2 DEM
    DEM_DIR=${STATIC_AUXS}/S2IPF-DEML2
    if [[ ! -d ${DEM_DIR} ]]; then
      echo ${DEM_DIR}" folder doesnt exists"
      exit 1
    fi

    # Find the L2 DEM
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

    has_locks=$(has_others_lock "$PROD")
    if [ "$has_locks" == "YES" ]; then
      echo "Product $PROD is claimed by another instance"
      continue
    else
      touch "$(dirname "$PROD")/.lock_$(basename "$PROD")_$HOST"
      #sleep 1s
      has_locks=$(has_others_lock "$PROD")
      if [ "$has_locks" == "YES" ]; then
        is_older=$(is_older_lock "$PROD")
        if [ "$is_older" == "YES" ]; then
          touch "$(dirname "$PROD")/.lock_$(basename "$PROD")"
        else
          echo "Product $PROD : we are not the best"
          rm -f "$(dirname "$PROD")/.lock_$(basename "$PROD")_$HOST"
          continue
        fi
      else
        touch "$(dirname "$PROD")/.lock_$(basename "$PROD")"
      fi
    fi

    TEMP_WORK=$(mktemp -p "${WORK_DIR}" -d)
    TEMP_OUT=$(mktemp -p "${OUT_DIR}" -d)

    echo "Treating L1C DS product : $PROD, originDate: ${ORIGIN_DATE} , tmp: ${TEMP_WORK} , out: ${TEMP_OUT} , log: ${LOG_FILE}, err: ${ERR_FILE}"
    echo "Treating L1C DS product : $PROD, originDate: ${ORIGIN_DATE} , tmp: ${TEMP_WORK} , out: ${TEMP_OUT} , log: ${LOG_FILE}, err: ${ERR_FILE}" >"${LOG_FILE}"
    echo "Treating L1C DS product : $PROD, originDate: ${ORIGIN_DATE} , tmp: ${TEMP_WORK} , out: ${TEMP_OUT} , log: ${LOG_FILE}, err: ${ERR_FILE}" >"${ERR_FILE}"

    # Replace DEM in GIPP CFG
    mkdir -p "${TEMP_WORK}"/cfg
    NEW_GIP_L2CFG=${TEMP_WORK}/cfg/$(basename "${GIP_L2ACFG}")
    cp ${GIP_L2ACFG} "${NEW_GIP_L2CFG}"
    sed -i 's+<DEM_Directory>.*</DEM_Directory>+<DEM_Directory>'${DEM_DIR}'</DEM_Directory>+' "${NEW_GIP_L2CFG}"
    sed -i 's+<DEM_Reference>.*</DEM_Reference>+<DEM_Reference>CopernicusDEM90</DEM_Reference>' "${NEW_GIP_L2CFG}"
    sed -i 's+<row0>.*</row0>+<row0>AUTO</row0>+' "${NEW_GIP_L2CFG}"
    sed -i 's+<col0>.*</col0>+<col0>AUTO</col0>+' "${NEW_GIP_L2CFG}"
    sed -i 's+<Force_Exit_On_DEM_Error>.*</Force_Exit_On_DEM_Error>+<Force_Exit_On_DEM_Error>FALSE</Force_Exit_On_DEM_Error>+' "${NEW_GIP_L2CFG}"
    sed -i 's+<Nr_Threads>.*</Nr_Threads>+<Nr_Threads>1</Nr_Threads>+' "${NEW_GIP_L2CFG}"

    # Exctract L1C DS and TL
    mkdir -p "${TEMP_WORK}"/DS
    tar xf "${PROD}" -C "${TEMP_WORK}"/DS/
    WORK_DS=$(find_one_file "${TEMP_WORK}"/DS/ "S2*L1C_DS*")
    MTD=$(find "${WORK_DS}" -maxdepth 2 -type f -name "*MTD_L1C_DS*xml")
    if [ -z "$MTD" ]; then
      echo "No MTD found under $f"
      continue
    fi
    SENSING_START=$(grep 'DATASTRIP_SENSING_START' "$MTD" | sed 's+.*<DATASTRIP_SENSING_START>++' | sed 's+</DATASTRIP_SENSING_START>++' | sed 's+Z+000+')
    SENSING_STOP=$(grep 'DATASTRIP_SENSING_STOP' "$MTD" | sed 's+.*<DATASTRIP_SENSING_STOP>++' | sed 's+</DATASTRIP_SENSING_STOP>++' | sed 's+Z+000+')
    #SENSING_START_FRT=$(echo $SENSING_START | sed 's+-++g' | sed 's+T++' | sed 's+:++g' | sed 's+\..*++')
    #SENSING_STOP_FRT=$(echo $SENSING_STOP | sed 's+-++g' | sed 's+T++' | sed 's+:++g' | sed 's+\..*++')
		echo "Datastrip sensing start : $SENSING_START"
		echo "Datastrip sensing stop : $SENSING_STOP"
		echo "Datastrip sensing start : $SENSING_START" >> "$LOG_FILE"
		echo "Datastrip sensing stop : $SENSING_STOP" >> "$LOG_FILE"

    # Create JobOrder
    JOB_ORDER=${TEMP_WORK}/cfg/JobOrderL2.xml
    cp "${CUR_DIR}"/job_order_ChaineL2.xml "$JOB_ORDER"
    sed -i 's+\@\@\@ACQUISITION_STATION\@\@\@+EDRS+' "$JOB_ORDER"
    sed -i 's+\@\@\@PROCESSING_STATION\@\@\@+CAPG+'  "$JOB_ORDER"
    sed -i 's+\@\@\@ARCHIVING_CENTER\@\@\@+CAPG+' "$JOB_ORDER"
    sed -i 's+\@\@\@PROCESSING_CENTER\@\@\@+CAPG+' "$JOB_ORDER"
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
    mkdir -p "${TEMP_WORK}"/work
    sed -i 's+\@\@\@WORKING\@\@\@+'"${TEMP_WORK}"/work'+' "$JOB_ORDER"
    mkdir -p "${TEMP_OUT}"/L2_DS
    mkdir -p "${TEMP_OUT}"/L2_TL
    mkdir -p "${TEMP_OUT}"/L2_KPI
    sed -i 's+\@\@\@MSI_L2A_DS\@\@\@+'"${TEMP_OUT}"/L2_DS'+' "$JOB_ORDER"
    sed -i 's+\@\@\@MSI_L2A_TL\@\@\@+'"${TEMP_OUT}"/L2_TL'+' "$JOB_ORDER"
    sed -i 's+\@\@\@KPI_L2A\@\@\@+'"${TEMP_OUT}"/L2_KPI'+' "$JOB_ORDER"

    echo "Start L2" >>"${LOG_FILE}"
    launch_step_python "$CUR_DIR"/WrapperCore/sen2cor_launcher.py --joborder "$JOB_ORDER" --mode DS | tee -a "${LOG_FILE}"
    retVal=$?
    echo "RetVal "$retVal
    handle_return $retVal "$PROD" "${LOG_FILE}" "${ERR_FILE}" "${TEMP_WORK}" "${TEMP_OUT}" "$PROD"
    if [ $retVal -ne 0 ]; then
      echo "FAILED" >"${STATUS_FILE}"
      touch "$(dirname "$PROD")/.error_$(basename "$PROD")_$HOST"
      continue
    fi

    L2A_DS=$(find_one_file "${TEMP_OUT}"/L2_DS/ S2*L2A_DS*tar)
    cp "$L2A_DS" "$(dirname "$PROD")/"

    mv "${TEMP_OUT}"/L2_DS/* "${PRIP_DIR}/"
    mv "${TEMP_OUT}"/L2_KPI/* "${KPI_DIR}/"

    # Gen SIZE and txt files
    find "${PRIP_DIR}/" -name "*.tar" -print0 | while read -rd $'\0' k; do
      echo "Product : $k"
      echo "Product : $k" >>"${LOG_FILE}"
      cp "${CUR_DIR}/Template_SIZE.xml" "${KPI_DIR}/$(basename "$k" | sed 's+.tar++')_SIZE.xml"
      sed -i 's+<Product name="NAME">+<Product name="'"$(basename "$k")"'">+' "${KPI_DIR}/$(basename "$k" | sed 's+.tar++')_SIZE.xml"
      sed -i 's+<Size>SIZE</Size>+<Size>'"$(du -s "$k" | sed 's+[ \t].*++')"'</Size>+' "${KPI_DIR}/$(basename "$k" | sed 's+.tar++')_SIZE.xml"
      echo "$ORIGIN_DATE" > "${PRIP_DIR}/$(basename "$k" | sed 's+.tar++').txt"
    done
    find "${PRIP_DIR}/" -name "*.xml" -print0 | while read -rd $'\0' k; do
      echo "Report : $k"
      echo "Report : $k" >>"${LOG_FILE}"
      #cp ${CUR_DIR}/data/JobOrderTemplates/Template_SIZE.xml ${KPI_DIR}/$(basename $k | sed 's+.xml++')_SIZE.xml
      #sed -i 's+<Product name="NAME">+<Product name="'$(basename $k)'">+' ${KPI_DIR}/$(basename $k | sed 's+.xml++')_SIZE.xml
      #sed -i 's+<Size>SIZE</Size>+<Size>'$(du -s $k | sed 's+[ \t].*++')'</Size>+' ${KPI_DIR}/$(basename $k | sed 's+.xml++')_SIZE.xml
      echo "$ORIGIN_DATE" > "${PRIP_DIR}/$(basename "$k" | sed 's+.xml++').txt"
      echo 'Validity_Start|UTC='"${SENSING_START}" >>"${PRIP_DIR}/$(basename "$k" | sed 's+.xml++').txt"
      echo 'Validity_Stop|UTC='"${SENSING_STOP}" >>"${PRIP_DIR}/$(basename "$k" | sed 's+.xml++').txt"
    done

    # Push to PRIP/KPI
    if [ -z ${PRIP_PASSWORD+x} ]; then
      echo "PRIP_PASSWORD is not set, not pushing to PRIP"
      echo "PRIP_PASSWORD is not set, not pushing to PRIP" >>"${LOG_FILE}"
    else
      find "${PRIP_DIR}/" -name "*.tar" -print0 | while read -rd $'\0' j; do
        echo "Pushing $(dirname "$j")/$(basename "$j" | sed 's+.tar++').txt to PRIP"
        echo "Pushing $(dirname "$j")/$(basename "$j" | sed 's+.tar++').txt to PRIP" >>"${LOG_FILE}"
        send_to_prip "$(dirname "$j")/$(basename "$j" | sed 's+.tar++').txt"
        retVal=$?
        if [ $retVal -ne 0 ]; then
          echo "Error pushing $j txt"
          echo "Error pushing $j txt" >>"${LOG_FILE}"
          echo "Error pushing $j txt" >>"${ERR_FILE}"
          echo "Pushed $j txt to PRIP : NK" >>"${PRIP_FILE}"
        else
          echo "Pushing $j txt is OK"
          echo "Pushing $j txt is OK" >>"${LOG_FILE}"
          echo "Pushed $j txt to PRIP : OK" >>"${PRIP_FILE}"
          rm -f "$(dirname "$j")/$(basename "$j" | sed 's+.tar++').txt"
          echo "Pushing $j to PRIP"
          echo "Pushing $j to PRIP" >>"${LOG_FILE}"
          send_to_prip "$j"
          retVal=$?
          if [ $retVal -ne 0 ]; then
            echo "Error pushing $j"
            echo "Error pushing $j" >>"${LOG_FILE}"
            echo "Error pushing $j" >>"${ERR_FILE}"
            echo "Pushed $j to PRIP : NK" >>"${PRIP_FILE}"
          else
            echo "Pushing $j is OK"
            echo "Pushing $j is OK" >>"${LOG_FILE}"
            echo "Pushed $j to PRIP : OK" >> "${PRIP_FILE}"
            rm -f "$j"
          fi
        fi
      done
      find "${PRIP_DIR}/" -name "*.xml" -print0 | while read -rd $'\0' j; do
        echo "Pushing $(dirname "$j")/$(basename "$j" | sed 's+.xml++').txt to PRIP"
        echo "Pushing $(dirname "$j")/$(basename "$j" | sed 's+.xml++').txt to PRIP" >>"${LOG_FILE}"
        send_to_prip "$(dirname "$j")/$(basename "$j" | sed 's+.xml++').txt"
        retVal=$?
        if [ $retVal -ne 0 ]; then
          echo "Error pushing $j txt"
          echo "Error pushing $j txt" >>"${LOG_FILE}"
          echo "Error pushing $j txt" >>"${ERR_FILE}"
          echo "Pushed $j txt to PRIP : NK" >>"${PRIP_FILE}"
        else
          echo "Pushing $j txt is OK"
          echo "Pushing $j txt is OK" >>"${LOG_FILE}"
          echo "Pushed $j txt to PRIP : OK" >>"${PRIP_FILE}"
          rm -f "$(dirname "$j")/$(basename "$j" | sed 's+.xml++').txt"
          echo "Pushing $j to PRIP"
          echo "Pushing $j to PRIP" >>"${LOG_FILE}"
          send_to_prip "$j"
          retVal=$?
          if [ $retVal -ne 0 ]; then
            echo "Error pushing $j"
            echo "Error pushing $j" >>"${LOG_FILE}"
            echo "Error pushing $j" >>"${ERR_FILE}"
            echo "Pushed $j to PRIP : NK" >>"${PRIP_FILE}"
          else
            echo "Pushing $j is OK"
            echo "Pushing $j is OK" >>"${LOG_FILE}"
            echo "Pushed $j to PRIP : OK" >>"${PRIP_FILE}"
            rm -f "$j"
          fi
        fi
      done
    fi

    if [ -z ${KPI_PASSWORD+x} ]; then
      echo "KPI_PASSWORD is not set, not pushing to KPI"
      echo "KPI_PASSWORD is not set, not pushing to KPI" >>"${LOG_FILE}"
    else
      for j in "${KPI_DIR}"/*SIZE.xml; do
        echo "Pushing $j to KPI"
        echo "Pushing $j to KPI" >>"${LOG_FILE}"
        send_to_kpi "$j" files/INBOX/PRODUCTION/SIZE
        retVal=$?
        if [ $retVal -ne 0 ]; then
          echo "Error pushing KPI $j !!!!"
          echo "Error pushing KPI $j" >>"${LOG_FILE}"
          echo "Error pushing KPI $j" >>"${ERR_FILE}"
          echo "Pushed $j to KPI : NK" >>"${KPI_FILE}"
        else
          echo "Pushing $j to KPI is OK"
          echo "Pushing $j to KPI is OK" >>"${LOG_FILE}"
          echo "Pushed $j to KPI : OK" >>"${KPI_FILE}"
          rm -f "$j"
        fi
      done

      for j in "${KPI_DIR}"/*PROBA2*; do
        echo "Pushing $j to KPI"
        echo "Pushing $j to KPI" >>"${LOG_FILE}"
        send_to_kpi "$j" "files/INBOX/PRODUCTION/PROBAS/REFERENCE"
        retVal=$?
        if [ $retVal -ne 0 ]; then
          echo "Error pushing $j !!!!"
          echo "Error pushing $j" >>"${LOG_FILE}"
          echo "Error pushing $j" >>"${ERR_FILE}"
          echo "Pushed $j to KPI : NK" >>"${KPI_FILE}"
        else
          echo "Pushing $j to KPI is OK"
          echo "Pushing $j to KPI is OK" >>"${LOG_FILE}"
          echo "Pushed $j to KPI : OK" >>"${KPI_FILE}"
          rm -f "$j"
        fi
      done

      for j in "${KPI_DIR}"/*MTD_DS.xml; do
        echo "Pushing $j to KPI"
        echo "Pushing $j to KPI" >>"${LOG_FILE}"
        send_to_kpi "$j" files/INBOX/PRODUCTION/MTD
        retVal=$?
        if [ $retVal -ne 0 ]; then
          echo "Error pushing $j !!!!"
          echo "Error pushing $j" >>"${LOG_FILE}"
          echo "Error pushing $j" >>"${ERR_FILE}"
          echo "Pushed $j to KPI : NK" >>"${KPI_FILE}"
        else
          echo "Pushing $j to KPI is OK"
          echo "Pushing $j to KPI is OK" >>"${LOG_FILE}"
          echo "Pushed $j to KPI : OK" >>"${KPI_FILE}"
          rm -f "$j"
        fi
      done

      for j in "${KPI_DIR}"/*idp_infos.xml; do
        echo "Pushing $j to KPI"
        echo "Pushing $j to KPI" >>"${LOG_FILE}"
        send_to_kpi "$j" files/INBOX/PRODUCTION/IDP_INFOS
        retVal=$?
        if [ $retVal -ne 0 ]; then
          echo "Error pushing $j !!!!"
          echo "Error pushing $j" >>"${LOG_FILE}"
          echo "Error pushing $j " >>"${ERR_FILE}"
          echo "Pushed $j to KPI : NK" >>"${KPI_FILE}"
        else
          echo "Pushing $j to KPI is OK"
          echo "Pushing $j to KPI is OK" >>"${LOG_FILE}"
          echo "Pushed $j to KPI : OK" >>"${KPI_FILE}"
          rm -f "$j"
        fi
      done
    fi
    echo "Success treating $PROD"
    echo "Success treating $PROD" >>"${LOG_FILE}"
    echo "Success treating $PROD" >>"${ERR_FILE}"

    remove_work "${TEMP_OUT}"
    remove_work "${TEMP_WORK}"
    rm "$(dirname "$PROD")/.lock_$(basename "$PROD")"
    rm "$(dirname "$PROD")/.lock_$(basename "$PROD")_$HOST"
    rm -f /core.*
    touch "$(dirname "$PROD")/.treated_$(basename "$PROD")"
    touch "$(dirname "$PROD")/.treated_$(basename "$PROD")_$HOST"
    echo "PASSED" >"${STATUS_FILE}"
  done <"${WORK_DIR}/ListDSRandom.txt"
  echo "Finished loop on products"
  sleep "$TIME_SLEEP_LOOP"
done
