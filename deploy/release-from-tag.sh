#!/bin/bash -e
if [ "${GITHUB_REF_TYPE}" = "tag" ]; then
  echo "Releasing ${GITHUB_REF_NAME}"

  echo "Executing deploy goal for ${GITHUB_REF_NAME}"
  if ! mvn -e -B -ntp -P ci -P ossrh clean deploy -Drevision="${GITHUB_REF_NAME}" -Dchangelist=""; then
    echo "Failure deploying release artifacts, aborting"
    exit 1
  fi

  echo "Configuring git to release"
  git config --local user.name "Automaton"
  git config --local user.email "bot@bestquality.engineering"

  echo "Creating release branch: release/${GITHUB_REF_NAME}"
  git checkout -b "release/${GITHUB_REF_NAME}"

  echo "Incrementing project revision"
  mvn -e -B -ntp -P ci ci:increment-revision

  echo "Updating version references in documentation"
  sed --in-place --regexp-extended --expression="s/(<version>).*(<\/version>)/\1${GITHUB_REF_NAME}\2/g" README.md
  sed --in-place --regexp-extended --expression="s/^(version:).*$/\1 ${GITHUB_REF_NAME}/g" docs/_config.yml

  echo "Pushing release/${GITHUB_REF_NAME}"
  git add README.md
  git add docs/
  find . -name pom.xml -exec git add {} \;
  git commit -m "Release ${GITHUB_REF_NAME} (build: ${GITHUB_RUN_ID})"
  git push -u origin "release/${GITHUB_REF_NAME}"

  echo "Uploading code coverage report for ${GITHUB_REF_NAME}"
  bash <(curl -s https://codecov.io/bash)
fi
