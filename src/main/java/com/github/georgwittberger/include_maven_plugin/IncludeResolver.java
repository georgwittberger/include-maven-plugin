package com.github.georgwittberger.include_maven_plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

public class IncludeResolver {
  private final Reader reader;
  private final Writer writer;
  private final File baseDirectory;
  private final Charset encoding;

  public IncludeResolver(Reader reader, Writer writer, File baseDirectory, Charset encoding) {
    this.reader = reader;
    this.writer = writer;
    this.baseDirectory = baseDirectory;
    this.encoding = encoding;
  }

  public void resolve() throws IOException {
    int input;
    String line = "";
    boolean crRead = false;
    while ((input = reader.read()) > -1) {
      char character = (char) input;
      if (crRead && character != '\n') {
        processLine(line);
        line = "";
        crRead = false;
      }
      line += character;
      if (character == '\n') {
        processLine(line);
        line = "";
        crRead = false;
        continue;
      }
      if (character == '\r')
        crRead = true;
    }
    if (line.length() > 0)
      processLine(line);
  }

  protected void processLine(String line) throws IOException {
    if (line.startsWith("//=include ") || line.startsWith("//=include\t")) {
      String includeFileName = line.substring(11).trim();
      File includeFile = new File(baseDirectory, includeFileName);
      if (!includeFile.isFile() || !includeFile.canRead())
        throw new IOException("File to include cannot be read: " + includeFile.getAbsolutePath());

      InputStreamReader reader = null;
      try {
        reader = new InputStreamReader(new FileInputStream(includeFile), encoding);
        IncludeResolver includeResolver = new IncludeResolver(reader, writer, includeFile.getParentFile(), encoding);
        includeResolver.resolve();
      } finally {
        if (reader != null) {
          try {
            reader.close();
          } catch (IOException ignore) {
          }
        }
      }
    } else {
      writer.write(line);
    }
  }
}
