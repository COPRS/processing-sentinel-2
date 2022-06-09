#!/usr/bin/env bash

echo -n "Current directory : "
pwd
echo -n "Directory content : "
ls
echo "This should be in debug";
echo "This should be in info [W]";
echo "This should be in info [E]";
echo "This should be in info [WARNING]";
echo "This should be in info [ERROR]";

exit 0
