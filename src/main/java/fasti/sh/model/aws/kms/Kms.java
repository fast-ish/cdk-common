package fasti.sh.model.aws.kms;

/**
 * Configuration record for AWS KMS key management settings.
 *
 * <p>
 * Defines comprehensive KMS key configuration including aliases, rotation, usage policies, and lifecycle management for encryption
 * infrastructure.
 *
 * @param alias
 *          KMS key alias for human-readable identification
 * @param description
 *          Human-readable description of the key purpose
 * @param enabled
 *          Whether the KMS key is enabled for use
 * @param enableKeyRotation
 *          Whether to enable automatic key rotation
 * @param keyUsage
 *          Key usage type (ENCRYPT_DECRYPT or SIGN_VERIFY)
 * @param keySpec
 *          Key specification (SYMMETRIC_DEFAULT, RSA_2048, etc.)
 * @param removalPolicy
 *          CloudFormation removal policy for the key
 */
public record Kms(
  String alias,
  String description,
  boolean enabled,
  boolean enableKeyRotation,
  String keyUsage,
  String keySpec,
  String removalPolicy
) {}
