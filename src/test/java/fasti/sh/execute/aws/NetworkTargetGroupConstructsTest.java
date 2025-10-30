package fasti.sh.execute.aws;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.aws.elb.NetworkLoadBalancerConstruct;
import fasti.sh.execute.aws.elb.NetworkTargetGroupConstruct;
import fasti.sh.execute.aws.vpc.VpcConstruct;
import fasti.sh.model.aws.loadbalancer.HealthCheck;
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
 * Tests for Network Target Group constructs.
 */
class NetworkTargetGroupConstructsTest {

  @Test
  void testNetworkTargetGroupBasic() {
    var ctx = createTestContext();

    // Create VPC
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

    // Create Network Load Balancer
    var healthCheck = new HealthCheck(true, "200", "/health", "80", "HTTP");
    var targetGroup = new TargetGroup(null, false, healthCheck, "test-tg", 80, false, "TCP", "instance");
    var lbConf = new LoadBalancer(
      "test-nlb",
      targetGroup,
      true,
      false,
      false,
      Map.of());
    var nlbConstruct = new NetworkLoadBalancerConstruct(ctx.scope(), ctx.common(), lbConf, vpcConstruct.vpc(), List.of());

    // Create Target Group
    var tgConstruct = new NetworkTargetGroupConstruct(ctx.scope(), ctx.common(), lbConf, vpcConstruct.vpc(), nlbConstruct
      .networkLoadBalancer());

    assertNotNull(tgConstruct);
    assertNotNull(tgConstruct.networkTargetGroup());
    assertNotNull(tgConstruct.networkListener());
  }

