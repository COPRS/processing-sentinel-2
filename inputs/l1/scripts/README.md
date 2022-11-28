# S2IPF Orchestrator launcher
Launcher for the S2IPF orchestrator

## Principle

The S2IPF Orchestrator is in charge of producing the JobOrders for the IDPSCs and execute them according to a tasktable.
It also uses configuration files for IDPSC parameters, parallelization, file paths etc.
This launcher is then a sublayer that allows to mask the complexity of the TaskTables/Configuration.
Instead it provides simple "Mode" in a defined pipeline to produce the product desired.

## Installation

These are python scripts that you can copy anywhere you want. 
However the host docker/machine needs to have installed:
- The S2IPF-Orchestrator in the last version available here ( https://github.com/CS-SI/S2IPF_Orchestrator , private repository for now, only for CS users)
- The IDPSCs in wanted versions ( see configuration files chapter)
- The inventory scripts in wanted versions ( see configuration files chapter)

## Commands

### Help 
```
usage: LaunchIPF.py [-h] -m MODE -a AUXS -s STATICAUXS -g GRI -i INPUT -w
                    WORKING -o OUTPUT -p PARALLEL
                    [-t TILE] [-l LOGLEVEL] [-e EXEVERSIONFILE]
                    [-k KILLTIMEOUT]

This script launches the IPF orchestrator

optional arguments:
  -h, --help            show this help message and exit
  -m MODE, --mode MODE  mode
  -a AUXS, --auxs AUXS  Auxs folder
  -s STATICAUXS, --staticauxs STATICAUXS
                        Statics Auxs folder: DEMs
  -g GRI, --gri GRI     Statics Auxs folder: GRI
  -i INPUT, --input INPUT
                        Input folder
  -w WORKING, --working WORKING
                        Working folder
  -o OUTPUT, --output OUTPUT
                        Output folder
  -p PARALLEL, --parallel PARALLEL
                        Number of parallel Tasks
  -t TILE, --tile TILE  Overload tile Ident: 011-002 for example
  -l LOGLEVEL, --loglevel LOGLEVEL
                        Log level
  -e EXEVERSIONFILE, --exeversionfile EXEVERSIONFILE
                        Set a different exe version file than the installed
  -k KILLTIMEOUT, --killtimeout KILLTIMEOUT
                        Set the kill process timeout on Orch step

launches the IPF orchestrator

```

### Details

- --mode: Sets the mode of the launcher. The list of modes is located in the data/pipeline.json file defining the workflow of modes such as "L1A" for example
- --auxs: Defines the directory of so-called “dynamic” auxiliary data dependent on the current datastrip.
- --static-auxs: Defines the directory of so-called "static" auxiliary data independent of the current datastrip.
- --gri: defines the GRI directory for the current orbit.
- --input: defines the directory of the product (L0u or L0c) of entry, is only used by the modes having no previous mode
- --working: Defines the temporary working directory that can be deleted at the end of processing. Attention for the ‘Inventory’ steps the output products are in this directory
- --output: output directory, contains the shared context to be able to chain Modes
- --parallel: defines the number of processes in parallel, so the number of cpu used
- --tile: In the case of a mode operating by tile, this defines the tile(s) to be processed 011-002
- --loglevel: uppercase log level (INFO/DEBUG)
- --exeversionfile: allows to redefine the version file of the IPF, in particular in IVV, if the parameter is absent then it is the GIPP PROBAS which prevails
- --killtimeout: Duration of the timeout from which processing is stopped (by default 36000 seconds)


## Input details

### AUXS ( --auxs )

the input auxs folder must contains : 
- S2IPF-CAMS : 
Directory containing the CAMS data corresponding to the dates of the product in the form of a directory containing the GRIB files
```
S2__OPER_AUX_CAMSFO_ADG__20220429T000000_V20220429T000000_20220501T010000/
  |-> z_cams_c_ecmf_*grib
  ```
  In order to generate it from the ESA TGZ
  
  ```bash
  #tar xvf S2__OPER_AUX_CAMSFO_ADG__20220429T000000_V20220429T000000_20220501T010000.TGZ
  S2__OPER_AUX_CAMSFO_ADG__20220429T000000_V20220429T000000_20220501T010000.DBL
  S2__OPER_AUX_CAMSFO_ADG__20220429T000000_V20220429T000000_20220501T010000.HDR
  # mkdir S2__OPER_AUX_CAMSFO_ADG__20220429T000000_V20220429T000000_20220501T010000
  # cd S2__OPER_AUX_CAMSFO_ADG__20220429T000000_V20220429T000000_20220501T010000
  # tar xvf ../S2__OPER_AUX_CAMSFO_ADG__20220429T000000_V20220429T000000_20220501T010000.DBL
  ```
                   
-	 S2IPF-ECMWF :  
Directory containing the ECMWF data corresponding to the dates of the product in the form of a single GRIB file resulting from the concatenation of the GRIB files of the original AUX files S2__OPER_AUX_ECMWFD_ADG__20220428T120000_V20220428T210000_20220430T150000
```bash
# tar xvzf S2__OPER_AUX_ECMWFD_ADG__20220428T120000_V20220428T210000_20220430T150000.TGZ
S2__OPER_AUX_ECMWFD_ADG__20220428T120000_V20220428T210000_20220430T150000.DBL
S2__OPER_AUX_ECMWFD_ADG__20220428T120000_V20220428T210000_20220430T150000.HDR
# mkdir temp
# cd temp
# tar xvf ../S2__OPER_AUX_ECMWFD_ADG__20220428T120000_V20220428T210000_20220430T150000.DBL
S2D.... files
# for f in S2D*;do cat $f >> S2__OPER_AUX_ECMWFD_ADG__20220428T120000_V20220428T210000_20220430T150000;done
```

-	 S2IPF-GIPP : 
Contains all the GIPP xml for the processing of the desired levels. Please note that the GIPP OLQCPA must have the extension .zip.
The list of required GIPP is given in the s2_aux_list.json file along this README file

```json
{
  "Name" : "AUX_UT1UTC",
  "ProductLevels" : [ "L012" , "EISP"],
  "Rule" : "LatestValCover",
  "BandNamed" : false,
  "BandDependent" : false,
  "UnitDependent" : false,
  "TargetFolder" : "S2IPF-IERS",
  "TargetExtension" : ".txt"
}
```
**ProductLevels** defines the level for which the aux is needed L012 means L0+L1+L2 etc.
**BandNamed** defines if the file has the band naming convention.
**BandDependent** if the file is band dependent (B01 B02 B03...). **UnitDependent** if is it S2A/S2B or S2_. The **TargetFolder** defines where to put the AUXS ans the **TargetExtension** defines the final extension needed by the AUX.


Here is an example:
```bash
# tar xvzf S2B_OPER_GIP_VIEDIR_MPC__20170512T114736_V20170322T000000_21000101T000000_B06.TGZ
S2B_OPER_GIP_VIEDIR_MPC__20170512T114736_V20170322T000000_21000101T000000_B06.DBL
S2B_OPER_GIP_VIEDIR_MPC__20170512T114736_V20170322T000000_21000101T000000_B06.HDR
# mv S2B_OPER_GIP_VIEDIR_MPC__20170512T114736_V20170322T000000_21000101T000000_B06.DBL S2B_OPER_GIP_VIEDIR_MPC__20170512T114736_V20170322T000000_21000101T000000_B06.xml
```

- S2IPF-IERS : 
Contains the IERS bulletin corresponding to the dates of the product
```
S2__OPER_AUX_UT1UTC_ADG__20220429T000000_V20220429T000000_20230428T000000.txt
```
To generate it from ESA auxs
```bash
# tar xvf S2__OPER_AUX_UT1UTC_ADG__20220429T000000_V20220429T000000_20230428T000000.TGZ
S2__OPER_AUX_UT1UTC_ADG__20220429T000000_V20220429T000000_20230428T000000.DBL
S2__OPER_AUX_UT1UTC_ADG__20220429T000000_V20220429T000000_20230428T000000.HDR
# mv S2__OPER_AUX_UT1UTC_ADG__20220429T000000_V20220429T000000_20230428T000000.DBL S2__OPER_AUX_UT1UTC_ADG__20220429T000000_V20220429T000000_20230428T000000.txt
```

- AUXS statiques (--static-auxs ) :
 Contains the static auxs for S2
 ```
../S2-STATIC-AUXS/
|-- GRI_LIST_FILE
  |-- tile_list_file.xml ( empty file)
|-- PDI_DS_L1B ( empty folder )
|-- S2IPF-DEMGEOID 
              |-- S2__OPER_DEM_GEOIDF_MPC__20200112T130120_S20190507T000000.DBL
             |-- S2__OPER_DEM_GEOIDF_MPC__20200112T130120_S20190507T000000.HDR
            |-- S2__OPER_DEM_GEOIDF_MPC__20200112T130120_S20190507T000000.gtx
    |-- S2__OPER_DEM_GEOIDF_MPC__20200112T130120_S20190507T000000.gtx.aux.xml
|-- S2IPF-DEMGLOBE
           |-- e000
           |-- e001
           |-- e002
           |-- e003
           |-- e004
           |-- e005
           |-- e006
           |-- e007
           |-- e008
           |-- e009
           |-- e010
           |-- e011
           |-- e012
           |-- e013
           |-- e014
           |-- e015
           …..

|-- S2IPF-DEMSRTM
|-- S2A_OPER_DEM_SRTMFO_PDMC_20091211T165851_S20091211T165851
|-- e000
|-- e001
|-- e002
|-- e003
|-- e004
|-- e005
….
```

###	Input product ( --input )
The input product is only used to initialize the shared context in the case of mode having no previous mode (L1A for example)
The input product is either in the form of a L0u:
```
|-- DS
|   |-- S2B_OPER_MSI_L0U_DS_2BPS_20220804T055142_S20220804T051655_N00.00
|   |   |-- ANC_DATA
|   |   |-- DIMAP_S2_Level-0_DataStrip_unconsolidated.xsd
|   |   |-- IMG_DATA
|   |   |-- QL_DATA
|   |   |-- S2B_OPER_MTD_L0U_DS_2BPS_20220804T055142_S20220804T051655.xml
|-- GR
|   |-- DB1
|       |-- S2B_OPER_MSI_L0U_GR_2BPS_20220804T054901_S20220804T051655_D01_N00.00
|       |-- S2B_OPER_MSI_L0U_GR_2BPS_20220804T054901_S20220804T051655_D02_N00.00
|       |-- S2B_OPER_MSI_L0U_GR_2BPS_20220804T054901_S20220804T051655_D03_N00.00
|       |-- S2B_OPER_MSI_L0U_GR_2BPS_20220804T054901_S20220804T051655_D04_N00.00
|       |-- S2B_OPER_MSI_L0U_GR_2BPS_20220804T054901_S20220804T051655_D05_N00.00
|       |-- S2B_OPER_MSI_L0U_GR_2BPS_20220804T054901_S20220804T051655_D06_N00.00
|       |-- S2B_OPER_MSI_L0U_GR_2BPS_20220804T054901_S20220804T051655_D07_N00.00
|       |-- S2B_OPER_MSI_L0U_GR_2BPS_20220804T054901_S20220804T051655_D08_N00.00
|       |-- S2B_OPER_MSI_L0U_GR_2BPS_20220804T054901_S20220804T051655_D09_N00.00
|       |-- S2B_OPER_MSI_L0U_GR_2BPS_20220804T054901_S20220804T051655_D10_N00.00
|       |-- S2B_OPER_MSI_L0U_GR_2BPS_20220804T054901_S20220804T051655_D11_N00.00
…
```

In L0c input case :
```
├── DS
│   └── S2B_OPER_MSI_L0__DS_2BPS_20220507T170216_S20220507T144809_N04.00
├── GR
│   ├── S2B_OPER_MSI_L0__GR_2BPS_20220507T170216_S20220507T144809_D01_N04.00
│   ├── S2B_OPER_MSI_L0__GR_2BPS_20220507T170216_S20220507T144809_D02_N04.00
│   ├── S2B_OPER_MSI_L0__GR_2BPS_20220507T170216_S20220507T144809_D03_N04.00
│   ├── S2B_OPER_MSI_L0__GR_2BPS_20220507T170216_S20220507T144809_D04_N04.00
│   ├── S2B_OPER_MSI_L0__GR_2BPS_20220507T170216_S20220507T144809_D05_N04.00
│   ├── S2B_OPER_MSI_L0__GR_2BPS_20220507T170216_S20220507T144809_D06_N04.00
│   ├── S2B_OPER_MSI_L0__GR_2BPS_20220507T170216_S20220507T144809_D07_N04.00
│   ├── S2B_OPER_MSI_L0__GR_2BPS_20220507T170216_S20220507T144809_D08_N04.00
│   ├── S2B_OPER_MSI_L0__GR_2BPS_20220507T170216_S20220507T144809_D09_N04.00
│   ├── S2B_OPER_MSI_L0__GR_2BPS_20220507T170216_S20220507T144809_D10_N04.00
│   ├── S2B_OPER_MSI_L0__GR_2BPS_20220507T170216_S20220507T144809_D11_N04.00
│   ├── S2B_OPER_MSI_L0__GR_2BPS_20220507T170216_S20220507T144809_D12_N04.00
│   ├── S2B_OPER_MSI_L0__GR_2BPS_20220507T170216_S20220507T144812_D01_N04.00
│   ├── S2B_OPER_MSI_L0__GR_2BPS_20220507T170216_S20220507T144812_D02_N04.00
│   ├── S2B_OPER_MSI_L0__GR_2BPS_20220507T170216_S20220507T144812_D03_N04.00
│   ├── S2B_OPER_MSI_L0__GR_2BPS_20220507T170216_S20220507T144812_D04_N04.00
│   ├── S2B_OPER_MSI_L0__GR_2BPS_20220507T170216_S20220507T144812_D05_N04.00
…
…
```

Please note that it is not possible to produce more than L0c on a product that does not have at least 48 granules (4 scenes). It is therefore impossible to launch an L1A/B/C treatment on an L0c of less than 48 granules.

### GRI input ( --gri )
The GRI input is the folder of the sensing orbit found in the datastrip metadata.

The whole GRI contains all the orbits :
```
/shared/S2-GRI/
|-- S2__OPER_AUX_GRI001_MPC__20181212T114817_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI002_MPC__20181212T114817_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI003_MPC__20181212T114817_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI004_MPC__20181212T114817_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI005_MPC__20181212T114817_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI006_MPC__20181212T114817_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI007_MPC__20181212T120213_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI008_MPC__20181212T122337_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI009_MPC__20181212T122837_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI010_MPC__20181212T122859_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI011_MPC__20181212T123418_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI012_MPC__20181212T123516_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI013_MPC__20181212T124644_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI014_MPC__20181212T125825_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI015_MPC__20181212T130443_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI016_MPC__20181212T131146_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI017_MPC__20181212T131326_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI018_MPC__20181212T131354_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI019_MPC__20181212T132547_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI020_MPC__20181212T132835_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI021_MPC__20181212T133315_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI022_MPC__20181212T134807_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI023_MPC__20181212T135713_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI024_MPC__20181212T140118_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI025_MPC__20181212T140120_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI026_MPC__20181212T142131_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI027_MPC__20181212T142210_V20150623T000001_21000101T000001
|-- S2__OPER_AUX_GRI028_MPC__20181212T142907_V20150623T000001_21000101T000001
...
```
Thus in case of sensing orbit 20 for example:
```
--gri /shared/S2-GRI/S2__OPER_AUX_GRI020_MPC__20181212T132835_V20150623T000001_21000101T000001
```


### Temporary folder ( --working)
The temporary folder is created by the launcher and have this structure:
```
|-- ipf_output_<Mode>_YYYYMMDDHHmmSS
|   |-- <Mode>
|   |   |-- HOMOLOG_POINTS_LIST
|   |   |-- OLQC_PERSISTENT
|   |   |-- TaskTable_ YYYYMMDDTHHmmSS
|   |   |   |-- CurrentState_<Number>.json
|   |   |   |-- IDP_INFOS
|   |   |   |-- <IDPSC>
|   |   |   |   |-- input
|   |   |   |   |-- output
|   |   |   |   |-- JobOrder_<IDPSC>_PID.xml (Job order of the instance of IDPSC )
|   |   |   |   |-- <IDPSC>_<PID>.log ( Log stdout of the instance of IDPSC )
|   |   |   |   |-- <IDPSC>_<PID>.err ( Log stderr of the instance of IDPSC )
|   |   |-- VIRTUAL_SENSOR
|   |   |-- cfg_YYYYMMDDHHmmSS
|   |   |-- logs_ YYYYMMDDHHmmSS
|   |-- PreviousContextFile_<PreviousMode>_D00_T000.json
|   |-- data_<Mode>_ YYYYMMDDHHmmSS
|   |-- orchestratorICD.json
|   |-- orchlog_<Mode>_T000_ YYYYMMDDHHmmSS.err
|   |-- orchlog_<Mode>_T000_ YYYYMMDDHHmmSS.log
|   |-- result_<Mode>_T000_ YYYYMMDDHHmmSS.txt
|   |-- INV_<MODE> ( dans le cas d’un mode d’inventory contiens les produits de sortie)
|   |-- KPI_<MODE> ( dans le cas d’un mode d’inventory contiens les kpis de sortie)
```

### Output folder ( --out)
This directory contains the shared context allowing to chain the Modes.
It is created at the end of the Mode from the temporary directory.
So you have to persist it between modes and make it accessible (on a POSIX compatible file share)
It has the following structure:
```
|-- <Mode> (pour chaque mode)
|   |-- ContextFile_T000.json
|   |-- <IDPSC> ( pour chaque IDPSC fournissant des données pour le suite)
|   |-- idp_infos.xml (Contains the list of IDPSCs applied to the product)
| |-- logs_<STATUS>_<Mode>_T000_YYYYMMDDHHmmSS.tar (contains an extract of all the logs/JobOrder/Reports files that were produced in the temporary folder, STATUS can be SUCCESS/FAILURE)
|   |-- orchestratorEnviron.json ( Environnement used by the Orchestrator)
|   |-- orchlog_<Mode>_T000_YYYYMMDDHHmmSS.err (Logs stdout of the internal IPF orchestrator)
|   |-- orchlog_<Mode>_T000_ YYYYMMDDHHmmSS.log (Logs stderr of the internal IPF orchestrator)
```

##	Sequence of Modes
The modes are defined in the data/pipeline.json file, for example the version at the time of writing this manual is as follows:
```
{
  "TASKTABLE_CONFIGURATION" :
  [
    {
      "Name" : "L0",
      "Chain" : "L0"
    },
    {
      "Name" : "L0C",
      "Chain" : "L0"
    },
    {
      "Name" : "OLQC_L0DS",
      "PreviousTask" : ["L0","L0C"],
      "Chain" : "L0"
    },
    {
      "Name" : "OLQC_L0GR",
      "PreviousTask" : ["L0","L0C"],
      "Chain" : "L0"
    },
    {
      "Name" : "L1A",
      "PreviousTask" : ["L0","L0C"],
      "Chain" : "L1"
    },    
    {
      "Name" : "L1B",
      "PreviousTask" : ["L1A"],
      "Chain" : "L1"
    },
    {
      "Name" : "L1AFormatGR",
      "PreviousTask" : ["L1ABFinalize"],
      "Chain" : "L1"
    },
    {
      "Name" : "L1AFormatDS",
      "PreviousTask" : ["L1ABFinalize"],
      "Chain" : "L1"
    },
    {
      "Name" : "L1BNoGRI",
      "PreviousTask" : ["L1A"],
      "Chain" : "L1"
    },
    {
      "Name" : "OLQC_L1BDS",
      "PreviousTask" : ["RefiningFinalize","L1BFinalize"],
      "Chain" : "L1"
    },
    {
      "Name" : "L1BFormatGR",
      "PreviousTask" : ["RefiningFinalize","L1BFinalize"],
      "Chain" : "L1"
    },
    {
      "Name" : "L1CTile",
      "PreviousTask" : ["RefiningFinalize","L1BFinalize"],
      "Chain" : "L1"
    },
    {
      "Name" : "OLQC_L1CTL",
      "PreviousTask" : ["L1CTile"],
      "Chain" : "L1"
    },
    {
      "Name" : "OLQC_L1CDS",
      "PreviousTask" : ["RefiningFinalize","L1BFinalize"],
      "Chain" : "L1"
    }
  ],
  "NO_TASKTABLE_STEPS" : ["OLQC_L1CTL"]
}


```


Before you can make L1B/L1C from a L0C you must first produce the L1A
The launch is only possible on a L0C product of at least 4 scenes (48 granules). For a L0U it will only be possible to make L0C
It produces an L1A context and depending on the type of DATATAKE it will be possible to produce different products:
 
 ```mermaid
 graph TD
    A[L0C] --> B(L1A)
    B --> C{HasGRI?}
    C -->|Yes| D(L1B)
    C -->|No| E(L1BNoGRI)
    D --> P[[L1BContext]]
    P --> O(OLQC_L1CDS)    
    P --> Q(OLQC_L1BDS)
    Q --> R[(L1B_DS)]
    E --> P
    P --> G{TileSplit}    
    G --> |ForEach|H(L1CTile)
    H --> I(OLQC_L1CTL)
    I --> K[(L1C_TL)]
    O --> L[(L1C_DS)]
    P --> M(L1BFormatGR)    
    M --> N[(L1B_GR)]
    B --> S(L1AFormatGR)    
    S --> T[(L1A_GR)]
    B --> U(L1AFormatDS)    
    U --> V[(L1A_DS)]
 ```
 
**To produce an L1A from a LOu/L0C you have to chain the modes:**
(L0 || L0C) -> L1A -> (L1AFormatGR + L1AFormatDS)




**To produce an L1B it is necessary to chain the modes:**
(L0 || L0C) -> L1A -> L1B || L1BNoGRI) -> (OLQC_L1BDS + L1BFormatGR)
The Refining/L1BFinalize choice depends on the content of the gri_list_file.xml file generated when running L1A mode in the <OUT>/GET_GRI/output/GRI_LIST_FILE/gri_list_file.xml directory. If it contains at least one GRI then it is possible to use the <L1B> mode otherwise it is necessary to use the <L1BFinalize> mode

