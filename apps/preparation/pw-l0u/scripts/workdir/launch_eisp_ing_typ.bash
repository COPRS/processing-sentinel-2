#!/bin/bash
set -o nounset

if [ -z "$1" ] ; then
    echo "missing job_order as argument"
    exit 0
fi


######################################
# Analysis of job_order
######################################
JOB_ORDER=$1

# Get the inputs
FILE_TYPES=`xml_grep --cond '/Ipf_Job_Order/List_of_Ipf_Procs/Ipf_Proc/List_of_Inputs/Input/File_Type' --text_only ${JOB_ORDER}`
INPUTS=`xml_grep --cond '/Ipf_Job_Order/List_of_Ipf_Procs/Ipf_Proc/List_of_Inputs/Input/List_of_File_Names/File_Name' --text_only ${JOB_ORDER}`
FT1=`echo $FILE_TYPES | sed -r 's/ /;/g'`
INPUTS1=`echo $INPUTS | sed -r 's/ /;/g'`

declare -A arr
IFS=';' read -ra FILE_NAMES <<< "$INPUTS1"
IFS=';' read -ra FTYPES <<< "$FT1"
len=${#FILE_NAMES[@]}

for (( i=0; i<$len; i++ ))
do
   arr[${FTYPES[$i]}]=${FILE_NAMES[$i]}
done

CADU_DIR=${arr["STO_CADU_S"]}
WORKING_DIR=${arr["WORKING"]}

echo "Orbit dir $CADU_DIR"
echo "working dir $WORKING_DIR"

# Get the outputs
FILE_OUT=`xml_grep --cond '/Ipf_Job_Order/List_of_Ipf_Procs/Ipf_Proc/List_of_Outputs/Output/File_Type' --text_only ${JOB_ORDER}`
OUTPUTS=`xml_grep --cond '/Ipf_Job_Order/List_of_Ipf_Procs/Ipf_Proc/List_of_Outputs/Output/File_Name' --text_only ${JOB_ORDER}`
FO1=`echo $FILE_OUT | sed -r 's/ /;/g'`
OUTPUTS1=`echo $OUTPUTS | sed -r 's/ /;/g'`

declare -A arrOut
IFS=';' read -ra FILE_NAMES_OUT <<< "$OUTPUTS1"
IFS=';' read -ra FTYOUT <<< "$FO1"
lenout=${#FILE_NAMES_OUT[@]}

for (( i=0; i<$lenout; i++ ))
do
   arrOut[${FTYOUT[$i]}]=${FILE_NAMES_OUT[$i]}
done

HKTM_FOLDER=${arrOut["HKTM_FOLDER"]}
SAD_FOLDER=${arrOut["SAD_FOLDER"]}
DS_GR_FOLDER=${arrOut["DS_GR_FOLDER"]}

echo "outputs dirs : $HKTM_FOLDER $SAD_FOLDER $DS_GR_FOLDER"

# Get the parameters for the telemetry
PARAM_NAMES=`xml_grep --cond '/Ipf_Job_Order/Ipf_Conf/Dynamic_Processing_Parameters/Processing_Parameter/Name' --text_only ${JOB_ORDER}`
PARAM_VALUES=`xml_grep --cond '/Ipf_Job_Order/Ipf_Conf/Dynamic_Processing_Parameters/Processing_Parameter/Value' --text_only ${JOB_ORDER}`
PNMES1=`echo $PARAM_NAMES | sed -r 's/ /;/g'`
PVALUES1=`echo $PARAM_VALUES | sed -r 's/ /;/g'`

declare -A arrParam
IFS=';' read -ra P_NAMES <<< "$PNMES1"
IFS=';' read -ra P_VALUES <<< "$PVALUES1"
len2=${#P_NAMES[@]}

for (( i=0; i<$len2; i++ ))
do
   arrParam[${P_NAMES[$i]}]=${P_VALUES[$i]}
done

# Parameters for EISP
SATELLITE=${arrParam["MissionID"]}
DOWNLINK_TIME=${arrParam["DownlinkTime"]}
ACQUISITION_START=${arrParam["AcquisitionStart"]}
PROCESSING_STATION=${arrParam["Processing_Station"]}
ACQUISITION_STATION=${arrParam["Acquisition_Station"]}
CADUAnnot=${arrParam["CADUAnnotations"]}

# Parameters for telemetry processing
TH_LINE_PER=${arrParam["theoretical_line_period"]}
QL_BANDS=`echo ${arrParam["ql_bands"]} | sed -r 's/,/ /g'`
MIN_PACKET_SIZE=${arrParam["minimum_packet_length"]}
GPS_TIME=${arrParam["gps_to_tai"]}
TAI_TIME=${arrParam["tai_to_utc"]}
ORBIT_OFFSET=${arrParam["orbit_offset"]}
CYCLIC_ORB_OFFSET=${arrParam["cyclic_orbit_offset"]}

echo "parameters : $TH_LINE_PER $QL_BANDS $MIN_PACKET_SIZE $GPS_TIME $TAI_TIME"
datems=$(($(date +%s%N)/1000000 + 1000000))


# Check the orbit folder for raw data
RAW_C1_DATA=`ls ${CADU_DIR}/ch_1/*DSDB*raw`
RAW_C2_DATA=`ls ${CADU_DIR}/ch_2/*DSDB*raw`

if [[ $RAW_C1_DATA == "" ]]; then
  echo "No raw data found for ch_1 found in $CADU_DIR"
  exit 1
fi

if [[ $RAW_C2_DATA == "" ]]; then
  echo "No raw data found for ch_2 found in $CADU_DIR"
  exit 1
fi

fileListC1=""
fileCountC1=0
for RAWfile in $RAW_C1_DATA; do
  fileListC1="${fileListC1}                        <File_Name>${RAWfile}</File_Name>\n"
  let fileCountC1++
done

fileListC2=""
fileCountC2=0
for RAWfile in $RAW_C2_DATA; do
  fileListC2="${fileListC2}                        <File_Name>${RAWfile}</File_Name>\n"
  let fileCountC2++
done

########################################
# Generation on job order file for EISP
########################################

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd ${DIR}/../conf/
cp job_order_C1_EISP.template job_order_C1_EISP.xml
cp job_order_C2_EISP.template job_order_C2_EISP.xml

sed -i "s#MPL_ORBSCT_Reference#${SATELLITE}_MPL_ORBSCT_Reference#g" *xml
sed -i "s#@SATELLITE@#${SATELLITE}#g" *xml
sed -i "s#@DOWNLINK_TIME@#${DOWNLINK_TIME}#g" *xml
sed -i "s#@ACQ_START@#${ACQUISITION_START}#g" *xml
sed -i "s#@PROC_STATION@#${PROCESSING_STATION}#g" *xml
sed -i "s#@ACQ_STATION@#${ACQUISITION_STATION}#g" *xml
sed -i "s#@ANNOTATIONS@#${CADUAnnot}#g" *xml
sed -i "s#@COUNT_FILE@#${fileCountC1}#g" job_order_C1_EISP.xml
sed -i "s#@COUNT_FILE@#${fileCountC2}#g" job_order_C2_EISP.xml
sed -i "s#@FILES@#${fileListC1}#g" job_order_C1_EISP.xml
sed -i "s#@FILES@#${fileListC2}#g" job_order_C2_EISP.xml

# Get the orbit
/usr/local/components/EISP/bin/EISPProcTime2Orbit job_order_C1_EISP.xml
if [ -f "DownlinkOrbit.txt" ]
then
    ORBIT_NUMBER=$(cat DownlinkOrbit.txt | head -1)
    echo "Orbit found is $ORBIT_NUMBER"
else
    echo "No orbit found"
    exit 1
fi

#################################################
# Generation on temp command files for telemetry
#################################################

# Create working folder for command files for telemetry
WP_DIR=${WORKING_DIR}/scenario/app_data
mkdir -p ${WP_DIR}/step_MSI_AnaTM/launch_1/
mkdir -p ${WP_DIR}/step_MSI_AnaTM/launch_2/
mkdir -p ${WP_DIR}/step_SAD_AnaTM/launch_1/
mkdir -p ${WP_DIR}/step_SAD_AnaTM/launch_2/
mkdir -p ${WP_DIR}/step_MSI_SAD_Merge/launch_1/
mkdir -p ${WP_DIR}/step_MSI_SAD_Merge/launch_2/

# Prepare command files for telemetry
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd ${DIR}/../../telemetry/conf
cp job_order_l0c_scenario_1_MSI_AnaTM.template job_order_l0c_scenario_1_MSI_AnaTM.xml
cp job_order_l0c_scenario_1_MSI_SAD_Merge.template job_order_l0c_scenario_1_MSI_SAD_Merge.xml
cp job_order_l0c_scenario_1_SAD_AnaTM.template job_order_l0c_scenario_1_SAD_AnaTM.xml
cp job_order_l0c_scenario_2_MSI_AnaTM.template job_order_l0c_scenario_2_MSI_AnaTM.xml
cp job_order_l0c_scenario_2_MSI_SAD_Merge.template job_order_l0c_scenario_2_MSI_SAD_Merge.xml
cp job_order_l0c_scenario_2_SAD_AnaTM.template job_order_l0c_scenario_2_SAD_AnaTM.xml
cp TYP_config_parameters.template TYP_config_parameters.xml

sed -i "s#@WORKING_FOLDER@#$WORKING_DIR#g" *xml
sed -i "s#@MERGE_OUT@#$DS_GR_FOLDER#g" *xml
sed -i "s#@SAD_OUTPUT@#$SAD_FOLDER#g" *xml
sed -i "s/@SATELLITE@/${SATELLITE}/g" *xml
sed -i "s/@ACQ_DATE@/${ACQUISITION_START}/g" *xml
sed -i "s/@ACQ_STATION@/${ACQUISITION_STATION}/g" *xml
sed -i "s/@VALIDITY_DATE@/${datems}/g" *xml
sed -i "s/@TH_LINE_PERIOD@/${TH_LINE_PER}/g" *xml
sed -i "s/@QL_BANDS@/${QL_BANDS}/g" *xml
sed -i "s/@MIN_PACK_LENGTH@/${MIN_PACKET_SIZE}/g" *xml
sed -i "s/@GPC_TO_TAI@/${GPS_TIME}/g" *xml
sed -i "s/@TAI_TO_UTC@/${TAI_TIME}/g" *xml
sed -i "s/@ORBIT_NUMBER@/${ORBIT_NUMBER}/g" *xml
sed -i "s/@ORBIT_OFFSET@/${ORBIT_OFFSET}/g" *xml
sed -i "s/@CYCLIC_ORBIT_OFFSET@/${CYCLIC_ORB_OFFSET}/g" *xml

# Change port for nominal mode
sed -i "s/<SOCKET_PORT>35141/<SOCKET_PORT>35101/g" *xml
sed -i "s/<SOCKET_PORT>35142/<SOCKET_PORT>35102/g" *xml
sed -i "s/<SOCKET_PORT>35151/<SOCKET_PORT>35113/g" *xml
sed -i "s/<SOCKET_PORT>35152/<SOCKET_PORT>35116/g" *xml

mv job_order_l0c_scenario_1_MSI_AnaTM.xml ${WP_DIR}/step_MSI_AnaTM/launch_1/
mv job_order_l0c_scenario_1_SAD_AnaTM.xml ${WP_DIR}/step_SAD_AnaTM/launch_1/
mv job_order_l0c_scenario_2_MSI_AnaTM.xml ${WP_DIR}/step_MSI_AnaTM/launch_2/
mv job_order_l0c_scenario_2_SAD_AnaTM.xml ${WP_DIR}/step_SAD_AnaTM/launch_2/
mv job_order_l0c_scenario_1_MSI_SAD_Merge.xml ${WP_DIR}/step_MSI_SAD_Merge/launch_1/
mv job_order_l0c_scenario_2_MSI_SAD_Merge.xml ${WP_DIR}/step_MSI_SAD_Merge/launch_2/
cp TYP_config_parameters.xml ${WP_DIR}/step_MSI_AnaTM/launch_1/
cp TYP_config_parameters.xml ${WP_DIR}/step_MSI_AnaTM/launch_2/
cp TYP_config_parameters.xml ${WP_DIR}/step_SAD_AnaTM/launch_1/
cp TYP_config_parameters.xml ${WP_DIR}/step_SAD_AnaTM/launch_2/
cp TYP_config_parameters.xml ${WP_DIR}/step_MSI_SAD_Merge/launch_1/
cp TYP_config_parameters.xml ${WP_DIR}/step_MSI_SAD_Merge/launch_2/


##########################################
# Launch EISP, ingestion and telemetry analysis
##########################################

PIDS=""
FAIL=""

cd ${DIR}/..
export LAUNCH_DIR=`pwd`
export CHAIN_HOME=$LAUNCH_DIR
export CADU_REPO=/home/user/S2-L0u-DATA/cadu_data

echo "LAUNCH_DIR=$LAUNCH_DIR launch ingestion with socket mode"
java -Dlog4j.configuration=./conf/log4j.xml -Xmx6g -cp ${LAUNCH_DIR}/lib/*:${LAUNCH_DIR}/lib/dpc-ingestion.jar s2.pdgs.dpc.ingestion.standalone.IngestionLauncherSocket > ${WP_DIR}/traces_ing.log &
PIDIng=$!

sleep 10
mkdir -p ${WP_DIR}/EISP_DIR/ch_1
mkdir -p ${WP_DIR}/EISP_DIR/ch_2
cd ${WP_DIR}/EISP_DIR/ch_1
${DIR}/../scripts/launch_eisp.bash ${DIR}/../conf/job_order_C1_EISP.xml ${WP_DIR}/EISP_DIR/ch_1 & 
PIDEisp1=$!

cd ${WP_DIR}/EISP_DIR/ch_2
${DIR}/../scripts/launch_eisp.bash ${DIR}/../conf/job_order_C2_EISP.xml ${WP_DIR}/EISP_DIR/ch_2 & 
PIDEisp2=$!

sleep 35
cd ${DIR}/../../telemetry/
echo "./scripts/launch_telemetry.bash $WORKING_DIR scenario"
./scripts/launch_telemetry.bash $WORKING_DIR "scenario" &
PIDTyp=$!

wait $PIDTyp
CR=$?

wait $PIDEisp1 $PIDEisp2

kill -9 $PIDIng &> /dev/null
wait $PIDIng  2> /dev/null

if [ $CR == 0 ];
then
    echo "Ingestion and analysis ended sucessfully"
else
    echo "Ingestion or analysis failed : $CR"
    exit -1
fi

if [[ -d ${DIR}/../conf/DATA/archive/ && "$(ls -A ${DIR}/../conf/DATA/archive/)" ]]
then
	cp -rf ${DIR}/../conf/DATA/archive/* $HKTM_FOLDER
fi

#####################################
# Launch telemetry merge
#####################################
echo "./scripts/launch_merge.bash $WORKING_DIR scenario"
./scripts/launch_merge.bash $WORKING_DIR "scenario"
echo "Telemetry merge ended"

