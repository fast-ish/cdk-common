package fasti.sh.execute.aws.s3;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.model.aws.iam.Principal;
import fasti.sh.model.aws.iam.PrincipalType;
import fasti.sh.model.aws.s3.BucketPolicyConf;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.services.iam.Effect;

/**
 * Tests for S3 Bucket Policy utility class.
 */
class BucketPolicyTest {

  @Test
  void testPolicyStatementAllow() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.STAR)
      .value("*")
      .conditions(Map.of())
      .build();

    var conf = new BucketPolicyConf(
      "allow-public-read",
      List.of(principal),
      "s3/allow-public-read.json",
      Map.of());

    var policyStatement = BucketPolicy.policyStatement(ctx.scope(), conf);

    assertNotNull(policyStatement);
    assertEquals(Effect.ALLOW, policyStatement.getEffect());
  }

  @Test
  void testPolicyStatementDeny() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.STAR)
      .value("*")
      .conditions(Map.of())
      .build();

    var conf = new BucketPolicyConf(
      "deny-delete",
      List.of(principal),
      "s3/deny-delete.json",
      Map.of());

    var policyStatement = BucketPolicy.policyStatement(ctx.scope(), conf);

    assertNotNull(policyStatement);
    assertEquals(Effect.DENY, policyStatement.getEffect());
  }

  @Test
  void testPolicyStatementMultiplePrincipals() {
    var ctx = createTestContext();

    var principal1 = Principal
      .builder()
      .type(PrincipalType.AWS)
      .value("123456789012")
      .conditions(Map.of())
      .build();

    var principal2 = Principal
      .builder()
      .type(PrincipalType.AWS)
      .value("987654321098")
      .conditions(Map.of())
      .build();

    var conf = new BucketPolicyConf(
      "allow-multi-account",
      List.of(principal1, principal2),
      "s3/allow-public-read.json",
      Map.of());

    var policyStatement = BucketPolicy.policyStatement(ctx.scope(), conf);

    assertNotNull(policyStatement);
    assertEquals(Effect.ALLOW, policyStatement.getEffect());
  }

  @Test
  void testPolicyStatementServicePrincipal() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("cloudfront.amazonaws.com")
      .conditions(Map.of())
      .build();

    var conf = new BucketPolicyConf(
      "allow-cloudfront",
      List.of(principal),
      "s3/allow-public-read.json",
      Map.of());

    var policyStatement = BucketPolicy.policyStatement(ctx.scope(), conf);

    assertNotNull(policyStatement);
    assertEquals(Effect.ALLOW, policyStatement.getEffect());
  }

  @Test
  void testPolicyStatementAccountPrincipal() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.ACCOUNT)
      .value("123456789012")
      .conditions(Map.of())
      .build();

    var conf = new BucketPolicyConf(
      "allow-account",
      List.of(principal),
      "s3/allow-public-read.json",
      Map.of());

    var policyStatement = BucketPolicy.policyStatement(ctx.scope(), conf);

    assertNotNull(policyStatement);
    assertEquals(Effect.ALLOW, policyStatement.getEffect());
  }

  @Test
  void testPolicyStatementArnPrincipal() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.ARN)
      .value("arn:aws:iam::123456789012:user/testuser")
      .conditions(Map.of())
      .build();

    var conf = new BucketPolicyConf(
      "allow-arn",
      List.of(principal),
      "s3/allow-public-read.json",
      Map.of());

    var policyStatement = BucketPolicy.policyStatement(ctx.scope(), conf);

    assertNotNull(policyStatement);
  }

  @Test
  void testPolicyStatementFederatedPrincipal() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.FEDERATED)
      .value("arn:aws:iam::123456789012:oidc-provider/oidc.eks.us-east-1.amazonaws.com/id/EXAMPLE")
      .action("sts:AssumeRoleWithWebIdentity")
      .conditions(Map.of())
      .build();

    var conf = new BucketPolicyConf(
      "allow-federated",
      List.of(principal),
      "s3/allow-public-read.json",
      Map.of());

    var policyStatement = BucketPolicy.policyStatement(ctx.scope(), conf);

    assertNotNull(policyStatement);
  }

  @Test
  void testPolicyStatementCaseSensitiveEffect() {
    var ctx = createTestContext();

    var principal = Principal
      .builder()
      .type(PrincipalType.STAR)
      .value("*")
      .conditions(Map.of())
      .build();

    var confAllow = new BucketPolicyConf(
      "allow-lowercase",
      List.of(principal),
      "s3/allow-public-read.json",
      Map.of());

    var policyStatementAllow = BucketPolicy.policyStatement(ctx.scope(), confAllow);
    assertEquals(Effect.ALLOW, policyStatementAllow.getEffect());

    var confDeny = new BucketPolicyConf(
      "deny-uppercase",
      List.of(principal),
      "s3/deny-delete.json",
      Map.of());

    var policyStatementDeny = BucketPolicy.policyStatement(ctx.scope(), confDeny);
    assertEquals(Effect.DENY, policyStatementDeny.getEffect());
  }
}
