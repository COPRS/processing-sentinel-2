#!/bin/bash

# Set PATH
for x in $(find /dpc -type d -name scripts); do
  PATH=${x}:${PATH}
done
PROC_EXE=$1
JOB_ORDER=$2

# Run processor
exec $PROC_EXE $JOB_ORDER
