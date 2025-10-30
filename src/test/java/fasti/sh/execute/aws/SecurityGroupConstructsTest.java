package fasti.sh.execute.aws;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.aws.vpc.SecurityGroupConstruct;
import fasti.sh.execute.aws.vpc.VpcConstruct;
import fasti.sh.model.aws.vpc.NetworkConf;
import fasti.sh.model.aws.vpc.Subnet;
import fasti.sh.model.aws.vpc.securitygroup.SecurityGroup;
import fasti.sh.model.aws.vpc.securitygroup.SecurityGroupIpRule;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.services.ec2.DefaultInstanceTenancy;
import software.amazon.awscdk.services.ec2.SubnetType;

/**
 * Tests for Security Group constructs.
 */
class SecurityGroupConstructsTest {

  @Test
  void testSecurityGroupBasic() {
    var ctx = createTestContext();

    // Create VPC first
    var subnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var networkConf = new NetworkConf(
      "test-vpc",
      "10.0.0.0/16",
      null,
      1,
      List.of(),
      List.of(subnet),
      List.of("us-east-1a"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var sg = new SecurityGroup(
      "test-sg",
      "Test security group",
      false,
      true,
      List.of(),
      List.of(),
      Map.of());

    var construct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    assertNotNull(construct);
    assertNotNull(construct.securityGroup());
  }

  @Test
  void testSecurityGroupWithIngressRules() {
    var ctx = createTestContext();

    var subnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var networkConf = new NetworkConf(
      "vpc-with-sg",
      "10.1.0.0/16",
      null,
      1,
      List.of(),
      List.of(subnet),
      List.of("us-east-1a"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var httpRule = new SecurityGroupIpRule("0.0.0.0/0", 80, 80);
    var httpsRule = new SecurityGroupIpRule("0.0.0.0/0", 443, 443);
    var sg = new SecurityGroup(
      "web-sg",
      "Web server security group",
      false,
      true,
      List.of(httpRule, httpsRule),
      List.of(),
      Map.of("Type", "web"));

    var construct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    assertNotNull(construct);
    assertNotNull(construct.securityGroup());
  }

  @Test
  void testSecurityGroupWithEgressRules() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "private-vpc",
      "10.2.0.0/16",
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

    var dbRule = new SecurityGroupIpRule("10.2.0.0/16", 5432, 5432);
    var sg = new SecurityGroup(
      "app-sg",
      "App security group with egress",
      false,
      false,
      List.of(),
      List.of(dbRule),
      Map.of());

    var construct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    assertNotNull(construct);
    assertNotNull(construct.securityGroup());
  }

  @Test
  void testSecurityGroupWithPortRange() {
    var ctx = createTestContext();

    var subnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var networkConf = new NetworkConf(
      "range-vpc",
      "10.3.0.0/16",
      null,
      1,
      List.of(),
      List.of(subnet),
      List.of("us-east-1a"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var portRangeRule = new SecurityGroupIpRule("192.168.1.0/24", 8000, 9000);
    var sg = new SecurityGroup(
      "range-sg",
      "Security group with port range",
      false,
      true,
      List.of(portRangeRule),
      List.of(),
      Map.of("PortRange", "8000-9000"));

    var construct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    assertNotNull(construct);
    assertNotNull(construct.securityGroup());
  }

  @Test
  void testSecurityGroupDisableInlineRules() {
    var ctx = createTestContext();

    var subnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var networkConf = new NetworkConf(
      "inline-vpc",
      "10.4.0.0/16",
      null,
      1,
      List.of(),
      List.of(subnet),
      List.of("us-east-1a"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var sg = new SecurityGroup(
      "no-inline-sg",
      "Security group with inline rules disabled",
      true, // disableInlineRules
      true,
      List.of(),
      List.of(),
      Map.of());

    var construct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    assertNotNull(construct);
    assertNotNull(construct.securityGroup());
  }

  @Test
  void testSecurityGroupWithBothIngressAndEgress() {
    var ctx = createTestContext();

    var subnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var networkConf = new NetworkConf(
      "mixed-vpc",
      "10.5.0.0/16",
      null,
      1,
      List.of(),
      List.of(subnet),
      List.of("us-east-1a"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var ingressRule = new SecurityGroupIpRule("0.0.0.0/0", 22, 22);
    var egressRule = new SecurityGroupIpRule("0.0.0.0/0", 0, 65535);
    var sg = new SecurityGroup(
      "mixed-sg",
      "Security group with ingress and egress",
      false,
      true,
      List.of(ingressRule),
      List.of(egressRule),
      Map.of("Type", "mixed"));

    var construct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    assertNotNull(construct);
    assertNotNull(construct.securityGroup());
  }

  @Test
  void testSecurityGroupRestrictedAccess() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "restricted-vpc",
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

    var ingressRule = new SecurityGroupIpRule("10.6.0.0/24", 3306, 3306);
    var sg = new SecurityGroup(
      "restricted-sg",
      "Restricted database security group",
      false,
      false, // allow all outbound disabled
      List.of(ingressRule),
      List.of(),
      Map.of("Access", "restricted"));

    var construct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    assertNotNull(construct);
    assertNotNull(construct.securityGroup());
  }

  @Test
  void testSecurityGroupMultipleIngressRules() {
    var ctx = createTestContext();

    var subnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var networkConf = new NetworkConf(
      "multi-ingress-vpc",
      "10.7.0.0/16",
      null,
      1,
      List.of(),
      List.of(subnet),
      List.of("us-east-1a"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var httpRule = new SecurityGroupIpRule("0.0.0.0/0", 80, 80);
    var httpsRule = new SecurityGroupIpRule("0.0.0.0/0", 443, 443);
    var sshRule = new SecurityGroupIpRule("10.7.0.0/16", 22, 22);

    var sg = new SecurityGroup(
      "multi-ingress-sg",
      "Multiple ingress rules",
      false,
      true,
      List.of(httpRule, httpsRule, sshRule),
      List.of(),
      Map.of("Rules", "multiple"));

    var construct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    assertNotNull(construct);
    assertNotNull(construct.securityGroup());
  }

  @Test
  void testSecurityGroupPortRange() {
    var ctx = createTestContext();

    var subnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var networkConf = new NetworkConf(
      "port-range-vpc",
      "10.8.0.0/16",
      null,
      1,
      List.of(),
      List.of(subnet),
      List.of("us-east-1a"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var portRangeRule = new SecurityGroupIpRule("10.8.0.0/16", 8000, 9000);

    var sg = new SecurityGroup(
      "port-range-sg",
      "Port range security group",
      false,
      true,
      List.of(portRangeRule),
      List.of(),
      Map.of("Range", "8000-9000"));

    var construct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    assertNotNull(construct);
    assertNotNull(construct.securityGroup());
  }

  @Test
  void testSecurityGroupMultipleEgressRules() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "multi-egress-vpc",
      "10.9.0.0/16",
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

    var dbRule = new SecurityGroupIpRule("10.9.1.0/24", 5432, 5432);
    var cacheRule = new SecurityGroupIpRule("10.9.2.0/24", 6379, 6379);

    var sg = new SecurityGroup(
      "multi-egress-sg",
      "Multiple egress rules",
      false,
      false,
      List.of(),
      List.of(dbRule, cacheRule),
      Map.of("Egress", "multiple"));

    var construct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    assertNotNull(construct);
    assertNotNull(construct.securityGroup());
  }

  @Test
  void testSecurityGroupAllTraffic() {
    var ctx = createTestContext();

    var subnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var networkConf = new NetworkConf(
      "all-traffic-vpc",
      "10.10.0.0/16",
      null,
      1,
      List.of(),
      List.of(subnet),
      List.of("us-east-1a"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var allTrafficRule = new SecurityGroupIpRule("10.10.0.0/16", 0, 65535);

    var sg = new SecurityGroup(
      "all-traffic-sg",
      "All traffic security group",
      false,
      true,
      List.of(allTrafficRule),
      List.of(),
      Map.of("Traffic", "all"));

    var construct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    assertNotNull(construct);
    assertNotNull(construct.securityGroup());
  }
}
