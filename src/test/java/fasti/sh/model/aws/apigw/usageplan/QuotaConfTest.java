package fasti.sh.model.aws.apigw.usageplan;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.serialization.Mapper;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.services.apigateway.Period;

class QuotaConfTest {

  @Test
  void testRecordInstantiation() {
    var quota = new QuotaConf(true, 1000, Period.DAY);

    assertTrue(quota.enabled());
    assertEquals(1000, quota.limit());
    assertEquals(Period.DAY, quota.period());
  }

  @Test
  void testRecordWithDisabledQuota() {
    var quota = new QuotaConf(false, null, null);

    assertFalse(quota.enabled());
    assertNull(quota.limit());
    assertNull(quota.period());
  }

  @Test
  void testRecordWithWeeklyPeriod() {
    var quota = new QuotaConf(true, 5000, Period.WEEK);

    assertTrue(quota.enabled());
    assertEquals(5000, quota.limit());
    assertEquals(Period.WEEK, quota.period());
  }

  @Test
  void testRecordWithMonthlyPeriod() {
    var quota = new QuotaConf(true, 100000, Period.MONTH);

    assertTrue(quota.enabled());
    assertEquals(100000, quota.limit());
    assertEquals(Period.MONTH, quota.period());
  }

  @Test
  void testRecordEquality() {
    var quota1 = new QuotaConf(true, 2000, Period.DAY);
    var quota2 = new QuotaConf(true, 2000, Period.DAY);

    assertRecordEquality(quota1, quota2);
  }

  @Test
  void testToString() {
    var quota = new QuotaConf(true, 1000, Period.DAY);
    assertRecordToString(quota);
  }

  @Test
  void testSerialization() throws Exception {
    var quota = new QuotaConf(true, 1000, Period.DAY);

    var json = Mapper.get().writeValueAsString(quota);
    assertNotNull(json);
    assertTrue(json.contains("true"));
    assertTrue(json.contains("1000"));
  }

  @Test
  void testDeserialization() throws Exception {
    var json = """
      {
        "enabled": true,
        "limit": 500,
        "period": "DAY"
      }
      """;

    var quota = Mapper.get().readValue(json, QuotaConf.class);
    assertNotNull(quota);
    assertTrue(quota.enabled());
    assertEquals(500, quota.limit());
  }
}
