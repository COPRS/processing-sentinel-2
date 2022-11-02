# coding=utf-8
import os

import FileUtils


class PDIVerifier(object):

    @staticmethod
    def verify_l2_tile(tile_folder):
        # QI_DATA files
        list_of_qi_data_files = ["MSK_QUALIT_B08.jp2", "MSK_QUALIT_B10.jp2", "MSK_QUALIT_B11.jp2", "MSK_QUALIT_B12.jp2",
                                 "MSK_CLDPRB_60m.jp2", "MSK_SNWPRB_60m.jp2", "FORMAT_CORRECTNESS.xml",
                                 "GEOMETRIC_QUALITY.xml", "MSK_QUALIT_B09.jp2", "MSK_QUALIT_B8A.jp2", "L2A_QUALITY.xml",
                                 "MSK_SNWPRB_20m.jp2", "MSK_CLDPRB_20m.jp2", "T*_PVI.jp2",
                                 "GENERAL_QUALITY.xml", "SENSOR_QUALITY.xml", "MSK_CLASSI_B00.jp2",
                                 "MSK_DETFOO_B01.jp2",
                                 "MSK_DETFOO_B02.jp2", "MSK_DETFOO_B03.jp2", "MSK_DETFOO_B04.jp2", "MSK_DETFOO_B05.jp2",
                                 "MSK_DETFOO_B06.jp2", "MSK_DETFOO_B07.jp2", "MSK_DETFOO_B08.jp2", "MSK_DETFOO_B09.jp2",
                                 "MSK_DETFOO_B10.jp2", "MSK_DETFOO_B11.jp2", "MSK_DETFOO_B12.jp2", "MSK_DETFOO_B8A.jp2",
                                 "MSK_QUALIT_B01.jp2", "MSK_QUALIT_B02.jp2", "MSK_QUALIT_B03.jp2", "MSK_QUALIT_B04.jp2",
                                 "MSK_QUALIT_B05.jp2", "MSK_QUALIT_B06.jp2", "MSK_QUALIT_B07.jp2"]

        # 10 meters bands
        list_of_img_data_r10 = ["T*_B02_10m.jp2", "T*_B03_10m.jp2", "T*_B04_10m.jp2", "T*_B08_10m.jp2",
                                "T*_AOT_10m.jp2", "T*_WVP_10m.jp2", "T*_TCI_10m.jp2"]

        # 20 meters bands
        list_of_img_data_r20 = ["T*_B11_20m.jp2", "T*_B12_20m.jp2", "T*_SCL_20m.jp2", "T*_AOT_20m.jp2",
                                "T*_WVP_20m.jp2", "T*_TCI_20m.jp2", "T*_B01_20m.jp2",
                                "T*_B02_20m.jp2", "T*_B03_20m.jp2", "T*_B04_20m.jp2", "T*_B05_20m.jp2",
                                "T*_B06_20m.jp2", "T*_B07_20m.jp2", "T*_B8A_20m.jp2"]

        list_of_img_data_r60 = ["T*_B09_60m.jp2", "T*_B11_60m.jp2", "T*_B12_60m.jp2", "T*_SCL_60m.jp2",
                                "T*_AOT_60m.jp2", "T*_WVP_60m.jp2", "T*_TCI_60m.jp2", "T*_B01_60m.jp2",
                                "T*_B02_60m.jp2", "T*_B03_60m.jp2", "T*_B04_60m.jp2", "T*_B05_60m.jp2",
                                "T*_B06_60m.jp2", "T*_B07_60m.jp2", "T*_B8A_60m.jp2"]

        list_of_aux_data = ["AUX_ECMWFT", "AUX_CAMSFO"]

        # MTD
        list_of_first_level_files = ["MTD_TL.xml", "manifest.safe", "Inventory_Metadata.xml"]

        map_to_verify = [(os.path.join(tile_folder), list_of_first_level_files),
                         (os.path.join(tile_folder, "IMG_DATA", "R20m"), list_of_img_data_r20),
                         (os.path.join(tile_folder, "IMG_DATA", "R10m"), list_of_img_data_r10),
                         (os.path.join(tile_folder, "IMG_DATA", "R60m"), list_of_img_data_r60),
                         (os.path.join(tile_folder, "QI_DATA"), list_of_qi_data_files),
                         (os.path.join(tile_folder, "AUX_DATA"), list_of_aux_data)
                         ]

        for t in map_to_verify:
            folder = t[0]
            if not os.path.isdir(folder):
                raise Exception("No folder " + folder + " found in Tile : " + tile_folder)
            for r in t[1]:
                file_to_search = os.path.join(folder, r)
                if not os.path.exists(file_to_search):
                    file_found = FileUtils.glob_one(folder, r)
                    if file_found is None:
                        raise Exception("No file "+r+" found in folder of Tile : "+folder)

    @staticmethod
    def verify_l2_datastrip(datastrip_folder):
        # QI_DATA files
        list_of_qi_data_files = ["FORMAT_CORRECTNESS.xml", "GEOMETRIC_QUALITY.xml",
                                 "GENERAL_QUALITY.xml", "SENSOR_QUALITY.xml", "RADIOMETRIC_QUALITY.xml"]

        # MTD
        list_of_first_level_files = ["MTD_DS.xml", "manifest.safe", "Inventory_Metadata.xml"]

        map_to_verify = [(os.path.join(datastrip_folder), list_of_first_level_files),
                         (os.path.join(datastrip_folder, "QI_DATA"), list_of_qi_data_files)
                         ]

        for t in map_to_verify:
            folder = t[0]
            if not os.path.isdir(folder):
                raise Exception("No folder " + folder + " found in Datastrip : " + datastrip_folder)
            for r in t[1]:
                file_to_search = os.path.join(folder, r)
                if not os.path.exists(file_to_search):
                    file_found = FileUtils.glob_one(folder, r)
                    if file_found is None:
                        raise Exception("No file "+r+" found in folder of Datastrip : "+folder)
