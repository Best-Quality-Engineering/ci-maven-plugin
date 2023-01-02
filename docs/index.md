---
layout: default
title: "Maven CI Plugin"
---
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
            <version>{{site.version}}</version>
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

{% include important.html content="
Unlike the [Flatten Maven Plugin](https://www.mojohaus.org/flatten-maven-plugin/) this goal will **not reformat
and strip comments** from the published `pom.xml` files. This is critical if your POMs contain important
information in its comments.
" %}

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

### `ci:replace-content`
By default, this goal is bound to the `verify` phase and can be used to replace version references in documentation.

This goal can be configured with a list of documents, i.e.:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>bestquality</groupId>
    <artifactId>ci-pom</artifactId>
    <version>${revision}</version>
    <packaging>jar</packaging>
    <properties>
        <revision>2.22.2-SNAPSHOT</revision>
    </properties>
    <build>
        <plugins>
            <plugin>
                <groupId>tools.bestquality</groupId>
                <artifactId>ci-maven-plugin</artifactId>
                <version>{{site.version}}</version>
                <configuration>
                    <documents>
                        <document>
                            <location>${project.basedir}/README.md</location>
                            <encoding>utf-8</encoding>
                            <pattern><![CDATA[(?sm)(<artifactId>ci-maven-plugin<\/artifactId>\s+<version>).*?(<\/version>)]]></pattern>
                            <replacement><![CDATA[$1${project.version}$2]]></replacement>
                        </document>
                        <document>
                            <location>${project.basedir}/docs/_config.yml</location>
                            <encoding>utf-8</encoding>
                            <pattern><![CDATA[(version:).*]]></pattern>
                            <replacement><![CDATA[$1 ${project.version}]]></replacement>
                        </document>
                        <document>
                            <location>${project.basedir}/pom.xml</location>
                            <encoding>utf-8</encoding>
                            <pattern><![CDATA[(<maven.ci.version>).*(<\/maven.ci.version>)]]></pattern>
                            <replacement><![CDATA[$1${project.version}$2]]></replacement>
                        </document>
                    </documents>
                </configuration>
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

Then, in a release script, the goal can be executed to update version references in the configured documents:
```shell
echo "Updating version references in documentation"
mvn ci:replace-content -Drevision="${GITHUB_REF_NAME}"
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
                <version>{{site.version}}</version>
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
                <version>{{site.version}}</version>
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
                <version>{{site.version}}</version>
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
