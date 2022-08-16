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
    args = parser.parse_args(theArgs[1:])
    if len(vars(args)) < 2:
        parser.print_help() 
    return vars(args)

class WorkplanFactory(object):
    def __init__(self):
        """ Constructor """
        # set default values
        pass

    def instanciateWorkPlan(self,path,name):
        """ Work plan factory. Create a workplan uuid root path 
        @param path root path of workplans
        @param name name of the workplan
        """
        print("instanciateWorkPlan root={0}".format(path))    
        print("instanciateWorkPlan name={0}".format(name))

        # Create workplan Directory
        wpDirPath = os.path.join(path,name)
        if not os.path.exists(wpDirPath):
            os.makedirs(wpDirPath)
        # os.chmod(wpDirPath,stat.S_IRWXU | stat.S_IRWXG)

        # creates Common structure and empty step_data sub directories
        pathsToPrepare = (
            # steps and launchs directories
            os.path.join(wpDirPath,'app_data'),

            # workplan data directories
            os.path.join(wpDirPath,'app_data','wp_data'), 

            # steps data directories
            os.path.join(wpDirPath,'steps_data'),
            os.path.join(wpDirPath,'steps_data','GR'),
            os.path.join(wpDirPath,'steps_data','DS'),
            os.path.join(wpDirPath,'steps_data','GIPP'),
            os.path.join(wpDirPath,'steps_data','GIPP','GIP_VIEDIR'),
            os.path.join(wpDirPath,'steps_data','GIPP','GIP_SPAMOD'),
            os.path.join(wpDirPath,'steps_data','GIPP','GIP_INVLOC'),
            os.path.join(wpDirPath,'steps_data','GIPP','GIP_DATATI'),
            os.path.join(wpDirPath,'steps_data','GIPP','GIP_BLINDP'),
            os.path.join(wpDirPath,'steps_data','GIPP','GIP_LREXTR'),
            os.path.join(wpDirPath,'steps_data','GIPP','GIP_CLOINV'),
            os.path.join(wpDirPath,'steps_data','GIPP','GIP_R2ABCA'),
            os.path.join(wpDirPath,'steps_data','GIPP','GIP_JP2KPA'),
            os.path.join(wpDirPath,'steps_data','GIPP','GIP_OLQCPA'),
            os.path.join(wpDirPath,'steps_data','GIPP','GIP_PROBAS'),
            os.path.join(wpDirPath,'steps_data','GIPP','GIP_ATMIMA'),
            os.path.join(wpDirPath,'steps_data','GIPP','GIP_ATMSAD'),
            os.path.join(wpDirPath,'steps_data','IERS','AUX_UT1UTC'),
            os.path.join(wpDirPath,'steps_data','DEM','DEM_GLOBEF'),
            os.path.join(wpDirPath,'steps_data','WORKING'),
            os.path.join(wpDirPath,'steps_data','REP_ISP_INFOS'),
            os.path.join(wpDirPath,'steps_data','L0U_DUMP'),
            os.path.join(wpDirPath,'steps_data','L0U_DUMP','imgch1'),
            os.path.join(wpDirPath,'steps_data','L0U_DUMP','imgch2'),
            os.path.join(wpDirPath,'steps_data','L0U_DUMP','sadch1'),
            os.path.join(wpDirPath,'steps_data','L0U_DUMP','sadch2'),
            os.path.join(wpDirPath,'steps_data','L0U_DUMP','MSIMerge'),
            os.path.join(wpDirPath,'steps_data','L0U_DUMP','MSISADMerge'),
            os.path.join(wpDirPath,'steps_data','L0U_DUMP','SADPDI'),
            os.path.join(wpDirPath,'steps_data','L0U_DUMP','PR'),
            os.path.join(wpDirPath,'steps_data','RAW'),
            os.path.join(wpDirPath,'steps_data','CADU'),
            os.path.join(wpDirPath,'steps_data','GSE_SAD_BACKUP')
        )

        # creates Common structure and empty step_data sub directories
        for path in pathsToPrepare:
            if not os.path.exists(path):
                os.makedirs(path)

        print("instanciateWorkPlan done.")


def mainTest(args):
    debug = True
    if not debug:
        args = checkOptions(sys.argv)

    app = WorkplanFactory()
    app.instanciateWorkPlan('F:\Job\S2\workplans','scenario3',)

def main(args):
    args = checkOptions(sys.argv)
    app = WorkplanFactory()
    app.instanciateWorkPlan(args['path'],args['wp_name'])

#_________________ Main ____________________________
if __name__ == "__main__":
    # execute only if run as a script
    # mainTest(sys.argv)
    main(sys.argv)
