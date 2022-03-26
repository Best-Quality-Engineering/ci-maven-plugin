#!/bin/bash
# Only deploy branch builds
if [ "${TRAVIS_BRANCH}" != "${TRAVIS_TAG}" ]; then
  echo "Performing a deploy on ${TRAVIS_BRANCH}"
  mvn -e -B -ntp -s deploy/settings.xml -P ossrh deploy
  echo "Uploading code coverage report for ${TRAVIS_BRANCH}"
  bash <(curl -s https://codecov.io/bash)
fi
