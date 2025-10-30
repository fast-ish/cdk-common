package fasti.sh.model.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

public class CommonTest {

  @Test
  public void testCommonBuilder() {
    // Test that Common can be built with builder
    Common common = Common
      .builder()
      .id("test-id")
      .account("123456789012")
      .region("us-east-1")
      .organization("test-org")
      .name("test-name")
      .alias("test-alias")
      .environment("dev")
      .version("1.0.0")
      .domain("example.com")
      .tags(Map.of("key", "value"))
      .build();

    assertNotNull(common);
    assertEquals("test-id", common.id());
    assertEquals("123456789012", common.account());
    assertEquals("us-east-1", common.region());
    assertEquals("test-org", common.organization());
    assertEquals("test-name", common.name());
    assertEquals("test-alias", common.alias());
    assertEquals("dev", common.environment());
    assertEquals("1.0.0", common.version());
    assertEquals("example.com", common.domain());
    assertEquals(Map.of("key", "value"), common.tags());
  }

  @Test
  public void testCommonIdGeneration() {
    // Test that Common.id() generates deterministic IDs
    String id1 = Common.id("test");
    String id2 = Common.id("test");
    String id3 = Common.id("different");

    assertNotNull(id1);
    assertNotNull(id2);
    assertNotNull(id3);

    // Same input should generate same ID
    assertEquals(id1, id2);

    // Different input should generate different ID
    assertFalse(id1.equals(id3));

    // ID should be lowercase
    assertEquals(id1, id1.toLowerCase());

    // ID should be 15 characters or less
    assertTrue(id1.length() <= 15);

    // ID should start with a letter
    assertTrue(Character.isLetter(id1.charAt(0)));
  }

  @Test
  public void testCommonIdRandomGeneration() {
    // Test that Common.id_() generates random IDs
    String id1 = Common.id_();
    String id2 = Common.id_();

    assertNotNull(id1);
    assertNotNull(id2);

    // IDs should be 10 characters
    assertEquals(10, id1.length());
    assertEquals(10, id2.length());

    // IDs should be lowercase letters
    assertTrue(id1.matches("[a-z]+"));
    assertTrue(id2.matches("[a-z]+"));
  }

  @Test
  public void testCommonMapsMerge() {
    // Test Maps.from() with two maps
    Map<String, String> map1 = Map.of("a", "1", "b", "2");
    Map<String, String> map2 = Map.of("b", "3", "c", "4");

    Map<String, String> result = Common.Maps.from(map1, map2);

    assertNotNull(result);
    assertEquals(3, result.size());
    assertEquals("1", result.get("a"));
    assertEquals("3", result.get("b")); // map2 should override map1
    assertEquals("4", result.get("c"));
  }

  @Test
  public void testCommonMapsMergeThree() {
    // Test Maps.from() with three maps
    Map<String, String> map1 = Map.of("a", "1", "b", "2");
    Map<String, String> map2 = Map.of("b", "3", "c", "4");
    Map<String, String> map3 = Map.of("c", "5", "d", "6");

    Map<String, String> result = Common.Maps.from(map1, map2, map3);

    assertNotNull(result);
    assertEquals(4, result.size());
    assertEquals("1", result.get("a"));
    assertEquals("3", result.get("b"));
    assertEquals("5", result.get("c")); // map3 should override map2
    assertEquals("6", result.get("d"));
  }

  @Test
  public void testCommonMapsMergeWithNulls() {
    // Test Maps.from() with null maps
    Map<String, String> map1 = Map.of("a", "1");

    Map<String, String> result1 = Common.Maps.from(null, null);
    Map<String, String> result2 = Common.Maps.from(map1, null);
    Map<String, String> result3 = Common.Maps.from(null, map1);

    assertNotNull(result1);
    assertTrue(result1.isEmpty());

    assertNotNull(result2);
    assertEquals(map1, result2);

    assertNotNull(result3);
    assertEquals(map1, result3);
  }

  @Test
  public void testCommonToBuilder() {
    // Test toBuilder() functionality
    Common original = Common
      .builder()
      .id("test-id")
      .account("123456789012")
      .region("us-east-1")
      .build();

    Common modified = original
      .toBuilder()
      .region("us-west-2")
      .build();

    assertNotNull(modified);
    assertEquals("test-id", modified.id());
    assertEquals("123456789012", modified.account());
    assertEquals("us-west-2", modified.region());
  }
}
