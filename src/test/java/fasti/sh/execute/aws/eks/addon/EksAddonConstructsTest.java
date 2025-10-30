package fasti.sh.execute.aws.eks.addon;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.aws.vpc.VpcConstruct;
import fasti.sh.model.aws.eks.HelmChart;
import fasti.sh.model.aws.eks.PodIdentity;
import fasti.sh.model.aws.eks.addon.core.*;
import fasti.sh.model.aws.eks.addon.core.karpenter.KarpenterAddon;
import fasti.sh.model.aws.iam.IamRole;
import fasti.sh.model.aws.iam.Principal;
import fasti.sh.model.aws.iam.PrincipalType;
import fasti.sh.model.aws.vpc.NetworkConf;
import fasti.sh.model.aws.vpc.Subnet;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.services.ec2.DefaultInstanceTenancy;
import software.amazon.awscdk.services.ec2.SubnetType;
import software.amazon.awscdk.services.eks.Cluster;
import software.amazon.awscdk.services.eks.KubernetesVersion;

/**
 * Tests for EKS addon constructs.
 */
class EksAddonConstructsTest {

  private Cluster createTestCluster(fasti.sh.test.CdkTestUtil.TestContext ctx, VpcConstruct vpcConstruct) {
    return Cluster.Builder
      .create(ctx.scope(), "test-cluster")
      .clusterName("test-eks-cluster")
      .version(KubernetesVersion.V1_28)
      .vpc(vpcConstruct.vpc())
      .kubectlLayer(
        software.amazon.awscdk.services.lambda.LayerVersion
          .fromLayerVersionArn(
            ctx.scope(),
            "kubectl-layer",
            "arn:aws:lambda:us-east-1:123456789012:layer:kubectl:1"))
      .build();
  }

  private VpcConstruct createTestVpc(fasti.sh.test.CdkTestUtil.TestContext ctx) {
    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());

    var networkConf = new NetworkConf(
      "eks-test-vpc",
      "10.0.0.0/16",
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

    return new VpcConstruct(ctx.scope(), ctx.common(), networkConf);
  }

  @Test
  void testCertManagerConstructBasic() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var chart = new HelmChart(
      "cert-manager",
      "cert-manager",
      "cert-manager",
      "https://charts.jetstack.io",
      "eks/addon/cert-manager.mustache",
      "v1.13.0");

    var addon = new CertManagerAddon(chart);
    var construct = new CertManagerConstruct(ctx.scope(), ctx.common(), addon, cluster);

