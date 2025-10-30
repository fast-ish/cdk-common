package fasti.sh.model.aws.vpc;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.services.ec2.DefaultInstanceTenancy;
import software.amazon.awscdk.services.ec2.SubnetType;

/**
 * Tests for VPC model records.
 */
class VpcModelsTest {

  @Test
  void testSubnetPublic() {
    var subnet = new Subnet("public-subnet", SubnetType.PUBLIC, 24, false, true, Map.of());

    assertEquals("public-subnet", subnet.name());
    assertEquals(SubnetType.PUBLIC, subnet.subnetType());
    assertEquals(24, subnet.cidrMask());
    assertTrue(subnet.mapPublicIpOnLaunch());
    assertFalse(subnet.reserved());
  }

  @Test
  void testSubnetPrivate() {
    var subnet = new Subnet("private-subnet", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());

    assertEquals(SubnetType.PRIVATE_WITH_EGRESS, subnet.subnetType());
    assertFalse(subnet.mapPublicIpOnLaunch());
  }

  @Test
  void testSubnetIsolated() {
    var subnet = new Subnet("isolated", SubnetType.PRIVATE_ISOLATED, 24, false, false, Map.of("Type", "db"));

    assertEquals(SubnetType.PRIVATE_ISOLATED, subnet.subnetType());
    assertEquals(1, subnet.tags().size());
  }

  @Test
  void testSubnetReserved() {
    var subnet = new Subnet("reserved", SubnetType.PUBLIC, 28, true, false, Map.of());

    assertTrue(subnet.reserved());
    assertEquals(28, subnet.cidrMask());
  }

  @Test
  void testNetworkConfBasic() {
    var subnet = new Subnet("subnet1", SubnetType.PUBLIC, 24, false, true, Map.of());
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

    assertEquals("test-vpc", networkConf.name());
    assertEquals("10.0.0.0/16", networkConf.cidr());
    assertEquals(1, networkConf.natGateways());
    assertTrue(networkConf.createInternetGateway());
    assertTrue(networkConf.enableDnsHostnames());
    assertTrue(networkConf.enableDnsSupport());
  }

  @Test
  void testNetworkConfMultipleAzs() {
    var subnet1 = new Subnet("s1", SubnetType.PUBLIC, 24, false, true, Map.of());
    var subnet2 = new Subnet("s2", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "multi-az-vpc",
      "172.16.0.0/16",
      null,
      3,
      List.of(),
      List.of(subnet1, subnet2),
      List.of("us-east-1a", "us-east-1b", "us-east-1c"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of("HA", "true"));

    assertEquals(3, networkConf.natGateways());
    assertEquals(3, networkConf.availabilityZones().size());
    assertEquals(2, networkConf.subnets().size());
  }
}
