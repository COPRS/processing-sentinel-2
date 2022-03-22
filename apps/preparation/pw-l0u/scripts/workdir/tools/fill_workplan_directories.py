#!/usr/bin/env python2.6
# coding: utf8
#
# $Id$
# =============================================================
#
# PROJET     : PDGS S2
#
# SOCIETE    : THALES
#
# =============================================================
# HISTORIQUE :
#
# version 27/04/2020
# FIN-HISTORIQUE
# =============================================================
#________________ IMPORT _________________________
from __future__ import absolute_import
import os,sys
import stat
from os.path import dirname

from shutil import copyfile

import xml.etree.ElementTree as ET 
sys.path.append(dirname(dirname(sys.argv[0])))

import argparse

#_________________ Fonctions globales ______________
def checkOptions(theArgs):
    """
    Checks chain processor options
    @param theArgs arguments
    @return parsed arguments
    """
    parser = argparse.ArgumentParser()
    parser.add_argument("-p","--path", type=str, help="Path to the L0 workplan root directory", required=True)
    parser.add_argument("-w","--wp_name", help="Name of the workplan", required=True)
    parser.add_argument("-j","--job_infos", type=str, help="Path to the job info dir", required=True)
    parser.add_argument("-d","--data_repo", help="Path to data repository root directory", required=True)
    args = parser.parse_args(theArgs[1:])
    if len(vars(args)) < 4:
        parser.print_help() 
    return vars(args)

class WorkplanHelper(object):
    def __init__(self):
        """ Constructor """
        # set default values
        self.auxDataInfos = dict()

    def searchSimilarity(self,searchedFile, availableFile):
        """ Search similarities beetwen a file name and an available file name
        @param searchedFile file name to search
        @param availableFile File name to be compared
        @return founded Files have similarities
        """
        similarity = True
        searchedFileNoExtension = os.path.splitext(searchedFile)[0]
        nameParts = searchedFileNoExtension.split("_")
        
        if availableFile.endswith('.HDR'):
            # ignore files with HDR extensions
            return False

        availableNameNoExtension = os.path.splitext(availableFile)[0] 
        # approching name =  name.(without extension) or 
        # name.(without extension) has same Syy and Byy see bellow
        # nameParts = ['S2A', 'OPER', 'GIP', 'BLINDP', 'MPC', '', '20150605T094736', 'V20150622T000000', '21000101T000000', 'B00']
        partsToCheck = (0,-1)

        if availableNameNoExtension == searchedFileNoExtension:
            return True 
        for part in partsToCheck:
            if nameParts[part] in availableNameNoExtension: 
                similarity =  similarity and True
            else:
                similarity = False
        return similarity

    def findDataPathInDataRepository(self,data,fileType,searchedFile):
        """ Find a data referenced on a job 
            using reference data of a data repository
        @param data Path to data repository root directory
        @param fileType File type to search
        @param searchedFile File to search
        @return founded File Path or None
        """

        foundedFilePath = None
        availableNames = []
        for r, d, f in os.walk(data):
            for file in f:
                # filtering fileType in the name
                if fileType in file:
                    if file == searchedFile:
                        print("      !! found {0}".format(file))
                        foundedFilePath = os.path.join(r,file)
                    else:
                        availableNames.append(file)
        if foundedFilePath is None: 
            print("!! name NOT found  {0}".format(searchedFile))  
            for availableName in  availableNames: 
                similarity = self.searchSimilarity(searchedFile,availableName)
                if similarity:            
                    print("    -> approching name {0}".format(availableName))  
                #else:       
                #    print("    !! available name  {0}".format(availableName))   
        return foundedFilePath     

    def loadJobInfos(self, jobInfosRootDir):
        """ Load job information
        @param jobInfosRootDir: path to the job info dir
        """

        for r, d, f in os.walk(jobInfosRootDir):
            for file in f:
                if not file.endswith(".yml"):
                    continue
                jobInfosPath = os.path.join(jobInfosRootDir,file)
                #print(jobInfoPath)

                with open(jobInfosPath, 'r') as ymlfile:
                    lines = ymlfile.readlines()
                    for line in lines:
                        if (len(line) and (line[0] == '#')):
                            continue
                        if ' - ' in line:
                            parts = line.split(':')
                            key = parts[0].replace('-','').strip()
                            value = parts[1].strip()
                            if 'gipp_' in key:
                                gippName = value
                                self.auxDataInfos['{0}@{1}'.format(file,key)] = value
 
    def fillWorkPlan(self,wpPath,wpName,jobInfoDir,data):
        """ Fill step-data aux data directories of a workplan according to data referenced on job infos
            and by using reference data of a data repository
        @param wpPath root path of workplans
        @param wpName name of the workplan
        @param jobInfoDir Path to the job info dira
        @param data Path to data repository root directory
        """
        print("fillWorkPlan jobInfoDir={0}".format(jobInfoDir)) 
        self.loadJobInfos(jobInfoDir)   
        for k,gippName in self.auxDataInfos.items():
            #print("!!!!!!gippName={0}".format(gippName))
            gippNameParts = gippName.split('_')
            gippType = gippNameParts[3]
            targetPath = os.path.join(wpPath,wpName,'steps_data','GIPP','GIP_{0}'.format(gippType),gippName)
            if not os.path.exists(targetPath):
                foundedFilePath = self.findDataPathInDataRepository(data,gippType,gippName)
                if foundedFilePath is not None:
                    print("##### copy {0} to {1}".format(foundedFilePath,targetPath))
                    copyfile(foundedFilePath, targetPath)

        print("fillWorkPlan done.")


def mainTest(args):
    debug = True
    if not debug:
        args = checkOptions(sys.argv)

    jobInfosDir = 'F:\Job\S2\Dev\S2-L0-PACKAGING-PC\inputs\jobOrdersExamples\scenario2'
    dataDir = 'F:\Job\S2\data'
    app = WorkplanHelper()
    app.fillWorkPlan('F:\Job\S2\workplans','scenario2',jobInfosDir,dataDir)

def main(args):
    args = checkOptions(sys.argv)
    app = WorkplanHelper()
    app.fillWorkPlan(args['path'],args['wp_name'],args['job_infos'],args['data_repo'])

#_________________ Main ____________________________
if __name__ == "__main__":
    # execute only if run as a script
    #mainTest(sys.argv)
     main(sys.argv)
