package fasti.sh.model.aws.s3;

import fasti.sh.model.aws.iam.Principal;
import java.util.List;
import java.util.Map;

/**
 * Configuration record for AWS S3 bucket policy settings.
 *
 * <p>
 * Defines the configuration for S3 bucket policies including policy content, principals, and variable mappings.
 *
 * @param name
 *          Policy name identifier
 * @param principals
 *          List of principals affected by the policy
 * @param policy
 *          Policy document content
 * @param mappings
 *          Variable mappings for policy customization
 */
public record BucketPolicyConf(
  String name,
  List<Principal> principals,
  String policy,
  Map<String, Object> mappings
) {}
