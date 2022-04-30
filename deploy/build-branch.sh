#!/bin/bash -e
# Only install branch builds
if [ "${GITHUB_REF_TYPE}" != "tag" ]; then
  echo "Performing a install on ${GITHUB_REF_NAME}"
  mvn -e -B -ntp -P ci clean install -Dsha1="-${GITHUB_RUN_ID}"
  echo "Uploading code coverage report for ${GITHUB_REF_NAME}"
  bash <(curl -s https://codecov.io/bash)
  echo "Publishing site to GitHub Pages"
  mvn -e -B -ntp -P ci site -Dsha1="-${GITHUB_RUN_ID}"
fi
