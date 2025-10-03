package fasti.sh.model.aws.dynamodb;

/**
 * Configuration record for DynamoDB provisioned billing settings.
 *
 * <p>
 * Defines provisioned throughput configuration for DynamoDB tables including separate read and write capacity provisioning.
 *
 * @param read
 *          Read capacity provisioned throughput configuration
 * @param write
 *          Write capacity provisioned throughput configuration
 */
public record ProvisionedBilling(
  Provisioned read,
  Provisioned write
) {}
