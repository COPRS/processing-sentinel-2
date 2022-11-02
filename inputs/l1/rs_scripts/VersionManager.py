# coding=utf-8
import datetime
import glob
import json
import logging
import os

from log_colorizer import make_colored_stream_handler

import Constants
import DatatakeTypes
import FileUtils
import ProbasReader
from GriListFileReader import GriListFileReader
from OrchestratorConfig import OrchestratorConfig
from OrchestratorICD import OrchestratorICD


class VersionManager(object):

    def __init__(self, probas, exe_file, trigger_file, from_probas, loglevel):
        self._logger = logging.getLogger("VersionManager")
        self._handler = make_colored_stream_handler()
        self._handler = make_colored_stream_handler()
        self._handler.setFormatter(Constants.LOG_FORMATTER)
        self._logger.addHandler(self._handler)
        self._logger.setLevel(loglevel)
        # Load the default exe file
        # Source the IDPSC_EXE_export filename $idpsc_exe_filename."
        self._logger.info("Using version file : " + exe_file)
        if not os.path.exists(exe_file):
            raise Exception("No EXE export file found under : " + exe_file)
        with open(exe_file) as f:
            self._exe_json = json.load(f)
        self._logger.info("Using trigger file : " + trigger_file)
        if not os.path.exists(trigger_file):
            raise Exception("No trigger file found under : " + trigger_file)
        with open(trigger_file) as f:
            self._trigger_json = json.load(f)
        self._probas_file = None
        if from_probas:
            self._logger.info("Reading versions from proba file : "+probas)
            self._probas_reader = ProbasReader.ProbasReader(probas)
            self._probas_file = probas
            self._probas_version_map = self._probas_reader.get_version_map()
            self._exe_json["PROCESSING_BASELINE"] = self._probas_reader.get_baseline_version()
            self._exe_json["OLQC"] = self._probas_version_map["OLQC"][0]
            if "GSE" in self._probas_version_map.keys():
                self._exe_json["GSE"] = self._probas_version_map["GSE"][0]
            for chain in self._exe_json["CHAINS"]:
                for idp in self._exe_json["CHAINS"][chain]:
                    self._exe_json["CHAINS"][chain][idp] = self._probas_version_map[idp][0]
        self._main_sheme_map = {}
        for key in self._trigger_json:
            for ver in self._trigger_json[key]:
                for chain in self._exe_json["CHAINS"]:
                    for idp in self._exe_json["CHAINS"][chain]:
                        if idp == key and self._exe_json["CHAINS"][chain][idp] == ver:
                            if chain in self._main_sheme_map.keys():
                                if int(self._main_sheme_map[chain].replace(".","")) < int(self._trigger_json[key][ver].replace(".","")):
                                    self._main_sheme_map[chain] = self._trigger_json[key][ver]
                            else:
                                self._main_sheme_map[chain] = self._trigger_json[key][ver]

    def get_main_scheme(self, chain_name):
        if chain_name in self._main_sheme_map.keys():
            return self._main_sheme_map[chain_name]
        else:
            return self._exe_json["MAIN_SCHEME"]

    def get_chain_versions(self, chain_name):
        return self._exe_json["CHAINS"][chain_name]

    def get_processing_baseline(self):
        return self._exe_json["PROCESSING_BASELINE"]

    def get_olqc_version(self):
        return self._exe_json["OLQC"]

    def get_gse_version(self):
        return self._exe_json["GSE"]

    def get_inventory_l0_soft_folder(self):
        return self._exe_json["INVENTORY_L0_SOFT_FOLDER"]

    def get_inventory_l0_ds_script_name(self):
        return self._exe_json["INVENTORY_L0_DS_SCRIPT_NAME"]

    def get_inventory_l0_gr_script_name(self):
        return self._exe_json["INVENTORY_L0_GR_SCRIPT_NAME"]

    def get_inventory_l0_soft_version(self):
        return self._exe_json["INVENTORY_L1_SOFT_VERSION"]

    def get_inventory_l0_ds_script_path(self):
        return os.path.join( self.get_inventory_l0_soft_folder(),self.get_inventory_l0_ds_script_name())

    def get_inventory_l0_gr_script_path(self):
        return os.path.join( self.get_inventory_l0_soft_folder(),self.get_inventory_l0_gr_script_name())

    def get_inventory_l1_soft_folder(self):
        return self._exe_json["INVENTORY_L1_SOFT_FOLDER"]

    def get_inventory_l1_ds_script_name(self):
        return self._exe_json["INVENTORY_L1_DS_SCRIPT_NAME"]

    def get_inventory_l1_gr_script_name(self):
        return self._exe_json["INVENTORY_L1_GR_SCRIPT_NAME"]

    def get_inventory_l1_soft_version(self):
        return self._exe_json["INVENTORY_L1_SOFT_VERSION"]

    def get_inventory_l1_ds_script_path(self):
        return os.path.join(self.get_inventory_l1_soft_folder(), self.get_inventory_l1_ds_script_name())

    def get_inventory_l1_gr_script_path(self):
        return os.path.join(self.get_inventory_l1_soft_folder(), self.get_inventory_l1_gr_script_name())
