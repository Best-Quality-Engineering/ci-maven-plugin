#!/bin/bash -e

write_settings_security() {
  cat <<EOF >~/.m2/settings-security.xml
<?xml version="1.0" encoding="UTF-8"?>
<settingsSecurity>
    <master>${MAVEN_SECURITY_MASTER}</master>
</settingsSecurity>
EOF
}

# Setup maven settings-security.xml for nexus staging
write_settings_security

# Setup the code signing key; it will be used to publish snapshots and releases
printenv CODE_SIGNING_KEY > /tmp/code-signing-key.asc
gpg --batch --no-default-keyring --allow-secret-key-import --import /tmp/code-signing-key.asc
rm /tmp/code-signing-key.asc
