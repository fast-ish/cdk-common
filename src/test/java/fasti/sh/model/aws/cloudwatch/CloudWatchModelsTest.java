package fasti.sh.model.aws.cloudwatch;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.model.aws.kms.Kms;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for CloudWatch model records.
 */
class CloudWatchModelsTest {

  @Test
  void testLogGroupConfBasic() {
    var conf = new LogGroupConf(
      "my-log-group",
      "STANDARD",
      "ONE_WEEK",
      null,
      "DESTROY",
      Map.of());

    assertEquals("my-log-group", conf.name());
    assertEquals("STANDARD", conf.type());
    assertEquals("ONE_WEEK", conf.retention());
    assertNull(conf.kms());
  }

  @Test
  void testLogGroupConfWithKms() {
    var kms = new Kms("alias/logs-key", "Logs encryption key", true, true, "ENCRYPT_DECRYPT", "SYMMETRIC_DEFAULT", "RETAIN");
    var conf = new LogGroupConf(
      "encrypted-logs",
      "STANDARD",
      "ONE_MONTH",
      kms,
      "RETAIN",
      Map.of("Encrypted", "true"));

    assertNotNull(conf.kms());
    assertEquals("alias/logs-key", conf.kms().alias());
  }

  @Test
  void testLogGroupConfInfrequentAccess() {
    var conf = new LogGroupConf(
      "infrequent-logs",
      "INFREQUENT_ACCESS",
      "SIX_MONTHS",
      null,
      "DESTROY",
      Map.of());

    assertEquals("INFREQUENT_ACCESS", conf.type());
    assertEquals("SIX_MONTHS", conf.retention());
    assertRecordToString(conf);
  }

  @Test
  void testLogGroupConfEquality() {
    var conf1 = new LogGroupConf("logs1", "STANDARD", "ONE_WEEK", null, "DESTROY", Map.of());
    var conf2 = new LogGroupConf("logs1", "STANDARD", "ONE_WEEK", null, "DESTROY", Map.of());

    assertRecordEquality(conf1, conf2);
  }

  @Test
  void testLogGroupConfWithLongRetention() {
    var conf = new LogGroupConf(
      "long-term-logs",
      "STANDARD",
      "TEN_YEARS",
      null,
      "RETAIN",
      Map.of("RetentionYears", "10"));

    assertEquals("TEN_YEARS", conf.retention());
    assertEquals("RETAIN", conf.removalPolicy());
    assertEquals(1, conf.tags().size());
  }

  @Test
  void testAlarmConfBasic() {
    var alarm = AlarmConf
      .builder()
      .name("high-cpu-alarm")
      .description("Alert when CPU is high")
      .metricNamespace("AWS/EC2")
      .metricName("CPUUtilization")
      .statistic("Average")
      .dimensions(Map.of("InstanceId", "i-1234567890"))
      .periodMinutes(5)
      .evaluationPeriods(2)
      .threshold(80.0)
      .comparisonOperator("GreaterThanThreshold")
      .treatMissingData("notBreaching")
      .alarmActions(List.of("arn:aws:sns:us-east-1:123456789012:alerts"))
      .tags(Map.of())
      .build();

    assertEquals("high-cpu-alarm", alarm.name());
    assertEquals("AWS/EC2", alarm.metricNamespace());
    assertEquals("CPUUtilization", alarm.metricName());
    assertEquals(80.0, alarm.threshold());
    assertEquals(2, alarm.evaluationPeriods());
    assertRecordToString(alarm);
  }

  @Test
  void testAlarmConfMemory() {
    var alarm = AlarmConf
      .builder()
      .name("high-memory-alarm")
      .description("Memory usage alert")
      .metricNamespace("CWAgent")
      .metricName("mem_used_percent")
      .statistic("Maximum")
      .dimensions(Map.of("InstanceId", "i-abc123"))
      .periodMinutes(1)
      .evaluationPeriods(3)
      .threshold(90.0)
      .comparisonOperator("GreaterThanThreshold")
      .treatMissingData("breaching")
      .alarmActions(List.of())
      .tags(Map.of("Team", "ops"))
      .build();

    assertEquals(90.0, alarm.threshold());
    assertEquals(1, alarm.periodMinutes());
    assertEquals("breaching", alarm.treatMissingData());
  }

  @Test
  void testMetricFilterConfBasic() {
    var filter = MetricFilterConf
      .builder()
      .filterName("error-count")
      .logGroupName("/aws/lambda/my-function")
      .filterPattern("[ERROR]")
      .metricNamespace("CustomMetrics")
      .metricName("ErrorCount")
      .metricValue("1")
      .defaultValue(0.0)
      .build();

    assertEquals("error-count", filter.filterName());
    assertEquals("/aws/lambda/my-function", filter.logGroupName());
    assertEquals("[ERROR]", filter.filterPattern());
    assertEquals("CustomMetrics", filter.metricNamespace());
    assertEquals("ErrorCount", filter.metricName());
    assertEquals(0.0, filter.defaultValue());
    assertRecordToString(filter);
  }

  @Test
  void testMetricFilterConf404s() {
    var filter = MetricFilterConf
      .builder()
      .filterName("404-count")
      .logGroupName("/aws/api/logs")
      .filterPattern("[... status=404 ...]")
      .metricNamespace("API")
      .metricName("NotFoundCount")
      .metricValue("1")
      .defaultValue(null)
      .build();

    assertEquals("404-count", filter.filterName());
    assertNull(filter.defaultValue());
  }
}
