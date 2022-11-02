# coding=utf-8
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# author : Esquis Benjamin for CSGroup
#

# coding=utf-8
import copy
import re

from lxml import etree as et


class GriListFileReader(object):

    def __init__(self, filename):
        self.tree_hdr = et.parse(filename)
        self.root_hdr = self.tree_hdr.getroot()
        self.filename = filename

    def strip_detector(self, detector_list):
        gri_files = self.root_hdr.xpath(
            '//GRI_GR')
        # Check if there is one detector element, if not keep all
        matched = False
        for gri in gri_files:
            for det in detector_list:
                if re.search(".*_D" + det + "_.*", gri.attrib["GRI_GR_Id"]) is not None:
                    matched = True
                    break
            if matched:
                break
        # Do the job
        if matched:
            idx = 1
            for gri in gri_files:
                matched = False
                for det in detector_list:
                    if re.search(".*_D"+det+"_.*",gri.attrib["GRI_GR_Id"]) is not None:
                        matched = True
                if matched:
                    gri.attrib["GRI_GR_Number"] = str(idx)
                    idx += 1
                else:
                    self.root_hdr.remove(gri)
            self.root_hdr.attrib["Total_Number_of_GRI_GR"] = str(idx-1)
        else:
            for gri in gri_files[1:]:
                self.root_hdr.remove(gri)
            self.root_hdr.attrib["Total_Number_of_GRI_GR"] = str(1)

    def write_to_file(self, filename):
        self.tree_hdr.write(filename, encoding="UTF-8")
