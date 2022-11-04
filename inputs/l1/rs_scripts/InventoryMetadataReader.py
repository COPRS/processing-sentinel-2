# coding=utf-8
from lxml import etree as ET


class InventoryMetadataReader(object):

    def __init__(self,filename):
        self.tree_hdr = ET.parse(filename)
        self.filename = filename
        # Remove namespace prefixes
        for elem in self.tree_hdr.getiterator():
            elem.tag = ET.QName(elem).localname
        self.root_hdr = self.tree_hdr.getroot()
        self.my_namespaces = self.root_hdr.nsmap

    def get_file_id(self):
        result = self.root_hdr.xpath('//File_ID')
        return result[0].text

    def set_file_id(self, file_id):
        result = self.root_hdr.xpath('//File_ID')
        result[0].text = file_id

    def get_file_name(self):
        result = self.root_hdr.xpath('//File_Name')
        return result[0].text

    def set_file_name(self, file_name):
        result = self.root_hdr.xpath('//File_Name')
        result[0].text = file_name

    def get_file_type(self):
        result = self.root_hdr.xpath('//File_Type')
        return result[0].text

    def set_file_type(self, file_type):
        result = self.root_hdr.xpath('//File_Type')
        result[0].text = file_type

    def set_data_size(self, data_size):
        result = self.root_hdr.xpath('//Data_Size')
        result[0].text = str(data_size)

    def set_val_start_date(self, start_date):
        result = self.root_hdr.xpath('//Validity_Start')
        result[0].text = str(start_date)

    def set_val_stop_date(self, stop_date):
        result = self.root_hdr.xpath('//Validity_Stop')
        result[0].text = str(stop_date)

    def write_to_file(self, filename):
        self.tree_hdr.write(filename, encoding="ISO-8859-1", standalone=False)
