# Contribution Guidelines

## Releasing
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
                <version>0.0.20</version>
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
Pushes to feature branches or pull requests trigger the 
[Maven Install Snapshot Workflow](https://github.com/Best-Quality-Engineering/ci-maven-workflows#install-snapshot)
resulting in a build that uses the current run number as the `sha1` value:

```shell
# GITHUB_RUN_ID: 2074468849
# POM version expanded to: 2.22.2-2074468849-SNAPSHOT
mvn -e -B -ntp clean install -Dsha1="-${GITHUB_RUN_ID}"
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
Pushes to the default branch trigger the 
[Deploy OSSRH Snapshot Workflow](https://github.com/Best-Quality-Engineering/ci-maven-workflows#deploy-snapshot) which 
results in a deployment to the OSSRH snapshot repository. In this case, all CI friendly properties obtain their values 
from the property definitions held in the `pom.xml`. The maven command looks like this:

```shell
# POM version expanded to: 2.22.2-SNAPSHOT
mvn -e -B -ntp -P ossrh clean deploy
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
Tags applied to the repository from a [GitHub release](https://github.com/Best-Quality-Engineering/ci-maven-plugin/releases/new) 
will trigger the [Deploy OSSRH Release Workflow](https://github.com/Best-Quality-Engineering/ci-maven-workflows#deploy-release) 
which results in a deployment to the OSSRH release repository. In this case the `revision` property takes its value from 
the tag name and the `changelist` is cleared to remove the `-SNAPSHOT` qualifier:

```shell
# GITHUB_REF_NAME: 2.22.22
# POM version expanded to: 2.22.22
mvn -e -B -ntp -P ossrh clean deploy -Drevision="${GITHUB_REF_NAME}" -Dchangelist=""
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