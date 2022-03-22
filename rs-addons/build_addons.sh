#!/usr/bin/env bash

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
######################################################################

#set -x

echo "Building rs-addons"

VERSION=$(grep "version =" ../apps/build.gradle | xargs | cut -d' ' -f3)
DATE=$(date '+%Y-%m-%d')

REGISTRY_URL="https://${REGISTRY_BASE}/artifactory/${REGISTRY_PROJECT}"

function build() {

  APP=$1

  echo "Building $APP"

  ROOT_NAME=RS_ADDON_${APP}_${VERSION}_${DATE}
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

  zip -qq -r "${ZIP_NAME}" ./*

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


