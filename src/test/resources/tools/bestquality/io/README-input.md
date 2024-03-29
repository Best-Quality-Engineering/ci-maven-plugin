# CI Maven Plugin
A Maven plugin for use in CI pipelines based on the patterns described in the
[Maven CI Friendly documentation](https://maven.apache.org/maven-ci-friendly.html).

[![Build Status](https://github.com/Best-Quality-Engineering/ci-maven-plugin/actions/workflows/ossrh-snapshot.yml/badge.svg)](https://github.com/Best-Quality-Engineering/ci-maven-plugin/actions/workflows/ossrh-snapshot.yml)
[![Release Status](https://github.com/Best-Quality-Engineering/ci-maven-plugin/actions/workflows/ossrh-release.yml/badge.svg)](https://github.com/Best-Quality-Engineering/ci-maven-plugin/actions/workflows/ossrh-release.yml)
[![Code Coverage](https://codecov.io/gh/Best-Quality-Engineering/ci-maven-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/Best-Quality-Engineering/ci-maven-plugin)
[![Maven Central](https://img.shields.io/maven-central/v/tools.bestquality/ci-maven-plugin?color=%234c1)](https://search.maven.org/search?q=g:tools.bestquality%20AND%20a:ci-maven-plugin)

## Usage
```xml
<build>
    <plugins>
        <plugin>
            <groupId>tools.bestquality</groupId>
            <artifactId>ci-maven-plugin</artifactId>
            <version>0.0.14</version>
            <executions>
                <execution>
                    <goals>
                        <goal>expand-pom</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

## Goals

### `ci:expand-pom` 
By default, this goal is bound to the `validate` phase and considers the project's `pom.xml` as a template
producing an expanded version performing the following actions on the template:
* replaces all `${revision}`, `${sha1}`, and `${changelist}` property references found in the project's `pom.xml` file
* updates the values of `revision`, `sha1`, and `changelist` defined in the `<properties>` element
* writes the expanded pom file to `target/generated-poms/pom-ci.xml` and sets it as the project's `pom.xml` file

_NOTE: Unlike the [Flatten Maven Plugin](https://www.mojohaus.org/flatten-maven-plugin/) this goal will **not reformat
and strip comments** from the published `pom.xml` files. This is critical if your POMs contain important
information in its comments._

### `ci:increment-pom`
By default, this aggregator goal is bound to the `validate` phase and will update the project's top-level
`pom.xml` ci `revision` property with the next selected component to increment. Use to prepare the `pom.xml`
file for the next development snapshot.

Without customization, the goal will attempt to resolve the version component to increment by starting with the `build` 
and working it's way up to the `major` component. The following standard incrementors are available:
* `auto` (default)
* `major`
* `minor`
* `patch`
* `build`

To use a specific incrementor:
```shell
mvn ci:increment-pom -Dincrementor=minor
```

This goal exports the incremented version and can be configured to export to standard out (default) or a file:

#### Writing to `stdout`

```shell
# assign next version to script variable
next_version=$(mvn -q ci:increment-pom)
```
#### Writing to a file

```shell
# writes to target/ci/next-version.txt
mvn ci:increment-pom -Dscriptable=false
```

```shell
# customize the file location (both file properties are optional)
mvn ci:increment-pom -Dscriptable=false -Doutput-directory="." -Dfilename="next.txt"
```

### `ci:release-version`
By default, this aggregator goal is bound to the `validate` phase and will read the top-level project's `revision`
property and output the value to a file or standard out while removing the `-SNAPSHOT` qualifier. It does not make 
any pom modifications and can be used if the release process is not event driven.

#### Writing to `stdout`
The goal is designed to be executed from the command line to capture and assign the output to a variable:

```shell
release_revision=$(mvn -q ci:release-version)
```
#### Writing to a file
The goal can be configured to write the output to a file:

```shell
# writes to target/ci/release-version.txt
mvn ci:release-version -Dscriptable=false
```

```shell
# customize the file location (both file properties are optional)
mvn ci:release-version -Dscriptable=false -Doutput-directory="." -Dfilename="release.txt"
```

### `ci:clean`
By default, this goal is bound to the `clean` phase and will remove the expanded `target/generated-poms/pom-ci.xml` file

_note: This goal is only needed if the default plugin `outputDirectory` configuration is changed to be outside
of `${project.build.directory}`_

## Example Project Configuration
### `pom.xml`
This configuration results in consistent developer and pipeline builds:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>...</groupId>
    <artifactId>...</artifactId>
    <version>${revision}${sha1}${changlist}</version>
    <packaging>...</packaging>

    <properties>
        <revision>2.22.2</revision>
        <sha1/>
        <changelist>-SNAPSHOT</changelist>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>tools.bestquality</groupId>
                <artifactId>ci-maven-plugin</artifactId>
                <version>0.0.14</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>expand-pom</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
    
</project>
```

### Installing from workflow or pipeline
A typical workflow or pipeline step will involve building and testing the project. In this case, the
`sha1` ci property can be used to namespace the branch build preventing any artifact collisions. This
is necessary if your organization captures all build artifacts:
```shell
# export BUILD_NUMBER=22
mvn clean install -Dsha1="-${BUILD_NUMBER}"
```

When built, the installed `pom.xml` will be expanded to:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>...</groupId>
    <artifactId>...</artifactId>
    <version>2.22.2-22-SNAPSHOT</version>
    <packaging>...</packaging>

    <properties>
        <revision>2.22.2</revision>
        <sha1>-22</sha1>
        <changelist>-SNAPSHOT</changelist>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>tools.bestquality</groupId>
                <artifactId>ci-maven-plugin</artifactId>
                <version>0.0.14</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>expand-pom</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
    
</project>
```

### Deploying
To deploy a release or non-snapshot build, such as when a tag is pushed:
```shell
# export RELEASE_VERSION=2.22.2
mvn clean deploy -Drevision="${RELEASE_VERSION}" -Dchangelist=
```

When deployed, the uploaded `pom.xml` will be expanded to:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>...</groupId>
    <artifactId>...</artifactId>
    <version>2.22.2</version>
    <packaging>...</packaging>

    <properties>
        <revision>2.22.2</revision>
        <sha1/>
        <changelist/>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>tools.bestquality</groupId>
                <artifactId>ci-maven-plugin</artifactId>
                <version>0.0.14</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>expand-pom</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
    
</project>
```

## Example Uses
This project uses the plugin combined with GitHub actions workflows. Here is the essential project config:

```xml
<project>
    <version>${revision}${sha1}${changelist}</version>
    <properties>
        <revision>2.22.2</revision>
        <sha1/>
        <changelist>-SNAPSHOT</changelist>
    </properties>
    <build>
        <plugins>
            <plugin>
                <groupId>tools.bestquality</groupId>
                <artifactId>ci-maven-plugin</artifactId>
                <version>0.0.14</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>expand-pom</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

### Feature Branch Builds & Pull Requests
Pushes to feature branches or pull requests result in a build that uses the current run number as the `sha1` value:

```shell
# GITHUB_RUN_ID: 2074468849
# POM version expanded to: 2.22.2-2074468849-SNAPSHOT
mvn -e -B -ntp -P ci clean install -Dsha1="-${GITHUB_RUN_ID}"
```

Generated POM:
```xml
<project>
    <version>2.22.2-2074468849-SNAPSHOT</version>
    <properties>
        <revision>2.22.2</revision>
        <sha1>-2074468849</sha1>
        <changelist>-SNAPSHOT</changelist>
    </properties>
    ...
</project>
```

### OSSRH Snapshot Deployment
Pushes to the default branch result in a snapshot deployment to the OSSRH snapshot repository. In this case, all CI 
friendly properties obtain their values from the property definitions held in the `pom.xml`. The maven command looks 
like this:

```shell
# POM version expanded to: 2.22.2-SNAPSHOT
mvn -e -B -ntp -P ci -P ossrh clean deploy
```

Generated POM:
```xml
<project>
    <version>2.22.2-SNAPSHOT</version>
    <properties>
        <revision>2.22.2</revision>
        <sha1/>
        <changelist>-SNAPSHOT</changelist>
    </properties>
    ...
</project>
```

### OSSRH Release Deployment
Tags applied to the repository result in a release deployment to the OSSRH release repository. In this case the
`revision` property takes its value from the tag name and the `changelist` is cleared to remove the `-SNAPSHOT`
qualifier:

```shell
# GITHUB_REF_NAME: 2.22.22
# POM version expanded to: 2.22.22
mvn -e -B -ntp -P ci -P ossrh clean deploy -Drevision="${GITHUB_REF_NAME}" -Dchangelist=""
```

Generated POM:
```xml
<project>
    <version>2.22.22</version>
    <properties>
        <revision>2.22.22</revision>
        <sha1/>
        <changelist/>
    </properties>
    ...
</project>
```

Next, the patch component of the revision project property value is incremented by one and the `changelist` property
retains its project value of `-SNAPSHOT` to prepare the default branch for the next iteration of development:

```shell
# GITHUB_REF_NAME: 2.22.22
# POM revision property updated to: 2.22.23
mvn -e -B -ntp -P ci ci:increment-pom -Drevision="${GITHUB_REF_NAME}"
```

Updated `pom.xml`:
```xml
<project>
    <version>${revision}${sha1}${changelist}</version>
    <properties>
        <revision>2.22.23</revision>
        <sha1/>
        <changelist>-SNAPSHOT</changelist>
    </properties>
    ...
</project>
```

Finally, version references to the current release are updated in documentation and the `pom.xml`
along with the updated docs are pushed to a release branch where they can be reviewed and merged
back into the default branch.

## Upcoming Features
* Goal to update any references to the project version in documentation, i.e. `README.md`