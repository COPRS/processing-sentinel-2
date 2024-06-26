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


class DatastripReader(object):

    def __init__(self,filename):
        self.tree_hdr = ET.parse(filename)
        self.filename = filename
        # Remove namespace prefixes
        for elem in self.tree_hdr.getiterator():
            elem.tag = ET.QName(elem).localname
        ET.cleanup_namespaces(self.tree_hdr)
        self.root_hdr = self.tree_hdr.getroot()
        self.my_namespaces = self.root_hdr.nsmap

    def get_sensing_start(self):
        result = self.root_hdr.xpath('//General_Info/Datastrip_Time_Info/DATASTRIP_SENSING_START',
                                     namespaces=self.my_namespaces)
        return result[0].text

    def get_sensing_stop(self):
        result = self.root_hdr.xpath('//General_Info/Datastrip_Time_Info/DATASTRIP_SENSING_STOP',
                                     namespaces=self.my_namespaces)
        return result[0].text

    def get_sensing_start_utc(self):
        sensing_start = self.get_sensing_start().replace("Z","")
        sensing_start_no_subsecond = sensing_start[:sensing_start.rfind(".")]
        if "." in sensing_start:
            sub_second_part = '{0:<06s}'.format(sensing_start[sensing_start.rfind(".")+1:])
        else:
            sub_second_part = '000000'
        return "UTC=" + sensing_start_no_subsecond + "." + sub_second_part

    def get_sensing_stop_utc(self):
        sensing_stop = self.get_sensing_stop().replace("Z", "")
        sensing_stop_no_subsecond = sensing_stop[:sensing_stop.rfind(".")]
        if "." in sensing_stop:
            sub_second_part = '{0:<06s}'.format(sensing_stop[sensing_stop.rfind(".")+1:])
        else:
            sub_second_part = '000000'
        return "UTC=" + sensing_stop_no_subsecond + "." + sub_second_part

    def get_sensing_time(self):
        result = self.root_hdr.xpath('//General_Info/SENSING_TIME',
                                     namespaces=self.my_namespaces)
        return result[0].text

    def get_sensing_time_utc(self):
        sensing_time = self.get_sensing_time().replace("Z", "")
        sensing_time_no_subsecond = sensing_time[:sensing_time.rfind(".")]
        if "." in sensing_time:
            sub_second_part = '{0:<06s}'.format(sensing_time[sensing_time.rfind(".") + 1:])
        else:
            sub_second_part = '000000'
        return "UTC=" + sensing_time_no_subsecond + "." + sub_second_part

    def get_reception_station(self):
        result = self.root_hdr.xpath('//General_Info/Downlink_Info/RECEPTION_STATION',
                                     namespaces=self.my_namespaces)
        return result[0].text
