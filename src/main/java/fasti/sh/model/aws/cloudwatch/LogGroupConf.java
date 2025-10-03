package fasti.sh.model.aws.cloudwatch;

import fasti.sh.model.aws.kms.Kms;
import java.util.Map;
import lombok.Builder;

/**
 * Configuration record for AWS CloudWatch Log Group settings.
 *
 * <p>
 * Defines comprehensive CloudWatch log group configuration including retention, encryption, and lifecycle management for centralized
 * logging infrastructure.
 *
 * @param name
 *          Log group name identifier
 * @param type
 *          Log group type specification
 * @param retention
 *          Log retention period in days
 * @param kms
 *          KMS encryption configuration for log data
 * @param removalPolicy
 *          CloudFormation removal policy
 * @param tags
 *          AWS resource tags for organization and billing
 */
@Builder
public record LogGroupConf(
  String name,
  String type,
  String retention,
  Kms kms,
  String removalPolicy,
  Map<String, String> tags
) {}
