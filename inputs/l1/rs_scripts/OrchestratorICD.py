# coding=utf-8
import json
import re


class OrchestratorICD(object):

    def __init__(self, confjsonfile,fromdict=False):
        self.conf_icd = None
        if not fromdict:
            self.conf_file = confjsonfile
            with open(confjsonfile) as f:
                self.conf_icd = json.load(f)
        else:
            self.conf_icd = confjsonfile

    def update_from_conf(self, param_dict):
        if not "PARAMETERS_OVERLOADING" in param_dict:
            raise Exception("No PARAMETERS_OVERLOADING in "+self.conf_file)

        if "GLOBAL" in param_dict["PARAMETERS_OVERLOADING"]:
            for j in param_dict["PARAMETERS_OVERLOADING"]["GLOBAL"].items():
                replaced = False
                for i in self.conf_icd["PARAMETERS"].items():
                    for p in i[1]:
                        if p[0] == j[0]:
                            p[1] = j[1]
                            replaced = True
                if not replaced:
                    raise Exception("Element "+j[0]+" has nowhere to be replaced !!!")

        if "IDPSC_SPECIFIC" in param_dict["PARAMETERS_OVERLOADING"]:
            for j in param_dict["PARAMETERS_OVERLOADING"]["IDPSC_SPECIFIC"].items():
                if j[0] in self.conf_icd["PARAMETERS"]:
                    for k in j[1].items():
                        replaced = False
                        for i in self.conf_icd["PARAMETERS"][j[0]]:
                            if i[0] == k[0]:
                                i[1] = k[1]
                                replaced = True
                        if not replaced:
                            raise Exception("Element "+k[0]+" has nowhere to be replaced !!!")
                else:
                    raise Exception("No IDPSC "+j[0]+" in orchstratorICD file")

        if "PARALLELIZATION" in param_dict and "MODE" in param_dict["PARALLELIZATION"]:
            self._update_parallelization(param_dict["PARALLELIZATION"]["MODE"])
        else:
            raise Exception("No PARALLELIZATION/MODE in config file : "+self.conf_file)

        if "FOLDER_FUSION_STRATEGY" in param_dict:
            self._update_folder_fusion_strategy(param_dict["FOLDER_FUSION_STRATEGY"])
        else:
            # Default to SYMLINK
            self._update_folder_fusion_strategy("SYMLINK")

    def _update_parallelization(self, parallelization_dict):
        for i in self.conf_icd["PARAMETERS"].items():
            has_parallel_atf = False
            has_parallel_atf_detector_ident = False
            if "ATF" in parallelization_dict:
                for p in i[1]:
                    if p[0] == "PARALLEL_ATF":
                        has_parallel_atf = parallelization_dict["ATF"]
                        p[1] = parallelization_dict["ATF"]
                    if p[0] == "PARALLEL_ATF_DETECTOR_IDENT":
                        has_parallel_atf_detector_ident = True
            if "GRANULE" in parallelization_dict:
                for p in i[1]:
                    if p[0] == "PARALLEL_GRANULE":
                        if has_parallel_atf:
                            p[1] = "false"
                        else:
                            p[1] = parallelization_dict["GRANULE"]
            if "DETECTOR" in parallelization_dict:
                for p in i[1]:
                    if p[0] == "PARALLEL_DETECTOR":
                        if has_parallel_atf and has_parallel_atf_detector_ident:
                            p[1] = "false"
                        else:
                            p[1] = parallelization_dict["DETECTOR"]
            if "BAND" in parallelization_dict:
                # Bug on RADIO_AB that doesn't support BAND //
                if i[0] != "RADIO_AB":
                    for p in i[1]:
                        if p[0] == "PARALLEL_BAND":
                            p[1] = parallelization_dict["BAND"]
                        if p[0] == "PARALLEL_BAND_QL":
                            p[1] = parallelization_dict["BAND"]
            if "TILE" in parallelization_dict:
                for p in i[1]:
                    if p[0] == "PARALLEL_TILE":
                        p[1] = parallelization_dict["TILE"]

    def _update_folder_fusion_strategy(self, strategy):
        self.conf_icd["FOLDER_FUSION_STRATEGY"] = strategy

    def update_detector_ident(self, detector):
        splitted = detector.split("-")
        for d in splitted:
            if re.search("[0-9]{2}", d) is None:
                raise Exception("Detectors "+detector + " doesn't match the pattern [0-9]{2}")
        for i in self.conf_icd["PARAMETERS"].items():
            has_parallel_atf = False
            has_parallel_detector = False
            value_parallel_atf = False
            has_parallel_atf_detector_ident = False
            value_parallel_detector = False
            has_parallel_detector_ident = False
            for p in i[1]:
                if p[0] == "PARALLEL_DETECTOR":
                    has_parallel_detector = True
                    value_parallel_detector = p[1].lower() == "true"
                if p[0] == "PARALLEL_DETECTOR_IDENT":
                    has_parallel_detector_ident = True
                if p[0] == "PARALLEL_ATF":
                    has_parallel_atf = True
                    value_parallel_atf = p[1].lower() == "true"
                if p[0] == "PARALLEL_ATF_DETECTOR_IDENT":
                    has_parallel_atf_detector_ident = True
            if has_parallel_atf and value_parallel_atf and has_parallel_detector and value_parallel_detector\
                and has_parallel_atf_detector_ident:
                raise Exception("Incompatible parallelization with both detector and ATF detectors detected")
            for p in i[1]:
                if p[0] == "PARALLEL_DETECTOR_IDENT":
                    p[1] = detector
                if p[0] == "PARALLEL_ATF_DETECTOR_IDENT":
                    if has_parallel_detector_ident and not has_parallel_detector:
                        p[1] = "01"
                    else:
                        p[1] = detector

    def activate_parallel_tile(self):
        for i in self.conf_icd["PARAMETERS"].items():
            for p in i[1]:
                if p[0] == "PARALLEL_TILE":
                    p[1] = "true"

    def desactivate_parallel_tile(self):
        for i in self.conf_icd["PARAMETERS"].items():
            for p in i[1]:
                if p[0] == "PARALLEL_TILE":
                    p[1] = "false"

    def update_band_ident(self, band):
        splitted = band.split("-")
        for b in splitted:
            if re.search("B[0-9]{2}", b) is None:
                raise Exception("Bands "+band + " doesn't match the pattern B[0-9]{2}")
        for i in self.conf_icd["PARAMETERS"].items():
            for p in i[1]:
                if p[0] == "PARALLEL_BAND_IDENT":
                    p[1] = band

    def update_radio_finalize_band_ident(self, band):
        splitted = band.split("-")
        for b in splitted:
            if re.search("B[0-9]{2}", b) is None:
                raise Exception("Bands " + band + " doesn't match the pattern B[0-9]{2}")
        for i in self.conf_icd["PARAMETERS"].items():
            if i[0] == "RADIO_FINALIZE":
                for p in i[1]:
                    if p[0] == "PARALLEL_BAND_IDENT":
                        p[1] = band

    def update_tile_ident(self, tile):
        splitted = tile.split("-")
        for t in splitted:
            if re.search("[0-9]{3}", t) is None:
                raise Exception("Tiles "+tile + " doesn't match the pattern [0-9]{3}")
        for i in self.conf_icd["PARAMETERS"].items():
            for p in i[1]:
                if p[0] == "PARALLEL_TILE_IDENT":
                    p[1] = tile

    def update_processing_baseline(self,baseline):
        self.update_all("PROCESSING_BASELINE",baseline)

    def update_processing_center(self,center):
        self.update_all("PROCESSING_CENTER",center)

    def update_archiving_center(self, center):
        self.update_all("ARCHIVING_CENTER", center)

    def update_creation_date(self,date):
        self.update_all("CREATION_DATE",date)

    def update_all(self, name, value):
        for i in self.conf_icd["PARAMETERS"].items():
            for p in i[1]:
                if p[0] == name:
                    p[1] = value

    def get_input_output_mapping(self):
        if "INPUT_OUTPUT_MAPPING" in self.conf_icd:
            return self.conf_icd["INPUT_OUTPUT_MAPPING"]
        else:
            return None

    def write_to_file(self,filename=None):
        if filename is None:
            with open(self.conf_file,"w") as f:
                json.dump(self.conf_icd,f)
        else:
            with open(filename,"w") as f:
                json.dump(self.conf_icd,f)

    def export_parameters(self,filename):
        with open(filename, "w") as f:
            json.dump(self.conf_icd["PARAMETERS"], f)




