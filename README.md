Libsass Maven Plugin [![pipeline status](https://gitlab.com/haynes/libsass-maven-plugin/badges/master/pipeline.svg)](https://gitlab.com/haynes/libsass-maven-plugin/commits/master) [![Maven Central](https://img.shields.io/maven-central/v/com.gitlab.haynes/libsass-maven-plugin.svg)](https://mvnrepository.com/artifact/com.gitlab.haynes/libsass-maven-plugin)
==========

Libsass Maven Plugin uses [libsass](http://github.com/hcatlin/libsass) to compile sass files.
Uses [jsass](https://gitlab.com/jsass/jsass) to interface with C-library.

## This project was forked from https://github.com/warmuuh/libsass-maven-plugin version 0.2.10-libsass_3.5.3

Installation
-----
libsass-maven-plugin is available on central-repository since version 0.2.11

Usage
-----
Configure plugin in your pom.xml:

```
<build>
   <plugins>
      <plugin>
         <groupId>com.gitlab.haynes</groupId>
         <artifactId>libsass-maven-plugin</artifactId>
         <version>0.2.17</version>
         <executions>
            <execution>
               <phase>generate-resources</phase>
               <goals>
                  <goal>compile</goal>
               </goals>
            </execution>
         </executions>
         <configuration>
            <inputPath>${basedir}/src/main/sass/</inputPath>
            <outputPath>${basedir}/target/</outputPath>
            <includePath>${basedir}/src/main/sass/plugins/</includePath>
         </configuration>
      </plugin>
   </plugins>
</build>
```

Alternatively, you can use the `watch` goal to have the plugin watch your files and recompile on change:
```
mvn com.gitlab.haynes:libsass-maven-plugin:0.2.17:watch
```

Configuration Elements
----------------------

<table>
  <thead>
    <tr>
       <td>Element</td>
       <td>Default value</td>
       <td>Documentation</td>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>outputPath</td>
      <td><code>${project.build.directory}</code></td>
      <td>The directory in which the compiled CSS files will be placed.</td>
    </tr>
    <tr>
      <td>inputPath</td>
      <td><code>src/main/sass</code></td>
      <td>
        The directory from which the source <code>.scss</code> files will be read. This directory will be
        traversed recursively, and all <code>.scss</code> files found in this directory or subdirectories
        will be compiled.
      </td>
    </tr>
    <tr>
      <td>includePath</td>
      <td><code>null</code></td>
      <td>Additional include path, ';'-separated</td>
    </tr>
    <tr>
      <td>outputStyle</td>
      <td><code>nested</code></td>
      <td>
         Output style for the generated css code. One of <code>nested</code>, <code>expanded</code>,
         <code>compact</code>, <code>compressed</code>. Note that as of libsass 3.1, <code>expanded</code>
         and <code>compact</code> result in the same output as <code>nested</code>.
      </td>
    </tr>
    <tr>
      <td>generateSourceComments</td>
      <td><code>false</code></td>
      <td>
         Emit comments in the compiled CSS indicating the corresponding source line. The default
         value is <code>false</code>.
      </td>
    </tr>
    <tr>
      <td>generateSourceMap</td>
      <td><code>true</code></td>
      <td>
        Generate source map files. The generated source map files will be placed in the directory
        specified by <code>sourceMapOutputPath</code>.
      </td>
    </tr>
    <tr>
      <td>sourceMapOutputPath</td>
      <td><code>${project.build.directory}</code></td>
      <td>
        The directory in which the source map files that correspond to the compiled CSS will be placed
      </td>
    </tr>
    <tr>
      <td>omitSourceMapingUrl</td>
      <td><code>false</code></td>
      <td>
        Prevents the generation of the <code>sourceMappingUrl</code> special comment as the last
        line of the compiled CSS.
      </td>
    </tr>
    <tr>
      <td>embedSourceMapInCss</td>
      <td><code>false</code></td>
      <td>
        Embeds the whole source map data directly into the compiled CSS file by transforming
        <code>sourceMappingUrl</code> into a data URI.
      </td>
    </tr>
    <tr>
      <td>embedSourceContentsInSourceMap</td>
      <td><code>false</code></td>
      <td>
       Embeds the contents of the source <code>.scss</code> files in the source map file instead of the
       paths to those files
      </td>
    </tr>
    <tr>
      <td>inputSyntax</td>
      <td><code>scss</code></td>
      <td>
       Switches the input syntax used by the files to either <code>sass</code> or <code>scss</code>.
      </td>
    </tr>
    <tr>
      <td>precision</td>
      <td><code>5</code></td>
      <td>
       Precision for fractional numbers
      </td>
    </tr>
    <tr>
      <td>enableClasspathAwareImporter</td>
      <td><code>false</code></td>
      <td>
       Enables classpath aware importer which make possible to <code>@import</code> files from classpath and WebJars.
       For classpath resources use <code>@import 'path/to/resource/in/classpath';</code>.
       For WebJar resources a shortcut can be used: <code>@import '{package}/{path}';</code> imports resource
       <code>META-INF/resources/webjars/{package}/{version}/{path}</code>.
      </td>
    </tr>
     <tr>
      <td>failOnError</td>
      <td><code>true</code></td>
      <td>
       should fail the build in case of compilation errors.
      </td>
    </tr>
    <tr>
      <td>copySourceToOutput</td>
      <td><code>false</code></td>
      <td>
       copies all files from source directory to output directory
      </td>
    </tr>
  </tbody>
</table>


License
-------

MIT License.
