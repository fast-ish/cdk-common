package fasti.sh.execute.aws;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.aws.kms.KmsConstruct;
import fasti.sh.execute.aws.sqs.SqsConstruct;
import fasti.sh.model.aws.kms.Kms;
import fasti.sh.model.aws.sqs.Sqs;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for AWS KMS and SQS constructs.
 */
class KmsSqsConstructsTest {

  @Test
  void testKmsConstructBasic() {
    var ctx = createTestContext();

    var kmsConf = new Kms(
      "test-key",
      "Test KMS key",
      true,
      true,
      "encrypt_decrypt",
      "symmetric_default",
      "destroy");

    var construct = new KmsConstruct(ctx.scope(), ctx.common(), kmsConf);

    assertNotNull(construct);
    assertNotNull(construct.key());
  }

  @Test
  void testKmsConstructWithRotation() {
    var ctx = createTestContext();

    var kmsConf = new Kms(
      "rotating-key",
      "KMS key with automatic rotation",
      true,
      true,
      "encrypt_decrypt",
      "symmetric_default",
      "retain");

    var construct = new KmsConstruct(ctx.scope(), ctx.common(), kmsConf);

    assertNotNull(construct);
    assertNotNull(construct.key());
  }

  @Test
  void testKmsConstructAsymmetric() {
    var ctx = createTestContext();

    var kmsConf = new Kms(
      "asymmetric-key",
      "Asymmetric KMS key for signing",
      true,
      false,
      "sign_verify",
      "rsa_2048",
      "destroy");

    var construct = new KmsConstruct(ctx.scope(), ctx.common(), kmsConf);

    assertNotNull(construct);
    assertNotNull(construct.key());
  }

  @Test
  void testSqsConstructBasic() {
    var ctx = createTestContext();

    var sqsConf = new Sqs(
      "test-queue",
      1209600, // 14 days in seconds
      List.of(),
      List.of(),
      Map.of("Type", "standard"));

    var construct = new SqsConstruct(ctx.scope(), ctx.common(), sqsConf);

    assertNotNull(construct);
    assertNotNull(construct.sqs());
    assertNotNull(construct.policies());
    assertNotNull(construct.rules());
    assertTrue(construct.policies().isEmpty());
    assertTrue(construct.rules().isEmpty());
  }

  @Test
  void testSqsConstructShortRetention() {
    var ctx = createTestContext();

    var sqsConf = new Sqs(
      "short-retention-queue",
      60, // 1 minute
      List.of(),
      List.of(),
      Map.of("Retention", "short"));

    var construct = new SqsConstruct(ctx.scope(), ctx.common(), sqsConf);

    assertNotNull(construct);
    assertNotNull(construct.sqs());
  }

  @Test
  void testSqsConstructLongRetention() {
    var ctx = createTestContext();

    var sqsConf = new Sqs(
      "long-retention-queue",
      1209600, // 14 days (maximum)
      List.of(),
      List.of(),
      Map.of("Retention", "maximum"));

    var construct = new SqsConstruct(ctx.scope(), ctx.common(), sqsConf);

    assertNotNull(construct);
    assertNotNull(construct.sqs());
  }

  @Test
  void testKmsConstructMultiRegion() {
    var ctx = createTestContext();

    var kmsConf = new Kms(
      "multi-region-key",
      "Multi-region KMS key",
      false, // rotation disabled for multi-region
      true, // multi-region enabled
      "encrypt_decrypt",
      "symmetric_default",
      "retain");

    var construct = new KmsConstruct(ctx.scope(), ctx.common(), kmsConf);

    assertNotNull(construct);
    assertNotNull(construct.key());
  }

  @Test
  void testKmsConstructEccKey() {
    var ctx = createTestContext();

    var kmsConf = new Kms(
      "ecc-key",
      "ECC key for signing",
      false, // no rotation for asymmetric
      false,
      "sign_verify",
      "ecc_nist_p256",
      "destroy");

    var construct = new KmsConstruct(ctx.scope(), ctx.common(), kmsConf);

    assertNotNull(construct);
    assertNotNull(construct.key());
  }

  @Test
  void testSqsConstructMediumRetention() {
    var ctx = createTestContext();

    var sqsConf = new Sqs(
      "medium-retention-queue",
      345600, // 4 days
      List.of(),
      List.of(),
      Map.of("Retention", "4days"));

    var construct = new SqsConstruct(ctx.scope(), ctx.common(), sqsConf);

    assertNotNull(construct);
    assertNotNull(construct.sqs());
  }

  @Test
  void testSqsConstructWithTags() {
    var ctx = createTestContext();

    var sqsConf = new Sqs(
      "tagged-queue",
      604800, // 7 days
      List.of(),
      List.of(),
      Map.of("Environment", "production", "Team", "platform", "CostCenter", "engineering"));

    var construct = new SqsConstruct(ctx.scope(), ctx.common(), sqsConf);

    assertNotNull(construct);
    assertNotNull(construct.sqs());
  }

  @Test
  void testKmsConstructRsa4096() {
    var ctx = createTestContext();

    var kmsConf = new Kms(
      "rsa-4096-key",
      "RSA 4096-bit key",
      false, // no rotation for asymmetric
      false,
      "sign_verify",
      "rsa_4096",
      "retain");

    var construct = new KmsConstruct(ctx.scope(), ctx.common(), kmsConf);

    assertNotNull(construct);
    assertNotNull(construct.key());
  }
}
