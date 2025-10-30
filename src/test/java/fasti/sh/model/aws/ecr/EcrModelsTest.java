package fasti.sh.model.aws.ecr;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.model.aws.kms.Kms;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.ecr.TagMutability;

/**
 * Tests for ECR model records.
 */
class EcrModelsTest {

  @Test
  void testEncryptionWithoutKms() {
    var encryption = new Encryption(false, null);

    assertFalse(encryption.enabled());
    assertNull(encryption.kms());
    assertRecordToString(encryption);
  }

  @Test
  void testEncryptionWithKms() {
    var kms = new Kms("alias/ecr-key", "ECR encryption key", true, true, "ENCRYPT_DECRYPT", "SYMMETRIC_DEFAULT", "RETAIN");
    var encryption = new Encryption(true, kms);

    assertTrue(encryption.enabled());
    assertNotNull(encryption.kms());
    assertEquals("alias/ecr-key", encryption.kms().alias());
  }

  @Test
  void testEncryptionEquality() {
    var encryption1 = new Encryption(false, null);
    var encryption2 = new Encryption(false, null);

    assertRecordEquality(encryption1, encryption2);
  }

  @Test
  void testEcrRepositoryBasic() {
    var encryption = new Encryption(false, null);
    var repo = new EcrRepository(
      "my-repo",
      true,
      false,
      TagMutability.MUTABLE,
      RemovalPolicy.DESTROY,
      encryption);

    assertEquals("my-repo", repo.name());
    assertTrue(repo.scanOnPush());
    assertFalse(repo.emptyOnDelete());
    assertEquals(TagMutability.MUTABLE, repo.tagMutability());
  }

  @Test
  void testEcrRepositoryWithEncryption() {
    var kms = new Kms("alias/repo-key", "Repository encryption key", true, true, "ENCRYPT_DECRYPT", "SYMMETRIC_DEFAULT", "DESTROY");
    var encryption = new Encryption(true, kms);
    var repo = new EcrRepository(
      "secure-repo",
      true,
      true,
      TagMutability.IMMUTABLE,
      RemovalPolicy.DESTROY,
      encryption);

    assertTrue(repo.encryption().enabled());
    assertEquals(TagMutability.IMMUTABLE, repo.tagMutability());
    assertEquals(RemovalPolicy.DESTROY, repo.removalPolicy());
  }

  @Test
  void testEcrRepositoryEquality() {
    var encryption = new Encryption(false, null);
    var repo1 = new EcrRepository("repo", true, false, TagMutability.MUTABLE, RemovalPolicy.DESTROY, encryption);
    var repo2 = new EcrRepository("repo", true, false, TagMutability.MUTABLE, RemovalPolicy.DESTROY, encryption);

    assertRecordEquality(repo1, repo2);
    assertRecordToString(repo1);
  }

  @Test
  void testEcrRepositoryImmutable() {
    var encryption = new Encryption(false, null);
    var repo = new EcrRepository(
      "immutable-repo",
      true,
      false,
      TagMutability.IMMUTABLE,
      RemovalPolicy.RETAIN,
      encryption);

    assertEquals(TagMutability.IMMUTABLE, repo.tagMutability());
    assertEquals(RemovalPolicy.RETAIN, repo.removalPolicy());
  }
}
