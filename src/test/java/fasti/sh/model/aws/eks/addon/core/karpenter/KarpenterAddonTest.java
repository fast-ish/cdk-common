package fasti.sh.model.aws.eks.addon.core.karpenter;

import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.serialization.Mapper;
import fasti.sh.model.aws.eks.HelmChart;
import fasti.sh.model.aws.eks.PodIdentity;
import fasti.sh.model.aws.iam.IamRole;
import fasti.sh.model.aws.iam.Principal;
import fasti.sh.model.aws.iam.PrincipalType;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for Karpenter addon model class.
 */
class KarpenterAddonTest {

  @Test
  void testKarpenterAddonInstantiation() {
    var chart = new HelmChart(
      "karpenter",
      "karpenter",
      "karpenter",
      "https://charts.karpenter.sh",
      "values.yaml",
      "v0.32.0");

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("pods.eks.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "karpenter-role",
      "Karpenter controller role",
      principal,
      List.of("arn:aws:iam::aws:policy/KarpenterControllerPolicy"),
      List.of(),
      Map.of());

    var metadata = new ObjectMeta();
    metadata.setName("karpenter");
    metadata.setNamespace("karpenter");

    var podIdentity = new PodIdentity(metadata, role, Map.of());

    var addon = new KarpenterAddon(chart, podIdentity);

    assertNotNull(addon.chart());
    assertNotNull(addon.podIdentity());
    assertEquals("karpenter", addon.chart().name());
    assertEquals("karpenter-role", addon.podIdentity().role().name());
  }

  @Test
  void testKarpenterAddonSerialization() throws Exception {
    var chart = new HelmChart(
      "karpenter",
      "karpenter",
      "kube-system",
      "https://charts.karpenter.sh",
      "karpenter/values.yaml",
      "v0.31.0");

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("pods.eks.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "karpenter-controller",
      "Karpenter",
      principal,
      List.of(),
      List.of(),
      Map.of());

    var metadata = new ObjectMeta();
    metadata.setName("karpenter");
    metadata.setNamespace("karpenter");

    var podIdentity = new PodIdentity(metadata, role, Map.of());

    var addon = new KarpenterAddon(chart, podIdentity);

    var json = Mapper.get().writeValueAsString(addon);

    assertNotNull(json);
    assertTrue(json.contains("karpenter"));
  }

  @Test
  void testKarpenterAddonDeserialization() throws Exception {
    var json = """
      {
        "chart": {
          "name": "karpenter",
          "namespace": "karpenter",
          "release": "karpenter",
          "repository": "https://charts.karpenter.sh",
          "values": "values.yaml",
          "version": "v0.32.0"
        },
        "podIdentity": {
          "metadata": {
            "name": "karpenter",
            "namespace": "karpenter"
          },
          "role": {
            "name": "karpenter-role",
            "description": "Karpenter role",
            "principal": {
              "type": "SERVICE",
              "value": "pods.eks.amazonaws.com",
              "composite": [],
              "conditions": {}
            },
            "managedPolicyNames": [],
            "customPolicies": [],
            "tags": {}
          },
          "tags": {}
        }
      }
      """;

    var addon = Mapper.get().readValue(json, KarpenterAddon.class);

    assertNotNull(addon);
    assertNotNull(addon.chart());
    assertNotNull(addon.podIdentity());
    assertEquals("karpenter", addon.chart().name());
  }
}
