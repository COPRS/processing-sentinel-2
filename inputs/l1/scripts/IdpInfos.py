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
