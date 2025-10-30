package fasti.sh.model.aws.vpc.securitygroup;

import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.serialization.Mapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for VPC Security Group model classes.
 */
class SecurityGroupModelsTest {

  @Test
  void testSecurityGroupIpRuleInstantiation() {
    var rule = new SecurityGroupIpRule("0.0.0.0/0", 80, 80);

    assertEquals("0.0.0.0/0", rule.ip());
    assertEquals(80, rule.startPort());
    assertEquals(80, rule.endPort());
  }

  @Test
  void testSecurityGroupIpRuleSerialization() throws Exception {
    var rule = new SecurityGroupIpRule("10.0.0.0/16", 443, 443);
    var json = Mapper.get().writeValueAsString(rule);

    assertNotNull(json);
    assertTrue(json.contains("10.0.0.0/16"));
    assertTrue(json.contains("443"));
  }

  @Test
  void testSecurityGroupIpRuleDeserialization() throws Exception {
    var json = """
      {"ip":"192.168.1.0/24","startPort":8080,"endPort":8090}
      """;
    var rule = Mapper.get().readValue(json, SecurityGroupIpRule.class);

    assertNotNull(rule);
    assertEquals("192.168.1.0/24", rule.ip());
    assertEquals(8080, rule.startPort());
    assertEquals(8090, rule.endPort());
  }

  @Test
  void testSecurityGroupInstantiation() {
    var ingressRule = new SecurityGroupIpRule("0.0.0.0/0", 443, 443);
    var egressRule = new SecurityGroupIpRule("0.0.0.0/0", 0, 65535);

    var sg = new SecurityGroup(
      "web-sg",
      "Web server security group",
      false,
      true,
      List.of(ingressRule),
      List.of(egressRule),
      Map.of("Environment", "production"));

    assertEquals("web-sg", sg.name());
    assertEquals("Web server security group", sg.description());
    assertFalse(sg.disableInlineRules());
    assertTrue(sg.allowAllOutbound());
    assertEquals(1, sg.ingressRules().size());
    assertEquals(1, sg.egressRules().size());
    assertEquals("production", sg.tags().get("Environment"));
  }

  @Test
  void testSecurityGroupSerialization() throws Exception {
    var ingressRule = new SecurityGroupIpRule("10.0.0.0/8", 22, 22);
    var sg = new SecurityGroup(
      "ssh-sg",
      "SSH access",
      true,
      false,
      List.of(ingressRule),
      List.of(),
      Map.of());

    var json = Mapper.get().writeValueAsString(sg);

    assertNotNull(json);
    assertTrue(json.contains("ssh-sg"));
    assertTrue(json.contains("SSH access"));
  }

  @Test
  void testSecurityGroupDeserialization() throws Exception {
    var json = """
      {
        "name": "app-sg",
        "description": "Application security group",
        "disableInlineRules": false,
        "allowAllOutbound": true,
        "ingressRules": [
          {"ip": "0.0.0.0/0", "startPort": 80, "endPort": 80},
          {"ip": "0.0.0.0/0", "startPort": 443, "endPort": 443}
        ],
        "egressRules": [],
        "tags": {"Type": "application"}
      }
      """;

    var sg = Mapper.get().readValue(json, SecurityGroup.class);

    assertNotNull(sg);
    assertEquals("app-sg", sg.name());
    assertEquals("Application security group", sg.description());
    assertTrue(sg.allowAllOutbound());
    assertEquals(2, sg.ingressRules().size());
    assertEquals(0, sg.egressRules().size());
    assertEquals("application", sg.tags().get("Type"));
  }

  @Test
  void testSecurityGroupWithMultipleRules() {
    var httpRule = new SecurityGroupIpRule("0.0.0.0/0", 80, 80);
    var httpsRule = new SecurityGroupIpRule("0.0.0.0/0", 443, 443);
    var sshRule = new SecurityGroupIpRule("10.0.0.0/16", 22, 22);

    var sg = new SecurityGroup(
      "multi-rule-sg",
      "Security group with multiple rules",
      false,
      true,
      List.of(httpRule, httpsRule, sshRule),
      List.of(),
      Map.of("Purpose", "web-server"));

    assertEquals(3, sg.ingressRules().size());
    assertEquals("multi-rule-sg", sg.name());
  }

  @Test
  void testSecurityGroupPortRange() {
    var rule = new SecurityGroupIpRule("192.168.0.0/16", 3000, 3100);

    assertEquals(3000, rule.startPort());
    assertEquals(3100, rule.endPort());
    assertTrue(rule.endPort() > rule.startPort());
  }
}
