# coding=utf-8
import copy

from lxml import etree as et


class JobOrderInventoryReader(object):

    def __init__(self, filename):
        self.tree_hdr = et.parse(filename)
        self.root_hdr = self.tree_hdr.getroot()
        self.filename = filename

    def set_acquisition_station(self, station):
        result = self.root_hdr.xpath(
            '//Ipf_Job_Order/Ipf_Conf/Acquisition_Station')
        result[0].text = station

    def set_processing_station(self, station):
        result = self.root_hdr.xpath(
            '//Ipf_Job_Order/Ipf_Conf/Processing_Station')
        result[0].text = station

    def set_source_software_version(self, version):
        result = self.root_hdr.xpath(
            '//Ipf_Job_Order/Ipf_Conf/Dynamic_Processing_Parameters/Processing_Parameter[Name/text()='
            '\'SOURCE_SW_VERSION\']/Value')
        result[0].text = version

    def set_source_software(self, software):
        result = self.root_hdr.xpath(
            '//Ipf_Job_Order/Ipf_Conf/Dynamic_Processing_Parameters/Processing_Parameter[Name/text()='
            '\'SOURCE\']/Value')
        result[0].text = software

    def set_processor_name(self, processor):
        result = self.root_hdr.xpath(
            '//Ipf_Job_Order/Ipf_Conf/Processor_Name')
        result[0].text = processor

    def set_file_type(self, file_type):
        result = self.root_hdr.xpath(
            '//Ipf_Job_Order/Ipf_Conf/Dynamic_Processing_Parameters/Processing_Parameter[Name/text()='
            '\'FILE_TYPE\']/Value')
        result[0].text = file_type

    def set_ds_l0_input(self, filename):
        result = self.root_hdr.xpath(
            '//Ipf_Job_Order/List_of_Ipf_Procs[1]/Ipf_Proc/List_of_Inputs/Input[File_Type/text()=\'L0_DS\']'
            '/List_of_File_Names[1]/File_Name')
        result[0].text = filename
        
    def set_ds_input(self, filename):
        result = self.root_hdr.xpath(
            '//Ipf_Job_Order/List_of_Ipf_Procs[1]/Ipf_Proc/List_of_Inputs/Input[File_Type/text()=\'DS\']'
            '/List_of_File_Names[1]/File_Name')
        result[0].text = filename

    def set_l0_gr_input(self, filenames):
        base_xpath_filenames = '//Ipf_Job_Order/List_of_Ipf_Procs[1]/Ipf_Proc/List_of_Inputs/' \
                               'Input[File_Type/text()=\'L0_GR\']/List_of_File_Names'
        list_of_filenames = self.root_hdr.xpath(base_xpath_filenames)
        filename_node_tpl = self.root_hdr.xpath(base_xpath_filenames + '[1]/File_Name')
        for g in filenames:
            new_node = copy.deepcopy(filename_node_tpl[0])
            new_node.text = g
            list_of_filenames[0].append(new_node)
        list_of_filenames[0].remove(filename_node_tpl[0])
        list_of_filenames[0].attrib['count'] = str(len(filenames))

    def set_gr_input(self, filenames):
        base_xpath_filenames = '//Ipf_Job_Order/List_of_Ipf_Procs[1]/Ipf_Proc/List_of_Inputs/' \
                               'Input[File_Type/text()=\'GR\']/List_of_File_Names'
        list_of_filenames = self.root_hdr.xpath(base_xpath_filenames)
        filename_node_tpl = self.root_hdr.xpath(base_xpath_filenames + '[1]/File_Name')
        for g in filenames:
            new_node = copy.deepcopy(filename_node_tpl[0])
            new_node.text = g
            list_of_filenames[0].append(new_node)
        list_of_filenames[0].remove(filename_node_tpl[0])
        list_of_filenames[0].attrib['count'] = str(len(filenames))

    def set_tl_input(self, filenames):
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

    def set_working_input(self, filename):
        result = self.root_hdr.xpath(
            '//Ipf_Job_Order/List_of_Ipf_Procs[1]/Ipf_Proc/List_of_Inputs/Input[File_Type/'
            'text()=\'WORKING\']/List_of_File_Names[1]/File_Name')
        result[0].text = filename

    def set_global_footprint_ai(self, filename):
        result = self.root_hdr.xpath(
            '//Ipf_Job_Order/List_of_Ipf_Procs[1]/Ipf_Proc/List_of_Inputs/Input[File_Type/'
            'text()=\'GLOBAL_FOOTPRINT_AI_GML\']/List_of_File_Names[1]/File_Name')
        result[0].text = filename

    def set_global_footprint(self, filename):
        result = self.root_hdr.xpath(
            '//Ipf_Job_Order/List_of_Ipf_Procs[1]/Ipf_Proc/List_of_Inputs/Input[File_Type/'
            'text()=\'GLOBAL_FOOTPRINT_GML\']/List_of_File_Names[1]/File_Name')
        result[0].text = filename

    def set_output(self, filename):
        result = self.root_hdr.xpath('//Ipf_Job_Order/List_of_Ipf_Procs[1]/Ipf_Proc/List_of_Outputs/Output/File_Name')
        result[0].text = filename

    def set_l1_tl_sensing_start(self, sensing_start):
        result = self.root_hdr.xpath('//Ipf_Job_Order/Ipf_Conf/Dynamic_Processing_Parameters/'
                                     'Processing_Parameter[Name/text()=\'SENSING_START\']/Value')
        result[0].text = sensing_start

    def set_l1_tl_sensing_stop(self, sensing_stop):
        result = self.root_hdr.xpath('//Ipf_Job_Order/Ipf_Conf/Dynamic_Processing_Parameters/'
                                     'Processing_Parameter[Name/text()=\'SENSING_STOP\']/Value')
        result[0].text = sensing_stop

    def write_to_file(self, filename):
        self.tree_hdr.write(filename, encoding="UTF-8")
