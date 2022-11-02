#!/bin/python
# -*- coding: utf-8 -*-
"""
 Copyright (C) 2021 CSGROUP

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 Author : Esauis Benjamin
"""

import argparse
import datetime
import glob
import json
import logging
import os
import subprocess
import tarfile
import traceback
import random

from log_colorizer import make_colored_stream_handler
import shutil
import Constants
import FileUtils
from DatastripReader import DatastripReader
from GSE import GSE
from IdpInfos import IdpInfos
from InventoryL0 import InventoryL0
from InventoryL1A import InventoryL1A
from InventoryL1B import InventoryL1B
from InventoryL1C import InventoryL1C
from OrchestratorLauncher import OrchestratorLauncher
from TaskTableReader import TaskTableReader
from FakeModeHandler import FakeModeHandler
from OrchestratorConfig import OrchestratorConfig
from OrchestratorICD import OrchestratorICD
from ContextManager import ContextManager
from FileUtils import create_directory
from OrchestratorPipeline import OrchestratorPipeline


class InstallIPF(object):
    """
        Main processing class
    """

    def __init__(self):
        # init logger
        self._logger = logging.getLogger("IPFinstaller")
        self._handler = make_colored_stream_handler()
        self._logger.addHandler(self._handler)
        self._logger.setLevel(logging.DEBUG)
        self._loglevel = logging.DEBUG
        self._libs_installed = []
        self._deps_installed = []
        self._rpm_list = None
        # Parameters
        self._chain = None
        self._rpm_dir = None
        self._working_dir = None
        self._loglevel_str = None
        self._versionfile = None


    def main(self):
        """
            Main function
        """
        parser = argparse.ArgumentParser(description="This script install the IPF version of the IDPS exe file",
                                         # main description for help
                                         epilog='launches the IPF Install\n\n',
                                         formatter_class=argparse.RawTextHelpFormatter)  # displayed after help
        parser.add_argument("-c", "--chain",
                            help="mode",
                            required=True)
        parser.add_argument("-f", "--versionfile",
                            help="version file json",
                            required=False)
        parser.add_argument("-r", "--rpmdir",
                            help="RPM directory",
                            required=True)
        parser.add_argument("-w", "--working",
                            help="Working folder",
                            required=True)
        parser.add_argument("-l", "--loglevel",
                            help="Log level",
                            default="INFO",
                            required=False)
        args = parser.parse_args()

        # Get the arguments
        self._chain = args.chain
        self._working_dir = args.working
        self._rpm_dir = os.path.join(args.rpmdir,self._chain)
        if args.versionfile is None:
            self._versionfile = os.path.join(os.path.dirname(os.path.realpath(__file__)), "config",
                                          "IDPSC_EXE_export.json")
        else:
            self._versionfile = args.versionfile

        if not os.path.isdir(self._rpm_dir):
            raise Exception("No folder found in "+self._rpm_dir)

        # Set the logger level
        self._loglevel_str = args.loglevel
        if self._loglevel_str == "INFO":
            self._logger.setLevel(logging.INFO)
            self._loglevel = logging.INFO
        elif self._loglevel_str == "DEBUG":
            self._logger.setLevel(logging.DEBUG)
            self._loglevel = logging.DEBUG
        else:
            self._logger.setLevel(logging.INFO)
            self._loglevel = logging.INFO

        start_time = datetime.datetime.now()
        # Source the IDPSC_EXE_export filename $idpsc_exe_filename."
        if not os.path.exists(self._versionfile):
            raise Exception("No EXE export file found under : " + self._versionfile)
        with open(self._versionfile) as f:
            exe_json = json.load(f)
        chain_json = exe_json[self._chain]
        # Idpsc execution folder
        idpsc_exe_dir = Constants.IDPSC_EXE_DIR
        for f in chain_json.items():
            bash_script = os.path.join(idpsc_exe_dir, f[0], f[1], "scripts", f[0] + ".bash")
            self._logger.info("Treating : "+f[0] + " in version : "+f[1])
            if not os.path.exists(bash_script):
                self._logger.info("No script found under : " + bash_script +" try install")
                self._install_idpsc(f[0], f[1], idpsc_exe_dir)
            else:
                self._logger.info("Already installed")

        self._logger.info("Finished installing "+self._chain +" in : "+str(
                (datetime.datetime.now() - start_time).seconds) + " seconds")

    def _install_idpsc(self, idpsc, version, idpsc_exe_dir):
        self._install_prerequired(version)
        self._install_librairies_and_cots(version)
        soft_folder = os.path.join(self._rpm_dir, version, "softs")
        if not os.path.isdir(soft_folder):
            raise Exception("No soft folder found under "+ soft_folder +" !!!")
        else:
            idpscs_rpms = glob.glob(os.path.join(soft_folder, "*"+idpsc+"*"+version+"*.rpm"))
            if len(idpscs_rpms) == 0:
                raise Exception("No RPM found for idspc "+idpsc+" in version "+version+ " in " + soft_folder)
            self._install_rpms(idpscs_rpms)
            bash_script = os.path.join(idpsc_exe_dir,idpsc, version, "scripts", idpsc + ".bash")
            if not os.path.exists(bash_script):
                self._logger.error("No script found under : " + bash_script + " installation failed")
                raise Exception("No script found under : " + bash_script + " installation failed")
            else:
                self._logger.info("Installed correctly : "+idpsc+" in version "+version)

    def _install_librairies_and_cots(self, version):
        if version not in self._libs_installed:
            lib_folder = os.path.join(self._rpm_dir, version, "libs")
            if not os.path.isdir(lib_folder):
                self._logger.warn("No lib folder found, assuming not needed for install")
            else:
                libs_cots_rpms = glob.glob(os.path.join(lib_folder, "*.rpm"))
                self._install_rpms(libs_cots_rpms)
            self._libs_installed.append(version)
        else:
            self._logger.info("Libs already installed")

    def _install_prerequired(self, version):
        if version not in self._deps_installed:
            prerequired_folder = os.path.join(self._rpm_dir, version, "prerequired_dependencies")
            if not os.path.isdir(prerequired_folder):
                self._logger.warn("No prerequired folder found, assuming not needed for install")
            else:
                rpms = glob.glob(os.path.join(prerequired_folder, "*.rpm"))
                self._install_rpms(rpms)
            self._deps_installed.append(version)
        else:
            self._logger.info("Prerequired already installed")

    def _install_rpms(self, rpms, force=False):
        if force:
            args = ["rpm", "-ivh", "--force"]
            has_one = False
            for f in rpms:
                if not self._check_if_installed(f):
                    args.append(f)
                    has_one = True
            if has_one:
                install_process = subprocess.Popen(args)
                status = install_process.wait()
                if status != 0:
                    raise Exception("Error while launching installation of  " + rpms)
        else:
            args = ["yum", "-y", "install"]
            has_one = False
            for f in rpms:
                 if not self._check_if_installed(f):
                    args.append(f)
                    has_one= True
            if has_one:
                install_process = subprocess.Popen(args)
                status = install_process.wait()
                if status != 0:
                    raise Exception("Error while launching installation of  " + rpms)

    def _check_if_installed(self, rpm):
        if self._rpm_list is None:
            self._rpm_list = os.path.join(self._working_dir, "rpm_list.txt")
            args = ["rpm", "-qa"]
            with open(self._rpm_list,"w") as list:
                list_process = subprocess.Popen(args, stdout=list)
                status = list_process.wait()
                if status != 0:
                    raise Exception("Error while getting rpm list")
        with open(self._rpm_list, "r") as list:
            lines = list.readlines()
            for line in lines:
                if line.strip() == os.path.splitext(os.path.basename(rpm))[0]:
                    self._logger.info(rpm + " already installed")
                    return True
        return False


if __name__ == "__main__":
    launch_ipf = InstallIPF()
    launch_ipf.main()
