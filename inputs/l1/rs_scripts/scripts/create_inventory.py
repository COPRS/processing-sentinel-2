# coding=utf-8
import datetime
import os
import os.path
import argparse
import glob,StringIO
from lxml import etree as ET, objectify
import inventory
from inventory import Geo_PntType,List_Of_Geo_PntType

def open_xml_file(xml_filepath):
    """
    Open the given filepath and return the dom if not None
    :param xml_filepath:
    :return:
    """
    with open(xml_filepath, 'r') as f:
        dom = ET.parse(f)

    if dom is not None:
        return dom
    else:
        raise Exception("Can't get the node of given xml file {}".format(xml_filepath))


def save_to_xml_file(root_node, xml_filepath):
    print("Writing "+ xml_filepath)
    tree = ET.ElementTree(root_node)

    f = open(xml_filepath, "w")
    f.write(ET.tostring(tree, pretty_print=True, xml_declaration=True, method="xml",
                        encoding="UTF-8").decode())
    f.close()
    print(xml_filepath +" Created", )


def get_root_xml(xml_filepath, deannotate=False):
    """
    Get the root of the given xml_filepath
    :param xml_filepath:
    :return:
    """
    dom = open_xml_file(xml_filepath)
    if deannotate:
        return deannotate_xml(dom.getroot())
    else:
        return dom.getroot()


def deannotate_xml(root):
    """
    Remove the namespaces of the current root.
    See :
    https:#stackoverflow.com/questions/18159221/remove-namespace-and-prefix-from-xml-in-python-using-lxml
    http:#www.goodmami.org/2015/11/04/python-xpath-and-default-namespaces.html
    :param root:
    :return:
    """
    ####
    for elem in root.iter():
        if not hasattr(elem.tag, 'find'):
            continue  # (1)
        i = elem.tag.find('}')
        if i >= 0:
            elem.tag = elem.tag[i + 1:]
    objectify.deannotate(root, xsi=True)

    return root
    ####


def inventory_dom_to_string(dom_object):
    output = StringIO.StringIO()
    output.write('<?xml version="1.0" ?>\n')
    dom_object.export(
        output, 0, name_='Inventory_Metadata',
        namespacedef_='',
        pretty_print=True)
    return output


def get_only_value(dom, xpath_request, namespaces={}, check=False):
    """
    Get the value requested by the given xpath
    If only one value is found, return it,
    Else raise an error

    :param dom:
    :type dom:
    :param xpath_request:
    :type xpath_request:
    :param namespaces:
    :type namespaces:
    """
    if namespaces != {}:
        xpath_list_result = dom.xpath(xpath_request, namespaces=namespaces)
    else:
        xpath_list_result = dom.xpath(xpath_request)

    if len(xpath_list_result) == 1 and xpath_list_result[0] is not None:
        return xpath_list_result[0]
    elif len(xpath_list_result) > 1:
        print("Fail to get the only value for %s in given dom. There several results",
                     xpath_request)
        raise Exception("Fail to get the only value for %s in given dom. There several results" % xpath_request)
    else:
        # allows to check if the requested xpath is in the node
        if check:
            return None
        else:
            print("No result found for %s in the given dom.", xpath_request)
            raise Exception("No result found for %s in the given dom." % xpath_request)


def extract_meta_dict_ds(ds_meta_filename, get_ext_pos = False):
    ds_meta_dict = {}
    ds_meta_xml = open_xml_file(ds_meta_filename)
    start_validity = get_only_value(ds_meta_xml, "//DATASTRIP_SENSING_START").text
    ds_meta_dict["VALIDITY_START"] = "UTC="+start_validity[:-1]+"000"
    print(ds_meta_dict["VALIDITY_START"])
    stop_validity = get_only_value(ds_meta_xml, "//DATASTRIP_SENSING_STOP").text
    ds_meta_dict["VALIDITY_STOP"] = "UTC=" + stop_validity[:-1] + "000"
    print(ds_meta_dict["VALIDITY_STOP"])
    footprints = ""
    creation_date = get_only_value(ds_meta_xml, "//Processing_Info/UTC_DATE_TIME").text
    ds_meta_dict["CREATION_DATE"] = "UTC=" + creation_date[:-1] + ".000000"
    print(ds_meta_dict["CREATION_DATE"])

    ds_meta_dict["PDI_ID"] = os.path.basename(ds_meta_filename)
    processing_station = get_only_value(ds_meta_xml, "//Processing_Info/PROCESSING_CENTER").text
    ds_meta_dict["process_station"] = processing_station
    baseline = get_only_value(ds_meta_xml, "//Processing_Info/PROCESSING_BASELINE").text
    ds_meta_dict["baseline"] = baseline
    datatake_info = get_only_value(ds_meta_xml, "//Datatake_Info").get("datatakeIdentifier")
    ds_meta_dict["datatake_info"] = datatake_info
    ds_meta_dict["orbit"] = datatake_info.split("_")[2]
    if datatake_info.startswith("GS2B"):
        ds_meta_dict["satellite"] = "S2B"
    else:
        ds_meta_dict["satellite"] = "S2A"
    ascending = get_only_value(ds_meta_xml, "//SENSING_ORBIT_DIRECTION").text != "DESCENDING"
    if ascending:
        ds_meta_dict["ASCENDING"] = "True"
    else:
        ds_meta_dict["ASCENDING"] = "False"
    #try to get EXT_POS_LIST
    if get_ext_pos:
        try:
            ext_pos = get_only_value(ds_meta_xml, "//EXT_POS_LIST").text
            ds_meta_dict["EXT_POS"] = ext_pos
        except:
            print("No ext pos list in datastrip")
            exit(1)
    return ds_meta_dict


