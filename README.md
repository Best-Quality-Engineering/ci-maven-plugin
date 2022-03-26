# Java OSSRH Template
This is a repository template that contains the scaffolding necessary to build and continuously deploy an open source 
Java project to [Maven Central](https://central.sonatype.org), a [Travis CI](https://travis-ci.com) configuration for 
deploying snapshot builds with automatic releasing from a tag, and uploading JaCoCo code coverage reports to 
[Codecov](https://codecov.io)

## Open Sonatype OSSRH Account
If not already done, ensure an account has been created with [OSSRH](https://central.sonatype.org/pages/ossrh-guide.html).

* Create an issue: Community Support - Open Source Project Repository Hosting
* If your `group` references a domain, add a DNS TXT Record referencing the ticket id (i.e. `OSSRH-54345`) as soon as the 
  issue is created. This will decrease turn-around time.
* Once issue has been resolved
    * Deploy artifacts to staging repository (https://oss.sonatype.org/#stagingRepositories)
    * Comment on ticket when first release is promoted (https://central.sonatype.org/pages/releasing-the-deployment.html) 
      to activate syncing with Maven Central 
    * Grab the repository's profile identifier from the Nexus Repository Manager by selecting the staging profile and 
      examining the url: (https://oss.sonatype.org/#stagingProfiles;<PROFILE-ID>)

## Project Bootstrapping
In order to continuously deploy to OSSRH from Travis CI, there a some prerequisites:

### OSSRH Code Signing Key 

Export the private key required by OSSRH for signing code artifacts:

```sh
gpg -a --export-secret-key <KEYID> > deploy/code-signing-key.asc
```

Next, use `travis` CLI to encrypt the code signing private key. This command will output the `key` and `iv`
parameters needed to individually encrypt multiple files. They will be referenced in later steps, so save 
them in a safe location:

```sh
travis encrypt-file deploy/code-signing-key.asc deploy/code-signing-key.asc.enc --com --print-key
```

After this command completes it will report the variable names to use in `.travis.yml`, so update the 
encrypted key (`$encrypted_XXXXXXXXXX_key`) and iv (`$encrypted_XXXXXXXXXX_key`) variable names with 
the correct values. The names can also be retrieved from the repository settings screen.

:exclamation: **Ensure that `deploy/code-signing-key.asc` is moved out of the project directory.** Then add the
file:
```sh
git add deploy/code-signing-key.asc.enc
```

### GitHub Repository Deploy Key
Generate a repository-specific deploy key that can be used to push commits from Travis:

```ssh
ssh-keygen -t rsa -C your@email.com -f deploy/id_rsa
```

Then upload public key to deploy keys area of the GitHub repository. Next, encrypt the 
private key that will be used to connect to the repository using the `key` and `iv` 
parameters from the previous step:

```sh
travis encrypt-file deploy/id_rsa deploy/id_rsa.enc --com --key <key> --iv <iv>
```

:exclamation: **Ensure that `deploy/id_rsa` is moved out of the project directory and to a safe location.** Then add the file:
```sh
git add deploy/id_rsa.enc
```

### Maven Master Password
To generate a master password:
```sh
mvn --encrypt-master-password <password>
```

To encrypt a password using the master:
```sh
mvn --encrypt-password <password>
```

Create a `deploy/settings-security.xml` file:
```xml
<settingsSecurity>
  <master>{MASTER-PASSWORD}</master>
</settingsSecurity>
```

Use the `travis` utility to encrypt the file for use during the build:
```sh
travis encrypt-file deploy/settings-security.xml deploy/settings-security.xml.enc --com --key <key> --iv <iv>
```

:exclamation: **Ensure that `deploy/settings-security.xml` is moved out of the project directory.** Then add the file:
```sh
git add deploy/settings-security.xml.enc
```

### Required Environment Variables
The following environment variables need to be defined to deploy locally and will need to be exposed as 
secret environment variables in your TravisCI settings:

|Variable Name|Purpose|
|:------------|:------|
| `OSSRH_USERNAME`| Maven Central username |
| `OSSRH_PASSWORD`| Maven Central password (encoded with master password) |
| `CODE_SIGNING_KEY_FINGERPRINT`| Code signing key identifier |
| `CODE_SIGNING_KEY_PASSPHRASE`| Code signing key passphrase |
| `CODECOV_TOKEN`| Codecov.io token for code coverage reports |

## Checklist
Here is a handy checklist required to complete project scaffolding:

- [ ] Setup encoded OSSRH code signing key
- [ ] Setup encoded GitHub deploy key
- [ ] Setup encoded Maven `settings-security.xml`
- [ ] Update `.travis.yml` to use TravisCI repository encryption key and initialization vector variables
- [ ] Update `pom.xml` with project specific information:
  - [ ] Fill out the `GAV`, `name`, and `description` elements
  - [ ] Set the `project.staging.profile` property to the OSSRH staging profile identifier
  - [ ] Set the `repository.slug` to match GitHub in the form of `owner-name/repository-name`
  - [ ] Fill out the required `<developers>` element
- [ ] Define environment variables for local deployment
- [ ] Define TravisCI secret variables for continuous deployment

# `README.md` Template
[![Build Status](https://img.shields.io/travis/ruffkat/XXX/master?color=green)](https://travis-ci.com/ruffkat/XXX)
[![codecov](https://codecov.io/gh/ruffkat/XXX/branch/master/graph/badge.svg)](https://codecov.io/gh/ruffkat/XXX)
[![Maven Central](https://img.shields.io/maven-central/v/io.bestquality/XXX.svg?color=green&label=maven%20central)](https://search.maven.org/search?q=g:io.bestquality%20AND%20a:XXX)

# Project Name
Project description

## Installation
```xml
<dependency>
  <groupId>io.bestquality</groupId>
  <artifactId>XXX</artifactId>
  <version>0.0.0</version>
</dependency>
```
