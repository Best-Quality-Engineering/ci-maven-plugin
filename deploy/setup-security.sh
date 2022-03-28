#!/bin/bash -e

# Setup maven settings-security.xml for nexus staging
echo "Installing ~/.m2/settings-security.xml"
printenv SETTINGS_SECURITY_XML > ~/.m2/settings-security.xml

# Setup the code signing key; it will be used to publish snapshots and releases
printenv CODE_SIGNING_KEY > /tmp/code-signing-key.asc
gpg --batch --no-default-keyring --allow-secret-key-import --import /tmp/code-signing-key.asc
rm /tmp/code-signing-key.asc
