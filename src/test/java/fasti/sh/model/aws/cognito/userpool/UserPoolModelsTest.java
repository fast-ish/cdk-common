package fasti.sh.model.aws.cognito.userpool;

import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.serialization.Mapper;
import fasti.sh.model.aws.fn.Lambda;
import fasti.sh.model.aws.fn.LambdaLayer;
import fasti.sh.model.aws.iam.IamRole;
import fasti.sh.model.aws.iam.Principal;
import fasti.sh.model.aws.iam.PrincipalType;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.lambda.Runtime;

/**
 * Tests for Cognito User Pool model classes.
 */
class UserPoolModelsTest {

  // Enum Tests
  @Test
  void testStandardAttributeKeyEnum() {
    assertEquals(17, StandardAttributeKey.values().length);
    assertNotNull(StandardAttributeKey.valueOf("email"));
    assertNotNull(StandardAttributeKey.valueOf("phoneNumber"));
    assertNotNull(StandardAttributeKey.valueOf("address"));
  }

  @Test
  void testCustomAttributeTypeEnum() {
    assertEquals(3, CustomAttributeType.values().length);
    assertEquals(CustomAttributeType.STRING, CustomAttributeType.valueOf("STRING"));
    assertEquals(CustomAttributeType.NUMBER, CustomAttributeType.valueOf("NUMBER"));
    assertEquals(CustomAttributeType.DATE, CustomAttributeType.valueOf("DATE"));
  }

  // Simple Record Tests
  @Test
  void testAutoVerifyInstantiation() {
    var autoVerify = new AutoVerify(true, false);

    assertTrue(autoVerify.email());
    assertFalse(autoVerify.phone());
  }

  @Test
  void testAutoVerifySerializationDeserialization() throws Exception {
    var autoVerify = new AutoVerify(true, true);
    var json = Mapper.get().writeValueAsString(autoVerify);
    var deserialized = Mapper.get().readValue(json, AutoVerify.class);

    assertEquals(autoVerify.email(), deserialized.email());
    assertEquals(autoVerify.phone(), deserialized.phone());
  }

  @Test
  void testKeepOriginalAttributesInstantiation() {
    var keep = new KeepOriginalAttributes(true, false);

    assertTrue(keep.email());
    assertFalse(keep.phone());
  }

  @Test
  void testSignInAliasesInstantiation() {
    var aliases = new SignInAliases(true, true, false, false);

    assertTrue(aliases.username());
    assertTrue(aliases.email());
    assertFalse(aliases.phone());
    assertFalse(aliases.preferredUsername());
  }

  @Test
  void testSignInAliasesSerialization() throws Exception {
    var aliases = new SignInAliases(false, true, true, false);
    var json = Mapper.get().writeValueAsString(aliases);

    assertNotNull(json);
    assertTrue(json.contains("true"));
  }

  @Test
  void testMfaInstantiation() {
    var mfa = new Mfa("required", "Your code is {####}", true, false);

    assertEquals("required", mfa.type());
    assertEquals("Your code is {####}", mfa.message());
    assertTrue(mfa.sms());
    assertFalse(mfa.otp());
  }

  @Test
  void testMfaSerialization() throws Exception {
    var mfa = new Mfa("optional", "Code: {####}", false, true);
    var json = Mapper.get().writeValueAsString(mfa);
    var deserialized = Mapper.get().readValue(json, Mfa.class);

    assertEquals(mfa.type(), deserialized.type());
    assertEquals(mfa.message(), deserialized.message());
    assertEquals(mfa.sms(), deserialized.sms());
    assertEquals(mfa.otp(), deserialized.otp());
  }

  @Test
  void testUserAttributeInstantiation() {
    var attr = new UserAttribute(true, false);

    assertTrue(attr.required());
    assertFalse(attr.mutable());
  }

  @Test
  void testDeviceTrackingInstantiation() {
    var tracking = new DeviceTracking(true, false);

    assertTrue(tracking.newDeviceChallenge());
    assertFalse(tracking.rememberOnUserPrompt());
  }

  @Test
  void testPasswordPolicyInstantiation() {
    var policy = new PasswordPolicy(12, true, true, true, true, 7);

    assertEquals(12, policy.minLength());
    assertTrue(policy.requireLowercase());
    assertTrue(policy.requireUppercase());
    assertTrue(policy.requireDigits());
    assertTrue(policy.requireSymbols());
    assertEquals(7, policy.tempPasswordValidity());
  }

