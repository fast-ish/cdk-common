package fasti.sh.execute.aws;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.aws.s3.BucketConstruct;
import fasti.sh.execute.aws.secretsmanager.SecretConstruct;
import fasti.sh.execute.aws.sqs.SqsConstruct;
import fasti.sh.model.aws.kms.Kms;
import fasti.sh.model.aws.s3.BucketLifecycleRule;
import fasti.sh.model.aws.s3.S3Bucket;
import fasti.sh.model.aws.secretsmanager.SecretCredentials;
import fasti.sh.model.aws.secretsmanager.SecretFormat;
import fasti.sh.model.aws.sqs.Sqs;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.s3.BucketAccessControl;
import software.amazon.awscdk.services.s3.ObjectOwnership;

/**
 * Tests for additional AWS constructs including S3, SQS, Secrets Manager.
 */
class MoreConstructsTest {

  @Test
  void testSqsConstruct() {
    var ctx = createTestContext();

    var sqsConf = new Sqs(
      "test-queue",
      1209600, // 14 days in seconds
      List.of(),
      List.of(),
      Map.of());

    var construct = new SqsConstruct(ctx.scope(), ctx.common(), sqsConf);

    assertNotNull(construct);
    assertNotNull(construct.sqs());
  }

  @Test
  void testS3BucketConstruct() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "test-bucket-" + System.currentTimeMillis(),
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

    var construct = new BucketConstruct(ctx.scope(), ctx.common(), s3Conf);

