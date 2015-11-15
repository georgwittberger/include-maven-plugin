package com.github.georgwittberger.include_maven_plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "resolve", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class ResolveMojo extends AbstractMojo {
  @Parameter(property = "inputFiles", required = true)
  private List<String> inputFiles;
  @Parameter(property = "outputDirectory", required = true, defaultValue = "${project.build.outputDirectory}")
  private File outputDirectory;
  @Parameter(property = "sourceEncoding", required = true, defaultValue = "${project.build.sourceEncoding}")
  private String sourceEncoding;

  public void execute() throws MojoExecutionException, MojoFailureException {
    if (inputFiles == null || inputFiles.isEmpty())
      throw new MojoFailureException("No input files provided");
    if (!outputDirectory.exists() && !outputDirectory.mkdirs())
      throw new MojoFailureException("Output directory could not be created: " + outputDirectory.getAbsolutePath());

    Charset encoding = Charset.forName(sourceEncoding);
    getLog().info("Processing files using encoding: " + encoding);

    for (String inputFileName : inputFiles) {
      File inputFile = new File(inputFileName);
      if (!inputFile.isFile() || !inputFile.canRead())
        throw new MojoFailureException("Input file cannot be read: " + inputFile.getAbsolutePath());

      File outputFile = new File(outputDirectory, inputFile.getName());

      getLog().info("Reading input file  : " + inputFile.getAbsolutePath());
      getLog().info("Writing output file : " + outputFile.getAbsolutePath());

      InputStreamReader reader = null;
      OutputStreamWriter writer = null;
      try {
        reader = new InputStreamReader(new FileInputStream(inputFile), encoding);
        writer = new OutputStreamWriter(new FileOutputStream(outputFile), encoding);
        IncludeResolver includeResolver = new IncludeResolver(reader, writer, inputFile.getParentFile(), encoding);
        includeResolver.resolve();
      } catch (IOException e) {
        throw new MojoFailureException("Input file could not be processed: " + inputFile.getAbsolutePath(), e);
      } finally {
        if (reader != null) {
          try {
            reader.close();
          } catch (IOException ignore) {
          }
        }
        if (writer != null) {
          try {
            writer.close();
          } catch (IOException ignore) {
          }
        }
      }
    }
  }
}
