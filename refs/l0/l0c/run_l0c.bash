# Define task/step to be launched
TASK="INIT_L0C_L0" ;
TASK_LOWER_CASE="$(echo ${TASK} | awk '{print tolower($0)}')" ;

# Extract DT number from L0u path (used only in path?)
DT_NUMBER=35 ;
L0U_DS_NAME="S2B_OPER_MSI_L0U_DS_SGS__20220422T185216_S20220413T114741_N00.00" ;

# Extract start/stop time from datastrip (not datatake!)
START_TIME_DS=$(awk -F '[<>]' '/DATASTRIP_SENSING_START/{print $3}' /data/S2L0IPF/L0U_DUMP/DT${DT_NUMBER}/DS/${L0U_DS_NAME}/S2B_OPER_MTD_L0U_DS_*.xml) ;
STOP_TIME_DS=$(awk -F '[<>]' '/DATASTRIP_SENSING_STOP/{print $3}' /data/S2L0IPF/L0U_DUMP/DT${DT_NUMBER}/DS/${L0U_DS_NAME}/S2B_OPER_MTD_L0U_DS_*.xml) ;

# Init parallel processing to false by default
PARALLEL_DETECTOR="false" ;
PARALLEL_BAND="false" ;

sed 's#<Version>2.3.6</Version>#<Version>3.0.3</Version>#g' -i job_order_template_${TASK}.xml ;

# Date de génération des JobOrders
### replace @creationDate@
sed 's#2020-04-20T20:58:28.099Z#2022-04-26T11:50:29.024Z#g' -i job_order_template_${TASK}.xml ;

sed 's#@gps_utc@#-18#g' -i job_order_template_${TASK}.xml ;

CMD_SED="sed 's#@start_time@#${START_TIME_DS}#g' -i job_order_template_${TASK}.xml" ;
eval $CMD_SED ;
CMD_SED="sed 's#@stop_time@#${STOP_TIME_DS}#g' -i job_order_template_${TASK}.xml" ;
eval $CMD_SED ;

# Some JobOrders are generated to be run in parallel depending of the detector ID or the Band number
if [ ${TASK} == "FORMAT_ISP" ] ; then
  TASK_VERSION=$(ls /dpc/app/s2ipf/${TASK}) ;
  PARALLEL_DETECTOR="true" ;
fi ;
if [ ${TASK} == "QL_GEO" ] ; then
  TASK_VERSION=$(ls /dpc/app/s2ipf/${TASK}) ;
  PARALLEL_DETECTOR="true" ;
fi ;
if [ ${TASK} == "QL_CLOUD_MASK" ] ; then
  TASK_VERSION=$(ls /dpc/app/s2ipf/${TASK}) ;
fi ;
if [ ${TASK} == "FORMAT_METADATA_GR_L0C" ] ; then
  TASK_VERSION=$(ls /dpc/app/s2ipf/${TASK}) ;
  PARALLEL_DETECTOR="true" ;
fi ;
if [ ${TASK} == "FORMAT_IMG_QL_L0" ] ; then
  TASK_VERSION=$(ls /dpc/app/s2ipf/${TASK}) ;
  PARALLEL_BAND="true" ;
fi ;
if [ ${TASK} == "OLQC-L0cGr" ] ; then
  TASK_VERSION=$(ls /dpc/app/s2ipf/OLQC) ;
  PARALLEL_DETECTOR="true" ;
fi ;
if [ ${TASK} == "OLQC-L0cDs" ]; then
  TASK_VERSION=$(ls /dpc/app/s2ipf/OLQC) ;
fi ;


