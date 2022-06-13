# ing & telemtry scripts are under
# /usr/local/components/facilities/DPC-CORE-l0pack-ing-typ/DPC-CORE-l0pack-ing-typ-2.3.6

###################### EISP-INV-TELEM-MERGE execution scenario ##########################
#------------- WORKSPACE AND JOB ORDERS PREPARATION
export L0U_PACKAGE_HOME=/usr/local/components/facilities/DPC-CORE-l0pack-ing-typ/DPC-CORE-l0pack-ing-typ-2.3.6/
export L0U_DATA_HOME=/home/$USER/S2-L0u-DATA/cadu_data
export EISP_DATA_HOME=/home/$USER/S2-L0u-DATA/raw_data
export L0_PACKAGE_HOME=/home/$USER/S2-L0-PACKAGING
export AUX_DATA_HOME=/home/$USER/S2-L0-DATA/aux_data
export TPL_REP=${L0_PACKAGE_HOME}/inputs/jobOrdersTemplates/
export INFO_REP=${L0_PACKAGE_HOME}/inputs/jobOrdersExamples/
export WORKPLANS_HOME=/home/$USER/workplans/
# ----- configure Validation scenario to RUNNING
export SCENARIO=scenario1
export WP_APP_DIR="${WORKPLANS_HOME}/${SCENARIO}/app_data"

# ---------------------
# workplan creation (to do only one time)
cd ${L0_PACKAGE_HOME}/tools
python create_workplan_directories.py --path ${WORKPLANS_HOME} --wp_name ${SCENARIO}

# ---------------------
# job orders preparation
cd ${L0_PACKAGE_HOME}/tools

###################### EISP execution part
# copy small_data_set_eisp_S2A.tgz from ${EISP_DATA_HOME} to ${WORKPLANS_HOME}/${SCENARIO}/steps_data/RAW
# untar using the following command
cd ${EISP_DATA_HOME}
cp small_data_set_eisp_S2A.tgz {WORKPLANS_HOME}/${SCENARIO}/steps_data/RAW
cd ${WORKPLANS_HOME}/${SCENARIO}/steps_data/RAW
tar xvf small_data_set_eisp_S2A.tgz

# depending on the satellite it could be necessary to change the:
#/usr/local/conf/EISP/EISPProcessor.xml
#/usr/local/conf/EISP/EISPProcessorS1Data.xml

# edit ${INFO_REP}/${SCENARIO}/EISP_infos.yml and update if necessary the values of the following tags
# - eisp_input_folder: /home/user/workplans/<scenario>/steps_data/RAW/<DCSdatName>
#      change <scenario> to the value of ${SCENARIO}
#      change <DCSdatName> to the name of directory name present under RAW
# - downlink_time: to the extracted date value from  directory name present under RAW (e.g 20200526T005608)


cd ${L0_PACKAGE_HOME}/tools
python create_job_orders.py --path ${WORKPLANS_HOME} --wp_name ${SCENARIO} --template ${TPL_REP}/job_order_template_EISP.xml --job_infos ${INFO_REP}/${SCENARIO}/EISP_infos.yml

### run the first EISP jobs for the two channels (EISPProcTime2orbit)
cd ${L0_PACKAGE_HOME}/scripts
./run_job_orders.bash ${WORKPLANS_HOME} ${SCENARIO} EISP-ProcTime2orbit 1 2

# logs of execution and outputs are under ${WORKPLANS_HOME}/${SCENARIO}/steps_data/CADU

### run the second EISP jobs for the two channels (EISPProcessor)
./run_job_orders.bash ${WORKPLANS_HOME} ${SCENARIO} EISP-Processor 1 2


###################### INV-TELEM-MERGE execution part ##########################
cd ${L0_PACKAGE_HOME}/tools


