# Changelog

All notable changes to this project will be documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
> Content of release :
> - **Added** for new features.
> - **Changed** for changes in existing functionality.
> - **Deprecated** for soon-to-be removed features.
> - **Removed** for now removed features.
> - **Fixed** for any bug fixes.
> - **Security** in case of vulnerabilities.

## [1.8.0-rc1] - 2023-07-17

### Added
- Added limits and request values for filters and routers

### Changed

- Updated memory and cpu values
- Homogenization of the titles of the documentation

### Fixed

- Corrected Processing Station default value for L2 orchestrator
- [#999 - Added custom field to PW Tracelogs](https://github.com/COPRS/rs-issues/issues/999)

## [1.7.0-rc1] - 2023-05-17

### Added

-Updated IPF version for L1 processing to 6.1.4

### Changed

- Updated Spring Boot version

### Fixed

- [#935 - Update default size for s2-l1-shared volume](https://github.com/COPRS/rs-issues/issues/935)
- [#924 - Update default memory limits](https://github.com/COPRS/rs-issues/issues/924)
- [#928 - Use proper CatalogEvent fields to link GR/TL to their Datastrip](https://github.com/COPRS/rs-issues/issues/928)
- [#923 - Add timeout for obs transfers](https://github.com/COPRS/rs-issues/issues/923)
- [#901 - Remove RESTART allowed Action for L2PW](https://github.com/COPRS/rs-issues/issues/901)
- [#945 - Fixed Upload trigger when no file is produced](https://github.com/COPRS/rs-issues/issues/945)
- [#917 - Increase kafka timeout from 1 hour to 2 hours for L0C](https://github.com/COPRS/rs-issues/issues/917)
- [#941 - Updated MissingOutput behaviour](https://github.com/COPRS/rs-issues/issues/941)
- [#970 - Added Aux folder cleanup before download](https://github.com/COPRS/rs-issues/issues/970)

## [1.6.0-rc1] - 2023-03-20

### Added

- Rework docker build and add Security scan with Trivy
- Added upload of True Color Image files for L1C and L2A

### Changed

- Added copyrights to Notice and harmonized Readme titles

### Fixed

- [#807 - Added logic for L0u Processing in case no Datastrip is produced](https://github.com/COPRS/rs-issues/issues/807)
- [#856 - Increase direct memory size](https://github.com/COPRS/rs-issues/issues/856)
- [#863 - Rework and set proper allowed actions for ProcessingMessages](https://github.com/COPRS/rs-issues/issues/863)
- [#876 - Overwrite existing files during EW download](https://github.com/COPRS/rs-issues/issues/876)
- Set proper input for some traces

## [1.5.0-rc1] - 2023-01-26

### Added

- [#594 - Sentinel-2 Level-0 RS add-on to produce all the metadata files](https://github.com/COPRS/rs-issues/issues/594)
- [#683 - Update l0u and l0c docker image](https://github.com/COPRS/rs-issues/issues/683)

### Changed

- Remove unnecessary properties and set proper default for SAD Data

## [1.4.0-rc1] - 2023-01-04

### Added

- [#492 - Sentinel-2 Level-2 RS add-on](https://github.com/COPRS/rs-issues/issues/492)
- [#514 - Expose S2 pending processing as gauge metric](https://github.com/COPRS/rs-issues/issues/514)
- [#537 - Add rsChainVersion field in Processing Message](https://github.com/COPRS/rs-issues/issues/537)
- [#736 - Add ObsRead and ObsWrite traces](https://github.com/COPRS/rs-issues/issues/736)
- Misc documentation update

### Changed

- Update jdk version used by wrappers
- Update libraries versions

### Fixed

- [#734 - Set proper product family for SADATA](https://github.com/COPRS/rs-issues/issues/734)

## [1.3.0-rc1] - 2022-11-23

### Added

- [#573 - Sentinel-2 Level 1 RS addon Execution Worker Shared Context](https://github.com/COPRS/rs-issues/issues/573)
- [#574 - Sentinel-2 Level 1 RS addon Execution Worker L1AB](https://github.com/COPRS/rs-issues/issues/574)
- [#576 - Sentinel-2 Level 1 RS addon Preparation Worker L1C](https://github.com/COPRS/rs-issues/issues/576)
- [#575 - Sentinel-2 Level 1 RS addon Execution Worker L1C](https://github.com/COPRS/rs-issues/issues/575)
- Documentation for Level 1 RS addon

### Changed

- Rework some rs-addon configuration parameters

### Fixed

- [#654 - S2a L0c Execution fails](https://github.com/COPRS/rs-issues/issues/654)
- [#664 - L0u DS are missing in the END processing trace](https://github.com/COPRS/rs-issues/issues/664)

## [1.2.0-rc1] - 2022-10-25

### Added

- [#596 - Add missing namespace in the KafkaTopic additional resource](https://github.com/COPRS/rs-issues/issues/596)
- [#490 - Sentinel-2 Level-1 RS add-on Skeleton](https://github.com/COPRS/rs-issues/issues/490)
- [#546 - Set timeliness field for L0 RS add-on](https://github.com/COPRS/rs-issues/issues/546)
- [#572 - Sentinel-2 Level-1 RS add-on Preparation Worker](https://github.com/COPRS/rs-issues/issues/572)

## [1.0.0] - 2022-09-29

## [1.1.0-rc1] - 2022-09-27

### Added

- [#413 - Provide missing output on RS add-on S2 traces](https://github.com/COPRS/rs-issues/issues/413)

## [1.0.0-rc3] - 2022-09-22

### Added

- [#562 - Add checksum file next to S2 product for S3 uploads](https://github.com/COPRS/rs-issues/issues/562)
- Documentation and Notice.md

## [1.0.0-rc2] - 2022-09-02

### Fixed

- [#541 - Secrets are not encoded](https://github.com/COPRS/rs-issues/issues/541)
- [#542 - Double quotes in stream parameters](https://github.com/COPRS/rs-issues/issues/542)
- [#544 - Secrets use incorrect inventory variables](https://github.com/COPRS/rs-issues/issues/544)
- [#545 - Stream definition using labels raise warning message](https://github.com/COPRS/rs-issues/issues/545)
- fix traces format

## [1.0.0-rc1] - 2022-08-31

### Added

- Code Quality improvements
- Docs skeleton

## [0.11.0-rc3] - 2022-08-19

### Added

- [#446 - End User documentation](https://github.com/COPRS/rs-issues/issues/446)

## [0.11.0-rc2] - 2022-08-16

### Added

- [#450 - Trace ICD Compliance](https://github.com/COPRS/rs-issues/issues/450)
- [#451 - Processing Message ICD Compliance](https://github.com/COPRS/rs-issues/issues/451)

## [0.9.0-rc1] - 2022-07-06

### Added

- [#233 - Develop RS add-on S2_L0 processing : L0u](https://github.com/COPRS/rs-issues/issues/233)
- [#300 - Develop RS add-on S2_L0 processing : L0c](https://github.com/COPRS/rs-issues/issues/300)
