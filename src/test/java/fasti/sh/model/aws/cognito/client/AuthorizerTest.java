package fasti.sh.model.aws.cognito.client;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.serialization.Mapper;
import org.junit.jupiter.api.Test;

class AuthorizerTest {

  @Test
  void testRecordInstantiation() {
    var authorizer = new Authorizer("cognito-authorizer");

    assertEquals("cognito-authorizer", authorizer.name());
  }

  @Test
  void testRecordWithDifferentNames() {
    var auth1 = new Authorizer("api-authorizer");
    var auth2 = new Authorizer("custom-authorizer");

    assertEquals("api-authorizer", auth1.name());
    assertEquals("custom-authorizer", auth2.name());
  }

  @Test
  void testRecordWithNullName() {
    var authorizer = new Authorizer(null);

    assertNull(authorizer.name());
  }

  @Test
  void testRecordEquality() {
    var auth1 = new Authorizer("test-auth");
    var auth2 = new Authorizer("test-auth");

    assertRecordEquality(auth1, auth2);
  }

  @Test
  void testToString() {
    var authorizer = new Authorizer("test-authorizer");
    assertRecordToString(authorizer);
  }

  @Test
  void testSerialization() throws Exception {
    var authorizer = new Authorizer("cognito-authorizer");

    var json = Mapper.get().writeValueAsString(authorizer);
    assertNotNull(json);
    assertTrue(json.contains("cognito-authorizer"));
  }

  @Test
  void testDeserialization() throws Exception {
    var json = """
      {
        "name": "api-authorizer"
      }
      """;

    var authorizer = Mapper.get().readValue(json, Authorizer.class);
    assertNotNull(authorizer);
    assertEquals("api-authorizer", authorizer.name());
  }
}
