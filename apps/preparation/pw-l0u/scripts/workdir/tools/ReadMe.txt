##########################################################################
usage: create_workplan_directories.py [-h] [-p PATH] [-w WP_NAME]

optional arguments:
  -h, --help            show this help message and exit
  -p PATH, --path PATH  Path to the L0 workplan root directory
  -w WP_NAME, --wp_name WP_NAME
                        Name of the workplan

python create_workplan_directories.py --path /home/user/workplans --wp_name scenario1

##########################################################################
usage: create_job_orders.py [-h] -p PATH -w WP_NAME -t TEMPLATE -j JOB_INFOS

optional arguments:
  -h, --help            show this help message and exit
  -p PATH, --path PATH  Path to the L0 workplan root directory
  -w WP_NAME, --wp_name WP_NAME
                        Name of the workplan
  -t TEMPLATE, --template TEMPLATE
                        Path to the jobOrderTemplate
  -j JOB_INFOS, --job_infos JOB_INFOS
                        Job infos to be used to create job orders

python create_job_orders.py --path /home/user/workplans --wp_name scenario1 --template /home/user/S2-L0-PACKAGING/inputs/jobOrdersTemplates/job_order_template_step_Init_Loc_L0.xml --job_infos /home/user/S2-L0-PACKAGING/inputs/jobOrdersExamples/scenario1/Init_Loc_L0_infos.yml

##########################################################################
usage: fill_workplan_directories.py [-h] -p PATH -w WP_NAME -j JOB_INFOS -d
                                    DATA_REPO

optional arguments:
  -h, --help            show this help message and exit
  -p PATH, --path PATH  Path to the L0 workplan root directory
  -w WP_NAME, --wp_name WP_NAME
                        Name of the workplan
  -j JOB_INFOS, --job_infos JOB_INFOS
                        Path to the job info dir
  -d DATA_REPO, --data_repo DATA_REPO
                        Path to data repository root directory


python fill_workplan_directories.py --path /home/user/workplans --wp_name scenario1 --job_infos /home/user/S2-L0-PACKAGING/inputs/jobOrdersExamples/scenario1 --data_repo /home/user/S2-L0-DATA/aux_data
 






