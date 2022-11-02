from os import listdir

import os, sys, tarfile
import shutil
import os.path
import argparse

#INPUTS ARGUMENTS
parser = argparse.ArgumentParser(description='This script copy .jp2 files into a directory and make the directory a .tar')
parser.add_argument('-p', '--path', dest='path', type=str, required=True, help='Path where .jp2 files are placed. Mandatory')

args = parser.parse_args()

def tar_jp2_files(directory):
    jp2 = '.jp2'
    for file in os.listdir(directory):
        if os.path.isfile(os.path.join(directory, file)):
            if jp2 in file:
                file_path = os.path.join(directory, file)
                dir_name = file_path.replace(jp2, '')
                dir_path = os.path.join(dir_name, file)

                try:
                    original_umask = os.umask(0)
                    os.makedirs(dir_name, 0777)
                finally:
                    os.umask(original_umask)

                os.rename(file_path, dir_path)

                tar = tarfile.open("{tar_name}.tar".format(tar_name=dir_name), "w:tar")
                tar.add(dir_path, arcname=dir_path.replace(directory, ''), recursive=False)

                shutil.rmtree(dir_name)

tar_jp2_files(args.path)

