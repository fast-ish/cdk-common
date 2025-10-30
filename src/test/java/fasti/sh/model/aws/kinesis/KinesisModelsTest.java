package fasti.sh.model.aws.kinesis;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.model.aws.kms.Kms;
import org.junit.jupiter.api.Test;

/**
 * Tests for Kinesis model records.
 */
class KinesisModelsTest {

  @Test
  void testKinesisStreamBasic() {
    var stream = new KinesisStream(
      true,
      "my-stream",
      2,
      "PROVISIONED",
      "NONE",
      null,
      "DESTROY",
      24);

    assertTrue(stream.enabled());
    assertEquals("my-stream", stream.name());
    assertEquals(2, stream.shards());
    assertEquals("PROVISIONED", stream.mode());
    assertEquals("NONE", stream.encryption());
    assertNull(stream.kms());
    assertEquals("DESTROY", stream.removalPolicy());
    assertEquals(24, stream.retentionPeriod());
    assertRecordToString(stream);
  }

  @Test
  void testKinesisStreamOnDemand() {
    var stream = new KinesisStream(
      true,
      "on-demand-stream",
      0,
      "ON_DEMAND",
      "NONE",
      null,
      "RETAIN",
      168);

    assertEquals("ON_DEMAND", stream.mode());
    assertEquals(0, stream.shards());
    assertEquals(168, stream.retentionPeriod());
  }

  @Test
  void testKinesisStreamWithEncryption() {
    var kms = new Kms("my-key", "My KMS Key", true, true, "ENCRYPT_DECRYPT", "SYMMETRIC_DEFAULT", "DESTROY");
    var stream = new KinesisStream(
      true,
      "encrypted-stream",
      5,
      "PROVISIONED",
      "KMS",
      kms,
      "RETAIN",
      24);

    assertEquals("KMS", stream.encryption());
    assertNotNull(stream.kms());
    assertEquals("my-key", stream.kms().alias());
  }

  @Test
  void testKinesisStreamDisabled() {
    var stream = new KinesisStream(
      false,
      "disabled-stream",
      1,
      "PROVISIONED",
      "NONE",
      null,
      "DESTROY",
      24);

    assertFalse(stream.enabled());
  }

  @Test
  void testKinesisStreamHighShards() {
    var stream = new KinesisStream(
      true,
      "high-capacity-stream",
      50,
      "PROVISIONED",
      "NONE",
      null,
      "DESTROY",
      24);

    assertEquals(50, stream.shards());
  }

  @Test
  void testKinesisStreamLongRetention() {
    var stream = new KinesisStream(
      true,
      "long-retention-stream",
      2,
      "PROVISIONED",
      "NONE",
      null,
      "RETAIN",
      8760);

    assertEquals(8760, stream.retentionPeriod());
  }

  @Test
  void testKinesisStreamEquality() {
    var stream1 = new KinesisStream(
      true, "s1", 2, "PROVISIONED", "NONE", null, "DESTROY", 24);
    var stream2 = new KinesisStream(
      true, "s1", 2, "PROVISIONED", "NONE", null, "DESTROY", 24);

    assertRecordEquality(stream1, stream2);
  }
}
