package fasti.sh.model.aws.s3;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.model.aws.kms.Kms;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.s3.BucketAccessControl;
import software.amazon.awscdk.services.s3.ObjectOwnership;

/**
 * Tests for S3 model records.
 */
class S3ModelsTest {

  @Test
  void testBucketLifecycleRuleBasic() {
    var rule = new BucketLifecycleRule(true, 30, "delete-old-files");

    assertTrue(rule.enabled());
    assertEquals(30, rule.expiration());
    assertEquals("delete-old-files", rule.id());
    assertRecordToString(rule);
  }

  @Test
  void testBucketLifecycleRuleDisabled() {
    var rule = new BucketLifecycleRule(false, 90, "archive-rule");

    assertFalse(rule.enabled());
    assertEquals(90, rule.expiration());
  }

  @Test
  void testBucketLifecycleRuleEquality() {
    var rule1 = new BucketLifecycleRule(true, 60, "rule1");
    var rule2 = new BucketLifecycleRule(true, 60, "rule1");

    assertRecordEquality(rule1, rule2);
  }

  @Test
  void testS3BucketBasic() {
    var bucket = new S3Bucket(
      "my-bucket",
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      false,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of());

    assertEquals("my-bucket", bucket.name());
    assertEquals(BucketAccessControl.PRIVATE, bucket.accessControl());
    assertFalse(bucket.versioned());
    assertFalse(bucket.eventBridgeEnabled());
  }

  @Test
  void testS3BucketWithVersioning() {
    var bucket = new S3Bucket(
      "versioned-bucket",
      null,
      BucketAccessControl.BUCKET_OWNER_FULL_CONTROL,
      ObjectOwnership.BUCKET_OWNER_PREFERRED,
      List.of(),
      List.of(),
      true,
      false,
      true,
      RemovalPolicy.RETAIN,
      null,
      Map.of("Versioned", "true"));

    assertTrue(bucket.versioned());
    assertEquals(RemovalPolicy.RETAIN, bucket.removalPolicy());
  }

  @Test
  void testS3BucketWithKmsEncryption() {
    var kms = new Kms("alias/s3-key", "S3 encryption key", true, true, "ENCRYPT_DECRYPT", "SYMMETRIC_DEFAULT", "RETAIN");
    var bucket = new S3Bucket(
      "encrypted-bucket",
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      true,
      false,
      RemovalPolicy.DESTROY,
      kms,
      Map.of());

    assertNotNull(bucket.kms());
    assertEquals("alias/s3-key", bucket.kms().alias());
    assertTrue(bucket.autoDeleteObjects());
  }

  @Test
  void testS3BucketWithLifecycleRules() {
    var rule1 = new BucketLifecycleRule(true, 30, "expire-logs");
    var rule2 = new BucketLifecycleRule(true, 90, "archive-data");

    var bucket = new S3Bucket(
      "lifecycle-bucket",
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(rule1, rule2),
      List.of(),
      false,
      true,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of());

    assertEquals(2, bucket.lifecycleRules().size());
    assertEquals("expire-logs", bucket.lifecycleRules().get(0).id());
  }
}
