package fasti.sh.model.aws.eks.addon.core;

import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.serialization.Mapper;
import fasti.sh.model.aws.eks.HelmChart;
import fasti.sh.model.aws.eks.ServiceAccountConf;
import fasti.sh.model.aws.iam.IamRole;
import fasti.sh.model.aws.iam.Principal;
import fasti.sh.model.aws.iam.PrincipalType;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for EKS core addon model classes.
 */
class CoreAddonModelsTest {

  private HelmChart createTestHelmChart() {
    return new HelmChart(
      "test-addon",
      "test-chart",
      "test-namespace",
      "https://charts.example.com",
      "values.yaml",
      "1.0.0");
  }

  private ServiceAccountConf createTestServiceAccount() {
    var metadata = new ObjectMeta();
    metadata.setName("test-sa");
    metadata.setNamespace("kube-system");

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("pods.eks.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "test-role",
      "Test role",
      principal,
      List.of("arn:aws:iam::aws:policy/TestPolicy"),
      List.of(),
      Map.of());

    return new ServiceAccountConf(metadata, role);
  }

  @Test
  void testGrafanaSecretInstantiation() {
    var secret = new GrafanaSecret(
      "grafana-secret-key",
      "https://loki.example.com",
      "loki-user",
      "https://prometheus.example.com",
      "prom-user",
      "https://tempo.example.com",
      "tempo-user",
      "instance-123",
      "https://pyroscope.example.com");

    assertEquals("grafana-secret-key", secret.key());
    assertEquals("https://loki.example.com", secret.lokiHost());
    assertEquals("loki-user", secret.lokiUsername());
    assertEquals("https://prometheus.example.com", secret.prometheusHost());
  }

  @Test
  void testGrafanaSecretSerialization() throws Exception {
    var secret = new GrafanaSecret(
      "key-123",
      "https://loki.test.com",
      "user1",
      "https://prom.test.com",
      "user2",
      "https://tempo.test.com",
      "user3",
      "inst-456",
      "https://pyro.test.com");

    var json = Mapper.get().writeValueAsString(secret);

    assertNotNull(json);
    assertTrue(json.contains("key-123"));
    assertTrue(json.contains("loki.test.com"));
  }

  @Test
  void testGrafanaSecretDeserialization() throws Exception {
    var json = """
      {
        "key": "secret-key",
        "lokiHost": "https://loki.local",
        "lokiUsername": "loki",
        "prometheusHost": "https://prom.local",
        "prometheusUsername": "prom",
        "tempoHost": "https://tempo.local",
        "tempoUsername": "tempo",
        "instanceId": "123",
        "pyroscopeHost": "https://pyro.local"
      }
      """;

    var secret = Mapper.get().readValue(json, GrafanaSecret.class);

    assertNotNull(secret);
    assertEquals("secret-key", secret.key());
    assertEquals("https://loki.local", secret.lokiHost());
  }

  @Test
  void testCertManagerAddonInstantiation() {
    var chart = createTestHelmChart();
    var addon = new CertManagerAddon(chart);

    assertNotNull(addon.chart());
    assertEquals("test-addon", addon.chart().name());
  }

  @Test
  void testCertManagerAddonSerialization() throws Exception {
    var chart = createTestHelmChart();
    var addon = new CertManagerAddon(chart);

    var json = Mapper.get().writeValueAsString(addon);

    assertNotNull(json);
    assertTrue(json.contains("test-addon") || json.contains("chart"));
  }

  @Test
  void testAwsSecretsStoreAddonInstantiation() {
    var chart = createTestHelmChart();
    var addon = new AwsSecretsStoreAddon(chart);

    assertNotNull(addon.chart());
    assertEquals("test-addon", addon.chart().name());
  }

  @Test
  void testAwsLoadBalancerAddonInstantiation() {
    var chart = createTestHelmChart();
    var serviceAccount = createTestServiceAccount();
    var addon = new AwsLoadBalancerAddon(chart, serviceAccount);

    assertNotNull(addon.chart());
    assertNotNull(addon.serviceAccount());
    assertEquals("test-sa", addon.serviceAccount().metadata().getName());
  }

  @Test
  void testAwsLoadBalancerAddonSerialization() throws Exception {
    var chart = createTestHelmChart();
    var serviceAccount = createTestServiceAccount();
    var addon = new AwsLoadBalancerAddon(chart, serviceAccount);

    var json = Mapper.get().writeValueAsString(addon);

    assertNotNull(json);
  }

  @Test
  void testGrafanaAddonInstantiation() {
    var chart = createTestHelmChart();
    var addon = new GrafanaAddon(chart, "grafana-secret");

    assertNotNull(addon.chart());
    assertEquals("grafana-secret", addon.secret());
  }

  @Test
  void testGrafanaAddonSerialization() throws Exception {
    var chart = createTestHelmChart();
    var addon = new GrafanaAddon(chart, "secret-123");

    var json = Mapper.get().writeValueAsString(addon);

    assertNotNull(json);
    assertTrue(json.contains("secret-123"));
  }

  @Test
  void testAlloyOperatorAddonInstantiation() {
    var chart = createTestHelmChart();
    var addon = new AlloyOperatorAddon(chart, "alloy-secret");

    assertNotNull(addon.chart());
    assertEquals("alloy-secret", addon.secret());
  }

  @Test
  void testAlloyOperatorAddonSerialization() throws Exception {
    var chart = createTestHelmChart();
    var addon = new AlloyOperatorAddon(chart, "operator-secret");

    var json = Mapper.get().writeValueAsString(addon);

    assertNotNull(json);
    assertTrue(json.contains("operator-secret") || json.contains("secret"));
  }
}
