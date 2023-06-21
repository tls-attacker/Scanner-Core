# Scanner Core

![licence](https://img.shields.io/badge/License-Apachev2-brightgreen.svg)

Scanner Core provides a modular framework to define, execute and evaluate probes. Each probe may yield a result which
is condensed into a report afterward. Moreover, Scanner Core features several concepts to analyze the results by
computing an aggregated score based on rating influencers and running guideline checks.

## Installation

In order to compile and use Scanner-Core, you need to have Java 11 and Maven 3 installed. On Ubuntu you can install
both by running:

```bash
$ sudo apt install openjdk-11-jdk maven
```

If you have your environment configured properly, you can build Scanner-Core as follows:

```bash
$ git clone https://github.com/tls-attacker/Scanner-Core.git
$ cd Scanner-Core
$ mvn clean install
```

If you would like to use this project as a dependency, you may also use the precompiled builds available at Maven
Central. To include Scanner-Core into your project, add the following section to your projects pom.xml, replacing
`VERSION_HERE` with the most recent release version:

```xml
<dependency>
    <groupId>de.rub.nds</groupId>
    <artifactId>scanner-core</artifactId>
    <version>VERSION_HERE</version>
</dependency>
```

## Concepts

### Probes

A probe represents a task to execute which yields a result. The result is then later combined with the results of other
probes into a fully fledged scan report. Each probe extends the `ScannerProbe` abstract base class which defines
several abstract methods to be overridden:

- `getRequirements` - Returns a requirement which needs to be fulfilled for this probe to be executed. Several
  requirements can be found in the `de.rub.nds.scanner.core.probe.requirements` package.
- `adjustConfig` - Performs any preliminary steps before probe execution. Usually this method is used to check for
  certain features in the scan report.
- `executeTest` - Executes the probe.
- `merge` - Implements the logic on how to merge the results obtained by the probes with the scan report.

*Hint: Methods are listed by their lifecycle order.*

In addition to active probes, Scanner-Core implements the concept of `AfterProbe`s. `AfterProbe`s are being executed
once all active probes have been successfully executed or no more can be executed due to missing requirements, and
serve the purpose of analyzing the scan report in greater detail.

### Guidelines

A guideline is a list of checks which are executed upon the scan report once all probes have been executed. It may
be used to highlight certain analyzed properties in the final scan report. Guidelines (as of now) require manual
invocation by the calling project and are not being executed as part of `ThreadedScanJobExecutor`. Related classes can
be found in the `de.rub.nds.scanner.core.guideline` package.

### Rating

The concept of rating is used to condense the report into a single, comparable integer score and give the user
recommendations on how to increase the score. Similar to guidelines, as of now, rating needs to be invoked by
the calling project and is not part of the `ThreadedScanJobExecutor` class. Related classes can be found in the
`de.rub.nds.scanner.core.report.rating` package.
