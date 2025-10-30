package fasti.sh.model.aws.kms;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests for KMS model record.
 */
class KmsTest {

  @Test
  void testKmsRecordCreation() {
    var kms = new Kms(
      "alias/test-key",
      "Test KMS key",
      true,
      false,
      "ENCRYPT_DECRYPT",
      "SYMMETRIC_DEFAULT",
      "RETAIN");

    assertRecordValid(kms, "alias/test-key", "Test KMS key", true, false, "ENCRYPT_DECRYPT", "SYMMETRIC_DEFAULT", "RETAIN");
  }

  @Test
  void testKmsWithRotation() {
    var kms = new Kms(
      "alias/rotation-key",
      "Rotation enabled key",
      true,
      true, // enableKeyRotation
      "ENCRYPT_DECRYPT",
      "SYMMETRIC_DEFAULT",
      "DESTROY");

    assertTrue(kms.enableKeyRotation());
    assertRecordToString(kms);
  }

  @Test
  void testKmsEquality() {
    var kms1 = new Kms("alias/key1", "Key 1", true, false, "ENCRYPT_DECRYPT", "SYMMETRIC_DEFAULT", "RETAIN");
    var kms2 = new Kms("alias/key1", "Key 1", true, false, "ENCRYPT_DECRYPT", "SYMMETRIC_DEFAULT", "RETAIN");
    var kms3 = new Kms("alias/key2", "Key 2", true, false, "ENCRYPT_DECRYPT", "SYMMETRIC_DEFAULT", "RETAIN");

    assertRecordEquality(kms1, kms2);
    assertNotEquals(kms1, kms3);
  }

  @Test
  void testKmsAsymmetric() {
    var kms = new Kms(
      "alias/asymmetric-key",
      "Asymmetric signing key",
      true,
      false, // no rotation for asymmetric keys
      "SIGN_VERIFY",
      "RSA_2048",
      "RETAIN");

    assertEquals("SIGN_VERIFY", kms.keyUsage());
    assertEquals("RSA_2048", kms.keySpec());
  }

  @Test
  void testKmsDisabled() {
    var kms = new Kms(
      "alias/disabled-key",
      "Disabled key",
      false,
      false,
      "ENCRYPT_DECRYPT",
      "SYMMETRIC_DEFAULT",
      "DESTROY");

    assertFalse(kms.enabled());
    assertEquals("DESTROY", kms.removalPolicy());
  }

  @Test
  void testKmsRsa4096() {
    var kms = new Kms(
      "alias/rsa-4096",
      "RSA 4096-bit key",
      true,
      false,
      "SIGN_VERIFY",
      "RSA_4096",
      "RETAIN");

    assertEquals("RSA_4096", kms.keySpec());
    assertEquals("SIGN_VERIFY", kms.keyUsage());
  }

  @Test
  void testKmsEccKey() {
    var kms = new Kms(
      "alias/ecc-key",
      "ECC signing key",
      true,
      false,
      "SIGN_VERIFY",
      "ECC_NIST_P256",
      "RETAIN");

    assertEquals("ECC_NIST_P256", kms.keySpec());
  }

  @Test
  void testKmsDestroyPolicy() {
    var kms = new Kms(
      "alias/temp-key",
      "Temporary key",
      true,
      false,
      "ENCRYPT_DECRYPT",
      "SYMMETRIC_DEFAULT",
      "DESTROY");

    assertEquals("DESTROY", kms.removalPolicy());
  }

  @Test
  void testKmsRetainPolicy() {
    var kms = new Kms(
      "alias/permanent-key",
      "Permanent key",
      true,
      true,
      "ENCRYPT_DECRYPT",
      "SYMMETRIC_DEFAULT",
      "RETAIN");

    assertEquals("RETAIN", kms.removalPolicy());
    assertTrue(kms.enableKeyRotation());
  }

  @Test
  void testKmsSnapshotPolicy() {
    var kms = new Kms(
      "alias/snapshot-key",
      "Key with snapshot",
      true,
      false,
      "ENCRYPT_DECRYPT",
      "SYMMETRIC_DEFAULT",
      "SNAPSHOT");

    assertEquals("SNAPSHOT", kms.removalPolicy());
  }

  @Test
  void testKmsEncryptionOnly() {
    var kms = new Kms(
      "alias/encrypt-key",
      "Encryption only key",
      true,
      true,
      "ENCRYPT_DECRYPT",
      "SYMMETRIC_DEFAULT",
      "RETAIN");

    assertEquals("ENCRYPT_DECRYPT", kms.keyUsage());
    assertTrue(kms.enabled());
  }

  @Test
  void testKmsSignVerifyOnly() {
    var kms = new Kms(
      "alias/sign-key",
      "Signing only key",
      true,
      false,
      "SIGN_VERIFY",
      "RSA_3072",
      "RETAIN");

    assertEquals("SIGN_VERIFY", kms.keyUsage());
    assertEquals("RSA_3072", kms.keySpec());
  }

  @Test
  void testKmsMinimalDescription() {
    var kms = new Kms(
      "alias/min",
      "",
      true,
      false,
      "ENCRYPT_DECRYPT",
      "SYMMETRIC_DEFAULT",
      "DESTROY");

    assertEquals("", kms.description());
  }

  @Test
  void testKmsLongAlias() {
    var kms = new Kms(
      "alias/very-long-descriptive-key-name-for-production-encryption",
      "Long alias key",
      true,
      true,
      "ENCRYPT_DECRYPT",
      "SYMMETRIC_DEFAULT",
      "RETAIN");

    assertTrue(kms.alias().startsWith("alias/"));
    assertTrue(kms.alias().length() > 20);
  }

  @Test
  void testKmsAllFieldsPopulated() {
    var kms = new Kms(
      "alias/complete",
      "Complete KMS configuration",
      true,
      true,
      "ENCRYPT_DECRYPT",
      "SYMMETRIC_DEFAULT",
      "RETAIN");

    assertNotNull(kms.alias());
    assertNotNull(kms.description());
    assertNotNull(kms.keyUsage());
    assertNotNull(kms.keySpec());
    assertNotNull(kms.removalPolicy());
  }

  @Test
  void testKmsEccP384() {
    var kms = new Kms(
      "alias/ecc-p384",
      "ECC P-384 key",
      true,
      false,
      "SIGN_VERIFY",
      "ECC_NIST_P384",
      "RETAIN");

    assertEquals("ECC_NIST_P384", kms.keySpec());
  }

  @Test
  void testKmsEccP521() {
    var kms = new Kms(
      "alias/ecc-p521",
      "ECC P-521 key",
      true,
      false,
      "SIGN_VERIFY",
      "ECC_NIST_P521",
      "RETAIN");

    assertEquals("ECC_NIST_P521", kms.keySpec());
  }
}
