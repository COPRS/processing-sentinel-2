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
import copy

from lxml import etree as et


class JobOrderInventoryReader(object):

    def __init__(self, filename):
        self.tree_hdr = et.parse(filename)
        self.root_hdr = self.tree_hdr.getroot()
        self.filename = filename

    def set_acquisition_station(self,station):
        result = self.root_hdr.xpath(
            '//Ipf_Job_Order/Ipf_Conf/Acquisition_Station')
        result[0].text = station

    def set_processing_station(self,station):
        result = self.root_hdr.xpath(
            '//Ipf_Job_Order/Ipf_Conf/Processing_Station')
        result[0].text = station

    def set_l2_ds_input(self, filename):
        result = self.root_hdr.xpath(
            '//Ipf_Job_Order/List_of_Ipf_Procs[1]/Ipf_Proc/List_of_Inputs/Input[File_Type/text()=\'DS\']'
            '/List_of_File_Names[1]/File_Name')
        result[0].text = filename

    def set_l1_ds_input(self, filename):
        result = self.root_hdr.xpath(
            '//Ipf_Job_Order/List_of_Ipf_Procs[1]/Ipf_Proc/List_of_Inputs/Input[File_Type/text()=\'L1C_DS\']'
            '/List_of_File_Names[1]/File_Name')
        result[0].text = filename

    def set_l2_tl_input(self, filenames):
        base_xpath_filenames = '//Ipf_Job_Order/List_of_Ipf_Procs[1]/Ipf_Proc/List_of_Inputs/' \
                               'Input[File_Type/text()=\'TL\']/List_of_File_Names'
        list_of_filenames = self.root_hdr.xpath(base_xpath_filenames)
        filename_node_tpl = self.root_hdr.xpath(base_xpath_filenames + '[1]/File_Name')
        for g in filenames:
            new_node = copy.deepcopy(filename_node_tpl[0])
            new_node.text = g
            list_of_filenames[0].append(new_node)
        list_of_filenames[0].remove(filename_node_tpl[0])
        list_of_filenames[0].attrib['count'] = str(len(filenames))

    def set_l1_tl_input(self, filenames):
        base_xpath_filenames = '//Ipf_Job_Order/List_of_Ipf_Procs[1]/Ipf_Proc/List_of_Inputs/' \
                               'Input[File_Type/text()=\'TL_L1C\']/List_of_File_Names'
        list_of_filenames = self.root_hdr.xpath(base_xpath_filenames)
        filename_node_tpl = self.root_hdr.xpath(base_xpath_filenames + '[1]/File_Name')
        for g in filenames:
            new_node = copy.deepcopy(filename_node_tpl[0])
            new_node.text = g
            list_of_filenames[0].append(new_node)
        list_of_filenames[0].remove(filename_node_tpl[0])
        list_of_filenames[0].attrib['count'] = str(len(filenames))

    def set_working_input(self, filename):
        result = self.root_hdr.xpath(
            '//Ipf_Job_Order/List_of_Ipf_Procs[1]/Ipf_Proc/List_of_Inputs/Input[File_Type/'
            'text()=\'WORKING\']/List_of_File_Names[1]/File_Name')
        result[0].text = filename

    def set_l2_tl_sensing_start(self, sensing_start):
        result = self.root_hdr.xpath('//Ipf_Job_Order/Ipf_Conf/Dynamic_Processing_Parameters/Processing_Parameter[Name/text()=\'SENSING_START\']/Value')
        result[0].text = sensing_start

    def set_l2_tl_sensing_stop(self, sensing_stop):
        result = self.root_hdr.xpath('//Ipf_Job_Order/Ipf_Conf/Dynamic_Processing_Parameters/Processing_Parameter[Name/text()=\'SENSING_STOP\']/Value')
        result[0].text = sensing_stop

    def write_to_file(self, filename):
        self.tree_hdr.write(filename, encoding="UTF-8")
