package fasti.sh.execute.aws;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.aws.bcm.BcmDataExportNestedStack;
import fasti.sh.model.aws.bcm.DataExportConf;
import fasti.sh.model.aws.bcm.DataQuery;
import fasti.sh.model.aws.bcm.DestinationConfigurations;
import fasti.sh.model.aws.bcm.OutputConfiguration;
import fasti.sh.model.aws.s3.S3Bucket;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.NestedStackProps;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.s3.BucketAccessControl;
import software.amazon.awscdk.services.s3.ObjectOwnership;

/**
 * Tests for AWS BCM (Billing and Cost Management) Data Export constructs.
 */
class BcmConstructsTest {

  @Test
  void testBcmDataExportBasic() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "bcm-export-" + System.currentTimeMillis(),
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      true,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of());

    var outputConfig = new OutputConfiguration(
      "gzip",
      "parquet",
      "custom",
      "overwrite_report");

    var destinationConfig = new DestinationConfigurations(
      "us-east-1",
      s3Conf.name(),
      "cost-exports/",
      outputConfig);

    var dataQuery = new DataQuery(
      "SELECT * FROM COST_AND_USAGE_REPORT",
      Map.of());

    var dataExportConf = new DataExportConf(
      "basic-cost-export",
      "Basic cost and usage export",
      dataQuery,
      destinationConfig,
      "daily",
      s3Conf,
      Map.of("Type", "cost-export"));

    var construct = new BcmDataExportNestedStack(
      ctx.scope(),
      ctx.common(),
      dataExportConf,
      NestedStackProps.builder().build());

    assertNotNull(construct);
    assertNotNull(construct.storage());
    assertNotNull(construct.export());
  }

  @Test
  void testBcmDataExportWithTableConfigurations() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "bcm-table-export-" + System.currentTimeMillis(),
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      true,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of());

    var outputConfig = new OutputConfiguration(
      "gzip",
      "parquet",
      "custom",
      "create_new_report");

    var destinationConfig = new DestinationConfigurations(
      "us-east-1",
      s3Conf.name(),
      "detailed-exports/",
      outputConfig);

    var tableConfigurations = Map
      .of(
        "COST_AND_USAGE_REPORT",
        Map
          .of(
            "TIME_GRANULARITY",
            "HOURLY",
            "INCLUDE_RESOURCES",
            "TRUE"));

    var dataQuery = new DataQuery(
      "SELECT line_item_usage_account_id, line_item_line_item_type FROM COST_AND_USAGE_REPORT",
      tableConfigurations);

    var dataExportConf = new DataExportConf(
      "detailed-cost-export",
      "Detailed cost export with table configurations",
      dataQuery,
      destinationConfig,
      "daily",
      s3Conf,
      Map.of("Detail", "high", "Granularity", "hourly"));

    var construct = new BcmDataExportNestedStack(
      ctx.scope(),
      ctx.common(),
      dataExportConf,
      NestedStackProps.builder().build());

    assertNotNull(construct);
    assertNotNull(construct.storage());
    assertNotNull(construct.export());
  }

  @Test
  void testBcmDataExportWeeklyRefresh() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "bcm-weekly-" + System.currentTimeMillis(),
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      true,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of());

    var outputConfig = new OutputConfiguration(
      "gzip",
      "parquet",
      "custom",
      "overwrite_report");

    var destinationConfig = new DestinationConfigurations(
      "us-west-2",
      s3Conf.name(),
      "weekly-reports/",
      outputConfig);

    var dataQuery = new DataQuery(
      "SELECT * FROM COST_AND_USAGE_REPORT WHERE line_item_usage_type LIKE '%Usage%'",
      Map.of());

    var dataExportConf = new DataExportConf(
      "weekly-usage-export",
      "Weekly usage cost export",
      dataQuery,
      destinationConfig,
      "weekly",
      s3Conf,
      Map.of("Frequency", "weekly"));

    var construct = new BcmDataExportNestedStack(
      ctx.scope(),
      ctx.common(),
      dataExportConf,
      NestedStackProps.builder().build());

    assertNotNull(construct);
    assertNotNull(construct.storage());
    assertNotNull(construct.export());
  }

  @Test
  void testBcmDataExportCsvFormat() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "bcm-csv-" + System.currentTimeMillis(),
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      true,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of());

    var outputConfig = new OutputConfiguration(
      "gzip",
      "text_or_csv",
      "custom",
      "overwrite_report");

    var destinationConfig = new DestinationConfigurations(
      "us-east-1",
      s3Conf.name(),
      "csv-exports/",
      outputConfig);

    var dataQuery = new DataQuery(
      "SELECT line_item_product_code, SUM(line_item_unblended_cost) AS cost FROM COST_AND_USAGE_REPORT GROUP BY line_item_product_code",
      Map.of());

    var dataExportConf = new DataExportConf(
      "csv-cost-by-service",
      "CSV format cost export by service",
      dataQuery,
      destinationConfig,
      "daily",
      s3Conf,
      Map.of("Format", "csv"));

    var construct = new BcmDataExportNestedStack(
      ctx.scope(),
      ctx.common(),
      dataExportConf,
      NestedStackProps.builder().build());

    assertNotNull(construct);
    assertNotNull(construct.storage());
    assertNotNull(construct.export());
  }

  @Test
  void testBcmDataExportMonthlyRefresh() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "bcm-monthly-" + System.currentTimeMillis(),
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      true,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of());

    var outputConfig = new OutputConfiguration(
      "gzip",
      "parquet",
      "custom",
      "create_new_report");

    var destinationConfig = new DestinationConfigurations(
      "us-east-1",
      s3Conf.name(),
      "monthly/",
      outputConfig);

    var dataQuery = new DataQuery(
      "SELECT * FROM COST_AND_USAGE_REPORT",
      Map.of());

    var dataExportConf = new DataExportConf(
      "monthly-export",
      "Monthly cost summary export",
      dataQuery,
      destinationConfig,
      "monthly",
      s3Conf,
      Map.of("Period", "monthly", "Type", "summary"));

    var construct = new BcmDataExportNestedStack(
      ctx.scope(),
      ctx.common(),
      dataExportConf,
      NestedStackProps.builder().build());

    assertNotNull(construct);
    assertNotNull(construct.storage());
    assertNotNull(construct.export());
  }

  @Test
  void testBcmDataExportNoCompression() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "bcm-nocomp-" + System.currentTimeMillis(),
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      true,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of());

    var outputConfig = new OutputConfiguration(
      "none",
      "parquet",
      "custom",
      "overwrite_report");

    var destinationConfig = new DestinationConfigurations(
      "us-east-1",
      s3Conf.name(),
      "uncompressed/",
      outputConfig);

    var dataQuery = new DataQuery(
      "SELECT * FROM COST_AND_USAGE_REPORT",
      Map.of());

    var dataExportConf = new DataExportConf(
      "uncompressed-export",
      "Uncompressed cost export",
      dataQuery,
      destinationConfig,
      "daily",
      s3Conf,
      Map.of("Compression", "none"));

    var construct = new BcmDataExportNestedStack(
      ctx.scope(),
      ctx.common(),
      dataExportConf,
      NestedStackProps.builder().build());

    assertNotNull(construct);
    assertNotNull(construct.storage());
    assertNotNull(construct.export());
  }

  @Test
  void testBcmDataExportMultiRegion() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "bcm-multiregion-" + System.currentTimeMillis(),
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      true,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of());

    var outputConfig = new OutputConfiguration(
      "gzip",
      "parquet",
      "custom",
      "create_new_report");

    var destinationConfig = new DestinationConfigurations(
      "eu-west-1",
      s3Conf.name(),
      "eu-costs/",
      outputConfig);

    var dataQuery = new DataQuery(
      "SELECT * FROM COST_AND_USAGE_REPORT WHERE bill_billing_entity = 'AWS'",
      Map.of());

    var dataExportConf = new DataExportConf(
      "eu-region-export",
      "EU region cost export",
      dataQuery,
      destinationConfig,
      "daily",
      s3Conf,
      Map.of("Region", "eu-west-1", "BillingEntity", "AWS"));

    var construct = new BcmDataExportNestedStack(
      ctx.scope(),
      ctx.common(),
      dataExportConf,
      NestedStackProps.builder().build());

    assertNotNull(construct);
    assertNotNull(construct.storage());
    assertNotNull(construct.export());
  }

  @Test
  void testBcmDataExportWithPrefix() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "bcm-prefix-" + System.currentTimeMillis(),
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      true,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of("Prefix", "cost-data"));

    var outputConfig = new OutputConfiguration(
      "gzip",
      "parquet",
      "custom",
      "overwrite_report");

    var destinationConfig = new DestinationConfigurations(
      "us-east-1",
      s3Conf.name(),
      "exports/billing/",
      outputConfig);

    var dataQuery = new DataQuery(
      "SELECT * FROM COST_AND_USAGE_REPORT",
      Map.of());

    var dataExportConf = new DataExportConf(
      "prefix-export",
      "Export with custom prefix",
      dataQuery,
      destinationConfig,
      "daily",
      s3Conf,
      Map.of());

    var construct = new BcmDataExportNestedStack(
      ctx.scope(),
      ctx.common(),
      dataExportConf,
      NestedStackProps.builder().build());

    assertNotNull(construct);
    assertNotNull(construct.storage());
  }

  @Test
  void testBcmDataExportApacRegion() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "bcm-apac-" + System.currentTimeMillis(),
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      true,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of());

    var outputConfig = new OutputConfiguration(
      "gzip",
      "parquet",
      "custom",
      "create_new_report");

    var destinationConfig = new DestinationConfigurations(
      "ap-southeast-1",
      s3Conf.name(),
      "apac-billing/",
      outputConfig);

    var dataQuery = new DataQuery(
      "SELECT * FROM COST_AND_USAGE_REPORT",
      Map.of());

    var dataExportConf = new DataExportConf(
      "apac-export",
      "APAC region export",
      dataQuery,
      destinationConfig,
      "daily",
      s3Conf,
      Map.of("Region", "apac"));

    var construct = new BcmDataExportNestedStack(
      ctx.scope(),
      ctx.common(),
      dataExportConf,
      NestedStackProps.builder().build());

    assertNotNull(construct);
  }

  @Test
  void testBcmDataExportWithComplexQuery() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "bcm-complex-" + System.currentTimeMillis(),
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      true,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of());

    var outputConfig = new OutputConfiguration(
      "gzip",
      "parquet",
      "athena",
      "overwrite_report");

    var destinationConfig = new DestinationConfigurations(
      "us-east-1",
      s3Conf.name(),
      "complex-reports/",
      outputConfig);

    var dataQuery = new DataQuery(
      "SELECT line_item_product_code, line_item_usage_type, SUM(line_item_unblended_cost) as cost FROM COST_AND_USAGE_REPORT WHERE line_item_usage_start_date >= '2024-01-01' GROUP BY line_item_product_code, line_item_usage_type HAVING SUM(line_item_unblended_cost) > 100",
      Map.of("COST_AND_USAGE_REPORT", Map.of("INCLUDE_MANUAL_DISCOUNT_COMPATIBILITY", "TRUE")));

    var dataExportConf = new DataExportConf(
      "complex-query-export",
      "Complex analytics query export",
      dataQuery,
      destinationConfig,
      "weekly",
      s3Conf,
      Map.of("QueryType", "complex", "Analytics", "enabled"));

    var construct = new BcmDataExportNestedStack(
      ctx.scope(),
      ctx.common(),
      dataExportConf,
      NestedStackProps.builder().build());

    assertNotNull(construct);
    assertNotNull(construct.export());
  }

  @Test
  void testBcmDataExportAthenaFormat() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "bcm-athena-" + System.currentTimeMillis(),
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      true,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of());

    var outputConfig = new OutputConfiguration(
      "gzip",
      "parquet",
      "athena",
      "create_new_report");

    var destinationConfig = new DestinationConfigurations(
      "us-east-1",
      s3Conf.name(),
      "athena-queries/",
      outputConfig);

    var dataQuery = new DataQuery(
      "SELECT * FROM COST_AND_USAGE_REPORT",
      Map.of());

    var dataExportConf = new DataExportConf(
      "athena-export",
      "Athena-optimized export",
      dataQuery,
      destinationConfig,
      "daily",
      s3Conf,
      Map.of("OutputFormat", "athena"));

    var construct = new BcmDataExportNestedStack(
      ctx.scope(),
      ctx.common(),
      dataExportConf,
      NestedStackProps.builder().build());

    assertNotNull(construct);
  }

  @Test
  void testBcmDataExportMinimalTags() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "bcm-minimal-" + System.currentTimeMillis(),
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      true,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of());

    var outputConfig = new OutputConfiguration(
      "gzip",
      "parquet",
      "custom",
      "overwrite_report");

    var destinationConfig = new DestinationConfigurations(
      "us-east-1",
      s3Conf.name(),
      "minimal/",
      outputConfig);

    var dataQuery = new DataQuery(
      "SELECT * FROM COST_AND_USAGE_REPORT",
      Map.of());

    var dataExportConf = new DataExportConf(
      "minimal-export",
      "Minimal configuration export",
      dataQuery,
      destinationConfig,
      "daily",
      s3Conf,
      Map.of());

    var construct = new BcmDataExportNestedStack(
      ctx.scope(),
      ctx.common(),
      dataExportConf,
      NestedStackProps.builder().build());

    assertNotNull(construct);
  }
}