CMD_SED="sed 's#<Task_Version>02.16.00</Task_Version>#<Task_Version>${TASK_VERSION}</Task_Version>#g' -i job_order_template_${TASK}.xml" ;
eval $CMD_SED ;
CMD_SED="sed 's#<Task_Version>01.06.04</Task_Version>#<Task_Version>${TASK_VERSION}</Task_Version>#g' -i job_order_template_${TASK}.xml" ;
eval $CMD_SED ;
sed 's#@wpdir@/steps_data#/data/S2L0IPF#g' -i job_order_template_${TASK}.xml ;
CMD_SED="sed 's#@dt_number@#DT${DT_NUMBER}#g' -i job_order_template_${TASK}.xml" ;
eval $CMD_SED ;
CMD_SED="sed 's#@l0u_dsname@#${L0U_DS_NAME}#g' -i job_order_template_${TASK}.xml" ;
eval $CMD_SED ;


# Retrieve L0c DS name for PART2
L0_DS_NAME="$(ls DS/S2B_OPER_MSI_L0__DS* -d | awk -F/ '{print $NF}')" ;
CMD_SED="sed 's#@l0_dsname@#${L0_DS_NAME}#g' -i job_order_template_${TASK}.xml" ;
eval $CMD_SED ;


sed 's#@gipp_lrextr@#S2B_OPER_GIP_LREXTR_MPC__20210608T000001_V20150622T000000_21000101T000000_B00.xml#g' -i job_order_template_${TASK}.xml ;
sed 's#@gipp_invloc@#S2B_OPER_GIP_INVLOC_MPC__20170523T080300_V20170322T000000_21000101T000000_B00.xml#g' -i job_order_template_${TASK}.xml ;
sed 's#@gipp_r2abca@#S2B_OPER_GIP_R2ABCA_MPC__20220315T151100_V20220317T000000_21000101T000000_B00.xml#g' -i job_order_template_${TASK}.xml ;
sed 's#@gipp_cloinv@#S2B_OPER_GIP_CLOINV_MPC__20210609T000002_V20210823T030000_21000101T000000_B00.xml#g' -i job_order_template_${TASK}.xml ;
sed 's#@gipp_jp2kpa@#S2B_OPER_GIP_JP2KPA_MPC__20220120T000006_V20220125T022000_21000101T000000_B00.xml#g' -i job_order_template_${TASK}.xml ;
sed 's#@gipp_olqcpa@#S2B_OPER_GIP_OLQCPA_MPC__20220119T000039_V20220125T022000_21000101T000000_B00.zip#g' -i job_order_template_${TASK}.xml ;
sed 's#@gipp_probas@#S2B_OPER_GIP_PROBAS_MPC__20220214T000400_V20220316T030000_21000101T000000_B00.xml#g' -i job_order_template_${TASK}.xml ;
sed 's#@gipp_viedir01@#S2B_OPER_GIP_VIEDIR_MPC__20170512T114736_V20170322T000000_21000101T000000_B01.xml#g' -i job_order_template_${TASK}.xml ;
sed 's#@gipp_viedir02@#S2B_OPER_GIP_VIEDIR_MPC__20170512T114736_V20170322T000000_21000101T000000_B02.xml#g' -i job_order_template_${TASK}.xml ;
sed 's#@gipp_viedir03@#S2B_OPER_GIP_VIEDIR_MPC__20170512T114736_V20170322T000000_21000101T000000_B03.xml#g' -i job_order_template_${TASK}.xml ;
sed 's#@gipp_viedir04@#S2B_OPER_GIP_VIEDIR_MPC__20170512T114736_V20170322T000000_21000101T000000_B04.xml#g' -i job_order_template_${TASK}.xml ;
sed 's#@gipp_viedir05@#S2B_OPER_GIP_VIEDIR_MPC__20170512T114736_V20170322T000000_21000101T000000_B05.xml#g' -i job_order_template_${TASK}.xml ;
sed 's#@gipp_viedir06@#S2B_OPER_GIP_VIEDIR_MPC__20170512T114736_V20170322T000000_21000101T000000_B06.xml#g' -i job_order_template_${TASK}.xml ;
sed 's#@gipp_viedir07@#S2B_OPER_GIP_VIEDIR_MPC__20170512T114736_V20170322T000000_21000101T000000_B07.xml#g' -i job_order_template_${TASK}.xml ;
sed 's#@gipp_viedir08@#S2B_OPER_GIP_VIEDIR_MPC__20170512T114736_V20170322T000000_21000101T000000_B08.xml#g' -i job_order_template_${TASK}.xml ;
sed 's#@gipp_viedir09@#S2B_OPER_GIP_VIEDIR_MPC__20170512T114736_V20170322T000000_21000101T000000_B09.xml#g' -i job_order_template_${TASK}.xml ;
sed 's#@gipp_viedir10@#S2B_OPER_GIP_VIEDIR_MPC__20170512T114736_V20170322T000000_21000101T000000_B10.xml#g' -i job_order_template_${TASK}.xml ;
sed 's#@gipp_viedir11@#S2B_OPER_GIP_VIEDIR_MPC__20170512T114736_V20170322T000000_21000101T000000_B11.xml#g' -i job_order_template_${TASK}.xml ;
sed 's#@gipp_viedir12@#S2B_OPER_GIP_VIEDIR_MPC__20170512T114736_V20170322T000000_21000101T000000_B12.xml#g' -i job_order_template_${TASK}.xml ;
sed 's#@gipp_viedir13@#S2B_OPER_GIP_VIEDIR_MPC__20170512T114736_V20170322T000000_21000101T000000_B8A.xml#g' -i job_order_template_${TASK}.xml ;
sed 's#@gipp_spamod@#S2B_OPER_GIP_SPAMOD_MPC__20220222T000033_V20220224T001500_21000101T000000_B00.xml#g' -i job_order_template_${TASK}.xml ;
sed 's#@gipp_blindp@#S2B_OPER_GIP_BLINDP_MPC__20170221T000000_V20170101T000000_21000101T000000_B00.xml#g' -i job_order_template_${TASK}.xml ;
sed 's#@dem_path@#S2__OPER_DEM_GLOBEF_PDMC_20091210T235100_S20091210T235134.DBL/average/#g' -i job_order_template_${TASK}.xml ;
sed 's#@gipp_datati@#S2B_OPER_GIP_DATATI_MPC__20170428T123038_V20170322T000000_21000101T000000_B00.xml#g' -i job_order_template_${TASK}.xml ;
sed 's#@gipp_atmima@#S2B_OPER_GIP_ATMIMA_MPC__20170206T103051_V20170101T000000_21000101T000000_B00.xml#g' -i job_order_template_${TASK}.xml ;
sed 's#@gipp_atmsad@#S2B_OPER_GIP_ATMSAD_MPC__20170324T155501_V20170306T000000_21000101T000000_B00.xml#g' -i job_order_template_${TASK}.xml ;
sed 's#@wpdir@/app_data/wp_data/file_idp_infos.xml#/data/S2L0IPF/file_idp_infos.xml#g' -i job_order_template_${TASK}.xml ;


