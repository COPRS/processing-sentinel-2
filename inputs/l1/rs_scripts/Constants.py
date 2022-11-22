# coding=utf-8
import logging

PROCESSING_STATION = "REFS"
VERSION = "1.0.0"
PROCESSOR_LOG_LEVEL = "DEBUG"

IDPSC_EXE_DIR = "/dpc/app/s2ipf"
DEFAULT_SENSING_START = "1983-01-01T00:00:00Z"
DEFAULT_SENSING_STOP = "2020-01-01T00:00:00Z"
DEFAULT_SENSING_START_MS = "1983-01-01T00:00:00.000Z"
DEFAULT_SENSING_STOP_MS = "2020-01-01T00:00:00.000Z"
DEFAULT_NB_TASKS = 4
DEFAULT_AQUISITION_STATION = ""
DEFAULT_PROCESSOR_NAME = "Chain"
DEFAULT_DETECTOR_LIST = "01-02-03-04-05-06-07-08-09-10-11-12"
DEFAULT_BAND_LIST = "B00-B01-B02-B03-B04-B05-B06-B07-B08-B09-B10-B11-B12"
NO_B00_BAND_LIST = "B01-B02-B03-B04-B05-B06-B07-B08-B09-B10-B11-B12"
ONLY_B00_BAND_LIST = "B00"
ONLY_D01_DETECTOR_LIST = "01"


LOG_FORMATTER=logging.Formatter(
            '%(asctime)s [%(levelname)s] %(name)s : %(message)s', "%Y-%m-%d %H:%M:%S")