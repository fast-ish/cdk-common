package fasti.sh.model.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class VersionTest {

  @Test
  public void testVersionClassExists() {
    // Test that Version class is accessible
    assertNotNull(Version.class, "Version class should be accessible");
  }

  @Test
  public void testVersionValues() {
    // Test that all versions exist
    assertEquals(3, Version.values().length);
    assertNotNull(Version.V1);
    assertNotNull(Version.V2);
    assertNotNull(Version.V3);
  }

  @Test
  public void testVersionOf() {
    // Test Version.of() method with different cases
    assertEquals(Version.V1, Version.of("v1"));
    assertEquals(Version.V2, Version.of("v2"));
    assertEquals(Version.V3, Version.of("v3"));
    assertEquals(Version.V1, Version.of("V1"));
    assertEquals(Version.V2, Version.of("V2"));
    assertEquals(Version.V3, Version.of("V3"));
  }

  @Test
  public void testVersionToString() {
    // Test toString() returns lowercase
    assertEquals("v1", Version.V1.toString());
    assertEquals("v2", Version.V2.toString());
    assertEquals("v3", Version.V3.toString());
  }

  @Test
  public void testVersionValueOf() {
    // Test standard valueOf() method
    assertEquals(Version.V1, Version.valueOf("V1"));
    assertEquals(Version.V2, Version.valueOf("V2"));
    assertEquals(Version.V3, Version.valueOf("V3"));
  }

  @Test
  public void testVersionOfInvalid() {
    // Test that invalid version throws exception
    assertThrows(IllegalArgumentException.class, () -> Version.of("INVALID"));
  }
}
