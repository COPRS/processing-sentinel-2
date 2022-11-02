# coding=utf-8
import os
import Constants
from Inventory import Inventory
from InventoryMetadataReader import InventoryMetadataReader
from JobOrderInventoryReader import JobOrderInventoryReader
import FileUtils
from TileMtdReader import TileMtdReader


class InventoryL2A(Inventory):
    """
        Inventory L2A
    """
    def __init__(self, logfile, errfile):
        # init logger
        super(InventoryL2A, self).__init__(logfile, errfile)

    def inventory_l2a_ds(self, dateoflogs, acquisition_station,
                         working_dir, l2a_datastrip, l1c_datastrip):
        print("Doing inventory for L2a Datastrip")
        # Inventory working dirs
        inventory_working_dir = os.path.join(working_dir, "ipf_output_inventory_DS_L2A_" + dateoflogs)
        if not os.path.exists(inventory_working_dir):
            FileUtils.create_directory(inventory_working_dir)
        output_folder_inventory = os.path.join(working_dir, "INV_L2A_DS")
        if not os.path.exists(output_folder_inventory):
            FileUtils.create_directory(output_folder_inventory)
        # Find the JobOrder Template
        inv_mtd_l2a_ds_template_filename = os.path.join(os.path.dirname(os.path.realpath(__file__)),
                                                       "job_order_inv_mtd_DS.xml")
        print("JobOrder inventory L2ADS template : " + inv_mtd_l2a_ds_template_filename)
        jo_reader = JobOrderInventoryReader(inv_mtd_l2a_ds_template_filename)
        jo_reader.set_l2_ds_input(l2a_datastrip)
        jo_reader.set_l1_ds_input(l1c_datastrip)
        jo_reader.set_working_input(inventory_working_dir)
        jo_reader.set_processing_station(Constants.PROCESSING_STATION)
        jo_reader.set_acquisition_station(acquisition_station)
        inventory_job_order = os.path.join(inventory_working_dir, "JobOrderInventory.xml")
        jo_reader.write_to_file(inventory_job_order)
        # Launch process
        script_path = os.path.join(Constants.INVENTORY_L2_SOFT_FOLDER, Constants.INVENTORY_L2_DS_SCRIPT_NAME)
        self.launch_inventory_process([inventory_job_order],"DS_L2A", script_path)
        # Check if inventory has been generated
        if not os.path.exists(os.path.join(l2a_datastrip, "Inventory_Metadata.xml")):
            raise Exception("No Inventory_Metadata.xml file generated in "+l2a_datastrip)
        self._patch_inventory_dates_datastrip(l2a_datastrip)

    def inventory_l2a_tl(self, dateoflogs, acquisition_station, ds_sensing_start,
                         ds_sensing_stop,working_dir,
                         l2a_tile, l1c_tile, l2a_datastrip):
        print("Doing inventory for L2A Tile")
        # Inventory working dirs
        inventory_working_dir = os.path.join(working_dir, "ipf_output_inventory_TL_L2A_" + dateoflogs)
        if not os.path.exists(inventory_working_dir):
            FileUtils.create_directory(inventory_working_dir)
        inv_mtd_l2a_tl_template_filename = os.path.join(os.path.dirname(os.path.realpath(__file__)),
                                                       "job_order_inv_mtd_TL_L2A.xml")
        print("JobOrder inventory L2ATL template : " + inv_mtd_l2a_tl_template_filename)
        jo_reader = JobOrderInventoryReader(inv_mtd_l2a_tl_template_filename)
        jo_reader.set_l2_ds_input(l2a_datastrip)
        jo_reader.set_l1_tl_input([l1c_tile])
        jo_reader.set_l2_tl_input([l2a_tile])
        jo_reader.set_working_input(inventory_working_dir)
        jo_reader.set_processing_station(Constants.PROCESSING_STATION)
        jo_reader.set_acquisition_station(acquisition_station)
        # Read the tile metadata to extract the sensing time
        tl_mtd_file = FileUtils.glob_one(l1c_tile, "S2*TL*xml")
        tl_mtd_rd = TileMtdReader(tl_mtd_file)
        tl_sensing_time = tl_mtd_rd.get_sensing_time()
        print("Inventory: Using sensing time : "+tl_sensing_time)
        jo_reader.set_l2_tl_sensing_start(tl_sensing_time)
        jo_reader.set_l2_tl_sensing_stop(tl_sensing_time)
        inventory_job_order = os.path.join(inventory_working_dir, "JobOrderInventory.xml")
        jo_reader.write_to_file(inventory_job_order)
        # Launch process
        script_path = os.path.join(Constants.INVENTORY_L2_SOFT_FOLDER, Constants.INVENTORY_L2_GR_SCRIPT_NAME)
        self.launch_inventory_process([inventory_job_order], "TL_L2A", script_path)
        if not os.path.exists(os.path.join(l2a_tile, "Inventory_Metadata.xml")):
            raise Exception("No Inventory_Metadata.xml file generated in " + l2a_tile)
        inventory_mtd_reader = InventoryMetadataReader(os.path.join(l2a_tile, "Inventory_Metadata.xml"))
        if inventory_mtd_reader.get_file_id() is None or len(inventory_mtd_reader.get_file_id()) == 0:
            raise Exception("Inventory_Metadata.xml is not correctly generated for " + l2a_tile)
        self._patch_inventory_dates_granule(l2a_tile)
