package fasti.sh.execute.aws;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.aws.cognito.UserPoolClientConstruct;
import fasti.sh.execute.aws.cognito.UserPoolConstruct;
import fasti.sh.execute.aws.vpc.VpcConstruct;
import fasti.sh.model.aws.vpc.NetworkConf;
import fasti.sh.model.aws.vpc.Subnet;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.services.ec2.DefaultInstanceTenancy;
import software.amazon.awscdk.services.ec2.SubnetType;

/**
 * Tests for AWS Cognito User Pool constructs.
 */
class CognitoConstructsTest {

  @BeforeAll
  static void setupAssets() throws IOException {
    // Create dummy asset files required by Cognito trigger Lambda layers
    new File("target").mkdirs();
    new File("target/test-layer.zip").createNewFile();
  }

  @Test
  void testUserPoolConstructBasic() {
    var ctx = createTestContext();

    // Create VPC for Lambda triggers
    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "userpool-vpc",
      "10.0.0.0/16",
      null,
      1,
      List.of(),
      List.of(publicSubnet, privateSubnet),
      List.of("us-east-1a"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    // Create user pool using template
    var construct = new UserPoolConstruct(
      ctx.scope(),
      ctx.common(),
      "auth/userpool.mustache",
      vpcConstruct.vpc());

    assertNotNull(construct);
    assertNotNull(construct.userPool());
    assertNotNull(construct.groups());
    assertFalse(construct.groups().isEmpty());
  }

  @Test
  void testUserPoolClientConstruct() {
    var ctx = createTestContext();

    // Create VPC
    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "client-vpc",
      "10.1.0.0/16",
      null,
      1,
      List.of(),
      List.of(publicSubnet, privateSubnet),
      List.of("us-east-1a"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    // Create user pool first
    var userPoolConstruct = new UserPoolConstruct(
      ctx.scope(),
      ctx.common(),
      "auth/userpool.mustache",
      vpcConstruct.vpc());

    // Create user pool client
    var clientConstruct = new UserPoolClientConstruct(
      ctx.scope(),
      ctx.common(),
      "auth/userpoolclient.mustache",
      userPoolConstruct.userPool());

    assertNotNull(clientConstruct);
    assertNotNull(clientConstruct.userPoolClient());
  }

  @Test
  void testUserPoolConstructMultipleAvailabilityZones() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "multi-az-userpool-vpc",
      "10.2.0.0/16",
      null,
      2,
      List.of(),
      List.of(publicSubnet, privateSubnet),
      List.of("us-east-1a", "us-east-1b"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var construct = new UserPoolConstruct(
      ctx.scope(),
      ctx.common(),
      "auth/userpool.mustache",
      vpcConstruct.vpc());

    assertNotNull(construct);
    assertNotNull(construct.userPool());
    assertNotNull(construct.groups());
  }

  @Test
  void testUserPoolClientConstructAlternativeVpc() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "alt-client-vpc",
      "10.3.0.0/16",
      null,
      1,
      List.of(),
      List.of(publicSubnet, privateSubnet),
      List.of("us-east-1a"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of("Type", "alternative"));
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var userPoolConstruct = new UserPoolConstruct(
      ctx.scope(),
      ctx.common(),
      "auth/userpool.mustache",
      vpcConstruct.vpc());

    var clientConstruct = new UserPoolClientConstruct(
      ctx.scope(),
      ctx.common(),
      "auth/userpoolclient.mustache",
      userPoolConstruct.userPool());

    assertNotNull(clientConstruct);
    assertNotNull(clientConstruct.userPoolClient());
  }

  @Test
  void testUserPoolConstructThreeAvailabilityZones() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "three-az-userpool-vpc",
      "10.4.0.0/16",
      null,
      3,
      List.of(),
      List.of(publicSubnet, privateSubnet),
      List.of("us-east-1a", "us-east-1b", "us-east-1c"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of("AZs", "3"));
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var construct = new UserPoolConstruct(
      ctx.scope(),
      ctx.common(),
      "auth/userpool.mustache",
      vpcConstruct.vpc());

    assertNotNull(construct);
    assertNotNull(construct.userPool());
    assertNotNull(construct.groups());
  }

