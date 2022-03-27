# Maven CI Plugin
A Maven plugin for use in CI pipelines based on the patterns described in the
[Maven Ci Friendly documentation](https://maven.apache.org/maven-ci-friendly.html).

[![Build Status](https://img.shields.io/travis/ruffkat/maven-ci-plugin/master?color=green)](https://travis-ci.com/ruffkat/maven-ci-plugin)
[![codecov](https://codecov.io/gh/ruffkat/XXX/branch/master/graph/badge.svg)](https://codecov.io/gh/ruffkat/maven-ci-plugin)
[![Maven Central](https://img.shields.io/maven-central/v/tools.bestquality/maven-ci-plugin.svg?color=green&label=maven%20central)](https://search.maven.org/search?q=g:tools.bestquality%20AND%20a:maven-ci-plugin)

## Usage
```xml
<build>
    <plugins>
        <plugin>
            <groupId>tools.bestquality</groupId>
            <artifactId>maven-ci-plugin</artifactId>
            <version>0.0.1</version>
            <executions>
                <execution>
                    <goals>
                        <goal>clean</goal>
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
* writes the expanded pom file to `target/generated-poms/ci-pom.xml` and sets it as the project's `pom.xml` file

### `ci:clean`
By default, this goal is bound to the `clean` phase and will remove the expanded `target/generated-poms/ci-pom.xml` file

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
                <artifactId>maven-ci-plugin</artifactId>
                <version>0.0.1</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>clean</goal>
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
        <sha1>22</sha1>
        <changelist>-SNAPSHOT</changelist>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>tools.bestquality</groupId>
                <artifactId>maven-ci-plugin</artifactId>
                <version>0.0.1</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>clean</goal>
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
mvn clean deploy -Dchangelist=""
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
                <artifactId>maven-ci-plugin</artifactId>
                <version>0.0.1</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>clean</goal>
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
* Goal to increment the deconstructed version's `revision` element for next iteration of development after a release