package fasti.sh.model.aws.cognito.client;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.serialization.Mapper;
import java.util.List;
import org.junit.jupiter.api.Test;

class CognitoOAuthTest {

  @Test
  void testRecordInstantiation() {
    var callbackUrls = List.of("https://example.com/callback");
    var logoutUrls = List.of("https://example.com/logout");
    var scopes = List.of("openid", "email", "profile");

    var oauth = new CognitoOAuth(
      true,
      callbackUrls,
      false,
      false,
      logoutUrls,
      scopes);

    assertTrue(oauth.authorizationCodeGrant());
    assertEquals(callbackUrls, oauth.callbackUrls());
    assertFalse(oauth.clientCredentials());
    assertFalse(oauth.implicitCodeGrant());
    assertEquals(logoutUrls, oauth.logoutUrls());
    assertEquals(scopes, oauth.scopes());
  }

  @Test
  void testRecordWithAllGrantsEnabled() {
    var oauth = new CognitoOAuth(
      true,
      List.of("https://example.com/callback"),
      true,
      true,
      List.of("https://example.com/logout"),
      List.of("openid"));

    assertTrue(oauth.authorizationCodeGrant());
    assertTrue(oauth.clientCredentials());
    assertTrue(oauth.implicitCodeGrant());
  }

  @Test
  void testRecordWithMultipleCallbackUrls() {
    var callbackUrls = List
      .of(
        "https://example.com/callback",
        "https://app.example.com/auth",
        "http://localhost:3000/callback");

    var oauth = new CognitoOAuth(
      true,
      callbackUrls,
      false,
      false,
      List.of(),
      List.of());

    assertEquals(3, oauth.callbackUrls().size());
    assertTrue(oauth.callbackUrls().contains("https://example.com/callback"));
  }

  @Test
  void testRecordWithEmptyLists() {
    var oauth = new CognitoOAuth(
      false,
      List.of(),
      false,
      false,
      List.of(),
      List.of());

    assertTrue(oauth.callbackUrls().isEmpty());
    assertTrue(oauth.logoutUrls().isEmpty());
    assertTrue(oauth.scopes().isEmpty());
  }

  @Test
  void testRecordEquality() {
    var oauth1 = new CognitoOAuth(
      true,
      List.of("https://example.com/callback"),
      false,
      false,
      List.of("https://example.com/logout"),
      List.of("openid"));
    var oauth2 = new CognitoOAuth(
      true,
      List.of("https://example.com/callback"),
      false,
      false,
      List.of("https://example.com/logout"),
      List.of("openid"));

    assertRecordEquality(oauth1, oauth2);
  }

  @Test
  void testToString() {
    var oauth = new CognitoOAuth(
      true,
      List.of("https://example.com/callback"),
      false,
      false,
      List.of(),
      List.of("openid"));
    assertRecordToString(oauth);
  }

  @Test
  void testSerialization() throws Exception {
    var oauth = new CognitoOAuth(
      true,
      List.of("https://example.com/callback"),
      false,
      true,
      List.of("https://example.com/logout"),
      List.of("openid", "email"));

    var json = Mapper.get().writeValueAsString(oauth);
    assertNotNull(json);
    assertTrue(json.contains("callback"));
    assertTrue(json.contains("openid"));
  }

  @Test
  void testDeserialization() throws Exception {
    var json = """
      {
        "authorizationCodeGrant": true,
        "callbackUrls": ["https://example.com/callback"],
        "clientCredentials": false,
        "implicitCodeGrant": false,
        "logoutUrls": ["https://example.com/logout"],
        "scopes": ["openid", "email"]
      }
      """;

    var oauth = Mapper.get().readValue(json, CognitoOAuth.class);
    assertNotNull(oauth);
    assertTrue(oauth.authorizationCodeGrant());
    assertEquals(1, oauth.callbackUrls().size());
    assertEquals(2, oauth.scopes().size());
  }
}