  @Test
  void testUserPoolClientConstructThreeAz() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "client-3az-vpc",
      "10.5.0.0/16",
      null,
      3,
      List.of(),
      List.of(publicSubnet, privateSubnet),
      List.of("us-east-1a", "us-east-1b", "us-east-1c"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var userPoolConstruct = new UserPoolConstruct(
      ctx.scope(),
      ctx.common(),
      "auth/userpool.mustache",
      vpcConstruct.vpc());

    var clientConstruct = new UserPoolClientConstruct(
      ctx.scope(),
      ctx.common(),
      "auth/userpoolclient.mustache",
      userPoolConstruct.userPool());

    assertNotNull(clientConstruct);
    assertNotNull(clientConstruct.userPoolClient());
  }

  @Test
  void testUserPoolConstructIsolatedVpc() {
    var ctx = createTestContext();

    var isolatedSubnet = new Subnet("isolated", SubnetType.PRIVATE_ISOLATED, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "isolated-userpool-vpc",
      "10.6.0.0/16",
      null,
      0,
      List.of(),
      List.of(isolatedSubnet),
      List.of("us-east-1a"),
      DefaultInstanceTenancy.DEFAULT,
      false,
      true,
      true,
      Map.of("Type", "isolated"));
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var construct = new UserPoolConstruct(
      ctx.scope(),
      ctx.common(),
      "auth/userpool.mustache",
      vpcConstruct.vpc());

    assertNotNull(construct);
    assertNotNull(construct.userPool());
  }

  @Test
  void testUserPoolConstructLargerCidr() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 20, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 20, false, false, Map.of());
    var networkConf = new NetworkConf(
      "large-cidr-vpc",
      "10.0.0.0/8",
      null,
      1,
      List.of(),
      List.of(publicSubnet, privateSubnet),
      List.of("us-east-1a"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of("CIDR", "large"));
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var construct = new UserPoolConstruct(
      ctx.scope(),
      ctx.common(),
      "auth/userpool.mustache",
      vpcConstruct.vpc());

    assertNotNull(construct);
    assertNotNull(construct.userPool());
    assertNotNull(construct.groups());
  }

  @Test
  void testUserPoolClientConstructMultipleAzs() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "multi-client-vpc",
      "10.7.0.0/16",
      null,
      2,
      List.of(),
      List.of(publicSubnet, privateSubnet),
      List.of("us-east-1a", "us-east-1b"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var userPoolConstruct = new UserPoolConstruct(
      ctx.scope(),
      ctx.common(),
      "auth/userpool.mustache",
      vpcConstruct.vpc());

    var clientConstruct = new UserPoolClientConstruct(
      ctx.scope(),
      ctx.common(),
      "auth/userpoolclient.mustache",
      userPoolConstruct.userPool());

    assertNotNull(clientConstruct);
    assertNotNull(clientConstruct.userPoolClient());
  }

  @Test
  void testUserPoolConstructIsolatedSubnetsOnly() {
    var ctx = createTestContext();

    var isolatedSubnet = new Subnet("isolated", SubnetType.PRIVATE_ISOLATED, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "isolated-only-vpc",
      "10.8.0.0/16",
      null,
      0,
      List.of(),
      List.of(isolatedSubnet),
      List.of("us-east-1a"),
      DefaultInstanceTenancy.DEFAULT,
      false,
      true,
      true,
      Map.of("Subnets", "isolated-only"));
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var construct = new UserPoolConstruct(
      ctx.scope(),
      ctx.common(),
      "auth/userpool.mustache",
      vpcConstruct.vpc());

    assertNotNull(construct);
    assertNotNull(construct.userPool());
    assertNotNull(construct.groups());
  }
}
