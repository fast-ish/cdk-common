package fasti.sh.model.aws.bcm;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for BCM (Billing and Cost Management) model records.
 */
class BcmModelsTest {

  @Test
  void testDataQueryBasic() {
    var query = new DataQuery(
      "SELECT * FROM COST_AND_USAGE_REPORT",
      Map.of());

    assertEquals("SELECT * FROM COST_AND_USAGE_REPORT", query.queryStatement());
    assertTrue(query.tableConfigurations().isEmpty());
    assertRecordToString(query);
  }

  @Test
  void testDataQueryWithTableConfig() {
    var tableConfig = Map
      .of(
        "COST_AND_USAGE_REPORT",
        Map
          .of(
            "TIME_GRANULARITY",
            "DAILY",
            "INCLUDE_RESOURCES",
            "true"));
    var query = new DataQuery(
      "SELECT line_item_usage_account_id, SUM(line_item_unblended_cost) AS cost FROM COST_AND_USAGE_REPORT GROUP BY line_item_usage_account_id",
      tableConfig);

    assertFalse(query.tableConfigurations().isEmpty());
    assertEquals(1, query.tableConfigurations().size());
    assertTrue(query.tableConfigurations().containsKey("COST_AND_USAGE_REPORT"));
  }

  @Test
  void testDataQueryEquality() {
    var q1 = new DataQuery("SELECT * FROM table", Map.of());
    var q2 = new DataQuery("SELECT * FROM table", Map.of());

    assertRecordEquality(q1, q2);
  }

  @Test
  void testOutputConfigurationBasic() {
    var output = new OutputConfiguration(
      "GZIP",
      "PARQUET",
      "S3",
      "OVERWRITE_REPORT");

    assertEquals("GZIP", output.compression());
    assertEquals("PARQUET", output.format());
    assertEquals("S3", output.outputType());
    assertEquals("OVERWRITE_REPORT", output.overwrite());
    assertRecordToString(output);
  }

  @Test
  void testOutputConfigurationCsv() {
    var output = new OutputConfiguration(
      "GZIP",
      "TEXT_OR_CSV",
      "S3",
      "CREATE_NEW_REPORT");

    assertEquals("TEXT_OR_CSV", output.format());
    assertEquals("CREATE_NEW_REPORT", output.overwrite());
  }

  @Test
  void testOutputConfigurationNoCompression() {
    var output = new OutputConfiguration(
      "NONE",
      "PARQUET",
      "S3",
      "OVERWRITE_REPORT");

    assertEquals("NONE", output.compression());
  }

  @Test
  void testOutputConfigurationEquality() {
    var o1 = new OutputConfiguration("GZIP", "PARQUET", "S3", "OVERWRITE_REPORT");
    var o2 = new OutputConfiguration("GZIP", "PARQUET", "S3", "OVERWRITE_REPORT");

    assertRecordEquality(o1, o2);
  }
}
