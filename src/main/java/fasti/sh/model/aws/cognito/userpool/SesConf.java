package fasti.sh.model.aws.cognito.userpool;

import fasti.sh.model.aws.ses.Sender;

/**
 * Configuration record for Cognito User Pool SES integration settings.
 *
 * <p>
 * Defines SES configuration for email sending from Cognito User Pools including enablement and sender configuration.
 *
 * @param enabled
 *          Whether SES integration is enabled
 * @param sender
 *          SES sender configuration details
 */
public record SesConf(
  boolean enabled,
  Sender sender
) {}