def extract_meta_dict_tl(tl_meta_filename, ds_meta_dict, get_ext_pos = False):
    tl_meta_dict = {}
    tl_meta_dict.update(ds_meta_dict)
    tl_meta_dict["parent_id"] = tl_meta_dict["PDI_ID"]
    tl_meta_dict["PDI_ID"] = os.path.basename(tl_meta_filename)
    tl_meta_xml = open_xml_file(tl_meta_filename)
    creation_date = get_only_value(tl_meta_xml, "//Archiving_Info/ARCHIVING_TIME").text
    tl_meta_dict["CREATION_DATE"] = "UTC=" + creation_date[:-1] + ".000000"
    print(tl_meta_dict["CREATION_DATE"])

    # try to get EXT_POS_LIST
    if get_ext_pos:
        try:
            ext_pos = get_only_value(tl_meta_xml, "//EXT_POS_LIST").text
            tl_meta_dict["EXT_POS"] = ext_pos
        except:
            print("No ext pos list in gr/tile")
            exit(1)

    return tl_meta_dict


def get_utc_from_datetime(datet):
    return datetime.datetime.strftime(datet, "UTC=%Y-%m-%dT%H:%M:%S.%f")


def modify_Inventory(inventory_input, inventory_output, meta_dict):
    inventory_in = inventory.parse(inventory_input, True)

    inventory_in.set_Processing_Station(meta_dict["process_station"])
    inventory_in.set_File_ID(meta_dict["PDI_ID"])
    inventory_in.set_File_Name(meta_dict["PDI_ID"])
    inventory_in.set_Source("L1C_Processor")
    inventory_in.set_Start_Orbit_Number(meta_dict["orbit"])
    inventory_in.set_Stop_Orbit_Number(meta_dict["orbit"])
    inventory_in.set_Generation_Time(meta_dict["CREATION_DATE"])
    inventory_in.set_Validity_Start(meta_dict["VALIDITY_START"])
    inventory_in.set_Validity_Stop(meta_dict["VALIDITY_STOP"])
    inventory_in.set_Ascending_Flag(meta_dict["ASCENDING"])
    inventory_in.set_File_Type(meta_dict["File_Type"])
    inventory_in.set_Satellite_Code(meta_dict["satellite"])

    if "parent_id" in meta_dict.keys():
        inventory_in.set_Parent_ID(meta_dict["parent_id"])

    #process time
    inventory_in.set_Generation_Time(get_utc_from_datetime(datetime.datetime.now()))

    #footprint
    geo_pt_list = List_Of_Geo_PntType()
    for p in meta_dict["Global_Footprint"]:
        p_split = p.split(",")
        geo_pt = Geo_PntType(LATITUDE=float(p_split[0]), LONGITUDE=float(p_split[1]))
        geo_pt_list.add_Geo_Pnt(geo_pt)

    geo_pt_list.set_count(len(meta_dict["Global_Footprint"]))
    inventory_in.get_Geographic_Localization().set_List_Of_Geo_Pnt(geo_pt_list)


    #write
    content = inventory_dom_to_string(inventory_in).getvalue()
    content = content.replace("    ", "  ")

    with open(inventory_output, "w") as fh:
        fh.write(content)
        print("Inventory writed in " + inventory_output)


