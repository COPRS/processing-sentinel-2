# coding=utf-8
import glob
import os
import shutil
import subprocess

import Constants
import FileUtils
from Inventory import Inventory
from InventoryMetadataReader import InventoryMetadataReader
from JobOrderInventoryReader import JobOrderInventoryReader
from TileListFileReader import TileListFileReader


class InventoryL1C(Inventory):
    """
        Inventory L1C
    """
    def __init__(self, loglevel, logfile, errfile, gr_inventory_script, ds_inventory_script,
                 inventory_soft_version):
        # init logger
        super(InventoryL1C, self).__init__(loglevel, logfile, errfile, gr_inventory_script, ds_inventory_script,
                                           inventory_soft_version)

    @staticmethod
    def is_inventory_mode(mode):
        # Inventory L1C DS + TL
        if "OLQC_L1CDS" in mode or "L1CTile" in mode:
            return True
        return False

    def do_inventory(self, working_dir, gipp_folder, mode, context_manager, tiles,
                     output_idp_infos_file, output_icd_file, dateoflogs, main_scheme, nb_tasks):
        """
            main function
        """

        # Get acquisition station from config
        acquisition_station = context_manager.get_acquisition_station()

        # Inventory L1C DS
        if "OLQC_L1CDS" in mode:
            self._inventory_l1c_ds(context_manager, dateoflogs, gipp_folder,
                                   acquisition_station, output_icd_file, output_idp_infos_file,
                                   working_dir)

        # Inventory L1C TL
        if "OLQC_L1CTL" in mode or "L1CTile" in mode:
            tile_list_file = context_manager.get_tile_list_file()
            self.logger.info("Reading tile list file : " + tile_list_file)
            tile_list_file_reader = TileListFileReader(tile_list_file)
            if tiles is None:
                tiles_names = tile_list_file_reader.get_all_tile_ids()
            else:
                tiles_names = tile_list_file_reader.get_tile_ids(tiles)
            self._inventory_l1c_tl(context_manager, tiles_names, dateoflogs,
                                   acquisition_station, working_dir, nb_tasks)
            self._generate_tci_product(context_manager, tiles_names, dateoflogs, working_dir, main_scheme)

    def _inventory_l1c_ds(self, context_manager, dateoflogs, gipp_folder, acquisition_station,
                          output_icd_file, output_idp_infos_file,
                          working_dir):
        self.logger.info("Doing inventory for L1C Datastrip")
        pdi_ds_output_folder = context_manager.get_element("PROC.L1C.PDI_DS")
        if pdi_ds_output_folder is None:
            raise Exception("No PROC.L1C.PDI_DS in context !!!")
        datastrips_out = glob.glob(os.path.join(pdi_ds_output_folder[0], "S2*DS*"))
        if len(datastrips_out) != 1:
            raise Exception(
                "No or more than one datastrips found in " + os.path.join(pdi_ds_output_folder[0], "S2*"))
        datastrip_out = datastrips_out[0]
        self.__verify_datastrip(datastrip_out)
        # Inventory working dirs
        inventory_working_dir = os.path.join(working_dir, "ipf_output_inventory_DS_L1C_" + dateoflogs)
        if not os.path.exists(inventory_working_dir):
            FileUtils.create_directory(inventory_working_dir)
        output_folder_inventory = os.path.join(working_dir, "INV_L1C_DS")
        if not os.path.exists(output_folder_inventory):
            FileUtils.create_directory(output_folder_inventory)
        # Find the JobOrder Template
        inv_mtd_l1c_ds_template_filename = os.path.join(os.path.dirname(os.path.realpath(__file__)), "data",
                                                        "JobOrderTemplates",
                                                        "job_order_inv_mtd_DS_L1C.xml")
        self.logger.debug("JobOrder inventory L1CDS template : " + inv_mtd_l1c_ds_template_filename)
        jo_reader = JobOrderInventoryReader(inv_mtd_l1c_ds_template_filename)
        jo_reader.set_ds_input(datastrip_out)
        jo_reader.set_working_input(inventory_working_dir)
        jo_reader.set_processing_station(Constants.PROCESSING_STATION)
        jo_reader.set_source_software_version(self._soft_version)
        jo_reader.set_acquisition_station(acquisition_station)
        jo_reader.set_global_footprint(self._get_global_footprint(context_manager))
        # No output really ?
        # jo_reader.set_output(output_folder_inventory)
        inventory_job_order = os.path.join(inventory_working_dir, "JobOrderInventory.xml")
        jo_reader.write_to_file(inventory_job_order)
        # Launch process
        self._launch_inventory_process([inventory_job_order], "DS_L1C", self._ds_inventory_script)
        # Check if inventory has been generated
        if not os.path.exists(os.path.join(datastrip_out, "Inventory_Metadata.xml")):
            raise Exception("No Inventory_Metadata.xml file generated in " + datastrip_out)
        # Patch the dates
        self._patch_inventory_dates_datastrip(datastrip_out)
        # Tar the datastrip
        ds_basename = os.path.basename(datastrip_out)
        out_file = os.path.join(output_folder_inventory, ds_basename)
        self.logger.info("Tar the L1C DS : "+datastrip_out + " to "+out_file)
        # copy the datastrip
        FileUtils.copy_directory_recursive(datastrip_out, out_file)
        # List datastrips in output of inventory
        list_of_ds_produced = glob.glob(os.path.join(output_folder_inventory, "S2*DS*"))
        if len(list_of_ds_produced) == 0:
            raise Exception("No datastrip produced in inventory DS L1C : " + output_folder_inventory)
        self.logger.info("Inventory DS L1C done, products can be found in : " + output_folder_inventory)
        # Copy OLQC reports
        self.__copy_olqc_datastrip(datastrip_out, output_folder_inventory)
        # Copy KPI files
        output_folder_kpi = os.path.join(working_dir, "KPI_L1C_DS")
        if not os.path.exists(output_folder_kpi):
            FileUtils.create_directory(output_folder_kpi)
        shutil.copyfile(output_idp_infos_file, os.path.join(output_folder_kpi, os.path.basename(datastrip_out) +
                                                            "_" + dateoflogs + "_" + os.path.basename(
            output_idp_infos_file)))
        shutil.copyfile(output_icd_file, os.path.join(output_folder_kpi, os.path.basename(datastrip_out) +
                                                      "_" + dateoflogs + "_" + os.path.basename(output_icd_file)))
        # Copy the datastrip metadata to the KPI folder
        datastrip_xml_out = glob.glob(os.path.join(datastrip_out, "S2*DS*xml"))[0]
        shutil.copyfile(datastrip_xml_out, os.path.join(output_folder_kpi, os.path.basename(datastrip_xml_out)))
        # Copy the PROBAS file to the KPI
        probas_in = glob.glob(os.path.join(gipp_folder, "S2*PROBAS*"))
        for f in probas_in:
            shutil.copyfile(f, os.path.join(output_folder_kpi, os.path.basename(f)))

    def _inventory_l1c_tl(self, context_manager, tiles_names, dateoflogs, acquisition_station, working_dir, nb_tasks):
        self.logger.info("Doing inventory for L1C Tile")
        pdi_ds_output_folder = context_manager.get_element("PROC.PDI_DS")
        if pdi_ds_output_folder is None:
            raise Exception("No PROC.PDI_DS in context !!!")
        datastrips_out = glob.glob(os.path.join(pdi_ds_output_folder[0], "S2*DS*"))
        if len(datastrips_out) != 1:
            raise Exception(
                "No or more than one datastrips found in " + os.path.join(pdi_ds_output_folder[0], "S2*"))
        datastrip_out = datastrips_out[0]
        pdi_gr_output_folder = context_manager.get_element("PROC.L1C.PDI_DS_TILE_LIST")
        if pdi_gr_output_folder is None:
            raise Exception("No PROC.L1C.PDI_DS_TILE_LIST in context !!!")
        tile_list_file = context_manager.get_tile_list_file()
        self.logger.info("Reading tile list file : " + tile_list_file)
        tiles_in = glob.glob(os.path.join(pdi_gr_output_folder[0], "S2*TL*"))
        tiles_out = []
        for t in tiles_names:
            found = False
            for g in tiles_in:
                if t in g:
                    tiles_out.append(g)
                    found = True
            if not found:
                raise Exception("Tile " + t + " not found in pdi tile list folder")

        tile_finalize_tl_list_output = context_manager.get_element_in_proc_list("TILE_FINALIZE",
                                                                                "PROC.LIST.PDI_DS_TILE_LIST")
        print(tile_finalize_tl_list_output)
        if tile_finalize_tl_list_output is None:
            raise Exception("No TILE_FINALIZE found in PROC.LIST.PDI_DS_TILE_LIST")
        for tl in tiles_out:
            self.logger.debug("Handling tile : "+tl)
            self.__verify_tile(tl)
            # copy the footprint.gml file from TILE_FINALIZE
            FileUtils.copy_tree(os.path.join(tile_finalize_tl_list_output, os.path.basename(tl)), tl, max_depth=5,
                                pattern="footprint.*")
        # Inventory working dirs
        inventory_working_dir = os.path.join(working_dir, "ipf_output_inventory_TL_L1C_" + dateoflogs)
        if not os.path.exists(inventory_working_dir):
            FileUtils.create_directory(inventory_working_dir)
        output_folder_inventory = os.path.join(working_dir, "INV_L1C_TL")
        if not os.path.exists(output_folder_inventory):
            FileUtils.create_directory(output_folder_inventory)
        # Create the job orders
        sensing_start = context_manager.get_sensings_ms()[0]
        sensing_stop = context_manager.get_sensings_ms()[1]
        jobs = self.split_tiles_jobs(tiles_out, inventory_working_dir, acquisition_station,
                                     sensing_start, sensing_stop,
                                     output_folder_inventory, datastrip_out,
                                     nb_tasks)
        # Launch process
        self._launch_inventory_process(jobs, "TL_L1C", self._gr_inventory_script)
        # Tar the granules
        for tl in tiles_out:
            # Check if inventory has been generated
            if not os.path.exists(os.path.join(tl, "Inventory_Metadata.xml")):
                raise Exception("No Inventory_Metadata.xml file generated in " + tl)
            inventory_mtd_reader = InventoryMetadataReader(os.path.join(tl, "Inventory_Metadata.xml"))
            if inventory_mtd_reader.get_file_id() is None or len(inventory_mtd_reader.get_file_id()) == 0:
                raise Exception("Inventory_Metadata.xml is not correctly generated for " + tl)
            # Patch the dates
            self._patch_inventory_dates_granule(tl)
            # Remove VECTOR folder
            if os.path.isdir(os.path.join(tl, "QI_DATA", "VECTOR")):
                shutil.rmtree(os.path.join(tl, "QI_DATA", "VECTOR"))
            tile_filename = os.path.join(output_folder_inventory, os.path.basename(tl))
            FileUtils.copy_directory_recursive(tl, tile_filename)
        # List produced
        list_of_tl_produced = glob.glob(os.path.join(output_folder_inventory, "S2*TL*"))
        if len(list_of_tl_produced) == 0:
            raise Exception("No tiles produced in inventory TL L1C : " + output_folder_inventory)
        # Copy OLQC reports
        for tl in tiles_out:
            self.__copy_olqc_tile(tl, output_folder_inventory)
        # Copy KPI files
        output_folder_kpi = os.path.join(working_dir, "KPI_L1C_TL")
        if not os.path.exists(output_folder_kpi):
            FileUtils.create_directory(output_folder_kpi)
        self.logger.info("Inventory TL L1C done, products can be found in : " + output_folder_inventory)

    def _generate_tci_product(self, context_manager, tiles_names, dateoflogs, working_dir, main_scheme):
        self.logger.info("Generating TCIs")
        pdi_gr_output_folder = context_manager.get_element("PROC.L1C.PDI_DS_TILE_LIST")
        if pdi_gr_output_folder is None:
            raise Exception("No PROC.L1C.PDI_DS_TILE_LIST in context !!!")
        tci_tl_output_folder = context_manager.get_element("PROC.PVI.PDI_DS_TCI_LIST")
        if tci_tl_output_folder is None:
            raise Exception("No PROC.PVI.PDI_DS_TCI_LIST in context !!!")
        tiles_in = glob.glob(os.path.join(pdi_gr_output_folder[0], "S2*TL*"))
        tiles_out = []
        for t in tiles_names:
            found = False
            for g in tiles_in:
                if t in g:
                    tiles_out.append(g)
                    found = True
            if not found:
                raise Exception("Tile " + t + " not found in pdi tile list folder")
        if len(tiles_out) == 0:
            raise Exception("No tiles found in " + os.path.join(pdi_gr_output_folder[0], "S2*TL*"))
        output_folder_inventory = os.path.join(working_dir, "INV_L1C_TL")
        if not os.path.exists(output_folder_inventory):
            FileUtils.create_directory(output_folder_inventory)
        # Inventory working dirs
        inventory_working_dir = os.path.join(working_dir, "ipf_output_inventory_TL_TCI_" + dateoflogs)
        for tl in tiles_out:
            self.logger.debug("Handling TCI tile : " + tl)
            tci_filename = os.path.basename(tl).replace("MSI_L1C_TL", "MSI_L1C_TC")
            # Find the jp2 file fr the tile
            tl_tci_jp2 = glob.glob(os.path.join(tci_tl_output_folder[0],
                                                tci_filename + ".jp2"))
            if len(tl_tci_jp2) == 0:
                raise Exception("No JP2 file found in "+os.path.join(tci_tl_output_folder[0],
                                tci_filename + ".jp2"))
            tl_tci_jp2 = tl_tci_jp2[0]
            # Check if inventory has been generated
            if not os.path.exists(os.path.join(tl, "Inventory_Metadata.xml")):
                raise Exception("No Inventory_Metadata.xml file found in " + tl)
            tl_inventory_mtd_file = os.path.join(tl, "Inventory_Metadata.xml")
            tl_tci_folder = os.path.join(inventory_working_dir, tci_filename)
            if not os.path.exists(tl_tci_folder):
                FileUtils.create_directory(tl_tci_folder)
            FileUtils.copy_file(tl_tci_jp2, os.path.join(tl_tci_folder, os.path.basename(tl_tci_jp2)))
            tci_inventory_mtd_file = os.path.join(tl_tci_folder, "Inventory_Metadata.xml")
            inventory_mtd_reader = InventoryMetadataReader(tl_inventory_mtd_file)
            inventory_mtd_reader.set_file_id(inventory_mtd_reader.get_file_id().replace("MSI_L1C_TL", "MSI_L1C_TC"))
            inventory_mtd_reader.set_file_name(inventory_mtd_reader.get_file_name().replace("MSI_L1C_TL", "MSI_L1C_TC"))
            inventory_mtd_reader.set_file_type(inventory_mtd_reader.get_file_type().replace("MSI_L1C_TL", "MSI_L1C_TC"))
            inventory_mtd_reader.set_data_size(os.path.getsize(os.path.join(tl_tci_folder,
                                                                            os.path.basename(tl_tci_jp2))))
            self.logger.info("Writing tci inventory metadata in "+tci_inventory_mtd_file)
            inventory_mtd_reader.write_to_file(tci_inventory_mtd_file)
            if not main_scheme.startswith("4"):
                inventory_process = subprocess.Popen(["exiftool", os.path.join(tl_tci_folder,
                                                                               os.path.basename(tl_tci_jp2)),
                                                      "-xml+<="+tci_inventory_mtd_file])
                status = inventory_process.wait()
                if status != 0:
                    raise Exception("Error while launching inventory exif tools on tile "+tl)
                os.remove(tci_inventory_mtd_file)
                jp2_original = FileUtils.glob_one(tl_tci_folder, os.path.basename(tl_tci_jp2)+"_original")
                if jp2_original is not None:
                    os.remove(jp2_original)
            tile_jp2_filename = os.path.join(output_folder_inventory, os.path.basename(tl_tci_jp2))
            self.logger.debug("Copy the tci jp2 "+os.path.join(tl_tci_folder, os.path.basename(tl_tci_jp2))
                              + " to " + tile_jp2_filename)
            FileUtils.copy_file(os.path.join(tl_tci_folder, os.path.basename(tl_tci_jp2)), tile_jp2_filename)
        # List produced
        list_of_tl_produced = glob.glob(os.path.join(output_folder_inventory, "S2*TC*jp2"))
        if len(list_of_tl_produced) == 0:
            raise Exception("No tiles produced in inventory TCI L1C : " + output_folder_inventory)
        self.logger.info("Inventory TCI L1C done, products can be found in : " + output_folder_inventory)

    def __verify_datastrip(self, datastrip_folder):
        self.logger.debug("Verifying datastrip : "+datastrip_folder)
        self._verify_olqc_report(datastrip_folder,
                                 ["FORMAT_CORRECTNESS", "GENERAL_QUALITY", "SENSOR_QUALITY",
                                  "GEOMETRIC_QUALITY", "RADIOMETRIC_QUALITY"])

    def __verify_tile(self, tile_folder):
        self.logger.debug("Verifying tile : "+tile_folder)
        self._verify_olqc_report(tile_folder, ["FORMAT_CORRECTNESS", "GENERAL_QUALITY", "SENSOR_QUALITY",
                                               "GEOMETRIC_QUALITY"])

    def __copy_olqc_datastrip(self, datastrip_folder, output_folder):
        self.logger.debug("Copying datastrip OLQC reports from : " + datastrip_folder + " to "+output_folder)
        self._copy_olqc_report(datastrip_folder, ["FORMAT_CORRECTNESS", "GENERAL_QUALITY",
                                                  "SENSOR_QUALITY", "GEOMETRIC_QUALITY",
                                                  "RADIOMETRIC_QUALITY"], output_folder)

    def __copy_olqc_tile(self, tile_folder, output_folder):
        self.logger.debug("Copying tile OLQC reports from : " + tile_folder + " to "+output_folder)
        self._copy_olqc_report(tile_folder, ["FORMAT_CORRECTNESS", "GENERAL_QUALITY", "SENSOR_QUALITY",
                                             "GEOMETRIC_QUALITY"], output_folder)
