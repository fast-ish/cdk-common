package fasti.sh.execute.aws;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.aws.ecr.EcrRepositoryConstruct;
import fasti.sh.model.aws.ecr.EcrRepository;
import fasti.sh.model.aws.ecr.Encryption;
import fasti.sh.model.aws.kms.Kms;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.ecr.TagMutability;

/**
 * Tests for ECR constructs.
 */
class EcrConstructsTest {

  @Test
  void testEcrRepositoryBasic() {
    var ctx = createTestContext();

    var encryption = new Encryption(false, null);
    var ecrConf = new EcrRepository(
      "test-repo",
      false,
      false,
      TagMutability.MUTABLE,
      RemovalPolicy.DESTROY,
      encryption);

    var construct = new EcrRepositoryConstruct(ctx.scope(), ctx.common(), ecrConf);

    assertNotNull(construct);
    assertNotNull(construct.repository());
  }

  @Test
  void testEcrRepositoryWithEncryption() {
    var ctx = createTestContext();

    var kms = new Kms(
      "ecr-key",
      "ECR encryption key",
      true,
      true,
      "ENCRYPT_DECRYPT",
      "SYMMETRIC_DEFAULT",
      "RETAIN");
    var encryption = new Encryption(true, kms);
    var ecrConf = new EcrRepository(
      "encrypted-repo",
      true,
      false, // emptyOnDelete must be false when removalPolicy is RETAIN
      TagMutability.IMMUTABLE,
      RemovalPolicy.RETAIN,
      encryption);

    var construct = new EcrRepositoryConstruct(ctx.scope(), ctx.common(), ecrConf);

    assertNotNull(construct);
    assertNotNull(construct.repository());
  }

  @Test
  void testEcrRepositoryImmutable() {
    var ctx = createTestContext();

    var encryption = new Encryption(false, null);
    var ecrConf = new EcrRepository(
      "immutable-repo",
      false,
      false,
      TagMutability.IMMUTABLE,
      RemovalPolicy.DESTROY,
      encryption);

    var construct = new EcrRepositoryConstruct(ctx.scope(), ctx.common(), ecrConf);

    assertNotNull(construct);
    assertNotNull(construct.repository());
  }

  @Test
  void testEcrRepositoryWithScanOnPush() {
    var ctx = createTestContext();

    var encryption = new Encryption(false, null);
    var ecrConf = new EcrRepository(
      "scan-repo",
      true, // scanOnPush enabled
      true,
      TagMutability.MUTABLE,
      RemovalPolicy.DESTROY,
      encryption);

    var construct = new EcrRepositoryConstruct(ctx.scope(), ctx.common(), ecrConf);

    assertNotNull(construct);
    assertNotNull(construct.repository());
  }

  @Test
  void testEcrRepositoryWithEmptyOnDelete() {
    var ctx = createTestContext();

    var encryption = new Encryption(false, null);
    var ecrConf = new EcrRepository(
      "autodelete-repo",
      false,
      true, // emptyOnDelete enabled
      TagMutability.MUTABLE,
      RemovalPolicy.DESTROY,
      encryption);

    var construct = new EcrRepositoryConstruct(ctx.scope(), ctx.common(), ecrConf);

    assertNotNull(construct);
    assertNotNull(construct.repository());
  }

  @Test
  void testEcrRepositoryFullFeatured() {
    var ctx = createTestContext();

    var kms = new Kms(
      "full-ecr-key",
      "Full featured ECR key",
      true,
      true,
      "ENCRYPT_DECRYPT",
      "SYMMETRIC_DEFAULT",
      "DESTROY");
    var encryption = new Encryption(true, kms);
    var ecrConf = new EcrRepository(
      "full-repo",
      true, // scanOnPush
      true, // emptyOnDelete
      TagMutability.IMMUTABLE,
      RemovalPolicy.DESTROY,
      encryption);

    var construct = new EcrRepositoryConstruct(ctx.scope(), ctx.common(), ecrConf);

    assertNotNull(construct);
    assertNotNull(construct.repository());
  }

  @Test
  void testEcrRepositoryWithRetainPolicy() {
    var ctx = createTestContext();

    var encryption = new Encryption(false, null);
    var ecrConf = new EcrRepository(
      "retain-repo",
      false,
      false, // emptyOnDelete must be false when using RETAIN
      TagMutability.IMMUTABLE,
      RemovalPolicy.RETAIN,
      encryption);

    var construct = new EcrRepositoryConstruct(ctx.scope(), ctx.common(), ecrConf);

    assertNotNull(construct);
    assertNotNull(construct.repository());
  }

  @Test
  void testEcrRepositoryEncryptedImmutableScanEnabled() {
    var ctx = createTestContext();

    var kms = new Kms(
      "secure-ecr-key",
      "Secure ECR encryption key",
      true,
      true,
      "ENCRYPT_DECRYPT",
      "SYMMETRIC_DEFAULT",
      "DESTROY");
    var encryption = new Encryption(true, kms);
    var ecrConf = new EcrRepository(
      "secure-repo",
      true, // scanOnPush
      false,
      TagMutability.IMMUTABLE,
      RemovalPolicy.DESTROY,
      encryption);

    var construct = new EcrRepositoryConstruct(ctx.scope(), ctx.common(), ecrConf);

    assertNotNull(construct);
    assertNotNull(construct.repository());
  }
}
