package fasti.sh.model.aws.apigw.usageplan;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.serialization.Mapper;
import org.junit.jupiter.api.Test;

class UsagePlanConfTest {

  @Test
  void testRecordInstantiation() {
    var throttle = new ThrottleConf(10.0, 100);
    var quota = new QuotaConf(true, 1000, software.amazon.awscdk.services.apigateway.Period.DAY);
    var usagePlan = new UsagePlanConf("api-usage-plan", "Production API usage plan", throttle, quota);

    assertEquals("api-usage-plan", usagePlan.name());
    assertEquals("Production API usage plan", usagePlan.description());
    assertEquals(throttle, usagePlan.throttle());
    assertEquals(quota, usagePlan.quota());
  }

  @Test
  void testRecordWithNullValues() {
    var usagePlan = new UsagePlanConf("basic-plan", null, null, null);

    assertEquals("basic-plan", usagePlan.name());
    assertNull(usagePlan.description());
    assertNull(usagePlan.throttle());
    assertNull(usagePlan.quota());
  }

  @Test
  void testRecordEquality() {
    var throttle = new ThrottleConf(5.0, 50);
    var quota = new QuotaConf(false, null, null);
    var plan1 = new UsagePlanConf("test-plan", "Test", throttle, quota);
    var plan2 = new UsagePlanConf("test-plan", "Test", throttle, quota);

    assertRecordEquality(plan1, plan2);
  }

  @Test
  void testToString() {
    var usagePlan = new UsagePlanConf("test", "desc", null, null);
    assertRecordToString(usagePlan);
  }

  @Test
  void testSerialization() throws Exception {
    var throttle = new ThrottleConf(10.0, 100);
    var quota = new QuotaConf(true, 1000, software.amazon.awscdk.services.apigateway.Period.DAY);
    var usagePlan = new UsagePlanConf("api-plan", "API usage plan", throttle, quota);

    var json = Mapper.get().writeValueAsString(usagePlan);
    assertNotNull(json);
    assertTrue(json.contains("api-plan"));
  }

  @Test
  void testDeserialization() throws Exception {
    var json = """
      {
        "name": "test-plan",
        "description": "Test plan",
        "throttle": {"rateLimit": 5.0, "burstLimit": 50},
        "quota": {"enabled": true, "limit": 500, "period": "DAY"}
      }
      """;

    var usagePlan = Mapper.get().readValue(json, UsagePlanConf.class);
    assertNotNull(usagePlan);
    assertEquals("test-plan", usagePlan.name());
    assertEquals("Test plan", usagePlan.description());
    assertNotNull(usagePlan.throttle());
    assertEquals(5.0, usagePlan.throttle().rateLimit());
  }
}