  @Test
  void testNetworkTargetGroupWithHttpsHealthCheck() {
    var ctx = createTestContext();

    var subnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var networkConf = new NetworkConf(
      "https-vpc",
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

    var healthCheck = new HealthCheck(true, "200,201", "/api/health", "443", "HTTPS");
    var targetGroup = new TargetGroup(null, false, healthCheck, "https-tg", 443, true, "TCP", "instance");
    var lbConf = new LoadBalancer(
      "https-nlb",
      targetGroup,
      true,
      false,
      false,
      Map.of("Protocol", "HTTPS"));
    var nlbConstruct = new NetworkLoadBalancerConstruct(ctx.scope(), ctx.common(), lbConf, vpcConstruct.vpc(), List.of());

    var tgConstruct = new NetworkTargetGroupConstruct(ctx.scope(), ctx.common(), lbConf, vpcConstruct.vpc(), nlbConstruct
      .networkLoadBalancer());

    assertNotNull(tgConstruct);
    assertNotNull(tgConstruct.networkTargetGroup());
    assertNotNull(tgConstruct.networkListener());
  }

  @Test
  void testNetworkTargetGroupIpTarget() {
    var ctx = createTestContext();

    var subnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var networkConf = new NetworkConf(
      "ip-vpc",
      "10.2.0.0/16",
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

    var healthCheck = new HealthCheck(true, "200", "/", "8080", "HTTP");
    var targetGroup = new TargetGroup(null, false, healthCheck, "ip-tg", 8080, false, "TCP", "ip");
    var lbConf = new LoadBalancer(
      "ip-nlb",
      targetGroup,
      true,
      false,
      false,
      Map.of("TargetType", "ip"));
    var nlbConstruct = new NetworkLoadBalancerConstruct(ctx.scope(), ctx.common(), lbConf, vpcConstruct.vpc(), List.of());

    var tgConstruct = new NetworkTargetGroupConstruct(ctx.scope(), ctx.common(), lbConf, vpcConstruct.vpc(), nlbConstruct
      .networkLoadBalancer());

    assertNotNull(tgConstruct);
    assertNotNull(tgConstruct.networkTargetGroup());
    assertNotNull(tgConstruct.networkListener());
  }

  @Test
  void testNetworkTargetGroupWithConnectionTermination() {
    var ctx = createTestContext();

    var subnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var networkConf = new NetworkConf(
      "term-vpc",
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

    var healthCheck = new HealthCheck(true, "200", "/health", "3000", "HTTP");
    var targetGroup = new TargetGroup(null, true, healthCheck, "term-tg", 3000, true, "TCP", "instance");
    var lbConf = new LoadBalancer(
      "term-nlb",
      targetGroup,
      true,
      false,
      false,
      Map.of());
    var nlbConstruct = new NetworkLoadBalancerConstruct(ctx.scope(), ctx.common(), lbConf, vpcConstruct.vpc(), List.of());

    var tgConstruct = new NetworkTargetGroupConstruct(ctx.scope(), ctx.common(), lbConf, vpcConstruct.vpc(), nlbConstruct
      .networkLoadBalancer());

    assertNotNull(tgConstruct);
    assertNotNull(tgConstruct.networkTargetGroup());
    assertNotNull(tgConstruct.networkListener());
  }

  @Test
  void testNetworkTargetGroupCustomPort() {
    var ctx = createTestContext();

    var subnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var networkConf = new NetworkConf(
      "custom-vpc",
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

    var healthCheck = new HealthCheck(true, "200", "/status", "9090", "HTTP");
    var targetGroup = new TargetGroup(null, false, healthCheck, "custom-tg", 9090, false, "TCP", "instance");
    var lbConf = new LoadBalancer(
      "custom-nlb",
      targetGroup,
      true,
      false,
      false,
      Map.of("Port", "9090"));
    var nlbConstruct = new NetworkLoadBalancerConstruct(ctx.scope(), ctx.common(), lbConf, vpcConstruct.vpc(), List.of());

    var tgConstruct = new NetworkTargetGroupConstruct(ctx.scope(), ctx.common(), lbConf, vpcConstruct.vpc(), nlbConstruct
      .networkLoadBalancer());

    assertNotNull(tgConstruct);
    assertNotNull(tgConstruct.networkTargetGroup());
    assertNotNull(tgConstruct.networkListener());
  }

  @Test
  void testNetworkTargetGroupTcpHealthCheck() {
    var ctx = createTestContext();

    var subnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var networkConf = new NetworkConf(
      "tcp-vpc",
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

    var healthCheck = new HealthCheck(true, "200", "/health", "80", "TCP");
    var targetGroup = new TargetGroup(null, false, healthCheck, "tcp-tg", 80, false, "TCP", "instance");
    var lbConf = new LoadBalancer(
      "tcp-nlb",
      targetGroup,
      true,
      false,
      false,
      Map.of("HealthCheck", "TCP"));
    var nlbConstruct = new NetworkLoadBalancerConstruct(ctx.scope(), ctx.common(), lbConf, vpcConstruct.vpc(), List.of());

    var tgConstruct = new NetworkTargetGroupConstruct(ctx.scope(), ctx.common(), lbConf, vpcConstruct.vpc(), nlbConstruct
      .networkLoadBalancer());

    assertNotNull(tgConstruct);
    assertNotNull(tgConstruct.networkTargetGroup());
    assertNotNull(tgConstruct.networkListener());
  }

  @Test
  void testNetworkTargetGroupMultipleHealthCodes() {
    var ctx = createTestContext();

    var subnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var networkConf = new NetworkConf(
      "multicodes-vpc",
      "10.6.0.0/16",
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

    var healthCheck = new HealthCheck(true, "200,201,202,204", "/api/status", "8080", "HTTP");
    var targetGroup = new TargetGroup(null, false, healthCheck, "multicodes-tg", 8080, false, "TCP", "instance");
    var lbConf = new LoadBalancer(
      "multicodes-nlb",
      targetGroup,
      true,
      false,
      false,
      Map.of("HealthCodes", "multiple"));
    var nlbConstruct = new NetworkLoadBalancerConstruct(ctx.scope(), ctx.common(), lbConf, vpcConstruct.vpc(), List.of());

    var tgConstruct = new NetworkTargetGroupConstruct(ctx.scope(), ctx.common(), lbConf, vpcConstruct.vpc(), nlbConstruct
      .networkLoadBalancer());

    assertNotNull(tgConstruct);
    assertNotNull(tgConstruct.networkTargetGroup());
    assertNotNull(tgConstruct.networkListener());
  }
}
