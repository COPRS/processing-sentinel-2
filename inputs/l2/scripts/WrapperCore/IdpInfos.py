# Copyright 2023 CS Group
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# coding=utf-8
from lxml import etree as ET


class IdpInfos(object):

    def __init__(self, filename):
        # self.my_namespaces = dict([node for _, node in ET.iterparse(filename, events=['start-ns'])])
        self.tree_hdr = ET.parse(filename)
        self.root_hdr = self.tree_hdr.getroot()
        self.filename = filename

    def agglomerate(self, other):
        idpsc_list = other.get_list_of_IDPSC()
        self.root_hdr.extend(idpsc_list)

    def get_list_of_IDPSC(self):
        idpsc_list = self.root_hdr.findall("IDPSc_Name")
        return idpsc_list

    def add(self, name, version):
        element = ET.Element("IDPSc_Name", attrib={"version": version})
        element.text = name
        self.root_hdr.append(element)

    def write_to_file(self, filename=None):
        if filename is None:
            self.tree_hdr.write(self.filename, encoding="UTF-8")
        else:
            self.tree_hdr.write(filename, encoding="UTF-8")
