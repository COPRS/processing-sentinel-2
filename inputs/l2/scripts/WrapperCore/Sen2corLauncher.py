# coding=utf-8
import argparse
import shutil
import subprocess

import JobOrderReader
import JobOrderReaderNew
import DatastripReader
import InventoryReader
import InventoryL2A
import SystemUtils
import FileUtils
import os
import StringIO
import datetime

import IdpInfos
from PDIVerifier import PDIVerifier
from InventoryMetadataReader import InventoryMetadataReader


def get_utc_from_datetime(datet):
    return datetime.datetime.strftime(datet, "UTC=%Y-%m-%dT%H:%M:%S.%f")


# xml dump in string
def joborder_dom_to_string(dom_object):
    output = StringIO.StringIO()
    output.write('<?xml version="1.0" ?>\n')
    dom_object.export(
        output, 0, name_='Ipf_Job_Order',
        namespacedef_='',
        pretty_print=True)
    return output


def inventory_dom_to_string(dom_object):
    output = StringIO.StringIO()
    output.write('<?xml version="1.0" ?>\n')
    dom_object.export(
        output, 0, name_='Inventory_Metadata',
        namespacedef_='',
        pretty_print=True)
    return output


def modify_Inventory(inventory_input, inventory_output, PDI_ID, file_type, process_station, parent_id=None):
    inventory_in = InventoryReader.parse(inventory_input, True)

    inventory_in.set_Processing_Station(process_station)
    inventory_in.set_File_ID(PDI_ID)
    inventory_in.set_File_Name(PDI_ID)
    inventory_in.set_Source("L2A_Processor")
    inventory_in.set_File_Type(file_type)
    if parent_id:
        inventory_in.set_Parent_ID(parent_id)

    # process time
    inventory_in.set_Generation_Time(get_utc_from_datetime(datetime.datetime.now()))

    # write
    content = inventory_dom_to_string(inventory_in).getvalue()
    content = content.replace("    ", "  ")

    with open(inventory_output, "w") as fh:
        fh.write(content)
        print("Inventory writed in " + inventory_output)


# Create a jobOrder
def create_JobOrder_OLQC(gip_probas, gip_olqcpa, persistent, products, working, report):
    print("Creating JobOrder for OLQC...")

    parameters = []
    parameters.append(JobOrderReader.Processing_Parameter("VALIDATE_SCHEMA", "false"))
    parameters.append(JobOrderReader.Processing_Parameter("TRACE", "false"))

    da_conf = JobOrderReader.Ipf_ConfType()
    da_Config_Files = JobOrderReader.Config_FilesType()
    da_conf.set_Config_Files(da_Config_Files)
    da_conf.set_Processor_Name("Chain")
    da_conf.set_Version("02.08.00")
    da_conf.set_Test(False)
    da_conf.set_Acquisition_Station("MSI")
    da_conf.set_Processing_Station("MSI")
    da_conf.set_Dynamic_Processing_Parameters(JobOrderReader.Dynamic_Processing_ParametersType(parameters))

    # Build inputs
    da_inputs = JobOrderReader.List_of_InputsType()

    # add products
    nt = JobOrderReader.List_of_File_NamesType()
    for p in products:
        nt.add_File_Name(p)
    nt.set_count(len(products))
    product_input = JobOrderReader.InputType("PDI_SAFE", "Directory", nt)
    da_inputs.add_Input(product_input)

    # add gip olqcpa
    nt = JobOrderReader.List_of_File_NamesType()
    nt.add_File_Name(gip_olqcpa)
    nt.set_count(1)
    olqcpa_input = JobOrderReader.InputType("GIP_OLQCPA", "Physical", nt)
    da_inputs.add_Input(olqcpa_input)

    # add gip probas
    nt = JobOrderReader.List_of_File_NamesType()
    nt.add_File_Name(gip_probas)
    nt.set_count(1)
    probas_input = JobOrderReader.InputType("GIP_PROBAS", "Physical", nt)
    da_inputs.add_Input(probas_input)

    # add persistent folder
    nt = JobOrderReader.List_of_File_NamesType()
    nt.add_File_Name(persistent)
    nt.set_count(1)
    persistent_input = JobOrderReader.InputType("PERSISTENT_RESOURCES", "Directory", nt)
    da_inputs.add_Input(persistent_input)

    # add WORKING, Directory
    nt = JobOrderReader.List_of_File_NamesType()
    nt.add_File_Name(working)
    nt.set_count(1)
    if not os.path.exists(working):
        print("[Output] WORKING_DIR must be created in %s" % working)
        os.mkdir(working)
    working_input = JobOrderReader.InputType("WORKING", "Directory", nt)
    da_inputs.add_Input(working_input)

    # Build outputs
    da_outputs = JobOrderReader.List_of_OutputsType()

    # add REPORT
    working_output = JobOrderReader.OutputType(True, "REPORT", "Physical", report)
    da_outputs.add_Output(working_output)

    procs = JobOrderReader.List_of_Ipf_ProcsType()
    proc = JobOrderReader.Ipf_Proc()
    proc.set_Task_Name("OLQC")
    proc.set_Task_Version("01.00.00")
    proc.set_List_of_Inputs(da_inputs)
    proc.set_List_of_Outputs(da_outputs)
    procs.add_Ipf_Proc(proc)
    procs.count = 1

    joborder = JobOrderReader.Ipf_Job_OrderType(da_conf, procs)

    content = joborder_dom_to_string(joborder).getvalue()
    content = content.replace("    ", "  ")
    # content = content.replace('  xmlns:ipf_base="http://gs2.esa.int/DICO/1.0/IPF/base/" ', '')

    return content


