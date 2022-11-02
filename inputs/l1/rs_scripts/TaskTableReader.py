# coding=utf-8

from lxml import etree as ET


class TaskTableReader(object):

    def __init__(self, filename):
        self.tree_hdr = ET.parse(filename)
        self.root_hdr = self.tree_hdr.getroot()
        self.filename = filename

    def replace_pool_count(self, newcount):
        """

        """
        pool_list = self.root_hdr.find("List_of_Pools")
        pool_list.attrib["count"] = str(newcount)

    def set_fake_mode(self, fakemode=True):
        """
            set fake mode
        """
        test_node = self.root_hdr.find("Test")
        if fakemode:
            test_node.text = "true"
        else:
            test_node.text = "false"

    def set_IDPSCs_versions(self, json_versions):
        #get all the Tasks
        task_nodes = self.root_hdr.findall("./List_of_Pools/Pool/List_of_Tasks/Task")
        for f in task_nodes:
            task_name =f.find("Name").text
            if task_name.startswith("OLQC"):
                task_name = "OLQC"
            if not task_name in json_versions:
                raise Exception("No "+task_name+" defined in IDPSC_EXE_export for this chain ")
            f.find("Version").text = json_versions[task_name]

    def write_to_file(self):
        self.tree_hdr.write(self.filename)
