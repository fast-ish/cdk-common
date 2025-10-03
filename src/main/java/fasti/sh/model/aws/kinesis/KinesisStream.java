package fasti.sh.model.aws.kinesis;

import fasti.sh.model.aws.kms.Kms;

/**
 * Configuration record for AWS Kinesis Data Streams infrastructure.
 *
 * <p>
 * Defines comprehensive Kinesis stream configuration including sharding, encryption, retention, and capacity management for real-time data
 * processing.
 *
 * @param enabled
 *          Whether the Kinesis stream is enabled
 * @param name
 *          Unique stream name identifier
 * @param shards
 *          Number of shards for stream capacity
 * @param mode
 *          Stream mode (PROVISIONED or ON_DEMAND)
 * @param encryption
 *          Encryption type (NONE or KMS)
 * @param kms
 *          KMS key configuration for stream encryption
 * @param removalPolicy
 *          CloudFormation removal policy
 * @param retentionPeriod
 *          Data retention period in hours (24-8760)
 */
public record KinesisStream(
  boolean enabled,
  String name,
  int shards,
  String mode,
  String encryption,
  Kms kms,
  String removalPolicy,
  int retentionPeriod
) {}
