package fasti.sh.model.aws.athena;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests for Athena model records.
 */
class AthenaModelsTest {

  @Test
  void testWorkGroupBasic() {
    var workGroup = new WorkGroup(
      "primary",
      "s3://my-bucket/query-results/",
      false,
      false,
      0L);

    assertEquals("primary", workGroup.name());
    assertEquals("s3://my-bucket/query-results/", workGroup.output());
    assertFalse(workGroup.enforceWorkGroupConfiguration());
    assertFalse(workGroup.publishCloudWatchMetricsEnabled());
    assertEquals(0L, workGroup.bytesScannedCutoffPerQuery());
    assertRecordToString(workGroup);
  }

  @Test
  void testWorkGroupWithEnforcement() {
    var workGroup = new WorkGroup(
      "analytics",
      "s3://analytics-bucket/results/",
      true,
      true,
      10000000000L);

    assertTrue(workGroup.enforceWorkGroupConfiguration());
    assertTrue(workGroup.publishCloudWatchMetricsEnabled());
    assertEquals(10000000000L, workGroup.bytesScannedCutoffPerQuery());
  }

  @Test
  void testWorkGroupWithMetrics() {
    var workGroup = new WorkGroup(
      "production",
      "s3://prod-bucket/queries/",
      false,
      true,
      5000000000L);

    assertTrue(workGroup.publishCloudWatchMetricsEnabled());
    assertEquals(5000000000L, workGroup.bytesScannedCutoffPerQuery());
  }

  @Test
  void testWorkGroupEquality() {
    var wg1 = new WorkGroup("test", "s3://bucket/", false, false, 0L);
    var wg2 = new WorkGroup("test", "s3://bucket/", false, false, 0L);

    assertRecordEquality(wg1, wg2);
  }

  @Test
  void testWorkGroupHighCutoff() {
    var workGroup = new WorkGroup(
      "unlimited",
      "s3://unlimited-bucket/",
      false,
      false,
      1000000000000L);

    assertEquals(1000000000000L, workGroup.bytesScannedCutoffPerQuery());
  }
}
