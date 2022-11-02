# coding=utf-8
import json

import Constants


class OrchestratorConfig(object):

    def __init__(self, confjson):
        self.conf_json = None
        if confjson is not None:
            self.conf_json = confjson
        else:
            sensing = [Constants.DEFAULT_SENSING_START, Constants.DEFAULT_SENSING_STOP]
            sensing_ms = [Constants.DEFAULT_SENSING_START_MS, Constants.DEFAULT_SENSING_STOP_MS]
            parallelization = [["BAND_QL", 1], ["BAND", 1], ["DETECTOR", 1], ["ATF", 1, 1], ["Granule", 1, 1],
                               ["Tile", 1, 1]]
            self.conf_json = {
                "Sensing": sensing,
                "SensingWithMillis": sensing_ms,
                "Parallelization": parallelization,
                "MAX_TASK": str(Constants.DEFAULT_NB_TASKS),
                "OLQC_Instances": int(Constants.DEFAULT_NB_TASKS),
                "Acquisition_Station": Constants.DEFAULT_AQUISITION_STATION,
                "Stdout_Log_Level": Constants.PROCESSOR_LOG_LEVEL,
                "Stderr_Log_Level": Constants.PROCESSOR_LOG_LEVEL,
                "Processor_Name": Constants.DEFAULT_PROCESSOR_NAME,
                "Version": Constants.VERSION,
                "Processing_Station": Constants.PROCESSING_STATION,
                "DATABLOCK_IDENT": ["001"],
                "DATATAKE_TYPE": "INS-NOBS"
            }

    def update_parallelisation(self, parallelisation):
        self.conf_json["Parallelization"] = parallelisation

    def update_nb_tasks(self, nbtasks):
        self.conf_json["MAX_TASK"] = str(nbtasks)
        self.conf_json["OLQC_Instances"] = int(nbtasks)

    def update_sensings(self, sensing_start, sensing_stop):
        self.conf_json["Sensing"] = []
        self.conf_json["Sensing"].append(sensing_start)
        self.conf_json["Sensing"].append(sensing_stop)

    def update_sensings_ms(self, sensing_start_ms, sensing_stop_ms):
        self.conf_json["SensingWithMillis"] = []
        self.conf_json["SensingWithMillis"].append(sensing_start_ms)
        self.conf_json["SensingWithMillis"].append(sensing_stop_ms)

    def get_sensings(self):
        return self.conf_json["Sensing"]

    def get_sensings_ms(self):
        return self.conf_json["SensingWithMillis"]

    def update_acquisition_station(self, station):
        self.conf_json["Acquisition_Station"] = station

    def get_acquisition_station(self):
        return self.conf_json["Acquisition_Station"]

    def get_datatake_type(self):
        return self.conf_json["DATATAKE_TYPE"]

    # Fill elements with the config
    def update_log_level(self, loglevel):
        self.conf_json["Stdout_Log_Level"] = loglevel
        self.conf_json["Stderr_Log_Level"] = loglevel

    def update_processor_name(self, processorname):
        self.conf_json["Processor_Name"] = processorname

    def update_version(self, version):
        self.conf_json["Version"] = version

    def update_processing_station(self, processingstation):
        self.conf_json["Processing_Station"] = processingstation

    def update_datatake_type(self, datataketype):
        self.conf_json["DATATAKE_TYPE"] = datataketype

    def write_to_file(self, conf_file):
        with open(conf_file, "w") as f:
            json.dump(self.conf_json, f)
