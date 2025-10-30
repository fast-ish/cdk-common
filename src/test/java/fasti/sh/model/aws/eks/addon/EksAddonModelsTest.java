package fasti.sh.model.aws.eks.addon;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.model.aws.eks.HelmChart;
import fasti.sh.model.aws.eks.PodIdentity;
import fasti.sh.model.aws.eks.ServiceAccountConf;
import fasti.sh.model.aws.eks.addon.core.*;
import fasti.sh.model.aws.eks.addon.core.karpenter.KarpenterAddon;
import fasti.sh.model.aws.eks.addon.managed.*;
import fasti.sh.model.aws.iam.IamRole;
import fasti.sh.model.aws.iam.Principal;
import fasti.sh.model.aws.iam.PrincipalType;
import fasti.sh.model.aws.kms.Kms;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for EKS addon model records and classes.
 */
class EksAddonModelsTest {

  @Test
  void testManagedAddonBasic() {
    var addon = ManagedAddon
      .builder()
      .name("vpc-cni")
      .version("v1.15.1")
      .preserveOnDelete(true)
      .resolveConflicts("OVERWRITE")
      .tags(Map.of("Type", "networking"))
      .build();

    assertEquals("vpc-cni", addon.name());
    assertEquals("v1.15.1", addon.version());
    assertTrue(addon.preserveOnDelete());
    assertEquals("OVERWRITE", addon.resolveConflicts());
    assertEquals("networking", addon.tags().get("Type"));
  }

  @Test
  void testManagedAddonWithServiceAccount() {
    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("eks.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "addon-role",
      "Addon service account role",
      principal,
      List.of("AmazonEKS_CNI_Policy"),
      List.of(),
      Map.of());

    var metadata = new ObjectMeta();
    metadata.setName("aws-node");
    metadata.setNamespace("kube-system");

    var serviceAccount = new ServiceAccountConf(metadata, role);

    var addon = ManagedAddon
      .builder()
      .name("vpc-cni")
      .version("v1.15.1")
      .serviceAccount(serviceAccount)
      .build();

    assertNotNull(addon.serviceAccount());
    assertEquals("aws-node", addon.serviceAccount().metadata().getName());
  }

  @Test
  void testAwsEbsCsiAddonBasic() {
    var kms = new Kms(
      "ebs-key",
      "EBS encryption key",
      true,
      false,
      "ENCRYPT_DECRYPT",
      "SYMMETRIC_DEFAULT",
      "DESTROY");

    var addon = new AwsEbsCsiAddon(kms, "gp3");

    assertNotNull(addon.kms());
    assertEquals("ebs-key", addon.kms().alias());
    assertEquals("gp3", addon.defaultStorageClass());
  }

  @Test
  void testAwsEbsCsiAddonWithoutKms() {
    var addon = new AwsEbsCsiAddon(null, "gp2");

    assertNull(addon.kms());
    assertEquals("gp2", addon.defaultStorageClass());
  }

  @Test
  void testManagedAddonsBasic() {
    var coreDns = ManagedAddon
      .builder()
      .name("coredns")
      .version("v1.10.1")
      .build();

    var kubeProxy = ManagedAddon
      .builder()
      .name("kube-proxy")
      .version("v1.28.2")
      .build();

    var addons = new ManagedAddons(
      null,
      null,
      coreDns,
      kubeProxy,
      null,
      null);

    assertNotNull(addons.coreDns());
    assertNotNull(addons.kubeProxy());
    assertEquals("coredns", addons.coreDns().name());
    assertEquals("kube-proxy", addons.kubeProxy().name());
    assertNull(addons.awsEbsCsi());
    assertRecordToString(addons);
  }

  @Test
  void testManagedAddonsComplete() {
    var kms = new Kms(
      "ebs-key",
      "EBS encryption key",
      true,
      false,
      "ENCRYPT_DECRYPT",
      "SYMMETRIC_DEFAULT",
      "DESTROY");

    var ebsCsi = new AwsEbsCsiAddon(kms, "gp3");

    var vpcCni = ManagedAddon
      .builder()
      .name("vpc-cni")
      .version("v1.15.1")
      .build();

    var coreDns = ManagedAddon
      .builder()
      .name("coredns")
      .version("v1.10.1")
      .build();

    var kubeProxy = ManagedAddon
      .builder()
      .name("kube-proxy")
      .version("v1.28.2")
      .build();

    var containerInsights = ManagedAddon
      .builder()
      .name("amazon-cloudwatch-observability")
      .version("v1.5.0")
      .build();

    var podIdentityAgent = ManagedAddon
      .builder()
      .name("eks-pod-identity-agent")
      .version("v1.0.0")
      .build();

    var addons = new ManagedAddons(
      ebsCsi,
      vpcCni,
      coreDns,
      kubeProxy,
      containerInsights,
      podIdentityAgent);

    assertNotNull(addons.awsEbsCsi());
    assertNotNull(addons.awsVpcCni());
    assertNotNull(addons.coreDns());
    assertNotNull(addons.kubeProxy());
    assertNotNull(addons.containerInsights());
    assertNotNull(addons.podIdentityAgent());
  }

