package test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.bit3.jsass.Output;
import io.bit3.jsass.OutputStyle;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wrm.libsass.SassCompiler;



/**
 * Tests for {@link SassCompiler}.
 *
 * @author ogolberg@vecna.com
 */
public class SassCompilerTest {

  private SassCompiler compiler;
  private Output out;

  /**
   * Preparations for each test.
   */
  @BeforeEach
  public void initCompiler() {
    compiler = new SassCompiler();
    compiler.setPrecision(5);
    compiler.setOutputStyle(OutputStyle.EXPANDED);
    compiler.setOmitSourceMappingUrl(false);
    compiler.setInputSyntax(SassCompiler.InputSyntax.scss);
    compiler.setEmbedSourceMapInCss(false);
    compiler.setEmbedSourceContentsInSourceMap(false);
    compiler.setGenerateSourceComments(false);
    compiler.setGenerateSourceMap(true);
    compiler.setIncludePaths(null);
    compiler.setEnableClasspathAwareImporter(true);
  }

  @Test
  public void testWithDefaultSettings() throws Exception {
    compile("/test.scss");

    assertCssContains("font: 100% Helvetica, sans-serif");
    assertCssContains("color: #333");
    assertCssContains("margin: 0");
  }

  @Test
  public void testWithOmitSourceMapUrlTrue() throws Exception {
    compiler.setOmitSourceMappingUrl(true);
    compiler.setGenerateSourceMap(true);
    compile("/test.scss");

    assertCssDoesNotContain("/*# sourceMappingURL=");
    assertNotNull(out.getSourceMap());
  }

  @Test
  public void testWithOmitSourceMapUrlFalse() throws Exception {
    compiler.setOmitSourceMappingUrl(false);
    compiler.setGenerateSourceMap(true);
    compile("/test.scss");

    assertCssContains("/*# sourceMappingURL=");
    assertNotNull(out.getSourceMap());
  }

  @Test
  public void testWithOutputStyleExpanded() throws Exception {
    // Warning: As of Libsass 3.1, expanded is the same as nested
    compiler.setOutputStyle(OutputStyle.EXPANDED);
    compile("/test.scss");

    assertCssContains("* {\n  margin: 0;\n}\n");
  }

  @Test
  public void testWithOutputStyleNested() throws Exception {
    compiler.setOutputStyle(OutputStyle.NESTED);
    compile("/test.scss");

    assertCssContains("* {\n  margin: 0; }\n");
  }

  @Test
  public void testWithOutputStyleCompressed() throws Exception {
    compiler.setOutputStyle(OutputStyle.COMPRESSED);
    compile("/test.scss");

    assertCssContains("*{margin:0}body{font:100% Helvetica,sans-serif;color:#333}");
  }

  @Test
  public void testWithOutputStyleCompact() throws Exception {
    // Warning: As of Libsass 3.1, compact is the same as nested
    compiler.setOutputStyle(OutputStyle.COMPACT);
    compile("/test.scss");

    assertCssContains("* { margin: 0; }\n");
  }

  @Test
  public void testWithGenerateSourceMapFalse() throws Exception {
    compiler.setGenerateSourceMap(false);
    compiler.setEmbedSourceContentsInSourceMap(true);
    compiler.setOmitSourceMappingUrl(false);
    compile("/test.scss");

    assertNull(out.getSourceMap());
    assertCssDoesNotContain("/*# sourceMappingURL=");
  }

  @Test
  public void testDefaultPrecision() throws Exception {
    compiler.setOutputStyle(OutputStyle.COMPRESSED);
    compile("/precision.scss");

    assertCssContains(".something{padding:0 0.8em .71429 0.8em}");
  }

  @Test
  public void testHighPrecision() throws Exception {
    compiler.setOutputStyle(OutputStyle.COMPRESSED);
    compiler.setPrecision(10);
    compile("/precision.scss");

    assertCssContains(".something{padding:0 0.8em .7142857143 0.8em}");
  }

  @Test
  public void testWebJar() throws Exception {
    compile("/webjar-test.scss");

    assertCssContains("*, *::before, *::after {\n" + "  -moz-box-sizing: border-box;\n"
        + "  -webkit-box-sizing: border-box;\n" + "  box-sizing: border-box;\n" + "}");
  }

  private void compile(String file) throws Exception {
    String absolutePath = new File(getClass().getResource(file).getFile()).getAbsolutePath();
    out = compiler.compileFile(absolutePath, "prout", "denver");
  }

  private void assertCssContains(String expected) {
    String css = out.getCss();
    String formatted = replaceNewLines(expected);
    assertTrue(css.contains(formatted),
        "Generated CSS does not contain: " + formatted + "\nbut got: " + css);
  }

  private String replaceNewLines(String expected) {
    return expected.replace("\n", System.lineSeparator());
  }

  private void assertCssDoesNotContain(String unwanted) {
    String css = out.getCss();
    String formatted = replaceNewLines(unwanted);
    assertFalse(css.contains(formatted), "Generated CSS contains: " + formatted + "\n" + css);
  }

  private void assertMapContains(String expected) {
    String sourceMap = out.getSourceMap();
    String formatted = replaceNewLines(expected);
    assertTrue(sourceMap.contains(formatted),
        "Generated SourceMap does not contain: " + formatted + "\n" + sourceMap);
  }

  private void assertMapDoesNotContain(String unwanted) {
    String sourceMap = out.getSourceMap();
    String formatted = replaceNewLines(unwanted);
    assertFalse(sourceMap.contains(formatted),
        "Generated SourceMap contains: " + formatted + "\n" + sourceMap);
  }
}
