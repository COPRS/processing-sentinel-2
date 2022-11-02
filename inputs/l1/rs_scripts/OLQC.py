# coding=utf-8
import glob
import logging
import math
import os
import subprocess
from multiprocessing.process import Process

from log_colorizer import make_colored_stream_handler

import Constants
import FileUtils
from TileListFileReader import TileListFileReader
from JobOrderOLQCHandler import JobOrderOLQCHandler


class OLQC(object):
    """
        OLQC
    """

    @staticmethod
    def is_olqc_mode(mode):
        if "OLQC_L1CTL" in mode or "L1CTile" in mode:
            return True
        return False

    def __init__(self, loglevel, logfile, errfile, olqc_script, soft_version):
        # init logger
        self.logger = logging.getLogger("OLQC")
        self._handler = make_colored_stream_handler()
        self._handler.setFormatter(Constants.LOG_FORMATTER)
        self.logger.addHandler(self._handler)
        self.logger.setLevel(loglevel)
        self._logfilename = logfile
        self._errfilename = errfile
        self._olqc_script = olqc_script
        self._soft_version = soft_version

    def do_olqc(self, working_dir, gipp_folder, mode, context_manager, tiles,
                output_idp_infos_file, output_icd_file, dateoflogs, nb_tasks):
        """
            main function
        """

        # Get acquisition station from config
        acquisition_station = context_manager.get_acquisition_station()

        # OLQC L1C TL
        if "OLQC_L1CTL" in mode or "L1CTile":
            self._olqc_l1c_tl(context_manager, gipp_folder, output_idp_infos_file, dateoflogs,
                              acquisition_station, tiles,
                              working_dir, nb_tasks)

    def _olqc_l1c_tl(self, context_manager, gipp_folder, output_idp_infos_file, dateoflogs, acquisition_station,
                     tiles, working_dir, nb_tasks):
        self.logger.info("Doing OLQC for L1C Tile")
        pdi_tl_output_folder = context_manager.get_element("PROC.L1C.PDI_DS_TILE_LIST")
        if pdi_tl_output_folder is None:
            raise Exception("No PROC.L1C.PDI_DS_TILE_LIST in context !!!")
        pdi_tl_output_folder = pdi_tl_output_folder[0]
        pvi_tl_output_folder = context_manager.get_element("PROC.PVI.PDI_DS_PVI_LIST")
        if pvi_tl_output_folder is None:
            raise Exception("No PROC.PVI.PDI_DS_PVI_LIST in context !!!")
        pvi_tl_output_folder = pvi_tl_output_folder[0]
        img_tl_output_folder = context_manager.get_element_in_proc_list("FORMAT_IMG_L1C",
                                                                        "PROC.L1C.LIST.PDI_DS_TILE_LIST")
        if img_tl_output_folder is None:
            raise Exception("No PROC.L1C.LIST.PDI_DS_TILE_LIST for FORMAT_IMG_L1C in context !!!")
        tile_list_file = OLQC._get_tile_list_file(context_manager)
        self.logger.info("Reading tile list file : " + tile_list_file)
        tile_list_file_reader = TileListFileReader(tile_list_file)
        tiles_in = glob.glob(os.path.join(pdi_tl_output_folder, "S2*TL*"))
        tiles_out = []
        if tiles is None:
            tiles_names = tile_list_file_reader.get_all_tile_ids()
        else:
            tiles_names = tile_list_file_reader.get_tile_ids(tiles)

        for t in tiles_names:
            found = False
            for g in tiles_in:
                if t in g:
                    tiles_out.append(g)
                    self._fusion_tile_elements(g, t, pvi_tl_output_folder, img_tl_output_folder)
                    found = True
            if not found:
                raise Exception("Tile "+t+" not found in pdi tile list folder")

        if len(tiles_out) == 0:
            raise Exception("No tiles found in " + os.path.join(pdi_tl_output_folder, "S2*TL*"))

        # get the GIPPs
        gip_olqcpa = FileUtils.glob_one(gipp_folder, "S2*GIP_OLQCPA*zip")
        if gip_olqcpa is None:
            raise Exception("No GIPP OLQCPA found in "+gipp_folder)
        gip_probas = FileUtils.glob_one(gipp_folder, "S2*GIP_PROBAS*xml")
        if gip_probas is None:
            raise Exception("No GIPP PROBAS found in " + gipp_folder)

        # OLQC working dirs
        olqc_working_dir = os.path.join(working_dir, "ipf_output_OLQC_L1C_TL_" + dateoflogs)
        if not os.path.exists(olqc_working_dir):
            FileUtils.create_directory(olqc_working_dir)
        olqc_persistent_folder = os.path.join(olqc_working_dir, "PERSISTENT_RESSOURCES")
        if not os.path.exists(olqc_persistent_folder):
            FileUtils.create_directory(olqc_persistent_folder)
        olqc_reports_folder = os.path.join(olqc_working_dir, "REPORTS")
        if not os.path.exists(olqc_reports_folder):
            FileUtils.create_directory(olqc_reports_folder)
        # Create the Jobs
        jobs = self._split_tiles_jobs(tiles_out, olqc_working_dir, gip_olqcpa, gip_probas, olqc_persistent_folder,
                                      output_idp_infos_file, olqc_reports_folder, nb_tasks)

        # Launch process
        self._launch_olqc_process(jobs, "TL_L1C", self._olqc_script)

    def _fusion_tile_elements(self, tile_folder, tile_name,  pvi_folder, img_folder):
        pvi_tiles_in = glob.glob(os.path.join(pvi_folder, "S2*TL*"))
        pvi_tile_folder = None
        for g in pvi_tiles_in:
            if tile_name in g:
                self.logger.debug("PVI folder " + g)
                pvi_tile_folder = g
        if pvi_tile_folder is None:
            raise Exception("No corresponding tile "+tile_name+" found in "+pvi_folder)
        self.logger.info("Fusioning pdi folder "+tile_folder+" with "+pvi_tile_folder)
        FileUtils.folder_fusion(pvi_tile_folder ,tile_folder )
        img_tiles_in = glob.glob(os.path.join(img_folder, "S2*TL*"))
        img_tile_folder = None
        for g in img_tiles_in:
            if tile_name in g:
                self.logger.debug("IMG folder " + g)
                img_tile_folder = g
        if img_tile_folder is None:
            raise Exception("No corresponding tile " + tile_name + " found in " + img_folder)
        self.logger.info("Fusioning pdi folder " + tile_folder + " with " + img_tile_folder)
        FileUtils.folder_fusion(img_tile_folder, tile_folder)

    @staticmethod
    def _get_tile_list_file(context_manager):
        return context_manager.get_tile_list_file()

    def _launch_olqc_process(self, olqc_job_orders, mode, script_path):
        self.logger.info("Launching OLQC "+mode+" with "+str(len(olqc_job_orders))+" jobs in parallel")
        idx = 1
        processes = []
        for job in olqc_job_orders:
            self.logger.info("Using JobOrder for olqc : "+job)
            logfile = os.path.splitext(self._logfilename)[0]+"_"+str(idx)+".log"
            errfile = os.path.splitext(self._errfilename)[0] + "_" + str(idx) + ".err"
            proc = Process(target=self._launch_one_job, args=(job, logfile, errfile, script_path, mode))
            proc.daemon = False
            proc.start()
            processes.append(proc)
            idx += 1
        for pr in processes:
            pr.join()
            if pr.exitcode != 0:
                raise IOError("Error while launching OLQC !!!")
        self.logger.info("OLQC processes successfully executed")

    def _launch_one_job(self, job_order, logfile, errfile, script_path, mode ):
        self.logger.info("OLQC log file in : " + logfile)
        self.logger.info("OLQC errlog file in : " + errfile)
        olqclogfile = open(logfile, 'wb')
        olqcerrfile = open(errfile, 'wb')
        if not os.path.exists(script_path):
            raise Exception("No OLQC script found under : " + script_path)
        olqc_process = subprocess.Popen([script_path, job_order], stdout=olqclogfile,
                                        stderr=olqcerrfile)
        status = olqc_process.wait()
        if status != 0:
            raise Exception("Error while launching OLQC " + mode)

    def _split_tiles_jobs(self, tiles, working, gip_olqcpa, gip_probas, persistent_folder,
                          idp_infos_file, output_reports, nb_tasks):
        jobs_filenames = []
        nb_tasks_internal = min(len(tiles), nb_tasks)
        self.logger.info("creating "+str(nb_tasks_internal) + " OLQC jobs")
        step = int(math.ceil(len(tiles) * 1.0 / nb_tasks_internal))
        self.logger.info("Nb Tiles per instance : "+str(step))
        for i in range(nb_tasks_internal):
            olqc_job_order = os.path.join(working, "JobOrderOLQC_"+str(i+1)+".xml")
            olqc_report = os.path.join(output_reports, "Report_OLQC_"+str(i+1))
            olqc_working = os.path.join(output_reports, "tmp_OLQC_"+str(i+1))
            FileUtils.create_directory(olqc_report)
            FileUtils.create_directory(olqc_working)
            if i < nb_tasks_internal-1:
                job_tiles = tiles[i*step:(i+1)*step]
            else:
                job_tiles = tiles[i * step:]
            self.logger.info("Writing JobOrder "+olqc_job_order)
            self._write_tl_l1_job_order(olqc_job_order, job_tiles, olqc_working,
                                        gip_olqcpa, gip_probas, persistent_folder,
                                        idp_infos_file, olqc_report)
            jobs_filenames.append(olqc_job_order)
        return jobs_filenames

    def _write_tl_l1_job_order(self, jo_filename, tiles, working, gip_olqcpa, gip_probas, persistent_folder,
                               idp_infos_file, output_report):
        # Find the JobOrder Template
        olqc_l1c_tl_template_filename = os.path.join(os.path.dirname(os.path.realpath(__file__)), "data",
                                                     "JobOrderTemplates", "Joborder_OLQC.xml")
        self.logger.debug("JobOrder olqc L1CTL template : " + olqc_l1c_tl_template_filename)
        jo_reader = JobOrderOLQCHandler(olqc_l1c_tl_template_filename)
        jo_reader.set_processor_name("OLQC")
        jo_reader.set_processor_version(self._soft_version)
        jo_reader.set_log_level("INFO")
        jo_reader.set_processing_station(Constants.PROCESSING_STATION)
        jo_reader.set_task_version(self._soft_version)
        jo_reader.set_gip_olqcpa_input(gip_olqcpa)
        jo_reader.set_gip_probas_input(gip_probas)
        jo_reader.set_safe_inputs(tiles)
        jo_reader.set_working_input(working)
        jo_reader.set_persistent_input(persistent_folder)
        jo_reader.set_idp_infos_input(idp_infos_file)
        jo_reader.set_output_report(output_report)
        # write to file
        jo_reader.write_to_file(jo_filename)

    @staticmethod
    def _verify_olqc_report(folder, list_of_reports):
        qi_data_path = os.path.join(folder, 'QI_DATA')
        if not os.path.isdir(qi_data_path):
            raise Exception('No QI_DATA folder in folder : ' + folder)
        for report in list_of_reports:
            # Find OLQC reports
            report_file = FileUtils.glob_one(qi_data_path, "S2*"+report+"_report.xml")
            if report_file is None:
                raise Exception("No "+report+" in folder : " + folder)

    def __verify_tile(self, tl):
        pass
