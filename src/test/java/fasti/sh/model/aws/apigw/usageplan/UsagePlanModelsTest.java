package fasti.sh.model.aws.apigw.usageplan;

import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.serialization.Mapper;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.services.apigateway.Period;

/**
 * Tests for API Gateway usage plan model classes.
 */
class UsagePlanModelsTest {

  @Test
  void testThrottleConfInstantiation() {
    var throttle = new ThrottleConf(100.0, 200);

    assertEquals(100.0, throttle.rateLimit());
    assertEquals(200, throttle.burstLimit());
  }

  @Test
  void testThrottleConfSerialization() throws Exception {
    var throttle = new ThrottleConf(50.5, 100);
    var json = Mapper.get().writeValueAsString(throttle);

    assertNotNull(json);
    assertTrue(json.contains("50.5"));
    assertTrue(json.contains("100"));
  }

  @Test
  void testThrottleConfDeserialization() throws Exception {
    var json = """
      {"rateLimit":75.0,"burstLimit":150}
      """;
    var throttle = Mapper.get().readValue(json, ThrottleConf.class);

    assertNotNull(throttle);
    assertEquals(75.0, throttle.rateLimit());
    assertEquals(150, throttle.burstLimit());
  }

  @Test
  void testQuotaConfInstantiation() {
    var quota = new QuotaConf(true, 10000, Period.DAY);

    assertTrue(quota.enabled());
    assertEquals(10000, quota.limit());
    assertEquals(Period.DAY, quota.period());
  }

  @Test
  void testQuotaConfSerialization() throws Exception {
    var quota = new QuotaConf(true, 5000, Period.MONTH);
    var json = Mapper.get().writeValueAsString(quota);

    assertNotNull(json);
    assertTrue(json.contains("5000"));
    assertTrue(json.contains("true"));
  }

  @Test
  void testQuotaConfDeserialization() throws Exception {
    var json = """
      {"enabled":false,"limit":1000,"period":"WEEK"}
      """;
    var quota = Mapper.get().readValue(json, QuotaConf.class);

    assertNotNull(quota);
    assertFalse(quota.enabled());
    assertEquals(1000, quota.limit());
    assertEquals(Period.WEEK, quota.period());
  }

  @Test
  void testUsagePlanConfInstantiation() {
    var throttle = new ThrottleConf(100.0, 200);
    var quota = new QuotaConf(true, 10000, Period.DAY);
    var usagePlan = new UsagePlanConf("basic-plan", "Basic usage plan", throttle, quota);

    assertEquals("basic-plan", usagePlan.name());
    assertEquals("Basic usage plan", usagePlan.description());
    assertNotNull(usagePlan.throttle());
    assertNotNull(usagePlan.quota());
    assertEquals(100.0, usagePlan.throttle().rateLimit());
    assertEquals(10000, usagePlan.quota().limit());
  }

  @Test
  void testUsagePlanConfSerialization() throws Exception {
    var throttle = new ThrottleConf(50.0, 100);
    var quota = new QuotaConf(true, 5000, Period.MONTH);
    var usagePlan = new UsagePlanConf("premium-plan", "Premium usage plan", throttle, quota);
    var json = Mapper.get().writeValueAsString(usagePlan);

    assertNotNull(json);
    assertTrue(json.contains("premium-plan"));
    assertTrue(json.contains("Premium usage plan"));
  }

  @Test
  void testUsagePlanConfDeserialization() throws Exception {
    var json = """
      {
        "name": "test-plan",
        "description": "Test plan",
        "throttle": {"rateLimit": 25.0, "burstLimit": 50},
        "quota": {"enabled": true, "limit": 2000, "period": "DAY"}
      }
      """;
    var usagePlan = Mapper.get().readValue(json, UsagePlanConf.class);

    assertNotNull(usagePlan);
    assertEquals("test-plan", usagePlan.name());
    assertEquals("Test plan", usagePlan.description());
    assertNotNull(usagePlan.throttle());
    assertEquals(25.0, usagePlan.throttle().rateLimit());
    assertNotNull(usagePlan.quota());
    assertTrue(usagePlan.quota().enabled());
  }
}
