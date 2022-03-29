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
            <version>0.0.7</version>
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

### `ci:next-revision`
By default, this aggregator goal is bound to the `validate` phase and will read the top-level project's `revision`
property and increment it according to the desired version component.

Without customization, the goal will attempt to resolve the version component to increment by starting with the `build` 
and working it's way up to the `major` component. The following incrementors are available:
* `auto` (default)
* `major`
* `minor`
* `patch`
* `build`

It will write the results into `target/ci/next-revision.txt`

#### Writing to `stdout`
The goal can be executed from the command line to capture and assign the output to a variable:

```shell
next_revision=$(mvn -q ci:next-revision -Dforce-stdout=true)
```
or using a specific incrementor:
```shell
next_revision=$(mvn -q ci:next-revision -Dforce-stdout=true -incrementor=patch)
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
                <version>0.0.7</version>
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
                <version>0.0.7</version>
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
                <version>0.0.7</version>
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