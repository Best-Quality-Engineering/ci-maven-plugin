#!/bin/bash -e
# Only deploy branch builds
if [ "${GITHUB_REF_TYPE}" != "tag" ]; then
  echo "Performing a deploy on ${GITHUB_REF_NAME}"
  mvn -e -B -ntp -P ci -P ossrh deploy -Dsha1="-${GITHUB_RUN_ID}"
  echo "Uploading code coverage report for ${GITHUB_REF_NAME}"
  bash <(curl -s https://codecov.io/bash)
fi
