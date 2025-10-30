package fasti.sh.model.aws.dynamodb;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.services.dynamodb.AttributeType;

/**
 * Tests for DynamoDB model records.
 */
class DynamoDbModelsTest {

  @Test
  void testSortKeyString() {
    var sortKey = new SortKey("userId", AttributeType.STRING);

    assertEquals("userId", sortKey.name());
    assertEquals(AttributeType.STRING, sortKey.type());
    assertRecordToString(sortKey);
  }

  @Test
  void testSortKeyNumber() {
    var sortKey = new SortKey("timestamp", AttributeType.NUMBER);

    assertEquals("timestamp", sortKey.name());
    assertEquals(AttributeType.NUMBER, sortKey.type());
  }

  @Test
  void testSortKeyBinary() {
    var sortKey = new SortKey("data", AttributeType.BINARY);

    assertEquals(AttributeType.BINARY, sortKey.type());
  }

  @Test
  void testSortKeyEquality() {
    var key1 = new SortKey("id", AttributeType.STRING);
    var key2 = new SortKey("id", AttributeType.STRING);

    assertRecordEquality(key1, key2);
  }

  @Test
  void testBillingOnDemand() {
    var billing = new Billing(true, null, null);

    assertTrue(billing.onDemand());
    assertNull(billing.fixed());
    assertNull(billing.provisioned());
  }

  @Test
  void testEncryptionAws() {
    var encryption = new Encryption(true, "AWS", null);

    assertTrue(encryption.enabled());
    assertEquals("AWS", encryption.owner());
    assertNull(encryption.kms());
  }

  @Test
  void testEncryptionDynamoDb() {
    var encryption = new Encryption(true, "DYNAMODB", null);

    assertTrue(encryption.enabled());
    assertEquals("DYNAMODB", encryption.owner());
  }

  @Test
  void testEncryptionDisabled() {
    var encryption = new Encryption(false, Owner.AWS.toString(), null);

    assertFalse(encryption.enabled());
  }

  @Test
  void testTableBasic() {
    var partitionKey = new SortKey("pk", AttributeType.STRING);
    var encryption = new Encryption(true, "AWS", null);
    var billing = new Billing(true, null, null);
    var streams = new Streams(null, null);

    var table = new Table(
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

    assertEquals("my-table", table.name());
    assertEquals("STANDARD", table.tableClass());
    assertFalse(table.deletionProtection());
    assertTrue(table.pointInTimeRecovery());
  }

  @Test
  void testTableWithSortKey() {
    var partitionKey = new SortKey("pk", AttributeType.STRING);
    var sortKey = new SortKey("sk", AttributeType.NUMBER);
    var encryption = new Encryption(false, "AWS", null);
    var billing = new Billing(true, null, null);
    var streams = new Streams(null, null);

    var table = new Table(
      "composite-key-table",
      partitionKey,
      sortKey,
      List.of(),
      List.of(),
      encryption,
      billing,
      "STANDARD_INFREQUENT_ACCESS",
      streams,
      true,
      true,
      true,
      "RETAIN",
      Map.of("Type", "composite"));

    assertNotNull(table.sortKey());
    assertEquals("sk", table.sortKey().name());
    assertTrue(table.contributorInsights());
    assertTrue(table.deletionProtection());
  }
}
