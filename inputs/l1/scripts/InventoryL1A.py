# coding=utf-8
import glob
import os
import shutil

import Constants
import FileUtils
from Inventory import Inventory
from JobOrderInventoryReader import JobOrderInventoryReader


class InventoryL1A(Inventory):
    """
        Inventory L1A
    """
    def __init__(self, loglevel, logfile, errfile, gr_inventory_script, ds_inventory_script, inventory_soft_version):
        # init logger
        super(InventoryL1A, self).__init__(loglevel, logfile, errfile, gr_inventory_script, ds_inventory_script,
                                           inventory_soft_version)

    @staticmethod
    def is_inventory_mode(mode):
        # Inventory L1A DS
        if "L1AFormatDS" in mode or "L1AFormatGR" in mode:
            return True
        return False

    def do_inventory(self, working_dir, gipp_folder, mode, context_manager,
                     output_idp_infos_file, output_icd_file, dateoflogs, nb_tasks):
        """
            main function
        """
        # Get acquisition station from config
        acquisition_station = context_manager.get_acquisition_station()

        # Inventory L1A DS
        if "L1AFormatDS" in mode:
            self._inventory_l1a_ds(context_manager, dateoflogs, gipp_folder,
                                   acquisition_station, output_icd_file, output_idp_infos_file,
                                   working_dir)

        # Inventory L1A GR
        if "L1AFormatGR" in mode:
            self._inventory_l1a_gr(context_manager, dateoflogs,
                                   acquisition_station, working_dir, nb_tasks)

    def _inventory_l1a_ds(self, context_manager, dateoflogs, gipp_folder,
                          acquisition_station, output_icd_file, output_idp_infos_file,
                          working_dir):
        self.logger.info("Doing inventory for L1A Datastrip")
        pdi_ds_output_folder = context_manager.get_element("PROC.L1A.PDI_DS")
        if pdi_ds_output_folder is None:
            raise Exception("No PROC.L1A.PDI_DS in context !!!")
        datastrips_out = glob.glob(os.path.join(pdi_ds_output_folder[0], "S2*DS*"))
        if len(datastrips_out) != 1:
            raise Exception(
                "No or more than one datastrips found in " + os.path.join(pdi_ds_output_folder[0], "S2*"))
        datastrip_out = datastrips_out[0]
        self.__verify_datastrip(datastrip_out)
        self.logger.info("Using datastrip : "+datastrip_out)
        # Inventory working dirs
        inventory_working_dir = os.path.join(working_dir, "ipf_output_inventory_DS_L1A_" + dateoflogs)
        if not os.path.exists(inventory_working_dir):
            FileUtils.create_directory(inventory_working_dir)
        self.logger.info("Working in "+inventory_working_dir)
        output_folder_inventory = os.path.join(working_dir, "INV_L1A_DS")
        if not os.path.exists(output_folder_inventory):
            FileUtils.create_directory(output_folder_inventory)
        # Find the JobOrder Template
        inv_mtd_l1a_ds_template_filename = os.path.join(os.path.dirname(os.path.realpath(__file__)), "data",
                                                        "JobOrderTemplates",
                                                        "job_order_inv_mtd_DS_L1A.xml")
        self.logger.debug("JobOrder inventory L1ADS template : " + inv_mtd_l1a_ds_template_filename)
        jo_reader = JobOrderInventoryReader(inv_mtd_l1a_ds_template_filename)
        jo_reader.set_ds_input(datastrip_out)
        jo_reader.set_working_input(inventory_working_dir)
        jo_reader.set_acquisition_station(acquisition_station)
        jo_reader.set_processing_station(Constants.PROCESSING_STATION)
        jo_reader.set_source_software_version(self._soft_version)
        jo_reader.set_global_footprint_ai(self._get_global_footprint_ai(context_manager))
        # No output really ?
        # jo_reader.set_output(output_folder_inventory)
        inventory_job_order = os.path.join(inventory_working_dir, "JobOrderInventory.xml")
        jo_reader.write_to_file(inventory_job_order)
        # Launch process
        self._launch_inventory_process([inventory_job_order], "DS_L1A", self._ds_inventory_script)
        # Check if inventory has been generated
        if not os.path.exists(os.path.join(datastrip_out, "Inventory_Metadata.xml")):
            raise Exception("No Inventory_Metadata.xml file generated in "+datastrip_out)
        # Patch dates
        self._patch_inventory_dates_datastrip(datastrip_out)
        # tar datastrip
        datastrip_filename = os.path.join(output_folder_inventory, os.path.basename(datastrip_out))
        FileUtils.copy_directory_recursive(datastrip_out, datastrip_filename)
        # List datastrips in output of inventory
        list_of_ds_produced = glob.glob(os.path.join(output_folder_inventory, "S2*DS*"))
        if len(list_of_ds_produced) == 0:
            raise Exception("No datastrip produced in inventory DS L1A : " + output_folder_inventory)
        self.logger.info("Inventory DS L1A done, products can be found in : " + output_folder_inventory)
        # Copy OLQC reports
        self.__copy_olqc_datastrip(datastrip_out, output_folder_inventory)
        # Copy KPI files
        output_folder_kpi = os.path.join(working_dir, "KPI_L1A_DS")
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

    def _inventory_l1a_gr(self, context_manager, dateoflogs,
                          acquisition_station, working_dir, nb_tasks):
        self.logger.info("Doing inventory for L1A Granules")
        pdi_ds_output_folder = context_manager.get_element("PROC.PDI_DS")
        if pdi_ds_output_folder is None:
            raise Exception("No PROC.PDI_DS in context !!!")
        datastrips_out = glob.glob(os.path.join(pdi_ds_output_folder[0], "S2*L1A*DS*"))
        if len(datastrips_out) != 1:
            raise Exception(
                "No or more than one datastrips found in " + os.path.join(pdi_ds_output_folder[0], "S2*L1A*DS"))
        datastrip_out = datastrips_out[0]
        pdi_gr_output_folder = context_manager.get_element("PROC.L1A.PDI_DS_GR_LIST")
        if pdi_gr_output_folder is None:
            raise Exception("No PROC.L1A.PDI_DS_GR_LIST in context !!!")
        granules_out = glob.glob(os.path.join(pdi_gr_output_folder[0], "DB1", "S2*GR*"))
        if len(granules_out) == 0:
            raise Exception("No granules found in " + os.path.join(pdi_gr_output_folder[0], "DB1", "S2*GR*"))
        for gr in granules_out:
            self.__verify_granule(gr)
        # Inventory working dirs
        inventory_working_dir = os.path.join(working_dir, "ipf_output_inventory_GR_L1A_" + dateoflogs)
        if not os.path.exists(inventory_working_dir):
            FileUtils.create_directory(inventory_working_dir)
        output_folder_inventory = os.path.join(working_dir, "INV_L1A_GR")
        if not os.path.exists(output_folder_inventory):
            FileUtils.create_directory(output_folder_inventory)
        # Create the job orders
        jobs = self.split_granules_jobs(granules_out, inventory_working_dir, output_folder_inventory, datastrip_out,
                                        acquisition_station, nb_tasks, "L1A")
        # Launch process
        self._launch_inventory_process(jobs, "GR_L1A", self._gr_inventory_script)
        # Tar the granules
        for gr in granules_out:
            # Check if inventory has been generated
            if not os.path.exists(os.path.join(gr, "Inventory_Metadata.xml")):
                raise Exception("No Inventory_Metadata.xml file generated in " + gr)
            # Patch dates
            self._patch_inventory_dates_granule(gr)
            granule_filename = os.path.join(output_folder_inventory, os.path.basename(gr))
            # Copy OLQC reports
            self.__copy_olqc_granule(gr, output_folder_inventory)
            FileUtils.copy_directory_recursive(gr, granule_filename)
        # List produced
        list_of_gr_produced = glob.glob(os.path.join(output_folder_inventory, "S2*GR*"))
        self.logger.debug(list_of_gr_produced)
        if len(list_of_gr_produced) == 0:
            raise Exception("No granules produced in inventory GR L1A : " + output_folder_inventory)
        # Copy KPI files
        output_folder_kpi = os.path.join(working_dir, "KPI_L1A_GR")
        if not os.path.exists(output_folder_kpi):
            FileUtils.create_directory(output_folder_kpi)
        self.logger.info("Inventory GR L1A done, products can be found in : " + output_folder_inventory)

    def __verify_datastrip(self, datastrip_folder):
        self.logger.debug("Verifying datastrip : " + datastrip_folder)
        self._verify_olqc_report(datastrip_folder, ["FORMAT_CORRECTNESS", "GENERAL_QUALITY",
                                                    "SENSOR_QUALITY", "GEOMETRIC_QUALITY",
                                                    "RADIOMETRIC_QUALITY"])

    def __verify_granule(self, granule_folder):
        self.logger.debug("Verifying granule : "+granule_folder)
        self._verify_olqc_report(granule_folder, ["FORMAT_CORRECTNESS", "GENERAL_QUALITY",
                                                  "SENSOR_QUALITY", "GEOMETRIC_QUALITY"])

    def __copy_olqc_datastrip(self, datastrip_folder, output_folder):
        self.logger.debug("Copying datastrip OLQC reports from : " + datastrip_folder + " to "+output_folder)
        self._copy_olqc_report(datastrip_folder, ["FORMAT_CORRECTNESS", "GENERAL_QUALITY",
                                                  "SENSOR_QUALITY", "GEOMETRIC_QUALITY",
                                                  "RADIOMETRIC_QUALITY"], output_folder)

    def __copy_olqc_granule(self, granule_folder, output_folder):
        self.logger.debug("Copying tile OLQC reports from : " + granule_folder + " to "+output_folder)
        self._copy_olqc_report(granule_folder, ["FORMAT_CORRECTNESS", "GENERAL_QUALITY",
                                                "SENSOR_QUALITY", "GEOMETRIC_QUALITY"], output_folder)
