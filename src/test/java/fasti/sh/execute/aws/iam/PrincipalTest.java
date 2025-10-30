package fasti.sh.execute.aws.iam;

import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.model.aws.iam.Principal;
import fasti.sh.model.aws.iam.PrincipalType;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for Principal utility class.
 */
class PrincipalTest {

  @Test
  void testServicePrincipal() {
    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("lambda.amazonaws.com")
      .conditions(Map.of())
      .build();

    var iamPrincipal = principal.iamPrincipal();

    assertNotNull(iamPrincipal);
  }

  @Test
  void testAccountPrincipal() {
    var principal = Principal
      .builder()
      .type(PrincipalType.ACCOUNT)
      .value("123456789012")
      .conditions(Map.of())
      .build();

    var iamPrincipal = principal.iamPrincipal();

    assertNotNull(iamPrincipal);
  }

  @Test
  void testAwsPrincipal() {
    var principal = Principal
      .builder()
      .type(PrincipalType.AWS)
      .value("123456789012")
      .conditions(Map.of())
      .build();

    var iamPrincipal = principal.iamPrincipal();

    assertNotNull(iamPrincipal);
  }

  @Test
  void testFederatedPrincipal() {
    var principal = Principal
      .builder()
      .type(PrincipalType.FEDERATED)
      .value("arn:aws:iam::123456789012:saml-provider/ExampleProvider")
      .action("sts:AssumeRoleWithSAML")
      .conditions(Map.of())
      .build();

    var iamPrincipal = principal.iamPrincipal();

    assertNotNull(iamPrincipal);
  }

  @Test
  void testArnPrincipal() {
    var principal = Principal
      .builder()
      .type(PrincipalType.ARN)
      .value("arn:aws:iam::123456789012:user/testuser")
      .conditions(Map.of())
      .build();

    var iamPrincipal = principal.iamPrincipal();

    assertNotNull(iamPrincipal);
  }

  @Test
  void testStarPrincipal() {
    var principal = Principal
      .builder()
      .type(PrincipalType.STAR)
      .value("*")
      .conditions(Map.of())
      .build();

    var iamPrincipal = principal.iamPrincipal();

    assertNotNull(iamPrincipal);
  }

  @Test
  void testPrincipalWithConditions() {
    Map<String, Object> conditions = Map
      .of(
        "StringEquals",
        Map
          .of(
            "aws:SourceAccount",
            "123456789012"));

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("s3.amazonaws.com")
      .conditions(conditions)
      .build();

    var iamPrincipal = principal.iamPrincipal();

    assertNotNull(iamPrincipal);
  }

  @Test
  void testCompositePrincipal() {
    var secondaryPrincipal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("ec2.amazonaws.com")
      .conditions(Map.of())
      .build();

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("lambda.amazonaws.com")
      .composite(List.of(secondaryPrincipal))
      .conditions(Map.of())
      .build();

    var iamPrincipal = principal.iamPrincipal();

    assertNotNull(iamPrincipal);
  }

  @Test
  void testCompositeWithMultiplePrincipals() {
    var principal2 = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("ec2.amazonaws.com")
      .conditions(Map.of())
      .build();

    var principal3 = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("ecs.amazonaws.com")
      .conditions(Map.of())
      .build();

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("lambda.amazonaws.com")
      .composite(List.of(principal2, principal3))
      .conditions(Map.of())
      .build();

    var iamPrincipal = principal.iamPrincipal();

    assertNotNull(iamPrincipal);
  }

  @Test
  void testPrincipalWithoutComposite() {
    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("lambda.amazonaws.com")
      .composite(null)
      .conditions(Map.of())
      .build();

    var iamPrincipal = principal.iamPrincipal();

    assertNotNull(iamPrincipal);
  }

  @Test
  void testPrincipalWithEmptyComposite() {
    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("lambda.amazonaws.com")
      .composite(List.of())
      .conditions(Map.of())
      .build();

    var iamPrincipal = principal.iamPrincipal();

    assertNotNull(iamPrincipal);
  }
}
