package fasti.sh.model.aws.cognito.userpool;

/**
 * Configuration record for Cognito User Pool device tracking settings.
 *
 * <p>
 * Defines device tracking configuration including challenge settings and user prompt behavior for device management.
 *
 * @param newDeviceChallenge
 *          Whether to challenge users on new devices
 * @param rememberOnUserPrompt
 *          Whether to remember devices based on user prompt
 */
public record DeviceTracking(
  boolean newDeviceChallenge,
  boolean rememberOnUserPrompt
) {}
