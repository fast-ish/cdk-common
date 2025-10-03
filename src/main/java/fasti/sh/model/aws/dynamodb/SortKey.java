package fasti.sh.model.aws.dynamodb;

import software.amazon.awscdk.services.dynamodb.AttributeType;

/**
 * Configuration record for DynamoDB table sort key settings.
 *
 * <p>
 * Defines the sort key configuration for DynamoDB tables including the attribute name and data type.
 *
 * @param name
 *          Sort key attribute name
 * @param type
 *          Sort key attribute data type
 */
public record SortKey(
  String name,
  AttributeType type
) {}
