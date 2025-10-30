package fasti.sh.model.aws.vpc;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.model.aws.vpc.securitygroup.SecurityGroup;
import fasti.sh.model.aws.vpc.securitygroup.SecurityGroupIpRule;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for VPC Security Group model records.
 */
class SecurityGroupModelsTest {

  @Test
  void testSecurityGroupIpRuleBasic() {
    var rule = new SecurityGroupIpRule("0.0.0.0/0", 80, 80);

    assertEquals("0.0.0.0/0", rule.ip());
    assertEquals(80, rule.startPort());
    assertEquals(80, rule.endPort());
    assertRecordToString(rule);
  }

  @Test
  void testSecurityGroupIpRulePortRange() {
    var rule = new SecurityGroupIpRule("10.0.0.0/8", 8000, 9000);

    assertEquals("10.0.0.0/8", rule.ip());
    assertEquals(8000, rule.startPort());
    assertEquals(9000, rule.endPort());
  }

  @Test
  void testSecurityGroupIpRuleHttps() {
    var rule = new SecurityGroupIpRule("0.0.0.0/0", 443, 443);

    assertEquals(443, rule.startPort());
    assertEquals(443, rule.endPort());
  }

  @Test
  void testSecurityGroupIpRuleSsh() {
    var rule = new SecurityGroupIpRule("192.168.1.0/24", 22, 22);

    assertEquals("192.168.1.0/24", rule.ip());
    assertEquals(22, rule.startPort());
  }

  @Test
  void testSecurityGroupIpRuleEquality() {
    var rule1 = new SecurityGroupIpRule("10.0.0.0/8", 80, 80);
    var rule2 = new SecurityGroupIpRule("10.0.0.0/8", 80, 80);

    assertRecordEquality(rule1, rule2);
  }

  @Test
  void testSecurityGroupBasic() {
    var sg = new SecurityGroup(
      "web-sg",
      "Security group for web servers",
      false,
      true,
      List.of(),
      List.of(),
      Map.of());

    assertEquals("web-sg", sg.name());
    assertEquals("Security group for web servers", sg.description());
    assertFalse(sg.disableInlineRules());
    assertTrue(sg.allowAllOutbound());
    assertTrue(sg.ingressRules().isEmpty());
    assertTrue(sg.egressRules().isEmpty());
    assertRecordToString(sg);
  }

  @Test
  void testSecurityGroupWithIngressRules() {
    var httpRule = new SecurityGroupIpRule("0.0.0.0/0", 80, 80);
    var httpsRule = new SecurityGroupIpRule("0.0.0.0/0", 443, 443);

    var sg = new SecurityGroup(
      "web-sg",
      "Web server security group",
      false,
      true,
      List.of(httpRule, httpsRule),
      List.of(),
      Map.of("Environment", "production"));

    assertEquals(2, sg.ingressRules().size());
    assertEquals(80, sg.ingressRules().get(0).startPort());
    assertEquals(443, sg.ingressRules().get(1).startPort());
    assertEquals(1, sg.tags().size());
  }

  @Test
  void testSecurityGroupWithEgressRules() {
    var egressRule = new SecurityGroupIpRule("10.0.0.0/8", 3306, 3306);

    var sg = new SecurityGroup(
      "app-sg",
      "Application security group",
      false,
      false,
      List.of(),
      List.of(egressRule),
      Map.of());

    assertFalse(sg.allowAllOutbound());
    assertEquals(1, sg.egressRules().size());
    assertEquals(3306, sg.egressRules().get(0).startPort());
  }

  @Test
  void testSecurityGroupDisableInlineRules() {
    var sg = new SecurityGroup(
      "strict-sg",
      "Strict security group",
      true,
      false,
      List.of(),
      List.of(),
      Map.of());

    assertTrue(sg.disableInlineRules());
    assertFalse(sg.allowAllOutbound());
  }

  @Test
  void testSecurityGroupComplex() {
    var sshRule = new SecurityGroupIpRule("192.168.1.0/24", 22, 22);
    var httpRule = new SecurityGroupIpRule("0.0.0.0/0", 80, 80);
    var httpsRule = new SecurityGroupIpRule("0.0.0.0/0", 443, 443);
    var dbRule = new SecurityGroupIpRule("10.0.0.0/16", 5432, 5432);

    var sg = new SecurityGroup(
      "complex-sg",
      "Complex security group",
      false,
      false,
      List.of(sshRule, httpRule, httpsRule),
      List.of(dbRule),
      Map.of("Team", "platform", "Project", "main"));

    assertEquals(3, sg.ingressRules().size());
    assertEquals(1, sg.egressRules().size());
    assertEquals(2, sg.tags().size());
  }
}