def main():
    #INPUTS ARGUMENTS
    parser = argparse.ArgumentParser(description='This script create an inventory metadata file for each DS and TILE')
    parser.add_argument('-t', '--tile', dest='tile', type=str, required=False, help='Path tiles')
    parser.add_argument('-d', '--ds', dest='ds', type=str, required=True, help='Path ds')
    parser.add_argument('-a', '--ai', dest='ai', type=str, required=False, help='Path footprint, granule or tiles containing footprint in QI_DATA folder')
    parser.add_argument('-f', '--footprint', dest='footprint', type=str, required=False, help='Path footprint tile or GR')

    args = parser.parse_args()

    

    count = 0
    site1 = args.tile
    site2 = args.ds
    footprint_ds_ext_pos = False
    site3 = ""
    if not args.ai:
        footprint_ds_ext_pos = True
    else:
        site3 = args.ai
    footprint_gr_ext_pos = False
    site4 = ""
    if not args.footprint:
        footprint_gr_ext_pos = True
    else:
        site4 = args.footprint
    #Glod DS
    ds_folders = glob.glob(os.path.join(site2,"S2*DS*"))

    if not footprint_ds_ext_pos and not os.path.exists(site3):
        raise Exception("No footprint AI found")
    if not ds_folders:
        raise Exception("No Datastrip folder found")
    elif len(ds_folders) != 1:
       print("More than one datastrip found")
    else:
       print("Datastrip found : "+ds_folders[0])
    
    #DS footprint AI
    footprint_ds_str = ""
    if not footprint_ds_ext_pos:
        xml_ai = get_root_xml(site3,deannotate=True)
        geo_node = get_only_value(xml_ai,"//featureMember")
        footprint_ds_str = geo_node.getchildren()[0].getchildren()[0].getchildren()[0].getchildren()[0].getchildren()[0].getchildren()[0].text

    ds_folder = ds_folders[0]
    ds_metadata = glob.glob(os.path.join(ds_folder,"S2*DS*xml"))
    #Do datastrip inventory
    ds_meta_dict = extract_meta_dict_ds(ds_metadata[0], footprint_ds_ext_pos)
    ds_meta_dict["Global_Footprint"] = footprint_ds_str.split(" ")
    if "L1C" in ds_metadata[0]:
        ds_meta_dict["File_Type"] = "MSI_L1C_DS"
    if "L1B" in ds_metadata[0]:
        ds_meta_dict["File_Type"] = "MSI_L1B_DS"
    modify_Inventory(os.path.join(os.path.dirname(os.path.realpath(__file__)), "inventory_ds.xml"),
                     os.path.join(ds_folder,"Inventory_Metadata.xml"), ds_meta_dict)

    do_tile = False
    do_gr = False
    if args.tile:
        if "L1C" in ds_metadata:
            do_tile = True
        else:
            do_gr = True
    if not do_gr and not do_tile:
        print("Nothing to do next")
        exit(0)

    #Do tile inventory
    if do_tile:
        for t in glob.glob(os.path.join(site1, "S2*TL*")):
            if t.endswith("tar"):
                continue
            print("Treating: "+os.path.basename(t))
            tl_metadata = glob.glob(os.path.join(t, "S2*TL*xml"))
            # Do datastrip inventory
            tl_meta_dict = extract_meta_dict_tl(tl_metadata[0],ds_meta_dict)
            
            # Tl footprint
            xml_ai = get_root_xml(os.path.join(site4,os.path.basename(t)+"/QI_DATA/VECTOR/footprint.gml"), deannotate=True)
            geo_node = get_only_value(xml_ai, "//featureMember")
            footprint_tl_str = \
                               geo_node.getchildren()[0].getchildren()[0].getchildren()[0].getchildren()[0].getchildren()[0].getchildren()[
                                   0].text
            tl_meta_dict["Global_Footprint"] = footprint_tl_str.split(" ")
            if args.mode == "L1C":
                tl_meta_dict["File_Type"] = "MSI_L1C_TL"
            if args.mode == "L1B":
                tl_meta_dict["File_Type"] = "MSI_L1B_GR"
            #do modifiy
            modify_Inventory(os.path.join(os.path.dirname(os.path.realpath(__file__)), "inventory_gr.xml"), os.path.join(t, "Inventory_Metadata.xml"), tl_meta_dict)
    # Do gr inventory
    if do_gr:
        for t in glob.glob(os.path.join(site1, "S2*GR*")):
            if t.endswith("tar"):
                continue
            print("Treating: " + os.path.basename(t))
            tl_metadata = glob.glob(os.path.join(t, "S2*GR*xml"))
            # Do datastrip inventory
            tl_meta_dict = extract_meta_dict_tl(tl_metadata[0], ds_meta_dict,footprint_gr_ext_pos)

            # Tl footprint
            if not footprint_gr_ext_pos:
                xml_ai = get_root_xml(os.path.join(site4, os.path.basename(t) + "/QI_DATA/VECTOR/footprint.gml"),
                                      deannotate=True)
                geo_node = get_only_value(xml_ai, "//featureMember")
                footprint_tl_str = \
                    geo_node.getchildren()[0].getchildren()[0].getchildren()[0].getchildren()[0].getchildren()[
                        0].getchildren()[
                        0].text
                real_footprint = footprint_tl_str.split(" ")
            else:
                footprint_tl_str = tl_meta_dict["EXT_POS"]
                print(footprint_tl_str)
                tmp_footprint_str = footprint_tl_str.split(" ")
                real_footprint = []
                for f in range(0, len(tmp_footprint_str)/3):
                    real_footprint.append(str(tmp_footprint_str[f*3]+","+tmp_footprint_str[f*3+1]))

            if "L1C" in ds_metadata[0]:
                tl_meta_dict["File_Type"] = "MSI_L1C_TL"
            if "L1B" in ds_metadata[0]:
                tl_meta_dict["File_Type"] = "MSI_L1B_GR"
            tl_meta_dict["Global_Footprint"] = real_footprint
            # do modifiy
            print(tl_meta_dict)
            modify_Inventory(os.path.join(os.path.dirname(os.path.realpath(__file__)), "inventory_gr.xml"),
                             os.path.join(t, "Inventory_Metadata.xml"), tl_meta_dict)

if __name__ == "__main__":
    main()
