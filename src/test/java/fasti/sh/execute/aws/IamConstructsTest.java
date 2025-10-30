package fasti.sh.execute.aws;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.aws.iam.RoleConstruct;
import fasti.sh.model.aws.iam.IamRole;
import fasti.sh.model.aws.iam.Principal;
import fasti.sh.model.aws.iam.PrincipalType;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.services.iam.ServicePrincipal;

/**
 * Tests for IAM constructs.
 */
class IamConstructsTest {

  @Test
  void testRoleConstructWithServicePrincipal() {
    var ctx = createTestContext();

    var principal = new ServicePrincipal("lambda.amazonaws.com");
    var roleConf = new IamRole(
      "lambda-execution-role",
      "Lambda function execution role",
      null,
      List.of("service-role/AWSLambdaBasicExecutionRole"),
      List.of(),
      Map.of("Service", "lambda"));

    var construct = new RoleConstruct(ctx.scope(), ctx.common(), principal, roleConf);

    assertNotNull(construct);
    assertNotNull(construct.role());
    // Role name may be tokenized by CDK
  }

  @Test
  void testRoleConstructWithPrincipalFromConfig() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("ecs-tasks.amazonaws.com")
      .conditions(Map.of())
      .build();

    var roleConf = new IamRole(
      "ecs-task-role",
      "ECS task execution role",
      principal,
      List.of("service-role/AmazonECSTaskExecutionRolePolicy"),
      List.of(),
      Map.of("Service", "ecs"));

    var construct = new RoleConstruct(ctx.scope(), ctx.common(), roleConf);

