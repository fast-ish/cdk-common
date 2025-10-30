package fasti.sh.model.main.common;

import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.serialization.Mapper;
import org.junit.jupiter.api.Test;

/**
 * Tests for Bare model class.
 */
class BareTest {

  @Test
  void testBareInstantiation() {
    var bare = new Bare();

    assertNotNull(bare);
  }

  @Test
  void testBareSerialization() throws Exception {
    var bare = new Bare();
    var json = Mapper.get().writeValueAsString(bare);

    assertNotNull(json);
    assertTrue(json.contains("{}") || json.isEmpty() || json.equals("{}"));
  }

  @Test
  void testBareDeserialization() throws Exception {
    var json = "{}";

    var bare = Mapper.get().readValue(json, Bare.class);

    assertNotNull(bare);
  }

  @Test
  void testBareEquality() {
    var bare1 = new Bare();
    var bare2 = new Bare();

    assertEquals(bare1, bare2);
    assertEquals(bare1.hashCode(), bare2.hashCode());
  }
}
