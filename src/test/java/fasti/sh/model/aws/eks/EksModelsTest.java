package fasti.sh.model.aws.eks;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for EKS model records.
 */
class EksModelsTest {

  @Test
  void testTenantBasic() {
    var tenant = new Tenant("admin@example.com", "admin", "admin-user");

    assertEquals("admin@example.com", tenant.email());
    assertEquals("admin", tenant.role());
    assertEquals("admin-user", tenant.username());
    assertRecordToString(tenant);
  }

  @Test
  void testTenantUser() {
    var tenant = new Tenant("user@example.com", "developer", "dev-user");

    assertEquals("user@example.com", tenant.email());
    assertEquals("developer", tenant.role());
    assertEquals("dev-user", tenant.username());
  }

  @Test
  void testTenantEquality() {
    var tenant1 = new Tenant("test@example.com", "viewer", "test");
    var tenant2 = new Tenant("test@example.com", "viewer", "test");

    assertRecordEquality(tenant1, tenant2);
  }

  @Test
  void testTenancyConfBasic() {
    var admin = new Tenant("admin@example.com", "admin", "admin");
    var user = new Tenant("user@example.com", "developer", "dev");

    var tenancy = new TenancyConf(List.of(admin), List.of(user));

    assertEquals(1, tenancy.administrators().size());
    assertEquals(1, tenancy.users().size());
    assertEquals("admin@example.com", tenancy.administrators().get(0).email());
    assertEquals("user@example.com", tenancy.users().get(0).email());
    assertRecordToString(tenancy);
  }

  @Test
  void testTenancyConfMultipleAdmins() {
    var admin1 = new Tenant("admin1@example.com", "admin", "admin1");
    var admin2 = new Tenant("admin2@example.com", "admin", "admin2");

    var tenancy = new TenancyConf(List.of(admin1, admin2), List.of());

    assertEquals(2, tenancy.administrators().size());
    assertTrue(tenancy.users().isEmpty());
  }

  @Test
  void testTenancyConfMultipleUsers() {
    var user1 = new Tenant("user1@example.com", "developer", "user1");
    var user2 = new Tenant("user2@example.com", "developer", "user2");
    var user3 = new Tenant("user3@example.com", "viewer", "user3");

    var tenancy = new TenancyConf(List.of(), List.of(user1, user2, user3));

    assertTrue(tenancy.administrators().isEmpty());
    assertEquals(3, tenancy.users().size());
  }

  @Test
  void testTenancyConfEmpty() {
    var tenancy = new TenancyConf(List.of(), List.of());

    assertTrue(tenancy.administrators().isEmpty());
    assertTrue(tenancy.users().isEmpty());
  }

  @Test
  void testHelmChartBasic() {
    var chart = new HelmChart(
      "nginx-ingress",
      "ingress-nginx",
      "my-nginx",
      "https://kubernetes.github.io/ingress-nginx",
      "values.yaml",
      "4.0.0");

    assertEquals("nginx-ingress", chart.name());
    assertEquals("ingress-nginx", chart.namespace());
    assertEquals("my-nginx", chart.release());
    assertEquals("https://kubernetes.github.io/ingress-nginx", chart.repository());
    assertEquals("values.yaml", chart.values());
    assertEquals("4.0.0", chart.version());
    assertRecordToString(chart);
  }

  @Test
  void testHelmChartCertManager() {
    var chart = new HelmChart(
      "cert-manager",
      "cert-manager",
      "cert-manager-release",
      "https://charts.jetstack.io",
      "cert-manager-values.yaml",
      "v1.12.0");

    assertEquals("cert-manager", chart.name());
    assertEquals("cert-manager", chart.namespace());
    assertEquals("v1.12.0", chart.version());
  }

  @Test
  void testHelmChartPrometheus() {
    var chart = new HelmChart(
      "prometheus",
      "monitoring",
      "prometheus-release",
      "https://prometheus-community.github.io/helm-charts",
      null,
      "15.0.0");

    assertEquals("prometheus", chart.name());
    assertEquals("monitoring", chart.namespace());
    assertNull(chart.values());
  }

  @Test
  void testHelmChartEquality() {
    var chart1 = new HelmChart("app", "default", "release1", "repo", "values", "1.0");
    var chart2 = new HelmChart("app", "default", "release1", "repo", "values", "1.0");

    assertRecordEquality(chart1, chart2);
  }