    assertNotNull(construct);
    assertNotNull(construct.role());
  }

  @Test
  void testRoleConstructWithMultipleManagedPolicies() {
    var ctx = createTestContext();

    var principal = new ServicePrincipal("ec2.amazonaws.com");
    var roleConf = new IamRole(
      "ec2-instance-role",
      "EC2 instance role with multiple policies",
      null,
      List
        .of(
          "AmazonSSMManagedInstanceCore",
          "CloudWatchAgentServerPolicy"),
      List.of(),
      Map.of());

    var construct = new RoleConstruct(ctx.scope(), ctx.common(), principal, roleConf);

    assertNotNull(construct);
    assertNotNull(construct.role());
  }

  @Test
  void testRoleConstructWithAccountPrincipal() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.ACCOUNT)
      .value("123456789012")
      .conditions(Map.of())
      .build();

    var roleConf = new IamRole(
      "cross-account-role",
      "Role for cross-account access",
      principal,
      List.of(),
      List.of(),
      Map.of("Access", "cross-account"));

    var construct = new RoleConstruct(ctx.scope(), ctx.common(), roleConf);

    assertNotNull(construct);
    assertNotNull(construct.role());
  }

  @Test
  void testRoleConstructWithStarPrincipal() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.STAR)
      .value("*")
      .conditions(Map.of())
      .build();

    var roleConf = new IamRole(
      "public-role",
      "Role with public access",
      principal,
      List.of(),
      List.of(),
      Map.of());

    var construct = new RoleConstruct(ctx.scope(), ctx.common(), roleConf);

    assertNotNull(construct);
    assertNotNull(construct.role());
  }

  @Test
  void testRoleConstructWithAwsPrincipal() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.AWS)
      .value("arn:aws:iam::123456789012:user/Developer")
      .conditions(Map.of())
      .build();

    var roleConf = new IamRole(
      "developer-role",
      "Role for developer user",
      principal,
      List.of("ReadOnlyAccess"),
      List.of(),
      Map.of("Department", "Engineering"));

    var construct = new RoleConstruct(ctx.scope(), ctx.common(), roleConf);

    assertNotNull(construct);
    assertNotNull(construct.role());
  }

  @Test
  void testRoleConstructWithArnPrincipal() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.ARN)
      .value("arn:aws:iam::987654321098:role/ExternalRole")
      .conditions(Map.of())
      .build();

    var roleConf = new IamRole(
      "external-access-role",
      "Role for external ARN",
      principal,
      List.of(),
      List.of(),
      Map.of("External", "true"));

    var construct = new RoleConstruct(ctx.scope(), ctx.common(), roleConf);

    assertNotNull(construct);
    assertNotNull(construct.role());
  }

  @Test
  void testRoleConstructWithFederatedPrincipal() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.FEDERATED)
      .value("arn:aws:iam::123456789012:saml-provider/ExampleProvider")
      .conditions(
        Map
          .of(
            "StringEquals",
            Map
              .of(
                "SAML:aud",
                "https://signin.aws.amazon.com/saml")))
      .build();

    var roleConf = new IamRole(
      "saml-federated-role",
      "Role for SAML federated users",
      principal,
      List.of("PowerUserAccess"),
      List.of(),
      Map.of("AuthType", "SAML"));

    var construct = new RoleConstruct(ctx.scope(), ctx.common(), roleConf);

    assertNotNull(construct);
    assertNotNull(construct.role());
  }

  @Test
  void testRoleConstructWithConditions() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("ec2.amazonaws.com")
      .conditions(
        Map
          .of(
            "StringEquals",
            Map
              .of(
                "sts:ExternalId",
                "unique-external-id-12345"),
            "IpAddress",
            Map
              .of(
                "aws:SourceIp",
                "203.0.113.0/24")))
      .build();

    var roleConf = new IamRole(
      "conditional-role",
      "Role with assume role conditions",
      principal,
      List.of("AmazonEC2ReadOnlyAccess"),
      List.of(),
      Map.of("Conditional", "true"));

    var construct = new RoleConstruct(ctx.scope(), ctx.common(), roleConf);

    assertNotNull(construct);
    assertNotNull(construct.role());
  }

  @Test
  void testRoleConstructWithNoManagedPolicies() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("lambda.amazonaws.com")
      .conditions(Map.of())
      .build();

    var roleConf = new IamRole(
      "minimal-role",
      "Role with no managed policies",
      principal,
      List.of(), // No managed policies
      List.of(),
      Map.of());

    var construct = new RoleConstruct(ctx.scope(), ctx.common(), roleConf);

    assertNotNull(construct);
    assertNotNull(construct.role());
  }

  @Test
  void testRoleConstructMultipleManagedPolicies() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("ec2.amazonaws.com")
      .conditions(Map.of())
      .build();

    var roleConf = new IamRole(
      "multi-policy-role",
      "Role with multiple managed policies",
      principal,
      List.of("AmazonEC2ReadOnlyAccess", "AmazonS3ReadOnlyAccess", "CloudWatchAgentServerPolicy"),
      List.of(),
      Map.of("Policies", "multiple"));

    var construct = new RoleConstruct(ctx.scope(), ctx.common(), roleConf);

    assertNotNull(construct);
    assertNotNull(construct.role());
  }

  @Test
  void testRoleConstructEcsTaskRole() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("ecs-tasks.amazonaws.com")
      .conditions(Map.of())
      .build();

    var roleConf = new IamRole(
      "ecs-task-role",
      "ECS task execution role",
      principal,
      List.of("service-role/AmazonECSTaskExecutionRolePolicy"),
      List.of(),
      Map.of("Service", "ecs"));

    var construct = new RoleConstruct(ctx.scope(), ctx.common(), roleConf);

    assertNotNull(construct);
    assertNotNull(construct.role());
  }

  @Test
  void testRoleConstructCodeBuildRole() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("codebuild.amazonaws.com")
      .conditions(Map.of())
      .build();

    var roleConf = new IamRole(
      "codebuild-role",
      "CodeBuild service role",
      principal,
      List.of("AWSCodeBuildDeveloperAccess", "CloudWatchLogsFullAccess"),
      List.of(),
      Map.of("Service", "codebuild"));

    var construct = new RoleConstruct(ctx.scope(), ctx.common(), roleConf);

    assertNotNull(construct);
    assertNotNull(construct.role());
  }

  @Test
  void testRoleConstructWithAccountCondition() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.AWS)
      .value("arn:aws:iam::123456789012:root")
      .conditions(
        Map
          .of(
            "StringEquals",
            Map.of("aws:PrincipalAccount", "123456789012")))
      .build();

    var roleConf = new IamRole(
      "account-role",
      "Role with account condition",
      principal,
      List.of("ReadOnlyAccess"),
      List.of(),
      Map.of("Condition", "account"));

    var construct = new RoleConstruct(ctx.scope(), ctx.common(), roleConf);

    assertNotNull(construct);
    assertNotNull(construct.role());
  }
}
