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

# coding=utf-8
PROCESSING_STATION = "REFS"
VERSION = "1.0.0"
PROCESSOR_LOG_LEVEL = "DEBUG"
INVENTORY_L2_SOFT_FOLDER = "/usr/local/components/facilities/DPC-CORE-l1l2pack-dpc-software/" \
                           "DPC-CORE-l1l2pack-dpc-software-6.1.0/l1l2_dpc_software/scripts/"
INVENTORY_L2_DS_SCRIPT_NAME = "inventory_metadata_l1_l2_ds.sh"
INVENTORY_L2_GR_SCRIPT_NAME = "inventory_metadata_l1_l2_gr_tl.sh"

IDPSC_EXE_DIR = "/dpc/app/s2ipf"
DEFAULT_SENSING_START = "1983-01-01T00:00:00Z"
DEFAULT_SENSING_STOP = "2020-01-01T00:00:00Z"
DEFAULT_SENSING_START_MS = "1983-01-01T00:00:00.000Z"
DEFAULT_SENSING_STOP_MS = "2020-01-01T00:00:00.000Z"
DEFAULT_NB_THREADS = 1
DEFAULT_AQUISITION_STATION = ""
DEFAULT_PROCESSOR_NAME = "Chain"
