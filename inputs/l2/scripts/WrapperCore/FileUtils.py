# Copyright 2023 CS Group
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import errno
import os,glob,shutil


# Resolve a path with env var
def fully_resolve(a_path, check_existence=False):
    resolved = os.path.expanduser(os.path.expandvars(a_path))
    if "$" in resolved:
        raise Exception("Environment variable not resolved in %s" % resolved)
    if check_existence:
        if not os.path.exists(resolved):
            raise Exception("File not found %s" % resolved)
    return resolved


def glob_one(search_directory, search_pattern):
    """
    Search in search directory the file with search_pattern and return it.
    If several files are found, a warning is printed and the first element is returned

    TODO: use logging

    :param search_directory:
    :type search_directory:
    :param search_pattern:
    :type search_pattern:
    """
    list_glob_result = glob.glob(os.path.join(search_directory, search_pattern))
    if len(list_glob_result) == 1:
        return list_glob_result[0]
    elif len(list_glob_result) == 0:
        return None
    else:
        print("More than one result found. Taking the first")
        return list_glob_result[0]


def glob_all(search_directory, search_pattern):
    """
    Search in search directory the file with search_pattern and return it.

    :param search_directory:
    :type search_directory:
    :param search_pattern:
    :type search_pattern:
    """
    list_glob_result = glob.glob(os.path.join(search_directory, search_pattern))
    return list_glob_result


def copy_tree(src, dst):
    files = os.listdir(src)
    if not os.path.isdir(dst):
        create_directory(dst)
    for f in files:
        src_filename = os.path.join(src, f)
        dst_filename = os.path.join(dst, f)
        if os.path.isdir(src_filename):
            copy_tree(src_filename, dst_filename)
        else:
            shutil.copyfile(src_filename, dst_filename)


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