# If the parallelisation is made for the detector
# first granule number is 001 and last is the count of all granule from the same detector
if [ ${PARALLEL_DETECTOR} == "true" ] ; then
  for DETECTOR_TMP in $(seq 01 $(ls L0U_DUMP/DT${DT_NUMBER}/GR/DB1/ | awk -F '_' '{print $10}' | sort -u | wc -l));
  do

      DETECTOR=$(printf "%02d" ${DETECTOR_TMP});
      echo "detector : D${DETECTOR}";

      TOTAL_GR_L0U_TMP=$(ls L0U_DUMP/DT${DT_NUMBER}/GR/DB1/S2B_OPER_MSI_L0U_GR_*_D${DETECTOR}_* -d | wc -l) ;
      TOTAL_GR_L0U=$(printf "%03d" ${TOTAL_GR_L0U_TMP}) ;
      echo "total gr l0u : ${TOTAL_GR_L0U}" ;

      cp job_order_template_${TASK}.xml job_order_template_${TASK}_${DETECTOR}.xml ;

      CMD_SED="sed 's#@granule_begin@#001#' -i job_order_template_${TASK}_${DETECTOR}.xml" ;
      eval $CMD_SED ;
      CMD_SED="sed 's#@granule_end@#${TOTAL_GR_L0U}#' -i job_order_template_${TASK}_${DETECTOR}.xml" ;
      eval $CMD_SED ;
      CMD_SED="sed 's#@wpdir@/app_data/step_@jobOrderName@/launch_@jobnumber@/ipf_report_file.xml#/data/S2L0IPF/reports/${TASK_LOWER_CASE}_ipf_report_file_${DETECTOR}.xml#g' -i job_order_template_${TASK}_${DETECTOR}.xml" ;
      eval $CMD_SED ;
      CMD_SED="sed 's#@detector@#${DETECTOR}#' -i job_order_template_${TASK}_${DETECTOR}.xml" ;
      eval $CMD_SED ;


      # put the list of all granules in the joborder from the same detector DXX
      if [ ${TASK} == "OLQC-L0cGr" ] ; then

        TOTAL_GR_L0C=$(ls GR/DB1/S2B_OPER_MSI_L0__GR_*_D${DETECTOR}_* -d | wc -l) ;
        echo "total gr l0c : ${TOTAL_GR_L0C}" ;
        CMD_SED="sed 's#@count_l0gr@#${TOTAL_GR_L0C}#' -i job_order_template_${TASK}_${DETECTOR}.xml" ;
        eval $CMD_SED ;
        LAST_SED="";
        for GR_TMP in $(ls GR/DB1/S2B_OPER_MSI_L0__GR_*_D${DETECTOR}_* -d) ;
        do
          GR=$(echo ${GR_TMP} | awk -F/ '{print $NF}') ;
          if [ -z "${LAST_SED}" ] ; then
            CMD_SED="sed 's#<File_Name>/data/S2L0IPF/GR/DB1/@list_l0gr_items@_${DETECTOR}_@gr_postfix@/</File_Name>#<File_Name>/data/S2L0IPF/GR/DB1/${GR}/</File_Name>#' -i job_order_template_${TASK}_${DETECTOR}.xml" ;
            eval $CMD_SED ;
          else
            CMD_SED="sed '/\/data\/S2L0IPF\/GR\/DB1\/${LAST_SED}\/<\/File_Name>/a <File_Name>\/data\/S2L0IPF\/GR\/DB1\/${GR}\/<\/File_Name>' -i job_order_template_${TASK}_${DETECTOR}.xml" ;
            eval $CMD_SED ;
          fi ;
          LAST_SED=${GR} ;
        done ;
      fi ;



  done ;