    assertNotNull(construct);
    assertNotNull(construct.chart());
  }

  @Test
  void testCertManagerConstructWithValues() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var chart = new HelmChart(
      "cert-manager",
      "cert-manager",
      "cert-manager-release",
      "https://charts.jetstack.io",
      "eks/addon/cert-manager.mustache",
      "v1.14.0");

    var addon = new CertManagerAddon(chart);
    var construct = new CertManagerConstruct(ctx.scope(), ctx.common(), addon, cluster);

    assertNotNull(construct);
    assertNotNull(construct.chart());
  }

  @Test
  void testAwsSecretsStoreConstructBasic() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var chart = new HelmChart(
      "secrets-store-csi-driver",
      "kube-system",
      "csi-secrets-store",
      "https://kubernetes-sigs.github.io/secrets-store-csi-driver/charts",
      "eks/addon/secrets-store.mustache",
      "v1.4.0");

    var addon = new AwsSecretsStoreAddon(chart);
    var construct = new AwsSecretsStoreConstruct(ctx.scope(), ctx.common(), addon, cluster);

    assertNotNull(construct);
    assertNotNull(construct.chart());
  }

  @Test
  void testAwsSecretsStoreConstructWithCustomValues() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var chart = new HelmChart(
      "secrets-store-csi-driver",
      "secrets-system",
      "secrets-store",
      "https://kubernetes-sigs.github.io/secrets-store-csi-driver/charts",
      "eks/addon/secrets-store.mustache",
      "v1.4.1");

    var addon = new AwsSecretsStoreAddon(chart);
    var construct = new AwsSecretsStoreConstruct(ctx.scope(), ctx.common(), addon, cluster);

    assertNotNull(construct);
    assertNotNull(construct.chart());
  }

  @Test
  void testKarpenterConstructBasic() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var chart = new HelmChart(
      "karpenter",
      "karpenter",
      "karpenter",
      "oci://public.ecr.aws/karpenter/karpenter",
      "eks/addon/karpenter.mustache",
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
      List.of("AmazonEC2FullAccess"),
      List.of(),
      Map.of());

    var metadata = new ObjectMeta();
    metadata.setName("karpenter");
    metadata.setNamespace("karpenter");

    var podIdentity = new PodIdentity(metadata, role, Map.of("Environment", "production"));

    var addon = new KarpenterAddon(chart, podIdentity);
    var construct = new KarpenterConstruct(ctx.scope(), ctx.common(), addon, cluster);

    assertNotNull(construct);
    assertNotNull(construct.chart());
    assertNotNull(construct.namespace());
    assertNotNull(construct.podIdentity());
  }

  @Test
  void testKarpenterConstructWithCustomConfig() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var chart = new HelmChart(
      "karpenter",
      "karpenter-system",
      "karpenter-controller",
      "oci://public.ecr.aws/karpenter/karpenter",
      "eks/addon/karpenter.mustache",
      "v0.33.0");

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("pods.eks.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "karpenter-custom-role",
      "Custom Karpenter role",
      principal,
      List.of("AmazonEC2FullAccess", "AmazonSSMManagedInstanceCore"),
      List.of(),
      Map.of("Team", "platform"));

    var metadata = new ObjectMeta();
    metadata.setName("karpenter-sa");
    metadata.setNamespace("karpenter-system");
    metadata.setLabels(Map.of("app", "karpenter"));

    var podIdentity = new PodIdentity(
      metadata,
      role,
      Map.of("Environment", "staging", "CostCenter", "engineering"));

    var addon = new KarpenterAddon(chart, podIdentity);
    var construct = new KarpenterConstruct(ctx.scope(), ctx.common(), addon, cluster);

    assertNotNull(construct);
    assertNotNull(construct.chart());
    assertNotNull(construct.namespace());
    assertNotNull(construct.podIdentity());
  }

  @Test
  void testCertManagerConstructProductionConfig() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var chart = new HelmChart(
      "cert-manager",
      "cert-manager-prod",
      "cert-manager-production",
      "https://charts.jetstack.io",
      "eks/addon/cert-manager.mustache",
      "v1.13.3");

    var addon = new CertManagerAddon(chart);
    var construct = new CertManagerConstruct(ctx.scope(), ctx.common(), addon, cluster);

    assertNotNull(construct);
    assertNotNull(construct.chart());
  }

  @Test
  void testAwsSecretsStoreConstructHighAvailability() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var chart = new HelmChart(
      "secrets-store-csi-driver",
      "kube-system",
      "secrets-store-ha",
      "https://kubernetes-sigs.github.io/secrets-store-csi-driver/charts",
      "eks/addon/secrets-store.mustache",
      "v1.4.2");

    var addon = new AwsSecretsStoreAddon(chart);
    var construct = new AwsSecretsStoreConstruct(ctx.scope(), ctx.common(), addon, cluster);

    assertNotNull(construct);
    assertNotNull(construct.chart());
  }

  @Test
  void testKarpenterConstructDevelopment() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var chart = new HelmChart(
      "karpenter",
      "karpenter",
      "karpenter-dev",
      "oci://public.ecr.aws/karpenter/karpenter",
      "eks/addon/karpenter.mustache",
      "v0.32.1");

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("pods.eks.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "karpenter-dev-role",
      "Karpenter dev role",
      principal,
      List.of("AmazonEC2FullAccess"),
      List.of(),
      Map.of("Environment", "development"));

    var metadata = new ObjectMeta();
    metadata.setName("karpenter-dev");
    metadata.setNamespace("karpenter");
    metadata.setAnnotations(Map.of("description", "Development Karpenter instance"));

    var podIdentity = new PodIdentity(metadata, role, Map.of("Environment", "development"));

    var addon = new KarpenterAddon(chart, podIdentity);
    var construct = new KarpenterConstruct(ctx.scope(), ctx.common(), addon, cluster);

    assertNotNull(construct);
    assertNotNull(construct.chart());
    assertNotNull(construct.namespace());
    assertNotNull(construct.podIdentity());
  }

  @Test
  void testCertManagerConstructMinimalConfig() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var chart = new HelmChart(
      "cert-manager",
      "cert-manager",
      "cert-manager-minimal",
      "https://charts.jetstack.io",
      "eks/addon/cert-manager.mustache",
      "v1.12.0");

    var addon = new CertManagerAddon(chart);
    var construct = new CertManagerConstruct(ctx.scope(), ctx.common(), addon, cluster);

    assertNotNull(construct);
    assertNotNull(construct.chart());
  }

  @Test
  void testAwsLoadBalancerConstructBasic() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var chart = new HelmChart(
      "aws-load-balancer-controller",
      "aws-load-balancer-controller",
      "aws-load-balancer",
      "https://aws.github.io/eks-charts",
      "eks/addon/aws-load-balancer.mustache",
      "v2.6.2");

    var metadata = new ObjectMeta();
    metadata.setName("aws-load-balancer-controller");
    metadata.setNamespace("kube-system");

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("pods.eks.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "alb-controller-role",
      "AWS Load Balancer Controller role",
      principal,
      List.of("arn:aws:iam::aws:policy/AWSLoadBalancerControllerPolicy"),
      List.of(),
      Map.of());

    var serviceAccount = new fasti.sh.model.aws.eks.ServiceAccountConf(metadata, role);
    var addon = new AwsLoadBalancerAddon(chart, serviceAccount);
    var construct = new AwsLoadBalancerConstruct(ctx.scope(), ctx.common(), addon, cluster);

    assertNotNull(construct);
    assertNotNull(construct.namespace());
    assertNotNull(construct.serviceAccount());
    assertNotNull(construct.chart());
  }

  @Test
  void testAwsLoadBalancerConstructProduction() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var chart = new HelmChart(
      "aws-load-balancer-controller",
      "aws-load-balancer-controller",
      "aws-load-balancer-prod",
      "https://aws.github.io/eks-charts",
      "eks/addon/aws-load-balancer.mustache",
      "v2.7.0");

    var metadata = new ObjectMeta();
    metadata.setName("aws-load-balancer-controller-prod");
    metadata.setNamespace("kube-system");
    metadata.setLabels(Map.of("environment", "production"));

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("pods.eks.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "alb-controller-prod-role",
      "Production ALB Controller role",
      principal,
      List.of("arn:aws:iam::aws:policy/AWSLoadBalancerControllerPolicy"),
      List.of(),
      Map.of("Environment", "production"));

    var serviceAccount = new fasti.sh.model.aws.eks.ServiceAccountConf(metadata, role);
    var addon = new AwsLoadBalancerAddon(chart, serviceAccount);
    var construct = new AwsLoadBalancerConstruct(ctx.scope(), ctx.common(), addon, cluster);

    assertNotNull(construct);
    assertNotNull(construct.namespace());
    assertNotNull(construct.serviceAccount());
    assertNotNull(construct.chart());
  }
}
