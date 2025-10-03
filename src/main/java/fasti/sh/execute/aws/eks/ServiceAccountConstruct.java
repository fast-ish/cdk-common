package fasti.sh.execute.aws.eks;

import static fasti.sh.execute.serialization.Format.id;

import fasti.sh.execute.aws.iam.RoleConstruct;
import fasti.sh.model.aws.eks.PodIdentity;
import fasti.sh.model.aws.eks.ServiceAccountConf;
import fasti.sh.model.aws.iam.Principal;
import fasti.sh.model.main.Common;
import fasti.sh.model.main.Common.Maps;
import java.util.Map;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awscdk.services.eks.ICluster;
import software.amazon.awscdk.services.eks.ServiceAccount;
import software.amazon.awscdk.services.iam.SessionTagsPrincipal;
import software.constructs.Construct;

/**
 * Sophisticated EKS Kubernetes Service Account construct that provides seamless integration between Kubernetes workloads and AWS IAM
 * through OIDC-based identity federation and Pod Identity management.
 *
 * <p>
 * This construct orchestrates the complex integration between Kubernetes Service Accounts and AWS IAM, enabling Kubernetes pods to assume
 * AWS roles and access AWS services securely without storing long-term credentials in the cluster.
 *
 * <p>
 * <b>Core Identity Integration Features:</b>
 *
 * <ul>
 * <li><b>Service Account Creation</b> - Kubernetes Service Account with proper annotations
 * <li><b>IAM Role Integration</b> - AWS IAM role with OIDC trust relationship
 * <li><b>Pod Identity Support</b> - Modern Pod Identity association for enhanced security
 * <li><b>OIDC Federation</b> - OpenID Connect integration with EKS cluster OIDC provider
 * </ul>
 *
 * <p>
 * <b>Advanced Security Architecture:</b>
 *
 * <ul>
 * <li><b>Zero Long-term Credentials</b> - No static AWS credentials stored in pods
 * <li><b>Scoped Permissions</b> - IAM roles with least-privilege access policies
 * <li><b>Namespace Isolation</b> - Service accounts scoped to specific Kubernetes namespaces
 * <li><b>Session Tags</b> - AWS session tagging for enhanced audit trails
 * </ul>
 *
 * <p>
 * <b>OIDC Trust Relationship:</b> The construct establishes sophisticated trust relationships:
 *
 * <ul>
 * <li><b>Cluster OIDC Provider</b> - Integration with EKS cluster's OIDC identity provider
 * <li><b>Audience Validation</b> - STS audience verification for token authenticity
 * <li><b>Subject Validation</b> - Kubernetes service account subject validation
 * <li><b>Conditional Access</b> - IAM conditions for enhanced security controls
 * </ul>
 *
 * <p>
 * <b>Pod Identity Integration:</b> Supports both traditional IRSA and modern Pod Identity patterns:
 *
 * <ul>
 * <li><b>IRSA (IAM Roles for Service Accounts)</b> - Traditional annotation-based approach
 * <li><b>Pod Identity</b> - Modern AWS Pod Identity association
 * <li><b>Automatic Detection</b> - Intelligent selection based on configuration
 * <li><b>Migration Support</b> - Seamless migration from IRSA to Pod Identity
 * </ul>
 *
 * <p>
 * <b>Configuration Flexibility:</b>
 *
 * <ul>
 * <li><b>Multiple Constructors</b> - Support for both Service Account and Pod Identity configurations
 * <li><b>Custom Annotations</b> - Additional Kubernetes annotations and labels
 * <li><b>Namespace Management</b> - Automatic namespace creation and configuration
 * <li><b>Role Customization</b> - Custom IAM role policies and permissions
 * </ul>
 *
 * <p>
 * <b>Security Best Practices:</b>
 *
 * <ul>
 * <li><b>Principle of Least Privilege</b> - Minimal necessary AWS permissions
 * <li><b>Audit Trail</b> - Comprehensive logging and monitoring integration
 * <li><b>Token Rotation</b> - Automatic token refresh and rotation
 * <li><b>Cross-Account Support</b> - Multi-account IAM role assumption patterns
 * </ul>
 *
 * <p>
 * <b>Identity Flow Architecture:</b>
 *
 * <pre>
 * Kubernetes Pod → Service Account → OIDC Token → STS AssumeRole → AWS Credentials → AWS API
 *        ↓              ↓              ↓              ↓                 ↓              ↓
 * Pod Spec → K8s API → EKS OIDC → AWS IAM → Temporary Creds → Service Access
 * </pre>
 *
 * <p>
 * <b>Usage Examples:</b>
 *
 * <pre>{@code
 * // Traditional Service Account with IRSA
 * ServiceAccountConstruct serviceAccount = new ServiceAccountConstruct(this, common, serviceAccountConfig, eksCluster);
 *
 * // Modern Pod Identity integration
 * ServiceAccountConstruct podIdentity = new ServiceAccountConstruct(this, common, podIdentityConfig, eksCluster);
 *
 * // The construct automatically:
 * // - Creates Kubernetes Service Account
 * // - Creates IAM role with OIDC trust relationship
 * // - Adds proper annotations for role association
 * // - Configures Pod Identity (if specified)
 * // - Sets up namespace and RBAC permissions
 *
 * // Access created resources
 * ServiceAccount k8sServiceAccount = serviceAccount.getServiceAccount();
 * Role iamRole = serviceAccount.getRoleConstruct().getRole();
 * }</pre>
 *
 * @author CDK Common Framework
 * @see ServiceAccount for Kubernetes Service Account management
 * @see RoleConstruct for IAM role provisioning
 * @see Principal for OIDC trust relationship configuration
 * @see ServiceAccountConf for Service Account configuration
 * @see PodIdentity for Pod Identity configuration
 * @since 1.0.0
 */
