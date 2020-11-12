# Changelog

* 0.2.26
  * remove outdated warning when using compact or compact expanded outputstyles
  * update dependency com.puppycrawl.tools:checkstyle to v8.37
  * update dependency io.bit3:jsass to v5.10.4
* 0.2.25
  * update dependency org.apache.maven.plugins:maven-surefire-plugin to v3.0.0-m5
  * update dependency org.webjars:webjars-locator-core to v0.46
  * update dependency com.puppycrawl.tools:checkstyle to v8.36.1
  * update dependency org.junit.jupiter:junit-jupiter-engine to v5.7.0
  * update dependency org.jacoco:jacoco-maven-plugin to v0.8.6
  * Test builds against JDK 15
* 0.2.24
  * update dependency com.puppycrawl.tools:checkstyle to v8.33
  * feature: allow multiple input directories separated by ;
* 0.2.23
  * update dependency org.junit.jupiter:junit-jupiter-engine to v5.6.2
  * update dependency com.puppycrawl.tools:checkstyle to v8.31
  * update dependency org.apache.maven.plugins:maven-checkstyle-plugin to v3.1.1
  * update dependency org.webjars:webjars-locator-core to v0.45
  * update dependency org.apache.maven.plugins:maven-javadoc-plugin to v3.2.0
* 0.2.22
  * update dependency org.apache.maven:maven-core to v3.6.3
  * update dependency org.apache.maven:maven-plugin-api to v3.6.3
  * update maven docker tag to v3.6.3
  * update dependency com.puppycrawl.tools:checkstyle to v8.28
  * update dependency org.slf4j:slf4j-api to v1.7.30
  * update dependency org.slf4j:slf4j-simple to v1.7.30
  * update dependency org.apache.maven.plugins:maven-source-plugin to v3.2.1
  * Document a limitation with the classpathAwareImporter
* 0.2.21
  * lower the target java version to java 8 and add the corresponding ci jobs.
* 0.2.20
  * fix a regression that was introduced in 0.2.17.
  * update dependency org.apache.maven.plugins:maven-surefire-plugin to v3.0.0-m4
* 0.2.19
  * update dependency org.webjars:webjars-locator-core to v0.43
* 0.2.18
  * update dependency com.puppycrawl.tools:checkstyle to v8.26
  * update dependency org.apache.maven.plugins:maven-source-plugin to v3.2.0
  * update dependency io.bit3:jsass to v5.10.3
* 0.2.17
  * renamed property `omitSourceMapingURL` to `omitSourceMapingUrl`
  * renamed property `embedSourceMapInCSS` to `embedSourceMapInCss`
  * enforce the google_checks checkstyle rules
  * update dependency org.slf4j:slf4j-api to v1.7.29
  * update dependency org.slf4j:slf4j-simple to v1.7.29
  * update dependency org.webjars:webjars-locator-core to v0.42
* 0.2.16
  * Update dependency io.bit3:jsass to v5.10.2
  * Update dependency org.jacoco:jacoco-maven-plugin to v0.8.5
* 0.2.15
  * Update dependency io.bit3:jsass to v5.10.1
* 0.2.14
  * Update dependency io.bit3:jsass to v5.10.0
* 0.2.13
  * update dependency org.junit.jupiter:junit-jupiter-engine to v5.5.2
  * update maven docker tag to v3.6.2
  * update dependency org.webjars:webjars-locator-core to v0.41
  * added LICENSE file
  * clarified a bit better that this project was forked.
* 0.2.12
  * update dependency org.webjars:webjars-locator-core to v0.40
  * update dependency org.apache.maven:maven-core to v3.6.2 
  * update dependency org.apache.maven:maven-plugin-api to v3.6.2
* 0.2.11 - upgraded libsass to 3.6.1
  * migrated all tests to junit 5
  * set java 11 as new build baseline
  * several dependency and plugin updates
  * added gitlab-ci builds
  * **forked project from https://github.com/warmuuh/libsass-maven-plugin**
* 0.2.10 - upgraded libsass to 3.5.3
* 0.2.9 - upgraded libsass to 3.4.7
  * refreshed output files for eclipse
  * enhanced error output with failing files - thanks to @VsevolodGolovanov
* 0.2.8 - upgraded libsass to 3.4.4
  * used compilation classpath for including webjars
  * fixed issue with os-dependent path-separator (using ';' for every OS)
  * plugin now aware of incremental builds
* 0.2.7 - upgraded libsass to 3.4.3
  * added webjar support - thanks to @flipp5b 
* 0.2.6 - upgraded libsass to 3.4.0
  * added libsass:watch goal to watch and recompile include directory - *thansk to @lorenzodee*
* 0.2.5 - added copySourceToOutput, changed default outputstyle to 'nested', upgraded libsass to 3.3.6
* 0.2.4 - fixed bug with empty spaces in path
* 0.2.3 - upgrade to libsass 3.3.4
* 0.2.2 - minor bugfixes, readded m2e lifecycle mapping 
* 0.2.1 - updated libsass to 3.3.3
* 0.2.0 - switched native bindings to bit3 bindings (using libsass 3.3.2), **java8-only**
* 0.1.7 - UTF8 encoding issue, used wrong file extension for sass style
* 0.1.6 - added m2e eclipse intergation, thanks @dashorst
* 0.1.5 - readded macOs binaries, thanks @tommix1987
* 0.1.4 - added contained libsass-version to artifact-version (e.g. `0.1.4-libsass_3.2.4-SNAPSHOT`). 
  * switched to new libsass API (sass_context.h)
  * removed image_path option (because of [#420](https://github.com/sass/libsass/issues/420))
  * added failOnError flag to skip errors and continue the build, if wanted
* 0.1.3 - fixed #10 - multi-module projects
* 0.1.2 - added PR #4, updated to libsass version 3.1 for windows, linux, macos - *thanks to @npiguet, @ogolberg*
* 0.1.1 - scss files can now be placed in inputpath/ directly
* 0.1.0 - changed artefact group to `com.github.warmuuh`
