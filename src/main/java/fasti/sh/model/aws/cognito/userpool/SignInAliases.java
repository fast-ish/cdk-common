package fasti.sh.model.aws.cognito.userpool;

/**
 * Configuration record for Cognito User Pool sign-in alias settings.
 *
 * <p>
 * Defines which attributes can be used as sign-in aliases for user authentication in Cognito User Pools.
 *
 * @param username
 *          Whether username can be used for sign-in
 * @param email
 *          Whether email can be used for sign-in
 * @param phone
 *          Whether phone number can be used for sign-in
 * @param preferredUsername
 *          Whether preferred username can be used for sign-in
 */
public record SignInAliases(
  boolean username,
  boolean email,
  boolean phone,
  boolean preferredUsername
) {}