  @Test
  void testNodeGroupBasic() {
    var principal = fasti.sh.model.aws.iam.Principal
      .builder()
      .type(fasti.sh.model.aws.iam.PrincipalType.SERVICE)
      .value("ec2.amazonaws.com")
      .conditions(java.util.Map.of())
      .build();

    var role = new fasti.sh.model.aws.iam.IamRole(
      "node-role",
      "Node group role",
      principal,
      List.of("AmazonEKSWorkerNodePolicy", "AmazonEC2ContainerRegistryReadOnly"),
      List.of(),
      java.util.Map.of());

    var nodeGroup = new NodeGroup(
      software.amazon.awscdk.services.eks.NodegroupAmiType.AL2_X86_64,
      false,
      "ON_DEMAND",
      2,
      software.amazon.awscdk.services.ec2.InstanceClass.M5,
      software.amazon.awscdk.services.ec2.InstanceSize.LARGE,
      5,
      1,
      "default-node-group",
      role,
      java.util.Map.of("Environment", "production"),
      java.util.Map.of("Team", "platform"));

    assertEquals(software.amazon.awscdk.services.eks.NodegroupAmiType.AL2_X86_64, nodeGroup.amiType());
    assertFalse(nodeGroup.forceUpdate());
    assertEquals("ON_DEMAND", nodeGroup.capacityType());
    assertEquals(2, nodeGroup.desiredSize());
    assertEquals(software.amazon.awscdk.services.ec2.InstanceClass.M5, nodeGroup.instanceClass());
    assertEquals(software.amazon.awscdk.services.ec2.InstanceSize.LARGE, nodeGroup.instanceSize());
    assertEquals(5, nodeGroup.maxSize());
    assertEquals(1, nodeGroup.minSize());
    assertEquals("default-node-group", nodeGroup.name());
    assertNotNull(nodeGroup.role());
    assertEquals("production", nodeGroup.labels().get("Environment"));
    assertEquals("platform", nodeGroup.tags().get("Team"));
    assertRecordToString(nodeGroup);
  }

  @Test
  void testNodeGroupSpot() {
    var principal = fasti.sh.model.aws.iam.Principal
      .builder()
      .type(fasti.sh.model.aws.iam.PrincipalType.SERVICE)
      .value("ec2.amazonaws.com")
      .conditions(java.util.Map.of())
      .build();

    var role = new fasti.sh.model.aws.iam.IamRole(
      "spot-node-role",
      "Spot node group role",
      principal,
      List.of("AmazonEKSWorkerNodePolicy"),
      List.of(),
      java.util.Map.of());

    var nodeGroup = new NodeGroup(
      software.amazon.awscdk.services.eks.NodegroupAmiType.AL2_ARM_64,
      true,
      "SPOT",
      3,
      software.amazon.awscdk.services.ec2.InstanceClass.T4G,
      software.amazon.awscdk.services.ec2.InstanceSize.MEDIUM,
      10,
      2,
      "spot-node-group",
      role,
      java.util.Map.of("Workload", "batch"),
      java.util.Map.of());

    assertEquals("SPOT", nodeGroup.capacityType());
    assertTrue(nodeGroup.forceUpdate());
    assertEquals(software.amazon.awscdk.services.eks.NodegroupAmiType.AL2_ARM_64, nodeGroup.amiType());
    assertEquals("spot-node-group", nodeGroup.name());
  }

  @Test
  void testServiceAccountConfBasic() {
    var principal = fasti.sh.model.aws.iam.Principal
      .builder()
      .type(fasti.sh.model.aws.iam.PrincipalType.FEDERATED)
      .value("arn:aws:iam::123456789012:oidc-provider/oidc.eks.us-east-1.amazonaws.com")
      .conditions(java.util.Map.of())
      .build();

    var role = new fasti.sh.model.aws.iam.IamRole(
      "service-account-role",
      "Service account IAM role",
      principal,
      List.of("AmazonS3ReadOnlyAccess"),
      List.of(),
      java.util.Map.of());

    var metadata = new io.fabric8.kubernetes.api.model.ObjectMeta();
    metadata.setName("my-service-account");
    metadata.setNamespace("default");

    var serviceAccount = new ServiceAccountConf(metadata, role);

    assertNotNull(serviceAccount.metadata());
    assertEquals("my-service-account", serviceAccount.metadata().getName());
    assertEquals("default", serviceAccount.metadata().getNamespace());
    assertNotNull(serviceAccount.role());
    assertEquals("service-account-role", serviceAccount.role().name());
    assertRecordToString(serviceAccount);
  }

