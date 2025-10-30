package fasti.sh.execute.serialization;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for Template utility class.
 */
class TemplateTest {

  @Test
  void testParseSimpleTemplate() {
    var ctx = createTestContext();

    var result = Template.parse(ctx.scope(), "test/simple-template.mustache");

    assertNotNull(result);
    assertFalse(result.isEmpty());
  }

  @Test
  void testParseTemplateWithCustomValues() {
    var ctx = createTestContext();

    var customValues = Map
      .<String, Object>of(
        "custom:key",
        "custom-value",
        "another:key",
        "another-value");

    var result = Template.parse(ctx.scope(), "test/simple-template.mustache", customValues);

    assertNotNull(result);
    assertFalse(result.isEmpty());
  }

  @Test
  void testParseTemplateWithOverrides() {
    var ctx = createTestContext();

    var overrides = Map
      .<String, Object>of(
        "host:name",
        "overridden-name");

    var result = Template.parse(ctx.scope(), "test/simple-template.mustache", overrides);

    assertNotNull(result);
    assertFalse(result.isEmpty());
  }

  @Test
  void testParseTemplateMissingFileThrowsException() {
    var ctx = createTestContext();

    assertThrows(
      RuntimeException.class,
      () -> {
        Template.parse(ctx.scope(), "nonexistent/template.mustache");
      });
  }

  @Test
  void testDefaultsExtraction() {
    var ctx = createTestContext();

    var defaults = Template.defaults(ctx.scope());

    assertNotNull(defaults);
    assertTrue(defaults.containsKey("host:id"));
    assertTrue(defaults.containsKey("host:account"));
    assertTrue(defaults.containsKey("host:region"));
    assertTrue(defaults.containsKey("host:name"));
    assertTrue(defaults.containsKey("host:environment"));
    assertTrue(defaults.containsKey("hosted:id"));
    assertTrue(defaults.containsKey("hosted:account"));
  }

  @Test
  void testDefaultsHaveCorrectValues() {
    var ctx = createTestContext();

    var defaults = Template.defaults(ctx.scope());

    assertEquals("test", defaults.get("host:id"));
    assertEquals("test-org", defaults.get("host:organization"));
    assertEquals("123456789012", defaults.get("host:account"));
    assertEquals("us-east-1", defaults.get("host:region"));
    assertEquals("test-deployment", defaults.get("host:name"));
  }
}
