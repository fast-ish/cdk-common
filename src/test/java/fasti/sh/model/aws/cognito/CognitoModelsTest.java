package fasti.sh.model.aws.cognito;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.model.aws.cognito.client.AuthFlow;
import fasti.sh.model.aws.cognito.userpool.AutoVerify;
import fasti.sh.model.aws.cognito.userpool.DeviceTracking;
import fasti.sh.model.aws.cognito.userpool.Group;
import fasti.sh.model.aws.cognito.userpool.Mfa;
import fasti.sh.model.aws.cognito.userpool.PasswordPolicy;
import fasti.sh.model.aws.cognito.userpool.SignInAliases;
import org.junit.jupiter.api.Test;

/**
 * Tests for Cognito model records.
 */
class CognitoModelsTest {

  @Test
  void testGroupBasic() {
    var group = new Group("admins", "Administrator group", 1);

    assertEquals("admins", group.name());
    assertEquals("Administrator group", group.description());
    assertEquals(1, group.precedence());
    assertRecordToString(group);
  }

  @Test
  void testGroupHighPrecedence() {
    var group = new Group("users", "Regular users", 10);

    assertEquals(10, group.precedence());
  }

  @Test
  void testGroupEquality() {
    var group1 = new Group("g1", "Group 1", 5);
    var group2 = new Group("g1", "Group 1", 5);

    assertRecordEquality(group1, group2);
  }

  @Test
  void testMfaOptional() {
    var mfa = new Mfa("OPTIONAL", "Your code is {####}", true, false);

    assertEquals("OPTIONAL", mfa.type());
    assertTrue(mfa.sms());
    assertFalse(mfa.otp());
    assertRecordToString(mfa);
  }

  @Test
  void testMfaRequired() {
    var mfa = new Mfa("REQUIRED", "Code: {####}", false, true);

    assertEquals("REQUIRED", mfa.type());
    assertFalse(mfa.sms());
    assertTrue(mfa.otp());
  }

  @Test
  void testMfaBoth() {
    var mfa = new Mfa("OPTIONAL", "Code: {####}", true, true);

    assertTrue(mfa.sms());
    assertTrue(mfa.otp());
  }

  @Test
  void testAutoVerifyEmail() {
    var autoVerify = new AutoVerify(true, false);

    assertTrue(autoVerify.email());
    assertFalse(autoVerify.phone());
    assertRecordToString(autoVerify);
  }

  @Test
  void testAutoVerifyPhone() {
    var autoVerify = new AutoVerify(false, true);

    assertFalse(autoVerify.email());
    assertTrue(autoVerify.phone());
  }

  @Test
  void testAutoVerifyBoth() {
    var autoVerify = new AutoVerify(true, true);

    assertTrue(autoVerify.email());
    assertTrue(autoVerify.phone());
  }

  @Test
  void testDeviceTrackingEnabled() {
    var tracking = new DeviceTracking(true, true);

    assertTrue(tracking.newDeviceChallenge());
    assertTrue(tracking.rememberOnUserPrompt());
    assertRecordToString(tracking);
  }

  @Test
  void testDeviceTrackingDisabled() {
    var tracking = new DeviceTracking(false, false);

    assertFalse(tracking.newDeviceChallenge());
    assertFalse(tracking.rememberOnUserPrompt());
  }

  @Test
  void testPasswordPolicyStrict() {
    var policy = new PasswordPolicy(12, true, true, true, true, 7);

    assertEquals(12, policy.minLength());
    assertTrue(policy.requireLowercase());
    assertTrue(policy.requireUppercase());
    assertTrue(policy.requireDigits());
    assertTrue(policy.requireSymbols());
    assertEquals(7, policy.tempPasswordValidity());
    assertRecordToString(policy);
  }

  @Test
  void testPasswordPolicyLenient() {
    var policy = new PasswordPolicy(8, false, false, false, false, 3);

    assertEquals(8, policy.minLength());
    assertFalse(policy.requireLowercase());
    assertFalse(policy.requireUppercase());
    assertFalse(policy.requireDigits());
    assertFalse(policy.requireSymbols());
    assertEquals(3, policy.tempPasswordValidity());
  }

  @Test
  void testPasswordPolicyMedium() {
    var policy = new PasswordPolicy(10, true, true, true, false, 5);

    assertTrue(policy.requireLowercase());
    assertTrue(policy.requireUppercase());
    assertTrue(policy.requireDigits());
    assertFalse(policy.requireSymbols());
  }

  @Test
  void testSignInAliasesUsername() {
    var aliases = new SignInAliases(true, false, false, false);

    assertTrue(aliases.username());
    assertFalse(aliases.email());
    assertFalse(aliases.phone());
    assertFalse(aliases.preferredUsername());
    assertRecordToString(aliases);
  }

  @Test
  void testSignInAliasesEmail() {
    var aliases = new SignInAliases(false, true, false, false);

    assertFalse(aliases.username());
    assertTrue(aliases.email());
  }

  @Test
  void testSignInAliasesAll() {
    var aliases = new SignInAliases(true, true, true, true);

    assertTrue(aliases.username());
    assertTrue(aliases.email());
    assertTrue(aliases.phone());
    assertTrue(aliases.preferredUsername());
  }

  @Test
  void testAuthFlowUserPassword() {
    var authFlow = new AuthFlow(false, false, true, false);

    assertFalse(authFlow.adminUserPassword());
    assertFalse(authFlow.custom());
    assertTrue(authFlow.userPassword());
    assertFalse(authFlow.userSrp());
    assertRecordToString(authFlow);
  }

  @Test
  void testAuthFlowSrp() {
    var authFlow = new AuthFlow(false, false, false, true);

    assertTrue(authFlow.userSrp());
    assertFalse(authFlow.userPassword());
  }

  @Test
  void testAuthFlowAdmin() {
    var authFlow = new AuthFlow(true, false, false, false);

    assertTrue(authFlow.adminUserPassword());
  }

  @Test
  void testAuthFlowCustom() {
    var authFlow = new AuthFlow(false, true, false, false);

    assertTrue(authFlow.custom());
  }

  @Test
  void testAuthFlowAll() {
    var authFlow = new AuthFlow(true, true, true, true);

    assertTrue(authFlow.adminUserPassword());
    assertTrue(authFlow.custom());
    assertTrue(authFlow.userPassword());
    assertTrue(authFlow.userSrp());
  }
}