  @Test
  void testCertManagerAddonBasic() {
    var chart = new HelmChart(
      "cert-manager",
      "cert-manager",
      "cert-manager-release",
      "https://charts.jetstack.io",
      null,
      "v1.13.2");

    var addon = new CertManagerAddon(chart);

    assertNotNull(addon.chart());
    assertEquals("cert-manager", addon.chart().name());
    assertEquals("cert-manager", addon.chart().namespace());
    assertRecordToString(addon);
  }

  @Test
  void testAwsLoadBalancerAddonBasic() {
    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("eks.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "alb-controller-role",
      "ALB controller role",
      principal,
      List.of("AWSLoadBalancerControllerIAMPolicy"),
      List.of(),
      Map.of());

    var metadata = new ObjectMeta();
    metadata.setName("aws-load-balancer-controller");
    metadata.setNamespace("kube-system");

    var serviceAccount = new ServiceAccountConf(metadata, role);

    var chart = new HelmChart(
      "aws-load-balancer-controller",
      "kube-system",
      "aws-load-balancer-controller-release",
      "https://aws.github.io/eks-charts",
      null,
      "1.6.2");

    var addon = new AwsLoadBalancerAddon(chart, serviceAccount);

    assertNotNull(addon.chart());
    assertNotNull(addon.serviceAccount());
    assertEquals("aws-load-balancer-controller", addon.chart().name());
    assertEquals("alb-controller-role", addon.serviceAccount().role().name());
    assertRecordToString(addon);
  }

  @Test
  void testAwsSecretsStoreAddonBasic() {
    var chart = new HelmChart(
      "secrets-store-csi-driver",
      "kube-system",
      "secrets-store-csi-driver-release",
      "https://kubernetes-sigs.github.io/secrets-store-csi-driver/charts",
      null,
      "1.4.0");

    var addon = new AwsSecretsStoreAddon(chart);

    assertNotNull(addon.chart());
    assertEquals("secrets-store-csi-driver", addon.chart().name());
    assertRecordToString(addon);
  }

  @Test
  void testGrafanaSecretBasic() {
    var secret = new GrafanaSecret(
      "my-secret-key",
      "https://loki.example.com",
      "loki-user",
      "https://prometheus.example.com",
      "prom-user",
      "https://tempo.example.com",
      "tempo-user",
      "instance-123",
      "https://pyroscope.example.com");

    assertEquals("my-secret-key", secret.key());
    assertEquals("https://loki.example.com", secret.lokiHost());
    assertEquals("loki-user", secret.lokiUsername());
    assertEquals("https://prometheus.example.com", secret.prometheusHost());
    assertEquals("prom-user", secret.prometheusUsername());
    assertEquals("https://tempo.example.com", secret.tempoHost());
    assertEquals("tempo-user", secret.tempoUsername());
    assertEquals("instance-123", secret.instanceId());
    assertEquals("https://pyroscope.example.com", secret.pyroscopeHost());
    assertRecordToString(secret);
  }

  @Test
  void testGrafanaAddonBasic() {
    var chart = new HelmChart(
      "grafana",
      "monitoring",
      "grafana-release",
      "https://grafana.github.io/helm-charts",
      null,
      "7.0.8");

    var addon = new GrafanaAddon(chart, "grafana-secret");

    assertNotNull(addon.chart());
    assertEquals("grafana", addon.chart().name());
    assertEquals("grafana-secret", addon.secret());
    assertRecordToString(addon);
  }

  @Test
  void testAlloyOperatorAddonBasic() {
    var chart = new HelmChart(
      "alloy-operator",
      "monitoring",
      "alloy-operator-release",
      "https://grafana.github.io/helm-charts",
      null,
      "0.1.0");

    var addon = new AlloyOperatorAddon(chart, "alloy-secret");

    assertNotNull(addon.chart());
    assertEquals("alloy-operator", addon.chart().name());
    assertEquals("alloy-secret", addon.secret());
    assertRecordToString(addon);
  }

  @Test
  void testKarpenterAddonBasic() {
    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("eks.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "karpenter-role",
      "Karpenter controller role",
      principal,
      List.of("AmazonEKSClusterPolicy"),
      List.of(),
      Map.of());

    var metadata = new ObjectMeta();
    metadata.setName("karpenter");
    metadata.setNamespace("karpenter");

    var podIdentity = new PodIdentity(metadata, role, Map.of("app", "karpenter"));

    var chart = new HelmChart(
      "karpenter",
      "karpenter",
      "karpenter-release",
      "oci://public.ecr.aws/karpenter",
      null,
      "v0.32.1");

    var addon = new KarpenterAddon(chart, podIdentity);

    assertNotNull(addon.chart());
    assertNotNull(addon.podIdentity());
    assertEquals("karpenter", addon.chart().name());
    assertEquals("karpenter-role", addon.podIdentity().role().name());
    assertRecordToString(addon);
  }

