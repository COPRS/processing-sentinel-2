###################### L0C execution scenario ##########################
#------------- CUSTOM ENV VARIABLE setting (to do only one time)
/dpc/app/cots/kakadu/kakadu/scripts/setenv_kakadu_cots.sh

#------------- WORKSPACE AND JOB ORDERS PREPARATION
export L0U_PACKAGE_HOME=/usr/local/components/facilities/DPC-CORE-l0pack-ing-typ/DPC-CORE-l0pack-ing-typ-2.3.6/
export L0U_DATA_HOME=/home/$USER/S2-L0u-DATA/cadu_data
export EISP_DATA_HOME=/home/$USER/S2-L0u-DATA/raw_data
export L0_PACKAGE_HOME=/home/$USER/S2-L0-PACKAGING
export AUX_DATA_HOME=/home/$USER/S2-L0-DATA/aux_data
export TPL_REP=${L0_PACKAGE_HOME}/inputs/jobOrdersTemplates/
export INFO_REP=${L0_PACKAGE_HOME}/inputs/jobOrdersExamples/
export WORKPLANS_HOME=/home/$USER/workplans/

# ----- configure Validation scenario to run
export SCENARIO=scenario1
export WP_APP_DIR="${WORKPLANS_HOME}/${SCENARIO}/app_data"

#------------- WORKSPACE CREATION AND INPUT DATA PREPARATION (to do only one time)
# workplan creation 
cd ${L0_PACKAGE_HOME}/tools
python create_workplan_directories.py --path ${WORKPLANS_HOME} --wp_name ${SCENARIO}

# DEM links settings
cd ${L0_PACKAGE_HOME}/scripts
./prepareInputData.bash ${WORKPLANS_HOME} ${SCENARIO}

# fill workplan with aux data (GIPP) defined in jobInfos
cd ${L0_PACKAGE_HOME}/tools
python fill_workplan_directories.py --path ${WORKPLANS_HOME} --wp_name ${SCENARIO} --job_infos ${INFO_REP}/${SCENARIO} --data_repo ${AUX_DATA_HOME}


#------------- JOB INFO DT to use CONFIGURATION (to do only one time)
# goto ${WORKPLANS_HOME}/${SCENARIO}/steps_data/L0U_DUMP/
# choose one of the DT directory not empty (e.g DT81). We will call this value <DTvalue> 

# On the GLOBAL_infos.yml file present into directory ${INFO_REP}/${SCENARIO}/ 
# replace the value at the right of @dt_number@ by the choosen <DTvalue>.
# if not correct, replace also product_id and output_name

#------------- GSE processing (optional)
# choose one of the DT directory not empty (e.g DT81). 
We will call this value <DTvalue> and datastrip directory into it <DSDirName> 

# rename the datastrip directory to process by adding the extension .SAFE
# example:
# cd ${WORKPLANS_HOME}/${SCENARIO}/steps_data/L0U_DUMP/DT81/DS
# mv S2A_OPER_MSI_L0U_DS_SGS__20200527T085028_S20160607T003621_N00.00 S2A_OPER_MSI_L0U_DS_SGS__20200527T085028_S20160607T003621_N00.00.SAFE

# replace <DTvalue> and <DSDirName> by choosen Data strip in the following command template then run it
# python /usr/local/components/GSE-2.5/scripts/improved_gse_patch.py -s ${WORKPLANS_HOME}/${SCENARIO}/steps_data/L0U_DUMP/<DTvalue>/DS/<DSDirName>.SAFE -b ${WORKPLANS_HOME}/${SCENARIO}/steps_data/GSE_SAD_BACKUP -u -0.1 -r 0.0 

# rename the datastrip directory processed by removing the extension .SAFE
# example:
# cd ${WORKPLANS_HOME}/${SCENARIO}/steps_data/L0U_DUMP/DT81/DS
# mv S2A_OPER_MSI_L0U_DS_SGS__20200527T085028_S20160607T003621_N00.00.SAFE S2A_OPER_MSI_L0U_DS_SGS__20200527T085028_S20160607T003621_N00.00

#------------- INITIAL L0c JOB ORDER PREPARATION AND EXECUTION
cd ${L0_PACKAGE_HOME}/tools

# INIT_LOC_L0
python create_job_orders.py --path ${WORKPLANS_HOME} --wp_name ${SCENARIO} --template ${TPL_REP}/job_order_template_INIT_LOC_L0.xml --job_infos ${INFO_REP}/${SCENARIO}/INIT_LOC_L0_infos.yml

