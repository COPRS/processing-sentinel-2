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
export SCENARIO=scenarioS2B
export WP_APP_DIR="${WORKPLANS_HOME}/${SCENARIO}/app_data"

# ---------------------
# workplan creation (to do only one time)
cd ${L0_PACKAGE_HOME}/tools
python create_workplan_directories.py --path ${WORKPLANS_HOME} --wp_name ${SCENARIO}

# ---------------------
# job orders preparation
cd ${L0_PACKAGE_HOME}/tools

###################### EISP execution part
# copy 5005_S2B.tgz from ${CADU_DATA_HOME} to ${WORKPLANS_HOME}/${SCENARIO}/steps_data/CADU
# untar using the following command
cd ${CADU_DATA_HOME}
cp 5005_S2B.tgz {WORKPLANS_HOME}/${SCENARIO}/steps_data/CADU
cd ${WORKPLANS_HOME}/${SCENARIO}/steps_data/CADU
tar xvf 5005_S2B.tgz

# content
#S2B_OPER_STO_ISP__S_SGS__20160607T020859_V20160607T020853_20160607T021100_C2_S001.DBL
#S2B_OPER_STO_ISP__S_SGS__20160607T020900_V20160607T020853_20160607T021040_C1_S001.DBL
#S2B_OPER_STO_VCDU_S_SGS__20170407T042127_V20170407T042121_20170407T042123_C1_S001.DBL


###################### INV-TELEM-MERGE execution part ##########################
cd ${L0_PACKAGE_HOME}/tools
# TODO
