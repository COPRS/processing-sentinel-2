# S2IPF L2 launcher
Scripts to launch the L2 processing chain

## Principle

This script encapsulate the L2 chain that execute Sen2Cor+FormatMetadata+CompressTileImages+Inventory .

## Installation

These are python scripts that you can copy anywhere you want. 
However the host docker/machine needs to have installed:
- Sen2Cor
- The IDPSCs for L2 in wanted versions ( see configuration files chapter)
- The inventory scripts in wanted versions ( see configuration files chapter)

## Commands

### Help 
```
Launch_L2_TL.sh PRODUCT_L1C_TL PRODUCT_L1C_DS PRODUCT_L2A_DS AUX_DIR STATIC_AUXS WORK_DIR OUT_DIR ACQUISITION_STATION PROCESSING_CENTER
Launch_L2_DS.sh PRODUCT_L1C_DS GIPP_DIR STATIC_AUXS WORK_DIR OUT_DIR ACQUISITION_STATION PROCESSING_CENTER
```

### Details
Launch_L2_TL.sh is used to produce L2A Tile from a L1C tile and a L2A Datastrip.
Thus Launch_L2_DS.sh must be launched first to have the L2A datastrip.

The same L2A Datastrip has to be used for all tiles of the datastrip or you will have L2A tiles with bad namings.


## Input details

### DS Mode


***PRODUCT_L1C_DS***

L1C Datastrip folder

### TL Mode

***PRODUCT_L1C_TL***

L1C Tile folder

***PRODUCT_L1C_DS***

L1C Datastrip folder

***PRODUCT_L2A_DS***

L2A Datastrip folder

### General

***GIPP_DIR***

GIPP folder containing :
- GIP_L2ACSC : Optional
- GIP_L2ACAC : Optional
- GIP_L2ACFG : Optional
- GIP_PROBA2 : Mandatory
- GIP_JP2KPA : Mandatory
- GIP_OLQCPA : Mandatory

***AUX_DIR***

folder containing a subfoldr S2IPF-GIPP containing the GIPP listed in GIPP_DIR

***STATIC_AUXS*** 

Static auxs folder containing :

```
|-- S2IPF-DEML2
   |-- w009_s73.dt1
   |-- w009_s74.dt1
   |-- w009_s75.dt1
   |-- w009_s76.dt1
   |-- w009_s77.dt1
   |-- w009_s78.dt1
   |-- w009_s79.dt1
  ….
|-- S2IPF-ESACCI
   |-- ESACCI-LC-L4-LCCS-Map-300m-P1Y-2015-v2.0.7.tif
   |-- ESACCI-LC-L4-Snow-Cond-500m-MONTHLY-2000-2012-v2.4
   |-- ESACCI-LC-L4-WB-Map-150m-P13Y-2000-v4.0.tif
```


***WORK_DIR***

Temporary working dir

***OUT_DIR*** 

Output directory for products


***ACQUISITION_STATION***

Acquisition station

***PROCESSING_CENTER***

Processing center to put in output products

## Configuration files

###	IPF Versions configuration file

At this current time versions are directly hardcoded in the launching script Launch_L2_TL.sh and Launch_L2_DS.sh