# FORMAT_ISP
python create_job_orders.py --path ${WORKPLANS_HOME}  --wp_name ${SCENARIO} --template ${TPL_REP}/job_order_template_FORMAT_ISP.xml --job_infos ${INFO_REP}/${SCENARIO}/FORMAT_ISP_infos.yml

#------------- RUNNING INIT_LOC_L0 and FORMAT_ISP jobs
cd ${L0_PACKAGE_HOME}/scripts
./run_job_orders.bash ${WORKPLANS_HOME} ${SCENARIO} INIT_LOC_L0 1 1
./run_job_orders.bash ${WORKPLANS_HOME} ${SCENARIO} FORMAT_ISP 1 12

# Now the L0c data strip and granule names are available for other job order preparation and execution

#------------- OTHER L0c JOB ORDERS PREPARATION
cd ${L0_PACKAGE_HOME}/tools

# QL_GEO 
python create_job_orders.py --path ${WORKPLANS_HOME} --wp_name ${SCENARIO} --template ${TPL_REP}/job_order_template_QL_GEO.xml --job_infos ${INFO_REP}/${SCENARIO}/QL_GEO_infos.yml

# QL_CLOUD_MASK 
python create_job_orders.py --path ${WORKPLANS_HOME} --wp_name ${SCENARIO} --template ${TPL_REP}/job_order_template_QL_CLOUD_MASK.xml --job_infos ${INFO_REP}/${SCENARIO}/QL_CLOUD_MASK_infos.yml

# FORMAT_METADATA_GR_L0C
python create_job_orders.py --path ${WORKPLANS_HOME} --wp_name ${SCENARIO} --template ${TPL_REP}/job_order_template_FORMAT_METADATA_GR_L0C.xml --job_infos ${INFO_REP}/${SCENARIO}/FORMAT_METADATA_GR_L0C_infos.yml

# FORMAT_IMG_QL_L0
python create_job_orders.py --path ${WORKPLANS_HOME} --wp_name ${SCENARIO} --template ${TPL_REP}/job_order_template_FORMAT_IMG_QL_L0.xml --job_infos ${INFO_REP}/${SCENARIO}/FORMAT_IMG_QL_L0_infos.yml

# OLQC-L0cGr
python create_job_orders.py --path ${WORKPLANS_HOME} --wp_name ${SCENARIO} --template ${TPL_REP}/job_order_template_OLQC-L0cGr.xml --job_infos ${INFO_REP}/${SCENARIO}/OLQC-L0cGr_infos.yml

# FORMAT_METADATA_DS_L0C
python create_job_orders.py --path ${WORKPLANS_HOME} --wp_name ${SCENARIO} --template ${TPL_REP}/job_order_template_FORMAT_METADATA_DS_L0C.xml --job_infos ${INFO_REP}/${SCENARIO}/FORMAT_METADATA_DS_L0C_infos.yml

# OLQC-L0cDs
python create_job_orders.py --path ${WORKPLANS_HOME}  --wp_name ${SCENARIO} --template ${TPL_REP}/job_order_template_OLQC-L0cDs.xml --job_infos ${INFO_REP}/${SCENARIO}/OLQC-L0cDs_infos.yml

#------------- OTHER L0c JOB ORDERS EXECUTION
cd ${L0_PACKAGE_HOME}/scripts
time ./run_job_orders.bash ${WORKPLANS_HOME} ${SCENARIO} QL_GEO 1 12 >QL_GEO.txt
time ./run_job_orders.bash ${WORKPLANS_HOME} ${SCENARIO} QL_CLOUD_MASK 1 1 >QL_CLOUD_MASK.txt
time ./run_job_orders.bash ${WORKPLANS_HOME} ${SCENARIO} FORMAT_METADATA_GR_L0C 1 12 >FORMAT_METADATA_GR_L0C.txt
time ./run_job_orders.bash ${WORKPLANS_HOME} ${SCENARIO} FORMAT_IMG_QL_L0 1 7 >FORMAT_IMG_QL_L0.txt
time ./run_job_orders.bash ${WORKPLANS_HOME} ${SCENARIO} OLQC-L0cGr 1 12 >OLQC-L0cGr.txt
time ./run_job_orders.bash ${WORKPLANS_HOME} ${SCENARIO} FORMAT_METADATA_DS_L0C 1 1 >FORMAT_METADATA_DS_L0C.txt
time ./run_job_orders.bash ${WORKPLANS_HOME} ${SCENARIO} OLQC-L0cDs 1 1 >OLQC-L0cDs.txt



