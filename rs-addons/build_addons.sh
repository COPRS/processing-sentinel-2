#!/usr/bin/env bash
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


######################################################################
# Build rs-addons for each folder found in the current directory
# Uses folder name as processor id for the addon
#
# Optionally uploads archives to registry, if "push" argument is passed
# Push requires env variables :
#   REGISTRY_USER
#   REGISTRY_PWD
#   REGISTRY_BASE
#   REGISTRY_PROJECT
#   REGISTRY_FOLDER
######################################################################

#set -x

echo "Building rs-addons"

VERSION=$(grep "version =" ../apps/build.gradle | xargs | cut -d' ' -f3)
DATE=$(date '+%Y-%m-%d')

REGISTRY_URL="https://${REGISTRY_BASE}/artifactory/${REGISTRY_PROJECT}/${REGISTRY_FOLDER}"

function build() {

  APP=$1

  echo "Building $APP"

  ROOT_NAME=RS_ADDON_${APP}_${VERSION}
  ZIP_NAME=${ROOT_NAME}.zip

  cd "$APP" || exit

  sed -i "s/<VERSION>/${VERSION}/g" Executables/stream-application-list.properties
  sed -i "s/<VERSION>/${VERSION}/g" Executables/stream-parameters.properties
  sed -i "s/<VERSION>/${VERSION}/g" Manifest.json
  sed -i "s/<NAME>/${ROOT_NAME}/g" Manifest.json
  sed -i "s/<DATE>/${DATE}/g" Manifest.json

  mv Executables "${ROOT_NAME}"_Executables
  mv Manifest.json "${ROOT_NAME}"_Manifest.json
  mv Release_Note.pdf "${ROOT_NAME}"_Release_Note.pdf

  zip -qq -r "${ZIP_NAME}" ./* -x ./*.md

  mv "${ZIP_NAME}" ../

  cd - || exit

}

for FOLDER in $(ls -d S2_*/)
do
  build "${FOLDER::-1}"
done

if [ "$1" == "push" ]; then
  for ARCHIVE in $(ls *.zip)
  do
    echo "Uploading ${ARCHIVE} to repository"
    curl -u "${REGISTRY_USER}:${REGISTRY_PWD}" -T "${ARCHIVE}" -X PUT "${REGISTRY_URL}/${ARCHIVE}"
  done
fi
