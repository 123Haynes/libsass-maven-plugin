package wrm.libsass;

import static wrm.libsass.SassCompiler.InputSyntax.sass;

import io.bit3.jsass.CompilationException;
import io.bit3.jsass.Options;
import io.bit3.jsass.Output;
import java.io.File;
import java.net.URI;

public class SassCompiler {

  private String includePaths;
  private io.bit3.jsass.OutputStyle outputStyle;
  private boolean generateSourceComments;
  private boolean generateSourceMap;
  private boolean omitSourceMappingUrl;
  private boolean embedSourceMapInCss;
  private boolean embedSourceContentsInSourceMap;
  private SassCompiler.InputSyntax inputSyntax;
  private int precision;
  private boolean enableClasspathAwareImporter;

  /**
   * All paths passed to this method must be relative to the same directory.
   */
  public Output compileFile(
      String inputPathAbsolute, //
      String outputPathRelativeToInput, //
      String sourceMapPathRelativeToInput //

  ) throws CompilationException {

    String inputOmitSpace = inputPathAbsolute.replaceAll("%20", " ");
    String outputOmitSpace = outputPathRelativeToInput.replaceAll("%20", " ");

    URI inputFile = new File(inputOmitSpace).toURI();
    URI outputFile = new File(outputOmitSpace).toURI();

    Options opt = getConfiguredOptions(inputPathAbsolute, sourceMapPathRelativeToInput);

    io.bit3.jsass.Compiler c = new io.bit3.jsass.Compiler();

    return c.compileFile(inputFile, outputFile, opt);
  }

  private Options getConfiguredOptions(String inputPathAbsolute,
      String sourceMapPathRelativeToInput) {
    Options opt = new Options();

    if (includePaths != null) {
      for (String path : includePaths.split(";")) {
        opt.getIncludePaths().add(new File(path));
      }
    }
    String allIncludePaths = new File(inputPathAbsolute).getParent();
    opt.getIncludePaths().add(new File(allIncludePaths));

    opt.setIsIndentedSyntaxSrc(inputSyntax == sass);
    opt.setOutputStyle(outputStyle);

    opt.setSourceComments(generateSourceComments);
    opt.setPrecision(precision);

    if (generateSourceMap) {
      opt.setSourceMapFile(new File(sourceMapPathRelativeToInput).toURI());
      opt.setSourceMapContents(embedSourceContentsInSourceMap);
      opt.setSourceMapEmbed(embedSourceMapInCss);
      opt.setOmitSourceMapUrl(omitSourceMappingUrl);
    } else {
      opt.setSourceMapContents(false);
      opt.setSourceMapEmbed(false);
      opt.setOmitSourceMapUrl(true);
    }

    if (enableClasspathAwareImporter) {
      opt.getImporters().add(new ClasspathAwareImporter());
    }

    return opt;
  }

  public void setEmbedSourceMapInCss(final boolean embedSourceMapInCss) {
    this.embedSourceMapInCss = embedSourceMapInCss;
  }

  public void setEmbedSourceContentsInSourceMap(final boolean embedSourceContentsInSourceMap) {
    this.embedSourceContentsInSourceMap = embedSourceContentsInSourceMap;
  }

  public void setGenerateSourceComments(final boolean generateSourceComments) {
    this.generateSourceComments = generateSourceComments;
  }

  public void setGenerateSourceMap(final boolean generateSourceMap) {
    this.generateSourceMap = generateSourceMap;
  }

  public void setIncludePaths(final String includePaths) {
    this.includePaths = includePaths;
  }

  public void setInputSyntax(final InputSyntax inputSyntax) {
    this.inputSyntax = inputSyntax;
  }

  public void setOmitSourceMappingUrl(final boolean omitSourceMappingUrl) {
    this.omitSourceMappingUrl = omitSourceMappingUrl;
  }

  public void setOutputStyle(final io.bit3.jsass.OutputStyle outputStyle) {
    this.outputStyle = outputStyle;
  }

  public void setOutputStyle(final OutputStyle outputStyle) {
    this.outputStyle = io.bit3.jsass.OutputStyle.values()[outputStyle.ordinal()];
  }

  public enum OutputStyle {
    nested, expanded, compact, compressed
  }

  public void setPrecision(final int precision) {
    this.precision = precision;
  }

  public void setEnableClasspathAwareImporter(boolean enableClasspathAwareImporter) {
    this.enableClasspathAwareImporter = enableClasspathAwareImporter;
  }

  public static enum InputSyntax {
    sass, scss
  }
}