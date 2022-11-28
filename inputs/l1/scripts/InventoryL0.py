# coding=utf-8
import glob
import os
import shutil

import Constants
import FileUtils
from Inventory import Inventory
from JobOrderInventoryReader import JobOrderInventoryReader


class InventoryL0(Inventory):
    """
        Inventory L0
    """
    def __init__(self, loglevel, logfile, errfile, gr_inventory_script, ds_inventory_script, inventory_soft_version):
        # init logger
        super(InventoryL0, self).__init__(loglevel, logfile, errfile, gr_inventory_script, ds_inventory_script,
                                          inventory_soft_version)

    @staticmethod
    def is_inventory_mode(mode):
        # Inventory L1A DS
        if "OLQC_L0DS" in mode or "OLQC_L0GR" in mode:
            return True
        return False

    def do_inventory(self, working_dir, gipp_folder, mode, context_manager,
                     output_idp_infos_file, output_icd_file, dateoflogs, nb_tasks):
        """
            main function
        """
        # Get acquisition station from config
        acquisition_station = context_manager.get_acquisition_station()

        # Inventory L0C DS
        if "OLQC_L0DS" in mode:
            self._inventory_l0c_ds(context_manager, dateoflogs, gipp_folder,
                                   acquisition_station,
                                   output_icd_file, output_idp_infos_file,
                                   working_dir)

        # Inventory L0C GR
        if "OLQC_L0GR" in mode:
            self._inventory_l0c_gr(context_manager, dateoflogs,
                                   acquisition_station,
                                   working_dir, nb_tasks)

    def _inventory_l0c_gr(self, context_manager, dateoflogs,
                          acquisition_station,
                          working_dir, nb_tasks):
        self.logger.info("Doing inventory for L0 Granules")
        pdi_ds_output_folder = context_manager.get_element_in_proc_list("FORMAT_METADATA_DS_L0C",
                                                                        "PROC.LIST.PDI_DS")
        if pdi_ds_output_folder is None:
            raise Exception("No PROC.PDI_DS in context !!!")
        datastrips_out = glob.glob(os.path.join(pdi_ds_output_folder, "S2*DS*"))
        if len(datastrips_out) != 1:
            raise Exception(
                "No or more than one datastrips found in " + os.path.join(pdi_ds_output_folder, "S2*"))
        datastrip_out = datastrips_out[0]
        pdi_gr_output_folder = context_manager.get_element_in_proc_list("FORMAT_METADATA_GR_L0C",
                                                                        "PROC.LIST.PDI_DS_GR_LIST")
        if pdi_gr_output_folder is None:
            raise Exception("No PROC.PDI_DS_GR_LIST in context !!!")

        # Inventory working dirs
        inventory_working_dir = os.path.join(working_dir, "ipf_output_inventory_GR_L0C_" + dateoflogs)
        if not os.path.exists(inventory_working_dir):
            FileUtils.create_directory(inventory_working_dir)
        # retrieve to local the granules because of shared folder
        granules_working_dir = os.path.join(inventory_working_dir, "TEMP_GR_L0C")
        if not os.path.exists(granules_working_dir):
            FileUtils.create_directory(granules_working_dir)
        FileUtils.copy_directory_recursive(os.path.join(pdi_gr_output_folder, "DB1"), granules_working_dir)
        granules_out = glob.glob(os.path.join(granules_working_dir, "S2*GR*"))
        if len(granules_out) == 0:
            raise Exception("No granules found in " + os.path.join(pdi_gr_output_folder, "DB1", "S2*GR*"))
        for gr in granules_out:
            self.__verify_granule(gr)
        output_folder_inventory = os.path.join(working_dir, "INV_L0C_GR")
        if not os.path.exists(output_folder_inventory):
            FileUtils.create_directory(output_folder_inventory)
        # Find the JobOrder Template
        jobs = self.split_granules_jobs(granules_out, inventory_working_dir, output_folder_inventory,
                                        datastrip_out, acquisition_station, nb_tasks, "L0")
        # Launch process
        self._launch_inventory_process(jobs, "GR_L0C", self._gr_inventory_script)
        list_of_gr_produced = glob.glob(os.path.join(output_folder_inventory, "S2*GR*tar"))
        self.logger.debug(list_of_gr_produced)
        if len(list_of_gr_produced) == 0:
            raise Exception("No granules produced in inventory GR L0C : " + output_folder_inventory)
        # Copy OLQC reports
        for gr in granules_out:
            self.__copy_olqc_granule(gr, output_folder_inventory)
        # Copy KPI files
        output_folder_kpi = os.path.join(working_dir, "KPI_L0C_GR")
        if not os.path.exists(output_folder_kpi):
            FileUtils.create_directory(output_folder_kpi)
        self.logger.info("Inventory GR L0C done, products can be found in : " + output_folder_inventory)

    def _inventory_l0c_ds(self, context_manager, dateoflogs, gipp_folder,
                          acquisition_station,
                          output_icd_file, output_idp_infos_file,
                          working_dir):
        self.logger.info("Doing inventory for L0 Datastrip")
        pdi_ds_output_folder = context_manager.get_element_in_proc_list("FORMAT_METADATA_DS_L0C",
                                                                        "PROC.LIST.PDI_DS")
        if pdi_ds_output_folder is None:
            raise Exception("No PROC.LIST.PDI_DS in context for FORMAT_METADATA_DS_L0C !!!")
        datastrips_out = glob.glob(os.path.join(pdi_ds_output_folder, "S2*DS*"))
        if len(datastrips_out) != 1:
            raise Exception(
                "No or more than one datastrips found in " + os.path.join(pdi_ds_output_folder, "S2*"))
        datastrip_out = datastrips_out[0]
        self.__verify_datastrip(datastrip_out)
        # Inventory working dirs
        inventory_working_dir = os.path.join(working_dir, "ipf_output_inventory_DS_L0C_" + dateoflogs)
        if not os.path.exists(inventory_working_dir):
            FileUtils.create_directory(inventory_working_dir)
        output_folder_inventory = os.path.join(working_dir, "INV_L0C_DS")
        if not os.path.exists(output_folder_inventory):
            FileUtils.create_directory(output_folder_inventory)
        # Find the JobOrder Template
        inv_mtd_l0_ds_template_filename = os.path.join(os.path.dirname(os.path.realpath(__file__)), "data",
                                                       "JobOrderTemplates",
                                                       "Joborder_InventoryDSL0c.xml")
        self.logger.debug("JobOrder inventory L0DS template : " + inv_mtd_l0_ds_template_filename)
        jo_reader = JobOrderInventoryReader(inv_mtd_l0_ds_template_filename)
        jo_reader.set_ds_l0_input(datastrip_out)
        jo_reader.set_working_input(inventory_working_dir)
        jo_reader.set_output(output_folder_inventory)
        jo_reader.set_acquisition_station(acquisition_station)
        jo_reader.set_processing_station(Constants.PROCESSING_STATION)
        jo_reader.set_source_software_version(self._soft_version)
        inventory_job_order = os.path.join(inventory_working_dir, "JobOrderInventory.xml")
        jo_reader.write_to_file(inventory_job_order)
        # Launch process
        self._launch_inventory_process([inventory_job_order], "DS_L0C", self._ds_inventory_script)
        # List datastrips in output of inventory
        list_of_ds_produced = glob.glob(os.path.join(output_folder_inventory, "S2*DS*tar"))
        if len(list_of_ds_produced) == 0:
            raise Exception("No datastrip produced in inventory DS L0C : " + output_folder_inventory)
        self.logger.info("Inventory DS L0C done, products can be found in : " + output_folder_inventory)
        # Copy OLQC report
        self.__copy_olqc_datastrip(datastrip_out, output_folder_inventory)
        # Copy KPI files
        output_folder_kpi = os.path.join(working_dir, "KPI_L0C_DS")
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

    def __verify_datastrip(self, datastrip_folder):
        self.logger.debug("Verifying datastrip : "+datastrip_folder)
        self._verify_olqc_report(datastrip_folder, ["FORMAT_CORRECTNESS", "GENERAL_QUALITY",
                                                    "SENSOR_QUALITY", "GEOMETRIC_QUALITY"])

    def __verify_granule(self, granule_folder):
        self.logger.debug("Verifying granule : "+granule_folder)
        self._verify_olqc_report(granule_folder, ["FORMAT_CORRECTNESS", "GENERAL_QUALITY", "SENSOR_QUALITY"])

    def __copy_olqc_datastrip(self, datastrip_folder, output_folder):
        self.logger.debug("Copying datastrip OLQC reports from : " + datastrip_folder + " to "+output_folder)
        self._copy_olqc_report(datastrip_folder, ["FORMAT_CORRECTNESS", "GENERAL_QUALITY",
                                                  "SENSOR_QUALITY", "GEOMETRIC_QUALITY"], output_folder)

    def __copy_olqc_granule(self, granule_folder, output_folder):
        self.logger.debug("Copying tile OLQC reports from : " + granule_folder + " to " + output_folder)
        self._copy_olqc_report(granule_folder, ["FORMAT_CORRECTNESS", "GENERAL_QUALITY", "SENSOR_QUALITY"],
                               output_folder)
