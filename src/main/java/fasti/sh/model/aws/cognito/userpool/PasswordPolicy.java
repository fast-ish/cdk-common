package fasti.sh.model.aws.cognito.userpool;

/**
 * Configuration record for Cognito User Pool password policy settings.
 *
 * <p>
 * Defines password complexity requirements and validation rules for user passwords in Cognito User Pools.
 *
 * @param minLength
 *          Minimum password length requirement
 * @param requireLowercase
 *          Whether to require lowercase characters
 * @param requireUppercase
 *          Whether to require uppercase characters
 * @param requireDigits
 *          Whether to require numeric digits
 * @param requireSymbols
 *          Whether to require special symbols
 * @param tempPasswordValidity
 *          Temporary password validity period in days
 */
public record PasswordPolicy(
  int minLength,
  boolean requireLowercase,
  boolean requireUppercase,
  boolean requireDigits,
  boolean requireSymbols,
  int tempPasswordValidity
) {}
