package fasti.sh.model.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class HostTest {

  @Test
  public void testHostCreation() {
    // Test that Host record can be created
    Common common = Common
      .builder()
      .id("test-id")
      .account("123456789012")
      .region("us-east-1")
      .build();

    Host<String> host = new Host<>(common);

    assertNotNull(host);
    assertEquals(common, host.common());
  }

  @Test
  public void testHostWithGenericType() {
    // Test that Host can work with different generic types
    Common common = Common
      .builder()
      .id("test-id")
      .build();

    Host<String> stringHost = new Host<>(common);
    Host<Integer> intHost = new Host<>(common);

    assertNotNull(stringHost);
    assertNotNull(intHost);
    assertEquals(common, stringHost.common());
    assertEquals(common, intHost.common());
  }
}
