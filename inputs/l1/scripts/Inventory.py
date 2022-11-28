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
from DatastripReader import DatastripReader
from InventoryMetadataReader import InventoryMetadataReader
from JobOrderInventoryReader import JobOrderInventoryReader
from TileMtdReader import TileMtdReader


class Inventory(object):
    """
        Inventory
    """
    def __init__(self, loglevel, logfile, errfile, gr_inventory_script, ds_inventory_script, soft_version):
        # init logger
        self.logger = logging.getLogger("Inventory")
        handler = make_colored_stream_handler()
        handler.setFormatter(Constants.LOG_FORMATTER)
        self.logger.addHandler(handler)
        self.logger.setLevel(loglevel)
        self._logfilename = logfile
        self._errfilename = errfile
        self._ds_inventory_script = ds_inventory_script
        self._gr_inventory_script = gr_inventory_script
        self._soft_version = soft_version

    def _launch_inventory_process(self, inventory_job_orders, mode, script_path):
        self.logger.info("Launching inventory "+mode+" with "+str(len(inventory_job_orders))+" jobs in parallel")
        idx = 0
        processes = []
        for job in inventory_job_orders:
            self.logger.info("Using JobOrder for inventory : "+job)
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
                raise IOError("Error while launching inventory !!!")
        self.logger.info("Inventory processes successfully executed")

    def _launch_one_job(self, job_order, logfile, errfile, script_path, mode ):
        self.logger.info("Inventory log file in : " + logfile)
        self.logger.info("Inventory errlog file in : " + errfile)
        invlogfile = open(logfile, 'wb')
        inverrfile = open(errfile, 'wb')
        if not os.path.exists(script_path):
            raise Exception("No inventory script found under : " + script_path)
        inventory_process = subprocess.Popen([script_path, job_order], stdout=invlogfile,
                                             stderr=inverrfile)
        status = inventory_process.wait()
        if status != 0:
            raise Exception("Error while launching inventory " + mode)

    def split_granules_jobs(self, granules, working, output, ds_input, acquisition_station, nb_tasks, granule_type):
        jobs_filenames = []
        real_nb_tasks = nb_tasks
        if len(granules) < nb_tasks:
            real_nb_tasks = len(granules)
        step = int(math.ceil(float(len(granules)) / real_nb_tasks))
        self.logger.debug("Nb granule per instance : "+str(step))
        for i in range(real_nb_tasks):
            inventory_job_order = os.path.join(working, "JobOrderInventory_"+str(i+1)+".xml")
            if i < nb_tasks:
                job_granules = granules[i*step:(i+1)*step]
            else:
                job_granules = granules[i * step:]
            if granule_type.startswith("L1"):
                self._write_gr_l1_job_order(inventory_job_order, job_granules, working,
                                            acquisition_station,
                                            output, ds_input, granule_type)
            else:
                self._write_gr_l0_job_order(inventory_job_order, job_granules, working,
                                            acquisition_station,
                                            output, ds_input)
            jobs_filenames.append(inventory_job_order)
        return jobs_filenames

    def split_tiles_jobs(self, tiles, working, acquisition_station,
                         sensing_start_ds, sensing_stop_ds,
                         output, ds_input, nb_tasks):
        jobs_filenames = []
        nb_tasks = len(tiles)
        step = 1
        self.logger.debug("Nb Tiles per instance : "+str(step))
        for i in range(len(tiles)):
            inventory_job_order = os.path.join(working, "JobOrderInventory_"+str(i+1)+".xml")
            job_granules = [tiles[i]]
            # Read the tile metadata to extract the sensing time
            tl_mtd_file = FileUtils.glob_one(tiles[i], "S2*TL*xml")
            tl_mtd_rd = TileMtdReader(tl_mtd_file)
            tl_sensing_time = tl_mtd_rd.get_sensing_time()
            self.logger.debug("Using sensing time for tile " + os.path.basename(tiles[i]) + " : " + tl_sensing_time)
            # write the jobOrder
            self._write_tl_l1_job_order(inventory_job_order, job_granules, working, acquisition_station,
                                        tl_sensing_time, tl_sensing_time,
                                        output, ds_input)
            jobs_filenames.append(inventory_job_order)
        return jobs_filenames

    def _write_tl_l1_job_order(self, jo_filename, granules, working,
                               acquisition_station, sensing_start, sensing_stop,
                               output, ds_input):
        # Find the JobOrder Template
        inv_mtd_l1c_tl_template_filename = os.path.join(os.path.dirname(os.path.realpath(__file__)), "data",
                                                        "JobOrderTemplates", "job_order_inv_mtd_TL_L1C.xml")
        self.logger.debug("JobOrder inventory L1CTL template : " + inv_mtd_l1c_tl_template_filename)
        jo_reader = JobOrderInventoryReader(inv_mtd_l1c_tl_template_filename)
        jo_reader.set_tl_input(granules)
        jo_reader.set_working_input(working)
        jo_reader.set_acquisition_station(acquisition_station)
        jo_reader.set_processing_station(Constants.PROCESSING_STATION)
        jo_reader.set_source_software_version(self._soft_version)
        jo_reader.set_l1_tl_sensing_start(sensing_start)
        jo_reader.set_l1_tl_sensing_stop(sensing_stop)
        jo_reader.set_ds_input(ds_input)
        jo_reader.write_to_file(jo_filename)

    def _write_gr_l1_job_order(self, jo_filename, granules, working,
                               acquisition_station,output, ds_input, granule_type):
        # Find the JobOrder Template
        inv_mtd_l1a_gr_template_filename = os.path.join(os.path.dirname(os.path.realpath(__file__)), "data",
                                                        "JobOrderTemplates", "job_order_inv_mtd_GR_L1A.xml")
        self.logger.debug("JobOrder inventory L1AGR template : " + inv_mtd_l1a_gr_template_filename)
        jo_reader = JobOrderInventoryReader(inv_mtd_l1a_gr_template_filename)
        jo_reader.set_gr_input(granules)
        jo_reader.set_working_input(working)
        jo_reader.set_source_software_version(self._soft_version)
        jo_reader.set_acquisition_station(acquisition_station)
        jo_reader.set_processing_station(Constants.PROCESSING_STATION)
        jo_reader.set_ds_input(ds_input)
        if granule_type == "L1A":
            jo_reader.set_file_type("MSI_L1A_GR")
            jo_reader.set_processor_name("l1a")
            jo_reader.set_source_software("L1A_Processor")
        else:
            jo_reader.set_file_type("MSI_L1B_GR")
            jo_reader.set_processor_name("l1b")
            jo_reader.set_source_software("L1B_Processor")
        jo_reader.write_to_file(jo_filename)

    def _write_gr_l0_job_order(self, jo_filename, granules, working,
                               acquisition_station,output, ds_input):
        # Find the JobOrder Template
        inv_mtd_l0_gr_template_filename = os.path.join(os.path.dirname(os.path.realpath(__file__)), "data",
                                                       "JobOrderTemplates", "Joborder_InventoryGRL0C.xml")
        self.logger.debug("JobOrder inventory L0GR template : " + inv_mtd_l0_gr_template_filename)
        jo_reader = JobOrderInventoryReader(inv_mtd_l0_gr_template_filename)
        jo_reader.set_l0_gr_input(granules)
        jo_reader.set_working_input(working)
        jo_reader.set_source_software_version(self._soft_version)
        jo_reader.set_acquisition_station(acquisition_station)
        jo_reader.set_processing_station(Constants.PROCESSING_STATION)
        jo_reader.set_output(output)
        jo_reader.set_ds_l0_input(ds_input)
        jo_reader.write_to_file(jo_filename)

    def _get_global_footprint_ai(self, context_manager):
        global_footprint_ai_folder = context_manager.get_element_in_proc_list("UPDATE_LOC",
                                                                              "PROC.LIST.VECTOR_DS_FOOTPRINT")
        if global_footprint_ai_folder is None:
            raise Exception("No VECTOR_DS_FOOTPRINT found in context for UPDATE_LOC")
        global_footprint_ai_files = glob.glob(os.path.join(global_footprint_ai_folder, "GlobalFootprintAI.gml"))
        if len(global_footprint_ai_files) != 1:
            raise Exception("Error in fecthing GlobalFootprintAI in "+global_footprint_ai_folder)
        global_footprint_ai_file = global_footprint_ai_files[0]
        self.logger.info("Using global footprint AI file : "+global_footprint_ai_file)
        return global_footprint_ai_file

    def _get_global_footprint(self, context_manager):
        global_footprint_folder = context_manager.get_element_in_proc_list("FORMAT_METADATA_DS_L1B",
                                                                              "PROC.L1B.LIST.VECTOR")
        if global_footprint_folder is None:
            raise Exception("No VECTOR_DS_FOOTPRINT found in context for FORMAT_METADATA_DS_L1B")
        global_footprint_files = glob.glob(os.path.join(global_footprint_folder, "GlobalFootprint.gml"))
        if len(global_footprint_files) != 1:
            raise Exception("Error in fetching GlobalFootprint in "+global_footprint_folder)
        global_footprint_file = global_footprint_files[0]
        self.logger.info("Using global footprint file : "+global_footprint_file)
        return global_footprint_file

    def _verify_olqc_report(self, folder, list_of_reports):
        qi_data_path = os.path.join(folder, 'QI_DATA')
        if not os.path.isdir(qi_data_path):
            raise Exception('No QI_DATA folder in folder : ' + folder)
        for report in list_of_reports:
            # Find OLQC reports
            report_file = FileUtils.glob_one(qi_data_path, "S2*"+report+"_report.xml")
            if report_file is None:
                raise Exception("No "+report+" in folder : " + folder)

    def _copy_olqc_report(self, folder, list_of_reports, output_folder):
        qi_data_path = os.path.join(folder, 'QI_DATA')
        if not os.path.isdir(qi_data_path):
            raise Exception('No QI_DATA folder in folder : ' + folder)
        for report in list_of_reports:
            # Find OLQC reports
            report_file = FileUtils.glob_one(qi_data_path, "S2*"+report+"_report.xml")
            if report_file is None:
                raise Exception("No "+report+" in folder : " + folder)
            FileUtils.copy_file(report_file, os.path.join(output_folder, os.path.basename(report_file)))

    def _patch_inventory_dates_datastrip(self, datastrip_out):
        self.logger.debug("Patching inventory mtd dates for datastrip : "+datastrip_out)
        # read mtd ds
        datastrip_xml_out = glob.glob(os.path.join(datastrip_out, "S2*DS*xml"))[0]
        reader_mt = DatastripReader(datastrip_xml_out)
        sensing_start = reader_mt.get_sensing_start_utc()
        sensing_stop = reader_mt.get_sensing_stop_utc()
        inventory_xml_out = os.path.join(datastrip_out, "Inventory_Metadata.xml")
        reader_inventory = InventoryMetadataReader(inventory_xml_out)
        reader_inventory.set_val_start_date(sensing_start)
        reader_inventory.set_val_stop_date(sensing_stop)
        reader_inventory.write_to_file(inventory_xml_out)

    def _patch_inventory_dates_granule(self, granule_out):
        self.logger.debug("Patching inventory mtd dates for granule : "+granule_out)
        # read mtd gr
        granule_xml_out = glob.glob(os.path.join(granule_out, "S2*xml"))[0]
        reader_mt = DatastripReader(granule_xml_out)
        sensing_time = reader_mt.get_sensing_time_utc()
        inventory_xml_out = os.path.join(granule_out, "Inventory_Metadata.xml")
        reader_inventory = InventoryMetadataReader(inventory_xml_out)
        reader_inventory.set_val_start_date(sensing_time)
        reader_inventory.set_val_stop_date(sensing_time)
        reader_inventory.write_to_file(inventory_xml_out)
