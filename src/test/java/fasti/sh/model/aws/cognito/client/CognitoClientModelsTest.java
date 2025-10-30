package fasti.sh.model.aws.cognito.client;

import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.serialization.Mapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for Cognito User Pool client model classes.
 */
class CognitoClientModelsTest {

  @Test
  void testAuthorizerInstantiation() {
    var authorizer = new Authorizer("cognito-authorizer");

    assertEquals("cognito-authorizer", authorizer.name());
  }

  @Test
  void testAuthorizerSerialization() throws Exception {
    var authorizer = new Authorizer("test-auth");
    var json = Mapper.get().writeValueAsString(authorizer);

    assertNotNull(json);
    assertTrue(json.contains("test-auth"));
  }

  @Test
  void testAuthorizerDeserialization() throws Exception {
    var json = """
      {"name":"my-authorizer"}
      """;
    var authorizer = Mapper.get().readValue(json, Authorizer.class);

    assertNotNull(authorizer);
    assertEquals("my-authorizer", authorizer.name());
  }

  @Test
  void testAuthFlowInstantiation() {
    var authFlow = new AuthFlow(true, false, true, true);

    assertTrue(authFlow.adminUserPassword());
    assertFalse(authFlow.custom());
    assertTrue(authFlow.userPassword());
    assertTrue(authFlow.userSrp());
  }

  @Test
  void testAuthFlowSerialization() throws Exception {
    var authFlow = new AuthFlow(false, true, false, true);
    var json = Mapper.get().writeValueAsString(authFlow);

    assertNotNull(json);
    assertTrue(json.contains("true"));
  }

  @Test
  void testAuthFlowDeserialization() throws Exception {
    var json = """
      {
        "adminUserPassword": true,
        "custom": true,
        "userPassword": false,
        "userSrp": true
      }
      """;
    var authFlow = Mapper.get().readValue(json, AuthFlow.class);

    assertNotNull(authFlow);
    assertTrue(authFlow.adminUserPassword());
    assertTrue(authFlow.custom());
    assertFalse(authFlow.userPassword());
    assertTrue(authFlow.userSrp());
  }

  @Test
  void testClientAttributesConfInstantiation() {
    var attrs = new ClientAttributesConf(
      true, true, true, false, true,
      true, false, true, false, true,
      false, true, true, false, true,
      false, true, true, false);

    assertTrue(attrs.address());
    assertTrue(attrs.email());
    assertFalse(attrs.email_verified());
    assertTrue(attrs.phone_number());
  }

  @Test
  void testClientAttributesConfSerialization() throws Exception {
    var attrs = new ClientAttributesConf(
      false, false, true, true, false,
      false, false, false, false, false,
      false, false, true, true, false,
      false, false, false, false);
    var json = Mapper.get().writeValueAsString(attrs);

    assertNotNull(json);
    assertTrue(json.contains("email"));
    assertTrue(json.contains("phone_number"));
  }

  @Test
  void testCognitoOAuthInstantiation() {
    var oauth = new CognitoOAuth(
      true,
      List.of("https://example.com/callback"),
      false,
      true,
      List.of("https://example.com/logout"),
      List.of("openid", "email", "profile"));

    assertTrue(oauth.authorizationCodeGrant());
    assertFalse(oauth.clientCredentials());
    assertTrue(oauth.implicitCodeGrant());
    assertEquals(1, oauth.callbackUrls().size());
    assertEquals("https://example.com/callback", oauth.callbackUrls().get(0));
    assertEquals(3, oauth.scopes().size());
  }

  @Test
  void testCognitoOAuthSerialization() throws Exception {
    var oauth = new CognitoOAuth(
      true,
      List.of("https://app.example.com/auth"),
      false,
      false,
      List.of("https://app.example.com/signout"),
      List.of("openid"));
    var json = Mapper.get().writeValueAsString(oauth);

    assertNotNull(json);
    assertTrue(json.contains("https://app.example.com/auth"));
    assertTrue(json.contains("openid"));
  }

  @Test
  void testCognitoOAuthDeserialization() throws Exception {
    var json = """
      {
        "authorizationCodeGrant": true,
        "callbackUrls": ["https://test.com/callback"],
        "clientCredentials": false,
        "implicitCodeGrant": true,
        "logoutUrls": ["https://test.com/logout"],
        "scopes": ["email", "profile"]
      }
      """;
    var oauth = Mapper.get().readValue(json, CognitoOAuth.class);

    assertNotNull(oauth);
    assertTrue(oauth.authorizationCodeGrant());
    assertFalse(oauth.clientCredentials());
    assertEquals(1, oauth.callbackUrls().size());
    assertEquals(2, oauth.scopes().size());
  }

