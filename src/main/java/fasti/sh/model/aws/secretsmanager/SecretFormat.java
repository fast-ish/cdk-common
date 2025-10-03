package fasti.sh.model.aws.secretsmanager;

/**
 * Configuration record for AWS Secrets Manager secret format settings.
 *
 * <p>
 * Defines the format and constraints for automatically generated secrets including character sets, length, and complexity requirements.
 *
 * @param excludeLowercase
 *          Whether to exclude lowercase characters
 * @param excludeNumbers
 *          Whether to exclude numeric characters
 * @param excludeUppercase
 *          Whether to exclude uppercase characters
 * @param includeSpace
 *          Whether to include space characters
 * @param length
 *          Length of the generated secret
 * @param requireEachIncludedType
 *          Whether to require at least one character from each included type
 */
public record SecretFormat(
  boolean excludeLowercase,
  boolean excludeNumbers,
  boolean excludeUppercase,
  boolean includeSpace,
  int length,
  boolean requireEachIncludedType
) {}
