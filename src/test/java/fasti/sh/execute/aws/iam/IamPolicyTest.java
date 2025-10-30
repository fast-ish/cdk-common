package fasti.sh.execute.aws.iam;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.model.aws.iam.PolicyStatementConf;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.services.iam.Effect;

/**
 * Tests for IAM Policy utility class.
 */
class IamPolicyTest {

  @Test
  void testPolicyStatementAllow() {
    var statement = new PolicyStatementConf(
      "AllowS3Access",
      "allow",
      List.of("s3:GetObject", "s3:PutObject"),
      List.of("arn:aws:s3:::my-bucket/*"),
      Map.of());

    var policyStatement = IamPolicy.policyStatement(statement);

    assertNotNull(policyStatement);
    assertEquals(Effect.ALLOW, policyStatement.getEffect());
  }

  @Test
  void testPolicyStatementDeny() {
    var statement = new PolicyStatementConf(
      "DenyS3Delete",
      "deny",
      List.of("s3:DeleteObject"),
      List.of("arn:aws:s3:::protected-bucket/*"),
      Map.of());

    var policyStatement = IamPolicy.policyStatement(statement);

    assertNotNull(policyStatement);
    assertEquals(Effect.DENY, policyStatement.getEffect());
  }

  @Test
  void testPolicyStatementWithConditions() {
    Map<String, Object> conditions = Map
      .of(
        "StringEquals",
        Map
          .of(
            "aws:PrincipalTag/Department",
            "Engineering"));

    var statement = new PolicyStatementConf(
      "DynamoDBAccess",
      "allow",
      List.of("dynamodb:GetItem"),
      List.of("arn:aws:dynamodb:us-east-1:123456789012:table/MyTable"),
      conditions);

    var policyStatement = IamPolicy.policyStatement(statement);

    assertNotNull(policyStatement);
    assertEquals(Effect.ALLOW, policyStatement.getEffect());
  }

  @Test
  void testPolicyStatementMultipleActions() {
    var statement = new PolicyStatementConf(
      "EC2Describe",
      "allow",
      List
        .of(
          "ec2:DescribeInstances",
          "ec2:DescribeVolumes",
          "ec2:DescribeSnapshots",
          "ec2:DescribeTags"),
      List.of("*"),
      Map.of());

    var policyStatement = IamPolicy.policyStatement(statement);

    assertNotNull(policyStatement);
    assertEquals(Effect.ALLOW, policyStatement.getEffect());
  }

  @Test
  void testPolicyStatementMultipleResources() {
    var statement = new PolicyStatementConf(
      "MultiBucketAccess",
      "allow",
      List.of("s3:GetObject"),
      List
        .of(
          "arn:aws:s3:::bucket-1/*",
          "arn:aws:s3:::bucket-2/*",
          "arn:aws:s3:::bucket-3/*"),
      Map.of());

    var policyStatement = IamPolicy.policyStatement(statement);

    assertNotNull(policyStatement);
    assertEquals(Effect.ALLOW, policyStatement.getEffect());
  }

  @Test
  void testPolicyStatementWildcardResource() {
    var statement = new PolicyStatementConf(
      "CloudWatchMetrics",
      "allow",
      List.of("cloudwatch:PutMetricData"),
      List.of("*"),
      Map.of());

    var policyStatement = IamPolicy.policyStatement(statement);

    assertNotNull(policyStatement);
    assertEquals(Effect.ALLOW, policyStatement.getEffect());
  }

  @Test
  void testPolicyStatementWithIpCondition() {
    Map<String, Object> conditions = Map
      .of(
        "IpAddress",
        Map
          .of(
            "aws:SourceIp",
            "192.168.1.0/24"));

    var statement = new PolicyStatementConf(
      "RestrictedIPAccess",
      "allow",
      List.of("s3:ListBucket"),
      List.of("arn:aws:s3:::my-bucket"),
      conditions);

    var policyStatement = IamPolicy.policyStatement(statement);

    assertNotNull(policyStatement);
    assertEquals(Effect.ALLOW, policyStatement.getEffect());
  }

  @Test
  void testPolicyStatementEmptyConditions() {
    var statement = new PolicyStatementConf(
      "CreateLogs",
      "allow",
      List.of("logs:CreateLogGroup"),
      List.of("arn:aws:logs:us-east-1:123456789012:*"),
      Map.of());

    var policyStatement = IamPolicy.policyStatement(statement);

    assertNotNull(policyStatement);
  }

  @Test
  void testPolicyStatementCaseSensitiveEffect() {
    var statementLower = new PolicyStatementConf(
      "AllowLowerCase",
      "allow",
      List.of("s3:GetObject"),
      List.of("*"),
      Map.of());

    var policyStatementLower = IamPolicy.policyStatement(statementLower);
    assertEquals(Effect.ALLOW, policyStatementLower.getEffect());

    var statementUpper = new PolicyStatementConf(
      "DenyUpperCase",
      "DENY",
      List.of("s3:DeleteObject"),
      List.of("*"),
      Map.of());

    var policyStatementUpper = IamPolicy.policyStatement(statementUpper);
    assertEquals(Effect.DENY, policyStatementUpper.getEffect());
  }

  @Test
  void testPolicyStatementsFromList() {
    var ctx = createTestContext();

    var statements = List
      .of(
        new PolicyStatementConf(
          "AllowRead",
          "allow",
          List.of("s3:GetObject"),
          List.of("arn:aws:s3:::bucket/*"),
          Map.of()),
        new PolicyStatementConf(
          "DenyDelete",
          "deny",
          List.of("s3:DeleteBucket"),
          List.of("arn:aws:s3:::bucket"),
          Map.of()));

    var policyStatements = statements
      .stream()
      .map(IamPolicy::policyStatement)
      .toList();

    assertEquals(2, policyStatements.size());
    assertEquals(Effect.ALLOW, policyStatements.get(0).getEffect());
    assertEquals(Effect.DENY, policyStatements.get(1).getEffect());
  }
}
