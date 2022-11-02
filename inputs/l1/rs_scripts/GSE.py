# coding=utf-8
import glob
import os
import shutil
import subprocess

from lxml import etree as et

import FileUtils


class GSE(object):

    def __init__(self, exe_dir, version, gipp_dir, work_dir, sad_backup_dir, datastrip, sensing_start, sensing_stop,
                 logfile, errfile):
        self._exe_dir = exe_dir
        self._version = version
        self._gipp_dir = gipp_dir
        self._work_dir = work_dir
        self._backup_dir = sad_backup_dir
        self._datastrip = datastrip
        self._sensing_start = sensing_start
        self._sensing_stop = sensing_stop
        self._logfilename = logfile
        self._errfilename = errfile

    def run(self):
        # Find the needed GIPPs
        gip_spamods = glob.glob(os.path.join(self._gipp_dir, "*GIP_SPAMOD*xml"))
        if len(gip_spamods) == 0:
            raise Exception("GSE: No SPAMOD in " + self._gipp_dir + " !!!!")
        if len(gip_spamods) != 1:
            raise Exception("GSE: More than one SPAMOD in " + self._gipp_dir + " : cannot decide")
        the_gip_spamod = gip_spamods[0]

        # patch JobOrder
        gse_working_dir = os.path.join(self._work_dir, "GSE")
        gse_backup_dir = os.path.join(gse_working_dir, "BACKUP_SAD")
        gse_job_order = os.path.join(gse_working_dir, "JobOrderGSE.xml")
        if not os.path.exists(gse_working_dir):
            FileUtils.create_directory(gse_working_dir)
        if not os.path.exists(gse_backup_dir):
            FileUtils.create_directory(gse_backup_dir)
        shutil.copy(os.path.join(os.path.dirname(os.path.realpath(__file__)), "data",
                                 "JobOrderTemplates", "job_order_GSE_template.xml"),
                    gse_job_order)
        tree_hdr = et.parse(gse_job_order)
        root_hdr = tree_hdr.getroot()
        sensing_start_node = root_hdr.xpath("//Ipf_Job_Order/Ipf_Conf/Sensing_Time/Start")[0]
        sensing_start_node.text = self._sensing_start
        sensing_stop_node = root_hdr.xpath("//Ipf_Job_Order/Ipf_Conf/Sensing_Time/Stop")[0]
        sensing_stop_node.text = self._sensing_stop
        version_node = root_hdr.xpath("//List_of_Ipf_Procs/Ipf_Proc/Task_Version")[0]
        version_node.text = str(self._version)
        filenames_nodes = root_hdr.xpath("//List_of_Ipf_Procs/Ipf_Proc/List_of_Inputs/Input"
                                         "/List_of_File_Names/File_Name")
        for f in filenames_nodes:
            if f.text is not None:
                f.text = f.text.replace("***DS_ANC_DATA***", os.path.join(self._datastrip, "ANC_DATA"))
                f.text = f.text.replace("***GIP_SPAMOD***", the_gip_spamod)
        filenames_nodes = root_hdr.xpath(
            "//List_of_Ipf_Procs/Ipf_Proc/List_of_Outputs/Output/File_Name")
        for f in filenames_nodes:
            if f.text is not None:
                f.text = f.text.replace("***DS_ANC_DATA***", os.path.join(self._datastrip, "ANC_DATA"))
                f.text = f.text.replace("***WORK***", gse_working_dir)
                f.text = f.text.replace("***DS_ANC_BACKUP_DATA***", self._backup_dir)

        tree_hdr.write(gse_job_order, encoding="UTF-8")

        # launch the process
        logfile = open(self._logfilename, 'wb')
        errfile = open(self._errfilename, 'wb')
        gse_process = subprocess.Popen([
            os.path.join(self._exe_dir, "GSE", self._version, "scripts", "GSE.bash"), gse_job_order]
                        , stdout=logfile, stderr=errfile)
        gse_process.wait()
        logfile.close()
        errfile.close()
        if gse_process.returncode != 0:
            raise Exception("Problem launching GSE !!! check logs : "+self._logfilename + " / "+self._errfilename)
