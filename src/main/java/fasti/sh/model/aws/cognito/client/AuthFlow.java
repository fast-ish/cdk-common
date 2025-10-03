package fasti.sh.model.aws.cognito.client;

/**
 * Configuration record for Cognito authentication flow settings.
 *
 * <p>
 * Defines which authentication flows are enabled for a Cognito User Pool client including admin flows, custom flows, and SRP-based flows.
 *
 * @param adminUserPassword
 *          Whether admin user password authentication is enabled
 * @param custom
 *          Whether custom authentication flow is enabled
 * @param userPassword
 *          Whether user password authentication is enabled
 * @param userSrp
 *          Whether user SRP authentication is enabled
 */
public record AuthFlow(
  boolean adminUserPassword,
  boolean custom,
  boolean userPassword,
  boolean userSrp
) {}
