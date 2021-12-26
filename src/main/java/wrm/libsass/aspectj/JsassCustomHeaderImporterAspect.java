package wrm.libsass.aspectj;

import io.bit3.jsass.context.ImportStack;
import io.bit3.jsass.importer.Import;
import io.bit3.jsass.importer.ImportException;
import io.bit3.jsass.importer.JsassCustomHeaderImporter;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * Aspect to work around an issue in Jsass.
 */
@Aspect
public class JsassCustomHeaderImporterAspect {

  /**
   * Replaces the method "createCustomHeaderImport" to fix a deprecation warning.
   *
   * <p>
   * Jsass is not maintained anymore, so a new release which fixes this error is unlikely.
   * This Aspect fixes https://gitlab.com/jsass/jsass/-/issues/95
   * </p>
   */
  @Around(""
      + "execution(* io.bit3.jsass.importer.JsassCustomHeaderImporter.createCustomHeaderImport(..))"
      + " && args(previous) && target(targetClass)")
  public Import callCreateCustomHeaderImport(ProceedingJoinPoint joinPoint, Import previous,
      JsassCustomHeaderImporter targetClass
  ) throws Throwable {

    //gain access to the private class variable "importStack"
    Class<?> clazz = targetClass.getClass();
    Field field = clazz.getDeclaredField("importStack");
    field.setAccessible(true);
    ImportStack importStack = (ImportStack) field.get(targetClass);

    int id = importStack.register(previous);

    StringBuilder source = new StringBuilder();

    // fix for #95 - JSASS_CUSTOM.scss deprecation warning
    source.append("$jsass-void: null;");

    // $jsass-void: jsass_import_stack_push(<id>) !global;
    source.append(
        String.format(
            "$jsass-void: jsass_import_stack_push(%d) !global;%n",
            id
        )
    );

    try {
      return new Import(
          new URI(previous.getAbsoluteUri() + "/JSASS_CUSTOM.scss"),
          new URI(previous.getAbsoluteUri() + "/JSASS_CUSTOM.scss"),
          source.toString()
      );
    } catch (URISyntaxException e) {
      throw new ImportException(e);
    }


  }
}

