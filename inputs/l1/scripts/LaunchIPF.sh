#!/bin/bash

export IDPORCH_DEBUG=1
#
# Script surrounding the orchestrator launch to prepare a context based in folders: 
CUR_DIR="$( cd "$(dirname "$0")" ; pwd -P )"

# RUN
python ${CUR_DIR}/LaunchIPF.py "$@"



