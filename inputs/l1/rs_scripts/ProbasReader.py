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


from lxml import etree as et


class ProbasReader(object):

    def __init__(self, filename):
        self.tree_hdr = et.parse(filename)
        self.root_hdr = self.tree_hdr.getroot()
        self.filename = filename

    def get_version_map(self):
        idpsc_list_xml = self.root_hdr.xpath(
            '//Idp_Sc_List')
        version_map = {}
        for idpsc in idpsc_list_xml:
            idpsc_name = idpsc.findtext("Idp_Sc_Name")
            idpsc_version = idpsc.findtext("Idp_Sc_Version")
            idpsc_workflow = idpsc.attrib["workflow"]
            version_map[idpsc_name] = (idpsc_version, idpsc_workflow)
        return version_map

    def get_baseline_version(self):
        result = self.root_hdr.xpath('//Baseline_Version')
        return result[0].text

    def write_to_file(self, filename):
        self.tree_hdr.write(filename, encoding="UTF-8")
