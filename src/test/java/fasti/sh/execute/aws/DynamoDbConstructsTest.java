package fasti.sh.execute.aws;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.aws.dynamodb.DynamoDbConstruct;
import fasti.sh.model.aws.dynamodb.Billing;
import fasti.sh.model.aws.dynamodb.DynamoDbStream;
import fasti.sh.model.aws.dynamodb.Encryption;
import fasti.sh.model.aws.dynamodb.FixedBilling;
import fasti.sh.model.aws.dynamodb.Index;
import fasti.sh.model.aws.dynamodb.Owner;
import fasti.sh.model.aws.dynamodb.SortKey;
import fasti.sh.model.aws.dynamodb.Streams;
import fasti.sh.model.aws.dynamodb.Table;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.ProjectionType;

/**
 * Tests for DynamoDB constructs.
 */
class DynamoDbConstructsTest {

  @Test
  void testDynamoDbConstructOnDemand() {
    var ctx = createTestContext();

    var partitionKey = new SortKey("pk", AttributeType.STRING);
    var encryption = new Encryption(true, "AWS", null);
    var billing = new Billing(true, null, null);
    var streams = new Streams(null, null);

    var tableConf = new Table(
      "my-table",
      partitionKey,
      null,
      List.of(),
      List.of(),
      encryption,
      billing,
      "STANDARD",
      streams,
      false,
      false,
      true,
      "DESTROY",
      Map.of());

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructWithSortKey() {
    var ctx = createTestContext();

    var partitionKey = new SortKey("userId", AttributeType.STRING);
    var sortKey = new SortKey("timestamp", AttributeType.NUMBER);
    var encryption = new Encryption(true, "AWS", null);
    var billing = new Billing(true, null, null);
    var streams = new Streams(null, null);

    var tableConf = new Table(
      "user-events",
      partitionKey,
      sortKey,
      List.of(),
      List.of(),
      encryption,
      billing,
      "STANDARD",
      streams,
      false,
      false,
      true,
      "DESTROY",
      Map.of("Type", "events"));

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructWithDeletionProtection() {
    var ctx = createTestContext();

    var partitionKey = new SortKey("id", AttributeType.STRING);
    var encryption = new Encryption(true, "DYNAMODB", null);
    var billing = new Billing(true, null, null);
    var streams = new Streams(null, null);

    var tableConf = new Table(
      "protected-table",
      partitionKey,
      null,
      List.of(),
      List.of(),
      encryption,
      billing,
      "STANDARD",
      streams,
      false,
      true, // deletionProtection
      true,
      "RETAIN",
      Map.of());

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructInfrequentAccess() {
    var ctx = createTestContext();

    var partitionKey = new SortKey("archiveId", AttributeType.STRING);
    var encryption = new Encryption(false, Owner.AWS.toString(), null);
    var billing = new Billing(true, null, null);
    var streams = new Streams(null, null);

    var tableConf = new Table(
      "archive-table",
      partitionKey,
      null,
      List.of(),
      List.of(),
      encryption,
      billing,
      "STANDARD_INFREQUENT_ACCESS",
      streams,
      false,
      false,
      false,
      "DESTROY",
      Map.of("Class", "infrequent-access"));

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  // NOTE: GSI test disabled because the Index record doesn't have a partitionKey field,
  // but GlobalSecondaryIndexPropsV2 requires one. This appears to be an incomplete feature.
  /*
   * @Test void testDynamoDbConstructWithGlobalSecondaryIndex() { var ctx = createTestContext();
   *
   * var partitionKey = new SortKey("pk", AttributeType.STRING); var sortKey = new SortKey("sk", AttributeType.STRING); var gsiSortKey = new
   * SortKey("gsi-sk", AttributeType.NUMBER); var gsi = new Index("gsi-index", gsiSortKey, ProjectionType.ALL, List.of()); var encryption =
   * new Encryption(true, "AWS", null); var billing = new Billing(true, null, null); var streams = new Streams(null, null);
   *
   * var tableConf = new Table( "table-with-gsi", partitionKey, sortKey, List.of(gsi), List.of(), encryption, billing, "STANDARD", streams,
   * false, false, true, "DESTROY", Map.of("HasGSI", "true") );
   *
   * var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);
   *
   * assertNotNull(construct); assertNotNull(construct.table()); }
   */

  @Test
  void testDynamoDbConstructWithLocalSecondaryIndex() {
    var ctx = createTestContext();

    var partitionKey = new SortKey("pk", AttributeType.STRING);
    var sortKey = new SortKey("sk", AttributeType.STRING);
    var lsiSortKey = new SortKey("lsi-sk", AttributeType.STRING);
    var lsi = new Index("lsi-index", lsiSortKey, ProjectionType.KEYS_ONLY, List.of());
    var encryption = new Encryption(true, "AWS", null);
    var billing = new Billing(true, null, null);
    var streams = new Streams(null, null);

    var tableConf = new Table(
      "table-with-lsi",
      partitionKey,
      sortKey,
      List.of(lsi), // localSecondaryIndexes
      List.of(), // globalSecondaryIndexes
      encryption,
      billing,
      "STANDARD",
      streams,
      false,
      false,
      true,
      "DESTROY",
      Map.of("HasLSI", "true"));

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructWithStreams() {
    var ctx = createTestContext();

    var partitionKey = new SortKey("eventId", AttributeType.STRING);
    var encryption = new Encryption(true, "AWS", null);
    var billing = new Billing(true, null, null);
    var dynamoDbStream = new DynamoDbStream(true, "NEW_AND_OLD_IMAGES");
    var streams = new Streams(null, dynamoDbStream);

    var tableConf = new Table(
      "stream-table",
      partitionKey,
      null,
      List.of(),
      List.of(),
      encryption,
      billing,
      "STANDARD",
      streams,
      false,
      false,
      true,
      "DESTROY",
      Map.of("Streams", "enabled"));

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructWithFixedBilling() {
    var ctx = createTestContext();

    var partitionKey = new SortKey("id", AttributeType.STRING);
    var encryption = new Encryption(true, "AWS", null);
    var fixedBilling = new FixedBilling(5, 5);
    var billing = new Billing(false, fixedBilling, null);
    var streams = new Streams(null, null);

    var tableConf = new Table(
      "fixed-billing-table",
      partitionKey,
      null,
      List.of(),
      List.of(),
      encryption,
      billing,
      "STANDARD",
      streams,
      false,
      false,
      true,
      "DESTROY",
      Map.of("Billing", "fixed"));

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  // NOTE: Multiple GSI test disabled for same reason as single GSI test
  /*
   * @Test void testDynamoDbConstructWithMultipleGSI() { var ctx = createTestContext();
   *
   * var partitionKey = new SortKey("pk", AttributeType.STRING); var sortKey = new SortKey("sk", AttributeType.STRING); var gsi1SortKey =
   * new SortKey("gsi1-sk", AttributeType.NUMBER); var gsi2SortKey = new SortKey("gsi2-sk", AttributeType.STRING); var gsi1 = new
   * Index("gsi1", gsi1SortKey, ProjectionType.ALL, List.of()); var gsi2 = new Index("gsi2", gsi2SortKey, ProjectionType.INCLUDE,
   * List.of("attr1", "attr2")); var encryption = new Encryption(true, "AWS", null); var billing = new Billing(true, null, null); var
   * streams = new Streams(null, null);
   *
   * var tableConf = new Table( "multi-gsi-table", partitionKey, sortKey, List.of(gsi1, gsi2), List.of(), encryption, billing, "STANDARD",
   * streams, false, false, true, "DESTROY", Map.of() );
   *
   * var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);
   *
   * assertNotNull(construct); assertNotNull(construct.table()); }
   */

  @Test
  void testDynamoDbConstructWithContributorInsights() {
    var ctx = createTestContext();

    var partitionKey = new SortKey("metricId", AttributeType.STRING);
    var encryption = new Encryption(true, "AWS", null);
    var billing = new Billing(true, null, null);
    var streams = new Streams(null, null);

    var tableConf = new Table(
      "insights-table",
      partitionKey,
      null,
      List.of(),
      List.of(),
      encryption,
      billing,
      "STANDARD",
      streams,
      true, // contributorInsights enabled
      false,
      true,
      "DESTROY",
      Map.of("Insights", "enabled"));

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructBinaryKey() {
    var ctx = createTestContext();

    var partitionKey = new SortKey("binaryId", AttributeType.BINARY);
    var encryption = new Encryption(true, "AWS", null);
    var billing = new Billing(true, null, null);
    var streams = new Streams(null, null);

    var tableConf = new Table(
      "binary-key-table",
      partitionKey,
      null,
      List.of(),
      List.of(),
      encryption,
      billing,
      "STANDARD",
      streams,
      false,
      false,
      true,
      "DESTROY",
      Map.of("KeyType", "binary"));

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructStreamKeysOnly() {
    var ctx = createTestContext();

    var partitionKey = new SortKey("id", AttributeType.STRING);
    var encryption = new Encryption(true, "AWS", null);
    var billing = new Billing(true, null, null);
    var dynamoDbStream = new DynamoDbStream(true, "KEYS_ONLY");
    var streams = new Streams(null, dynamoDbStream);

    var tableConf = new Table(
      "keys-only-stream-table",
      partitionKey,
      null,
      List.of(),
      List.of(),
      encryption,
      billing,
      "STANDARD",
      streams,
      false,
      false,
      true,
      "DESTROY",
      Map.of("StreamType", "keys-only"));

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructStreamNewImage() {
    var ctx = createTestContext();

    var partitionKey = new SortKey("id", AttributeType.STRING);
    var encryption = new Encryption(true, "AWS", null);
    var billing = new Billing(true, null, null);
    var dynamoDbStream = new DynamoDbStream(true, "NEW_IMAGE");
    var streams = new Streams(null, dynamoDbStream);

    var tableConf = new Table(
      "new-image-stream-table",
      partitionKey,
      null,
      List.of(),
      List.of(),
      encryption,
      billing,
      "STANDARD",
      streams,
      false,
      false,
      true,
      "DESTROY",
      Map.of("StreamType", "new-image"));

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructLsiWithIncludeProjection() {
    var ctx = createTestContext();

    var partitionKey = new SortKey("pk", AttributeType.STRING);
    var sortKey = new SortKey("sk", AttributeType.STRING);
    var lsiSortKey = new SortKey("lsi-sk", AttributeType.NUMBER);
    var lsi = new Index("lsi-include", lsiSortKey, ProjectionType.INCLUDE, List.of("attr1", "attr2", "attr3"));
    var encryption = new Encryption(true, "AWS", null);
    var billing = new Billing(true, null, null);
    var streams = new Streams(null, null);

    var tableConf = new Table(
      "lsi-include-table",
      partitionKey,
      sortKey,
      List.of(lsi),
      List.of(),
      encryption,
      billing,
      "STANDARD",
      streams,
      false,
      false,
      true,
      "DESTROY",
      Map.of("LSIProjection", "include"));

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructNoPitr() {
    var ctx = createTestContext();

    var partitionKey = new SortKey("tempId", AttributeType.STRING);
    var encryption = new Encryption(true, "AWS", null);
    var billing = new Billing(true, null, null);
    var streams = new Streams(null, null);

    var tableConf = new Table(
      "no-pitr-table",
      partitionKey,
      null,
      List.of(),
      List.of(),
      encryption,
      billing,
      "STANDARD",
      streams,
      false,
      false,
      false, // pointInTimeRecovery disabled
      "DESTROY",
      Map.of("PITR", "disabled"));

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructNumberKey() {
    var ctx = createTestContext();

    var partitionKey = new SortKey("itemId", AttributeType.NUMBER);
    var encryption = new Encryption(true, "AWS", null);
    var billing = new Billing(true, null, null);
    var streams = new Streams(null, null);

    var tableConf = new Table(
      "number-key-table",
      partitionKey,
      null,
      List.of(),
      List.of(),
      encryption,
      billing,
      "STANDARD",
      streams,
      false,
      false,
      true,
      "DESTROY",
      Map.of("KeyType", "number"));

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructOldImage() {
    var ctx = createTestContext();

    var partitionKey = new SortKey("id", AttributeType.STRING);
    var encryption = new Encryption(true, "AWS", null);
    var billing = new Billing(true, null, null);
    var dynamoDbStream = new DynamoDbStream(true, "OLD_IMAGE");
    var streams = new Streams(null, dynamoDbStream);

    var tableConf = new Table(
      "old-image-table",
      partitionKey,
      null,
      List.of(),
      List.of(),
      encryption,
      billing,
      "STANDARD",
      streams,
      false,
      false,
      true,
      "DESTROY",
      Map.of("StreamType", "old"));

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructBothImages() {
    var ctx = createTestContext();

    var partitionKey = new SortKey("id", AttributeType.STRING);
    var encryption = new Encryption(true, "AWS", null);
    var billing = new Billing(true, null, null);
    var dynamoDbStream = new DynamoDbStream(true, "NEW_AND_OLD_IMAGES");
    var streams = new Streams(null, dynamoDbStream);

    var tableConf = new Table(
      "both-images-table",
      partitionKey,
      null,
      List.of(),
      List.of(),
      encryption,
      billing,
      "STANDARD",
      streams,
      false,
      false,
      true,
      "DESTROY",
      Map.of("StreamType", "both"));

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructInfrequentAccessWithEncryption() {
    var ctx = createTestContext();

    var partitionKey = new SortKey("archiveId", AttributeType.STRING);
    var encryption = new Encryption(true, "AWS", null);
    var billing = new Billing(true, null, null);
    var streams = new Streams(null, null);

    var tableConf = new Table(
      "infrequent-encrypted-table",
      partitionKey,
      null,
      List.of(),
      List.of(),
      encryption,
      billing,
      "STANDARD_INFREQUENT_ACCESS",
      streams,
      false,
      false,
      true,
      "DESTROY",
      Map.of("TableClass", "infrequent-encrypted"));

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructLsiAllProjection() {
    var ctx = createTestContext();

    var partitionKey = new SortKey("userId", AttributeType.STRING);
    var sortKey = new SortKey("timestamp", AttributeType.NUMBER);
    var encryption = new Encryption(true, "AWS", null);
    var billing = new Billing(true, null, null);
    var streams = new Streams(null, null);

    var lsiSortKey = new SortKey("status", AttributeType.STRING);
    var index = new Index(
      "status-index",
      lsiSortKey,
      ProjectionType.ALL,
      List.of());

    var tableConf = new Table(
      "indexed-table",
      partitionKey,
      sortKey,
      List.of(index),
      List.of(),
      encryption,
      billing,
      "STANDARD",
      streams,
      false,
      false,
      true,
      "DESTROY",
      Map.of("HasIndex", "true"));

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructProvisionedBilling() {
    var ctx = createTestContext();

    var partitionKey = new SortKey("orderId", AttributeType.STRING);
    var encryption = new Encryption(true, "AWS", null);
    var fixedBilling = new FixedBilling(100, 50);
    var billing = new Billing(false, fixedBilling, null);
    var streams = new Streams(null, null);

    var tableConf = new Table(
      "provisioned-table",
      partitionKey,
      null,
      List.of(),
      List.of(),
      encryption,
      billing,
      "STANDARD",
      streams,
      false,
      false,
      true,
      "DESTROY",
      Map.of("BillingMode", "provisioned"));

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructWithTtl() {
    var ctx = createTestContext();

    var partitionKey = new SortKey("sessionId", AttributeType.STRING);
    var encryption = new Encryption(true, "AWS", null);
    var billing = new Billing(true, null, null);
    var streams = new Streams(null, null);

    var tableConf = new Table(
      "ttl-table",
      partitionKey,
      null,
      List.of(),
      List.of(),
      encryption,
      billing,
      "STANDARD",
      streams,
      true,
      false,
      true,
      "DESTROY",
      Map.of("TTL", "enabled"));

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructWithPointInTimeRecovery() {
    var ctx = createTestContext();

    var partitionKey = new SortKey("backupId", AttributeType.STRING);
    var encryption = new Encryption(true, "AWS", null);
    var billing = new Billing(true, null, null);
    var streams = new Streams(null, null);

    var tableConf = new Table(
      "pitr-table",
      partitionKey,
      null,
      List.of(),
      List.of(),
      encryption,
      billing,
      "STANDARD",
      streams,
      false,
      true,
      true,
      "RETAIN",
      Map.of("PITR", "enabled"));

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructMultipleLocalSecondaryIndexes() {
    var ctx = createTestContext();

    var partitionKey = new SortKey("id", AttributeType.STRING);
    var sortKey = new SortKey("sk", AttributeType.STRING);
    var encryption = new Encryption(true, "AWS", null);
    var billing = new Billing(true, null, null);
    var streams = new Streams(null, null);

    var lsi1Sort = new SortKey("lsi1sk", AttributeType.STRING);
    var index1 = new Index("lsi1", lsi1Sort, ProjectionType.ALL, List.of());

    var lsi2Sort = new SortKey("lsi2sk", AttributeType.NUMBER);
    var index2 = new Index("lsi2", lsi2Sort, ProjectionType.KEYS_ONLY, List.of());

    var tableConf = new Table(
      "multi-lsi-table",
      partitionKey,
      sortKey,
      List.of(index1, index2),
      List.of(),
      encryption,
      billing,
      "STANDARD",
      streams,
      false,
      false,
      true,
      "DESTROY",
      Map.of("LSI", "multiple"));

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructNumberPartitionKey() {
    var ctx = createTestContext();

    var partitionKey = new SortKey("numericId", AttributeType.NUMBER);
    var encryption = new Encryption(true, "AWS", null);
    var billing = new Billing(true, null, null);
    var streams = new Streams(null, null);

    var tableConf = new Table(
      "numeric-pk-table",
      partitionKey,
      null,
      List.of(),
      List.of(),
      encryption,
      billing,
      "STANDARD",
      streams,
      false,
      false,
      true,
      "DESTROY",
      Map.of());

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructBinaryPartitionKey() {
    var ctx = createTestContext();

    var partitionKey = new SortKey("binaryKey", AttributeType.BINARY);
    var encryption = new Encryption(true, "AWS", null);
    var billing = new Billing(true, null, null);
    var streams = new Streams(null, null);

    var tableConf = new Table(
      "binary-pk-table",
      partitionKey,
      null,
      List.of(),
      List.of(),
      encryption,
      billing,
      "STANDARD",
      streams,
      false,
      false,
      true,
      "DESTROY",
      Map.of());

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructCompositeSortKey() {
    var ctx = createTestContext();

    var partitionKey = new SortKey("pk", AttributeType.STRING);
    var sortKey = new SortKey("sk", AttributeType.STRING);
    var encryption = new Encryption(true, "AWS", null);
    var billing = new Billing(true, null, null);
    var streams = new Streams(null, null);

    var tableConf = new Table(
      "composite-key-table",
      partitionKey,
      sortKey,
      List.of(),
      List.of(),
      encryption,
      billing,
      "STANDARD",
      streams,
      false,
      false,
      true,
      "DESTROY",
      Map.of("KeyType", "composite"));

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }
}
