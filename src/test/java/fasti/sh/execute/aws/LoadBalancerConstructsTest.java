package fasti.sh.execute.aws;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.aws.elb.NetworkLoadBalancerConstruct;
import fasti.sh.execute.aws.vpc.VpcConstruct;
import fasti.sh.model.aws.loadbalancer.LoadBalancer;
import fasti.sh.model.aws.loadbalancer.TargetGroup;
import fasti.sh.model.aws.vpc.NetworkConf;
import fasti.sh.model.aws.vpc.Subnet;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.services.ec2.DefaultInstanceTenancy;
import software.amazon.awscdk.services.ec2.SubnetType;

/**
 * Tests for Load Balancer constructs.
 */
class LoadBalancerConstructsTest {

  @Test
  void testNetworkLoadBalancerBasic() {
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

    var targetGroup = new TargetGroup(null, false, null, "default-tg", 80, false, "TCP", "instance");
    var lbConf = new LoadBalancer(
      "test-nlb",
      targetGroup,
      true,
      false,
      false,
      Map.of());

    var construct = new NetworkLoadBalancerConstruct(ctx.scope(), ctx.common(), lbConf, vpcConstruct.vpc(), List.of());

    assertNotNull(construct);
    assertNotNull(construct.networkLoadBalancer());
  }

  @Test
  void testNetworkLoadBalancerWithDeletionProtection() {
    var ctx = createTestContext();

    var subnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var networkConf = new NetworkConf(
      "protected-vpc",
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

    var targetGroup = new TargetGroup(null, false, null, "protected-tg", 443, false, "TCP", "instance");
    var lbConf = new LoadBalancer(
      "protected-nlb",
      targetGroup,
      true,
      true,
      true, // deletionProtection
      Map.of("Protected", "true"));

    var construct = new NetworkLoadBalancerConstruct(ctx.scope(), ctx.common(), lbConf, vpcConstruct.vpc(), List.of());

    assertNotNull(construct);
    assertNotNull(construct.networkLoadBalancer());
  }

  @Test
  void testNetworkLoadBalancerInternal() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "internal-vpc",
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

    var targetGroup = new TargetGroup(null, false, null, "internal-tg", 8080, false, "TCP", "instance");
    var lbConf = new LoadBalancer(
      "internal-nlb",
      targetGroup,
      false, // internal
      true,
      false,
      Map.of("Type", "internal"));

    var construct = new NetworkLoadBalancerConstruct(ctx.scope(), ctx.common(), lbConf, vpcConstruct.vpc(), List.of());

    assertNotNull(construct);
    assertNotNull(construct.networkLoadBalancer());
  }

  @Test
  void testNetworkLoadBalancerWithHttpsTarget() {
    var ctx = createTestContext();

    var subnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var networkConf = new NetworkConf(
      "https-vpc",
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

    var targetGroup = new TargetGroup(null, false, null, "https-tg", 443, false, "TCP", "instance");
    var lbConf = new LoadBalancer(
      "https-nlb",
      targetGroup,
      true,
      true,
      false,
      Map.of("Protocol", "https"));

    var construct = new NetworkLoadBalancerConstruct(ctx.scope(), ctx.common(), lbConf, vpcConstruct.vpc(), List.of());

    assertNotNull(construct);
    assertNotNull(construct.networkLoadBalancer());
  }

  @Test
  void testNetworkLoadBalancerIpTarget() {
    var ctx = createTestContext();

    var subnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var networkConf = new NetworkConf(
      "ip-vpc",
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

    var targetGroup = new TargetGroup(null, false, null, "ip-tg", 8080, false, "TCP", "ip");
    var lbConf = new LoadBalancer(
      "ip-nlb",
      targetGroup,
      true,
      true,
      false,
      Map.of("TargetType", "ip"));

    var construct = new NetworkLoadBalancerConstruct(ctx.scope(), ctx.common(), lbConf, vpcConstruct.vpc(), List.of());

    assertNotNull(construct);
    assertNotNull(construct.networkLoadBalancer());
  }

  @Test
  void testNetworkLoadBalancerMultiAz() {
    var ctx = createTestContext();

    var subnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var networkConf = new NetworkConf(
      "multi-az-vpc",
      "10.5.0.0/16",
      null,
      3,
      List.of(),
      List.of(subnet),
      List.of("us-east-1a", "us-east-1b", "us-east-1c"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var targetGroup = new TargetGroup(null, false, null, "multi-az-tg", 80, false, "TCP", "instance");
    var lbConf = new LoadBalancer(
      "multi-az-nlb",
      targetGroup,
      true,
      false,
      false,
      Map.of("AZs", "3"));

    var construct = new NetworkLoadBalancerConstruct(ctx.scope(), ctx.common(), lbConf, vpcConstruct.vpc(), List.of());

    assertNotNull(construct);
    assertNotNull(construct.networkLoadBalancer());
  }

  @Test
  void testNetworkLoadBalancerCrossZoneEnabled() {
    var ctx = createTestContext();

    var subnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var networkConf = new NetworkConf(
      "crosszone-vpc",
      "10.6.0.0/16",
      null,
      2,
      List.of(),
      List.of(subnet),
      List.of("us-east-1a", "us-east-1b"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var targetGroup = new TargetGroup(null, false, null, "crosszone-tg", 443, false, "TCP", "instance");
    var lbConf = new LoadBalancer(
      "crosszone-nlb",
      targetGroup,
      true,
      false,
      true, // crossZoneEnabled
      Map.of("CrossZone", "enabled"));

    var construct = new NetworkLoadBalancerConstruct(ctx.scope(), ctx.common(), lbConf, vpcConstruct.vpc(), List.of());

    assertNotNull(construct);
    assertNotNull(construct.networkLoadBalancer());
  }

  @Test
  void testNetworkLoadBalancerCustomPort() {
    var ctx = createTestContext();

    var subnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var networkConf = new NetworkConf(
      "custom-port-vpc",
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

    var targetGroup = new TargetGroup(null, false, null, "custom-port-tg", 9000, false, "TCP", "instance");
    var lbConf = new LoadBalancer(
      "custom-port-nlb",
      targetGroup,
      true,
      false,
      false,
      Map.of("Port", "9000"));

    var construct = new NetworkLoadBalancerConstruct(ctx.scope(), ctx.common(), lbConf, vpcConstruct.vpc(), List.of());

    assertNotNull(construct);
    assertNotNull(construct.networkLoadBalancer());
  }
}