**To produce L1C :**  
(L0 || L0C) -> L1A -> L1B || L1BNoGRI -> ( OLQC_L1CDS + // for each Tile in tile_list_file.xml : L1CTile -> OLQC_L1CTL )
The tile_list_file.xml file contains all the tiles to produce for the current datastrip, it is available in <SHARED>/<L1B/L1BNoGRI>/GET_TILE_LIST/tile_list_file.xml



## Configuration files

###	IPF Versions configuration file

The “config/IDPSC_EXE_export.json” configuration file contains the following information:
- The versions of the default IPF components although the versions are extracted from the GIPP PROBAS (cf …)
- The main master plan relating to the version of the IPF interfaces, note that this will be deducted from PROBAS versions
- Paths to inventory tools

This file can be overridden by a configMap and be forced through the “--exeversionfile” option

Example configuration file:
```
{
  "PROCESSING_BASELINE" : "04.00",
  "MAIN_SCHEME" : "5.0",
  "INVENTORY_L0_SOFT_FOLDER" : "/usr/local/components/facilities/DPC-CORE-l0pack-inv-mtd/DPC-CORE-l0pack-inv-mtd-3.0.3/inventory_metadata_l0/scripts/",
  "INVENTORY_L0_DS_SCRIPT_NAME" : "inventory_metadata_l0_ds.sh",
  "INVENTORY_L0_GR_SCRIPT_NAME" : "inventory_metadata_l0_gr.sh",
  "INVENTORY_L0_SOFT_VERSION" : "3.0.3",
  "INVENTORY_L1_SOFT_FOLDER" : "/usr/local/components/facilities/DPC-CORE-l1l2pack-dpc-software/DPC-CORE-l1l2pack-dpc-software-3.0.4/l1l2_dpc_software/scripts/",
  "INVENTORY_L1_DS_SCRIPT_NAME" : "inventory_metadata_l1_l2_ds.sh",
  "INVENTORY_L1_GR_SCRIPT_NAME" : "inventory_metadata_l1_l2_gr_tl.sh",
  "INVENTORY_L1_SOFT_VERSION" : "3.0.4",
  "OLQC" : "05.01.00",
  "GSE": "05.01.00",
  "CHAINS" : {
    "L0": {
      "FORMAT_IMG_QL_L0": "05.01.00",
      "FORMAT_ISP": "05.01.00",
      "FORMAT_METADATA_DS_L0C": "05.01.00",
      "FORMAT_METADATA_GR_L0C": "05.01.00",
      "INIT_LOC_L0": "05.01.00",
      "QL_CLOUD_MASK": "05.01.00",
      "QL_DECOMP": "05.01.00",
      "QL_GEO": "05.01.00",
      "UNFORMAT_SAFE_DS": "05.01.00",
      "UNFORMAT_SAFE_GR": "05.01.00",
      "UPDATE_LOC": "05.01.00",
      "OLQC": "05.01.00"
    },
    "L1": {
      "UNFORMAT_SAFE_DS": "05.01.00",
      "UNFORMAT_SAFE_GR": "05.01.00",
      "QL_CLOUD_MASK": "05.01.00",
      "QL_DECOMP": "05.01.00",
      "QL_GEO": "05.01.00",
      "UPDATE_LOC": "05.01.00",
      "DECOMP": "05.01.00",
      "FORMAT_IMG_L1A": "05.01.00",
      "FORMAT_IMG_L1B": "05.01.00",
      "FORMAT_IMG_L1C": "05.01.00",
      "INIT_LOC_L1": "05.01.00",
      "INIT_VS_GEO": "05.01.02",
      "MASK_S2": "05.01.00",
      "FORMAT_IMG_PVI_TCI": "05.01.01",
      "FORMAT_METADATA_DS_L1A": "05.01.00",
      "FORMAT_METADATA_DS_L1B": "05.01.00",
      "FORMAT_METADATA_DS_L1C": "05.01.00",
      "FORMAT_METADATA_GR_L1A": "05.01.01",
      "FORMAT_METADATA_GR_L1B": "05.01.01",
      "FORMAT_METADATA_TILE_L1C": "05.01.01",
      "GEN_ORTHO_TOA": "05.01.00",
      "GEO1B_FINALIZE": "05.01.00",
      "GET_GRI": "05.01.00",
      "GET_TILE_LIST": "05.01.00",
      "RADIO_AB": "05.01.00",
      "RADIO_FINALIZE": "05.01.00",
      "RESAMPLE_TO_VS": "05.01.02",
      "SPATIO": "05.01.02",
      "TILE_FINALIZE": "05.01.00",
      "TILE_INIT": "05.01.00",
      "TP_COLLECT": "05.01.02",
      "TP_FILTER": "05.01.02",
      "UNFORMAT_GRI": "05.01.00",
      "OLQC": "05.01.00"
    }
  }
}
```
### Pipeline

The list of available Mode with their previous requested mode and conditions is available in ./config/pipeline.json.
This defines the pipeline to produce the wanted data. Inventory modes are producing the final products in the temprary folder.


###	MainScheme 

The MainScheme defines the version of the IPF interfaces.
For example the IPF 4.4 version does not have the same interfaces as the 5.1 and therefore it is necessary to define interface versions.

Each MainScheme has a set of environnment configuration files that defines the global S2IPF Orchestrator configuration in ./config/<scheme>
and a set of TaskTables/ConfigurationFiles for each Mode available in ./data/<scheme>

#### Main scheme trigger file

In order to determine the master scheme when using the PROBAS GIPP to automaticcaly detect the IPF version, a configuration file containing "Triggers" is installed in "config/MainSchemeTriggers.json"
Example of the trigger file:

  ```
{
  "ISP_FORMAT":
  {
     "05.01.00": "5.0"
  },
  "RESAMPLE_TO_VS": {
    "05.01.02": "5.0"
  }
}
```

In this example if FORMAT_ISP is in version "05.01.00" then MainScheme "5.0" is used.
The system will use the highest version of MainScheme activating a trigger.
  
#### Creating a new scheme

You can create a new scheme by copying the previous one and making the necessary interfaces changes. Once done you have to add a trigger in the MainSchemeTriggers.json file


