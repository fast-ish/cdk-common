package fasti.sh.execute.aws;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.aws.cloudwatch.LogGroupConstruct;
import fasti.sh.execute.aws.ecr.EcrRepositoryConstruct;
import fasti.sh.execute.aws.kms.KmsConstruct;
import fasti.sh.model.aws.cloudwatch.LogGroupConf;
import fasti.sh.model.aws.ecr.EcrRepository;
import fasti.sh.model.aws.ecr.Encryption;
import fasti.sh.model.aws.kms.Kms;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.ecr.TagMutability;

/**
 * Comprehensive tests for simple AWS constructs. Tests instantiation and validates that models are properly used.
 */
class SimpleConstructsTest {

  @Test
  void testKmsConstruct() {
    var ctx = createTestContext();

    var kmsConf = new Kms(
      "test-key",
      "alias/test-key",
      false,
      true,
      "ENCRYPT_DECRYPT",
      "SYMMETRIC_DEFAULT",
      "RETAIN");

    var construct = new KmsConstruct(ctx.scope(), ctx.common(), kmsConf);

    assertNotNull(construct);
    assertNotNull(construct.key());
    // CDK uses tokens for lazy evaluation, so we just verify the key exists
  }

  @Test
  void testEcrRepositoryConstruct() {
    var ctx = createTestContext();

    var encryption = new Encryption(false, null);
    var ecrConf = new EcrRepository(
      "my-repo",
      true,
      false,
      TagMutability.MUTABLE,
      RemovalPolicy.DESTROY,
      encryption);

    var construct = new EcrRepositoryConstruct(ctx.scope(), ctx.common(), ecrConf);

    assertNotNull(construct);
    assertNotNull(construct.repository());
  }

  @Test
  void testEcrRepositoryConstructWithEncryption() {
    var ctx = createTestContext();

    var kmsConf = new Kms(
      "ecr-key",
      "alias/ecr-key",
      false,
      true,
      "ENCRYPT_DECRYPT",
      "SYMMETRIC_DEFAULT",
      "RETAIN");
    var encryption = new Encryption(true, kmsConf);
    var ecrConf = new EcrRepository(
      "secure-repo",
      true,
      true,
      TagMutability.IMMUTABLE,
      RemovalPolicy.DESTROY,
      encryption);

    var construct = new EcrRepositoryConstruct(ctx.scope(), ctx.common(), ecrConf);

    assertNotNull(construct);
    assertNotNull(construct.repository());
  }

  @Test
  void testLogGroupConstruct() {
    var ctx = createTestContext();

    var logGroupConf = new LogGroupConf(
      "log-group-1",
      "STANDARD",
      "ONE_WEEK",
      null,
      "DESTROY",
      Map.of());

    var construct = new LogGroupConstruct(ctx.scope(), ctx.common(), logGroupConf);

    assertNotNull(construct);
    assertNotNull(construct.logGroup());
  }

  @Test
  void testLogGroupConstructWithKms() {
    var ctx = createTestContext();

    var kmsConf = new Kms(
      "logs-key",
      "alias/logs-key",
      false,
      false,
      "ENCRYPT_DECRYPT",
      "SYMMETRIC_DEFAULT",
      "RETAIN");
    var logGroupConf = new LogGroupConf(
      "secure-logs",
      "STANDARD",
      "ONE_MONTH",
      kmsConf,
      "RETAIN",
      Map.of("Encrypted", "true"));

    var construct = new LogGroupConstruct(ctx.scope(), ctx.common(), logGroupConf);

    assertNotNull(construct);
    assertNotNull(construct.logGroup());
  }

  @Test
  void testMultipleConstructsInSameStack() {
    var ctx = createTestContext();

    // Create multiple constructs in the same stack
    var kmsConf = new Kms("key1", "alias/key1", false, true, "ENCRYPT_DECRYPT", "SYMMETRIC_DEFAULT", "DESTROY");
    var kmsConstruct = new KmsConstruct(ctx.scope(), ctx.common(), kmsConf);

    var encryption = new Encryption(false, null);
    var ecrConf = new EcrRepository("repo1", true, true, TagMutability.MUTABLE, RemovalPolicy.DESTROY, encryption);
    var ecrConstruct = new EcrRepositoryConstruct(ctx.scope(), ctx.common(), ecrConf);

    var logConf = new LogGroupConf("logs1", "STANDARD", "ONE_WEEK", null, "DESTROY", Map.of());
    var logConstruct = new LogGroupConstruct(ctx.scope(), ctx.common(), logConf);

    // Verify all constructs were created
    assertNotNull(kmsConstruct.key());
    assertNotNull(ecrConstruct.repository());
    assertNotNull(logConstruct.logGroup());

    // Verify they're all in the same construct tree
    assertEquals(ctx.scope(), kmsConstruct.getNode().getScope());
    assertEquals(ctx.scope(), ecrConstruct.getNode().getScope());
    assertEquals(ctx.scope(), logConstruct.getNode().getScope());
  }

  @Test
  void testKmsConstructWithRotation() {
    var ctx = createTestContext();

    var kmsConf = new Kms(
      "rotation-key",
      "alias/rotation-key",
      true, // enable rotation
      true,
      "ENCRYPT_DECRYPT",
      "SYMMETRIC_DEFAULT",
      "RETAIN");

    var construct = new KmsConstruct(ctx.scope(), ctx.common(), kmsConf);

    assertNotNull(construct);
    assertNotNull(construct.key());
  }

  @Test
  void testLogGroupConstructInfrequentAccess() {
    var ctx = createTestContext();

    var logGroupConf = new LogGroupConf(
      "infrequent-logs",
      "INFREQUENT_ACCESS",
      "THREE_MONTHS",
      null,
      "DESTROY",
      Map.of("LogClass", "infrequent"));

    var construct = new LogGroupConstruct(ctx.scope(), ctx.common(), logGroupConf);

    assertNotNull(construct);
    assertNotNull(construct.logGroup());
  }

  @Test
  void testLogGroupConstructWithLongRetention() {
    var ctx = createTestContext();

    var logGroupConf = new LogGroupConf(
      "long-retention-logs",
      "STANDARD",
      "ONE_YEAR",
      null,
      "RETAIN",
      Map.of("Retention", "365days"));

    var construct = new LogGroupConstruct(ctx.scope(), ctx.common(), logGroupConf);

    assertNotNull(construct);
    assertNotNull(construct.logGroup());
  }

  @Test
  void testKmsConstructAsymmetric() {
    var ctx = createTestContext();

    var kmsConf = new Kms(
      "asymmetric-key",
      "alias/asymmetric-key",
      false, // rotation must be false for asymmetric keys
      false, // no multi-region
      "SIGN_VERIFY",
      "RSA_2048",
      "DESTROY");

    var construct = new KmsConstruct(ctx.scope(), ctx.common(), kmsConf);

    assertNotNull(construct);
    assertNotNull(construct.key());
  }

  @Test
  void testEcrRepositoryConstructRetainPolicy() {
    var ctx = createTestContext();

    var encryption = new Encryption(false, null);
    var ecrConf = new EcrRepository(
      "production-repo",
      true,
      false, // emptyOnDelete must be false with RETAIN
      TagMutability.IMMUTABLE,
      RemovalPolicy.RETAIN,
      encryption);

    var construct = new EcrRepositoryConstruct(ctx.scope(), ctx.common(), ecrConf);

    assertNotNull(construct);
    assertNotNull(construct.repository());
  }
}