  @Test
  void testPasswordPolicySerialization() throws Exception {
    var policy = new PasswordPolicy(8, false, true, false, true, 3);
    var json = Mapper.get().writeValueAsString(policy);
    var deserialized = Mapper.get().readValue(json, PasswordPolicy.class);

    assertEquals(policy.minLength(), deserialized.minLength());
    assertEquals(policy.requireLowercase(), deserialized.requireLowercase());
    assertEquals(policy.requireUppercase(), deserialized.requireUppercase());
    assertEquals(policy.requireDigits(), deserialized.requireDigits());
    assertEquals(policy.requireSymbols(), deserialized.requireSymbols());
    assertEquals(policy.tempPasswordValidity(), deserialized.tempPasswordValidity());
  }

  @Test
  void testUserVerificationInstantiation() {
    var verification = new UserVerification(
      "Your code is {####}",
      "code",
      "Verify Email",
      "SMS: {####}");

    assertEquals("Your code is {####}", verification.emailBody());
    assertEquals("code", verification.emailStyle());
    assertEquals("Verify Email", verification.emailSubject());
    assertEquals("SMS: {####}", verification.smsMessage());
  }

  @Test
  void testCustomAttributeInstantiation() {
    var customAttr = new CustomAttribute("company", "string", 1, 100, true);

    assertEquals("company", customAttr.name());
    assertEquals("string", customAttr.type());
    assertEquals(1, customAttr.min());
    assertEquals(100, customAttr.max());
    assertTrue(customAttr.mutable());
  }

  @Test
  void testCustomAttributeSerialization() throws Exception {
    var customAttr = new CustomAttribute("age", "number", 0, 150, false);
    var json = Mapper.get().writeValueAsString(customAttr);

    assertNotNull(json);
    assertTrue(json.contains("age"));
    assertTrue(json.contains("number"));
  }

  @Test
  void testGroupInstantiation() {
    var group = new Group("admins", "Administrator group", 1);

    assertEquals("admins", group.name());
    assertEquals("Administrator group", group.description());
    assertEquals(1, group.precedence());
  }

  @Test
  void testGroupSerialization() throws Exception {
    var group = new Group("users", "Regular users", 10);
    var json = Mapper.get().writeValueAsString(group);
    var deserialized = Mapper.get().readValue(json, Group.class);

    assertEquals(group.name(), deserialized.name());
    assertEquals(group.description(), deserialized.description());
    assertEquals(group.precedence(), deserialized.precedence());
  }

  @Test
  void testSesConfInstantiation() {
    var sender = new fasti.sh.model.aws.ses.Sender(
      "us-east-1",
      "example.com",
      "Example",
      "noreply@example.com",
      "noreply@example.com",
      null);
    var ses = new SesConf(true, sender);

    assertTrue(ses.enabled());
    assertNotNull(ses.sender());
    assertEquals("noreply@example.com", ses.sender().fromEmail());
  }

  @Test
  void testSnsInstantiation() {
    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("cognito-idp.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "sns-role",
      "SNS role",
      principal,
      List.of(),
      List.of(),
      Map.of());

    var sns = new Sns(true, "external-123", role);

    assertTrue(sns.enabled());
    assertEquals("external-123", sns.externalId());
    assertNotNull(sns.role());
  }

  @Test
  void testTriggersInstantiation() {
    var layer = new LambdaLayer("base-layer", "layer.zip", RemovalPolicy.DESTROY, List.of("nodejs18.x"));

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("lambda.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "lambda-role",
      "Lambda execution role",
      principal,
      List.of(),
      List.of(),
      Map.of());

    var preSignUp = new Lambda(
      "pre-signup-fn",
      "Pre-signup function",
      "code.zip",
      "index.handler",
      "",
      30,
      512,
      Runtime.NODEJS_18_X,
      role,
      List.of(),
      List.of(),
      Map.of());

    var triggers = new Triggers(layer, preSignUp, null, null, null, null);

    assertNotNull(triggers.base());
    assertNotNull(triggers.preSignUp());
    assertNull(triggers.customMessage());
  }

