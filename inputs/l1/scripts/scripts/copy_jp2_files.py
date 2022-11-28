import os, shutil, sys, tarfile
from os import listdir
import os.path
import argparse

#INPUTS ARGUMENTS
parser = argparse.ArgumentParser(description='This script copy .jp2 files from FORMAT_IMG_L1C and FORMAT_IMG_PVI_TCI to FORMAT_METADATA_TILE_L1C, and optionally create a .tar file from every directory')
parser.add_argument('-p', '--path', dest='path', type=str, required=True, help='Path where FORMAT_IMG_L1C, FORMAT_METADATA_TILE_L1C and FORMAT_IMG_PVI_TCI are placed. Mandatory')
parser.add_argument('--copy', dest='copy_file', default=False, action='store_true', help='If "True" copy .jp2 files. Required, True by default')
parser.add_argument('--tar', dest='tar_file', default=False, action='store_true', help='If "True" create .tar files. Optional, False by default')

args = parser.parse_args()


count = 0
tar_opt = args.tar_file
copy_opt = args.copy_file
print("tar")
print(args.tar_file)
print("copy")
print(args.copy_file)
site1 = os.path.join(str(args.path),"FORMAT_IMG_L1C/output/PDI_DS_TILE_LIST/")
site2 = os.path.join(str(args.path),"FORMAT_METADATA_TILE_L1C/output/PDI_DS_TILE_LIST/")
site3 = os.path.join(str(args.path),"FORMAT_IMG_PVI_TCI/output/PDI_DS_PVI_LIST/")
array_files = []
matrix_num = []

#This function compare the number of directories in FORMAT_IMG_L1C/output/PDI_DS_TILE_LIST, FORMAT_METADATA_TILE_L1C/output/PDI_DS_TILE_LIST and FORMAT_IMG_PVI_TCI/output/PDI_DS_PVI_LIST
#if the number is the same, return 0 and continue, otherwise, return 1 and the script finish
def compare_directories(directory, num):
    count = 0  
    for root, dir, files_site_2 in os.walk(site2):
        print("Files in FORMAT_METADATA_TILE_L1C: " + str(len(dir)) + "\n")
        dir_FORMAT = dir
        break

    if num == 0:
        dir_name = "FORMAT_IMG_L1C"
    else:
        dir_name = "FORMAT_IMG_PVI_TCI"

    if len(directory) == len(dir_FORMAT):
        for t in directory:
            if t in dir_FORMAT:
                count = count +1
        if count == len(directory):
            print("Files from ", dir_name," are OK, the files have the same names\n")
            return 0
        else:
            print("Files from ", dir_name," are no OK, the files have differents names \n")
            return 1
    else:
        print("Different number of directories in ", dir_name,"\n")
        return 1

#If the previous result was 0, comprobe if the dir is FORMAT_IMG_L1C or FORMAT_IMG_PVI_TCI. Copy every .jp2 file in the directory specified.
def compare_results(result, site, i):
    array_files = []
    counter = 1
    if result_comparison == 0:
        for b in range(len(dir)):
            if i == 0:
                array_files.append(os.path.join(site, dir[b], "IMG_DATA/"))
                sys.stdout.write('\r' + str((float(counter)/float(len(dir)))*100.0) + "%")
                sys.stdout.flush()   
            else:
                array_files.append(os.path.join(site, dir[b], "QI_DATA/"))
                sys.stdout.write('\r' + str((float(counter)/float(len(dir)))*100.0) + "%")
                sys.stdout.flush()                
            a = listdir(array_files[b])
            row = []
            for files in a:
                row = []
                if (".jp2" in files):
                    row.append(dir[b])
                    row.append(array_files[b] + files)
                    matrix_num.append(row)                  #Generates an array with the name of the folder where it is located and the file path
                    if copy_opt == True:
                        if i == 0:
                            if (os.path.exists(os.path.join(site2, dir[b], "IMG_DATA", files))):
                                os.unlink(os.path.join(site2, dir[b], "IMG_DATA", files))
                            shutil.copy(row[1], os.path.join(site2, dir[b], "IMG_DATA", files))
                        else:
                            if (os.path.exists(os.path.join(site2, dir[b], "QI_DATA", files))):	
                                os.unlink(os.path.join(site2, dir[b], "QI_DATA", files))
                            shutil.copy(row[1], os.path.join(site2, dir[b], "QI_DATA", files))
                    #If -t argument is specified True, the script will create a .tar file for every directory in TILE
                    if tar_opt == True and i ==1:
                        sys.stdout.write('\r' + ".tar conversion: " + str(counter) + "/" + str(len(dir)))
                        sys.stdout.flush()
                        tar = tarfile.open(site2 + "/" + dir[b] + ".tar", "w")
                        tar.add(site2 + "/" + dir[b],arcname=os.path.basename(site2 + "/" + dir[b]))
                        tar.close()
            counter = counter + 1
        if args.copy_file == True:
            print("---All files copied---\n")
        if args.tar_file == True:
            print("---All files converted in .tar---\n")

if tar_opt == True or copy_opt == True:
    for root, dir, files_site_1 in os.walk(site1):
        dir_FORMAT_IMG_L1C = dir
        break
    result_comparison = compare_directories(dir_FORMAT_IMG_L1C, 0)
    if result_comparison == 0:
        print("---Copying IMG_DATA---")
        compare_results(result_comparison, site1, 0)
        for root, dir, files_site_3 in os.walk(site3):
            dir_FORMAT_IMG_PVI_TCI = dir
            break
        result_comparison_2 = compare_directories(dir_FORMAT_IMG_PVI_TCI, 1)
        print("---Copying QI_DATA---")
        compare_results(result_comparison_2, site3, 1)
    else:
        print("Exiting")
else:
    print("At least one input argument has to be True")
