# Maven CI Plugin
A Maven plugin for use in CI pipelines based on the patterns described in the
[Maven Ci Friendly documentation](https://maven.apache.org/maven-ci-friendly.html).

[![Build Status](https://github.com/ruffkat/ci-maven-plugin/actions/workflows/ossrh-snapshot.yml/badge.svg)](https://github.com/ruffkat/ci-maven-plugin/actions/workflows/ossrh-snapshot.yml)
[![Release Status](https://github.com/ruffkat/ci-maven-plugin/actions/workflows/ossrh-release.yml/badge.svg)](https://github.com/ruffkat/ci-maven-plugin/actions/workflows/ossrh-release.yml)
[![Code Coverage](https://codecov.io/gh/ruffkat/ci-maven-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/ruffkat/ci-maven-plugin)
[![Maven Central](https://img.shields.io/maven-central/v/tools.bestquality/ci-maven-plugin?color=%234c1)](https://search.maven.org/search?q=g:tools.bestquality%20AND%20a:ci-maven-plugin)

## Usage
```xml
<build>
    <plugins>
        <plugin>
            <groupId>tools.bestquality</groupId>
            <artifactId>ci-maven-plugin</artifactId>
            <version>0.0.12</version>
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
By default, this goal is bound to the `process-resources` phase and considers the project's `pom.xml` as a template
producing an expanded version performing the following actions on the template:
* replaces all `${revision}`, `${sha1}`, and `${changelist}` property references found in the project's `pom.xml` file
* updates the values of `revision`, `sha1`, and `changelist` defined in the `<properties>` element
* writes the expanded pom file to `target/generated-poms/pom-ci.xml` and sets it as the project's `pom.xml` file

### `ci:increment-pom`
By default, this aggregator goal is bound to the `process-resources` phase and will update the project's top-level
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

### `ci:release-version`
By default, this aggregator goal is bound to the `validate` phase and will read the top-level project's `revision`
property and output the value to a file or standard out while removing the `-SNAPSHOT` qualifier. It does not make 
any pom modifications and can be used if the release process is not event driven.

If not explicitly configured to export the version to standard out, it will write the results 
into `target/ci/release-version.txt`.

#### Writing to `stdout`
The goal can be executed from the command line to capture and assign the output to a variable:

```shell
release_revision=$(mvn -q ci:release-version -Dscriptable=true)
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
                <version>0.0.12</version>
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

### Installing
To install from a typical non-release build:
```shell
# export BUILD_NUMBER=22
mvn clean install -Dsha1=".${BUILD_NUMBER}"
```

When installed, this will become:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>...</groupId>
    <artifactId>...</artifactId>
    <version>2.22.2.22-SNAPSHOT</version>
    <packaging>...</packaging>

    <properties>
        <revision>2.22.2</revision>
        <sha1>.22</sha1>
        <changelist>-SNAPSHOT</changelist>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>tools.bestquality</groupId>
                <artifactId>ci-maven-plugin</artifactId>
                <version>0.0.12</version>
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
To deploy a release:
```shell
mvn clean deploy -Dchangelist=
```

When deployed, this will become:
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
                <version>0.0.12</version>
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

## Upcoming Features
* Goal to update any references to the project version in documentation, i.e. `README.md`