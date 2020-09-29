package wrm;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import io.bit3.jsass.CompilationException;
import io.bit3.jsass.Output;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.sonatype.plexus.build.incremental.BuildContext;
import wrm.libsass.SassCompiler;

public abstract class AbstractSassMojo extends AbstractMojo {

  /**
   * The directory in which the compiled CSS files will be placed. The default value is
   * <tt>${project.build.directory}</tt>
   *
   * @parameter property="project.build.directory"
   * @required
   */
  protected File outputPath;
  /**
   * The directories from which the source .scss files will be read, ';'-separated.
   * These directories will be traversed recursively, and all .scss files found in
   * these directories or subdirectories will be compiled.
   * The default value is <tt>src/main/sass</tt>
   *
   * @parameter default-value="src/main/sass"
   */
  protected String inputPath;
  /**
   * Additional include path, ';'-separated. The default value is <tt>null</tt>
   *
   * @parameter
   */
  protected String includePath;
  /**
   * Output style for the generated css code. One of <tt>nested</tt>, <tt>expanded</tt>,
   * <tt>compact</tt>, <tt>compressed</tt>. Note that as of libsass 3.1, <tt>expanded</tt>
   * and <tt>compact</tt> are the same as <tt>nested</tt>. The default value is
   * <tt>nested</tt>.
   *
   * @parameter default-value="nested"
   */
  private SassCompiler.OutputStyle outputStyle;
  /**
   * Emit comments in the compiled CSS indicating the corresponding source line. The default value
   * is <tt>false</tt>
   *
   * @parameter default-value="false"
   */
  private boolean generateSourceComments;
  /**
   * Generate source map files. The generated source map files will be placed in the directory
   * specified by <tt>sourceMapOutputPath</tt>. The default value is <tt>true</tt>.
   *
   * @parameter default-value="true"
   */
  private boolean generateSourceMap;
  /**
   * The directory in which the source map files that correspond to the compiled CSS will be placed.
   * The default value is <tt>${project.build.directory}</tt>
   *
   * @parameter property="project.build.directory"
   */
  private String sourceMapOutputPath;
  /**
   * Prevents the generation of the <tt>sourceMappingURL</tt> special comment as the last line of
   * the compiled CSS. The default value is <tt>false</tt>.
   *
   * @parameter default-value="false"
   */
  private boolean omitSourceMapingUrl;
  /**
   * Embeds the whole source map data directly into the compiled CSS file by transforming
   * <tt>sourceMappingURL</tt> into a data URI. The default value is <tt>false</tt>.
   *
   * @parameter default-value="false"
   */
  private boolean embedSourceMapInCss;
  /**
   * Embeds the contents of the source .scss files in the source map file instead of the paths to
   * those files. The default value is <tt>false</tt>
   *
   * @parameter default-value="false"
   */
  private boolean embedSourceContentsInSourceMap;
  /**
   * Switches the input syntax used by the files to either <tt>sass</tt> or <tt>scss</tt>. The
   * default value is <tt>scss</tt>.
   *
   * @parameter default-value="scss"
   */
  private SassCompiler.InputSyntax inputSyntax;
  /**
   * Precision for fractional numbers. The default value is <tt>5</tt>.
   *
   * @parameter default-value="5"
   */
  private int precision;
  /**
   * Enables classpath aware importer which make possible to <tt>@import</tt> files from classpath
   * and WebJars.
   *
   * @parameter default-value="false"
   */
  private boolean enableClasspathAwareImporter;
  /**
   * should fail the build in case of compilation errors.
   *
   * @parameter default-value="true"
   */
  protected boolean failOnError;

  /**
   * Copy source files to output directory.
   *
   * @parameter default-value="false"
   */
  private boolean copySourceToOutput;

  /**
   * The maven project.
   *
   * @parameter property="project"
   * @required
   * @readonly
   */
  protected MavenProject project;

  /**
   * The build context.
   *
   * @component
   */
  protected BuildContext buildContext;

  protected SassCompiler compiler;

  private static final Pattern PATTERN_ERROR_JSON_LINE = Pattern
      .compile("[\"']line[\"'][:\\s]+([0-9]+)");
  private static final Pattern PATTERN_ERROR_JSON_COLUMN = Pattern
      .compile("[\"']column[\"'][:\\s]+([0-9]+)");

  public AbstractSassMojo() {
    super();
  }

