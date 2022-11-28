# coding=utf-8
import errno
import glob
import os
import re
import shutil
import subprocess
from Queue import Empty


def glob_one(search_directory, search_pattern, throw=False):
    """
    Search in search directory the file with search_pattern and return it.
    If several files are found, the first element is returned
    :param search_directory:
    :type search_directory:
    :param search_pattern:
    :type search_pattern:
    """
    list_glob_result = glob.glob(os.path.join(search_directory, search_pattern))
    if len(list_glob_result) == 1:
        return list_glob_result[0]
    else:
        if len(list_glob_result) == 0:
            if throw:
                raise Exception("0 files found with glob in "+search_directory+" with pattern "+search_pattern)
            else:
                return None
        else:
            if throw:
                raise Exception("Multiples files found with glob in "+search_directory+" with pattern "+search_pattern)
            else:
                return list_glob_result[0]


def split_all(path):
    allparts = []
    while 1:
        parts = os.path.split(path)
        if parts[0] == path:  # sentinel for absolute paths
            allparts.insert(0, parts[0])
            break
        elif parts[1] == path: # sentinel for relative paths
            allparts.insert(0, parts[1])
            break
        else:
            path = parts[0]
            allparts.insert(0, parts[1])
    return allparts


def create_directory(path):
    try:
        os.makedirs(path)
    except OSError as e:
        if e.errno != errno.EEXIST:
            raise


def copy_file(source, dest_file, replace = False):
    if not os.path.isdir(os.path.dirname(dest_file)):
        try:
            create_directory(os.path.dirname(dest_file))
        except OSError as e:
            if not os.path.isdir(os.path.dirname(dest_file)):
                raise e
    if not os.path.exists(dest_file) or replace:
        shutil.copyfile(source, dest_file)


def _copy_files(queue,src_dir,dst_dir,idx):
    while not queue.empty():
        try:
            file_to_copy = queue.get(timeout=1)
            source_file = os.path.join(src_dir,file_to_copy)
            dest_file = os.path.join(dst_dir,file_to_copy)
            if not os.path.exists(dest_file):
                copy_file(source_file,dest_file)
            queue.task_done()
        except Empty:
            return


def copy_directory_recursive(src, dst, pattern=None):
    __copy_recursive_nothreads(src, dst, pattern)


# merge the input folder in the given one by puting symbolic links to files
def folder_fusion(an_input_dir, a_dest_dir):
    try:
        os.mkdir(a_dest_dir)
    except OSError, e:
        if not os.path.exists(a_dest_dir):
            raise
    # list the alternative files
    for strRoot, listDirNames, listFileNames in os.walk(an_input_dir):
        # for all dirs underneath
        for strDirName in listDirNames:
            str_alternative_dir = os.path.join(strRoot, strDirName)
            str_link_dir = a_dest_dir + os.sep + str_alternative_dir.replace(an_input_dir, "", 1)
            if not os.path.exists(str_link_dir):
                try:
                    os.mkdir(str_link_dir)
                    print(str_link_dir)
                except OSError, e:
                    if not os.path.exists(str_link_dir):
                        raise
        # for all files underneath
        for strFileName in listFileNames:
            str_alternative_file = os.path.join(strRoot, strFileName)
            str_link_file = a_dest_dir + os.sep + str_alternative_file.replace(an_input_dir, "", 1)
            str_rel_alternative_file = os.path.relpath(str_alternative_file, os.path.dirname(str_link_file))
            if not os.path.exists(str_link_file):
                try:
                    os.symlink(str_rel_alternative_file, str_link_file)
                    print(str_link_file)
                except OSError, e:
                    if not os.path.exists(str_link_file):
                        raise Exception("Internal error with file: " + str_link_file)

def __copy_recursive_nothreads(src,dst, pattern=None):
    regex = None
    if pattern is not None:
        regex = re.compile(pattern)
    files = os.listdir(src)
    if not os.path.isdir(dst):
        create_directory(dst)
    for f in files:
        src_filename = os.path.join(src, f)
        dst_filename = os.path.join(dst, f)
        if os.path.isdir(src_filename):
            __copy_recursive_nothreads(src_filename,dst_filename, pattern)
        elif pattern is not None and regex.match(os.path.basename(src_filename)):
            copy_file(src_filename,dst_filename)
        else:
            copy_file(src_filename, dst_filename)


def copy_tree(inpath, destdir, max_depth, pattern):
    __copy_dir(inpath, destdir, max_depth, pattern, 0)


def __copy_dir(inpath, destdir, max_depth, pattern, depth):
    regex = re.compile(pattern)
    if depth > max_depth:
        return
    create_directory(destdir)
    for d in [ os.path.join(inpath, f) for f in os.listdir(inpath)]:
        if os.path.isfile(d) and regex.match(os.path.basename(d)):
            copy_file(d, os.path.join(destdir, os.path.basename(d)))
        elif os.path.isdir(d):
            __copy_dir(d, os.path.join(destdir, os.path.basename(d)), max_depth, pattern, depth+1)


def remove_files_recursively(inpath, pattern):
    __remove_file(inpath,pattern)


def __remove_file(inpath, pattern):
    regex = re.compile(pattern)
    for d in [os.path.join(inpath, f) for f in os.listdir(inpath)]:
        if os.path.isfile(d) and regex.match(os.path.basename(d)):
            os.remove(d)
        elif os.path.isdir(d):
            __remove_file(d, pattern)


def tar_directory(src_dir, target):
    if not os.path.isdir(os.path.dirname(target)):
        create_directory(os.path.dirname(target))
    tar_process_code = subprocess.call(["tar", "hcf", target, "-C", os.path.dirname(src_dir), os.path.basename(src_dir)],
                                       shell=False)
    if tar_process_code != 0:
        raise IOError("Can't tar "+src_dir+" to "+target)


def untar_directory(src_file, target_dir):
    if not os.path.isdir(target_dir):
        create_directory(target_dir)
    try:
        tar_process_code = subprocess.call(["tar","xf", src_file, "-C", target_dir],
                         shell=False)
    except Exception as err:
        print('err in tarr')
        print(err)
        raise err
    if tar_process_code != 0:
        raise IOError("Can't untar "+src_file+" to "+target_dir)
