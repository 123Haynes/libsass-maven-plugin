package wrm.libsass;

import java.net.URI;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Testclass for the WebjarTranslator.
 */
public class WebJarTranslatorTest {
  @Test
  public void testTranslate() throws Exception {
    WebJarTranslator translator = new WebJarTranslator();

    URI importUri = URI.create("susy/sass/susy/_math.scss");
    Optional<URI> fullUri = translator.translate(importUri);
    Optional<URI> expectedUri = Optional.of(
        URI.create("META-INF/resources/webjars/susy/2.1.1/sass/susy/_math.scss"));

    Assertions.assertEquals(expectedUri, fullUri);
  }
}