  @Test
  void testPodIdentityBasic() {
    var principal = fasti.sh.model.aws.iam.Principal
      .builder()
      .type(fasti.sh.model.aws.iam.PrincipalType.SERVICE)
      .value("pods.eks.amazonaws.com")
      .conditions(java.util.Map.of())
      .build();

    var role = new fasti.sh.model.aws.iam.IamRole(
      "pod-identity-role",
      "Pod identity IAM role",
      principal,
      List.of("AmazonDynamoDBReadOnlyAccess"),
      List.of(),
      java.util.Map.of());

    var metadata = new io.fabric8.kubernetes.api.model.ObjectMeta();
    metadata.setName("app-pod-identity");
    metadata.setNamespace("applications");
    metadata.setLabels(java.util.Map.of("app", "my-app"));

    var podIdentity = new PodIdentity(
      metadata,
      role,
      java.util.Map.of("Environment", "production", "Team", "backend"));

    assertNotNull(podIdentity.metadata());
    assertEquals("app-pod-identity", podIdentity.metadata().getName());
    assertEquals("applications", podIdentity.metadata().getNamespace());
    assertEquals("my-app", podIdentity.metadata().getLabels().get("app"));
    assertNotNull(podIdentity.role());
    assertEquals("pod-identity-role", podIdentity.role().name());
    assertEquals("production", podIdentity.tags().get("Environment"));
    assertEquals("backend", podIdentity.tags().get("Team"));
    assertRecordToString(podIdentity);
  }

  @Test
  void testKubernetesConfBasic() {
    var config = new KubernetesConf(
      "my-cluster",
      "1.28",
      "PUBLIC_AND_PRIVATE",
      true,
      "{\"userClusterRole\":{\"rules\":[]}}",
      "{\"administrators\":[],\"users\":[]}",
      List.of("api", "audit", "authenticator"),
      List.of("PRIVATE_WITH_EGRESS", "PUBLIC"),
      "{\"nodeGroups\":[]}",
      "{\"managed\":{}}",
      "{\"queues\":[]}",
      "{\"metrics\":{}}",
      java.util.Map.of("app.kubernetes.io/managed-by", "cdk"),
      java.util.Map.of("cluster-name", "my-cluster"),
      java.util.Map.of("Environment", "production"));

    assertEquals("my-cluster", config.name());
    assertEquals("1.28", config.version());
    assertEquals("PUBLIC_AND_PRIVATE", config.endpointAccess());
    assertTrue(config.prune());
    assertNotNull(config.rbac());
    assertNotNull(config.tenancy());
    assertEquals(3, config.loggingTypes().size());
    assertTrue(config.loggingTypes().contains("api"));
    assertTrue(config.loggingTypes().contains("audit"));
    assertEquals(2, config.vpcSubnetTypes().size());
    assertNotNull(config.nodeGroups());
    assertNotNull(config.addons());
    assertEquals("cdk", config.annotations().get("app.kubernetes.io/managed-by"));
    assertEquals("my-cluster", config.labels().get("cluster-name"));
    assertEquals("production", config.tags().get("Environment"));
    assertRecordToString(config);
  }

  @Test
  void testKubernetesConfMinimal() {
    var config = new KubernetesConf(
      "minimal-cluster",
      "1.27",
      "PUBLIC",
      false,
      "{}",
      "{}",
      List.of(),
      List.of("PUBLIC"),
      "{}",
      "{}",
      "{}",
      "{}",
      java.util.Map.of(),
      java.util.Map.of(),
      java.util.Map.of());

    assertEquals("minimal-cluster", config.name());
    assertEquals("1.27", config.version());
    assertFalse(config.prune());
    assertTrue(config.loggingTypes().isEmpty());
    assertTrue(config.annotations().isEmpty());
    assertTrue(config.labels().isEmpty());
    assertTrue(config.tags().isEmpty());
  }
}
