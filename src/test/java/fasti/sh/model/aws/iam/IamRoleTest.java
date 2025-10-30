package fasti.sh.model.aws.iam;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;

/**
 * Tests for IamRole model utility methods.
 */
class IamRoleTest {

  @Test
  void testAddAssumeRoleStatementsSuccess() {
    var ctx = createTestContext();

    var role = Role.Builder
      .create(ctx.scope(), "test-role")
      .assumedBy(new ServicePrincipal("lambda.amazonaws.com"))
      .build();

    var principal1 = Role.Builder
      .create(ctx.scope(), "principal-role-1")
      .assumedBy(new ServicePrincipal("ec2.amazonaws.com"))
      .build();

    var principal2 = Role.Builder
      .create(ctx.scope(), "principal-role-2")
      .assumedBy(new ServicePrincipal("ecs.amazonaws.com"))
      .build();

    // Should not throw exception
    assertDoesNotThrow(
      () -> {
        IamRole.addAssumeRoleStatements(role, List.of(principal1, principal2));
      });
  }

  @Test
  void testAddAssumeRoleStatementsEmptyList() {
    var ctx = createTestContext();

    var role = Role.Builder
      .create(ctx.scope(), "test-role")
      .assumedBy(new ServicePrincipal("lambda.amazonaws.com"))
      .build();

    // Should not throw exception with empty list
    assertDoesNotThrow(
      () -> {
        IamRole.addAssumeRoleStatements(role, List.of());
      });
  }

  @Test
  void testAddAssumeRoleStatementsSinglePrincipal() {
    var ctx = createTestContext();

    var role = Role.Builder
      .create(ctx.scope(), "test-role")
      .assumedBy(new ServicePrincipal("lambda.amazonaws.com"))
      .build();

    var principal = Role.Builder
      .create(ctx.scope(), "principal-role")
      .assumedBy(new ServicePrincipal("ec2.amazonaws.com"))
      .build();

    assertDoesNotThrow(
      () -> {
        IamRole.addAssumeRoleStatements(role, List.of(principal));
      });
  }

  @Test
  void testIamRoleConstruction() {
    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("lambda.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "test-role",
      "Test role description",
      principal,
      List.of("AWSLambdaBasicExecutionRole"),
      List.of(),
      Map.of("Environment", "test"));

    assertNotNull(role);
    assertEquals("test-role", role.name());
    assertEquals("Test role description", role.description());
    assertEquals(1, role.managedPolicyNames().size());
    assertTrue(role.customPolicies().isEmpty());
    assertEquals(1, role.tags().size());
  }

  @Test
  void testIamRoleWithCustomPolicies() {
    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("lambda.amazonaws.com")
      .conditions(Map.of())
      .build();

    var policyConf = new PolicyConf(
      "s3-access-policy",
      "iam/s3-access-policy.json",
      Map.of());

    var role = new IamRole(
      "test-role",
      "Test role with custom policies",
      principal,
      List.of(),
      List.of(policyConf),
      Map.of());

    assertNotNull(role);
    assertEquals(1, role.customPolicies().size());
    assertTrue(role.managedPolicyNames().isEmpty());
  }

  @Test
  void testIamRoleWithMixedPolicies() {
    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("lambda.amazonaws.com")
      .conditions(Map.of())
      .build();

    var policyConf = new PolicyConf(
      "dynamodb-policy",
      "iam/dynamodb-policy.json",
      Map.of("table", "MyTable"));

    var role = new IamRole(
      "mixed-role",
      "Role with both managed and custom policies",
      principal,
      List.of("AWSLambdaBasicExecutionRole", "AmazonS3ReadOnlyAccess"),
      List.of(policyConf),
      Map.of("Environment", "production", "Team", "backend"));

    assertNotNull(role);
    assertEquals(2, role.managedPolicyNames().size());
    assertEquals(1, role.customPolicies().size());
    assertEquals(2, role.tags().size());
  }
}