# Create a jobOrder
def create_JobOrder_FORMAT_METADATA_TL_L2A(arch_center, proc_baseline, proc_center,
                                           tile_list_file,
                                           gip_jp2, gip_olqcpa,
                                           product, acquisition_station, sensing_time_start, sensing_time_stop,
                                           working, report, output):
    print("Creating JobOrder for FORMAT_METADATA_TL_L2A...")

    parameters = []
    parameters.append(JobOrderReader.Processing_Parameter("ARCHIVING_CENTER", arch_center))
    #parameters.append(JobOrderReader.Processing_Parameter("ARCHIVING_CENTER_NAME", "2BPS"))
    #parameters.append(JobOrderReader.Processing_Parameter("ARCHIVING_CENTER_COUNTRY", "Italy"))
    #parameters.append(JobOrderReader.Processing_Parameter("ARCHIVING_CENTER_ORG", "ESA"))
    parameters.append(JobOrderReader.Processing_Parameter("PROCESSING_BASELINE", proc_baseline))
    parameters.append(JobOrderReader.Processing_Parameter("PROCESSING_CENTER", proc_center))
    #parameters.append(JobOrderReader.Processing_Parameter("PROCESSING_CENTER_NAME", "2BPS"))
    #parameters.append(JobOrderReader.Processing_Parameter("PROCESSING_CENTER_COUNTRY", "Italy"))
    #parameters.append(JobOrderReader.Processing_Parameter("PROCESSING_CENTER_ORG", "ESA"))
    parameters.append(JobOrderReader.Processing_Parameter("FAKE_MODE", "false"))
    parameters.append(JobOrderReader.Processing_Parameter("VALIDATE_SCHEMA", "false"))
    parameters.append(JobOrderReader.Processing_Parameter("PARALLEL_TILE", "false"))
    parameters.append(JobOrderReader.Processing_Parameter("PARALLEL_TILE_IDENT", "001"))
    parameters.append(JobOrderReader.Processing_Parameter("TRACE", "false"))

    da_conf = JobOrderReader.Ipf_ConfType()
    da_Config_Files = JobOrderReader.Config_FilesType()
    da_conf.set_Config_Files(da_Config_Files)
    da_conf.set_Processor_Name("Chain")
    da_conf.set_Version("02.08.00")
    da_conf.set_Test(False)
    da_conf.set_Acquisition_Station(acquisition_station)
    da_conf.set_Processing_Station(proc_center)
    da_conf.set_Sensing_Time(JobOrderReader.Sensing_TimeType(sensing_time_start, sensing_time_stop))
    da_conf.set_Dynamic_Processing_Parameters(JobOrderReader.Dynamic_Processing_ParametersType(parameters))

    # Build inputs
    da_inputs = JobOrderReader.List_of_InputsType()

    # add products
    nt = JobOrderReader.List_of_File_NamesType()
    nt.add_File_Name(product)
    nt.set_count(1)
    product_input = JobOrderReader.InputType("PDI_DS_TILE_LIST", "Directory", nt)
    da_inputs.add_Input(product_input)

    # add products
    nt = JobOrderReader.List_of_File_NamesType()
    nt.add_File_Name(tile_list_file)
    nt.set_count(1)
    tile_list_file_input = JobOrderReader.InputType("TILE_LIST_FILE", "Physical", nt)
    da_inputs.add_Input(tile_list_file_input)

    # add gip olqcpa
    nt = JobOrderReader.List_of_File_NamesType()
    nt.add_File_Name(gip_olqcpa)
    nt.set_count(1)
    olqcpa_input = JobOrderReader.InputType("GIP_OLQCPA", "Physical", nt)
    da_inputs.add_Input(olqcpa_input)

    # add gip JP2KPA
    nt = JobOrderReader.List_of_File_NamesType()
    nt.add_File_Name(gip_jp2)
    nt.set_count(1)
    probas_input = JobOrderReader.InputType("GIP_JP2KPA", "Physical", nt)
    da_inputs.add_Input(probas_input)

    da_inputs.set_count(4)

    # add WORKING, Directory
    nt = JobOrderReader.List_of_File_NamesType()
    nt.add_File_Name(working)
    nt.set_count(1)
    if not os.path.exists(working):
        print("[Output] WORKING_DIR must be created in %s" % working)
        os.mkdir(working)
    working_input = JobOrderReader.InputType("WORKING", "Directory", nt)
    da_inputs.add_Input(working_input)

    # Build outputs
    da_outputs = JobOrderReader.List_of_OutputsType()

    # add outputDir
    working_output = JobOrderReader.OutputType(True, "PDI_DS_TILE_LIST", "Directory", output)
    da_outputs.add_Output(working_output)

    # add REPORT
    working_output = JobOrderReader.OutputType(True, "REPORT", "Physical", report)
    da_outputs.add_Output(working_output)
    da_outputs.set_count(2)

    procs = JobOrderReader.List_of_Ipf_ProcsType()
    proc = JobOrderReader.Ipf_Proc()
    proc.set_Task_Name("FORMAT_METADATA_TILE_L2A")
    proc.set_Task_Version("04.04.01")
    proc.set_List_of_Inputs(da_inputs)
    proc.set_List_of_Outputs(da_outputs)
    procs.add_Ipf_Proc(proc)
    procs.count = 1

    joborder = JobOrderReader.Ipf_Job_OrderType(da_conf, procs)

    content = joborder_dom_to_string(joborder).getvalue()
    content = content.replace("    ", "  ")
    content = content.replace('  xmlns:ipf_base="http://gs2.esa.int/DICO/1.0/IPF/base/" ', '')

    return content