  @Test
  void testUserPoolConfInstantiation() {
    var autoVerify = new AutoVerify(true, false);
    var mfa = new Mfa("optional", "Code: {####}", true, false);
    var passwordPolicy = new PasswordPolicy(8, true, true, true, true, 7);
    var verification = new UserVerification("Code: {####}", "code", "Verify", "SMS: {####}");
    var deviceTracking = new DeviceTracking(false, true);
    var aliases = new SignInAliases(true, true, false, false);
    var keep = new KeepOriginalAttributes(true, false);

    var userPoolConf = new UserPoolConf(
      "test-pool",
      "essentials",
      "",
      "email_only",
      "audit",
      "",
      "",
      List.of(),
      autoVerify,
      mfa,
      passwordPolicy,
      verification,
      deviceTracking,
      aliases,
      Map.of(),
      List.of(),
      keep,
      false,
      false,
      true,
      "destroy",
      Map.of());

    assertEquals("test-pool", userPoolConf.name());
    assertEquals("essentials", userPoolConf.featurePlan());
    assertNotNull(userPoolConf.autoVerify());
    assertNotNull(userPoolConf.mfa());
    assertNotNull(userPoolConf.passwordPolicy());
    assertTrue(userPoolConf.selfSignup());
  }

  @Test
  void testUserPoolConfSerialization() throws Exception {
    var autoVerify = new AutoVerify(true, true);
    var mfa = new Mfa("required", "MFA: {####}", true, true);
    var passwordPolicy = new PasswordPolicy(12, true, true, true, true, 3);
    var verification = new UserVerification("Click link", "link", "Verify", "SMS");
    var deviceTracking = new DeviceTracking(true, false);
    var aliases = new SignInAliases(false, true, true, false);
    var keep = new KeepOriginalAttributes(false, false);

    var userPoolConf = new UserPoolConf(
      "prod-pool",
      "plus",
      "triggers.yaml",
      "email_and_phone_without_mfa",
      "enforced",
      "sns.yaml",
      "ses.yaml",
      List.of(new Group("admin", "Admins", 1)),
      autoVerify,
      mfa,
      passwordPolicy,
      verification,
      deviceTracking,
      aliases,
      Map.of(StandardAttributeKey.email, new UserAttribute(true, true)),
      List.of(new CustomAttribute("company", "string", 1, 50, true)),
      keep,
      true,
      true,
      false,
      "retain",
      Map.of("Environment", "production"));

    var json = Mapper.get().writeValueAsString(userPoolConf);

    assertNotNull(json);
    assertTrue(json.contains("prod-pool"));
    assertTrue(json.contains("plus"));
  }

  @Test
  void testUserPoolConfDeserialization() throws Exception {
    var json = """
      {
        "name": "test-pool",
        "featurePlan": "essentials",
        "triggers": "",
        "accountRecovery": "email_only",
        "standardThreatProtectionMode": "audit",
        "sns": "",
        "ses": "",
        "groups": [],
        "autoVerify": {"email": true, "phone": false},
        "mfa": {"type": "optional", "message": "Code", "sms": true, "otp": false},
        "passwordPolicy": {
          "minLength": 8,
          "requireSymbols": true,
          "requireLowercase": true,
          "requireDigits": true,
          "tempPasswordValidity": 7
        },
        "verification": {
          "emailStyle": "code",
          "emailBody": "Code",
          "emailSubject": "Verify",
          "smsMessage": "SMS"
        },
        "deviceTracking": {"newDeviceChallenge": false, "rememberOnUserPrompt": true},
        "aliases": {"username": true, "email": true, "phone": false, "preferredUsername": false},
        "standardAttributes": {},
        "customAttributes": [],
        "keepOriginalAttributes": {"email": true, "phone": false},
        "signInCaseSensitive": false,
        "deletionProtection": false,
        "selfSignup": true,
        "removalPolicy": "destroy",
        "tags": {}
      }
      """;

    var userPoolConf = Mapper.get().readValue(json, UserPoolConf.class);

    assertNotNull(userPoolConf);
    assertEquals("test-pool", userPoolConf.name());
    assertEquals("essentials", userPoolConf.featurePlan());
    assertTrue(userPoolConf.selfSignup());
  }
}
