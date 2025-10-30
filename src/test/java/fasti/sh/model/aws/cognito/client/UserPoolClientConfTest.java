package fasti.sh.model.aws.cognito.client;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.serialization.Mapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class UserPoolClientConfTest {

  @Test
  void testRecordInstantiation() {
    var authFlow = new AuthFlow(true, false, true, true);
    var oauth = new CognitoOAuth(
      true,
      List.of("https://example.com/callback"),
      false,
      false,
      List.of("https://example.com/logout"),
      List.of("openid"));
    var readAttrs = new ClientAttributesConf(
      true, false, true, true, false, true, false, true, false,
      false, false, true, true, true, false, false, false, true, false);
    var writeAttrs = new ClientAttributesConf(
      false, false, true, false, false, true, false, true, false,
      false, false, false, true, false, false, false, false, false, false);

    var config = new UserPoolClientConf(
      "1h",
      authFlow,
      "15m",
      true,
      false,
      true,
      false,
      true,
      "1h",
      "my-app-client",
      oauth,
      readAttrs,
      writeAttrs,
      List.of("custom:attribute1"),
      "30d",
      Map.of("Environment", "production"));

    assertEquals("1h", config.accessTokenValidity());
    assertEquals(authFlow, config.authFlow());
    assertEquals("15m", config.authSessionValidity());
    assertTrue(config.preventUserExistenceErrors());
    assertFalse(config.disableOAuth());
    assertTrue(config.enableTokenRevocation());
    assertFalse(config.generateSecret());
    assertTrue(config.enablePropagateAdditionalUserContextData());
    assertEquals("1h", config.idTokenValidity());
    assertEquals("my-app-client", config.name());
    assertEquals(oauth, config.oauth());
    assertEquals(readAttrs, config.readAttributes());
    assertEquals(writeAttrs, config.writeAttributes());
    assertEquals(1, config.customAttributes().size());
    assertEquals("30d", config.refreshTokenValidity());
    assertEquals(1, config.tags().size());
  }

  @Test
  void testRecordWithMinimalConfiguration() {
    var config = new UserPoolClientConf(
      null, null, null, false, true, false, false, false, null,
      "basic-client", null, null, null, null, null, null);

    assertNull(config.accessTokenValidity());
    assertNull(config.authFlow());
    assertFalse(config.preventUserExistenceErrors());
    assertTrue(config.disableOAuth());
    assertEquals("basic-client", config.name());
  }

  @Test
  void testRecordWithCustomAttributes() {
    var customAttrs = List.of("custom:attr1", "custom:attr2", "custom:attr3");

    var config = new UserPoolClientConf(
      "1h", null, null, false, false, false, false, false, "1h",
      "client", null, null, null, customAttrs, "7d", Map.of());

    assertEquals(3, config.customAttributes().size());
    assertTrue(config.customAttributes().contains("custom:attr1"));
  }

  @Test
  void testRecordWithTags() {
    var tags = Map
      .of(
        "Environment",
        "production",
        "Application",
        "my-app",
        "Team",
        "backend");

    var config = new UserPoolClientConf(
      null, null, null, false, false, false, false, false, null,
      "tagged-client", null, null, null, null, null, tags);

    assertEquals(3, config.tags().size());
    assertEquals("production", config.tags().get("Environment"));
  }

  @Test
  void testRecordEquality() {
    var authFlow = new AuthFlow(true, false, true, true);
    var config1 = new UserPoolClientConf(
      "1h", authFlow, "15m", true, false, true, false, false, "1h",
      "client", null, null, null, null, "7d", Map.of());
    var config2 = new UserPoolClientConf(
      "1h", authFlow, "15m", true, false, true, false, false, "1h",
      "client", null, null, null, null, "7d", Map.of());

    assertRecordEquality(config1, config2);
  }

  @Test
  void testToString() {
    var config = new UserPoolClientConf(
      "1h", null, null, false, false, false, false, false, null,
      "test-client", null, null, null, null, null, null);
    assertRecordToString(config);
  }

  @Test
  void testSerialization() throws Exception {
    var authFlow = new AuthFlow(true, false, true, true);
    var config = new UserPoolClientConf(
      "1h", authFlow, "15m", true, false, true, false, false, "1h",
      "my-client", null, null, null, null, "30d", Map.of("Env", "test"));

    var json = Mapper.get().writeValueAsString(config);
    assertNotNull(json);
    assertTrue(json.contains("my-client"));
  }

  @Test
  void testDeserialization() throws Exception {
    var json = """
      {
        "accessTokenValidity": "1h",
        "authFlow": {
          "adminUserPassword": true,
          "custom": false,
          "userPassword": true,
          "userSrp": true
        },
        "authSessionValidity": "15m",
        "preventUserExistenceErrors": true,
        "disableOAuth": false,
        "enableTokenRevocation": true,
        "generateSecret": false,
        "enablePropagateAdditionalUserContextData": false,
        "idTokenValidity": "1h",
        "name": "test-client",
        "oauth": null,
        "readAttributes": null,
        "writeAttributes": null,
        "customAttributes": null,
        "refreshTokenValidity": "30d",
        "tags": {"Environment": "test"}
      }
      """;

    var config = Mapper.get().readValue(json, UserPoolClientConf.class);
    assertNotNull(config);
    assertEquals("test-client", config.name());
    assertEquals("1h", config.accessTokenValidity());
    assertTrue(config.preventUserExistenceErrors());
  }
}
