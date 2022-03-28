#!/bin/bash -e

# Setup maven settings-security.xml for nexus staging
echo "Installing ${HOME}/.m2/settings-security.xml"
printenv SETTINGS_SECURITY_XML > "${HOME}/.m2/settings-security.xml"
