package fasti.sh.model.aws.apigw.usageplan;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.serialization.Mapper;
import org.junit.jupiter.api.Test;

class ThrottleConfTest {

  @Test
  void testRecordInstantiation() {
    var throttle = new ThrottleConf(10.0, 100);

    assertEquals(10.0, throttle.rateLimit());
    assertEquals(100, throttle.burstLimit());
  }

  @Test
  void testRecordWithHighLimits() {
    var throttle = new ThrottleConf(1000.0, 5000);

    assertEquals(1000.0, throttle.rateLimit());
    assertEquals(5000, throttle.burstLimit());
  }

  @Test
  void testRecordWithLowLimits() {
    var throttle = new ThrottleConf(1.0, 10);

    assertEquals(1.0, throttle.rateLimit());
    assertEquals(10, throttle.burstLimit());
  }

  @Test
  void testRecordWithNullValues() {
    var throttle = new ThrottleConf(null, null);

    assertNull(throttle.rateLimit());
    assertNull(throttle.burstLimit());
  }

  @Test
  void testRecordEquality() {
    var throttle1 = new ThrottleConf(10.0, 100);
    var throttle2 = new ThrottleConf(10.0, 100);

    assertRecordEquality(throttle1, throttle2);
  }

  @Test
  void testToString() {
    var throttle = new ThrottleConf(5.0, 50);
    assertRecordToString(throttle);
  }

  @Test
  void testSerialization() throws Exception {
    var throttle = new ThrottleConf(10.0, 100);

    var json = Mapper.get().writeValueAsString(throttle);
    assertNotNull(json);
    assertTrue(json.contains("10.0") || json.contains("10"));
    assertTrue(json.contains("100"));
  }

  @Test
  void testDeserialization() throws Exception {
    var json = """
      {
        "rateLimit": 5.0,
        "burstLimit": 50
      }
      """;

    var throttle = Mapper.get().readValue(json, ThrottleConf.class);
    assertNotNull(throttle);
    assertEquals(5.0, throttle.rateLimit());
    assertEquals(50, throttle.burstLimit());
  }
}
