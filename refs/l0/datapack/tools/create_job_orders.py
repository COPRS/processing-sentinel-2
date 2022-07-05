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
# FIN-HISTORIQUE
# =============================================================
#________________ IMPORT _________________________
from __future__ import absolute_import
import os,sys
import stat
from os.path import dirname
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
    parser.add_argument("-t","--template", type=str, help="Path to the jobOrderTemplate", required=True)
    parser.add_argument("-j","--job_infos", type=str, help="Job infos to be used to create job orders", required=True)
    args = parser.parse_args(theArgs[1:])
    if len(vars(args)) < 3:
        parser.print_help() 
    return vars(args)

class JobOrderGenerator(object):
    def __init__(self):
        """ Constructor """
        # set default values
        self.patterns = dict()
        self.jobInfos = dict()
        self.globalInfos = dict()
        self.jobTemplate =  None

    def findL0uDSName(self,wpDirPath,dtNumber):
        """ Find L0u data strip name of provided DT number under L0u processing outputs
        @param wpDataDirPath root path of workplan
        @param dtNumber name of the DT
        @return L0u datastrip name        
        """
        l0uDSPath = os.path.join(wpDirPath,'steps_data','L0U_DUMP',dtNumber,'DS')
        l0uDSName = "'???no name found under <{0}>???'".format(l0uDSPath)
        #print("  l0uDSPath = {0}".format(l0uDSPath))
        for r, d, f in os.walk(l0uDSPath):
            for directoryName in d:
                if '_OPER_MSI_L0U_DS' in directoryName:
                    l0uDSName = directoryName
        #print("    L0u name={0}".format(l0uDSName))
        return l0uDSName

    def findL0DSName(self,wpDirPath):
        """ Find L0 data strip name under L0 DS processing outputs
        @param wpDataDirPath root path of workplan
        @return L0 datastrip name        
        """
        l0DSPath = os.path.join(wpDirPath,'steps_data','DS')
        l0DSName = "'???no name found under <{0}>???'".format(l0DSPath)
        #print("  l0DSPath = {0}".format(l0DSPath))
        for r, d, f in os.walk(l0DSPath):
            for directoryName in d:
                if '_OPER_MSI_L0__DS' in directoryName:
                    l0DSName = directoryName
        #print("    L0 name={0}".format(l0DSName))
        return l0DSName

    def findL0uGRNames(self,wpDirPath,dtNumber):
        """ Find L0u Granules name under L0_DUMP/ processing outputs
        @param wpDataDirPath root path of workplan
        @param dtNumber DTxx directory tarfet of research
        @return L0u datastrip granules names list  (truncated before detector value)  
                S2x_OPER_MSI_L0U_GR_SGS__20200420T205828_S20160607T003621  _D01_N00.00    
        """
        l0uGRPath = os.path.join(wpDirPath,'steps_data','L0U_DUMP',dtNumber,'GR','DB1')
        l0uGRNames = list()
        #print("  L0uGRpath = {0}".format(l0uGRPath))
        for r, d, f in os.walk(l0uGRPath):
            for directoryName in d:
                # Correct granule dir are wihich that match ls -d DB1/*D01*
                if ('_OPER_MSI_L0U_GR' in directoryName) and ('_D01' in directoryName):
                    # split GR name full name and keep only first part
                    # S2x_OPER_MSI_L0U_GR_SGS__20200420T205828_S20160607T003621  _D01_N00.00
                    grSplitName = directoryName.split('_D01')[0] 
                    l0uGRNames.append(grSplitName) 
                    print("    L0uGRname = {0}".format(grSplitName))
        if len(l0uGRNames) == 0:
            l0uGRNames.append("'???no name found under <{0}>???'".format(l0uGRPath))
        return l0uGRNames

    def findL0GRNames(self,wpDirPath):
        """ Find L0 Granules name under L0 DS processing outputs
        @param wpDataDirPath root path of workplan
        @return L0 datastrip granules names list  (truncated before detector value)  
                S2x_OPER_MSI_L0__GR_SGS__20200420T205828_S20160607T003621  _D01_N02.08    
        """
        l0GRPath = os.path.join(wpDirPath,'steps_data','GR','DB1')
        l0GRNames = list()
        # print("  L0GRpath = {0}".format(l0GRPath))
        for r, d, f in os.walk(l0GRPath):
            for directoryName in d:
                # Correct granule dir are wihich that match ls -d DB1/*D01*/IMG_DATA
                if ('_OPER_MSI_L0__GR' in directoryName) and ('_D01' in directoryName):
                    for r2, d2, f2 in os.walk(os.path.join(r,directoryName)):
                        for directoryName2 in d2:
                            if directoryName2 == 'IMG_DATA':
                                # split GR name full name and keep only first part
                                # S2x_OPER_MSI_L0__GR_SGS__20200420T205828_S20160607T003621  _D01_N02.08
                                grSplitName = directoryName.split('_D01')[0] 
                                l0GRNames.append(grSplitName) 
                                print("    L0GRname = {0}".format(grSplitName))
        if len(l0GRNames) == 0:
            l0GRNames.append("'???no name found under <{0}>???'".format(l0GRPath))
        return l0GRNames

    def findRawNames(self,eispInputFolder,eispChannel):
        """ Find Cadu rax files name under EISP Input Folder
        @param eispInputFolder root path of Eisp cadu raw data
        @param eispChannel channbel of cadu raw data to process
        @return Cadu Raw names list  
                DCS_03_L20151105074120040101000_ch01_DSDB_00001.raw    
        """

        eispInputFolder = eispInputFolder.replace('@',':')
        rawPath = os.path.join(eispInputFolder,eispChannel)
        rawNames = list()
        #print("  rawPath = {0}".format(rawPath))
        for r, d, f in os.walk(rawPath):
            for fileName in f:
                if fileName.endswith('.raw'):
                    rawNames.append(fileName) 
                    print("    rawName = {0}".format(fileName))
        if len(rawNames) == 0:
            rawNames.append("'???no name found under <{0}>???'".format(rawPath))
        rawNames.sort()
        return rawNames

    def generateJobOrder(self,wpPath,jobNumber):
        """ Create a job order from template and deposit on a workplan instance     
        @param wpPath root path of workplans
        @param wpName name of the workplan
        @param template Path to the jobOrderTemplate
        """

        # jobOrder final name
        wpName = self.jobInfos['workplanName']
        jobOrderName = self.jobInfos['jobOrderName']
        print("generateJobOrder {0} jobNumber={1}".format(jobOrderName,jobNumber)) 
        fullJobOrderName = 'job_order_l0c_{0}_{1}_{2}.xml'.format(wpName,jobNumber,jobOrderName)
        # print('fullJobOrderName = {0}'.format(fullJobOrderName))

        # launch dir creation
        launchDirPath = os.path.join(wpPath,wpName,'app_data','step_{0}'.format(jobOrderName),'launch_{0}'.format(jobNumber))
        if not os.path.exists(launchDirPath):
            os.makedirs(launchDirPath)
        jobOrderPath = os.path.join(launchDirPath,fullJobOrderName)
        # print('jobOrderPath = {0}'.format(jobOrderPath))

        # select patterns for current job using Job infos properties
        instancePatterns = dict()
        for k, v in self.patterns.items():
            if (k == 'jobnumber') and (v == '<automatic>'):
                v = str(jobNumber)
            if type(v).__name__ == 'list':
                # to avoid overflow if list elements are less than job count 
                # (except for list_xxxx where all items are process on a single job)
                if 'list_' not in k:
                    v = v[min(jobNumber-1,len(v)-1)]
            templateKey = "@{0}@".format(k) 
            #print("templateKey={0} value={1}".format(templateKey,v))
            instancePatterns[templateKey] = v

        # replace patterns in the template with jobinfos values
        # ignore in a first pass the @list_xxxx@ pattern
        jobData1 = list()
        for line in self.jobTemplate:
            for k, v in instancePatterns.items():
                if 'list_' not in k:
                    line = line.replace(k,v)
            jobData1.append(line)

        # process in a second pass the list_xxxx_items pattern if present
        jobData2 = list()
        for line in jobData1:
            if '@list_' in line:
                # isolate pattern list name
                parts1 = line.split('@list_')
                parts2 = parts1[1].split('@')
                listPatternName = "@list_{0}@".format(parts2[0])
                if '_by_channel' in listPatternName:
                    items_by_channel = instancePatterns[listPatternName][jobNumber-1]
                    for patternValue in items_by_channel:
                        newLine = line.replace(listPatternName,patternValue)
                        jobData2.append(newLine)
                else:
                    for patternValue in instancePatterns[listPatternName]:
                        newLine = line.replace(listPatternName,patternValue)
                        jobData2.append(newLine)                   
            else:
                jobData2.append(line)

        with open(jobOrderPath, 'w') as jobFile:
            for line in jobData2:
                jobFile.write("{0}".format(line))

    def calculateAutomaticJobInfos(self,wpPath,wpName,patternDic):
        """ Resolve automatic job information  
        @param wpPath root path of workplans  
        @param wpName workplan name 
        @param patternDic dictionary with values to resolve
        """
        
        wpDirPath = os.path.join(wpPath,wpName)
        # process dtNumber, eisp_input_folder, eisp_channel first because it is used by other automatic process
        dependingParamList = ('dt_number','eisp_input_folder','eisp_channel')
        for dependingParam in dependingParamList:
            if dependingParam in patternDic.keys():
                dependingValue = patternDic[dependingParam]
                if ('<global.' in dependingValue) and (dependingParam in self.globalInfos.keys()):
                    dependingValue = self.globalInfos[dependingParam]
                    patternDic[dependingParam] = dependingValue

        for k, v in patternDic.items():
            if '<global.' in v:
                globalKeyParts = v.split('.')
                globalKey = globalKeyParts[1].replace('>','').strip()
                if globalKey in self.globalInfos.keys():
                    v = self.globalInfos[globalKey]
                    patternDic[k] = v
            if (k == 'wpdir') and (v == '<automatic>'):
                patternDic[k] = os.path.join(wpPath,wpName)
            if (k == 'l0u_dsname') and (v == '<automatic>'):
                patternDic[k]  = self.findL0uDSName(wpDirPath, patternDic['dt_number'])
            if (k == 'l0_dsname') and (v == '<automatic>'):
                patternDic[k]  = self.findL0DSName(wpDirPath)
            if (k == 'count_l0gr') and (v == '<automatic>'):
                patternDic[k]  = str(len(self.findL0GRNames(wpDirPath)))
            if (k == 'list_l0gr_items') and (v == '<automatic>'):
                patternDic[k]  = self.findL0GRNames(wpDirPath)
            if (k == 'count_l0ugr') and (v == '<automatic>'):
                # "%03d" to have 001
                patternDic[k]  = "%03d" % (len(self.findL0uGRNames(wpDirPath,patternDic['dt_number'])))
            if (k == 'list_l0ugr_items') and (v == '<automatic>'):
                patternDic[k]  = self.findL0uGRNames(wpDirPath,patternDic['dt_number'])
            if (k == 'count_raw_by_channel') and (v == '<automatic>'):
                patternDic[k]= list()
                patternDic[k].append(str(len(self.findRawNames(patternDic['eisp_input_folder'],'ch_1'))))
                patternDic[k].append(str(len(self.findRawNames(patternDic['eisp_input_folder'],'ch_2'))))
            if (k == 'list_raw_items_by_channel') and (v == '<automatic>'):
                    patternDic[k]= list()
                    patternDic[k].append(self.findRawNames(patternDic['eisp_input_folder'],'ch_1'))
                    patternDic[k].append(self.findRawNames(patternDic['eisp_input_folder'],'ch_2'))


    def loadGlobalInfos(self, jobInfosPath,wpName):
        """ Load global information
        @param jobInfosPath: path to the job info file
        @param wpName workplan name
        """
 
        # yml special interpreter
        globalInfoPath = os.path.join(os.path.dirname(jobInfosPath),'GLOBAL_infos.yml')
        # print("globalInfoPath={0}".format(globalInfoPath))
        if globalInfoPath is not None:
            with open(globalInfoPath, 'r') as ymlfile:
                lines = ymlfile.readlines()

            # print("lines={0}".format(lines))
            for line in lines:
                if (len(line) and (line[0] == '#')):
                    continue
                if ' - ' in line:
                    parts = line.split(':')
                    key = parts[0].replace('-','').strip()
                    if len(parts) > 2:
                        value = ':'.join(parts[1:]).strip()
                    else:
                        value = parts[1].strip()
                    if '[' in value:
                        value = value.strip('][').split(',')
                        value = [v.strip() for v in value] # to remove spaces
                    if '(' in value:
                        value = value.strip(')(').split(',')
                        value = [v.strip() for v in value] # to remove spaces
                    self.globalInfos[key] = value

    def loadJobInfos(self, jobInfosPath,wpName):
        """ Load job information
        @param jobInfosPath: path to the job info file
        @param wpName workplan name
        """
 
        self.jobInfos['workplanName'] = wpName.strip()
        print("workplanName={0}".format(wpName))

        # yml special interpreter
        if jobInfosPath is not None:
            with open(jobInfosPath, 'r') as ymlfile:
                lines = ymlfile.readlines()

            # print("lines={0}".format(lines))
            for line in lines:
                if (len(line) and (line[0] == '#')):
                    continue
                if 'jobOrderName' in line:
                    jobOrderName = ':'.join(line.split(':')[1:])
                    self.jobInfos['jobOrderName'] = jobOrderName.strip()
                    # set jobOrderName in pattern list because some jobOrders input (e.g REPORT) are related to this parameter
                    self.patterns['jobOrderName'] = jobOrderName.strip()
                if 'nbjobs' in line:
                    nbjobs = ':'.join(line.split(':')[1:])
                    self.jobInfos['nbjobs'] = nbjobs.strip()
                if ' - ' in line:
                    parts = line.split(':')
                    key = parts[0].replace('-','').strip()
                    if len(parts) > 2:
                        value = ':'.join(parts[1:]).strip()
                    else:
                        value = parts[1].strip()
                    if '[' in value:
                        value = value.strip('][').split(',')
                        value = [v.strip() for v in value] # to remove spaces
                    if '(' in value:
                        value = value.strip(')(').split(',')
                        value = [v.strip() for v in value] # to remove spaces
                    self.patterns[key] = value
            # for k,v in self.patterns.items():
            #     print("patterns {0}={1}".format(k,v))

    def createJobOrders(self,wpPath,wpName, templatePath,jobInfosPath):
        """ Create job orders from template according to job infos and deposit them ont the workplan instance   
        @param wpPath root path of workplans  
        @param wpName workplan name
        @param templatePath Path to the jobOrderTemplate  
        @param jobInfos Infos to creates jobs
        """

        # read template infos
        with open(templatePath, 'r') as jobTemplateFile:
            self.jobTemplate = jobTemplateFile.readlines()

        # read job and global infos
        self.loadGlobalInfos(jobInfosPath,wpName)
        self.loadJobInfos(jobInfosPath,wpName)
        self.calculateAutomaticJobInfos(wpPath,wpName,self.globalInfos)
        self.calculateAutomaticJobInfos(wpPath,wpName,self.patterns)

        # print("globalInfos={0}".format(self.globalInfos))
        # print("jobInfos= {0}".format(self.jobInfos))
        # print("patterns= {0}".format(self.patterns))
        for it in range(0,int(self.jobInfos['nbjobs'])):  
            self.generateJobOrder(wpPath,it+1)
        print("createJobOrders {0} done !!!!!!!".format(os.path.basename(templatePath)))

