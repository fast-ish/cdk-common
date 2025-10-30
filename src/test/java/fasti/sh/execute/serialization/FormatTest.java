package fasti.sh.execute.serialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import fasti.sh.model.main.Common;
import org.junit.jupiter.api.Test;
import software.constructs.Construct;
import software.constructs.Node;

public class FormatTest {

  @Test
  public void testFormatClassExists() {
    // Test that Format class is accessible
    assertNotNull(Format.class, "Format class should be accessible");
  }

  @Test
  public void testIdMethod() {
    // Test Format.id() method
    String result = Format.id("service", "component", "resource");
    assertEquals("service.component.resource", result);
  }

  @Test
  public void testIdMethodWithHyphens() {
    // Test Format.id() method replaces hyphens with dots
    String result = Format.id("service-name", "component-part");
    assertEquals("service.name.component.part", result);
  }

  @Test
  public void testNameMethod() {
    // Test Format.name() method
    String result = Format.name("service", "component", "resource");
    assertEquals("service-component-resource", result);
  }

  @Test
  public void testNameMethodWithDots() {
    // Test Format.name() method replaces dots with hyphens
    String result = Format.name("service.name");
    assertEquals("service-name", result);
  }

  @Test
  public void testDescribeMethod() {
    // Test Format.describe() method
    Common common = Common
      .builder()
      .organization("MyOrg")
      .environment("prod")
      .build();

    String result = Format.describe(common, "API", "Gateway");
    assertEquals("MyOrg prod API Gateway", result);
  }

  @Test
  public void testExportedMethod() {
    // Test Format.exported() method with mocked Construct
    Construct scope = mock(Construct.class);
    Node node = mock(Node.class);

    when(scope.getNode()).thenReturn(node);
    when(node.tryGetContext("hosted:synthesizer:name")).thenReturn(null);
    when(node.getContext("host:id")).thenReturn("host123");
    when(node.getContext("hosted:id")).thenReturn("hosted456");

    String result = Format.exported(scope, "-suffix");
    assertEquals("host123hosted456-suffix", result);
  }

  @Test
  public void testNamedMethod() {
    // Test Format.named() method with mocked Construct
    Construct scope = mock(Construct.class);
    Node node = mock(Node.class);

    when(scope.getNode()).thenReturn(node);
    when(node.tryGetContext("hosted:synthesizer:name")).thenReturn(null);
    when(node.getContext("host:id")).thenReturn("host123");
    when(node.getContext("hosted:id")).thenReturn("hosted456");

    String result = Format.named(scope, "resource");
    assertEquals("host123-hosted456-resource", result);
  }

  @Test
  public void testExportedMethodWithSynthesizerName() {
    // Test Format.exported() with hosted:synthesizer:name present
    Construct scope = mock(Construct.class);
    Node node = mock(Node.class);

    when(scope.getNode()).thenReturn(node);
    when(node.tryGetContext("hosted:synthesizer:name")).thenReturn("custom-synth");
    when(node.getContext("hosted:id")).thenReturn("hosted456");

    String result = Format.exported(scope, "-export");
    assertEquals("custom-synthhosted456-export", result);
  }

  @Test
  public void testNamedMethodWithSynthesizerName() {
    // Test Format.named() with hosted:synthesizer:name present
    Construct scope = mock(Construct.class);
    Node node = mock(Node.class);

    when(scope.getNode()).thenReturn(node);
    when(node.tryGetContext("hosted:synthesizer:name")).thenReturn("custom-synth");
    when(node.getContext("hosted:id")).thenReturn("hosted456");

    String result = Format.named(scope, "resource");
    assertEquals("custom-synth-hosted456-resource", result);
  }
}
