package fasti.sh.execute.aws;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.aws.msk.MskConstruct;
import fasti.sh.execute.aws.vpc.SecurityGroupConstruct;
import fasti.sh.execute.aws.vpc.VpcConstruct;
import fasti.sh.model.aws.msk.Msk;
import fasti.sh.model.aws.vpc.NetworkConf;
import fasti.sh.model.aws.vpc.Subnet;
import fasti.sh.model.aws.vpc.securitygroup.SecurityGroup;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.services.ec2.DefaultInstanceTenancy;
import software.amazon.awscdk.services.ec2.SubnetType;

/**
 * Tests for MSK (Managed Streaming for Kafka) constructs.
 */
class MskConstructsTest {

  @Test
  void testMskConstructBasic() {
    var ctx = createTestContext();

    // Create VPC first
    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "msk-vpc",
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

    // Create security group
    var sg = new SecurityGroup(
      "msk-sg",
      "MSK security group",
      false,
      true,
      List.of(),
      List.of(),
      Map.of());
    var sgConstruct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    var mskConf = new Msk(
      "test-msk-cluster",
      List.of(),
      Map.of("Type", "kafka"));

    var construct = new MskConstruct(
      ctx.scope(),
      ctx.common(),
      mskConf,
      vpcConstruct.vpc(),
      List.of(sgConstruct.securityGroup().getSecurityGroupId()));

    assertNotNull(construct);
    assertNotNull(construct.msk());
  }

  @Test
  void testMskConstructMultipleSecurityGroups() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "msk-multi-sg-vpc",
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

    var sg1 = new SecurityGroup(
      "msk-sg-1",
      "MSK security group 1",
      false,
      true,
      List.of(),
      List.of(),
      Map.of());
    var sgConstruct1 = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg1, vpcConstruct.vpc());

    var sg2 = new SecurityGroup(
      "msk-sg-2",
      "MSK security group 2",
      false,
      true,
      List.of(),
      List.of(),
      Map.of());
    var sgConstruct2 = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg2, vpcConstruct.vpc());

    var mskConf = new Msk(
      "multi-sg-cluster",
      List.of(),
      Map.of("Environment", "production"));

    var construct = new MskConstruct(
      ctx.scope(),
      ctx.common(),
      mskConf,
      vpcConstruct.vpc(),
      List
        .of(
          sgConstruct1.securityGroup().getSecurityGroupId(),
          sgConstruct2.securityGroup().getSecurityGroupId()));

    assertNotNull(construct);
    assertNotNull(construct.msk());
  }

  @Test
  void testMskConstructMultipleAvailabilityZones() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "msk-multi-az-vpc",
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

    var sg = new SecurityGroup(
      "msk-multi-az-sg",
      "Multi-AZ MSK security group",
      false,
      true,
      List.of(),
      List.of(),
      Map.of());
    var sgConstruct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    var mskConf = new Msk(
      "multi-az-cluster",
      List.of(),
      Map.of("HighAvailability", "true"));

    var construct = new MskConstruct(
      ctx.scope(),
      ctx.common(),
      mskConf,
      vpcConstruct.vpc(),
      List.of(sgConstruct.securityGroup().getSecurityGroupId()));

    assertNotNull(construct);
    assertNotNull(construct.msk());
  }

  @Test
  void testMskConstructThreeAvailabilityZones() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "msk-3az-vpc",
      "10.3.0.0/16",
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

    var sg = new SecurityGroup(
      "msk-3az-sg",
      "Three-AZ MSK security group",
      false,
      true,
      List.of(),
      List.of(),
      Map.of());
    var sgConstruct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    var mskConf = new Msk(
      "three-az-cluster",
      List.of(),
      Map.of("HighAvailability", "maximum", "AZs", "3"));

    var construct = new MskConstruct(
      ctx.scope(),
      ctx.common(),
      mskConf,
      vpcConstruct.vpc(),
      List.of(sgConstruct.securityGroup().getSecurityGroupId()));

    assertNotNull(construct);
    assertNotNull(construct.msk());
  }

  @Test
  void testMskConstructWithTags() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "msk-tags-vpc",
      "10.4.0.0/16",
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

    var sg = new SecurityGroup(
      "msk-tags-sg",
      "MSK with tags security group",
      false,
      true,
      List.of(),
      List.of(),
      Map.of());
    var sgConstruct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    var mskConf = new Msk(
      "tags-cluster",
      List.of(),
      Map.of("Environment", "dev", "Team", "platform", "Cost-Center", "engineering"));

    var construct = new MskConstruct(
      ctx.scope(),
      ctx.common(),
      mskConf,
      vpcConstruct.vpc(),
      List.of(sgConstruct.securityGroup().getSecurityGroupId()));

    assertNotNull(construct);
    assertNotNull(construct.msk());
  }

  @Test
  void testMskConstructProductionWorkload() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "msk-prod-vpc",
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

    var sg1 = new SecurityGroup(
      "msk-prod-broker-sg",
      "Production broker security group",
      false,
      true,
      List.of(),
      List.of(),
      Map.of());
    var sgConstruct1 = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg1, vpcConstruct.vpc());

    var sg2 = new SecurityGroup(
      "msk-prod-client-sg",
      "Production client security group",
      false,
      true,
      List.of(),
      List.of(),
      Map.of());
    var sgConstruct2 = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg2, vpcConstruct.vpc());

    var mskConf = new Msk(
      "production-cluster",
      List.of(),
      Map.of("Environment", "production", "Workload", "high-throughput"));

    var construct = new MskConstruct(
      ctx.scope(),
      ctx.common(),
      mskConf,
      vpcConstruct.vpc(),
      List
        .of(
          sgConstruct1.securityGroup().getSecurityGroupId(),
          sgConstruct2.securityGroup().getSecurityGroupId()));

    assertNotNull(construct);
    assertNotNull(construct.msk());
  }

  @Test
  void testMskConstructSingleAvailabilityZone() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "msk-single-az-vpc",
      "10.6.0.0/16",
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

    var sg = new SecurityGroup(
      "msk-single-az-sg",
      "Single AZ security group",
      false,
      true,
      List.of(),
      List.of(),
      Map.of());
    var sgConstruct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    var mskConf = new Msk(
      "single-az-cluster",
      List.of(),
      Map.of("AZs", "1", "Type", "dev"));

    var construct = new MskConstruct(
      ctx.scope(),
      ctx.common(),
      mskConf,
      vpcConstruct.vpc(),
      List.of(sgConstruct.securityGroup().getSecurityGroupId()));

    assertNotNull(construct);
    assertNotNull(construct.msk());
  }

  @Test
  void testMskConstructFourAvailabilityZones() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "msk-4az-vpc",
      "10.7.0.0/16",
      null,
      4,
      List.of(),
      List.of(publicSubnet, privateSubnet),
      List.of("us-east-1a", "us-east-1b", "us-east-1c", "us-east-1d"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var sg = new SecurityGroup(
      "msk-4az-sg",
      "Four-AZ MSK security group",
      false,
      true,
      List.of(),
      List.of(),
      Map.of());
    var sgConstruct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    var mskConf = new Msk(
      "four-az-cluster",
      List.of(),
      Map.of("HighAvailability", "ultra", "AZs", "4"));

    var construct = new MskConstruct(
      ctx.scope(),
      ctx.common(),
      mskConf,
      vpcConstruct.vpc(),
      List.of(sgConstruct.securityGroup().getSecurityGroupId()));

    assertNotNull(construct);
    assertNotNull(construct.msk());
  }
}