  @Test
  void testUserPoolClientConfInstantiation() {
    var authFlow = new AuthFlow(true, false, true, true);
    var oauth = new CognitoOAuth(
      true,
      List.of("https://example.com/callback"),
      false,
      false,
      List.of("https://example.com/logout"),
      List.of("openid", "email"));
    var readAttrs = new ClientAttributesConf(
      true, true, true, true, true,
      true, true, true, true, true,
      true, true, true, true, true,
      true, true, true, true);
    var writeAttrs = new ClientAttributesConf(
      false, false, true, false, true,
      true, false, true, false, false,
      false, true, true, false, false,
      false, false, false, false);

    var client = new UserPoolClientConf(
      "1h",
      authFlow,
      "30m",
      true,
      false,
      true,
      false,
      true,
      "1h",
      "web-client",
      oauth,
      readAttrs,
      writeAttrs,
      List.of("custom:company", "custom:department"),
      "30d",
      Map.of("Environment", "production"));

    assertEquals("1h", client.accessTokenValidity());
    assertEquals("web-client", client.name());
    assertTrue(client.preventUserExistenceErrors());
    assertFalse(client.disableOAuth());
    assertTrue(client.enableTokenRevocation());
    assertNotNull(client.authFlow());
    assertNotNull(client.oauth());
    assertEquals(2, client.customAttributes().size());
  }

  @Test
  void testUserPoolClientConfSerialization() throws Exception {
    var authFlow = new AuthFlow(true, false, true, false);
    var oauth = new CognitoOAuth(
      true,
      List.of("https://app.com/auth"),
      false,
      false,
      List.of("https://app.com/logout"),
      List.of("openid"));
    var readAttrs = new ClientAttributesConf(
      true, true, true, false, false,
      false, false, false, false, false,
      false, false, false, false, false,
      false, false, false, false);
    var writeAttrs = new ClientAttributesConf(
      false, false, true, false, false,
      false, false, false, false, false,
      false, false, false, false, false,
      false, false, false, false);

    var client = new UserPoolClientConf(
      "2h",
      authFlow,
      "1h",
      false,
      false,
      true,
      true,
      false,
      "2h",
      "mobile-client",
      oauth,
      readAttrs,
      writeAttrs,
      List.of(),
      "60d",
      Map.of());

    var json = Mapper.get().writeValueAsString(client);

    assertNotNull(json);
    assertTrue(json.contains("mobile-client"));
    assertTrue(json.contains("2h"));
  }

  @Test
  void testUserPoolClientConfDeserialization() throws Exception {
    var json = """
      {
        "accessTokenValidity": "1h",
        "authFlow": {
          "adminUserPassword": true,
          "custom": false,
          "userPassword": true,
          "userSrp": true
        },
        "authSessionValidity": "30m",
        "preventUserExistenceErrors": true,
        "disableOAuth": false,
        "enableTokenRevocation": true,
        "generateSecret": false,
        "enablePropagateAdditionalUserContextData": false,
        "idTokenValidity": "1h",
        "name": "test-client",
        "oauth": {
          "authorizationCodeGrant": true,
          "callbackUrls": ["https://test.com/auth"],
          "clientCredentials": false,
          "implicitCodeGrant": false,
          "logoutUrls": ["https://test.com/logout"],
          "scopes": ["openid"]
        },
        "readAttributes": {
          "address": true,
          "birthdate": true,
          "email": true,
          "email_verified": true,
          "family_name": true,
          "name": true,
          "gender": false,
          "given_name": true,
          "updated_at": false,
          "locale": false,
          "middle_name": false,
          "nickname": false,
          "phone_number": true,
          "phone_number_verified": true,
          "preferred_username": false,
          "profile_page": false,
          "profile_picture": false,
          "timezone": false,
          "website": false
        },
        "writeAttributes": {
          "address": false,
          "birthdate": false,
          "email": true,
          "email_verified": false,
          "family_name": true,
          "name": true,
          "gender": false,
          "given_name": true,
          "updated_at": false,
          "locale": false,
          "middle_name": false,
          "nickname": true,
          "phone_number": true,
          "phone_number_verified": false,
          "preferred_username": false,
          "profile_page": false,
          "profile_picture": false,
          "timezone": false,
          "website": false
        },
        "customAttributes": ["custom:role"],
        "refreshTokenValidity": "30d",
        "tags": {"Type": "web"}
      }
      """;

    var client = Mapper.get().readValue(json, UserPoolClientConf.class);

    assertNotNull(client);
    assertEquals("test-client", client.name());
    assertEquals("1h", client.accessTokenValidity());
    assertTrue(client.preventUserExistenceErrors());
    assertNotNull(client.authFlow());
    assertNotNull(client.oauth());
    assertEquals(1, client.customAttributes().size());
  }
}