# Create a jobOrder
def create_JobOrder_FORMAT_METADATA_DS_L2A(arch_center, proc_baseline, proc_center,
                                           gip_jp2, gip_olqcpa,
                                           product, acquisition_station, sensing_time_start, sensing_time_stop,
                                           working, report, output):
    print("Creating JobOrder for FORMAT_METADATA_DS_L2A...")

    parameters = []
    parameters.append(JobOrderReader.Processing_Parameter("ARCHIVING_CENTER", arch_center))
    #parameters.append(JobOrderReader.Processing_Parameter("ARCHIVING_CENTER_NAME", "2BPS"))
    #parameters.append(JobOrderReader.Processing_Parameter("ARCHIVING_CENTER_COUNTRY", "Italy"))
    #parameters.append(JobOrderReader.Processing_Parameter("ARCHIVING_CENTER_ORG", "ESA"))
    parameters.append(JobOrderReader.Processing_Parameter("PROCESSING_BASELINE", proc_baseline))
    parameters.append(JobOrderReader.Processing_Parameter("PROCESSING_CENTER", proc_center))
    #parameters.append(JobOrderReader.Processing_Parameter("PROCESSING_CENTER_NAME", "2BPS"))
    #parameters.append(JobOrderReader.Processing_Parameter("PROCESSING_CENTER_COUNTRY", "Italy"))
    #parameters.append(JobOrderReader.Processing_Parameter("PROCESSING_CENTER_ORG", "ESA"))
    parameters.append(JobOrderReader.Processing_Parameter("FAKE_MODE", "false"))
    parameters.append(JobOrderReader.Processing_Parameter("VALIDATE_SCHEMA", "false"))
    parameters.append(JobOrderReader.Processing_Parameter("TRACE", "false"))

    da_conf = JobOrderReader.Ipf_ConfType()
    da_Config_Files = JobOrderReader.Config_FilesType()
    da_conf.set_Config_Files(da_Config_Files)
    da_conf.set_Processor_Name("Chain")
    da_conf.set_Version("02.09.00")
    da_conf.set_Test(False)
    da_conf.set_Acquisition_Station(acquisition_station)
    da_conf.set_Processing_Station(proc_center)
    da_conf.set_Sensing_Time(JobOrderReader.Sensing_TimeType(sensing_time_start, sensing_time_stop))
    da_conf.set_Dynamic_Processing_Parameters(JobOrderReader.Dynamic_Processing_ParametersType(parameters))

    # Build inputs
    da_inputs = JobOrderReader.List_of_InputsType()

    # add products
    nt = JobOrderReader.List_of_File_NamesType()
    nt.add_File_Name(product)
    nt.set_count(1)
    product_input = JobOrderReader.InputType("PDI_DS", "Directory", nt)
    da_inputs.add_Input(product_input)

    # add gip olqcpa
    nt = JobOrderReader.List_of_File_NamesType()
    nt.add_File_Name(gip_olqcpa)
    nt.set_count(1)
    olqcpa_input = JobOrderReader.InputType("GIP_OLQCPA", "Physical", nt)
    da_inputs.add_Input(olqcpa_input)

    # add gip JP2KPA
    nt = JobOrderReader.List_of_File_NamesType()
    nt.add_File_Name(gip_jp2)
    nt.set_count(1)
    probas_input = JobOrderReader.InputType("GIP_JP2KPA", "Physical", nt)
    da_inputs.add_Input(probas_input)

    # add WORKING, Directory
    nt = JobOrderReader.List_of_File_NamesType()
    nt.add_File_Name(working)
    nt.set_count(1)
    if not os.path.exists(working):
        print("[Output] WORKING_DIR must be created in %s" % working)
        os.mkdir(working)
    working_input = JobOrderReader.InputType("WORKING", "Directory", nt)
    da_inputs.add_Input(working_input)

    da_inputs.set_count(4)

    # Build outputs
    da_outputs = JobOrderReader.List_of_OutputsType()

    # add outputDir
    working_output = JobOrderReader.OutputType(True, "PDI_DS", "Directory", output)
    da_outputs.add_Output(working_output)

    # add REPORT
    working_output = JobOrderReader.OutputType(True, "REPORT", "Physical", report)
    da_outputs.add_Output(working_output)
    da_outputs.set_count(2)

    procs = JobOrderReader.List_of_Ipf_ProcsType()
    proc = JobOrderReader.Ipf_Proc()
    proc.set_Task_Name("FORMAT_METADATA_DS_L2A")
    proc.set_Task_Version("04.04.01")
    proc.set_List_of_Inputs(da_inputs)
    proc.set_List_of_Outputs(da_outputs)
    procs.add_Ipf_Proc(proc)
    procs.count = 1

    joborder = JobOrderReader.Ipf_Job_OrderType(da_conf, procs)

    content = joborder_dom_to_string(joborder).getvalue()
    content = content.replace("    ", "  ")
    content = content.replace('  xmlns:ipf_base="http://gs2.esa.int/DICO/1.0/IPF/base/" ', '')

    return content


# create a fake tile list file
def create_tile_list_file(filename, tile_id):
    # create fake tile list file
    tile_list_file = open(filename, "w")
    tile_list_file.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>")
    tile_list_file.write(
        "<Tile_List xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"/dpc/app/s2ipf/GET_TILE_LIST/04.04.00/scripts/../../../schemas/04.04.00/ICD/DICO/1.0/IPF/ipf/File_Tile_List.xsd\">")
    tile_list_file.write("<Number_Of_Tiles>1</Number_Of_Tiles>")
    tile_list_file.write("<List_Tile_Id Tile_Number=\"1\">")
    tile_list_file.write("<Tile_Id>" + tile_id + "</Tile_Id>")
    tile_list_file.write("</List_Tile_Id>")
    tile_list_file.write("</Tile_List>")
    tile_list_file.close()


def tar_directory(src_dir, target):
    tar_process_code = subprocess.call(
        ["tar", "hcf", target, "-C", os.path.dirname(src_dir), os.path.basename(src_dir)],
        shell=False)
    if tar_process_code != 0:
        raise IOError("Can't tar " + src_dir + " to " + target)


def untar_to_directory(src_file, target):
    tar_process_code = subprocess.call(
        ["tar", "xvf", src_file, "-C", target],
        shell=False)
    if tar_process_code != 0:
        raise IOError("Can't untar " + src_file + " to " + target)