  @Test
  void testAddonsConfBasic() {
    var certManagerChart = new HelmChart(
      "cert-manager",
      "cert-manager",
      "cert-manager-release",
      "https://charts.jetstack.io",
      null,
      "v1.13.2");

    var certManager = new CertManagerAddon(certManagerChart);

    var addons = new AddonsConf(
      null,
      null,
      null,
      certManager,
      null,
      null,
      null);

    assertNotNull(addons.certManager());
    assertEquals("cert-manager", addons.certManager().chart().name());
    assertNull(addons.managed());
    assertNull(addons.karpenter());
    assertRecordToString(addons);
  }

  @Test
  void testAddonsConfComplete() {
    var coreDns = ManagedAddon
      .builder()
      .name("coredns")
      .version("v1.10.1")
      .build();

    var managedAddons = new ManagedAddons(null, null, coreDns, null, null, null);

    var secretsStoreChart = new HelmChart(
      "secrets-store",
      "kube-system",
      "secrets-store-release",
      "https://kubernetes-sigs.github.io/secrets-store-csi-driver/charts",
      null,
      "1.4.0");

    var secretsStore = new AwsSecretsStoreAddon(secretsStoreChart);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("eks.amazonaws.com")
      .conditions(Map.of())
      .build();

    var albRole = new IamRole(
      "alb-role",
      "ALB role",
      principal,
      List.of(),
      List.of(),
      Map.of());

    var albMetadata = new ObjectMeta();
    albMetadata.setName("alb-controller");
    albMetadata.setNamespace("kube-system");

    var albServiceAccount = new ServiceAccountConf(albMetadata, albRole);

    var albChart = new HelmChart(
      "aws-load-balancer-controller",
      "kube-system",
      "alb-controller-release",
      "https://aws.github.io/eks-charts",
      null,
      "1.6.2");

    var albAddon = new AwsLoadBalancerAddon(albChart, albServiceAccount);

    var certManagerChart = new HelmChart(
      "cert-manager",
      "cert-manager",
      "cert-manager-release",
      "https://charts.jetstack.io",
      null,
      "v1.13.2");

    var certManager = new CertManagerAddon(certManagerChart);

    var grafanaChart = new HelmChart(
      "grafana",
      "monitoring",
      "grafana-release",
      "https://grafana.github.io/helm-charts",
      null,
      "7.0.8");

    var grafana = new GrafanaAddon(grafanaChart, "grafana-secret");

    var alloyChart = new HelmChart(
      "alloy",
      "monitoring",
      "alloy-release",
      "https://grafana.github.io/helm-charts",
      null,
      "0.1.0");

    var alloy = new AlloyOperatorAddon(alloyChart, "alloy-secret");

    var karpenterRole = new IamRole(
      "karpenter-role",
      "Karpenter role",
      principal,
      List.of(),
      List.of(),
      Map.of());

    var karpenterMetadata = new ObjectMeta();
    karpenterMetadata.setName("karpenter");
    karpenterMetadata.setNamespace("karpenter");

    var karpenterPodIdentity = new PodIdentity(karpenterMetadata, karpenterRole, Map.of());

    var karpenterChart = new HelmChart(
      "karpenter",
      "karpenter",
      "karpenter-release",
      "oci://public.ecr.aws/karpenter",
      null,
      "v0.32.1");

    var karpenter = new KarpenterAddon(karpenterChart, karpenterPodIdentity);

    var addons = new AddonsConf(
      managedAddons,
      secretsStore,
      albAddon,
      certManager,
      karpenter,
      alloy,
      grafana);

    assertNotNull(addons.managed());
    assertNotNull(addons.awsSecretsStore());
    assertNotNull(addons.awsLoadBalancer());
    assertNotNull(addons.certManager());
    assertNotNull(addons.karpenter());
    assertNotNull(addons.alloyOperator());
    assertNotNull(addons.grafana());
  }

  @Test
  void testManagedAddonWithConfigurationValues() {
    var addon = ManagedAddon
      .builder()
      .name("vpc-cni")
      .version("v1.15.1")
      .configurationValues("{\"env\":{\"ENABLE_PREFIX_DELEGATION\":\"true\"}}")
      .resolveConflicts("OVERWRITE")
      .build();

    assertEquals("vpc-cni", addon.name());
    assertNotNull(addon.configurationValues());
    assertTrue(addon.configurationValues().contains("ENABLE_PREFIX_DELEGATION"));
  }

  @Test
  void testAwsEbsCsiAddonGp3StorageClass() {
    var kms = new Kms(
      "ebs-key",
      "EBS encryption key",
      true,
      false,
      "ENCRYPT_DECRYPT",
      "SYMMETRIC_DEFAULT",
      "RETAIN");

    var addon = new AwsEbsCsiAddon(kms, "gp3");

    assertEquals("gp3", addon.defaultStorageClass());
    assertEquals("RETAIN", addon.kms().removalPolicy());
    assertNotNull(addon.kms());
  }
}
