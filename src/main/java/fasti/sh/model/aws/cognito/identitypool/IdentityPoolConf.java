package fasti.sh.model.aws.cognito.identitypool;

import fasti.sh.model.aws.iam.IamRole;
import java.util.List;

/**
 * Configuration record for AWS Cognito Identity Pool settings.
 *
 * <p>
 * Defines the configuration for Cognito Identity Pools including authentication settings, role mappings, and access control.
 *
 * @param name
 *          Identity pool name identifier
 * @param authenticated
 *          IAM role for authenticated identities
 * @param allowClassicFlow
 *          Whether to allow classic authentication flow
 * @param allowUnauthenticatedIdentities
 *          Whether to allow unauthenticated identities
 * @param disableServerSideTokenCheck
 *          Whether to disable server-side token validation
 * @param userPoolRoleMappings
 *          List of user pool role mapping configurations
 */
public record IdentityPoolConf(
  String name,
  IamRole authenticated,
  boolean allowClassicFlow,
  boolean allowUnauthenticatedIdentities,
  boolean disableServerSideTokenCheck,
  List<RoleMappingConf> userPoolRoleMappings
) {}
