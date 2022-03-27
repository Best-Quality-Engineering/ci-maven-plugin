# Maven CI Plugin
[![Build Status](https://img.shields.io/travis/ruffkat/maven-ci-plugin/master?color=green)](https://travis-ci.com/ruffkat/maven-ci-plugin)
[![codecov](https://codecov.io/gh/ruffkat/XXX/branch/master/graph/badge.svg)](https://codecov.io/gh/ruffkat/maven-ci-plugin)
[![Maven Central](https://img.shields.io/maven-central/v/tools.bestquality/maven-ci-plugin.svg?color=green&label=maven%20central)](https://search.maven.org/search?q=g:tools.bestquality%20AND%20a:maven-ci-plugin)

# Project Name
A Maven plugin for use in CI pipelines based on the patterns described in the 
[Maven Ci Friendly documentation](https://maven.apache.org/maven-ci-friendly.html).

# What this plugin will do:
* Expand the "template" project POM using CI properties into a consumable POM
* Update project README.md (or other documentation) referencing versions with the expanded version

## Installation
```xml
<plugin>
  <groupId>tools.bestquality</groupId>
  <artifactId>maven-ci-plugin</artifactId>
  <version>0.0.0</version>
</plugin>
```
