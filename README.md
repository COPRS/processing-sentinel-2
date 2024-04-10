:arrow_heading_up:
Go back to the
[Reference System Software repository](https://github.com/COPRS/reference-system-software)
:arrow_heading_up:

---

# Processing Sentinel 2

<!-- TOC -->

* [Processing Sentinel 2](#processing-sentinel-2)
    * [Overview](#overview)
    * [Available RS Addons](#available-rs-addons)
    * [Installation](#installation)
        * [Prerequisites](#prerequisites)
            * [Infrastructure](#infrastructure)
            * [RS-Core](#rs-core)
            * [Static AUX files](#static-aux-files)
                * [Obtaining static AUX files](#obtaining-static-aux-files)
                * [DEM folder](#dem-folder)
                * [Grid folder](#grid-folder)
        * [Build](#build)
        * [Repository content](#repository-content)
        * [Using Ansible](#using-ansible)
        * [Manual Install](#manual-install)
        * [Uninstall](#uninstall)
* [Copyright and license](#copyright-and-license)

<!-- TOC -->

## Overview

This repository contains components for the Sentinel 2 processing chain.  
They are grouped by processing level, and deployed using a separate rs-addon for each level.

Each rs-addon includes : preparation worker, execution worker(s) and internal interface management
with rs-core components and Object Storage.

Basic workflow :  
![](inputs/media/s2_basic_workflow.png)

- The S2_L0U addon consumes Session data to produce basic datastrips/granules, SAD, and HKTM files.
- The S2_L0C addon uses the output of S2_L0U to produce L0 products.
- The S2_L1 addon uses L0C products (Datastrips and Granules) to produce:
    - L1A and/or L1B Datastrips and Granules
    - L1C Datastrips and Tiles
- The S2_L2 addon uses the output of the S2_L1 addon (L1C Datastrips and Tiles) to produce L2A Datastrips and Tiles

For more information: [Sentinel 2 website](https://sentinels.copernicus.eu/web/sentinel/missions/sentinel-2).

## Available RS Addons

- [S2_L0U](rs-addons/S2_L0U)
- [S2_L0C](rs-addons/S2_L0C)
- [S2_L1](rs-addons/S2_L1)
- [S2_L2](rs-addons/S2_L2)

## Installation

Each RS-Addon will provide its own specific installation instructions, which may be found in their respective directory (see [above](#available-rs-addons)).

### Prerequisites

#### Infrastructure

All the required tools (such as Kafka and MongoDB) are included in the RS infrastructure installation.  
See  [Reference System Software Infrastructure](https://github.com/COPRS/infrastructure) for details.

#### RS-Core

All the required components can be found in the following repositories :

- [Production-Common](https://github.com/COPRS/production-common) (Ingestion, Catalog)
- [Monitoring](https://github.com/COPRS/monitoring) (Processing monitoring & reporting)

#### Static AUX files

S2 processing requires static AUX files, DEM and GRID, each of them stored on a specific shared folder available to the processing containers that require them.  
Mount points for each of these shared folders are defined through rs-addons parameters.

##### Obtaining static AUX files

[//]: # (TODO)
_TBD_

##### DEM folder

The following subfolders should be found inside the DEM folder:

- S2IPF-DEMGEOID for DEM_GEOIDF
- S2IPF-DEMGLOBE for DEM_GLOBEF
- S2IPF-DEMSRTM for DEM_SRTMFO (dted)
- S2IPF-DEML2 for DEM_SRTMFO (l2a)
- S2IPF-ESACCI for ESACCI

Example:

```
[wrapper@s2-l2-develop-part2-ew-l2-tl-v1-5ff9795558-t6s2x dem]$ ll
total 2
lrwxrwxrwx 1 1001 1001  57 Oct 24 13:47 S2IPF-DEMGEOID -> S2__OPER_DEM_GEOIDF_MPC__20200112T130120_S20190507T000000
lrwxrwxrwx 1 1001 1001  70 Oct 24 08:14 S2IPF-DEMGLOBE -> S2__OPER_DEM_GLOBEF_PDMC_20091210T235100_S20091210T235134.DBL/average/
lrwxrwxrwx 1 1001 1001 123 Nov 30 15:58 S2IPF-DEML2 -> S2__OPER_DEM_SRTMFO_PDMC_20200113T130120_S20190507T000000/S2__OPER_DEM_SRTMFO_PDMC_20200113T130120_S20190507T000000.DBL/l2a
lrwxrwxrwx 1 1001 1001 125 Oct 27 14:39 S2IPF-DEMSRTM -> S2__OPER_DEM_SRTMFO_PDMC_20200113T130120_S20190507T000000/S2__OPER_DEM_SRTMFO_PDMC_20200113T130120_S20190507T000000.DBL/dted/
drwxrwxr-x 3 1001 1001   3 Nov 30 15:40 S2IPF-ESACCI
drwxrwxr-x 2 1001 1001   2 Oct 24 13:46 S2__OPER_DEM_GEOIDF_MPC__20200112T130120_S20190507T000000
drwxr-xr-x 3 1001 1001   2 Jul 28 14:25 S2__OPER_DEM_GLOBEF_PDMC_20091210T235100_S20091210T235134.DBL
drwxrwxr-x 3 1001 1001   2 Oct 21 13:37 S2__OPER_DEM_SRTMFO_PDMC_20200113T130120_S20190507T000000
```

```
[wrapper@s2-l2-develop-part2-ew-l2-tl-v1-5ff9795558-t6s2x dem]$ ll S2IPF-ESACCI
total 608916
-rw-r--r-- 1 1001 1001 312727481 Nov 30 15:33 ESACCI-LC-L4-LCCS-Map-300m-P1Y-2015-v2.0.7.tif
drwxr-xr-x 2 1001 1001        12 Nov 30 15:46 ESACCI-LC-L4-Snow-Cond-500m-MONTHLY-2000-2012-v2.4
-rw-r--r-- 1 1001 1001 310801834 Nov 30 15:39 ESACCI-LC-L4-WB-Map-150m-P13Y-2000-v4.0.tif
```

##### Grid folder

Grid subfolders are stored directly under the folder root.  
Example:

```
[wrapper@s2-l1-develop-part1-ew-l1sb-v2-fdd6f46d8-v7x5c grid]$ ll
total 25430564
drwxrwxr-x 7 1001 1001           8 Nov  2 13:54 S2A_OPER_GRI_MSIL1B_MPC__20160521T210047_R034_V20160210T062901_20160210T063303.SAFE
drwxrwxr-x 7 1001 1001           9 Oct 24 16:24 S2A_OPER_GRI_MSIL1B_MPC__20160524T183716_R034_V20160520T062640_20160520T063242.SAFE
drwxrwxr-x 7 1001 1001           8 Oct 24 14:19 S2A_OPER_GRI_MSIL1B_MPC__20160601T125502_R034_V20160430T064047_20160430T064058.SAFE
drwxrwxr-x 7 1001 1001           8 Oct 24 14:20 S2A_OPER_GRI_MSIL1B_MPC__20160904T101913_R034_V20160410T064041_20160410T064052.SAFE
drwxrwxr-x 7 1001 1001           9 Oct 24 16:24 S2A_OPER_GRI_MSIL1B_MPC__20160907T181940_R034_V20160808T061935_20160808T063058.SAFE
drwxrwxr-x 7 1001 1001           9 Oct 24 15:19 S2A_OPER_GRI_MSIL1B_MPC__20160915T012537_R034_V20160729T064046_20160729T064057.SAFE
drwxrwxr-x 7 1001 1001           8 Oct 24 15:36 S2A_OPER_GRI_MSIL1B_MPC__20170913T101004_R034_V20160917T062000_20160917T062348.SAFE
drwxrwxr-x 7 1001 1001           9 Oct 24 15:59 S2A_OPER_GRI_MSIL1B_MPC__20181006T092551_R034_V20180629T061632_20180629T062103.SAFE
drwxrwxr-x 7 1001 1001           9 Oct 24 16:12 S2A_OPER_GRI_MSIL1B_MPC__20181006T094051_R034_V20180828T061804_20180828T062057.SAFE
drwxrwxr-x 7 1001 1001           8 Oct 24 16:24 S2B_OPER_GRI_MSIL1B_MPC__20180424T141740_R034_V20170719T062255_20170719T062506.SAFE
(...)
```

### Build

In order to build the project from source, first clone the GitHub repository :

```shellsession
git clone https://github.com/COPRS/processing-sentinel-2.git
```

Then build the docker base images, from the dockerfiles in the execution [docker folder](apps/execution/docker).

Then build all docker images:

```shellsession
./gradlew clean build bootBuildImage docker
```

And finally build the zip files:

```shellsession
./rs-addons/build_addons.sh
```

The zip files will be found in the rs-addons folder.

### Repository content

For each RS-Addon, the artifactory repository should contain:

- Docker images for the custom components of the addon in:  
  https://artifactory.coprs.esa-copernicus.eu/ui/repos/tree/General/rs-docker-private
- A zip file (its name includes the version number) for the addon in:  
  https://artifactory.coprs.esa-copernicus.eu/ui/repos/tree/General/rs-zip-private

### Using Ansible

Run the `deploy-rs-addon.yaml` playbook with the following variables:

- **stream_name**: name given to the stream in *Spring Cloud Dataflow*
- **rs_addon_location**: direct download url of the zip file or zip location on the bastion

Example:

```shellsession
ansible-playbook deploy-rs-addon.yaml \
    -i inventory/mycluster/hosts.yaml \
    -e rs_addon_location=https://artifactory.coprs.esa-copernicus.eu/artifactory/demo-zip/demo-rs-addon.zip \
    -e stream_name=example-stream-name
```

### Manual Install

Download and extract the zip file for the RS-Addon to install.  
If necessary, edit the parameters as required (See the specific addon release note for parameters description).

- Create all objects defined by files in _Executables/additional_resources_
- Using the SCDF GUI:
    - Register the applications using the content of the _stream-application-list.properties_ file
    - Create the streams using the content fo the _stream-definition.properties_ file
    - Deploy the stream using the properties defined in the _stream-parameters.properties_ file (removing comments)

### Uninstall

Using the SCDF GUI, undeploy then destroy all the streams relative to the RS-Addon.

# Copyright and license

The Reference System Software as a whole is distributed under the Apache License, version 2.0. A copy of this license is available in the [LICENSE](LICENSE) file. Reference System
Software depends on third-party components and code snippets released under their own license (obviously, all compatible with the one of the Reference System Software). These
dependencies are listed in the [NOTICE](NOTICE.md) file.

<p align="center">
 <img src="/docs/media/banner.jpg" width="800" height="50" />
</p>
<p align="center">This project is funded by the EU and ESA.</p>
