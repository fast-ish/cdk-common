package fasti.sh.model.aws.cognito.identitypool;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.model.aws.iam.IamRole;
import fasti.sh.model.aws.iam.Principal;
import fasti.sh.model.aws.iam.PrincipalType;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.services.cognito.identitypool.alpha.RoleMappingMatchType;

/**
 * Tests for Cognito Identity Pool model records.
 */
class IdentityPoolModelsTest {

  @Test
  void testRuleBasic() {
    var rule = new Rule(
      "cognito:groups",
      "admins",
      RoleMappingMatchType.EQUALS);

    assertEquals("cognito:groups", rule.claim());
    assertEquals("admins", rule.claimValue());
    assertEquals(RoleMappingMatchType.EQUALS, rule.matchType());
    assertRecordToString(rule);
  }

  @Test
  void testRuleContains() {
    var rule = new Rule(
      "custom:department",
      "engineering",
      RoleMappingMatchType.CONTAINS);

    assertEquals("custom:department", rule.claim());
    assertEquals("engineering", rule.claimValue());
    assertEquals(RoleMappingMatchType.CONTAINS, rule.matchType());
  }

  @Test
  void testRuleStartsWith() {
    var rule = new Rule(
      "custom:role",
      "admin",
      RoleMappingMatchType.STARTS_WITH);

    assertEquals(RoleMappingMatchType.STARTS_WITH, rule.matchType());
  }

  @Test
  void testRuleMappingConfBasic() {
    var rule = new Rule("cognito:groups", "users", RoleMappingMatchType.EQUALS);

    var mapping = new RoleMappingConf(
      "user-pool-client-id",
      true,
      false,
      List.of(rule));

    assertEquals("user-pool-client-id", mapping.key());
    assertTrue(mapping.useToken());
    assertFalse(mapping.resolveAmbiguousRoles());
    assertEquals(1, mapping.rules().size());
    assertRecordToString(mapping);
  }

  @Test
  void testRoleMappingConfMultipleRules() {
    var rule1 = new Rule("cognito:groups", "admins", RoleMappingMatchType.EQUALS);
    var rule2 = new Rule("cognito:groups", "moderators", RoleMappingMatchType.EQUALS);

    var mapping = new RoleMappingConf(
      "admin-pool-client",
      true,
      true,
      List.of(rule1, rule2));

    assertEquals(2, mapping.rules().size());
    assertTrue(mapping.resolveAmbiguousRoles());
  }

  @Test
  void testRoleMappingConfNoRules() {
    var mapping = new RoleMappingConf(
      "simple-client",
      false,
      false,
      List.of());

    assertFalse(mapping.useToken());
    assertTrue(mapping.rules().isEmpty());
  }

  @Test
  void testIdentityPoolConfBasic() {
    var principal = Principal
      .builder()
      .type(PrincipalType.FEDERATED)
      .value("cognito-identity.amazonaws.com")
      .conditions(Map.of())
      .build();

    var authenticatedRole = new IamRole(
      "authenticated-role",
      "Role for authenticated users",
      principal,
      List.of("AmazonS3ReadOnlyAccess"),
      List.of(),
      Map.of());

    var conf = new IdentityPoolConf(
      "my-identity-pool",
      authenticatedRole,
      false,
      false,
      false,
      List.of());

    assertEquals("my-identity-pool", conf.name());
    assertNotNull(conf.authenticated());
    assertEquals("authenticated-role", conf.authenticated().name());
    assertFalse(conf.allowClassicFlow());
    assertFalse(conf.allowUnauthenticatedIdentities());
    assertFalse(conf.disableServerSideTokenCheck());
    assertTrue(conf.userPoolRoleMappings().isEmpty());
    assertRecordToString(conf);
  }

  @Test
  void testIdentityPoolConfWithClassicFlow() {
    var principal = Principal
      .builder()
      .type(PrincipalType.FEDERATED)
      .value("cognito-identity.amazonaws.com")
      .conditions(Map.of())
      .build();

    var authenticatedRole = new IamRole(
      "classic-auth-role",
      "Classic flow role",
      principal,
      List.of(),
      List.of(),
      Map.of());

    var conf = new IdentityPoolConf(
      "classic-pool",
      authenticatedRole,
      true,
      false,
      false,
      List.of());

    assertTrue(conf.allowClassicFlow());
  }

  @Test
  void testIdentityPoolConfWithUnauthenticated() {
    var principal = Principal
      .builder()
      .type(PrincipalType.FEDERATED)
      .value("cognito-identity.amazonaws.com")
      .conditions(Map.of())
      .build();

    var authenticatedRole = new IamRole(
      "unauth-role",
      "Unauthenticated role",
      principal,
      List.of(),
      List.of(),
      Map.of());

    var conf = new IdentityPoolConf(
      "unauth-pool",
      authenticatedRole,
      false,
      true,
      false,
      List.of());

    assertTrue(conf.allowUnauthenticatedIdentities());
  }

  @Test
  void testIdentityPoolConfWithRoleMappings() {
    var principal = Principal
      .builder()
      .type(PrincipalType.FEDERATED)
      .value("cognito-identity.amazonaws.com")
      .conditions(Map.of())
      .build();

    var authenticatedRole = new IamRole(
      "mapped-role",
      "Role with mappings",
      principal,
      List.of("PowerUserAccess"),
      List.of(),
      Map.of());

    var rule = new Rule("cognito:groups", "admins", RoleMappingMatchType.EQUALS);
    var mapping = new RoleMappingConf("client-id", true, true, List.of(rule));

    var conf = new IdentityPoolConf(
      "mapped-pool",
      authenticatedRole,
      false,
      false,
      false,
      List.of(mapping));

    assertEquals(1, conf.userPoolRoleMappings().size());
    assertEquals("client-id", conf.userPoolRoleMappings().get(0).key());
  }

  @Test
  void testIdentityPoolConfDisableTokenCheck() {
    var principal = Principal
      .builder()
      .type(PrincipalType.FEDERATED)
      .value("cognito-identity.amazonaws.com")
      .conditions(Map.of())
      .build();

    var authenticatedRole = new IamRole(
      "no-check-role",
      "No token check role",
      principal,
      List.of(),
      List.of(),
      Map.of());

    var conf = new IdentityPoolConf(
      "no-check-pool",
      authenticatedRole,
      false,
      false,
      true,
      List.of());

    assertTrue(conf.disableServerSideTokenCheck());
  }
}