# If the parallelisation is made for the band
elif [ ${PARALLEL_BAND} == "true" ] ; then
  BAND_LIST=( B01 B02 B03 B07 B04-B05-B06 B08-B11-B12 B00-B09-B10 )
  for BAND in ${BAND_LIST[@]} ;
  do
    cp job_order_template_${TASK}.xml job_order_template_${TASK}_${BAND}.xml ;
    CMD_SED="sed 's#@detector@#${BAND}#' -i job_order_template_${TASK}_${BAND}.xml" ;
    eval $CMD_SED ;
    CMD_SED="sed 's#@wpdir@/app_data/step_@jobOrderName@/launch_@jobnumber@/ipf_report_file.xml#/data/S2L0IPF/reports/${TASK_LOWER_CASE}_ipf_report_file_${BAND}.xml#g' -i job_order_template_${TASK}_${BAND}.xml" ;
    eval $CMD_SED ;
  done ;
else
  CMD_SED="sed 's#@wpdir@/app_data/step_@jobOrderName@/launch_@jobnumber@/ipf_report_file.xml#/data/S2L0IPF/reports/${TASK_LOWER_CASE}_ipf_report_file.xml#g' -i job_order_template_${TASK}.xml" ;
  eval $CMD_SED ;
fi ;


# Run each script with JO as arg
# /dpc/app/s2ipf/QL_GEO/05.01.00/scripts/<TASK>/scripts/<VERSION>/<TASK>

# - UT1UTC : convert text to xml
# - DBL TGZ = DEM_GLOBEF

# Replace
#@acquisitionStation@ from MDC session info
#@processingStation@ = REFS
#@creationDate@ = now
