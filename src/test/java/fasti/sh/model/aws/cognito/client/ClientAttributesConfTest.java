package fasti.sh.model.aws.cognito.client;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.serialization.Mapper;
import org.junit.jupiter.api.Test;

class ClientAttributesConfTest {

  @Test
  void testRecordInstantiationAllEnabled() {
    var attrs = new ClientAttributesConf(
      true, true, true, true, true, true, true, true, true,
      true, true, true, true, true, true, true, true, true, true);

    assertTrue(attrs.address());
    assertTrue(attrs.birthdate());
    assertTrue(attrs.email());
    assertTrue(attrs.email_verified());
    assertTrue(attrs.family_name());
    assertTrue(attrs.name());
    assertTrue(attrs.gender());
    assertTrue(attrs.given_name());
    assertTrue(attrs.updated_at());
    assertTrue(attrs.locale());
    assertTrue(attrs.middle_name());
    assertTrue(attrs.nickname());
    assertTrue(attrs.phone_number());
    assertTrue(attrs.phone_number_verified());
    assertTrue(attrs.preferred_username());
    assertTrue(attrs.profile_page());
    assertTrue(attrs.profile_picture());
    assertTrue(attrs.timezone());
    assertTrue(attrs.website());
  }

  @Test
  void testRecordInstantiationAllDisabled() {
    var attrs = new ClientAttributesConf(
      false, false, false, false, false, false, false, false, false,
      false, false, false, false, false, false, false, false, false, false);

    assertFalse(attrs.address());
    assertFalse(attrs.birthdate());
    assertFalse(attrs.email());
    assertFalse(attrs.email_verified());
    assertFalse(attrs.family_name());
    assertFalse(attrs.name());
    assertFalse(attrs.gender());
    assertFalse(attrs.given_name());
    assertFalse(attrs.updated_at());
    assertFalse(attrs.locale());
    assertFalse(attrs.middle_name());
    assertFalse(attrs.nickname());
    assertFalse(attrs.phone_number());
    assertFalse(attrs.phone_number_verified());
    assertFalse(attrs.preferred_username());
    assertFalse(attrs.profile_page());
    assertFalse(attrs.profile_picture());
    assertFalse(attrs.timezone());
    assertFalse(attrs.website());
  }

  @Test
  void testRecordInstantiationPartialEnabled() {
    var attrs = new ClientAttributesConf(
      true, false, true, true, false, true, false, true, false,
      false, false, true, true, true, false, false, false, true, false);

    assertTrue(attrs.address());
    assertFalse(attrs.birthdate());
    assertTrue(attrs.email());
    assertTrue(attrs.email_verified());
    assertFalse(attrs.family_name());
    assertTrue(attrs.name());
    assertFalse(attrs.gender());
    assertTrue(attrs.given_name());
  }

  @Test
  void testRecordEquality() {
    var attrs1 = new ClientAttributesConf(
      true, true, true, true, true, true, true, true, true,
      true, true, true, true, true, true, true, true, true, true);
    var attrs2 = new ClientAttributesConf(
      true, true, true, true, true, true, true, true, true,
      true, true, true, true, true, true, true, true, true, true);

    assertRecordEquality(attrs1, attrs2);
  }

  @Test
  void testToString() {
    var attrs = new ClientAttributesConf(
      true, false, true, false, true, false, true, false, true,
      false, true, false, true, false, true, false, true, false, true);
    assertRecordToString(attrs);
  }

  @Test
  void testSerialization() throws Exception {
    var attrs = new ClientAttributesConf(
      true, true, true, true, true, true, true, true, true,
      true, true, true, true, true, true, true, true, true, true);

    var json = Mapper.get().writeValueAsString(attrs);
    assertNotNull(json);
    assertTrue(json.contains("email"));
    assertTrue(json.contains("true"));
  }

  @Test
  void testDeserialization() throws Exception {
    var json = """
      {
        "address": true,
        "birthdate": false,
        "email": true,
        "email_verified": true,
        "family_name": false,
        "name": true,
        "gender": false,
        "given_name": true,
        "updated_at": false,
        "locale": false,
        "middle_name": false,
        "nickname": true,
        "phone_number": true,
        "phone_number_verified": true,
        "preferred_username": false,
        "profile_page": false,
        "profile_picture": false,
        "timezone": true,
        "website": false
      }
      """;

    var attrs = Mapper.get().readValue(json, ClientAttributesConf.class);
    assertNotNull(attrs);
    assertTrue(attrs.address());
    assertFalse(attrs.birthdate());
    assertTrue(attrs.email());
  }
}