    assertNotNull(construct);
    assertNotNull(construct.bucket());
  }

  @Test
  void testS3BucketConstructWithVersioning() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "versioned-bucket-" + System.currentTimeMillis(),
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

    var construct = new BucketConstruct(ctx.scope(), ctx.common(), s3Conf);

    assertNotNull(construct);
    assertNotNull(construct.bucket());
  }

  @Test
  void testSecretsManagerConstruct() {
    var ctx = createTestContext();

    var passwordFormat = new SecretFormat(
      false, // excludeLowercase
      false, // excludeNumbers
      false, // excludeUppercase
      false, // includeSpace
      32, // length
      true // requireEachIncludedType
    );

    var secretConf = new SecretCredentials(
      "test-secret-" + System.currentTimeMillis(),
      "Test database credentials",
      "admin",
      passwordFormat,
      "DESTROY",
      Map.of("Type", "database"));

    var construct = new SecretConstruct(ctx.scope(), ctx.common(), secretConf);

    assertNotNull(construct);
    assertNotNull(construct.secret());
  }

  @Test
  void testSecretsManagerConstructSimplePassword() {
    var ctx = createTestContext();

    var passwordFormat = new SecretFormat(
      false, // excludeLowercase
      true, // excludeNumbers - exclude numbers
      false, // excludeUppercase
      false, // includeSpace
      16, // shorter length
      false // don't require each type
    );

    var secretConf = new SecretCredentials(
      "simple-secret-" + System.currentTimeMillis(),
      "Simple secret for testing",
      "testuser",
      passwordFormat,
      "DESTROY",
      Map.of());

    var construct = new SecretConstruct(ctx.scope(), ctx.common(), secretConf);

    assertNotNull(construct);
    assertNotNull(construct.secret());
  }

  @Test
  void testSecretsManagerWithSpaces() {
    var ctx = createTestContext();

    var passwordFormat = new SecretFormat(
      false,
      false,
      false,
      true, // includeSpace enabled
      24,
      true);

    var secretConf = new SecretCredentials(
      "spaces-secret-" + System.currentTimeMillis(),
      "Secret with spaces in password",
      "spaceuser",
      passwordFormat,
      "DESTROY",
      Map.of("Spaces", "enabled"));

    var construct = new SecretConstruct(ctx.scope(), ctx.common(), secretConf);

    assertNotNull(construct);
    assertNotNull(construct.secret());
  }

  @Test
  void testSecretsManagerWithRetainPolicy() {
    var ctx = createTestContext();

    var passwordFormat = new SecretFormat(
      false,
      false,
      false,
      false,
      32,
      true);

    var secretConf = new SecretCredentials(
      "retained-secret-" + System.currentTimeMillis(),
      "Retained secret",
      "retainuser",
      passwordFormat,
      "RETAIN",
      Map.of("Policy", "retain"));

    var construct = new SecretConstruct(ctx.scope(), ctx.common(), secretConf);

    assertNotNull(construct);
    assertNotNull(construct.secret());
  }

  @Test
  void testS3BucketWithLifecycleRules() {
    var ctx = createTestContext();

    var lifecycleRule = new BucketLifecycleRule(
      true, // enabled
      90, // expiration in days
      "expire-old-objects");

    var s3Conf = new S3Bucket(
      "lifecycle-bucket-" + System.currentTimeMillis(),
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(lifecycleRule),
      List.of(),
      false,
      true,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of("Lifecycle", "enabled"));

    var construct = new BucketConstruct(ctx.scope(), ctx.common(), s3Conf);

    assertNotNull(construct);
    assertNotNull(construct.bucket());
  }

  @Test
  void testS3BucketWithEncryption() {
    var ctx = createTestContext();

    var kmsConf = new Kms(
      "s3-encryption-key",
      "alias/s3-key",
      false,
      true,
      "ENCRYPT_DECRYPT",
      "SYMMETRIC_DEFAULT",
      "DESTROY");

    var s3Conf = new S3Bucket(
      "encrypted-bucket-" + System.currentTimeMillis(),
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      true,
      false,
      RemovalPolicy.DESTROY,
      kmsConf,
      Map.of("Encrypted", "true"));

    var construct = new BucketConstruct(ctx.scope(), ctx.common(), s3Conf);

    assertNotNull(construct);
    assertNotNull(construct.bucket());
  }

  @Test
  void testS3BucketWithEventBridge() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "eventbridge-bucket-" + System.currentTimeMillis(),
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      true, // eventBridgeEnabled
      true,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of("EventBridge", "enabled"));

    var construct = new BucketConstruct(ctx.scope(), ctx.common(), s3Conf);

    assertNotNull(construct);
    assertNotNull(construct.bucket());
  }

  @Test
  void testS3BucketPublicRead() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "public-bucket-" + System.currentTimeMillis(),
      null,
      BucketAccessControl.PUBLIC_READ,
      ObjectOwnership.OBJECT_WRITER,
      List.of(),
      List.of(),
      false,
      false, // block public access disabled
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of("Access", "public"));

    var construct = new BucketConstruct(ctx.scope(), ctx.common(), s3Conf);

    assertNotNull(construct);
    assertNotNull(construct.bucket());
  }

  @Test
  void testS3BucketWithMultipleLifecycleRules() {
    var ctx = createTestContext();

    var rule1 = new BucketLifecycleRule(
      true,
      30,
      "delete-temp-files");

    var rule2 = new BucketLifecycleRule(
      true,
      365,
      "archive-old-data");

    var s3Conf = new S3Bucket(
      "multi-lifecycle-bucket-" + System.currentTimeMillis(),
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
      Map.of("LifecycleRules", "multiple"));

    var construct = new BucketConstruct(ctx.scope(), ctx.common(), s3Conf);

    assertNotNull(construct);
    assertNotNull(construct.bucket());
  }

  @Test
  void testS3BucketLogDestination() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "log-bucket-" + System.currentTimeMillis(),
      null,
      BucketAccessControl.LOG_DELIVERY_WRITE,
      ObjectOwnership.BUCKET_OWNER_PREFERRED,
      List.of(),
      List.of(),
      false,
      true,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of("Purpose", "logs"));

    var construct = new BucketConstruct(ctx.scope(), ctx.common(), s3Conf);

    assertNotNull(construct);
    assertNotNull(construct.bucket());
  }

  @Test
  void testSqsConstructWithLongRetention() {
    var ctx = createTestContext();

    var sqsConf = new Sqs(
      "long-retention-queue",
      345600, // 4 days in seconds
      List.of(),
      List.of(),
      Map.of("Retention", "4days"));

    var construct = new SqsConstruct(ctx.scope(), ctx.common(), sqsConf);

    assertNotNull(construct);
    assertNotNull(construct.sqs());
  }

  @Test
  void testSecretsManagerLongPassword() {
    var ctx = createTestContext();

    var passwordFormat = new SecretFormat(
      false,
      false,
      false,
      false,
      64, // long password
      true);

    var secretConf = new SecretCredentials(
      "long-password-secret-" + System.currentTimeMillis(),
      "Secret with long password",
      "longpassuser",
      passwordFormat,
      "DESTROY",
      Map.of("Length", "64"));

    var construct = new SecretConstruct(ctx.scope(), ctx.common(), secretConf);

    assertNotNull(construct);
    assertNotNull(construct.secret());
  }

  @Test
  void testSecretsManagerNoUppercase() {
    var ctx = createTestContext();

    var passwordFormat = new SecretFormat(
      false,
      false,
      true, // exclude uppercase
      false,
      20,
      false);

    var secretConf = new SecretCredentials(
      "no-upper-secret-" + System.currentTimeMillis(),
      "Secret without uppercase letters",
      "lowercase",
      passwordFormat,
      "DESTROY",
      Map.of("Case", "lower"));

    var construct = new SecretConstruct(ctx.scope(), ctx.common(), secretConf);

    assertNotNull(construct);
    assertNotNull(construct.secret());
  }

  @Test
  void testSecretsManagerNoLowercase() {
    var ctx = createTestContext();

    var passwordFormat = new SecretFormat(
      true, // exclude lowercase
      false,
      false,
      false,
      20,
      false);

    var secretConf = new SecretCredentials(
      "no-lower-secret-" + System.currentTimeMillis(),
      "Secret without lowercase letters",
      "UPPERCASE",
      passwordFormat,
      "DESTROY",
      Map.of("Case", "upper"));

    var construct = new SecretConstruct(ctx.scope(), ctx.common(), secretConf);

    assertNotNull(construct);
    assertNotNull(construct.secret());
  }

  @Test
  void testS3BucketWithWebsiteConfiguration() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "website-bucket-" + System.currentTimeMillis(),
      null,
      BucketAccessControl.PUBLIC_READ,
      ObjectOwnership.BUCKET_OWNER_PREFERRED,
      List.of(),
      List.of(),
      false,
      false, // public access allowed for website
      true, // website hosting
      RemovalPolicy.DESTROY,
      null,
      Map.of("Type", "website"));

    var construct = new BucketConstruct(ctx.scope(), ctx.common(), s3Conf);

    assertNotNull(construct);
    assertNotNull(construct.bucket());
  }

  @Test
  void testS3BucketAwsOwned() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "aws-owned-bucket-" + System.currentTimeMillis(),
      null,
      BucketAccessControl.AWS_EXEC_READ,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      true,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of("Owner", "aws"));

    var construct = new BucketConstruct(ctx.scope(), ctx.common(), s3Conf);

    assertNotNull(construct);
    assertNotNull(construct.bucket());
  }

  @Test
  void testSqsConstructShortRetention() {
    var ctx = createTestContext();

    var sqsConf = new Sqs(
      "short-retention-queue",
      60, // 1 minute
      List.of(),
      List.of(),
      Map.of("Retention", "minimal"));

    var construct = new SqsConstruct(ctx.scope(), ctx.common(), sqsConf);

    assertNotNull(construct);
    assertNotNull(construct.sqs());
  }

  @Test
  void testS3BucketWithShortLifecycle() {
    var ctx = createTestContext();

    var lifecycleRule = new BucketLifecycleRule(
      true,
      7, // 7 days
      "quick-cleanup");

    var s3Conf = new S3Bucket(
      "short-lifecycle-bucket-" + System.currentTimeMillis(),
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(lifecycleRule),
      List.of(),
      false,
      true,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of("Lifecycle", "7days"));

    var construct = new BucketConstruct(ctx.scope(), ctx.common(), s3Conf);

    assertNotNull(construct);
    assertNotNull(construct.bucket());
  }

  @Test
  void testSecretsManagerMinimalPassword() {
    var ctx = createTestContext();

    var passwordFormat = new SecretFormat(
      false,
      false,
      false,
      false,
      8, // minimal length
      false);

    var secretConf = new SecretCredentials(
      "minimal-secret-" + System.currentTimeMillis(),
      "Minimal length secret",
      "minuser",
      passwordFormat,
      "DESTROY",
      Map.of("Length", "minimal"));

    var construct = new SecretConstruct(ctx.scope(), ctx.common(), secretConf);

    assertNotNull(construct);
    assertNotNull(construct.secret());
  }
}
