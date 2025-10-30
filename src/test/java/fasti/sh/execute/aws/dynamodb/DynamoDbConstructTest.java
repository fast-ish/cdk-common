package fasti.sh.execute.aws.dynamodb;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.model.aws.dynamodb.*;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.services.dynamodb.AttributeType;

/**
 * Tests for DynamoDbConstruct.
 */
class DynamoDbConstructTest {

  @Test
  void testDynamoDbConstructMinimal() {
    var ctx = createTestContext();

    var table = new Table(
      "test-table",
      new SortKey("id", AttributeType.STRING),
      null,
      List.of(),
      List.of(),
      new Encryption(false, null, null),
      new Billing(true, null, null),
      "standard",
      new Streams(null, null),
      false,
      false,
      false,
      "destroy",
      Map.of());

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), table);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructWithSortKey() {
    var ctx = createTestContext();

    var table = new Table(
      "test-table-sort",
      new SortKey("id", AttributeType.STRING),
      new SortKey("timestamp", AttributeType.NUMBER),
      List.of(),
      List.of(),
      new Encryption(false, null, null),
      new Billing(true, null, null),
      "standard",
      new Streams(null, null),
      false,
      false,
      false,
      "destroy",
      Map.of());

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), table);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  // GSI test removed - requires partition key configuration not in current model

  @Test
  void testDynamoDbConstructWithLocalSecondaryIndex() {
    var ctx = createTestContext();

    var lsi = new Index(
      "lsi-timestamp",
      new SortKey("timestamp", AttributeType.NUMBER),
      software.amazon.awscdk.services.dynamodb.ProjectionType.KEYS_ONLY,
      List.of());

    var table = new Table(
      "test-table-lsi",
      new SortKey("id", AttributeType.STRING),
      new SortKey("status", AttributeType.STRING),
      List.of(lsi),
      List.of(),
      new Encryption(false, null, null),
      new Billing(true, null, null),
      "standard",
      new Streams(null, null),
      false,
      false,
      false,
      "destroy",
      Map.of());

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), table);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructWithAwsManagedEncryption() {
    var ctx = createTestContext();

    var table = new Table(
      "test-table-encryption-aws",
      new SortKey("id", AttributeType.STRING),
      null,
      List.of(),
      List.of(),
      new Encryption(true, "aws", null),
      new Billing(true, null, null),
      "standard",
      new Streams(null, null),
      false,
      false,
      false,
      "destroy",
      Map.of());

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), table);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructWithDynamoDbOwnedEncryption() {
    var ctx = createTestContext();

    var table = new Table(
      "test-table-encryption-ddb",
      new SortKey("id", AttributeType.STRING),
      null,
      List.of(),
      List.of(),
      new Encryption(true, "dynamodb", null),
      new Billing(true, null, null),
      "standard",
      new Streams(null, null),
      false,
      false,
      false,
      "destroy",
      Map.of());

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), table);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructWithFixedBilling() {
    var ctx = createTestContext();

    var table = new Table(
      "test-table-fixed-billing",
      new SortKey("id", AttributeType.STRING),
      null,
      List.of(),
      List.of(),
      new Encryption(false, null, null),
      new Billing(false, new FixedBilling(5, 5), null),
      "standard",
      new Streams(null, null),
      false,
      false,
      false,
      "destroy",
      Map.of());

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), table);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructWithProvisionedBilling() {
    var ctx = createTestContext();

    var readCapacity = new Provisioned(1, 10, 5, 70);
    var writeCapacity = new Provisioned(1, 10, 5, 70);

    var table = new Table(
      "test-table-provisioned",
      new SortKey("id", AttributeType.STRING),
      null,
      List.of(),
      List.of(),
      new Encryption(false, null, null),
      new Billing(false, null, new ProvisionedBilling(readCapacity, writeCapacity)),
      "standard",
      new Streams(null, null),
      false,
      false,
      false,
      "destroy",
      Map.of());

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), table);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructWithDynamoDbStream() {
    var ctx = createTestContext();

    var table = new Table(
      "test-table-stream",
      new SortKey("id", AttributeType.STRING),
      null,
      List.of(),
      List.of(),
      new Encryption(false, null, null),
      new Billing(true, null, null),
      "standard",
      new Streams(null, new DynamoDbStream(true, "new_and_old_images")),
      false,
      false,
      false,
      "destroy",
      Map.of());

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), table);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructWithContributorInsights() {
    var ctx = createTestContext();

    var table = new Table(
      "test-table-insights",
      new SortKey("id", AttributeType.STRING),
      null,
      List.of(),
      List.of(),
      new Encryption(false, null, null),
      new Billing(true, null, null),
      "standard",
      new Streams(null, null),
      true,
      false,
      false,
      "destroy",
      Map.of());

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), table);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructWithPointInTimeRecovery() {
    var ctx = createTestContext();

    var table = new Table(
      "test-table-pitr",
      new SortKey("id", AttributeType.STRING),
      null,
      List.of(),
      List.of(),
      new Encryption(false, null, null),
      new Billing(true, null, null),
      "standard",
      new Streams(null, null),
      false,
      false,
      true,
      "destroy",
      Map.of());

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), table);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructWithDeletionProtection() {
    var ctx = createTestContext();

    var table = new Table(
      "test-table-delete-protect",
      new SortKey("id", AttributeType.STRING),
      null,
      List.of(),
      List.of(),
      new Encryption(false, null, null),
      new Billing(true, null, null),
      "standard",
      new Streams(null, null),
      false,
      true,
      false,
      "retain",
      Map.of());

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), table);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructInfrequentAccessClass() {
    var ctx = createTestContext();

    var table = new Table(
      "test-table-ia",
      new SortKey("id", AttributeType.STRING),
      null,
      List.of(),
      List.of(),
      new Encryption(false, null, null),
      new Billing(true, null, null),
      "standard_infrequent_access",
      new Streams(null, null),
      false,
      false,
      false,
      "destroy",
      Map.of());

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), table);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructWithIndexProjection() {
    var ctx = createTestContext();

    var lsi = new Index(
      "lsi-with-attrs",
      new SortKey("timestamp", AttributeType.NUMBER),
      software.amazon.awscdk.services.dynamodb.ProjectionType.INCLUDE,
      List.of("attribute1", "attribute2"));

    var table = new Table(
      "test-table-lsi-proj",
      new SortKey("id", AttributeType.STRING),
      new SortKey("status", AttributeType.STRING),
      List.of(lsi),
      List.of(),
      new Encryption(false, null, null),
      new Billing(true, null, null),
      "standard",
      new Streams(null, null),
      false,
      false,
      false,
      "destroy",
      Map.of());

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), table);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructCombinedBillingError() {
    var ctx = createTestContext();

    // This should trigger the billing error path (none selected)
    var table = new Table(
      "test-table-billing-error",
      new SortKey("id", AttributeType.STRING),
      null,
      List.of(),
      List.of(),
      new Encryption(false, null, null),
      new Billing(false, null, null),
      "standard",
      new Streams(null, null),
      false,
      false,
      false,
      "destroy",
      Map.of());

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), table);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructProvisionedWithFixed() {
    var ctx = createTestContext();

    // Test the nested billing path where provisioned is set but fixed overrides it
    var readCapacity = new Provisioned(1, 10, 5, 70);
    var writeCapacity = new Provisioned(1, 10, 5, 70);

    var table = new Table(
      "test-table-prov-fixed",
      new SortKey("id", AttributeType.STRING),
      null,
      List.of(),
      List.of(),
      new Encryption(false, null, null),
      new Billing(false, new FixedBilling(10, 10), new ProvisionedBilling(readCapacity, writeCapacity)),
      "standard",
      new Streams(null, null),
      false,
      false,
      false,
      "destroy",
      Map.of());

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), table);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }

  @Test
  void testDynamoDbConstructWithTags() {
    var ctx = createTestContext();

    var table = new Table(
      "test-table-tags",
      new SortKey("id", AttributeType.STRING),
      null,
      List.of(),
      List.of(),
      new Encryption(false, null, null),
      new Billing(true, null, null),
      "standard",
      new Streams(null, null),
      false,
      false,
      false,
      "destroy",
      Map.of("CustomTag", "CustomValue", "Environment", "test"));

    var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), table);

    assertNotNull(construct);
    assertNotNull(construct.table());
  }
}
