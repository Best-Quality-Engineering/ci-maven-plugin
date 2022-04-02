# Maven CI Plugin
A Maven plugin for use in CI pipelines based on the patterns described in the
[Maven CI Friendly documentation](https://maven.apache.org/maven-ci-friendly.html).

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
            <version>0.0.13</version>
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