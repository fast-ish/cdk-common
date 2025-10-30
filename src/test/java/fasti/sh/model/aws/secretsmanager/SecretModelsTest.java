package fasti.sh.model.aws.secretsmanager;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for Secrets Manager model records.
 */
class SecretModelsTest {

  @Test
  void testSecretFormatBasic() {
    var format = new SecretFormat(false, false, false, false, 32, true);

    assertRecordValid(format, false, false, false, false, 32, true);
    assertEquals(32, format.length());
  }

  @Test
  void testSecretFormatWithExclusions() {
    var format = new SecretFormat(
      true, // excludeLowercase
      true, // excludeNumbers
      false, // excludeUppercase
      false, // includeSpace
      16,
      false);

    assertTrue(format.excludeLowercase());
    assertTrue(format.excludeNumbers());
    assertFalse(format.excludeUppercase());
  }

  @Test
  void testSecretFormatEquality() {
    var format1 = new SecretFormat(false, false, false, false, 32, true);
    var format2 = new SecretFormat(false, false, false, false, 32, true);

    assertRecordEquality(format1, format2);
  }

  @Test
  void testSecretCredentialsBasic() {
    var format = new SecretFormat(false, false, false, false, 32, true);
    var credentials = new SecretCredentials(
      "my-secret",
      "Database credentials",
      "admin",
      format,
      "DESTROY",
      Map.of("Type", "database"));

    assertEquals("my-secret", credentials.name());
    assertEquals("admin", credentials.username());
    assertEquals("Database credentials", credentials.description());
  }

  @Test
  void testSecretCredentialsEquality() {
    var format = new SecretFormat(false, false, false, false, 32, true);
    var creds1 = new SecretCredentials("secret1", "desc", "user1", format, "DESTROY", Map.of());
    var creds2 = new SecretCredentials("secret1", "desc", "user1", format, "DESTROY", Map.of());

    assertRecordEquality(creds1, creds2);
    assertRecordToString(creds1);
  }

  @Test
  void testSecretCredentialsWithDifferentFormats() {
    var shortFormat = new SecretFormat(false, false, false, false, 16, false);
    var longFormat = new SecretFormat(false, false, false, false, 64, true);

    var shortCreds = new SecretCredentials("short-secret", "Short password", "user", shortFormat, "DESTROY", Map.of());
    var longCreds = new SecretCredentials("long-secret", "Long password", "user", longFormat, "RETAIN", Map.of());

    assertEquals(16, shortCreds.password().length());
    assertEquals(64, longCreds.password().length());
    assertEquals("DESTROY", shortCreds.removalPolicy());
    assertEquals("RETAIN", longCreds.removalPolicy());
  }
}
