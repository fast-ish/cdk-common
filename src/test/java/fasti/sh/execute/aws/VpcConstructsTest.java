package fasti.sh.execute.aws;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.aws.vpc.VpcConstruct;
import fasti.sh.model.aws.vpc.NetworkConf;
import fasti.sh.model.aws.vpc.Subnet;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.services.ec2.DefaultInstanceTenancy;
import software.amazon.awscdk.services.ec2.IpProtocol;
import software.amazon.awscdk.services.ec2.SubnetType;

/**
 * Tests for VPC constructs.
 */
class VpcConstructsTest {

  @Test
  void testVpcConstructBasic() {
    var ctx = createTestContext();

    var subnet1 = new Subnet("public-subnet-1", SubnetType.PUBLIC, 24, false, true, Map.of());
    var subnet2 = new Subnet("private-subnet-1", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());

    var networkConf = new NetworkConf(
      "test-vpc",
      "10.0.0.0/16",
      null, // ipProtocol
      1, // natGateways
      List.of(),
      List.of(subnet1, subnet2),
      List.of("us-east-1a", "us-east-1b"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());

    var construct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    assertNotNull(construct);
    assertNotNull(construct.vpc());
  }

  @Test
  void testVpcConstructWithMultipleNatGateways() {
    var ctx = createTestContext();

    var subnet1 = new Subnet("public-subnet", SubnetType.PUBLIC, 24, false, true, Map.of());
    var subnet2 = new Subnet("private-subnet", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());

    var networkConf = new NetworkConf(
      "ha-vpc",
      "10.1.0.0/16",
      null, // ipProtocol
      2, // multiple NAT gateways for HA
      List.of(),
      List.of(subnet1, subnet2),
      List.of("us-east-1a", "us-east-1b"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of("HA", "true"));

    var construct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    assertNotNull(construct);
    assertNotNull(construct.vpc());
  }

  @Test
  void testVpcConstructIsolatedSubnets() {
    var ctx = createTestContext();

    var subnet1 = new Subnet("isolated-subnet", SubnetType.PRIVATE_ISOLATED, 24, false, false, Map.of());

    var networkConf = new NetworkConf(
      "isolated-vpc",
      "10.2.0.0/16",
      null, // ipProtocol
      0, // no NAT gateways for isolated
      List.of(),
      List.of(subnet1),
      List.of("us-east-1a"),
      DefaultInstanceTenancy.DEFAULT,
      false, // no internet gateway
      true,
      true,
      Map.of("Type", "isolated"));

    var construct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    assertNotNull(construct);
    assertNotNull(construct.vpc());
  }

  @Test
  void testVpcConstructCustomCidr() {
    var ctx = createTestContext();

    var subnet = new Subnet("custom-subnet", SubnetType.PUBLIC, 20, false, true, Map.of());

    var networkConf = new NetworkConf(
      "custom-vpc",
      "172.16.0.0/16",
      null, // ipProtocol
      1,
      List.of(),
      List.of(subnet),
      List.of("us-east-1a"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());

    var construct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    assertNotNull(construct);
    assertNotNull(construct.vpc());
  }

  @Test
  void testVpcConstructDualStack() {
    var ctx = createTestContext();

    var subnet = new Subnet("dual-subnet", SubnetType.PUBLIC, 24, false, true, Map.of());

    var networkConf = new NetworkConf(
      "dual-stack-vpc",
      "10.5.0.0/16",
      IpProtocol.DUAL_STACK,
      1,
      List.of(),
      List.of(subnet),
      List.of("us-east-1a"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of("IPProtocol", "dual-stack"));

    var construct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    assertNotNull(construct);
    assertNotNull(construct.vpc());
  }

  @Test
  void testVpcConstructDedicatedTenancy() {
    var ctx = createTestContext();

    var subnet = new Subnet("dedicated-subnet", SubnetType.PUBLIC, 24, false, true, Map.of());

    var networkConf = new NetworkConf(
      "dedicated-vpc",
      "10.6.0.0/16",
      null,
      0,
      List.of(),
      List.of(subnet),
      List.of("us-east-1a"),
      DefaultInstanceTenancy.DEDICATED,
      true,
      true,
      true,
      Map.of("Tenancy", "dedicated"));

    var construct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    assertNotNull(construct);
    assertNotNull(construct.vpc());
  }

  @Test
  void testVpcConstructMultipleAvailabilityZones() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());

    var networkConf = new NetworkConf(
      "multi-az-vpc",
      "10.7.0.0/16",
      null,
      3, // NAT gateway per AZ
      List.of(),
      List.of(publicSubnet, privateSubnet),
      List.of("us-east-1a", "us-east-1b", "us-east-1c"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of("HighAvailability", "true"));

    var construct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    assertNotNull(construct);
    assertNotNull(construct.vpc());
  }

  @Test
  void testVpcConstructNoInternetGateway() {
    var ctx = createTestContext();

    var privateSubnet = new Subnet("private-only", SubnetType.PRIVATE_ISOLATED, 24, false, false, Map.of());

    var networkConf = new NetworkConf(
      "no-igw-vpc",
      "10.8.0.0/16",
      null,
      0,
      List.of(),
      List.of(privateSubnet),
      List.of("us-east-1a"),
      DefaultInstanceTenancy.DEFAULT,
      false, // no internet gateway
      true,
      true,
      Map.of("Type", "private-only"));

    var construct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    assertNotNull(construct);
    assertNotNull(construct.vpc());
  }

  @Test
  void testVpcConstructAllSubnetTypes() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var isolatedSubnet = new Subnet("isolated", SubnetType.PRIVATE_ISOLATED, 24, false, false, Map.of());

    var networkConf = new NetworkConf(
      "mixed-vpc",
      "10.9.0.0/16",
      null,
      1,
      List.of(),
      List.of(publicSubnet, privateSubnet, isolatedSubnet),
      List.of("us-east-1a"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of("Type", "mixed-subnets"));

    var construct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    assertNotNull(construct);
    assertNotNull(construct.vpc());
  }

  @Test
  void testVpcConstructDnsDisabled() {
    var ctx = createTestContext();

    var subnet = new Subnet("no-dns-subnet", SubnetType.PUBLIC, 24, false, true, Map.of());

    var networkConf = new NetworkConf(
      "no-dns-vpc",
      "10.10.0.0/16",
      null,
      0,
      List.of(),
      List.of(subnet),
      List.of("us-east-1a"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      false, // DNS hostnames disabled
      false, // DNS support disabled
      Map.of("DNS", "disabled"));

    var construct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    assertNotNull(construct);
    assertNotNull(construct.vpc());
  }

  @Test
  void testVpcConstructReservedSubnets() {
    var ctx = createTestContext();

    var regularSubnet = new Subnet("regular", SubnetType.PUBLIC, 24, false, true, Map.of());
    var reservedSubnet = new Subnet("reserved", SubnetType.PRIVATE_WITH_EGRESS, 24, true, false, Map.of("Reserved", "true"));

    var networkConf = new NetworkConf(
      "reserved-vpc",
      "10.11.0.0/16",
      null,
      1,
      List.of(),
      List.of(regularSubnet, reservedSubnet),
      List.of("us-east-1a"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());

    var construct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    assertNotNull(construct);
    assertNotNull(construct.vpc());
  }

  @Test
  void testVpcConstructDedicatedTenancyPublic() {
    var ctx = createTestContext();

    var subnet = new Subnet("dedicated-public", SubnetType.PUBLIC, 24, false, true, Map.of());

    var networkConf = new NetworkConf(
      "dedicated-public-vpc",
      "10.12.0.0/16",
      null,
      1,
      List.of(),
      List.of(subnet),
      List.of("us-east-1a"),
      DefaultInstanceTenancy.DEDICATED,
      true,
      true,
      true,
      Map.of("Tenancy", "dedicated-public"));

    var construct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    assertNotNull(construct);
    assertNotNull(construct.vpc());
  }

  @Test
  void testVpcConstructLargeCidr() {
    var ctx = createTestContext();

    var subnet = new Subnet("large-public", SubnetType.PUBLIC, 20, false, true, Map.of());

    var networkConf = new NetworkConf(
      "large-vpc",
      "10.0.0.0/8",
      null,
      1,
      List.of(),
      List.of(subnet),
      List.of("us-east-1a"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of("Size", "large"));

    var construct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    assertNotNull(construct);
    assertNotNull(construct.vpc());
  }

  @Test
  void testVpcConstructSmallCidr() {
    var ctx = createTestContext();

    var subnet = new Subnet("small-public", SubnetType.PUBLIC, 28, false, true, Map.of());

    var networkConf = new NetworkConf(
      "small-vpc",
      "10.20.0.0/28",
      null,
      1,
      List.of(),
      List.of(subnet),
      List.of("us-east-1a"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of("Size", "small"));

    var construct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    assertNotNull(construct);
    assertNotNull(construct.vpc());
  }

  @Test
  void testVpcConstructFourAzs() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());

    var networkConf = new NetworkConf(
      "four-az-vpc",
      "10.13.0.0/16",
      null,
      4,
      List.of(),
      List.of(publicSubnet, privateSubnet),
      List.of("us-east-1a", "us-east-1b", "us-east-1c", "us-east-1d"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of("AZs", "4"));

    var construct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    assertNotNull(construct);
    assertNotNull(construct.vpc());
  }
}
