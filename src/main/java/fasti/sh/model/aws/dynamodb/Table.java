package fasti.sh.model.aws.dynamodb;

import java.util.List;
import java.util.Map;

/**
 * Configuration record for AWS DynamoDB table infrastructure.
 *
 * <p>
 * Defines comprehensive DynamoDB table configuration including keys, indexes, encryption, billing, streaming, and operational settings.
 *
 * @param name
 *          Unique table name identifier
 * @param partitionKey
 *          Primary partition key configuration
 * @param sortKey
 *          Optional sort key configuration for composite primary key
 * @param localSecondaryIndexes
 *          List of local secondary indexes
 * @param globalSecondaryIndexes
 *          List of global secondary indexes
 * @param encryption
 *          Table encryption configuration
 * @param billing
 *          Billing mode and capacity settings
 * @param tableClass
 *          DynamoDB table class (STANDARD or STANDARD_INFREQUENT_ACCESS)
 * @param streams
 *          DynamoDB Streams configuration for change data capture
 * @param contributorInsights
 *          Whether to enable CloudWatch Contributor Insights
 * @param deletionProtection
 *          Whether to enable deletion protection
 * @param pointInTimeRecovery
 *          Whether to enable point-in-time recovery
 * @param removalPolicy
 *          CloudFormation removal policy
 * @param tags
 *          AWS resource tags for organization and billing
 */
public record Table(
  String name,
  SortKey partitionKey,
  SortKey sortKey,
  List<Index> localSecondaryIndexes,
  List<Index> globalSecondaryIndexes,
  Encryption encryption,
  Billing billing,
  String tableClass,
  Streams streams,
  boolean contributorInsights,
  boolean deletionProtection,
  boolean pointInTimeRecovery,
  String removalPolicy,
  Map<String, String> tags
) {}
