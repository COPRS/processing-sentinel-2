#!/bin/python
# -*- coding: utf-8 -*-
"""
 Copyright (C) 2021 CSGROUP

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 Author : Esquis Benjamin
"""

import argparse
import datetime
import glob
import json
import logging
import os
import traceback
import random

from log_colorizer import make_colored_stream_handler
import shutil
import Constants
import FileUtils
from OLQC import OLQC
from DatastripReader import DatastripReader
from GSE import GSE
from IdpInfos import IdpInfos
from InventoryL0 import InventoryL0
from InventoryL1A import InventoryL1A
from InventoryL1B import InventoryL1B
from InventoryL1C import InventoryL1C
from OrchestratorLauncher import OrchestratorLauncher
from TaskTableReader import TaskTableReader
from OrchestratorConfig import OrchestratorConfig
from OrchestratorICD import OrchestratorICD
from ContextManager import ContextManager
from FileUtils import create_directory
from OrchestratorPipeline import OrchestratorPipeline
from VersionManager import VersionManager


class LaunchIPF(object):
    """
        Main processing class
    """

    def __init__(self):
        # init logger
        self._main_scheme = None
        self._suffix = None
        self._tile_ident = None
        self._logger = logging.getLogger("IPFLauncher")
        self._handler = make_colored_stream_handler()
        self._handler.setFormatter(Constants.LOG_FORMATTER)
        self._logger.addHandler(self._handler)
        self._logger.setLevel(logging.DEBUG)
        self._port = 6666 + int(random.randint(0, 100))
        self._loglevel = logging.DEBUG
        self._gipp_folder = None
        self._dem_globe_folder = None
        self._dem_srtm_folder = None
        self._ecmwf_folder = None
        self._iers_folder = None
        self._datastrip_input_folder = None
        self._datastrip_sensing_start = None
        self._datastrip_sensing_stop = None
        self._datastrip_sensing_station = None
        self._datastrip_folder = None
        self._datastrip_metadata = None
        self._granule_input_Folder = None
        self._dateoflogs = None
        self._creation_date = None
        self._ipf_working_dir = None
        self._data_working_dir = None
        self._ipf_conf_dir = None
        self._ipf_mode_conf_dir = None
        self._default_config_dir = None
        self._ipf_orch_working_dir = None
        self._resultfile = None
        self._orchlogfile = None
        self._ipf_orch_log_dir = None
        self._output_tasktable_folder = None
        # Parameters
        self._input_product_folder = None
        self._output_folder = None
        self._nb_parallel_tasks = None
        self._mode = None
        self._auxs_folder = None
        self._staticauxs_folder = None
        self._gri_folder = None
        self._working_dir = None
        self._loglevel_str = None
        self._tile = None
        self._mode = None
        self._previous_mode = None
        self._chain_name = None
        self._killtimeout = 14400
        self._gsebackupfolder = None

    def main(self):
        """
            Main function
        """
        parser = argparse.ArgumentParser(description="This script launches the IPF orchestrator",
                                         # main description for help
                                         epilog='launches the IPF orchestrator\n\n',
                                         formatter_class=argparse.RawTextHelpFormatter)  # displayed after help
        parser.add_argument("-m", "--mode",
                            help="mode",
                            required=True)
        parser.add_argument("-a", "--auxs",
                            help="Auxs folder",
                            required=True)
        parser.add_argument("-s", "--staticauxs",
                            help="Statics Auxs folder: DEMs",
                            required=True)
        parser.add_argument("-g", "--gri",
                            help="Statics Auxs folder: GRI",
                            required=True)
        parser.add_argument("-i", "--input",
                            help="Input folder",
                            required=True)
        parser.add_argument("-w", "--working",
                            help="Working folder",
                            required=True)
        parser.add_argument("-o", "--output",
                            help="Output folder",
                            required=True)
        parser.add_argument("-p", "--parallel",
                            help="Number of parallel Tasks",
                            required=True)
        parser.add_argument("-t", "--tile",
                            help="Overload tile Ident: 011-002 for example",
                            required=False)
        parser.add_argument("-l", "--loglevel",
                            help="Log level",
                            default="INFO",
                            required=False)
        parser.add_argument("-e", "--exeversionfile",
                            help="Set a different exe version file than the installed",
                            required=False)
        parser.add_argument("-k", "--killtimeout",
                            help="Set the kill process timeout on Orch step",
                            required=False)
        args = parser.parse_args()

        # Get the arguments
        self._input_product_folder = args.input
        self._output_folder = args.output
        self._nb_parallel_tasks = int(args.parallel)
        self._mode = args.mode
        self._auxs_folder = args.auxs
        self._staticauxs_folder = args.staticauxs
        self._gri_folder = args.gri
        self._working_dir = args.working
        self._tile = args.tile

        if args.killtimeout is not None:
            self._killtimeout = int(args.killtimeout)

        if self._tile:
            self._tile_ident = self._tile
        else:
            self._tile_ident = "000"

        self._suffix = "_T" + self._tile_ident

        if self._tile:
            self._logger.info("Requesting processing on tiles : " + self._tile)
        self._mode = args.mode

        # Set the logger level
        self._loglevel_str = args.loglevel
        if self._loglevel_str == "INFO":
            self._logger.setLevel(logging.INFO)
            self._loglevel = logging.INFO
        elif self._loglevel_str == "DEBUG":
            self._logger.setLevel(logging.DEBUG)
            self._loglevel = logging.DEBUG
        else:
            self._logger.setLevel(logging.INFO)
            self._loglevel = logging.INFO

        start_time = datetime.datetime.now()
        # Trigger file to select main scheme applicable
        trigger_filename = os.path.join(os.path.dirname(os.path.realpath(__file__)), "config",
                                        "MainSchemeTriggers.json")
        # Version management
        if args.exeversionfile:
            idpsc_exe_filename = args.exeversionfile
            probas = None
            from_probas = False
        else:
            idpsc_exe_filename = os.path.join(os.path.dirname(os.path.realpath(__file__)), "config",
                                              "IDPSC_EXE_export.json")
            probas = FileUtils.glob_one(os.path.join(self._auxs_folder, "S2IPF-GIPP"), "S2*PROBAS*xml")
            if probas is None:
                raise Exception("No PROBAS file found under "+os.path.join(self._auxs_folder, "S2IPF-GIPP"))
            from_probas = True

        # Version manager
        version_manager = VersionManager(probas, idpsc_exe_filename, trigger_filename, from_probas, self._loglevel)

        # Initialize paths
        self.__initialize_paths()

        try:
            # Pipeline configuration load
            previous_context_file = None
            previous_context_file_working = None
            pipeline_json_file = os.path.join(self._ipf_conf_dir, "pipeline.json")
            pipeline_handler = OrchestratorPipeline(pipeline_json_file)
            if not pipeline_handler.is_defined(self._mode):
                raise Exception(self._mode + " is not define in " + pipeline_json_file)
            if pipeline_handler.has_previous_mode(self._mode):
                while self._previous_mode is None:
                    self._previous_mode = pipeline_handler.get_previous_mode(self._mode)
                    if self._previous_mode is None:
                        raise Exception("All the possible previous mode have been tried, none available")
                    previous_context_file = FileUtils.glob_one(os.path.join(args.output, self._previous_mode),
                                                               "ContextFile*.json")
                    if previous_context_file is None:
                        self._logger.info("Context for mode " + self._previous_mode +
                                          " is not available, trying next possible mode")
                        self._previous_mode = None
                        continue
                    self._logger.info("Using context from previous mode " + self._previous_mode
                                      + " : " + previous_context_file)
            self.__verify_paths(self._mode, self._previous_mode)
            self._chain_name = pipeline_handler.get_chain_name(self._mode)
            self._main_scheme = version_manager.get_main_scheme(self._chain_name)
            self._logger.info("Using main scheme : "+self._main_scheme)
            self.__initialize_scheme_paths(self._main_scheme)
            # Copy orchestrator ICD to output
            output_icd_file = os.path.join(self._ipf_working_dir, "orchestratorICD.json")

            # Tasktable and conf init
            self._init_tasktable_and_conf(pipeline_handler.is_tasktable_step(self._mode))

            # edit the configuration according to parameters
            conf_json_file = os.path.join(self._ipf_mode_conf_dir, "conf_"+self._mode+".json")
            with open(conf_json_file) as f:
                conf_json = json.load(f)
            chain_json = version_manager.get_chain_versions(self._chain_name)
            # Idpsc execution folder
            idpsc_exe_dir = Constants.IDPSC_EXE_DIR
            # Init env
            ipf_env = self._init_ipf_env(chain_json, idpsc_exe_dir)

            # Get the previous mode context file to update
            context_manager_previous = None
            if self._previous_mode is not None:
                previous_context_file_working = os.path.join(self._ipf_working_dir,
                                                             "PreviousContextFile_"
                                                             + self._previous_mode + self._suffix + ".json")
                previous_idp_infos_file = os.path.join(self._output_folder, self._previous_mode, "idp_infos.xml")
                shutil.copyfile(previous_context_file, previous_context_file_working)
                context_manager_previous = ContextManager(previous_context_file_working, self._loglevel)
                self._logger.info("DatatakeType: "+context_manager_previous.config_manager.get_datatake_type())
                context_manager_previous.set_number_of_task(self._nb_parallel_tasks)
                context_manager_previous.update_version(Constants.VERSION)
                context_manager_previous.update_log_level(self._loglevel_str)
                context_manager_previous.update_processor_name(self._chain_name)
                context_manager_previous.update_from_conf(conf_json)
                context_manager_previous.update_processing_station(Constants.PROCESSING_STATION)
                context_manager_previous.update_archiving_center(Constants.PROCESSING_STATION)
                context_manager_previous.update_processing_baseline(version_manager.get_processing_baseline())
                context_manager_previous.update_creation_date(self._creation_date)
                if self._mode == "L1A":
                    local_qlcloud_folder = os.path.join(self._ipf_working_dir,
                                                        "QL_CLOUD_MASK_" + self._previous_mode)
                    context_manager_previous.import_ql_cloud_mask(local_qlcloud_folder)
                context_manager_previous.update_detector_ident(Constants.DEFAULT_DETECTOR_LIST)
                context_manager_previous.update_band_ident(Constants.DEFAULT_BAND_LIST)
                if self._tile and "TILE" in conf_json["EXTERNAL_PARALLELIZATION"]:
                    context_manager_previous.update_tile_ident(self._tile)
                    context_manager_previous.activate_parallel_tile()
                else:
                    context_manager_previous.desactivate_parallel_tile()
                context_manager_previous.write_to_file(previous_context_file_working)
                context_manager_previous.write_icd_to_file(output_icd_file)
                self._logger.info(
                    "Using context from previous mode " + self._previous_mode + " : " + previous_context_file_working)
            else:
                previous_idp_infos_file = None
                # Patch ICD
                orch_icd_files = glob.glob(os.path.join(self._output_tasktable_folder, "orchestratorICD.json"))
                if not len(orch_icd_files) == 1:
                    self._logger.error("More than one ICD or no found in " + self._output_tasktable_folder)
                    raise Exception("More than one ICD or no found in " + self._output_tasktable_folder)
                the_icd_file = orch_icd_files[0]
                self._logger.info("Using ICD: " + the_icd_file)
                icd_handler = OrchestratorICD(the_icd_file)
                icd_handler.update_from_conf(conf_json)
                icd_handler.update_processing_center(Constants.PROCESSING_STATION)
                icd_handler.update_archiving_center(Constants.PROCESSING_STATION)
                icd_handler.update_processing_baseline(version_manager.get_processing_baseline())
                icd_handler.update_creation_date(self._creation_date)
                icd_handler.update_detector_ident(Constants.DEFAULT_DETECTOR_LIST)
                icd_handler.update_band_ident(Constants.DEFAULT_BAND_LIST)
                # write down
                icd_handler.write_to_file()
                icd_handler.export_parameters(output_icd_file)
                # Patch Config
                the_orch_config_file = os.path.join(self._output_tasktable_folder, "orchestratorConfig.json")
                config_handler = OrchestratorConfig(None)
                config_handler.update_nb_tasks(self._nb_parallel_tasks)
                config_handler.update_sensings(self._datastrip_sensing_start, self._datastrip_sensing_stop)
                self._logger.info("DatatakeType: " + self._datatake_type)
                config_handler.update_datatake_type(self._datatake_type)
                config_handler.update_sensings_ms(self._datastrip_sensing_start_ms, self._datastrip_sensing_stop_ms)
                config_handler.update_acquisition_station(self._datastrip_sensing_station)
                config_handler.update_processing_station(Constants.PROCESSING_STATION)
                config_handler.update_version(Constants.VERSION)
                config_handler.update_log_level(self._loglevel_str)
                config_handler.update_processor_name(self._chain_name)
                config_handler.update_parallelisation(conf_json["PARALLELIZATION"]["PARTITIONS"])
                self._logger.info("Writing Config orch : " + the_orch_config_file)
                config_handler.write_to_file(the_orch_config_file)

            # Test if it is an inventory task
            is_inventory_mode = InventoryL0.is_inventory_mode(self._mode) or InventoryL1A.is_inventory_mode(
                self._mode) or InventoryL1B.is_inventory_mode(self._mode) or InventoryL1C.is_inventory_mode(self._mode)

            self._logger.debug("Is an inventory mode : "+str(is_inventory_mode))

            # Copy orchestratorEnviron to output as it is used in the next step
            output_env_file = os.path.join(self._output_folder_mode, "orchestratorEnviron.json")
            if self._previous_mode is None:
                orch_env_files = glob.glob(os.path.join(self._output_tasktable_folder, "orchestratorEnviron.json"))
                if not len(orch_env_files) == 1:
                    raise Exception("No orchestratorEnviron.json file found in " + self._output_tasktable_folder)
                else:
                    shutil.copyfile(orch_env_files[0], output_env_file)

            if pipeline_handler.is_tasktable_step(self._mode):
                # Find the new tasktable in cfg folder
                tasktables = glob.glob(os.path.join(self._output_tasktable_folder, "TaskTable*xml"))
                if not len(tasktables) == 1:
                    self._logger.error("More than one tasktable or no found in " + self._output_tasktable_folder)
                    raise Exception("More than one tasktable or no found in " + self._output_tasktable_folder)
                the_tasktable = tasktables[0]
                self._logger.info("Using Tasktable: " + the_tasktable)
                # Edit the number of pools in tasktable
                tasktable_reader = TaskTableReader(the_tasktable)
                tasktable_reader.set_IDPSCs_versions(chain_json)
                tasktable_reader.write_to_file()

                #########################################################################
                #                   PROCESSING START                                    #
                #########################################################################

                # GSE
                if self._mode == "L0":
                    self._logger.info("Launching GSE")
                    gse_runner = GSE(idpsc_exe_dir, version_manager.get_gse_version(), self._gipp_folder, self._working_dir,
                                     self._gsebackupfolder,
                                     self._datastrip_folder,
                                     self._datastrip_sensing_start,
                                     self._datastrip_sensing_stop,
                                     self._gselogfile,
                                     self._gseerrfile)
                    gse_runner.run()
                    self._logger.info("GSE Done")

                # Launch the orchestrator
                orch_launcher = OrchestratorLauncher(self._loglevel, port=self._port)
                poll_interval = 5
                orch_launcher.start_orchestrator(the_tasktable, self._mode, ipf_env, self._orchlogfile,
                                                 self._orcherrfile,
                                                 self._resultfile, self._dateoflogs, 0, previous_context_file_working,
                                                 poll_interval, self._tile, self._killtimeout)

                #########################################################################
                #                   PROCESSING END                                      #
                #########################################################################

                # get Tasktable processing output folder
                tasktable_folders = glob.glob(os.path.join(self._ipf_orch_working_dir, "TaskTable*"))
                if len(tasktable_folders) != 1:
                    raise Exception("More than one tasktable output folder found in " + self._ipf_orch_working_dir)
                the_tasktable_folder = tasktable_folders[0]

                # create the output context from the status file
                status_files = glob.glob(os.path.join(the_tasktable_folder, "CurrentState_*json"))
                # get the last  context file
                last_number = -1
                last_context_file = ""
                for f in status_files:
                    base_name_no_ext = os.path.splitext(os.path.basename(f))[0]
                    number = int(base_name_no_ext[base_name_no_ext.rfind("_") + 1:])
                    if number > last_number:
                        last_number = number
                        last_context_file = f
                if last_context_file == "":
                    self._logger.error(
                        "Impossible to find the CurrentState file in orch context: " + self._ipf_orch_working_dir)
                    raise Exception(
                        "Impossible to find the CurrentState file in orch context: " + self._ipf_orch_working_dir)
                self._logger.info("Using Context file : " + last_context_file)
                # Load the  context file
                context_manager = ContextManager(last_context_file, self._loglevel)
                # Update the env file
                if self._previous_mode is None:
                    context_manager.set_environment_file(output_env_file)

                if not is_inventory_mode:
                    if "OUTPUT_CONTEXT_RULES" in conf_json:
                        # Relocate outputs
                        context_manager.export_datas(the_tasktable_folder,
                                                     self._output_folder_mode,
                                                     mode=self._mode,
                                                     conf=conf_json["OUTPUT_CONTEXT_RULES"])
                    else:
                        # Relocate outputs
                        context_manager.export_datas(the_tasktable_folder, self._output_folder_mode,
                                                     self._mode)
                # GSE
                if self._mode == "L0":
                    context_manager.copy_original_sad_files(self._gsebackupfolder)

                # Write output context file
                output_context_file = os.path.join(self._output_folder_mode, "ContextFile"+self._suffix+".json")
                if not os.path.exists(output_context_file):
                    context_manager.write_to_file(output_context_file)
                else:
                    self._logger.info("Context file " + output_context_file + " already exist from previous launch")

                # Agglomerate IDP_INFOS
                current_idp_infos_file = context_manager.get_element("PROC.IDP_INFOS")
                current_idp_handler = IdpInfos(current_idp_infos_file)
                output_idp_infos_file = os.path.join(self._output_folder_mode, "idp_infos.xml")
                self._logger.info("Copying idp_info file to " + output_idp_infos_file)
                if self._previous_mode is not None:
                    previous_idp_handler = IdpInfos(previous_idp_infos_file)
                    previous_idp_handler.agglomerate(current_idp_handler)
                    previous_idp_handler.write_to_file(output_idp_infos_file)
                else:
                    if self._mode == "L0":
                        current_idp_handler.add("GSE", version_manager.get_gse_version())
                        current_idp_handler.write_to_file(output_idp_infos_file)
                    else:
                        shutil.copyfile(current_idp_infos_file, output_idp_infos_file)
            else:
                # Load the  context file
                if self._previous_mode is not None:
                    context_manager = context_manager_previous
                    # Load the previous IDP infos
                    output_idp_infos_file = previous_idp_infos_file
                    self._logger.info("Copying idp_info file to " + output_idp_infos_file)
                else:
                    raise Exception("Can't have a first step without TaskTable mode !!!!")

            #########################
            #       OLQC            #
            #########################
            self._logger.debug("Is an OLQC mode : "+str(OLQC.is_olqc_mode(self._mode)))
            if OLQC.is_olqc_mode(self._mode):
                olqc_launcher = OLQC(loglevel=self._loglevel, logfile=self._olqclogfile,
                                     errfile=self._olqcerrfile,
                                     olqc_script=os.path.join(Constants.IDPSC_EXE_DIR, "OLQC",
                                                              version_manager.get_olqc_version(),
                                                              "scripts",
                                                              "OLQC.bash"
                                                              ),
                                     soft_version=version_manager.get_olqc_version())
                olqc_launcher.do_olqc(self._working_dir,
                                      self._gipp_folder,
                                      self._mode,
                                      context_manager,
                                      self._tile,
                                      output_idp_infos_file,
                                      output_icd_file, self._dateoflogs, self._nb_parallel_tasks)

            #########################
            #       Inventory       #
            #########################
            if is_inventory_mode:
                if "L0" in self._mode:
                    inventory_launcher = InventoryL0(loglevel=self._loglevel, logfile=self._inventorylogfile,
                                                     errfile=self._inventoryerrfile,
                                                     ds_inventory_script=version_manager.get_inventory_l0_ds_script_path(),
                                                     gr_inventory_script=version_manager.get_inventory_l0_gr_script_path(),
                                                     inventory_soft_version=version_manager.get_inventory_l0_soft_version()
                                                     )
                    inventory_launcher.do_inventory(self._working_dir,
                                                    self._gipp_folder,
                                                    self._mode,
                                                    context_manager,
                                                    output_idp_infos_file,
                                                    output_icd_file, self._dateoflogs, self._nb_parallel_tasks)
                elif "L1A" in self._mode:
                    inventory_launcher = InventoryL1A(loglevel=self._loglevel, logfile=self._inventorylogfile,
                                                      errfile=self._inventoryerrfile,
                                                      ds_inventory_script=version_manager.get_inventory_l1_ds_script_path(),
                                                      gr_inventory_script=version_manager.get_inventory_l1_gr_script_path(),
                                                      inventory_soft_version=version_manager.get_inventory_l1_soft_version()
                                                      )
                    inventory_launcher.do_inventory(self._working_dir,
                                                    self._gipp_folder,
                                                    self._mode,
                                                    context_manager,
                                                    output_idp_infos_file,
                                                    output_icd_file, self._dateoflogs, self._nb_parallel_tasks)
                elif "L1B" in self._mode:
                    inventory_launcher = InventoryL1B(loglevel=self._loglevel, logfile=self._inventorylogfile,
                                                      errfile=self._inventoryerrfile,
                                                      ds_inventory_script=version_manager.get_inventory_l1_ds_script_path(),
                                                      gr_inventory_script=version_manager.get_inventory_l1_gr_script_path(),
                                                      inventory_soft_version=version_manager.get_inventory_l1_soft_version()
                                                      )
                    inventory_launcher.do_inventory(self._working_dir,
                                                    self._gipp_folder,
                                                    self._mode,
                                                    context_manager,
                                                    output_idp_infos_file,
                                                    output_icd_file, self._dateoflogs, self._nb_parallel_tasks)
                elif "L1C" in self._mode:
                    inventory_laucher = InventoryL1C(loglevel=self._loglevel, logfile=self._inventorylogfile,
                                                     errfile=self._inventoryerrfile,
                                                     ds_inventory_script=version_manager.get_inventory_l1_ds_script_path(),
                                                     gr_inventory_script=version_manager.get_inventory_l1_gr_script_path(),
                                                     inventory_soft_version=version_manager.get_inventory_l1_soft_version()
                                                     )
                    inventory_laucher.do_inventory(self._working_dir,
                                                   self._gipp_folder,
                                                   self._mode,
                                                   context_manager,
                                                   self._tile,
                                                   output_idp_infos_file,
                                                   output_icd_file, self._dateoflogs,
                                                   self._main_scheme,
                                                   self._nb_parallel_tasks)

        except Exception as e:
            # Retrieve logs
            self.__retrieve_logs(is_error=True)
            self._logger.error(e)
            self._logger.error(traceback.format_exc())
            raise e

        # Retrieve logs
        self.__retrieve_logs()

        self._logger.info("Finished processing "+self._mode + " in : " + str(
                (datetime.datetime.now() - start_time).seconds) + " seconds")

    def _init_tasktable_and_conf(self, is_tasktable_step):
        if is_tasktable_step:
            base_tasktable = os.path.join(self._ipf_mode_conf_dir, "TaskTable_" + self._mode + ".xml")
            out_tasktable = os.path.join(self._output_tasktable_folder, os.path.basename(base_tasktable))
            FileUtils.copy_file(base_tasktable, out_tasktable)
            # find relicate @ in tasktable file
            with open(out_tasktable) as f:
                if '@' in f.read():
                    raise Exception("Tasktable still contains @ !!!")
        if self._previous_mode is not None:
            return
        # DB.json file
        db_conf_file = os.path.join(self._default_config_dir, "DB.json")
        db_conf_out_file = os.path.join(self._output_tasktable_folder, "DB.json")
        FileUtils.copy_file(db_conf_file, db_conf_out_file)
        # orchestratorICD file
        if self._mode != "L0C" and self._mode != "L1A":
            icd_conf_file = os.path.join(self._default_config_dir, "orchestratorICD.json")
        else:
            icd_conf_file = os.path.join(self._default_config_dir, "orchestratorICD_L0C.json")
        icd_conf_out_file = os.path.join(self._output_tasktable_folder, "orchestratorICD.json")
        FileUtils.copy_file(icd_conf_file, icd_conf_out_file)
        # orchestratorEnviron file
        env_conf_file = os.path.join(self._default_config_dir, "orchestratorEnviron.json")
        env_conf_out_file = os.path.join(self._output_tasktable_folder, "orchestratorEnviron.json")
        FileUtils.copy_file(env_conf_file, env_conf_out_file)

    def _init_ipf_env(self, chain_json, idpsc_exe_dir):
        # Create env
        # --------------------------------------------------------------------------------------
        # Required environment variables configuration information details
        # IDPORCH_HOME      -  Defines the root directory.
        # IDPORCH_BIN_DIR  -  Defines the binary files directory.
        # IDPORCH_LOG_DIR  -  Defines the log files directory.
        # IDPORCH_TEMP_DIR  -  Defines the temporal files directory.
        # IDPORCH_CONFIG_DIR  -  Defines the configuration files directory.
        # IDPORCH_PROCESSING_DIR    -   Defines where the output of the processing will be stored
        # IDPORCH_GIPP_DIR    -   Defines where the GIPP files are stored
        # IDPORCH_AUX_DIR    -   Defines where the AUX files are stored
        # idpsc_exe_dir    -   Defines the path of the IDP-SC executables
        # ------------------------------------------------------------------------------------------
        ipf_env = os.environ.copy()
        # log level
        if self._loglevel == logging.DEBUG:
            ipf_env["IDPORCH_DEBUG"] = "true"
        # Set output processing dir to local folder
        ipf_env["S2IPF_PROCESSING_DIR"] = self._ipf_working_dir
        self._logger.info("S2IPF_PROCESSING_DIR set to " + self._ipf_working_dir)
        ipf_env["IDPORCH_PORT"] = str(self._port)
        self._logger.info("IDPORCH_PORT is set to " + str(ipf_env["IDPORCH_PORT"]))
        ipf_env["IDPORCH_PROCESSING_DIR"] = self._ipf_orch_working_dir
        ipf_env["IDPSC_EXE_DIR"] = idpsc_exe_dir
        self._logger.info("idpsc_exe_dir: " + ipf_env["IDPSC_EXE_DIR"])
        # Fill Orch needed env vars
        ipf_env["IDPORCH_LOG_DIR"] = os.path.join(self._ipf_orch_working_dir, "logs")
        ipf_env["IDPORCH_TEMP_DIR"] = os.path.join(self._ipf_orch_working_dir, "tmp")
        ipf_env["IDPORCH_CONFIG_DIR"] = self._output_tasktable_folder
        if self._datastrip_folder is not None:
            ipf_env["IDPORCH_INPUT_DATASTRIP_DIR"] = self._datastrip_input_folder
            ipf_env["IDPORCH_TDS_L0NC_DIR"] = self._datastrip_folder
            ipf_env["IDPORCH_INPUT_QUICKLOOKS_DIR"] = self._datastrip_folder
        else:
            ipf_env["IDPORCH_INPUT_DATASTRIP_DIR"] = self._ipf_orch_working_dir
            ipf_env["IDPORCH_TDS_L0NC_DIR"] = self._ipf_orch_working_dir
            ipf_env["IDPORCH_INPUT_QUICKLOOKS_DIR"] = self._ipf_orch_working_dir
        if self._granule_input_Folder is not None:
            ipf_env["IDPORCH_INPUT_GRANULES_DIR"] = self._granule_input_Folder
        ipf_env["IDPORCH_GIPP_DIR"] = self._gipp_folder
        ipf_env["IDPORCH_AUX_DIR"] = self._auxs_folder
        ipf_env["IDPORCH_STATIC_AUX_DIR"] = self._staticauxs_folder
        if self._gri_folder is not None:
            ipf_env["IDPORCH_GRI_DIR"] = self._gri_folder
        ipf_env["IDPORCH_IDPSC_INPUT_DIR"] = self._staticauxs_folder
        ipf_env["IDPORCH_LOG_DIR"] = self._ipf_orch_log_dir
        # Load the IDPSC executable
        for f in chain_json.items():
            bash_script = os.path.join(idpsc_exe_dir, f[0], f[1], "scripts", f[0] + ".bash")
            if not os.path.exists(bash_script):
                self._logger.error("No script found under : " + bash_script)
                raise Exception("No script found under : " + bash_script)
            ipf_env[f[0] + "_EXE_DIR"] = bash_script
            self._logger.info("Using idpsc : "+f[0]+" : "+f[1])
        return ipf_env

    def __verify_paths(self, mode, previous_mode):
        # verify input folders
        self._gipp_folder = os.path.join(self._auxs_folder, "S2IPF-GIPP")
        if not os.path.exists(self._gipp_folder):
            self._logger.error("Folder : " + self._gipp_folder + " doesn't exist")
            raise Exception("Folder : " + self._gipp_folder + " doesn't exist")
        self._dem_globe_folder = os.path.join(self._staticauxs_folder, "S2IPF-DEMGLOBE")
        if not os.path.exists(self._dem_globe_folder):
            self._logger.error("Folder : " + self._dem_globe_folder + " doesn't exist")
            raise Exception("Folder : " + self._dem_globe_folder + " doesn't exist")
        self._dem_geoid_folder = os.path.join(self._staticauxs_folder, "S2IPF-DEMGEOID")
        if not os.path.exists(self._dem_geoid_folder):
            self._logger.error("Folder : " + self._dem_geoid_folder + " doesn't exist")
            raise Exception("Folder : " + self._dem_geoid_folder + " doesn't exist")
        self._dem_srtm_folder = os.path.join(self._staticauxs_folder, "S2IPF-DEMSRTM")
        if not os.path.exists(self._dem_srtm_folder):
            self._logger.error("Folder : " + self._dem_srtm_folder + " doesn't exist")
            raise Exception("Folder : " + self._dem_srtm_folder + " doesn't exist")
        self._ecmwf_folder = os.path.join(self._auxs_folder, "S2IPF-ECMWF")
        if mode == "L1CTile" and not os.path.exists(self._ecmwf_folder):
            self._logger.error("Folder : " + self._ecmwf_folder + " doesn't exist")
            raise Exception("Folder : " + self._ecmwf_folder + " doesn't exist")
        self._cams_folder = os.path.join(self._auxs_folder, "S2IPF-CAMS")
        if mode == "L1CTile" and not os.path.exists(self._cams_folder):
            self._logger.error("Folder : " + self._cams_folder + " doesn't exist")
            raise Exception("Folder : " + self._cams_folder + " doesn't exist")
        self._iers_folder = os.path.join(self._auxs_folder, "S2IPF-IERS")
        if not os.path.exists(self._iers_folder):
            self._logger.error("Folder : " + self._iers_folder + " doesn't exist")
            raise Exception("Folder : " + self._iers_folder + " doesn't exist")
        if mode == "L1B":
            if not os.path.exists(self._gri_folder):
                self._logger.error("Folder : " + self._gri_folder + " doesn't exist")
                raise Exception("Folder : " + self._gri_folder + " doesn't exist")

        if previous_mode is None:
            self._datastrip_input_folder = os.path.join(self._input_product_folder, "DS")
            if not os.path.exists(self._datastrip_input_folder):
                self._logger.error("Folder : " + self._datastrip_input_folder + " doesn't exist")
                raise Exception("Folder : " + self._datastrip_input_folder + " doesn't exist")
            self._granule_input_Folder = os.path.join(self._input_product_folder, "GR")
            if not os.path.exists(self._granule_input_Folder):
                self._logger.error("Folder : " + self._granule_input_Folder + " doesn't exist")
                raise Exception("Folder : " + self._granule_input_Folder + " doesn't exist")
            # Find DS
            ds_founds = glob.glob(os.path.join(self._datastrip_input_folder, "S2*"))
            if len(ds_founds) != 1:
                raise Exception("0 or multiple datastrips founds, expected only one")
            self._datastrip_folder = ds_founds[0]
            self._logger.info("DS Folder: " + self._datastrip_folder)
            # get the metadata
            self._datastrip_metadata = glob.glob(os.path.join(self._datastrip_folder, "S2*xml"))[0]
            self._logger.info("Datastrip metadata file: " + self._datastrip_metadata)
            ds_metadata_reader = DatastripReader(self._datastrip_metadata)
            self._logger.debug(
                "Sensings: " + ds_metadata_reader.get_sensing_start() + " / " + ds_metadata_reader.get_sensing_stop())
            self._logger.debug("Reception station: " + ds_metadata_reader.get_reception_station())
            # get senging times no millis
            self._datastrip_sensing_start_ms = ds_metadata_reader.get_sensing_start()
            self._datastrip_sensing_stop_ms = ds_metadata_reader.get_sensing_stop()
            # get datatake type
            self._datatake_type = ds_metadata_reader.get_datatake_type()
            if len(self._datastrip_sensing_start_ms.split('.')) > 1:
                self._datastrip_sensing_start = self._datastrip_sensing_start_ms.split('.')[0] + "Z"
            else:
                self._datastrip_sensing_start = self._datastrip_sensing_start_ms
            if len(self._datastrip_sensing_stop_ms.split('.')) > 1:
                self._datastrip_sensing_stop = self._datastrip_sensing_stop_ms.split('.')[0] + "Z"
            else:
                self._datastrip_sensing_stop = self._datastrip_sensing_stop_ms
            self._datastrip_sensing_station = ds_metadata_reader.get_reception_station()
        else:
            self._datastrip_input_folder = os.path.join(self._input_product_folder)
            self._granule_input_Folder = os.path.join(self._input_product_folder)

    def __initialize_paths(self):
        # Create output dir
        self._output_folder_mode = os.path.join(self._output_folder, self._mode)
        if not os.path.exists(self._output_folder_mode):
            create_directory(self._output_folder_mode)
            if not os.path.exists(self._output_folder_mode):
                raise Exception("Impossible to create output dir " + self._output_folder_mode)
        # Create output dir
        if not os.path.exists(self._working_dir):
            create_directory(self._working_dir)
            if not os.path.exists(self._working_dir):
                raise Exception("Impossible to create working dir " + self._working_dir)

        # Prepare elements
        self._dateoflogs = datetime.datetime.now().strftime("%Y%m%d%H%M%S")
        self._creation_date = datetime.datetime.now().strftime("%Y-%m-%dT%H:%M:%SZ")
        # Temp Output folder for IPF
        self._ipf_working_dir = os.path.join(self._working_dir, "ipf_output_" + self._mode + "_" + self._dateoflogs)
        # data dir
        self._data_working_dir = os.path.join(self._ipf_working_dir, "data_" + self._mode + "_" + self._dateoflogs)
        create_directory(self._data_working_dir)
        self._ipf_conf_dir = os.path.join(os.path.dirname(os.path.realpath(__file__)), "data")
        self._ipf_orch_working_dir = os.path.join(self._ipf_working_dir, self._mode)
        self._resultfile = os.path.join(self._ipf_working_dir, "result_" + self._mode + self._suffix +
                                        "_" + self._dateoflogs + ".txt")
        self._orchlogfile = os.path.join(self._ipf_working_dir,
                                         "orchlog_" + self._mode + self._suffix + "_" + self._dateoflogs + ".log")
        self._orcherrfile = os.path.join(self._ipf_working_dir,
                                         "orchlog_" + self._mode + self._suffix + "_" + self._dateoflogs + ".err")
        self._gselogfile = os.path.join(self._ipf_working_dir,
                                        "gselog_" + self._mode + self._suffix + "_" + self._dateoflogs + ".log")
        self._gseerrfile = os.path.join(self._ipf_working_dir,
                                        "gselog_" + self._mode + self._suffix + "_" + self._dateoflogs + ".err")
        self._gsebackupfolder = os.path.join(self._working_dir, "GSE_BACKUP_FILES")
        create_directory(self._gsebackupfolder)
        self._inventorylogfile = os.path.join(self._ipf_working_dir,
                                              "inventorylog_" + self._mode + self._suffix + "_" + self._dateoflogs + ".log")
        self._inventoryerrfile = os.path.join(self._ipf_working_dir,
                                              "inventorylog_" + self._mode + self._suffix + "_" + self._dateoflogs + ".err")
        self._olqclogfile = os.path.join(self._ipf_working_dir,
                                         "olqclog_" + self._mode + self._suffix + "_" + self._dateoflogs + ".log")
        self._olqcerrfile = os.path.join(self._ipf_working_dir,
                                         "olqclog_" + self._mode + self._suffix + "_" + self._dateoflogs + ".err")

        create_directory(self._ipf_orch_working_dir)
        self._ipf_orch_log_dir = os.path.join(self._ipf_orch_working_dir, "logs_" + self._dateoflogs)
        create_directory(self._ipf_orch_log_dir)
        self._output_tasktable_folder = os.path.join(self._ipf_orch_working_dir, "cfg_" + self._dateoflogs)

    def __initialize_scheme_paths(self, main_scheme):
        self._ipf_mode_conf_dir = os.path.join(os.path.dirname(os.path.realpath(__file__)), "data", main_scheme,
                                               self._mode)
        self._default_config_dir = os.path.join(os.path.dirname(os.path.realpath(__file__)), "config", main_scheme)

    def __retrieve_logs(self, is_error=False):
        if os.path.exists(self._orchlogfile):
            FileUtils.copy_file(self._orchlogfile,
                                os.path.join(self._output_folder_mode, os.path.basename(self._orchlogfile)))
        if os.path.exists(self._orcherrfile):
            FileUtils.copy_file(self._orcherrfile,
                                os.path.join(self._output_folder_mode, os.path.basename(self._orcherrfile)))
        if os.path.exists(self._gselogfile):
            FileUtils.copy_file(self._gselogfile,
                                os.path.join(self._output_folder_mode, os.path.basename(self._gselogfile)))
        if os.path.exists(self._gseerrfile):
            FileUtils.copy_file(self._gseerrfile,
                                os.path.join(self._output_folder_mode, os.path.basename(self._gseerrfile)))
        if os.path.exists(self._inventorylogfile):
            FileUtils.copy_file(self._inventorylogfile,
                                os.path.join(self._output_folder_mode, os.path.basename(self._inventorylogfile)))
        if os.path.exists(self._inventoryerrfile):
            FileUtils.copy_file(self._inventoryerrfile,
                                os.path.join(self._output_folder_mode, os.path.basename(self._inventoryerrfile)))

        if os.path.isdir(self._ipf_orch_working_dir):
            if not is_error:
                working_log_folder = os.path.join(self._ipf_working_dir, "logs_SUCCESS_" + self._mode
                                                  + "_T" + self._tile_ident + "_" + self._dateoflogs)
                out_log_file = os.path.join(self._output_folder_mode, "logs_SUCCESS_" + self._mode
                                            + "_T" + self._tile_ident + "_" + self._dateoflogs + ".tar")
            else:
                working_log_folder = os.path.join(self._ipf_working_dir, "logs_FAILURE_" + self._mode
                                                  + "_T" + self._tile_ident + "_" + self._dateoflogs)
                out_log_file = os.path.join(self._output_folder_mode, "logs_FAILURE_" + self._mode
                                            + "_T" + self._tile_ident + "_" + self._dateoflogs + ".tar")
            self._logger.info("Copying log/job to "+out_log_file)

            FileUtils.copy_tree(self._ipf_orch_working_dir,
                                working_log_folder, 2, "JobOrder.*xml|.*log|.*err|report.xml")
            FileUtils.tar_directory(working_log_folder, out_log_file)


if __name__ == "__main__":
    launch_ipf = LaunchIPF()
    launch_ipf.main()
