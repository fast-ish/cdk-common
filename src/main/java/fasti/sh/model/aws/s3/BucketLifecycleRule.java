package fasti.sh.model.aws.s3;

/**
 * Configuration record for AWS S3 bucket lifecycle rule settings.
 *
 * <p>
 * Defines lifecycle rules for S3 bucket objects including expiration policies and rule identification.
 *
 * @param enabled
 *          Whether the lifecycle rule is active
 * @param expiration
 *          Number of days after object creation when it expires
 * @param id
 *          Unique identifier for the lifecycle rule
 */
public record BucketLifecycleRule(
  boolean enabled,
  int expiration,
  String id
) {}
