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


class TileMtdReader(object):

    def __init__(self,filename):
        self.tree_hdr = ET.parse(filename)
        self.filename = filename
        # Remove namespace prefixes
        for elem in self.tree_hdr.getiterator():
            elem.tag = ET.QName(elem).localname
        ET.cleanup_namespaces(self.tree_hdr)
        self.root_hdr = self.tree_hdr.getroot()
        self.my_namespaces = self.root_hdr.nsmap

    def get_sensing_time(self):
        result = self.root_hdr.xpath('//General_Info/SENSING_TIME',
                                     namespaces=self.my_namespaces)
        return result[0].text
