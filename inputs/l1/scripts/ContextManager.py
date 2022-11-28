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
from GriListFileReader import GriListFileReader
from OrchestratorConfig import OrchestratorConfig
from OrchestratorICD import OrchestratorICD


class ContextManager(object):

    def __init__(self, contextfile, loglevel):
        self._logger = logging.getLogger("ContextManager")
        handler = make_colored_stream_handler()
        handler.setFormatter(Constants.LOG_FORMATTER)
        self._logger.addHandler(handler)
        self._logger.setLevel(loglevel)
        self.context_json = None
        self.context_file = contextfile
        with open(contextfile) as f:
            self.context_json = json.load(f)
        self.config_manager = OrchestratorConfig(self.context_json["Config"])
        self.context_icd = {"PARAMETERS": self.context_json["ICD_parameters"],
                            "INPUT_OUTPUT_MAPPING": self.context_json["INPUT_OUTPUT_MAPPING"]
                            }
        self.icd_manager = OrchestratorICD(self.context_icd, fromdict=True)
        self.input_output_map = self.context_json["INPUT_OUTPUT_MAPPING"]
        self._original_gri_list_file = None
        self._original_gri_orbit_folder = None

    def export_datas(self, current_tasktable_dir, output_folder, mode, conf=None):
        self._logger.info("Starting relocation from " + current_tasktable_dir + " to "
                           + output_folder )
        do_l1b = DatatakeTypes.do_l1b(self.config_manager.get_datatake_type())
        do_l1a = DatatakeTypes.do_l1a(self.config_manager.get_datatake_type())
        self._logger.debug("Export L1A datas : "+str(do_l1a))
        self._logger.debug("Export L1B datas : "+str(do_l1b))
        start_time = datetime.datetime.now()
        # cache of already relocated folders
        cache_folders = []
        # dict of already relocated
        already_relocated = {}
        # GEt all the PROC elements and exlude from conf mode
        for f in self.context_json.items():
            if str(f[0]).startswith("PROC.") and not str(f[0]).startswith("PROC.LIST"):
                if isinstance(f[1], list):
                    for idx in range(len(f[1])):
                        localpath = f[1][idx]
                        if current_tasktable_dir in localpath:
                            excluded, filtered = self._is_excluded_or_filtered(conf, localpath)
                            if excluded:
                                continue
                            self._logger.debug("Rellocating " + localpath)
                            try:
                                element_name_to_copy = str(localpath).replace(current_tasktable_dir + os.sep, "").replace(
                                    current_tasktable_dir, "")
                                element_to_copy = os.path.join(current_tasktable_dir, element_name_to_copy)
                                if element_to_copy not in cache_folders:
                                    if os.path.isdir(element_to_copy):
                                        # Special case when we need to remove the gfs files
                                        if "VECTOR" in element_to_copy:
                                            FileUtils.remove_files_recursively(element_to_copy,".*gfs")
                                        if filtered and not do_l1a:
                                            FileUtils.remove_files_recursively(element_to_copy, ".*L1A.*raw")
                                        if filtered and not do_l1b:
                                            FileUtils.remove_files_recursively(element_to_copy, ".*L1B.*raw")

                                        # Standard case
                                        FileUtils.copy_directory_recursive(element_to_copy,
                                                                           os.path.join(output_folder,
                                                                                        element_name_to_copy))
                                        cache_folders.append(element_to_copy)
                                        f[1][idx] = os.path.join(output_folder, element_name_to_copy)
                                    elif os.path.isfile(element_to_copy):
                                        FileUtils.copy_file(element_to_copy,
                                                            os.path.join(output_folder, element_name_to_copy))
                                        f[1][idx] = os.path.join(output_folder, element_name_to_copy)
                                else:
                                    self._logger.debug("Already in cache")
                            except OSError as err:
                                raise err
                elif isinstance(f[1], unicode):
                    localpath = f[1]
                    if current_tasktable_dir in localpath:
                        excluded, filtered = self._is_excluded_or_filtered(conf, localpath)
                        if excluded:
                            continue
                        try:
                            filename = os.path.basename(localpath)
                            dir_to_copy = os.path.dirname(localpath).replace(current_tasktable_dir + os.sep, "").replace(
                                current_tasktable_dir, "")
                            output_dir = os.path.join(output_folder, dir_to_copy)
                            self._logger.debug("Rellocating " + localpath + " to " + output_dir)
                            if not os.path.exists(output_dir):
                                FileUtils.create_directory(output_dir)
                            FileUtils.copy_file(localpath, os.path.join(output_dir, filename))
                            self.context_json[f[0]] = os.path.join(output_dir, filename)
                        except OSError as err:
                            raise err

        # Get the ones from INPUT/OUTPUT mapping
        if self.input_output_map is not None:
            for targets_idpsc in self.input_output_map.items():
                # idpsc_target = targets_idpsc[0]
                for format_targets in targets_idpsc[1].items():
                    # format_target = format_targets[0]
                    for format_sources in format_targets[1].items():
                        format_source = format_sources[0]
                        for idpsc_source in format_sources[1]:
                            excluded, filtered = self._is_filtered_or_excluded_in_out(conf, format_source, idpsc_source)
                            if excluded:
                                continue
                            key = format_source if format_source.startswith("PROC.") else ("PROC.LIST." + format_source)
                            if key in self.context_json:
                                for pp in range(len(self.context_json[key])):
                                    idpsc_target_context = self.context_json[key][pp]
                                    if idpsc_target_context[0] == idpsc_source:
                                        for t in range(len(idpsc_target_context[1])):
                                            target_path_context = idpsc_target_context[1][t]
                                            if current_tasktable_dir in target_path_context:
                                                try:
                                                    folder_to_copy = str(idpsc_target_context[1][t]).replace(
                                                        current_tasktable_dir + os.sep,
                                                        "").replace(current_tasktable_dir, "")
                                                    self._logger.debug(
                                                        "Relocating IN/OUT MAP: " + idpsc_target_context[1][t] + " to "
                                                        + os.path.join(output_folder, folder_to_copy))
                                                    element_to_copy = os.path.join(current_tasktable_dir, folder_to_copy)
                                                    if os.path.isdir(element_to_copy):
                                                        if element_to_copy not in cache_folders:
                                                            # Special case when we need to remove the gfs files
                                                            if "VECTOR" in element_to_copy:
                                                                FileUtils.remove_files_recursively(element_to_copy,
                                                                                                   ".*gfs")
                                                            if filtered and not do_l1a:
                                                                FileUtils.remove_files_recursively(element_to_copy,
                                                                                                   ".*L1A.*raw")
                                                            if filtered and not do_l1b:
                                                                FileUtils.remove_files_recursively(element_to_copy,
                                                                                                   ".*L1B.*raw")
                                                            # standard case
                                                            FileUtils.copy_directory_recursive(
                                                                   os.path.join(element_to_copy),
                                                                    os.path.join(output_folder, folder_to_copy))
                                                            cache_folders.append(element_to_copy)
                                                        else:
                                                            self._logger.debug("Previously copied")
                                                        # still update the path since it can be copied from the PROC
                                                        self.context_json[key][pp][1][t] = os.path.join(output_folder,
                                                                                                        folder_to_copy)
                                                    else:
                                                        raise Exception("File detected in input/output mapping export")
                                                except OSError as err:
                                                    raise err
        # Relocate the VIRTUAL SENSOR
        if "DB" not in self.context_json and "VIRTUAL_SENSOR" not in self.context_json["DB"]:
            raise Exception("No DB/VIRTUAL_SENSOR in context file !!")
        else:
            self._logger.debug("Relocating VIRTUAL_SENSOR : "+self.context_json["DB"]["VIRTUAL_SENSOR"] + " to " +
                                               os.path.join(output_folder,"VIRTUAL_SENSOR"))
            virtual_sensor_folder = os.path.join(self.context_json["DB"]["VIRTUAL_SENSOR"], "VIRTUAL_SENSOR_MODEL")
            if os.path.exists(virtual_sensor_folder):
                FileUtils.copy_directory_recursive(virtual_sensor_folder,
                                                   os.path.join(output_folder,"VIRTUAL_SENSOR", "VIRTUAL_SENSOR_MODEL"))
        # Relocate the Homologuous Points
        if "DB" not in self.context_json and "HOMOLOG_POINTS_LIST" not in self.context_json["DB"]:
            raise Exception("No DB/HOMOLOG_POINTS_LIST in context file !!")
        else:
            self._logger.debug("Relocating HOMOLOG_POINTS_LIST : " + self.context_json["DB"]["HOMOLOG_POINTS_LIST"]
                               + " to " +
                               os.path.join(output_folder, "HOMOLOG_POINTS_LIST"))
            homologuous_folder = self.context_json["DB"]["HOMOLOG_POINTS_LIST"]
            if os.path.exists(homologuous_folder):
                FileUtils.copy_directory_recursive(homologuous_folder,
                                                   os.path.join(output_folder, "HOMOLOG_POINTS_LIST"))
        self._logger.info("Finished relocating in : "+str(
                (datetime.datetime.now() - start_time).seconds) + " seconds")

    def _is_filtered_or_excluded_in_out(self, conf, format_source, idpsc_source):
        excluded = False
        filtered = False
        if conf is not None:
            for excl_idp in conf["EXCLUDE"].items():
                if excl_idp[0] in idpsc_source:
                    for excl_type in excl_idp[1]:
                        if excl_type in format_source:
                            self._logger.debug(
                                "Excluded IN/OUT : " + excl_idp[0] + " for type " + format_source)
                            excluded = True
                            continue
            if "FILTER_L1A" in conf:
                for g in conf["FILTER_L1A"].items():
                    if g[0] in idpsc_source:
                        for t in g[1]:
                            if t in format_source:
                                self._logger.debug(
                                    "Filtered : " + format_source + " from " + g[0] + " of type " + t)
                                filtered = True
                                continue
        return excluded, filtered

    def _is_excluded_or_filtered(self, conf, p):
        excluded = False
        filtered = False
        if conf is not None:
            for g in conf["EXCLUDE"].items():
                if g[0] in p:
                    for t in g[1]:
                        if t in p:
                            self._logger.debug(
                                "Excluded : " + p + " from " + g[0] + " of type " + t)
                            excluded = True
                            continue
            if "FILTER_L1A" in conf:
                for g in conf["FILTER_L1A"].items():
                    if g[0] in p:
                        for t in g[1]:
                            if t in p:
                                self._logger.debug(
                                    "Filtered : " + p + " from " + g[0] + " of type " + t)
                                filtered = True
                                continue
        return excluded, filtered

    def set_environment_file(self, env_file):
        self.context_json["OrchestratorEnvironment"] = env_file

    def set_number_of_task(self, nbtask):
        self.config_manager.update_nb_tasks(nbtask)

    def update_from_conf(self, param_dict):
        self.icd_manager.update_from_conf(param_dict)
        self.config_manager.update_parallelisation(param_dict["PARALLELIZATION"]["PARTITIONS"])

    def activate_parallel_tile(self):
        self.icd_manager.activate_parallel_tile()

    def desactivate_parallel_tile(self):
        self.icd_manager.desactivate_parallel_tile()

    def update_detector_ident(self, detector):
        self.icd_manager.update_detector_ident(detector)

    def update_radio_finalize_band_ident(self, bands):
        self.icd_manager.update_radio_finalize_band_ident(bands)

    def import_ql_cloud_mask(self, local_qlcloud_folder):
        if self.get_element_in_proc_list("QL_CLOUD_MASK", "PROC.LIST.VECTOR") is not None:
            original_ql_cloud_folder = self.get_element_in_proc_list("QL_CLOUD_MASK", "PROC.LIST.VECTOR")
            self._logger.debug("Copying QL_CLOUD_MASK : " +
                               self.get_element_in_proc_list("QL_CLOUD_MASK", "PROC.LIST.VECTOR") + " to " +
                               local_qlcloud_folder)
            FileUtils.create_directory(local_qlcloud_folder)
            FileUtils.copy_directory_recursive(original_ql_cloud_folder, local_qlcloud_folder)
            FileUtils.remove_files_recursively(local_qlcloud_folder, ".*gfs")
            for idx in range(0, len(self.context_json["PROC.LIST.VECTOR"])):
                if self.context_json["PROC.LIST.VECTOR"][idx][0] == "QL_CLOUD_MASK":
                    self.context_json["PROC.LIST.VECTOR"][idx][1] = [local_qlcloud_folder]
            if "QL_CLOUD_MASK" in self.context_json["PROC.VECTOR"][0]:
                self.context_json["PROC.VECTOR"][0] = [local_qlcloud_folder]

    def copy_original_sad_files(self, gse_folder):
        pdi_ds_output_folder = self.get_element_in_proc_list("FORMAT_METADATA_DS_L0C",
                                                                        "PROC.LIST.PDI_DS")
        if pdi_ds_output_folder is None:
            raise Exception("No PROC.LIST.PDI_DS in context for FORMAT_METADATA_DS_L0C !!!")
        datastrips_out = glob.glob(os.path.join(pdi_ds_output_folder, "S2*DS*"))
        if len(datastrips_out) != 1:
            raise Exception(
                "No or more than one datastrips found in " + os.path.join(pdi_ds_output_folder, "S2*"))
        datastrip_out_anc_folder = os.path.join(datastrips_out[0], "ANC_DATA")
        for f in glob.glob(os.path.join(gse_folder, "original_S2*")):
            self._logger.info("Copying original SAD files : " + f + " to " + datastrip_out_anc_folder)
            FileUtils.copy_file(f, os.path.join(datastrip_out_anc_folder, os.path.basename(f)))

    def update_band_ident(self, band):
        self.icd_manager.update_band_ident(band)

    def update_tile_ident(self, tile):
        self.icd_manager.update_tile_ident(tile)

    def update_sensings(self, ds_sensing_start, ds_sensing_stop):
        self.config_manager.update_sensings(ds_sensing_start, ds_sensing_stop)

    def get_sensings(self):
        return self.config_manager.get_sensings()

    def get_sensings_ms(self):
        return self.config_manager.get_sensings_ms()

    def get_acquisition_station(self):
        return self.config_manager.get_acquisition_station()

    def update_processing_station(self, station):
        self.config_manager.update_processing_station(station)
        self.icd_manager.update_processing_center(station)

    def update_archiving_station(self, station):
        self.icd_manager.update_archiving_center(station)

    def update_version(self, version):
        self.config_manager.update_version(version)

    def update_log_level(self, processor_log_level):
        self.config_manager.update_log_level(processor_log_level)

    def update_processor_name(self, chain_name):
        self.config_manager.update_processor_name(chain_name)

    def update_processing_baseline(self, baseline):
        self.icd_manager.update_processing_baseline(baseline)

    def update_archiving_center(self, center):
        self.icd_manager.update_archiving_center(center)

    def update_creation_date(self, date):
        self.icd_manager.update_creation_date(date)

    def get_element(self, elementname):
        if elementname in self.context_json:
            return self.context_json[elementname]
        return None

    def get_element_in_proc_list(self, idpsc_name, output_list_name):
        if output_list_name in self.context_json:
            for f in self.context_json[output_list_name]:
                if f[0] == idpsc_name:
                    return f[1][0]
        return None

    def get_tile_list_file(self):
        tile_list_file = self.get_element("PROC.TILE_LIST_FILE")
        if tile_list_file is None:
            raise Exception("No PROC.TILE_LIST_FILE found in context !!!")
        if not os.path.exists(tile_list_file[0]):
            raise Exception("No file " + tile_list_file[0] + " found on the system !!!")
        return tile_list_file[0]

    def write_to_file(self, output_file_name):
        with open(output_file_name, "w") as f:
            json.dump(self.context_json, f)

    def write_icd_to_file(self, output_file_name):
        self.icd_manager.export_parameters(output_file_name)




