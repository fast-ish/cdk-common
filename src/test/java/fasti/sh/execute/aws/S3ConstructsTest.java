package fasti.sh.execute.aws;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.aws.s3.BucketConstruct;
import fasti.sh.model.aws.iam.Principal;
import fasti.sh.model.aws.iam.PrincipalType;
import fasti.sh.model.aws.kms.Kms;
import fasti.sh.model.aws.s3.BucketLifecycleRule;
import fasti.sh.model.aws.s3.S3Bucket;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.s3.BucketAccessControl;
import software.amazon.awscdk.services.s3.ObjectOwnership;

/**
 * Tests for S3 bucket constructs.
 */
class S3ConstructsTest {

  @Test
  void testBucketConstructBasic() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("s3.amazonaws.com")
      .conditions(Map.of())
      .build();

    var bucketConf = new S3Bucket(
      "test-bucket",
      principal,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      false,
      false,
      RemovalPolicy.RETAIN,
      null,
      Map.of());

    var construct = new BucketConstruct(ctx.scope(), ctx.common(), bucketConf);

    assertNotNull(construct);
    assertNotNull(construct.bucket());
  }

  @Test
  void testBucketConstructWithVersioning() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("s3.amazonaws.com")
      .conditions(Map.of())
      .build();

    var bucketConf = new S3Bucket(
      "versioned-bucket",
      principal,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      false,
      true,
      RemovalPolicy.RETAIN,
      null,
      Map.of("Versioned", "true"));

    var construct = new BucketConstruct(ctx.scope(), ctx.common(), bucketConf);

    assertNotNull(construct);
    assertNotNull(construct.bucket());
  }

  @Test
  void testBucketConstructWithLifecycleRules() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("s3.amazonaws.com")
      .conditions(Map.of())
      .build();

    var lifecycleRule1 = new BucketLifecycleRule(true, 90, "delete-old-objects");
    var lifecycleRule2 = new BucketLifecycleRule(true, 365, "archive-logs");

    var bucketConf = new S3Bucket(
      "lifecycle-bucket",
      principal,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(lifecycleRule1, lifecycleRule2),
      List.of(),
      false,
      false,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of());

    var construct = new BucketConstruct(ctx.scope(), ctx.common(), bucketConf);

    assertNotNull(construct);
    assertNotNull(construct.bucket());
  }

  @Test
  void testBucketConstructWithKmsEncryption() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("s3.amazonaws.com")
      .conditions(Map.of())
      .build();

    var kms = new Kms(
      "s3-bucket-key",
      "S3 bucket encryption key",
      true,
      true,
      "ENCRYPT_DECRYPT",
      "SYMMETRIC_DEFAULT",
      "RETAIN");

    var bucketConf = new S3Bucket(
      "encrypted-bucket",
      principal,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      false,
      true,
      RemovalPolicy.RETAIN,
      kms,
      Map.of("Encrypted", "true"));

    var construct = new BucketConstruct(ctx.scope(), ctx.common(), bucketConf);

    assertNotNull(construct);
    assertNotNull(construct.bucket());
  }

  @Test
  void testBucketConstructWithEventBridge() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("s3.amazonaws.com")
      .conditions(Map.of())
      .build();

    var bucketConf = new S3Bucket(
      "eventbridge-bucket",
      principal,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      true,
      false,
      false,
      RemovalPolicy.RETAIN,
      null,
      Map.of("EventBridge", "enabled"));

    var construct = new BucketConstruct(ctx.scope(), ctx.common(), bucketConf);

    assertNotNull(construct);
    assertNotNull(construct.bucket());
  }

  @Test
  void testBucketConstructWithAutoDelete() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("s3.amazonaws.com")
      .conditions(Map.of())
      .build();

    var bucketConf = new S3Bucket(
      "autodelete-bucket",
      principal,
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

    var construct = new BucketConstruct(ctx.scope(), ctx.common(), bucketConf);

    assertNotNull(construct);
    assertNotNull(construct.bucket());
  }

  @Test
  void testBucketConstructPublicRead() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("s3.amazonaws.com")
      .conditions(Map.of())
      .build();

    var bucketConf = new S3Bucket(
      "public-bucket",
      principal,
      BucketAccessControl.PUBLIC_READ,
      ObjectOwnership.OBJECT_WRITER,
      List.of(),
      List.of(),
      false,
      false,
      false,
      RemovalPolicy.RETAIN,
      null,
      Map.of("Public", "true"));

    var construct = new BucketConstruct(ctx.scope(), ctx.common(), bucketConf);

    assertNotNull(construct);
    assertNotNull(construct.bucket());
  }

  @Test
  void testBucketConstructWithMultipleLifecycleRules() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("s3.amazonaws.com")
      .conditions(Map.of())
      .build();

    var rule1 = new BucketLifecycleRule(true, 30, "delete-30-days");
    var rule2 = new BucketLifecycleRule(true, 60, "delete-60-days");
    var rule3 = new BucketLifecycleRule(true, 90, "delete-90-days");

    var bucketConf = new S3Bucket(
      "multi-lifecycle-bucket",
      principal,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(rule1, rule2, rule3),
      List.of(),
      false,
      false,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of());

    var construct = new BucketConstruct(ctx.scope(), ctx.common(), bucketConf);

    assertNotNull(construct);
    assertNotNull(construct.bucket());
  }

  @Test
  void testBucketConstructWithTags() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("s3.amazonaws.com")
      .conditions(Map.of())
      .build();

    var bucketConf = new S3Bucket(
      "tagged-bucket",
      principal,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      false,
      false,
      RemovalPolicy.RETAIN,
      null,
      Map
        .of(
          "Environment",
          "production",
          "Team",
          "data",
          "CostCenter",
          "engineering"));

    var construct = new BucketConstruct(ctx.scope(), ctx.common(), bucketConf);

    assertNotNull(construct);
    assertNotNull(construct.bucket());
  }

  @Test
  void testBucketConstructFullFeatures() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("s3.amazonaws.com")
      .conditions(Map.of())
      .build();

    var kms = new Kms(
      "full-bucket-key",
      "Full featured bucket encryption",
      true,
      true,
      "ENCRYPT_DECRYPT",
      "SYMMETRIC_DEFAULT",
      "RETAIN");

    var lifecycleRule = new BucketLifecycleRule(true, 180, "cleanup");

    var bucketConf = new S3Bucket(
      "full-featured-bucket",
      principal,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(lifecycleRule),
      List.of(),
      true,
      false,
      true,
      RemovalPolicy.RETAIN,
      kms,
      Map
        .of(
          "Environment",
          "production",
          "Versioned",
          "true",
          "Encrypted",
          "true",
          "EventBridge",
          "enabled"));

    var construct = new BucketConstruct(ctx.scope(), ctx.common(), bucketConf);

    assertNotNull(construct);
    assertNotNull(construct.bucket());
  }
}
