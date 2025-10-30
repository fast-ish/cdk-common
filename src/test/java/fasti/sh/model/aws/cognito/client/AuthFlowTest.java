package fasti.sh.model.aws.cognito.client;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.serialization.Mapper;
import org.junit.jupiter.api.Test;

class AuthFlowTest {

  @Test
  void testRecordInstantiationAllEnabled() {
    var authFlow = new AuthFlow(true, true, true, true);

    assertTrue(authFlow.adminUserPassword());
    assertTrue(authFlow.custom());
    assertTrue(authFlow.userPassword());
    assertTrue(authFlow.userSrp());
  }

  @Test
  void testRecordInstantiationAllDisabled() {
    var authFlow = new AuthFlow(false, false, false, false);

    assertFalse(authFlow.adminUserPassword());
    assertFalse(authFlow.custom());
    assertFalse(authFlow.userPassword());
    assertFalse(authFlow.userSrp());
  }

  @Test
  void testRecordInstantiationPartialEnabled() {
    var authFlow = new AuthFlow(true, false, true, false);

    assertTrue(authFlow.adminUserPassword());
    assertFalse(authFlow.custom());
    assertTrue(authFlow.userPassword());
    assertFalse(authFlow.userSrp());
  }

  @Test
  void testRecordWithOnlySrp() {
    var authFlow = new AuthFlow(false, false, false, true);

    assertFalse(authFlow.adminUserPassword());
    assertFalse(authFlow.custom());
    assertFalse(authFlow.userPassword());
    assertTrue(authFlow.userSrp());
  }

  @Test
  void testRecordWithOnlyCustom() {
    var authFlow = new AuthFlow(false, true, false, false);

    assertFalse(authFlow.adminUserPassword());
    assertTrue(authFlow.custom());
    assertFalse(authFlow.userPassword());
    assertFalse(authFlow.userSrp());
  }

  @Test
  void testRecordEquality() {
    var flow1 = new AuthFlow(true, false, true, true);
    var flow2 = new AuthFlow(true, false, true, true);

    assertRecordEquality(flow1, flow2);
  }

  @Test
  void testToString() {
    var authFlow = new AuthFlow(true, true, false, true);
    assertRecordToString(authFlow);
  }

  @Test
  void testSerialization() throws Exception {
    var authFlow = new AuthFlow(true, false, true, true);

    var json = Mapper.get().writeValueAsString(authFlow);
    assertNotNull(json);
    assertTrue(json.contains("true"));
    assertTrue(json.contains("false"));
  }

  @Test
  void testDeserialization() throws Exception {
    var json = """
      {
        "adminUserPassword": true,
        "custom": false,
        "userPassword": true,
        "userSrp": true
      }
      """;

    var authFlow = Mapper.get().readValue(json, AuthFlow.class);
    assertNotNull(authFlow);
    assertTrue(authFlow.adminUserPassword());
    assertFalse(authFlow.custom());
    assertTrue(authFlow.userPassword());
    assertTrue(authFlow.userSrp());
  }
}
