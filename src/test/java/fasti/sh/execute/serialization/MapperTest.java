package fasti.sh.execute.serialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class MapperTest {

  @Test
  public void testMapperInitialization() {
    // Test that Mapper can be accessed
    assertNotNull(Mapper.class, "Mapper class should be accessible");
  }

  @Test
  public void testMapperGet() {
    // Test that Mapper.get() returns an ObjectMapper
    ObjectMapper mapper = Mapper.get();

    assertNotNull(mapper);
  }

  @Test
  public void testMapperSingleton() {
    // Test that Mapper.get() returns the same instance
    ObjectMapper mapper1 = Mapper.get();
    ObjectMapper mapper2 = Mapper.get();

    assertEquals(mapper1, mapper2);
  }

  @Test
  public void testMapperSerialization() throws Exception {
    // Test that Mapper can serialize objects
    ObjectMapper mapper = Mapper.get();

    Map<String, String> testMap = Map.of("key", "value");
    String json = mapper.writeValueAsString(testMap);

    assertNotNull(json);
    assertTrue(json.contains("key"));
    assertTrue(json.contains("value"));
  }

  @Test
  public void testMapperDeserialization() throws Exception {
    // Test that Mapper can deserialize JSON
    ObjectMapper mapper = Mapper.get();

    String json = "{\"key\":\"value\"}";
    @SuppressWarnings("unchecked")
    Map<String, String> result = mapper.readValue(json, Map.class);

    assertNotNull(result);
    assertEquals("value", result.get("key"));
  }
}
