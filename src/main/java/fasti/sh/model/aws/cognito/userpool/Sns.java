package fasti.sh.model.aws.cognito.userpool;

import fasti.sh.model.aws.iam.IamRole;

/**
 * Configuration record for Cognito User Pool SNS integration settings.
 *
 * <p>
 * Defines SNS configuration for Cognito User Pool notifications including enablement, external ID, and IAM role settings.
 *
 * @param enabled
 *          Whether SNS integration is enabled
 * @param externalId
 *          External ID for SNS role assumption
 * @param role
 *          IAM role for SNS access
 */
public record Sns(
  boolean enabled,
  String externalId,
  IamRole role
) {}