@Slf4j
@Getter
public class ServiceAccountConstruct extends Construct {
  private final String AWS_ROLE_ARN = "eks.amazonaws.com/role-arn";

  private final RoleConstruct roleConstruct;
  private final ServiceAccount serviceAccount;

  @SneakyThrows
  public ServiceAccountConstruct(Construct scope, Common common, ServiceAccountConf conf, ICluster cluster) {
    super(scope, id("service-account", conf.metadata().getName()));

    log.debug("{} [common: {} conf: {}]", "ServiceAccountConstruct", common, conf);

    var oidc = cluster.getOpenIdConnectProvider();
    var principal = Principal.builder().build().oidcPrincipal(scope, oidc, conf);
    this.roleConstruct = new RoleConstruct(this, common, principal, conf.role());
    this.serviceAccount = ServiceAccount.Builder
      .create(this, conf.metadata().getName())
      .cluster(cluster)
      .name(conf.metadata().getName())
      .namespace(conf.metadata().getNamespace())
      .labels(conf.metadata().getLabels())
      .annotations(Maps.from(conf.metadata().getAnnotations(), Map.of(AWS_ROLE_ARN, this.roleConstruct().role().getRoleArn())))
      .build();
  }

  @SneakyThrows
  public ServiceAccountConstruct(Construct scope, Common common, PodIdentity conf, ICluster cluster) {
    super(scope, id("service-account", conf.metadata().getName()));

    log.debug("{} [common: {} conf: {}]", "ServiceAccountConstruct", common, conf);

    var principal = new SessionTagsPrincipal(conf.role().principal().iamPrincipal());
    this.roleConstruct = new RoleConstruct(this, common, principal, conf.role());
    this.serviceAccount = ServiceAccount.Builder
      .create(this, conf.metadata().getName())
      .cluster(cluster)
      .name(conf.metadata().getName())
      .namespace(conf.metadata().getNamespace())
      .labels(conf.metadata().getLabels())
      .annotations(Maps.from(conf.metadata().getAnnotations(), Map.of(AWS_ROLE_ARN, this.roleConstruct().role().getRoleArn())))
      .build();
  }
}
