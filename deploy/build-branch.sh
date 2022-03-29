#!/bin/bash -e
# Only install branch builds
if [ "${GITHUB_REF_TYPE}" != "tag" ]; then
  echo "Performing a install on ${GITHUB_REF_NAME}"
  mvn -e -B -ntp -P ci clean install
  echo "Uploading code coverage report for ${GITHUB_REF_NAME}"
  bash <(curl -s https://codecov.io/bash)
fi