def mainTest(args):
    debug = True
    if not debug:
        args = checkOptions(sys.argv)

    # dictionary with { templateName : infoName, ... }
    jobsToBuild = { 
                    "job_order_template_INIT_LOC_L0.xml" : "INIT_LOC_L0_infos.yml",
                    "job_order_template_FORMAT_ISP.xml" : "FORMAT_ISP_infos.yml",
                    "job_order_template_QL_GEO.xml" : "QL_GEO_infos.yml",
                    "job_order_template_QL_CLOUD_MASK.xml" : "QL_CLOUD_MASK_infos.yml",
                    "job_order_template_FORMAT_METADATA_GR_L0C.xml" : "FORMAT_METADATA_GR_L0C_infos.yml",
                    "job_order_template_FORMAT_IMG_QL_L0.xml" : "FORMAT_IMG_QL_L0_infos.yml",
                    # "job_order_template_OLQC-L0cGr.xml" : "OLQC-L0cGr_infos.yml",
                    # # "job_order_template_FORMAT_METADATA_DS_L0C.xml" : "FORMAT_METADATA_DS_L0C_infos.yml",
                    # # "job_order_template_OLQC-L0cDs.xml" : "OLQC-L0cDs_infos.yml",
                    "job_order_template_EISP.xml" : "EISP_infos.yml"
                    }
    templateDir = 'F:\Job\S2\Dev\S2-L0-PACKAGING-PC\inputs\jobOrdersTemplates'
    jobInfosDir = 'F:\Job\S2\Dev\S2-L0-PACKAGING-PC\inputs\jobOrdersExamples\scenario3'

    app = JobOrderGenerator()
    for tpl, info in jobsToBuild.items():
        app.createJobOrders('F:\Job\S2\workplans','scenario3',os.path.join(templateDir,tpl),os.path.join(jobInfosDir,info))


def main(args):
    args = checkOptions(sys.argv)
    app = JobOrderGenerator()
    app.createJobOrders(args['path'],args['wp_name'],args['template'],args['job_infos'])

#_________________ Main ____________________________
if __name__ == "__main__":
    # execute only if run as a script
    #mainTest(sys.argv)
    main(sys.argv)
