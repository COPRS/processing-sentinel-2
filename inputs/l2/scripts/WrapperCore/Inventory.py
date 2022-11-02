# coding=utf-8
import glob
import os
import Constants
import subprocess
from multiprocessing.process import Process

from JobOrderInventoryReader import JobOrderInventoryReader
from DatastripReader import DatastripReader
from InventoryMetadataReader import InventoryMetadataReader


class Inventory(object):
    """
        Inventory
    """
    def __init__(self, logfile, errfile):
        # init logger
        self._logfilename = logfile
        self._errfilename = errfile

    def launch_inventory_process(self, inventory_job_orders, mode, script_path):
        print("Launching inventory "+mode)
        idx = 0
        processes = []
        for job in inventory_job_orders:
            print("Using JobOrder for inventory : "+job)
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

    def _launch_one_job(self, job_order, logfile, errfile, script_path, mode ):
        print("Inventory log file in : " + logfile)
        print("Inventory errlog file in : " + errfile)
        invlogfile = open(logfile, 'wb')
        inverrfile = open(errfile, 'wb')
        if not os.path.exists(script_path):
            raise Exception("No inventory script found under : " + script_path)
        inventory_process = subprocess.Popen([script_path, job_order], stdout=invlogfile,
                                             stderr=inverrfile)
        status = inventory_process.wait()
        if status != 0:
            raise Exception("Error while launching inventory " + mode)

    def write_tl_l2_job_order(self, jo_filename, tile, working,
                              acquisition_station, sensing_start, sensing_stop,
                              ds_input, l1_tile_input):
        # Find the JobOrder Template
        inv_mtd_l2a_tl_template_filename = os.path.join(os.path.dirname(os.path.realpath(__file__)), "job_order_inv_mtd_TL_L2A.xml")
        print("JobOrder inventory L1CTL template : " + inv_mtd_l2a_tl_template_filename)
        jo_reader = JobOrderInventoryReader(inv_mtd_l2a_tl_template_filename)
        jo_reader.set_l2_tl_input(tile)
        jo_reader.set_working_input(working)
        jo_reader.set_acquisition_station(acquisition_station)
        jo_reader.set_processing_station(Constants.PROCESSING_STATION)
        jo_reader.set_l2_tl_sensing_start(sensing_start)
        jo_reader.set_l2_tl_sensing_stop(sensing_stop)
        jo_reader.set_l2_ds_input(ds_input)
        jo_reader.set_l1_tl_input(l1_tile_input)
        jo_reader.write_to_file(jo_filename)

    def _patch_inventory_dates_datastrip(self, datastrip_out):
        print("Patching inventory mtd dates for datastrip : "+datastrip_out)
        # read mtd ds
        datastrip_xml_out = glob.glob(os.path.join(datastrip_out, "MTD_DS.xml"))[0]
        reader_mt = DatastripReader(datastrip_xml_out)
        sensing_start = reader_mt.get_sensing_start_utc()
        sensing_stop = reader_mt.get_sensing_stop_utc()
        inventory_xml_out = os.path.join(datastrip_out, "Inventory_Metadata.xml")
        reader_inventory = InventoryMetadataReader(inventory_xml_out)
        reader_inventory.set_val_start_date(sensing_start)
        reader_inventory.set_val_stop_date(sensing_stop)
        reader_inventory.write_to_file(inventory_xml_out)

    def _patch_inventory_dates_granule(self, granule_out):
        print("Patching inventory mtd dates for granule : "+granule_out)
        # read mtd gr
        granule_xml_out = glob.glob(os.path.join(granule_out, "MTD_TL.xml"))[0]
        reader_mt = DatastripReader(granule_xml_out)
        sensing_time = reader_mt.get_sensing_time_utc()
        inventory_xml_out = os.path.join(granule_out, "Inventory_Metadata.xml")
        reader_inventory = InventoryMetadataReader(inventory_xml_out)
        reader_inventory.set_val_start_date(sensing_time)
        reader_inventory.set_val_stop_date(sensing_time)
        reader_inventory.write_to_file(inventory_xml_out)
