package fasti.sh.model.aws.eks.addon.managed;

import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.serialization.Mapper;
import fasti.sh.model.aws.kms.Kms;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for EKS managed addon model classes.
 */
class ManagedAddonModelsTest {

  @Test
  void testManagedAddonBuilder() {
    var addon = ManagedAddon
      .builder()
      .name("vpc-cni")
      .version("v1.12.0")
      .preserveOnDelete(false)
      .resolveConflicts("OVERWRITE")
      .configurationValues("{}")
      .tags(Map.of("Environment", "test"))
      .build();

    assertEquals("vpc-cni", addon.name());
    assertEquals("v1.12.0", addon.version());
    assertFalse(addon.preserveOnDelete());
    assertEquals("OVERWRITE", addon.resolveConflicts());
  }

  @Test
  void testManagedAddonInstantiation() {
    var addon = new ManagedAddon("{}", "coredns", false, "NONE", null, Map.of(), "v1.9.3");

    assertEquals("coredns", addon.name());
    assertEquals("v1.9.3", addon.version());
  }

  @Test
  void testManagedAddonSerialization() throws Exception {
    var addon = ManagedAddon
      .builder()
      .name("kube-proxy")
      .version("v1.27.1")
      .preserveOnDelete(true)
      .resolveConflicts("NONE")
      .build();

    var json = Mapper.get().writeValueAsString(addon);

    assertNotNull(json);
    assertTrue(json.contains("kube-proxy"));
    assertTrue(json.contains("v1.27.1"));
  }

  @Test
  void testManagedAddonDeserialization() throws Exception {
    var json = """
      {
        "name": "container-insights",
        "version": "v1.0.0",
        "preserveOnDelete": false,
        "resolveConflicts": "OVERWRITE",
        "configurationValues": "{}",
        "tags": {"Type": "monitoring"}
      }
      """;

    var addon = Mapper.get().readValue(json, ManagedAddon.class);

    assertNotNull(addon);
    assertEquals("container-insights", addon.name());
    assertEquals("v1.0.0", addon.version());
  }

  @Test
  void testAwsEbsCsiAddonInstantiation() {
    var kms = new Kms(
      "alias/ebs-encryption",
      "EBS encryption key",
      true,
      true,
      "encrypt_decrypt",
      "symmetric_default",
      "retain");

    var addon = new AwsEbsCsiAddon(kms, "gp3");

    assertNotNull(addon.kms());
    assertEquals("gp3", addon.defaultStorageClass());
    assertEquals("alias/ebs-encryption", addon.kms().alias());
  }

  @Test
  void testAwsEbsCsiAddonSerialization() throws Exception {
    var kms = new Kms(
      "alias/ebs-key",
      "EBS key",
      true,
      false,
      "encrypt_decrypt",
      "symmetric_default",
      "destroy");

    var addon = new AwsEbsCsiAddon(kms, "gp3");

    var json = Mapper.get().writeValueAsString(addon);

    assertNotNull(json);
    assertTrue(json.contains("gp3"));
    assertTrue(json.contains("alias/ebs-key"));
  }

  @Test
  void testManagedAddonsInstantiation() {
    var ebsCsi = new AwsEbsCsiAddon();

    var vpcCni = ManagedAddon
      .builder()
      .name("vpc-cni")
      .version("v1.12.0")
      .build();

    var coreDns = ManagedAddon
      .builder()
      .name("coredns")
      .version("v1.9.3")
      .build();

    var kubeProxy = ManagedAddon
      .builder()
      .name("kube-proxy")
      .version("v1.27.1")
      .build();

    var managedAddons = new ManagedAddons(
      ebsCsi,
      vpcCni,
      coreDns,
      kubeProxy,
      null,
      null);

    assertNotNull(managedAddons.awsEbsCsi());
    assertNotNull(managedAddons.awsVpcCni());
    assertNotNull(managedAddons.coreDns());
    assertNotNull(managedAddons.kubeProxy());
    assertNull(managedAddons.containerInsights());
    assertNull(managedAddons.podIdentityAgent());
  }

  @Test
  void testManagedAddonsSerialization() throws Exception {
    var vpcCni = ManagedAddon
      .builder()
      .name("vpc-cni")
      .version("v1.12.0")
      .build();

    var managedAddons = new ManagedAddons(null, vpcCni, null, null, null, null);

    var json = Mapper.get().writeValueAsString(managedAddons);

    assertNotNull(json);
    assertTrue(json.contains("vpc-cni") || json.contains("awsVpcCni"));
  }

  @Test
  void testManagedAddonsDeserialization() throws Exception {
    var json = """
      {
        "awsEbsCsi": {
          "name": "aws-ebs-csi-driver",
          "version": "v1.25.0",
          "kms": {
            "alias": "alias/ebs",
            "description": "EBS key",
            "enabled": true,
            "enableKeyRotation": false,
            "keyUsage": "encrypt_decrypt",
            "keySpec": "symmetric_default",
            "removalPolicy": "destroy"
          },
          "defaultStorageClass": "gp3"
        },
        "awsVpcCni": {
          "name": "vpc-cni",
          "version": "v1.12.0"
        },
        "coreDns": null,
        "kubeProxy": null,
        "containerInsights": null,
        "podIdentityAgent": null
      }
      """;

    var managedAddons = Mapper.get().readValue(json, ManagedAddons.class);

    assertNotNull(managedAddons);
    assertNotNull(managedAddons.awsEbsCsi());
    assertNotNull(managedAddons.awsVpcCni());
    assertEquals("gp3", managedAddons.awsEbsCsi().defaultStorageClass());
  }
}
