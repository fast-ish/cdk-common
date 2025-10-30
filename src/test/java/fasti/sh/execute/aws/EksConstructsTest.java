package fasti.sh.execute.aws;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.aws.eks.*;
import fasti.sh.execute.aws.vpc.VpcConstruct;
import fasti.sh.model.aws.eks.*;
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
 * Tests for EKS constructs.
 */
class EksConstructsTest {

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
  void testNamespaceConstructBasic() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var metadata = new ObjectMeta();
    metadata.setName("test-namespace");
    metadata.setNamespace("test-namespace");
    metadata.setLabels(Map.of("environment", "test"));

    var construct = new NamespaceConstruct(ctx.scope(), ctx.common(), metadata, cluster);

    assertNotNull(construct);
    assertNotNull(construct.manifest());
  }

  @Test
  void testNamespaceConstructWithAnnotations() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var metadata = new ObjectMeta();
    metadata.setName("annotated-namespace");
    metadata.setNamespace("annotated-namespace");
    metadata.setLabels(Map.of("team", "platform"));
    metadata.setAnnotations(Map.of("description", "Platform team namespace"));

    var construct = new NamespaceConstruct(ctx.scope(), ctx.common(), metadata, cluster);

    assertNotNull(construct);
    assertNotNull(construct.manifest());
  }

  @Test
  void testServiceAccountConstructBasic() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var principal = Principal
      .builder()
      .type(PrincipalType.FEDERATED)
      .value("arn:aws:iam::123456789012:oidc-provider/oidc.eks.us-east-1.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "test-service-account-role",
      "Test service account role",
      principal,
      List.of("AmazonS3ReadOnlyAccess"),
      List.of(),
      Map.of());

    var metadata = new ObjectMeta();
    metadata.setName("test-sa");
    metadata.setNamespace("default");

    var serviceAccountConf = new ServiceAccountConf(metadata, role);

    var construct = new ServiceAccountConstruct(ctx.scope(), ctx.common(), serviceAccountConf, cluster);

    assertNotNull(construct);
    assertNotNull(construct.serviceAccount());
    assertNotNull(construct.roleConstruct());
  }

  @Test
  void testServiceAccountConstructWithLabels() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var principal = Principal
      .builder()
      .type(PrincipalType.FEDERATED)
      .value("arn:aws:iam::123456789012:oidc-provider/oidc.eks.us-east-1.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "labeled-service-account-role",
      "Labeled service account role",
      principal,
      List.of("AmazonDynamoDBReadOnlyAccess"),
      List.of(),
      Map.of());

    var metadata = new ObjectMeta();
    metadata.setName("labeled-sa");
    metadata.setNamespace("applications");
    metadata.setLabels(Map.of("app", "backend", "tier", "api"));

    var serviceAccountConf = new ServiceAccountConf(metadata, role);

    var construct = new ServiceAccountConstruct(ctx.scope(), ctx.common(), serviceAccountConf, cluster);

    assertNotNull(construct);
    assertNotNull(construct.serviceAccount());
    assertNotNull(construct.roleConstruct());
  }

  @Test
  void testPodIdentityConstructBasic() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("pods.eks.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "pod-identity-role",
      "Pod identity role",
      principal,
      List.of("AmazonEC2ReadOnlyAccess"),
      List.of(),
      Map.of());

    var metadata = new ObjectMeta();
    metadata.setName("app-pod-identity");
    metadata.setNamespace("default");

    var podIdentity = new PodIdentity(metadata, role, Map.of("Environment", "test"));

    var construct = new ServiceAccountConstruct(ctx.scope(), ctx.common(), podIdentity, cluster);

    assertNotNull(construct);
    assertNotNull(construct.serviceAccount());
    assertNotNull(construct.roleConstruct());
  }

  @Test
  void testPodIdentityConstructMultipleNamespaces() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("pods.eks.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "monitoring-pod-identity",
      "Monitoring pod identity",
      principal,
      List.of("CloudWatchAgentServerPolicy"),
      List.of(),
      Map.of());

    var metadata = new ObjectMeta();
    metadata.setName("monitoring-identity");
    metadata.setNamespace("monitoring");
    metadata.setLabels(Map.of("component", "observability"));

    var podIdentity = new PodIdentity(metadata, role, Map.of("Team", "platform"));

    var construct = new ServiceAccountConstruct(ctx.scope(), ctx.common(), podIdentity, cluster);

    assertNotNull(construct);
    assertNotNull(construct.serviceAccount());
    assertNotNull(construct.roleConstruct());
  }

  @Test
  void testNamespaceConstructProductionNamespace() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var metadata = new ObjectMeta();
    metadata.setName("production");
    metadata.setNamespace("production");
    metadata
      .setLabels(
        Map
          .of(
            "environment",
            "production",
            "istio-injection",
            "enabled"));
    metadata
      .setAnnotations(
        Map
          .of(
            "description",
            "Production workloads",
            "contact",
            "platform-team@example.com"));

    var construct = new NamespaceConstruct(ctx.scope(), ctx.common(), metadata, cluster);

    assertNotNull(construct);
    assertNotNull(construct.manifest());
  }

  @Test
  void testServiceAccountConstructS3Access() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var principal = Principal
      .builder()
      .type(PrincipalType.FEDERATED)
      .value("arn:aws:iam::123456789012:oidc-provider/oidc.eks.us-east-1.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "s3-access-role",
      "S3 access role for pods",
      principal,
      List.of("AmazonS3FullAccess"),
      List.of(),
      Map.of("Service", "s3"));

    var metadata = new ObjectMeta();
    metadata.setName("s3-access-sa");
    metadata.setNamespace("data-processing");
    metadata.setLabels(Map.of("access", "s3"));

    var serviceAccountConf = new ServiceAccountConf(metadata, role);

    var construct = new ServiceAccountConstruct(ctx.scope(), ctx.common(), serviceAccountConf, cluster);

    assertNotNull(construct);
    assertNotNull(construct.serviceAccount());
    assertNotNull(construct.roleConstruct());
  }

  @Test
  void testPodIdentityConstructWithMultipleTags() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("pods.eks.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "tagged-pod-identity",
      "Tagged pod identity role",
      principal,
      List.of("AmazonSQSFullAccess"),
      List.of(),
      Map.of());

    var metadata = new ObjectMeta();
    metadata.setName("queue-processor");
    metadata.setNamespace("workers");

    var podIdentity = new PodIdentity(
      metadata,
      role,
      Map
        .of(
          "Environment",
          "production",
          "Team",
          "data",
          "CostCenter",
          "engineering",
          "Application",
          "queue-processor"));

    var construct = new ServiceAccountConstruct(ctx.scope(), ctx.common(), podIdentity, cluster);

    assertNotNull(construct);
    assertNotNull(construct.serviceAccount());
    assertNotNull(construct.roleConstruct());
  }

  @Test
  void testNamespaceConstructDevelopment() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var metadata = new ObjectMeta();
    metadata.setName("development");
    metadata.setNamespace("development");
    metadata.setLabels(Map.of("environment", "dev", "monitoring", "enabled"));
    metadata.setAnnotations(Map.of("owner", "dev-team@example.com"));

    var construct = new NamespaceConstruct(ctx.scope(), ctx.common(), metadata, cluster);

    assertNotNull(construct);
    assertNotNull(construct.manifest());
  }

  @Test
  void testServiceAccountConstructRdsAccess() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var principal = Principal
      .builder()
      .type(PrincipalType.FEDERATED)
      .value("arn:aws:iam::123456789012:oidc-provider/oidc.eks.us-east-1.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "rds-access-role",
      "RDS access role",
      principal,
      List.of("AmazonRDSReadOnlyAccess"),
      List.of(),
      Map.of());

    var metadata = new ObjectMeta();
    metadata.setName("rds-reader");
    metadata.setNamespace("backend");

    var serviceAccountConf = new ServiceAccountConf(metadata, role);

    var construct = new ServiceAccountConstruct(ctx.scope(), ctx.common(), serviceAccountConf, cluster);

    assertNotNull(construct);
    assertNotNull(construct.serviceAccount());
    assertNotNull(construct.roleConstruct());
  }

  @Test
  void testPodIdentityConstructSecretsManager() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("pods.eks.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "secrets-pod-identity",
      "Secrets Manager pod identity",
      principal,
      List.of("SecretsManagerReadWrite"),
      List.of(),
      Map.of());

    var metadata = new ObjectMeta();
    metadata.setName("secrets-reader");
    metadata.setNamespace("security");
    metadata.setLabels(Map.of("security-level", "high"));

    var podIdentity = new PodIdentity(metadata, role, Map.of("Compliance", "pci"));

    var construct = new ServiceAccountConstruct(ctx.scope(), ctx.common(), podIdentity, cluster);

    assertNotNull(construct);
    assertNotNull(construct.serviceAccount());
    assertNotNull(construct.roleConstruct());
  }

  @Test
  void testNamespaceConstructStagingEnvironment() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var metadata = new ObjectMeta();
    metadata.setName("staging");
    metadata.setNamespace("staging");
    metadata.setLabels(Map.of("environment", "staging", "auto-scaling", "enabled"));

    var construct = new NamespaceConstruct(ctx.scope(), ctx.common(), metadata, cluster);

    assertNotNull(construct);
    assertNotNull(construct.manifest());
  }

  @Test
  void testServiceAccountConstructKinesisAccess() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var principal = Principal
      .builder()
      .type(PrincipalType.FEDERATED)
      .value("arn:aws:iam::123456789012:oidc-provider/oidc.eks.us-east-1.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "kinesis-access-role",
      "Kinesis streams access",
      principal,
      List.of("AmazonKinesisFullAccess"),
      List.of(),
      Map.of());

    var metadata = new ObjectMeta();
    metadata.setName("kinesis-consumer");
    metadata.setNamespace("streaming");
    metadata.setLabels(Map.of("type", "stream-processor"));

    var serviceAccountConf = new ServiceAccountConf(metadata, role);

    var construct = new ServiceAccountConstruct(ctx.scope(), ctx.common(), serviceAccountConf, cluster);

    assertNotNull(construct);
    assertNotNull(construct.serviceAccount());
    assertNotNull(construct.roleConstruct());
  }

  @Test
  void testPodIdentityConstructMultipleLabels() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("pods.eks.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "labeled-identity",
      "Multi-labeled pod identity",
      principal,
      List.of("AmazonSNSFullAccess"),
      List.of(),
      Map.of());

    var metadata = new ObjectMeta();
    metadata.setName("notification-service");
    metadata.setNamespace("services");
    metadata
      .setLabels(
        Map
          .of(
            "app",
            "notifications",
            "tier",
            "backend",
            "version",
            "v2"));

    var podIdentity = new PodIdentity(metadata, role, Map.of("Service", "notifications"));

    var construct = new ServiceAccountConstruct(ctx.scope(), ctx.common(), podIdentity, cluster);

    assertNotNull(construct);
    assertNotNull(construct.serviceAccount());
    assertNotNull(construct.roleConstruct());
  }

  @Test
  void testServiceAccountConstructMultiplePolicies() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var principal = Principal
      .builder()
      .type(PrincipalType.FEDERATED)
      .value("arn:aws:iam::123456789012:oidc-provider/oidc.eks.us-east-1.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "multi-policy-role",
      "Role with multiple policies",
      principal,
      List.of("AmazonS3ReadOnlyAccess", "AmazonDynamoDBReadOnlyAccess", "AmazonSQSReadOnlyAccess"),
      List.of(),
      Map.of());

    var metadata = new ObjectMeta();
    metadata.setName("multi-policy-sa");
    metadata.setNamespace("apps");

    var serviceAccountConf = new ServiceAccountConf(metadata, role);
    var construct = new ServiceAccountConstruct(ctx.scope(), ctx.common(), serviceAccountConf, cluster);

    assertNotNull(construct);
    assertNotNull(construct.serviceAccount());
    assertNotNull(construct.roleConstruct());
  }

  @Test
  void testPodIdentityConstructCustomNamespace() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("pods.eks.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "custom-namespace-role",
      "Role for custom namespace",
      principal,
      List.of("CloudWatchAgentServerPolicy"),
      List.of(),
      Map.of());

    var metadata = new ObjectMeta();
    metadata.setName("custom-app");
    metadata.setNamespace("custom-apps");

    var podIdentity = new PodIdentity(metadata, role, Map.of());
    var construct = new ServiceAccountConstruct(ctx.scope(), ctx.common(), podIdentity, cluster);

    assertNotNull(construct);
    assertNotNull(construct.serviceAccount());
    assertNotNull(construct.roleConstruct());
  }

  @Test
  void testNamespaceConstructMinimal() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var metadata = new ObjectMeta();
    metadata.setName("minimal-ns");
    metadata.setNamespace("minimal-ns");

    var construct = new NamespaceConstruct(ctx.scope(), ctx.common(), metadata, cluster);

    assertNotNull(construct);
    assertNotNull(construct.manifest());
  }

  @Test
  void testServiceAccountConstructSystemNamespace() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var principal = Principal
      .builder()
      .type(PrincipalType.FEDERATED)
      .value("arn:aws:iam::123456789012:oidc-provider/oidc.eks.us-east-1.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "system-role",
      "System namespace role",
      principal,
      List.of("AmazonEKSWorkerNodePolicy"),
      List.of(),
      Map.of());

    var metadata = new ObjectMeta();
    metadata.setName("system-sa");
    metadata.setNamespace("kube-system");

    var serviceAccountConf = new ServiceAccountConf(metadata, role);
    var construct = new ServiceAccountConstruct(ctx.scope(), ctx.common(), serviceAccountConf, cluster);

    assertNotNull(construct);
    assertNotNull(construct.serviceAccount());
    assertNotNull(construct.roleConstruct());
  }

  @Test
  void testPodIdentityConstructDatabaseAccess() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("pods.eks.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "db-access-role",
      "Database access role",
      principal,
      List.of("AmazonRDSFullAccess", "AmazonDynamoDBFullAccess"),
      List.of(),
      Map.of());

    var metadata = new ObjectMeta();
    metadata.setName("db-service");
    metadata.setNamespace("database");

    var podIdentity = new PodIdentity(metadata, role, Map.of("Type", "database"));
    var construct = new ServiceAccountConstruct(ctx.scope(), ctx.common(), podIdentity, cluster);

    assertNotNull(construct);
    assertNotNull(construct.serviceAccount());
    assertNotNull(construct.roleConstruct());
  }

  @Test
  void testNamespaceConstructWithMultipleAnnotations() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var metadata = new ObjectMeta();
    metadata.setName("annotated-ns");
    metadata.setNamespace("annotated-ns");
    metadata.setLabels(Map.of("env", "test", "team", "platform"));
    metadata
      .setAnnotations(
        Map
          .of(
            "owner",
            "platform-team",
            "contact",
            "platform@example.com",
            "cost-center",
            "engineering"));

    var construct = new NamespaceConstruct(ctx.scope(), ctx.common(), metadata, cluster);

    assertNotNull(construct);
    assertNotNull(construct.manifest());
  }

  @Test
  void testServiceAccountConstructMessagingAccess() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var principal = Principal
      .builder()
      .type(PrincipalType.FEDERATED)
      .value("arn:aws:iam::123456789012:oidc-provider/oidc.eks.us-east-1.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "messaging-role",
      "Messaging services role",
      principal,
      List.of("AmazonSQSFullAccess", "AmazonSNSFullAccess", "AmazonEventBridgeFullAccess"),
      List.of(),
      Map.of());

    var metadata = new ObjectMeta();
    metadata.setName("messaging-sa");
    metadata.setNamespace("messaging");

    var serviceAccountConf = new ServiceAccountConf(metadata, role);
    var construct = new ServiceAccountConstruct(ctx.scope(), ctx.common(), serviceAccountConf, cluster);

    assertNotNull(construct);
    assertNotNull(construct.serviceAccount());
    assertNotNull(construct.roleConstruct());
  }

  @Test
  void testPodIdentityConstructStorageAccess() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("pods.eks.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "storage-role",
      "Storage access role",
      principal,
      List.of("AmazonS3FullAccess", "AmazonEFSFullAccess"),
      List.of(),
      Map.of());

    var metadata = new ObjectMeta();
    metadata.setName("storage-service");
    metadata.setNamespace("storage");
    metadata.setLabels(Map.of("service", "storage"));

    var podIdentity = new PodIdentity(metadata, role, Map.of("Access", "storage"));
    var construct = new ServiceAccountConstruct(ctx.scope(), ctx.common(), podIdentity, cluster);

    assertNotNull(construct);
    assertNotNull(construct.serviceAccount());
    assertNotNull(construct.roleConstruct());
  }

  @Test
  void testNamespaceConstructQAEnvironment() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var metadata = new ObjectMeta();
    metadata.setName("qa");
    metadata.setNamespace("qa");
    metadata.setLabels(Map.of("environment", "qa", "purpose", "testing"));
    metadata.setAnnotations(Map.of("description", "QA testing environment"));

    var construct = new NamespaceConstruct(ctx.scope(), ctx.common(), metadata, cluster);

    assertNotNull(construct);
    assertNotNull(construct.manifest());
  }

  @Test
  void testServiceAccountConstructAnalyticsAccess() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var principal = Principal
      .builder()
      .type(PrincipalType.FEDERATED)
      .value("arn:aws:iam::123456789012:oidc-provider/oidc.eks.us-east-1.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "analytics-role",
      "Analytics services role",
      principal,
      List.of("AthenaFullAccess", "AmazonKinesisFullAccess", "AWSGlueServiceRole"),
      List.of(),
      Map.of());

    var metadata = new ObjectMeta();
    metadata.setName("analytics-sa");
    metadata.setNamespace("analytics");
    metadata.setLabels(Map.of("team", "data"));

    var serviceAccountConf = new ServiceAccountConf(metadata, role);
    var construct = new ServiceAccountConstruct(ctx.scope(), ctx.common(), serviceAccountConf, cluster);

    assertNotNull(construct);
    assertNotNull(construct.serviceAccount());
    assertNotNull(construct.roleConstruct());
  }

  @Test
  void testPodIdentityConstructNetworkingAccess() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("pods.eks.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "networking-role",
      "Networking role",
      principal,
      List.of("AmazonVPCFullAccess", "ElasticLoadBalancingFullAccess"),
      List.of(),
      Map.of());

    var metadata = new ObjectMeta();
    metadata.setName("network-controller");
    metadata.setNamespace("networking");

    var podIdentity = new PodIdentity(metadata, role, Map.of("Component", "networking"));
    var construct = new ServiceAccountConstruct(ctx.scope(), ctx.common(), podIdentity, cluster);

    assertNotNull(construct);
    assertNotNull(construct.serviceAccount());
    assertNotNull(construct.roleConstruct());
  }

  @Test
  void testNamespaceConstructIntegrationEnvironment() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var metadata = new ObjectMeta();
    metadata.setName("integration");
    metadata.setNamespace("integration");
    metadata.setLabels(Map.of("environment", "integration"));

    var construct = new NamespaceConstruct(ctx.scope(), ctx.common(), metadata, cluster);

    assertNotNull(construct);
    assertNotNull(construct.manifest());
  }

  @Test
  void testServiceAccountConstructCICDAccess() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var principal = Principal
      .builder()
      .type(PrincipalType.FEDERATED)
      .value("arn:aws:iam::123456789012:oidc-provider/oidc.eks.us-east-1.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "cicd-role",
      "CI/CD pipeline role",
      principal,
      List.of("AWSCodeBuildAdminAccess", "AmazonEC2ContainerRegistryPowerUser"),
      List.of(),
      Map.of());

    var metadata = new ObjectMeta();
    metadata.setName("cicd-sa");
    metadata.setNamespace("cicd");

    var serviceAccountConf = new ServiceAccountConf(metadata, role);
    var construct = new ServiceAccountConstruct(ctx.scope(), ctx.common(), serviceAccountConf, cluster);

    assertNotNull(construct);
    assertNotNull(construct.serviceAccount());
    assertNotNull(construct.roleConstruct());
  }

  @Test
  void testPodIdentityConstructBatchProcessing() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("pods.eks.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "batch-role",
      "Batch processing role",
      principal,
      List.of("AWSBatchServiceRole", "AmazonS3FullAccess"),
      List.of(),
      Map.of());

    var metadata = new ObjectMeta();
    metadata.setName("batch-processor");
    metadata.setNamespace("batch");
    metadata.setLabels(Map.of("workload", "batch"));

    var podIdentity = new PodIdentity(metadata, role, Map.of("Type", "batch"));
    var construct = new ServiceAccountConstruct(ctx.scope(), ctx.common(), podIdentity, cluster);

    assertNotNull(construct);
    assertNotNull(construct.serviceAccount());
    assertNotNull(construct.roleConstruct());
  }

  @Test
  void testNamespaceConstructInfrastructure() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var metadata = new ObjectMeta();
    metadata.setName("infrastructure");
    metadata.setNamespace("infrastructure");
    metadata.setLabels(Map.of("type", "infrastructure", "critical", "true"));
    metadata.setAnnotations(Map.of("managed-by", "platform-team"));

    var construct = new NamespaceConstruct(ctx.scope(), ctx.common(), metadata, cluster);

    assertNotNull(construct);
    assertNotNull(construct.manifest());
  }

  @Test
  void testObservabilityConstructEmpty() {
    var ctx = createTestContext();

    var construct = new fasti.sh.execute.aws.eks.ObservabilityConstruct(
      ctx.scope(),
      ctx.common(),
      "eks/observability/empty.json");

    assertNotNull(construct);
    assertTrue(construct.alarmTopics().isEmpty());
    assertTrue(construct.metricFilters().isEmpty());
    assertTrue(construct.alarms().isEmpty());
    assertTrue(construct.dashboards().isEmpty());
  }

  @Test
  void testObservabilityConstructComplete() {
    var ctx = createTestContext();

    var construct = new fasti.sh.execute.aws.eks.ObservabilityConstruct(
      ctx.scope(),
      ctx.common(),
      "eks/observability/complete.json");

    assertNotNull(construct);
    assertEquals(2, construct.alarmTopics().size());
    assertEquals(1, construct.metricFilters().size());
    assertEquals(1, construct.alarms().size());
    assertEquals(1, construct.dashboards().size());
  }

  @Test
  void testManagedAddonsConstructMinimal() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var kubernetesConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "test-cluster",
      "1.28",
      "public_and_private",
      false,
      null,
      "default",
      List.of("api", "audit"),
      List.of("Public", "Private"),
      null,
      "eks/addons/minimal.json",
      null,
      null,
      Map.of(),
      Map.of(),
      Map.of());

    var construct = new fasti.sh.execute.aws.eks.ManagedAddonsConstruct(
      ctx.scope(),
      ctx.common(),
      kubernetesConf,
      cluster);

    assertNotNull(construct);
    assertNotNull(construct.vpcCniConstruct());
    assertNotNull(construct.kubeProxyConstruct());
    assertNotNull(construct.coreDnsConstruct());
    assertNotNull(construct.podIdentityAgentConstruct());
    assertNotNull(construct.awsEbsCsiConstruct());
    assertNotNull(construct.containerInsightsConstruct());
  }

  @Test
  void testNodeGroupsConstruct() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var nodeGroupConfs = List
      .of(
        new fasti.sh.model.aws.eks.NodeGroup(
          software.amazon.awscdk.services.eks.NodegroupAmiType.AL2_X86_64,
          false,
          "on_demand",
          2,
          software.amazon.awscdk.services.ec2.InstanceClass.T3,
          software.amazon.awscdk.services.ec2.InstanceSize.MEDIUM,
          3,
          1,
          "test-nodegroup",
          new fasti.sh.model.aws.iam.IamRole(
            "test-ng-role",
            "Node group role",
            fasti.sh.model.aws.iam.Principal
              .builder()
              .type(fasti.sh.model.aws.iam.PrincipalType.SERVICE)
              .value("ec2.amazonaws.com")
              .conditions(Map.of())
              .build(),
            List.of("arn:aws:iam::aws:policy/AmazonEKSWorkerNodePolicy"),
            List.of(),
            Map.of()),
          Map.of("role", "worker"),
          Map.of()));

    var construct = new fasti.sh.execute.aws.eks.NodeGroupsConstruct(
      ctx.scope(),
      "test",
      ctx.common(),
      nodeGroupConfs,
      cluster);

    assertNotNull(construct);
    assertNotNull(construct.nodeGroups());
    assertEquals(1, construct.nodeGroups().size());
  }

  @Test
  void testManagedAddonsConstructWithServiceAccounts() {
    var ctx = createTestContext();
    var vpcConstruct = createTestVpc(ctx);
    var cluster = createTestCluster(ctx, vpcConstruct);

    var kubernetesConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "test-cluster",
      "1.28",
      "public_and_private",
      false,
      null,
      "default",
      List.of("api", "audit"),
      List.of("Public", "Private"),
      null,
      "eks/addons/with-service-accounts.json",
      null,
      null,
      Map.of(),
      Map.of(),
      Map.of());

    var construct = new fasti.sh.execute.aws.eks.ManagedAddonsConstruct(
      ctx.scope(),
      ctx.common(),
      kubernetesConf,
      cluster);

    assertNotNull(construct);
    assertNotNull(construct.awsEbsCsiConstruct());
    assertNotNull(construct.vpcCniConstruct());
    assertNotNull(construct.kubeProxyConstruct());
    assertNotNull(construct.coreDnsConstruct());
    assertNotNull(construct.podIdentityAgentConstruct());
    assertNotNull(construct.containerInsightsConstruct());
  }
}
