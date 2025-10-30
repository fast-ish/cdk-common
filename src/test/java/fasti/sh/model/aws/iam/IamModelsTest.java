package fasti.sh.model.aws.iam;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for IAM model records.
 */
class IamModelsTest {

  @Test
  void testPrincipalService() {
    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("lambda.amazonaws.com")
      .conditions(Map.of())
      .build();

    assertEquals(PrincipalType.SERVICE, principal.type());
    assertEquals("lambda.amazonaws.com", principal.value());
    assertNotNull(principal.iamPrincipal());
  }

  @Test
  void testPrincipalAccount() {
    var principal = Principal
      .builder()
      .type(PrincipalType.ACCOUNT)
      .value("123456789012")
      .conditions(Map.of())
      .build();

    assertEquals(PrincipalType.ACCOUNT, principal.type());
    assertNotNull(principal.iamPrincipal());
  }

  @Test
  void testPrincipalArn() {
    var principal = Principal
      .builder()
      .type(PrincipalType.ARN)
      .value("arn:aws:iam::123456789012:role/MyRole")
      .conditions(Map.of())
      .build();

    assertEquals(PrincipalType.ARN, principal.type());
  }

  @Test
  void testPrincipalFederated() {
    var principal = Principal
      .builder()
      .type(PrincipalType.FEDERATED)
      .value("arn:aws:iam::123456789012:saml-provider/MySAML")
      .action("sts:AssumeRoleWithSAML")
      .conditions(Map.of())
      .build();

    assertEquals(PrincipalType.FEDERATED, principal.type());
    assertEquals("sts:AssumeRoleWithSAML", principal.action());
  }

  @Test
  void testPrincipalStar() {
    var principal = Principal
      .builder()
      .type(PrincipalType.STAR)
      .value("*")
      .conditions(Map.of())
      .build();

    assertEquals(PrincipalType.STAR, principal.type());
    assertNotNull(principal.iamPrincipal());
  }

  @Test
  void testIamRoleBasic() {
    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("ecs.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "ecs-task-role",
      "ECS task execution role",
      principal,
      List.of("service-role/AmazonECSTaskExecutionRolePolicy"),
      List.of(),
      Map.of("Service", "ecs"));

    assertEquals("ecs-task-role", role.name());
    assertEquals("ECS task execution role", role.description());
    assertEquals(1, role.managedPolicyNames().size());
  }

  @Test
  void testIamRoleWithMultiplePolicies() {
    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("ec2.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "ec2-role",
      "EC2 instance role",
      principal,
      List.of("AmazonSSMManagedInstanceCore", "CloudWatchAgentServerPolicy"),
      List.of(),
      Map.of());

    assertEquals(2, role.managedPolicyNames().size());
    assertRecordToString(role);
  }

  @Test
  void testPrincipalTypeValues() {
    assertTrue(PrincipalType.values().length >= 5);
    assertNotNull(PrincipalType.valueOf("SERVICE"));
    assertNotNull(PrincipalType.valueOf("AWS"));
    assertNotNull(PrincipalType.valueOf("ACCOUNT"));
    assertNotNull(PrincipalType.valueOf("FEDERATED"));
    assertNotNull(PrincipalType.valueOf("ARN"));
  }
}
