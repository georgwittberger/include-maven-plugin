# Include Maven Plugin

## About

This plugin is inspired by the gulp-include plugin (https://github.com/wiledal/gulp-include) which allows to include files in each other using a special comment syntax:

    //=include relative/path/to/file

Having these two files:

-   /main.js
-   /partial/part.js

With the contents:

main.js:

    var main = 'This is from the main file';
    //=include partial/part.js

partial/part.js:

    var part = 'This is from the part file';

Using the plugin on `main.js` will result in the output file:

main.js:

    var main = 'This is from the main file';
    var part = 'This is from the part file';

## Usage

    <build>
      <plugins>
        <plugin>
          <groupId>com.github.georgwittberger.include-maven-plugin</groupId>
          <artifactId>include-maven-plugin</artifactId>
          <version>1.0.0-SNAPSHOT</version>
          <executions>
            <execution>
              <id>resolve-includes</id>
              <goals>
                <goal>resolve</goal>
              </goals>
              <configuration>
                <inputFiles>
                  <inputFile>src/main/client-resources/main.js</inputFile>
                </inputFiles>
              </configuration>
            </execution>
          </executions>
        </plugin>
      </plugins>
    </build>

Configuration parameters:

-   **inputFiles**: Collection of input files to process. These files may contain include comments which are resolved in the respective output file.
-   **outputDirectory**: Target directory to write the output files to. Default: `${project.build.outputDirectory}`
-   **sourceEncoding**: Character encoding to use when resolving includes. Also the encoding for output files. Default: `${project.build.sourceEncoding}`