def main():
    parser = argparse.ArgumentParser(
        description='Launch L2A processor',  # main description for help
        epilog='Beta')  # displayed after help

    parser.add_argument("-j", "--joborder", help="Specify the joborder file (xml file)", required=True)
    parser.add_argument("-m", "--mode", help="Specify the mode ( DS or TL)", required=True)

    args = parser.parse_args()

    if args.joborder is None:
        raise Exception("No joborder specified")

    if args.mode == "TL":
        mode = "TL"
    elif args.mode == "DS":
        mode = "DS"
    else:
        raise Exception("Mode should be either TL or DS")
    job = JobOrderReaderNew.parse(args.joborder, silence=True)

    job_options = job.get_Ipf_Conf().get_Dynamic_Processing_Parameters().get_Processing_Parameter()

    processing_baseline = None
    archiving_center = None
    processing_center = None
    resolution = None
    debug = False

    for opt in job_options:
        if opt.get_Name() == "ARCHIVING_CENTER":
            archiving_center = opt.get_Value()
        if opt.get_Name() == "PROCESSING_CENTER":
            processing_center = opt.get_Value()
        if opt.get_Name() == "TRACE":
            debug = opt.get_Value() == "true"

    # Get job orer proc
    job_proc = job.get_List_of_Ipf_Procs().get_Ipf_Proc()

    datastrip = None
    tile = None
    output_ds = None
    output_tl = None
    output_kpi = None
    gip_l2a = None
    gip_sc = None
    gip_ac = None
    gip_pb = None
    gip_olqc = None
    gip_jp2 = None
    work = None
    sen2cor_installdir = None
    compresstile_installdir = None
    format_mtd_ds_l2a_installdir = None
    format_mtd_tl_l2a_installdir = None
    olqc_installdir = None
    dateoflogs = datetime.datetime.now().strftime("%Y%m%d%H%M%S")

    for proc in job_proc:
        if proc.get_Task_Name() == "Sen2Cor":
            job_inputs = proc.get_List_of_Inputs().get_Input()
            for input in job_inputs:
                if input.get_File_Type() == "MSI_L1C_DS":
                    datastrip = input.get_List_of_File_Names().get_File_Name()[0]
                if input.get_File_Type() == "MSI_L1C_TL":
                    tile = input.get_List_of_File_Names().get_File_Name()[0]
                if input.get_File_Type() == "GIP_L2ACFG":
                    gip_l2a = input.get_List_of_File_Names().get_File_Name()[0]
                if input.get_File_Type() == "GIP_L2ACSC":
                    gip_sc = input.get_List_of_File_Names().get_File_Name()[0]
                if input.get_File_Type() == "GIP_L2ACAC":
                    gip_ac = input.get_List_of_File_Names().get_File_Name()[0]
                if input.get_File_Type() == "GIP_PROBA2":
                    gip_pb = input.get_List_of_File_Names().get_File_Name()[0]
                if input.get_File_Type() == "GIP_OLQCPA":
                    gip_olqc = input.get_List_of_File_Names().get_File_Name()[0]
                if input.get_File_Type() == "GIP_JP2KPA":
                    gip_jp2 = input.get_List_of_File_Names().get_File_Name()[0]
                if input.get_File_Type() == "WORKING":
                    work = input.get_List_of_File_Names().get_File_Name()[0]
                if input.get_File_Type() == "SEN2COR_DIR_INSTALL":
                    sen2cor_installdir = input.get_List_of_File_Names().get_File_Name()[0]
                if input.get_File_Type() == "COMPRESS_TILE_IMAGE_DIR_INSTALL":
                    compresstile_installdir = input.get_List_of_File_Names().get_File_Name()[0]
                if input.get_File_Type() == "OLQC_DIR_INSTALL":
                    olqc_installdir = input.get_List_of_File_Names().get_File_Name()[0]
                if input.get_File_Type() == "FORMAT_METADATA_DS_DIR_INSTALL":
                    format_mtd_ds_l2a_installdir = input.get_List_of_File_Names().get_File_Name()[0]
                if input.get_File_Type() == "FORMAT_METADATA_TL_DIR_INSTALL":
                    format_mtd_tl_l2a_installdir = input.get_List_of_File_Names().get_File_Name()[0]
            job_outputs = proc.get_List_of_Outputs().get_Output()
            for output in job_outputs:
                if output.get_File_Type() == "MSI_L2A_DS":
                    output_ds = output.get_File_Name()
                if output.get_File_Type() == "MSI_L2A_TL":
                    output_tl = output.get_File_Name()
                if output.get_File_Type() == "KPI_L2A":
                    output_kpi = output.get_File_Name()

    print("Launching datastrip Sen2Cor")
    if datastrip is None:
        raise Exception("No MSI_L1C_DS found in JobOrder")
    elif not os.path.exists(datastrip):
        raise Exception("MSI_L1C_DS found in JobOrder does not exists : " + datastrip)
    if mode == "TL":
        if tile is None:
            raise Exception("No MSI_L1C_TL found in JobOrder")
        elif not os.path.exists(tile):
            raise Exception("MSI_L1C_TL found in JobOrder does not exists : " + tile)
    if gip_l2a is None:
        raise Exception("No GIP_L2ACFG found in JobOrder")
    elif not os.path.exists(gip_l2a):
        raise Exception("GIP_L2ACFG found in JobOrder does not exists : " + gip_l2a)
    if gip_sc is None:
        raise Exception("No GIP_L2ACSC found in JobOrder")
    elif not os.path.exists(gip_sc):
        raise Exception("GIP_L2ACSC found in JobOrder does not exists : " + gip_sc)
    if gip_ac is None:
        raise Exception("No GIP_L2ACAC found in JobOrder")
    elif not os.path.exists(gip_ac):
        raise Exception("GIP_L2ACAC found in JobOrder does not exists : " + gip_ac)
    if gip_pb is None:
        raise Exception("No GIP_PROBA2 found in JobOrder")
    elif not os.path.exists(gip_pb):
        raise Exception("GIP_PROBA2 found in JobOrder does not exists : " + gip_pb)
    if gip_olqc is None:
        raise Exception("No GIP_OLQCPA found in JobOrder")
    elif not os.path.exists(gip_olqc):
        raise Exception("GIP_OLQCPA found in JobOrder does not exists : " + gip_olqc)
    if gip_jp2 is None:
        raise Exception("No GIP_JP2KPA found in JobOrder")
    elif not os.path.exists(gip_jp2):
        raise Exception("GIP_JP2KPA found in JobOrder does not exists : " + gip_jp2)
    if work is None:
        raise Exception("No WORKING found in JobOrder")
    elif not os.path.exists(work):
        raise Exception("WORKING found in JobOrder does not exists : " + work)
    if sen2cor_installdir is None:
        raise Exception("No SEN2COR_DIR_INSTALL found in JobOrder")
    elif not os.path.exists(sen2cor_installdir):
        raise Exception("SEN2COR_DIR_INSTALL found in JobOrder does not exists : " + sen2cor_installdir)

    if olqc_installdir is None:
        raise Exception("No OLQC_DIR_INSTALL found in JobOrder")
    elif not os.path.exists(olqc_installdir):
        raise Exception("OLQC_DIR_INSTALL found in JobOrder does not exists : " + olqc_installdir)

    if compresstile_installdir is None:
        raise Exception("No COMPRESS_TILE_IMAGE_DIR_INSTALL found in JobOrder")
    elif not os.path.exists(compresstile_installdir):
        raise Exception(
            "COMPRESS_TILE_IMAGE_DIR_INSTALL found in JobOrder does not exists : " + compresstile_installdir)

    if format_mtd_ds_l2a_installdir is None:
        raise Exception("No FORMAT_METADATA_DS_DIR_INSTALL found in JobOrder")
    elif not os.path.exists(format_mtd_ds_l2a_installdir):
        raise Exception(
            "FORMAT_METADATA_DS_DIR_INSTALL found in JobOrder does not exists : " + format_mtd_ds_l2a_installdir)

    if format_mtd_tl_l2a_installdir is None:
        raise Exception("No FORMAT_METADATA_TL_DIR_INSTALL found in JobOrder")
    elif not os.path.exists(format_mtd_tl_l2a_installdir):
        raise Exception(
            "FORMAT_METADATA_TL_DIR_INSTALL found in JobOrder does not exists : " + format_mtd_tl_l2a_installdir)

    if mode == "DS":
        if output_ds is None:
            raise Exception("No MSI_L2A_DS found in JobOrder outputs")
        elif not os.path.exists(output_ds):
            raise Exception("MSI_L2A_DS found in JobOrder does not exists : " + output_ds)
    if mode == "TL":
        if output_tl is None:
            raise Exception("No MSI_L2A_TL found in JobOrder outputs")
        elif not os.path.exists(output_tl):
            raise Exception("MSI_L2A_TL found in JobOrder does not exists : " + output_tl)

    if output_kpi is None:
        raise Exception("No KPI_L2A found in JobOrder outputs")
    elif not os.path.exists(output_kpi):
        raise Exception("KPI_L2A found in JobOrder does not exists : " + output_kpi)

    # Read datastrip info
    datastrip_mtd = FileUtils.glob_one(datastrip, "S2*MTD_L1C*xml")
    datastrip_reader = DatastripReader.DatastripReader(datastrip_mtd)
    ds_sensing_start = datastrip_reader.get_sensing_start()
    ds_sensing_stop = datastrip_reader.get_sensing_stop()
    ds_reception_station = datastrip_reader.get_reception_station()

    # prepare Sen2Cor setup
    sen2cor_env = os.environ.copy()
    sen2cor_env.pop("LD_LIBRARY_PATH", None)
    sen2cor_env.pop("PYTHONPATH", None)
    sen2cor_env.pop("PYTHONHOME", None)
    sen2cor_env.pop("PYTHONEXECUTABLE", None)
    sen2cor_env.pop("PYTHONUSERBASE", None)
    sen2cor_env["SEN2COR_HOME"] = work
    sen2cor_env["SEN2COR_BIN"] = os.path.join(sen2cor_installdir, "lib/python2.7/site-packages/sen2cor")
    # set numeric locale to C
    sen2cor_env["LC_NUMERIC"] = "C"
    # set GDAL_DATA
    sen2cor_env["GDAL_DATA"] = os.path.join(sen2cor_installdir, "share/gdal")
    sen2cor_env["GDAL_DRIVER_PATH"] = "disable"

    print(sen2cor_env)

    global_start_time = datetime.datetime.now()

    if mode == "DS":
        # working dir
        start_time = datetime.datetime.now()
        work_sen2core_ds = os.path.join(work, "Sen2Core_tmp_ds_" + dateoflogs)
        work_ds = os.path.join(work_sen2core_ds, "DS")
        sen2cor_env["SEN2COR_HOME"] = work_sen2core_ds
        FileUtils.create_directory(work_ds)
        print("sen2cor install dir= " + sen2cor_installdir)
        print("datastrip=" + datastrip)
        print("work_dir=" + work_sen2core_ds)
        print("output_dir=" + work_ds)
        print("processing_centre=" + processing_center)
        print("archiving_centre=" + archiving_center)
        print("GIP_L2A=" + gip_l2a)
        print("GIP_L2A_PB=" + gip_pb)
        print("GIP_L2A_AC=" + gip_ac + " --GIP_L2A_SC=" + gip_sc)
        print("Launching Sen2cor " + sen2cor_installdir + "in datastrip mode")
        err_code = SystemUtils.launch_command(
            os.path.join(sen2cor_installdir, "bin/python2.7") +
            " -s " + os.path.join(sen2cor_installdir, "lib/python2.7/site-packages/sen2cor/L2A_Process.py") +
            " --mode generate_datastrip " +
            " --datastrip " + datastrip +
            " --work_dir " + work_sen2core_ds +
            " --output_dir " + work_ds +
            " --processing_centre " + processing_center +
            " --archiving_centre " + archiving_center +
            " --GIP_L2A " + gip_l2a +
            " --GIP_L2A_PB " + gip_pb +
            " --GIP_L2A_AC " + gip_ac +
            " --GIP_L2A_SC " + gip_sc,
            env=sen2cor_env)

        if err_code != 0:
            raise Exception("Error while running datastrip Sen2Cor mode")

        # Find the output datastip
        temp_sen_ds = FileUtils.glob_one(work_ds, "*L2A_DS*")
        if temp_sen_ds is None:
            raise Exception("No datastrip found in the Sen2Cor datastrip output directory")
        print("L2A datastrip found : " + temp_sen_ds)
        print("L2A Sen2Cor Datastrip done in : " + str(
            (datetime.datetime.now() - start_time).seconds) + " seconds")

        start_time = datetime.datetime.now()
        # Launch the FORMAT_METADATA_DS_L2A
        work_format_ds = os.path.join(work, "format_mdt_ds_" + dateoflogs)
        FileUtils.create_directory(work_format_ds)
        # Create the JobOrder
        job_order_content = create_JobOrder_FORMAT_METADATA_DS_L2A(archiving_center, "04.00", processing_center,
                                                                   gip_jp2, gip_olqc,
                                                                   work_ds, ds_reception_station, ds_sensing_start,
                                                                   ds_sensing_stop,
                                                                   work_format_ds, os.path.join(work_format_ds,
                                                                                                "format_MTD_DS_L2A_report.xml"),
                                                                   work_ds)
        job_order_filename = work_format_ds + os.sep + "JobOrder_FORMAT_METADATA_DS_L2A.xml"
        with open(job_order_filename, "w") as fh:
            fh.write(job_order_content)
            print("JobOrder writed in " + job_order_filename)

        err_code = SystemUtils.launch_command(
            os.path.join(format_mtd_ds_l2a_installdir,
                         "scripts/FORMAT_METADATA_DS_L2A.bash") + " " + job_order_filename)
        if err_code != 0:
            raise Exception("Error while running FORMAT_METADATA_DS_L2A mode")

        print("L2A Format datastrip done in : " + str(
            (datetime.datetime.now() - start_time).seconds) + " seconds")

        # Launch the OLQC
        start_time = datetime.datetime.now()
        work_olqc = os.path.join(work, "olqc_" + dateoflogs)
        FileUtils.create_directory(work_olqc)

        # Create the JobOrder
        product_list = [temp_sen_ds]

        job_order_content = create_JobOrder_OLQC(gip_pb, gip_olqc, os.path.join(work_olqc, "PERSISTENT_RESOURCES"),
                                                 product_list, work_olqc, os.path.join(work_olqc, "olqc_report.xml"))
        job_order_filename = work_olqc + os.sep + "JobOrder_OLQC.xml"
        with open(job_order_filename, "w") as fh:
            fh.write(job_order_content)
            print("JobOrder writed in " + job_order_filename)

        err_code = SystemUtils.launch_command(
            os.path.join(olqc_installdir, "scripts/OLQC.bash") + " " + job_order_filename)

        if err_code != 0:
            raise Exception("Error while running OLQC mode")
        print("L2A OLQC DS done in : " + str(
            (datetime.datetime.now() - start_time).seconds) + " seconds")

        # Generated the Inventory file
        work_inventory = os.path.join(work, "inventory_" + dateoflogs)
        FileUtils.create_directory(work_inventory)
        inventory_log_file = os.path.join(work_inventory, "inventoryDS.log")
        inventory_err_file = os.path.join(work_inventory, "inventoryDS.err")

        inventory_proc = InventoryL2A.InventoryL2A(inventory_log_file, inventory_err_file)
        inventory_proc.inventory_l2a_ds("20212121", ds_reception_station, work_inventory, temp_sen_ds, datastrip)

        # Verify product structure
        PDIVerifier.verify_l2_datastrip(temp_sen_ds)

        # Tar the product to the output
        try:
            tar_directory(temp_sen_ds, os.path.join(output_ds, os.path.basename(temp_sen_ds) + ".tar"))
        except OSError as e:
            print("Error while tar " + temp_sen_ds + " to " + output_ds)
        except ValueError as e:
            print("Error while tar " + temp_sen_ds + " to " + output_ds)

        # copy report to output folder
        list_of_reports = ["GENERAL_QUALITY.xml", "RADIOMETRIC_QUALITY.xml", "FORMAT_CORRECTNESS.xml",
                           "SENSOR_QUALITY.xml", "GEOMETRIC_QUALITY.xml"]
        ds_dirname = os.path.basename(temp_sen_ds)
        ds_shortname = ds_dirname[:ds_dirname.rfind("_") + 1]
        for r in list_of_reports:
            report = FileUtils.glob_one(os.path.join(temp_sen_ds, "QI_DATA"), r)
            report_out = os.path.join(output_ds,
                                      ds_shortname + os.path.splitext(os.path.basename(report))[0] + "_report.xml")
            print("Copying " + report + " to " + report_out)
            shutil.copyfile(report, report_out)

        # Copy the KPIs
        mtd_ds_l2a = FileUtils.glob_one(temp_sen_ds, "MTD_DS.xml")
        kpi_mtd_ds_l2a = os.path.join(output_kpi, os.path.basename(temp_sen_ds) + "_MTD_DS.xml")
        print("Copy " + mtd_ds_l2a + " to " + kpi_mtd_ds_l2a)
        shutil.copyfile(mtd_ds_l2a, kpi_mtd_ds_l2a)
        kpi_proba2 = os.path.join(output_kpi, os.path.basename(gip_pb))
        print("Copy " + gip_pb + " to " + kpi_proba2)
        shutil.copyfile(gip_pb, kpi_proba2)
        # IdpInfos
        kpi_idp_infos = os.path.join(output_kpi, os.path.basename(temp_sen_ds) + "_idp_infos.xml")
        template_idp_infos = os.path.join(os.path.dirname(os.path.realpath(__file__)),
                                          "idp_infos.xml")
        idp_info_hdl = IdpInfos.IdpInfos(template_idp_infos)
        idp_info_hdl.add("Sen2Cor", "02.10.00")
        idp_info_hdl.add("OLQC", "05.00.00")
        idp_info_hdl.add("CompressTileImage", "05.00.00")
        idp_info_hdl.add("FORMAT_METADATA_DS_L2A", "05.00.01")
        idp_info_hdl.add("FORMAT_METADATA_TILE_L2A", "05.00.01")
        idp_info_hdl.write_to_file(kpi_idp_infos)
        # End
        print("L2A DS done in : " + str(
            (datetime.datetime.now() - global_start_time).seconds) + " seconds")
    else:
        # Tile mode
        start_time = datetime.datetime.now()
        work_sen2core_ds_tar = os.path.join(work, "Sen2Core_tmp_ds_" + dateoflogs)
        work_ds_tar = os.path.join(work_sen2core_ds_tar, "DS")
        FileUtils.create_directory(work_ds_tar)
        l2_ds_tar = FileUtils.glob_one(output_ds, "S2*L2A_DS*tar")
        if l2_ds_tar is None:
            raise Exception("Unable to find L2A DS in " + output_ds)
        print("Untar " + l2_ds_tar + " to " + work_ds_tar)
        untar_to_directory(l2_ds_tar, work_ds_tar)
        temp_sen_ds = FileUtils.glob_one(work_ds_tar, "*L2A_DS*")
        if temp_sen_ds is None:
            raise Exception("No datastrip found in the Sen2Cor datastrip output directory")
        print("L2A datastrip found : " + temp_sen_ds)

        work_sen2core_tl = os.path.join(work, "Sen2Core_tmp_tl_" + dateoflogs)
        work_tl = os.path.join(work_sen2core_tl, "TL")
        FileUtils.create_directory(work_tl)
        sen2cor_env["SEN2COR_HOME"] = work_sen2core_tl
        img_database = os.path.join(work_sen2core_tl, "img_database")
        res_database = os.path.join(work_sen2core_tl, "res_database")
        print("img_database: " + img_database)
        print("res_database: " + res_database)
        err_code = SystemUtils.launch_command(os.path.join(sen2cor_installdir, "bin/python2.7") +
                                              " -s "
                                              + os.path.join(sen2cor_installdir,
                                                             "lib/python2.7/site-packages/sen2cor/L2A_Process.py") +
                                              " --mode process_tile " +
                                              " --resolution 10 " +
                                              " --datastrip " + temp_sen_ds +
                                              " --processing_centre " + processing_center +
                                              " --archiving_centre " + archiving_center +
                                              " --work_dir " + work_sen2core_tl +
                                              " --output_dir " + work_tl +
                                              " --tile " + tile +
                                              " --GIP_L2A " + gip_l2a +
                                              " --GIP_L2A_PB " + gip_pb +
                                              " --GIP_L2A_AC " + gip_ac +
                                              " --GIP_L2A_SC " + gip_sc +
                                              " --img_database_dir " + img_database +
                                              " --res_database_dir " + res_database + " --raw",
                                              env=sen2cor_env)

        if err_code != 0:
            raise Exception("Error while running tile Sen2Cor mode")

        temp_sen_tl = FileUtils.glob_one(work_tl, "*L2A_TL*")
        print("L2A Sen2Cor Tile done in : " + str(
            (datetime.datetime.now() - start_time).seconds) + " seconds")

        # Launch CompressTileImage
        start_time = datetime.datetime.now()
        compress_tile_env = os.environ.copy()
        compress_tile_env["PYTHONPATH"] = os.path.join(compresstile_installdir, "lib/python/")

        # for each rawl do compress
        # 10m
        rawl_files = FileUtils.glob_all(os.path.join(temp_sen_tl, "IMG_DATA/R10m"), "*rawl")
        if rawl_files is not None:
            for f in rawl_files:
                out_file = os.path.splitext(f)[0] + ".jp2"
                print("Compressing : " + f + " to " + out_file)
                # find the associated gml file
                gml_file = os.path.splitext(f)[0] + ".gml"
                hdr_file = os.path.splitext(f)[0] + ".hdr"
                if not os.path.exists(gml_file):
                    raise Exception("Gml File : " + gml_file + " not found")
                err_code = SystemUtils.launch_command(
                    os.path.join(compresstile_installdir, "scripts/compress_tile_image") + " --input " +
                    f + " --jp2kpa " + gip_jp2 + " --box " + gml_file + " --output " + out_file + " --work_dir " + work,
                    env=compress_tile_env)
                if err_code != 0:
                    raise Exception("Error while running CompressTileImage")
                # Remove unwanted files
                try:
                    os.remove(f)
                except OSError as e:
                    raise Exception("Unable to remove rawl files")
                try:
                    os.remove(gml_file)
                except OSError as e:
                    raise Exception("Unable to remove gml files")
                try:
                    os.remove(hdr_file)
                except OSError as e:
                    raise Exception("Unable to remove hdr files")
        # 20m
        rawl_files = FileUtils.glob_all(os.path.join(temp_sen_tl, "IMG_DATA/R20m"), "*rawl")
        if rawl_files is not None:
            for f in rawl_files:
                out_file = os.path.splitext(f)[0] + ".jp2"
                print("Compressing : " + f + " to " + out_file)
                # find the associated gml file
                gml_file = os.path.splitext(f)[0] + ".gml"
                hdr_file = os.path.splitext(f)[0] + ".hdr"
                if not os.path.exists(gml_file):
                    raise Exception("Gml File : " + gml_file + " not found")
                err_code = SystemUtils.launch_command(
                    os.path.join(compresstile_installdir, "scripts/compress_tile_image") + " --input " +
                    f + " --jp2kpa " + gip_jp2 + " --box " + gml_file +
                    " --output " + out_file + " --work_dir " + work,
                    env=compress_tile_env)
                if err_code != 0:
                    raise Exception("Error while running CompressTileImage")
                # Remove unwanted files
                try:
                    os.remove(f)
                except OSError as e:
                    raise Exception("Unable to remove rawl files")
                try:
                    os.remove(gml_file)
                except OSError as e:
                    raise Exception("Unable to remove gml files")
                try:
                    os.remove(hdr_file)
                except OSError as e:
                    raise Exception("Unable to remove hdr files")
        # 60m
        rawl_files = FileUtils.glob_all(os.path.join(temp_sen_tl, "IMG_DATA/R60m"), "*rawl")
        if rawl_files is not None:
            for f in rawl_files:
                out_file = os.path.splitext(f)[0] + ".jp2"
                print("Compressing : " + f + " to " + out_file)
                # find the associated gml file
                gml_file = os.path.splitext(f)[0] + ".gml"
                hdr_file = os.path.splitext(f)[0] + ".hdr"
                if not os.path.exists(gml_file):
                    raise Exception("Gml File : " + gml_file + " not found")
                err_code = SystemUtils.launch_command(
                    os.path.join(compresstile_installdir, "scripts/compress_tile_image") + " --input " +
                    f + " --jp2kpa " + gip_jp2 + " --box " + gml_file +
                    " --output " + out_file + " --work_dir " + work,
                    env=compress_tile_env)
                if err_code != 0:
                    raise Exception("Error while running CompressTileImage")
                # Remove unwanted files
                try:
                    os.remove(f)
                except OSError as e:
                    raise Exception("Unable to remove rawl files")
                try:
                    os.remove(gml_file)
                except OSError as e:
                    raise Exception("Unable to remove gml files")
                try:
                    os.remove(hdr_file)
                except OSError as e:
                    raise Exception("Unable to remove hdr files")
        # QI_DATA
        rawl_files = FileUtils.glob_all(os.path.join(temp_sen_tl, "QI_DATA/"), "*rawl")
        if rawl_files is not None:
            for f in rawl_files:
                out_file = os.path.splitext(f)[0] + ".jp2"
                print("Compressing : " + f + " to " + out_file)
                # find the associated gml file
                gml_file = os.path.splitext(f)[0] + ".gml"
                hdr_file = os.path.splitext(f)[0] + ".hdr"
                if not os.path.exists(gml_file):
                    raise Exception("Gml File : " + gml_file + " not found")
                err_code = SystemUtils.launch_command(
                    os.path.join(compresstile_installdir, "scripts/compress_tile_image") + " --input " +
                    f + " --jp2kpa " + gip_jp2 + " --box " + gml_file +
                    " --output " + out_file + " --work_dir " + work,
                    env=compress_tile_env)
                if err_code != 0:
                    raise Exception("Error while running CompressTileImage")
                # Remove unwanted files
                try:
                    os.remove(f)
                except OSError as e:
                    raise Exception("Unable to remove rawl files")
                try:
                    os.remove(gml_file)
                except OSError as e:
                    raise Exception("Unable to remove gml files")
                try:
                    os.remove(hdr_file)
                except OSError as e:
                    raise Exception("Unable to remove hdr files")

        # Patch the metadata
        mtd_xml = FileUtils.glob_one(temp_sen_tl, "MTD_TL.xml")
        err_code = SystemUtils.launch_command(
            "sed -i 's+\.rawl<+\.jp2<+' " + mtd_xml)
        if err_code != 0:
            raise Exception("Error while running sed on MTD_TL.xml")

        # remove any residual hdr file ( OPR-277 )
        hdr_files = FileUtils.glob_all(os.path.join(temp_sen_tl, "IMG_DATA/R20m"), "*hdr")
        for hdr_file in hdr_files:
            try:
                os.remove(hdr_file)
            except OSError as e:
                raise Exception("Unable to remove hdr file " + hdr_file)
        hdr_files = FileUtils.glob_all(os.path.join(temp_sen_tl, "IMG_DATA/R60m"), "*hdr")
        for hdr_file in hdr_files:
            try:
                os.remove(hdr_file)
            except OSError as e:
                raise Exception("Unable to remove hdr file " + hdr_file)
        hdr_files = FileUtils.glob_all(os.path.join(temp_sen_tl, "IMG_DATA/R10m"), "*hdr")
        for hdr_file in hdr_files:
            try:
                os.remove(hdr_file)
            except OSError as e:
                raise Exception("Unable to remove hdr file " + hdr_file)

        print("L2A CompressTileImage done in : " + str(
            (datetime.datetime.now() - start_time).seconds) + " seconds")

        # Launch the FORMAT_METADATA_TL_L2A
        start_time = datetime.datetime.now()
        work_format_tl = os.path.join(work, "format_mdt_tl_" + dateoflogs)
        FileUtils.create_directory(work_format_tl)

        # find the tile ID
        # S2B_OPER_MSI_L2A_TL_CAPG_20220108T143614_A025203_T28VDJ_N04.00/
        pdi_id = os.path.basename(temp_sen_tl)
        tile_id = pdi_id[50:55]
        tile_list_file = os.path.join(work, "tile_list_file.xml")
        create_tile_list_file(tile_list_file, tile_id)

        # Create the JobOrder
        job_order_content = create_JobOrder_FORMAT_METADATA_TL_L2A(archiving_center, "04.00", processing_center,
                                                                   tile_list_file,
                                                                   gip_jp2, gip_olqc,
                                                                   work_tl, ds_reception_station, ds_sensing_start,
                                                                   ds_sensing_stop,
                                                                   work_format_tl, os.path.join(work_format_tl,
                                                                                                "format_MTD_TILE_L2A_report.xml"),
                                                                   work_tl)
        job_order_filename = work_format_tl + os.sep + "JobOrder_FORMAT_METADATA_TILE_L2A.xml"
        with open(job_order_filename, "w") as fh:
            fh.write(job_order_content)
            print("JobOrder writed in " + job_order_filename)

        err_code = SystemUtils.launch_command(
            os.path.join(format_mtd_tl_l2a_installdir,
                         "scripts/FORMAT_METADATA_TILE_L2A.bash") + " " + job_order_filename)
        if err_code != 0:
            raise Exception("Error while running FORMAT_METADATA_TILE_L2A mode")

        print("L2A Format metadata tile done in : " + str(
            (datetime.datetime.now() - start_time).seconds) + " seconds")

        # Launch the OLQC
        start_time = datetime.datetime.now()
        work_olqc = os.path.join(work, "olqc_" + dateoflogs)
        FileUtils.create_directory(work_olqc)

        # Create the JobOrder
        product_list = [temp_sen_tl]

        job_order_content = create_JobOrder_OLQC(gip_pb, gip_olqc, os.path.join(work_olqc, "PERSISTENT_RESOURCES"),
                                                 product_list, work_olqc, os.path.join(work_olqc, "olqc_report.xml"))
        job_order_filename = work_olqc + os.sep + "JobOrder_OLQC.xml"
        with open(job_order_filename, "w") as fh:
            fh.write(job_order_content)
            print("JobOrder writed in " + job_order_filename)

        err_code = SystemUtils.launch_command(
            os.path.join(olqc_installdir, "scripts/OLQC.bash") + " " + job_order_filename)

        if err_code != 0:
            raise Exception("Error while running OLQC mode")

        print("L2A OLQC tile done in : " + str(
            (datetime.datetime.now() - start_time).seconds) + " seconds")

        # Generated the Inventory file
        work_inventory = os.path.join(work, "inventory_" + dateoflogs)
        FileUtils.create_directory(work_inventory)
        inventory_log_file = os.path.join(work_inventory, "inventoryTL.log")
        inventory_err_file = os.path.join(work_inventory, "inventoryTL.err")

        inventory_proc = InventoryL2A.InventoryL2A(inventory_log_file, inventory_err_file)
        inventory_proc.inventory_l2a_tl(dateoflogs, ds_reception_station, ds_sensing_start,
                                        ds_sensing_stop, work_inventory,
                                        temp_sen_tl, tile, temp_sen_ds)

        # Verify product structure
        PDIVerifier.verify_l2_tile(temp_sen_tl)

        # TCI product
        tci_filename = FileUtils.glob_one(os.path.join(temp_sen_tl, "IMG_DATA", "R10m"), "T*_TCI_10m.jp2")
        if tci_filename is None:
            raise Exception("No TCI file found in "+os.path.join(temp_sen_tl, "IMG_DATA", "R10m"))
        print("Using file " + tci_filename + " for TCI generation")
        # Check if inventory has been generated
        if not os.path.exists(os.path.join(temp_sen_tl, "Inventory_Metadata.xml")):
            raise Exception("No Inventory_Metadata.xml file found in " + temp_sen_tl)
        tl_inventory_mtd_file = os.path.join(temp_sen_tl, "Inventory_Metadata.xml")
        tl_tci_folder = os.path.join(work_inventory, "TCI")
        if not os.path.exists(tl_tci_folder):
            FileUtils.create_directory(tl_tci_folder)
        output_tci_file = os.path.join(tl_tci_folder, os.path.basename(tci_filename))
        FileUtils.copy_file(tci_filename, output_tci_file)
        tci_inventory_mtd_file = os.path.join(tl_tci_folder, "Inventory_Metadata.xml")
        inventory_mtd_reader = InventoryMetadataReader(tl_inventory_mtd_file)
        inventory_mtd_reader.set_file_id(inventory_mtd_reader.get_file_id().replace("MSI_L2A_TL", "MSI_L2A_TC"))
        inventory_mtd_reader.set_file_name(inventory_mtd_reader.get_file_name().replace("MSI_L2A_TL", "MSI_L2A_TC"))
        inventory_mtd_reader.set_file_type(inventory_mtd_reader.get_file_type().replace("MSI_L2A_TL", "MSI_L2A_TC"))
        inventory_mtd_reader.set_data_size(os.path.getsize(output_tci_file))
        print("Writing tci inventory metadata in " + tci_inventory_mtd_file)
        inventory_mtd_reader.write_to_file(tci_inventory_mtd_file)
        inventory_process = subprocess.Popen(["exiftool", output_tci_file,
                                              "-xml+<=" + tci_inventory_mtd_file])
        status = inventory_process.wait()
        if status != 0:
            raise Exception("Error while launching inventory exif tools on tile " + output_tci_file)
        os.remove(tci_inventory_mtd_file)
        jp2_original = FileUtils.glob_one(tl_tci_folder, os.path.basename(output_tci_file) + "_original")
        if jp2_original is not None:
            os.remove(jp2_original)
        tci_filename = os.path.basename(temp_sen_tl).replace("MSI_L2A_TL", "MSI_L2A_TC") + ".jp2"
        tile_jp2_filename = os.path.join(output_tl, tci_filename)
        print("Copy the tci jp2 " + output_tci_file
              + " to " + tile_jp2_filename)
        FileUtils.copy_file(output_tci_file, tile_jp2_filename)

        # Tar the product to the output
        print("Tar the output tile "+temp_sen_ds+" to "+os.path.join(output_tl, os.path.basename(temp_sen_tl) + ".tar"))
        try:
            tar_directory(temp_sen_tl, os.path.join(output_tl, os.path.basename(temp_sen_tl) + ".tar"))
        except Exception as e:
            print("Error while tar " + temp_sen_tl + " to " + output_tl)

        list_of_reports = ["GENERAL_QUALITY.xml", "FORMAT_CORRECTNESS.xml",
                           "SENSOR_QUALITY.xml", "GEOMETRIC_QUALITY.xml"]
        tl_dirname = os.path.basename(temp_sen_tl)
        tl_shortname = tl_dirname[:tl_dirname.rfind("_") + 1]
        for r in list_of_reports:
            report = FileUtils.glob_one(os.path.join(temp_sen_tl, "QI_DATA"), r)
            report_out = os.path.join(output_tl,
                                      tl_shortname + os.path.splitext(os.path.basename(report))[0] + "_report.xml")
            print("Copying " + report + " to " + report_out)
            shutil.copyfile(report, report_out)

        print("L2A Tile done in : " + str(
            (datetime.datetime.now() - global_start_time).seconds) + " seconds")


if __name__ == "__main__":
    main()