  protected void compile() throws Exception {

    final AtomicInteger errorCount = new AtomicInteger(0);
    final AtomicInteger fileCount = new AtomicInteger(0);

    if (inputPath != null) {
      for (String path : inputPath.split(";")) {

        final Path root = project.getBasedir().toPath().resolve(Paths.get(path));
        String fileExt = getFileExtension();
        String globPattern = "glob:{**/,}*." + fileExt;
        getLog().debug("Glob = " + globPattern);

        final PathMatcher matcher = FileSystems.getDefault().getPathMatcher(globPattern);

        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
          @Override
          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
              throws IOException {
            if (matcher.matches(file) && !file.getFileName().toString().startsWith("_")) {
              fileCount.incrementAndGet();
              if (!processFile(root, file)) {
                errorCount.incrementAndGet();
              }
            }

            return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
          }
        });
      }
    }
    getLog().info("Compiled " + fileCount + " files");
    if (errorCount.get() > 0) {
      if (failOnError) {
        throw new Exception("Failed with " + errorCount.get() + " errors");
      } else {
        getLog().error(
            "Failed with " + errorCount.get() + " errors. Continuing due to failOnError=false.");
      }
    }
  }

  protected String getFileExtension() {
    return inputSyntax.toString();
  }

  protected void validateConfig() {
    if (!generateSourceMap) {
      if (embedSourceMapInCss) {
        getLog().warn("embedSourceMapInCSS=true is ignored. Cause: generateSourceMap=false");
      }
      if (embedSourceContentsInSourceMap) {
        getLog()
            .warn("embedSourceContentsInSourceMap=true is ignored. Cause: generateSourceMap=false");
      }
    }
  }

  private void setCompileClasspath() {
    try {
      Set<URL> urls = new HashSet<>();
      List<String> elements = project.getCompileClasspathElements();
      for (String element : elements) {
        urls.add(new File(element).toURI().toURL());
      }

      ClassLoader contextClassLoader = URLClassLoader.newInstance(
          urls.toArray(new URL[0]),
          Thread.currentThread().getContextClassLoader());

      Thread.currentThread().setContextClassLoader(contextClassLoader);

    } catch (DependencyResolutionRequiredException e) {
      throw new RuntimeException(e);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  protected SassCompiler initCompiler() {
    setCompileClasspath();

    SassCompiler compiler = new SassCompiler();
    compiler.setEmbedSourceMapInCss(this.embedSourceMapInCss);
    compiler.setEmbedSourceContentsInSourceMap(this.embedSourceContentsInSourceMap);
    compiler.setGenerateSourceComments(this.generateSourceComments);
    compiler.setGenerateSourceMap(this.generateSourceMap);
    compiler.setIncludePaths(this.includePath);
    compiler.setInputSyntax(this.inputSyntax);
    compiler.setOmitSourceMappingUrl(this.omitSourceMapingUrl);
    compiler.setOutputStyle(this.outputStyle);
    compiler.setPrecision(this.precision);
    compiler.setEnableClasspathAwareImporter(this.enableClasspathAwareImporter);
    return compiler;
  }

  protected boolean processFile(Path inputRootPath, Path inputFilePath) throws IOException {
    getLog().debug("Processing File " + inputFilePath);

    Path relativeInputPath = inputRootPath.relativize(inputFilePath);

    Path outputRootPath = this.outputPath.toPath();
    Path outputFilePath = outputRootPath.resolve(relativeInputPath);
    String fileExtension = getFileExtension();
    outputFilePath = Paths.get(outputFilePath.toAbsolutePath().toString()
        .replaceFirst("\\." + fileExtension + "$", ".css"));

    Path sourceMapRootPath = Paths.get(this.sourceMapOutputPath);
    Path sourceMapOutputPath = sourceMapRootPath.resolve(relativeInputPath);
    sourceMapOutputPath = Paths
        .get(sourceMapOutputPath.toAbsolutePath().toString().replaceFirst("\\.scss$", ".css.map"));

    if (copySourceToOutput) {
      Path inputOutputPath = outputRootPath.resolve(relativeInputPath);
      inputOutputPath.toFile().mkdirs();
      Files.copy(inputFilePath, inputOutputPath, REPLACE_EXISTING);
      buildContext.refresh(inputOutputPath.toFile());
      inputFilePath = inputOutputPath;
    }

    Output out;
    try {
      out = compiler.compileFile(
          inputFilePath.toAbsolutePath().toString(),
          outputFilePath.toAbsolutePath().toString(),
          sourceMapOutputPath.toAbsolutePath().toString()
      );
    } catch (CompilationException e) {
      getLog().error(e.getMessage());
      getLog().debug(e);

      // we need this info from json:
      // "line": 4,
      // "column": 1,
      // - a full blown parser for this would probably be an overkill, let's just regex
      String errorJson = e.getErrorJson();
      int line = 0;
      int column = 0;
      if (errorJson != null) { // defensive, in case we don't always get it
        Matcher lineMatcher = PATTERN_ERROR_JSON_LINE.matcher(errorJson);
        if (lineMatcher.find()) {
          try {
            line = Integer.parseInt(lineMatcher.group(1));
            // in case regex doesn't cut it anymore
          } catch (IndexOutOfBoundsException | NumberFormatException e1) {
            getLog().error("Failed to parse error json line: " + e1.getMessage());
            getLog().debug(e1);
          }
        }
        Matcher columnMatcher = PATTERN_ERROR_JSON_COLUMN.matcher(errorJson);
        if (columnMatcher.find()) {
          try {
            column = Integer.parseInt(columnMatcher.group(1));
            // in case regex doesn't cut it anymore
          } catch (IndexOutOfBoundsException | NumberFormatException e1) {
            getLog().error("Failed to parse error json column: " + e1.getMessage());
            getLog().debug(e1);
          }
        }
      }
      buildContext.addMessage(inputFilePath.toFile(), line, column, e.getErrorMessage(),
          BuildContext.SEVERITY_ERROR, e);

      return false;
    }

    getLog().debug("Compilation finished.");

    writeContentToFile(outputFilePath, out.getCss());
    if (out.getSourceMap() != null) {
      writeContentToFile(sourceMapOutputPath, out.getSourceMap());
    }
    return true;
  }

  private void writeContentToFile(Path outputFilePath, String content) throws IOException {
    File f = outputFilePath.toFile();
    f.getParentFile().mkdirs();
    f.createNewFile();
    OutputStreamWriter os = null;
    try {
      os = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8);
      os.write(content);
      os.flush();
    } finally {
      if (os != null) {
        os.close();
      }
    }
    buildContext.refresh(outputFilePath.toFile());
    getLog().debug("Written to: " + f);
  }

}
